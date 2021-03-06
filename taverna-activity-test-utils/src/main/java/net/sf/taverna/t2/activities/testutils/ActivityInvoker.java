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
package net.sf.taverna.t2.activities.testutils;

import static java.util.ServiceLoader.load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import net.sf.taverna.t2.reference.ExternalReferenceBuilderSPI;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ExternalReferenceTranslatorSPI;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.StreamToValueConverterSPI;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.ValueToReferenceConverterSPI;
import net.sf.taverna.t2.reference.impl.ErrorDocumentServiceImpl;
import net.sf.taverna.t2.reference.impl.InMemoryErrorDocumentDao;
import net.sf.taverna.t2.reference.impl.InMemoryListDao;
import net.sf.taverna.t2.reference.impl.InMemoryReferenceSetDao;
import net.sf.taverna.t2.reference.impl.ListServiceImpl;
import net.sf.taverna.t2.reference.impl.ReferenceServiceImpl;
import net.sf.taverna.t2.reference.impl.ReferenceSetAugmentorImpl;
import net.sf.taverna.t2.reference.impl.ReferenceSetServiceImpl;
import net.sf.taverna.t2.reference.impl.SimpleT2ReferenceGenerator;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivity;

/**
 * Helper class to facilitate in executing Activities in isolation.
 * 
 * @author Stuart Owen
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * @author David Withers
 */
public class ActivityInvoker {
	/**
	 * Timeout in seconds
	 */
	public static long TIMEOUT = 30;

	/**
	 * Invokes an {@link AsynchronousActivity} with a given set of input Objects
	 * and returns a Map<String,Object> of requested output values.
	 * 
	 * @param activity
	 *            the activity to be tested
	 * @param inputs
	 *            a Map<String,Object> of input Objects
	 * @param requestedOutputs
	 *            a List<String> of outputs to be examined
	 * 
	 * @return a Map<String,Object> of the outputs requested by requestedOutput
	 *         or <code>null</code> if a failure occurs
	 * @throws InterruptedException
	 * @throws Throwable
	 */
/*	public static Map<String, Object> invokeAsyncActivity(
			AbstractAsynchronousActivity<?> activity,
			Map<String, Object> inputs, Map<String, Class<?>> requestedOutputs)
			throws Exception {
		Map<String, Object> results = new HashMap<>();

		ApplicationContext context = new RavenAwareClassPathXmlApplicationContext("inMemoryActivityTestsContext.xml");
		ReferenceService referenceService = (ReferenceService) context.getBean("t2reference.service.referenceService");

		DummyCallback callback = new DummyCallback(referenceService);
		Map<String, T2Reference> inputEntities = new HashMap<>();
		for (String inputName : inputs.keySet()) {
			Object val = inputs.get(inputName);
			if (val instanceof List)
				inputEntities.put(inputName, referenceService.register(val, 1, true, callback.getContext()));
			else
				inputEntities.put(inputName, referenceService.register(val, 0, true, callback.getContext()));
		}

		activity.executeAsynch(inputEntities, callback);
		callback.thread.join();

		if (callback.failed)
			return null;
		for (Map.Entry<String, Class<?>> output : requestedOutputs.entrySet()) {
			T2Reference id = callback.data.get(output.getKey());
			if (id != null) {
				Object result = referenceService.renderIdentifier(id, output.getValue(), callback.getContext());
				results.put(output.getKey(), result);
			}
		}
		return results;
	}
	*/

