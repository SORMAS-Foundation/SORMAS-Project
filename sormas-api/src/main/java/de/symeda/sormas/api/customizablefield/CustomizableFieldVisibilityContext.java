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

package de.symeda.sormas.api.customizablefield;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import de.symeda.sormas.api.Disease;

/**
 * Runtime context against which {@link CustomizableFieldVisibilityRestrictions} are evaluated.
 * <p>
 * Where {@link CustomizableFieldVisibilityRestrictions} represents the stored <em>what should
 * restrict this field</em> configuration, this class carries the <em>current runtime values</em>
 * (e.g. the disease of the case being viewed) that are tested against those restrictions.
 * <p>
 * New criteria can be added as additional fields without changing the evaluation contract;
 * {@link CustomizableFieldVisibilityRestrictions#matches(CustomizableFieldVisibilityContext)}
 * handles all criteria in one place.
 * <p>
 * Builder-style fluent setters allow convenient one-liner construction:
 * 
 * <pre>
 * 
 * CustomizableFieldVisibilityContext ctx = new CustomizableFieldVisibilityContext().withDisease(caze.getDisease());
 * </pre>
 */
public class CustomizableFieldVisibilityContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private Disease disease;

	public CustomizableFieldVisibilityContext() {
		// no-arg constructor required by deserialization frameworks
	}

	public Optional<Disease> getDisease() {
		return Optional.ofNullable(disease);
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public CustomizableFieldVisibilityContext withDisease(Disease disease) {
		this.disease = disease;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CustomizableFieldVisibilityContext that = (CustomizableFieldVisibilityContext) o;
		return Objects.equals(disease, that.disease);
	}

	@Override
	public int hashCode() {
		return Objects.hash(disease);
	}

}
