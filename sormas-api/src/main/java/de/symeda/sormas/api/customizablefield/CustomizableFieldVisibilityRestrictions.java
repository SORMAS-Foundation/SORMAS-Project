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
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.Disease;

/**
 * Typed representation of the {@code visibilityRestrictions} JSON column on
 * {@link CustomizableFieldMetadataDto}.
 * <p>
 * Controls when a customizable field is visible to the user:
 * <ul>
 * <li><b>diseases</b> – when set, the field is only visible for cases/entities
 * associated with one of the listed {@link Disease} values. When {@code null}
 * or empty, the field is visible regardless of disease.</li>
 * </ul>
 * Additional visibility criteria can be added here without touching the
 * database schema (the whole object is stored as a single {@code jsonb} column).
 */
public class CustomizableFieldVisibilityRestrictions implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The diseases for which this field should be visible.
	 * When {@code null} or empty, the field is visible for all diseases.
	 */
	private List<Disease> diseases;

	public CustomizableFieldVisibilityRestrictions() {
		// Required for JSON deserialization.
	}

	public List<Disease> getDiseases() {
		return diseases;
	}

	public void setDiseases(List<Disease> diseases) {
		this.diseases = diseases;
	}

	/**
	 * Returns {@code true} when the given runtime context satisfies all restrictions defined
	 * on this object. A restriction criterion is considered satisfied when either:
	 * <ul>
	 * <li>the restriction list for that criterion is {@code null} or empty (= no restriction), or</li>
	 * <li>the context's value for that criterion is contained in the restriction list.</li>
	 * </ul>
	 *
	 * @param context
	 *            the runtime context to evaluate; {@code null} is treated as an empty context
	 *            (all criteria with non-empty restriction lists will fail)
	 * @return {@code true} if the field should be visible given this context
	 */
	public boolean matches(CustomizableFieldVisibilityContext context) {
		return CollectionUtils.isEmpty(diseases) || (context != null && context.getDisease().filter(diseases::contains).isPresent());
	}
}
