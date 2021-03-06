/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester
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
package uk.org.taverna.platform.execution.api;

import java.util.UUID;

import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.platform.report.ActivityReport;
import uk.org.taverna.platform.report.ProcessorReport;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Abstract implementation of an {@link Execution}.
 *
 * @author David Withers
 */
public abstract class AbstractExecution implements Execution {

	private final String ID;
	private final WorkflowBundle workflowBundle;
	private final Bundle dataBundle;
	private final Workflow workflow;
	private final Profile profile;
	private final WorkflowReport workflowReport;

	private final Scufl2Tools scufl2Tools = new Scufl2Tools();

	/**
	 * Constructs an abstract implementation of an Execution.
	 *
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the <code>Workflow</code>s required for
	 *            execution
	 * @param workflow
	 *            the <code>Workflow</code> to execute
	 * @param profile
	 *            the <code>Profile</code> to use when executing the <code>Workflow</code>
	 * @param dataBundle
	 *            the <code>Bundle</code> containing the data values for the <code>Workflow</code>
	 * @throws InvalidWorkflowException
	 *             if the specified workflow is invalid
	 */
	public AbstractExecution(WorkflowBundle workflowBundle, Workflow workflow, Profile profile,
			Bundle dataBundle) {
		this.workflowBundle = workflowBundle;
		this.workflow = workflow;
		this.profile = profile;
		this.dataBundle = dataBundle;
		ID = UUID.randomUUID().toString();
		workflowReport = generateWorkflowReport(workflow);
	}

	protected abstract WorkflowReport createWorkflowReport(Workflow workflow);

	protected abstract ProcessorReport createProcessorReport(Processor processor);

	protected abstract ActivityReport createActivityReport(Activity activity);

	public WorkflowReport generateWorkflowReport(Workflow workflow) {
		WorkflowReport workflowReport = createWorkflowReport(workflow);
		for (Processor processor : workflow.getProcessors()) {
			ProcessorReport processorReport = createProcessorReport(processor);
			processorReport.setParentReport(workflowReport);
			workflowReport.addProcessorReport(processorReport);
			for (ProcessorBinding processorBinding : scufl2Tools.processorBindingsForProcessor(
					processor, profile)) {
				Activity boundActivity = processorBinding.getBoundActivity();
				ActivityReport activityReport = createActivityReport(boundActivity);
				activityReport.setParentReport(processorReport);
				if (scufl2Tools.containsNestedWorkflow(processor, profile)) {
					Workflow nestedWorkflow = scufl2Tools.nestedWorkflowForProcessor(processor,
							profile);
					WorkflowReport nestedWorkflowReport = generateWorkflowReport(nestedWorkflow);
					nestedWorkflowReport.setParentReport(activityReport);
					activityReport.setNestedWorkflowReport(nestedWorkflowReport);
				}
				processorReport.addActivityReport(activityReport);
			}
		}
		return workflowReport;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public WorkflowBundle getWorkflowBundle() {
		return workflowBundle;
	}

	@Override
	public Bundle getDataBundle() {
		return dataBundle;
	}

	@Override
	public Workflow getWorkflow() {
		return workflow;
	}

	@Override
	public Profile getProfile() {
		return profile;
	}

	@Override
	public WorkflowReport getWorkflowReport() {
		return workflowReport;
	}

}