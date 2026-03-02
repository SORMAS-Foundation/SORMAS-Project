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
package de.symeda.sormas.ui.samples;

import de.symeda.sormas.api.Disease;

/**
 * Encapsulates the disease-specific portion of PathogenTestForm.
 * Each implementation adds/removes its own fields to/from the host form's field group
 * and injects its layout template into the "diseaseSectionLoc" CustomLayout slot.
 *
 * <p>
 * Lifecycle:
 * <ol>
 * <li>{@link #getHtmlLayout()} is called once to build the section's CustomLayout template.</li>
 * <li>{@link #bindFields(PathogenTestForm)} is called to add fields to the form.</li>
 * <li>{@link #unbindFields(PathogenTestForm)} is called before a new section replaces this one.</li>
 * </ol>
 */
public interface DiseaseSectionLayout {

	/**
	 * Returns the HTML template (using fluidRowLocs/loc helpers) for this section's fields.
	 * An empty string means no disease-specific fields.
	 */
	String getHtmlLayout();

	/**
	 * Adds this section's fields to the host form's layout and field group.
	 * Called once after the section's CustomLayout has been installed.
	 */
	void bindFields(PathogenTestForm form);

	/**
	 * Removes this section's fields from the host form's layout and field group,
	 * clearing their values so they don't bleed into the saved DTO.
	 * Called before a new section replaces this one.
	 */
	void unbindFields(PathogenTestForm form);

	/** Returns the disease(s) this section handles, for logging/debugging. */
	Disease[] getDiseases();

	/** Factory: returns the correct section implementation for the given disease. */
	static DiseaseSectionLayout forDisease(Disease disease) {
		if (disease == Disease.TUBERCULOSIS || disease == Disease.LATENT_TUBERCULOSIS) {
			return new TuberculosisDiseaseSectionLayout();
		}
		return new DefaultDiseaseSectionLayout();
	}
}
