/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
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
package uk.org.taverna.platform.run.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.databundle.DataBundles;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.impl.local.LocalExecutionEnvironment;
import uk.org.taverna.platform.execution.impl.local.LocalExecutionService;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 *
 *
 * @author David Withers
 */
@Ignore
public class RunProfileTest {

	private RunProfile runProfile;
	private ExecutionEnvironment executionEnvironment;
	private WorkflowBundle workflowBundle;
	private LocalExecutionService executionService;
	private Workflow workflow, mainWorkflow;
	private Profile profile, mainProfile;
	private Bundle dataBundle;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		workflow = new Workflow();
		mainWorkflow = new Workflow();
		profile = new Profile();
		mainProfile = new Profile();
		workflowBundle = new WorkflowBundle();
		workflowBundle.setMainProfile(mainProfile);
		workflowBundle.setMainWorkflow(mainWorkflow);
		executionService = new LocalExecutionService();
		executionEnvironment = new LocalExecutionEnvironment(executionService, null, null);

		dataBundle = DataBundles.createBundle();
		runProfile = new RunProfile(executionEnvironment, workflowBundle, workflow.getName(), profile.getName(), dataBundle);
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#RunProfile(uk.org.taverna.scufl2.api.container.WorkflowBundle, java.util.Map, net.sf.taverna.t2.reference.ReferenceService, uk.org.taverna.platform.execution.api.ExecutionService)}
	 * .
	 */
	@Test
	public void testRunProfileWorkflowBundleMapOfStringT2ReferenceReferenceServiceExecutionService() {
		runProfile = new RunProfile(executionEnvironment, workflowBundle, dataBundle);
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#RunProfile(uk.org.taverna.scufl2.api.container.WorkflowBundle, uk.org.taverna.scufl2.api.core.Workflow, uk.org.taverna.scufl2.api.profiles.Profile, java.util.Map, net.sf.taverna.t2.reference.ReferenceService, uk.org.taverna.platform.execution.api.ExecutionService)}
	 * .
	 */
	@Test
	public void testRunProfileWorkflowBundleWorkflowProfileMapOfStringT2ReferenceReferenceServiceExecutionService() {
		runProfile = new RunProfile(executionEnvironment, workflowBundle, workflow.getName(), profile.getName(), dataBundle);
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getWorkflowBundle()}.
	 */
	@Test
	public void testGetWorkflowBundle() {
		assertNotNull(runProfile.getWorkflowBundle());
		assertEquals(workflowBundle, runProfile.getWorkflowBundle());
		assertEquals(runProfile.getWorkflowBundle(), runProfile.getWorkflowBundle());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setWorkflowBundle(uk.org.taverna.scufl2.api.container.WorkflowBundle)}
	 * .
	 */
	@Test
	public void testSetWorkflowBundle() {
		runProfile.setWorkflowBundle(null);
		assertNull(runProfile.getWorkflowBundle());
		runProfile.setWorkflowBundle(workflowBundle);
		assertEquals(workflowBundle, runProfile.getWorkflowBundle());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getWorkflow()}.
	 */
	@Test
	public void testGetWorkflow() {
		assertNotNull(runProfile.getWorkflowName());
		assertEquals(workflow.getName(), runProfile.getWorkflowName());
		assertEquals(runProfile.getWorkflowName(), runProfile.getWorkflowName());
		runProfile.setWorkflowName(null);
		assertNotNull(runProfile.getWorkflowName());
		assertEquals(mainWorkflow.getName(), runProfile.getWorkflowName());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setWorkflow(uk.org.taverna.scufl2.api.core.Workflow)}
	 * .
	 */
	@Test
	public void testSetWorkflow() {
		runProfile.setWorkflowName(null);
		assertNotNull(runProfile.getWorkflowName());
		assertEquals(mainWorkflow.getName(), runProfile.getWorkflowName());
		runProfile.setWorkflowBundle(new WorkflowBundle());
		runProfile.setWorkflowName(null);
		assertNull(runProfile.getWorkflowName());
		runProfile.setWorkflowName(workflow.getName());
		assertEquals(workflow.getName(), runProfile.getWorkflowName());
		runProfile.setWorkflowName(mainWorkflow.getName());
		assertEquals(mainWorkflow.getName(), runProfile.getWorkflowName());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getProfile()}.
	 */
	@Test
	public void testGetProfile() {
		assertNotNull(runProfile.getProfileName());
		assertEquals(profile.getName(), runProfile.getProfileName());
		assertEquals(runProfile.getProfileName(), runProfile.getProfileName());
		runProfile.setProfileName(null);
		assertNotNull(runProfile.getProfileName());
		assertEquals(mainProfile.getName(), runProfile.getProfileName());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setProfile(uk.org.taverna.scufl2.api.profiles.Profile)}
	 * .
	 */
	@Test
	public void testSetProfile() {
		runProfile.setProfileName(null);
		assertNotNull(runProfile.getProfileName());
		assertEquals(mainProfile.getName(), runProfile.getProfileName());
		runProfile.setWorkflowBundle(new WorkflowBundle());
		runProfile.setProfileName(null);
		assertNull(runProfile.getProfileName());
		runProfile.setProfileName(profile.getName());
		assertEquals(profile.getName(), runProfile.getProfileName());
		runProfile.setProfileName(mainProfile.getName());
		assertEquals(mainProfile.getName(), runProfile.getProfileName());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getDataBundle()}.
	 */
	@Test
	public void testGetDataBundle() {
		assertNotNull(runProfile.getDataBundle());
		assertEquals(dataBundle, runProfile.getDataBundle());
		assertEquals(runProfile.getDataBundle(), runProfile.getDataBundle());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setDataBundle(org.purl.wf4ever.robundle.Bundle)}
	 * .
	 */
	@Test
	public void testSetDataBundle() {
		runProfile.setDataBundle(null);
		assertNull(runProfile.getDataBundle());
		runProfile.setDataBundle(dataBundle);
		assertEquals(dataBundle, runProfile.getDataBundle());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getExecutionEnvironment()}.
	 */
	@Test
	public void testGetExecutionEnvironment() {
		assertNotNull(runProfile.getExecutionEnvironment());
		assertEquals(executionEnvironment, runProfile.getExecutionEnvironment());
		assertEquals(runProfile.getExecutionEnvironment(), runProfile.getExecutionEnvironment());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setExecutionEnvironment(uk.org.taverna.platform.execution.api.ExecutionEnvironment)}
	 * .
	 */
	@Test
	public void testSetExecutionEnvironment() {
		runProfile.setExecutionEnvironment(null);
		assertNull(runProfile.getExecutionEnvironment());
		runProfile.setExecutionEnvironment(executionEnvironment);
		assertEquals(executionEnvironment, runProfile.getExecutionEnvironment());
	}

}
