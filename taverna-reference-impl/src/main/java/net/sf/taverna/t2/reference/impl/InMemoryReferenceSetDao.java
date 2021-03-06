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
package net.sf.taverna.t2.reference.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.taverna.t2.reference.DaoException;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferenceSetDao;
import net.sf.taverna.t2.reference.T2Reference;

/**
 * A trivial in-memory implementation of ReferenceSetDao for either testing or
 * very lightweight embedded systems. Uses a java Map as the backing store.
 * 
 * @author Tom Oinn
 */
public class InMemoryReferenceSetDao implements ReferenceSetDao {
	private Map<T2Reference, ReferenceSet> store;

	public InMemoryReferenceSetDao() {
		this.store = new ConcurrentHashMap<>();
	}

	@Override
	public synchronized ReferenceSet get(T2Reference reference)
			throws DaoException {
		return store.get(reference);
	}

	@Override
	public synchronized void store(ReferenceSet refSet) throws DaoException {
		store.put(refSet.getId(), refSet);
	}

	@Override
	public synchronized void update(ReferenceSet refSet) throws DaoException {
		store.put(refSet.getId(), refSet);
	}

	@Override
	public synchronized boolean delete(ReferenceSet refSet) throws DaoException {
		return store.remove(refSet.getId()) != null;
	}

	@Override
	public synchronized void deleteReferenceSetsForWFRun(String workflowRunId)
			throws DaoException {
		for (T2Reference reference : store.keySet())
			if (reference.getNamespacePart().equals(workflowRunId))
				store.remove(reference);
	}
}