	/*
	 * Changed this method to render the T2Reference to an object only if the
	 * type of the object in requestedOutputs is not an instance of
	 * ExternalReferenceSPI. Otherwise, the calling test method should get
	 * activity ReferenceSet and render the object itself. This was needed for
	 * API consumer activity testing - see ApiConsumerActivityTest.
	 * 
	 * Also added support for multi-dimensional lists.
	 */
	public static Map<String, Object> invokeAsyncActivity(
			AbstractAsynchronousActivity<?> activity,
			Map<String, Object> inputs, Map<String, Class<?>> requestedOutputs)
			throws InterruptedException {
		Map<String, Object> results = new HashMap<>();
		ReferenceService referenceService = createReferenceService();
		DummyCallback callback = new DummyCallback(referenceService);
		Map<String, T2Reference> inputEntities = new HashMap<>();
		for (Entry<String, Object> input : inputs.entrySet())
			inputEntities.put(
					input.getKey(),
					referenceService.register(input.getValue(),
							getDepth(input.getValue()), true,
							callback.getContext()));

		activity.executeAsynch(inputEntities, callback);
		callback.thread.join(TIMEOUT * 1000);

		if (callback.failed)
			throw callback.failures.get(0);

		for (Map.Entry<String, Class<?>> output : requestedOutputs.entrySet()) {
			T2Reference id = callback.data.get(output.getKey());
			Object result;
			if (ExternalReferenceSPI.class.isAssignableFrom(output.getValue()))
				// Do not render the object - just resolve the T2Reference
				result = referenceService.resolveIdentifier(id, null,
						callback.getContext());
			else
				// Try to render the object behind the reference
				result = referenceService.renderIdentifier(id,
						output.getValue(), callback.getContext());
			results.put(output.getKey(), result);
		}
		return results;
	}

	private static ReferenceService createReferenceService() {
		SimpleT2ReferenceGenerator referenceGenerator = new SimpleT2ReferenceGenerator();
		ReferenceSetAugmentorImpl referenceSetAugmentor = new ReferenceSetAugmentorImpl();
		referenceSetAugmentor.setBuilders(getBuilders());
		referenceSetAugmentor.setTranslators(getTranslators());
		
		ReferenceSetServiceImpl referenceSetService = new ReferenceSetServiceImpl();
		referenceSetService.setT2ReferenceGenerator(referenceGenerator);
		referenceSetService.setReferenceSetDao(new InMemoryReferenceSetDao());
		referenceSetService.setReferenceSetAugmentor(referenceSetAugmentor);
		
		ListServiceImpl listService = new ListServiceImpl();
		listService.setT2ReferenceGenerator(referenceGenerator);
		listService.setListDao(new InMemoryListDao());
		
		ErrorDocumentServiceImpl errorDocumentService = new ErrorDocumentServiceImpl();
		errorDocumentService.setT2ReferenceGenerator(referenceGenerator);
		errorDocumentService.setErrorDao(new InMemoryErrorDocumentDao());
		
		ReferenceServiceImpl referenceService = new ReferenceServiceImpl();
		referenceService.setReferenceSetService(referenceSetService);
		referenceService.setListService(listService);
		referenceService.setErrorDocumentService(errorDocumentService);
		referenceService.setConverters(getConverters());
		referenceService.setValueBuilders(getValueBuilders());
		
		return referenceService;
	}
	
	private static <T> List<T> getImplementations(Class<T> api) {
		List<T> implementations = new ArrayList<T>();
		for (T implementation : load(api))
			implementations.add(implementation);
		return implementations;
	}

	@SuppressWarnings("rawtypes")
	private static List<StreamToValueConverterSPI> getValueBuilders() {
		return getImplementations(StreamToValueConverterSPI.class);
	}

	private static List<ValueToReferenceConverterSPI> getConverters() {
		return getImplementations(ValueToReferenceConverterSPI.class);
	}

	private static List<ExternalReferenceTranslatorSPI<?, ?>> getTranslators() {
		List<ExternalReferenceTranslatorSPI<?, ?>> implementations = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		ServiceLoader<ExternalReferenceTranslatorSPI> serviceLoader = load(ExternalReferenceTranslatorSPI.class);
		for (ExternalReferenceTranslatorSPI<?, ?> implementation : serviceLoader)
			implementations.add(implementation);
		return implementations;
	}

	private static List<ExternalReferenceBuilderSPI<?>> getBuilders() {
		List<ExternalReferenceBuilderSPI<?>> implementations = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		ServiceLoader<ExternalReferenceBuilderSPI> serviceLoader = load(ExternalReferenceBuilderSPI.class);
		for (ExternalReferenceBuilderSPI<?> implementation : serviceLoader)
			implementations.add(implementation);
		return implementations;
	}

	/**
	 * If an object is activity list - returns its depth, 0 otherwise (for
	 * single objects).
	 * 
	 * @param obj
	 * @return
	 */
	private static int getDepth(Object obj) {
		if (!(obj instanceof List))
			return 0;

		/*
		 * Assumes all sub-lists are of the same depth, so just uses the first
		 * sub-list to calculate it.
		 */
		return getDepth(((List<?>) obj).get(0)) + 1;
	}
}
