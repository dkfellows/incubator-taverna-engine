/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.workflowmodel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.invocation.Completion;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.IterationInternalEvent;
import net.sf.taverna.t2.invocation.TreeCache;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;

/**
 * Receives Job and Completion events and emits Jobs unaltered. Completion
 * events additionally cause registration of lists for each key in the datamap
 * of the jobs at immediate child locations in the index structure. These list
 * identifiers are sent in place of the Completion events.
 * <p>
 * State for a given process ID is purged when a final completion event is
 * received so there is no need for an explicit cache purge operation in the
 * public API (although for termination of partially complete workflows it may
 * be sensible for subclasses to provide one)
 * <p>
 * 
 * @author Tom Oinn
 * @author David Withers
 */
public abstract class AbstractCrystalizer implements Crystalizer {
	private Map<String, CompletionAwareTreeCache> cacheMap = new HashMap<>();

	public abstract Job getEmptyJob(String owningProcess, int[] index,
			InvocationContext context);

	/**
	 * Receive a Job or Completion, Jobs are emitted unaltered and cached,
	 * Completion events trigger registration of a corresponding list - this may
	 * be recursive in nature if the completion event's index implies nested
	 * lists which have not been registered.
	 * <p>
	 * If the baseListDepth property is defined then completion events on nodes
	 * which don't already exist create empty jobs instead and emit those, if
	 * undefined the completion event is emited intact.
	 * 
	 * @param e The event (a {@link Job} or a {@link Completion})
	 */
	@Override
	public void receiveEvent(IterationInternalEvent<?> e) {
		String owningProcess = e.getOwningProcess();
		CompletionAwareTreeCache cache = null;
		synchronized (cacheMap) {
			if (!cacheMap.containsKey(owningProcess)) {
				cache = new CompletionAwareTreeCache(owningProcess, e
						.getContext());
				cacheMap.put(owningProcess, cache);
			} else
				cache = cacheMap.get(owningProcess);
		}
		if (e instanceof Job) {
			// Pass through Job after storing it in the cache
			Job j = (Job) e;
			synchronized (cache) {
				cache.insertJob(new Job("", j.getIndex(), j.getData(), j
						.getContext()));
				jobCreated(j);
				if (j.getIndex().length == 0)
					cacheMap.remove(j.getOwningProcess());
			}
		} else if (e instanceof Completion) {
			Completion c = (Completion) e;
			synchronized (cache) {
				cache.resolveAt(owningProcess, c.getIndex());
				if (c.getIndex().length == 0)
					cacheMap.remove(c.getOwningProcess());
			}
		}
	}

	protected class CompletionAwareTreeCache extends TreeCache {
		private String owningProcess;
		private InvocationContext context;

		public CompletionAwareTreeCache(String owningProcess,
				InvocationContext context) {
			super();
			this.context = context;
			this.owningProcess = owningProcess;
		}

		public void resolveAt(String owningProcess, int[] completionIndex) {
			NamedNode n = nodeAt(completionIndex);
			if (n != null) {
				assignNamesTo(n, completionIndex);
				return;
			}

			/*
			 * We know what the list depth should be, so we can construct
			 * appropriate depth empty lists to fill in the gaps.
			 */

			Job j = getEmptyJob(owningProcess, completionIndex, context);
			insertJob(j);
			jobCreated(j);
		}

		private void assignNamesTo(NamedNode n, int[] index) {
			/* Only act if contents of this node undefined */
			if (n.contents != null)
				return;

			Map<String, List<T2Reference>> listItems = new HashMap<>();
			int pos = 0;
			for (NamedNode child : n.children) {
				/*
				 * If child doesn't have a defined name map yet then define it.
				 */
				Job j;
				if (child == null) {
					/*
					 * happens if we're completing a partially empty collection
					 * structure
					 */
					int[] newIndex = new int[index.length + 1];
					for (int i = 0; i < index.length; i++)
						newIndex[i] = index[i];
					newIndex[index.length] = pos++;
					j = getEmptyJob(owningProcess, newIndex, context);
					jobCreated(j);
				} else if (child.contents == null) {
					int[] newIndex = new int[index.length + 1];
					for (int i = 0; i < index.length; i++)
						newIndex[i] = index[i];
					newIndex[index.length] = pos++;
					assignNamesTo(child, newIndex);
					j = child.contents;
				} else {
					pos++;
					j = child.contents;
				}

				/*
				 * Now pull the names out of the child job map and push them
				 * into lists to be registered
				 */

				for (String outputName : j.getData().keySet()) {
					List<T2Reference> items = listItems.get(outputName);
					if (items == null) {
						items = new ArrayList<>();
						listItems.put(outputName, items);
					}
					items.add(j.getData().get(outputName));
				}
			}
			Map<String, T2Reference> newDataMap = new HashMap<>();
			for (String outputName : listItems.keySet())
				newDataMap.put(
						outputName,
						context.getReferenceService()
								.getListService()
								.registerList(listItems.get(outputName),
										context).getId());
			Job newJob = new Job(owningProcess, index, newDataMap, context);
			n.contents = newJob;

			/* Get rid of the children as we've now named this node */

			n.children.clear();
			jobCreated(n.contents);
		}
	}
}
