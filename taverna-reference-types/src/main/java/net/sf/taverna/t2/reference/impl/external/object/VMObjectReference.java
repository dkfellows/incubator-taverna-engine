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
package net.sf.taverna.t2.reference.impl.external.object;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.taverna.t2.reference.AbstractExternalReference;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceContext;

/**
 * Implementation of ExternalReferenceSPI used to refer to objects in the local
 * virtual machine.
 * 
 * @author Stian Soiland-Reyes
 * @author Alex Nenadic
 */
public class VMObjectReference extends AbstractExternalReference implements
		ExternalReferenceSPI, Serializable {
	private static final long serialVersionUID = 6708284419760319684L;
	private static final Charset UTF8 = Charset.forName("UTF-8");

	/**
	 * Mapping from objects to their UUIDs.
	 */
	private static Map<Object, UUID> objectToUUID = new HashMap<>();
	/**
	 * Mapping from UUIDs to objects.
	 */
	private static Map<UUID, Object> uuidToObject = new HashMap<>();

	/**
	 * Unique reference to the object.
	 */
	private String uuid;

	@Override
	public InputStream openStream(ReferenceContext context) {
		return new ByteArrayInputStream(getObject().toString().getBytes(UTF8));
	}

	/**
	 * Getter used by hibernate to retrieve the object uuid property.
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Setter used by hibernate to set the object uuid property.
	 */
	public void setUuid(String id) {
		if (uuid != null)
			throw new IllegalStateException("Can't set UUID of an object twice");
		this.uuid = id;
	}

	public void setObject(Object object) {
		if (uuid != null)
			throw new IllegalStateException("Can't set UUID an object twice");
		UUID knownUUID = objectToUUID.get(object);
		if (knownUUID == null) {
			// register object
			knownUUID = UUID.randomUUID();
			objectToUUID.put(object, knownUUID);
			uuidToObject.put(knownUUID, object);
		}
		setUuid(knownUUID.toString());
	}

	public Object getObject() {
		return uuidToObject.get(UUID.fromString(uuid));
	}

	@Override
	public Long getApproximateSizeInBytes() {
		// We do not know the object size
		return new Long(-1);
	}

	@Override
	public VMObjectReference clone() {
		VMObjectReference result = new VMObjectReference();
		result.setUuid(this.getUuid());
		return result;
	}
}
