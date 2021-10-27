/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.auditlog.api.sample;

import java.util.List;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedAttribute;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.HasUuid;

@Audited
public class EntityWithIgnoredMethods implements HasUuid {

	public static final String SOME_ATTRIBUTE = "someAttribute";
	public static final String IGNORED_ATTRIBUTE = "ignoredAttribute";
	public static final String TRANSIENT_ATTRIBUTE = "transientAttribute";
	public static final String TWO_ANNOTATIONS_ATTRIBUTE = "twoAnnotationsAttribute";
	public static final String ONE_TO_ONE_ATTRIBUTE = "oneToOneAttribute";
	public static final String ONE_TO_MANY_ATTRIBUTE = "oneToManyAttribute";
	public static final String ONE_TO_ONE_ATTRIBUTE_UNMAPPED = "oneToOneAttributeUnmapped";

	private final String uuid;
	private final String someAttribute;
	private final String ignoredAttribute;
	private final String transientAttribute;
	private final String twoAnnotationsAttribute;
	private final EntityWithHelperAttributes oneToOneAttribute;
	private final List<EntityWithHelperAttributes> oneToManyAttribute;
	private final EntityWithHelperAttributes oneToOneAttributeUnmapped;

	public EntityWithIgnoredMethods(
		String uuid,
		String someAttribute,
		String ignoredAttribute,
		String transientAttribute,
		String twoAnnotationsAttribute,
		EntityWithHelperAttributes oneToOneAttribute,
		List<EntityWithHelperAttributes> oneToManyAttribute,
		EntityWithHelperAttributes oneToOneAttributeUnmapped) {
		this.uuid = uuid;
		this.someAttribute = someAttribute;
		this.ignoredAttribute = ignoredAttribute;
		this.transientAttribute = transientAttribute;
		this.twoAnnotationsAttribute = twoAnnotationsAttribute;
		this.oneToOneAttribute = oneToOneAttribute;
		this.oneToManyAttribute = oneToManyAttribute;
		this.oneToOneAttributeUnmapped = oneToOneAttributeUnmapped;
	}

	@Override
	@AuditedIgnore
	public String getUuid() {
		return uuid;
	}

	public String getSomeAttribute() {
		return someAttribute;
	}

	@AuditedIgnore
	public String getIgnoredAttribute() {
		return ignoredAttribute;
	}

	@Transient
	public String getTransientAttribute() {
		return transientAttribute;
	}

	@AuditedIgnore
	@AuditedAttribute
	public String getTwoAnnotationsAttribute() {
		return twoAnnotationsAttribute;
	}

	@OneToOne(mappedBy = "oneToOneAttribute")
	public EntityWithHelperAttributes getOneToOneAttribute() {
		return oneToOneAttribute;
	}

	@OneToMany(mappedBy = "oneToManyAttribute")
	public List<EntityWithHelperAttributes> getOneToManyAttribute() {
		return oneToManyAttribute;
	}

	@OneToOne
	public EntityWithHelperAttributes getOneToOneAttributeUnmapped() {
		return oneToOneAttributeUnmapped;
	}
}
