package uk.org.taverna.platform;

import org.eclipse.osgi.framework.internal.core.Constants;
import org.osgi.framework.Bundle;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.util.OsgiStringUtils;

public class PlatformTest extends AbstractConfigurableBundleCreatorTests {
	
//	protected String[] getTestBundlesNames() {
//	return new String[] {
//			"net.sf.taverna.t2.core, reference-api, 2.0-SNAPSHOT",
//			"org.jdom, com.springsource.org.jdom, 1.0.0",
//			"net.sf.taverna.t2.lang, observer, 2.0-SNAPSHOT",
//			"net.sf.taverna.t2.core, workflowmodel-api, 2.0-SNAPSHOT",
//			"uk.org.taverna.platform, monitor, 0.0.1-SNAPSHOT",
//			"uk.org.taverna.platform, execution, 0.0.1-SNAPSHOT" };
//}

	protected String[] getTestBundlesNames() {
	return new String[] {
			"net.sf.taverna.t2.core, reference-api, 2.0-SNAPSHOT",
			"org.jdom, com.springsource.org.jdom, 1.0.0",
			"net.sf.taverna.t2.lang, observer, 2.0-SNAPSHOT",
			"net.sf.taverna.t2.core, workflowmodel-api, 2.0-SNAPSHOT",
			"org.xmlpull, com.springsource.org.xmlpull, 1.1.3.4-O",
			"javax.xml.stream, com.springsource.javax.xml.stream, 1.0.1",
			"com.thoughtworks.xstream, com.springsource.com.thoughtworks.xstream, 1.2.2",
			"net.sf.taverna.t2.core, workflowmodel-impl, 2.0-SNAPSHOT",
			"org.apache.commons, com.springsource.org.apache.commons.codec, 1.3.0",
			"org.apache.commons, com.springsource.org.apache.commons.httpclient, 3.1.0",
			"net.sf.taverna.t2.core, reference-core-extensions, 2.0-SNAPSHOT",
			"net.sf.taverna.t2.core, reference-impl, 2.0-SNAPSHOT",
			"uk.org.taverna.scufl2, scufl2-api, 0.1-SNAPSHOT",
			"uk.org.taverna.scufl2, scufl2-t2flow, 0.1-SNAPSHOT",
			"uk.org.taverna.platform, monitor, 0.0.1-SNAPSHOT",
			"uk.org.taverna.platform, execution, 0.0.1-SNAPSHOT",
			"net.sf.taverna.t2.activities, dependency-activity, 2.0-SNAPSHOT",
			"javax.servlet, com.springsource.javax.servlet, 2.5.0",
			"org.beanshell, com.springsource.bsh, 2.0.0.b4",
			"net.sf.taverna.t2.activities, beanshell-activity, 2.0-SNAPSHOT",
			"uk.org.taverna.platform, execution-local, 0.0.1-SNAPSHOT"
			};
	}

	public void testOsgiPlatformStarts() throws Exception {
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
	}

	public void testOsgiEnvironment() throws Exception {
		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			System.out.println(OsgiStringUtils.nullSafeName(bundles[i]));
		}
		System.out.println();
	}
	
}
