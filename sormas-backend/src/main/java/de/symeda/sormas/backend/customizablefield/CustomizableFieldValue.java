/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.customizablefield;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.backend.common.DeletableAdo;

/**
 * Entity class for customizable field values.
 * Stores the actual value for a custom field for a specific entity instance.
 */
@Entity(name = "customizablefieldvalue")
public class CustomizableFieldValue extends DeletableAdo {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "customizablefieldvalue";

	public static final String CUSTOMIZABLE_FIELD_METADATA = "customizableFieldMetadata";
	public static final String ENTITY_UUID = "entityUuid";
	public static final String CONTEXT_CLASS = "contextClass";
	public static final String VALUE = "value";

	private CustomizableFieldMetadata customizableFieldMetadata;
	private String entityUuid;
	private CustomizableFieldContext contextClass;
	private String value;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customizablefieldmetadata_id", nullable = false)
	public CustomizableFieldMetadata getCustomizableFieldMetadata() {
		return customizableFieldMetadata;
	}

	public void setCustomizableFieldMetadata(CustomizableFieldMetadata customizableFieldMetadata) {
		this.customizableFieldMetadata = customizableFieldMetadata;
	}

	@Column(length = 36, nullable = false)
	public String getEntityUuid() {
		return entityUuid;
	}

	public void setEntityUuid(String entityUuid) {
		this.entityUuid = entityUuid;
	}

	@Column(length = 256, nullable = false)
	@Convert(converter = CustomizableFieldContextConverter.class)
	public CustomizableFieldContext getContextClass() {
		return contextClass;
	}

	public void setContextClass(CustomizableFieldContext contextClass) {
		this.contextClass = contextClass;
	}

	@Column(columnDefinition = "text")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
