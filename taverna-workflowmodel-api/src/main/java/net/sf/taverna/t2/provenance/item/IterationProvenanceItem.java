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
package net.sf.taverna.t2.provenance.item;

import java.sql.Timestamp;

import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;

/**
 * One of these is created for each iteration inside an enacted activity.
 * Contains both the input and output data and port names contained inside
 * {@link DataProvenanceItem}s. The actual iteration number is contained inside
 * an int array eg [1]
 * 
 * @author Ian Dunlop
 * @author Paolo Missier
 * @author Stuart Owen
 */
public class IterationProvenanceItem extends AbstractProvenanceItem {
	private Timestamp enactmentEnded;
	private Timestamp enactmentStarted;
	private ErrorProvenanceItem errorItem;
	private final SharedVocabulary eventType = SharedVocabulary.ITERATION_EVENT_TYPE;
	private InputDataProvenanceItem inputDataItem;
	private int[] iteration;
	private OutputDataProvenanceItem outputDataItem;
	private IterationProvenanceItem parentIterationItem = null;

	public IterationProvenanceItem getParentIterationItem() {
		return parentIterationItem;
	}

	public Timestamp getEnactmentEnded() {
		return enactmentEnded;
	}

	public Timestamp getEnactmentStarted() {
		return enactmentStarted;
	}

	public ErrorProvenanceItem getErrorItem() {
		return errorItem;
	}

	@Override
	public SharedVocabulary getEventType() {
		return eventType;
	}

	public InputDataProvenanceItem getInputDataItem() {
		return inputDataItem;
	}

	public int[] getIteration() {
		return iteration;
	}

	public OutputDataProvenanceItem getOutputDataItem() {
		return outputDataItem;
	}

	public void setEnactmentEnded(Timestamp enactmentEnded) {
		this.enactmentEnded = enactmentEnded;
	}

	public void setEnactmentStarted(Timestamp enactmentStarted) {
		this.enactmentStarted = enactmentStarted;
	}

	public void setErrorItem(ErrorProvenanceItem errorItem) {
		this.errorItem = errorItem;
	}

	public void setInputDataItem(InputDataProvenanceItem inputDataItem) {
		this.inputDataItem = inputDataItem;
	}

	public void setIteration(int[] iteration) {
		this.iteration = iteration;
	}

	public void setOutputDataItem(OutputDataProvenanceItem outputDataItem) {
		this.outputDataItem = outputDataItem;
	}

	public void setParentIterationItem(
			IterationProvenanceItem parentIterationItem) {
		this.parentIterationItem = parentIterationItem;
	}
}
