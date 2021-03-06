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
package net.sf.taverna.t2.workflowmodel.processor.dispatch.layers;

import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationBean;
import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationProperty;

/**
 * Bean to hold the configuration for the parallelize layer, specifically a
 * single int property defining the number of concurrent jobs in that processor
 * instance per owning process ID.
 * 
 * @author Tom Oinn
 */
@ConfigurationBean(uri = Parallelize.URI + "#Config")
public class ParallelizeConfig {
	private int maxJobs;

	public ParallelizeConfig() {
		super();
		this.maxJobs = 1;
	}

	@ConfigurationProperty(name = "maxJobs", label = "Maximum Parallel Jobs", description = "The maximum number of jobs that can run in parallel", required = false)
	public void setMaximumJobs(int maxJobs) {
		this.maxJobs = maxJobs;
	}

	public int getMaximumJobs() {
		return this.maxJobs;
	}
}
