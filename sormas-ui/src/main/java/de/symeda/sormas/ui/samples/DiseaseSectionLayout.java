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

import java.util.Collection;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.AbstractField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;

/**
 * Encapsulates the disease-specific portion of PathogenTestForm.
 * Each implementation adds/removes its own fields to/from the host form's field group
 * and injects its layout template into the "diseaseSectionLoc" CustomLayout slot.
 *
 * <p>
 * Lifecycle:
 * <ol>
 * <li>{@link #getHtmlLayout()} is called once to build the section's CustomLayout template.</li>
 * <li>{@link #bindFields(FieldGroup, CustomLayout, Disease)} is called to add fields.</li>
 * <li>{@link #unbindFields(FieldGroup, CustomLayout)} is called before a new section replaces this one.</li>
 * </ol>
 */
public interface DiseaseSectionLayout {

	/**
	 * Returns the HTML template (using fluidRowLocs/loc helpers) for this section's fields.
	 * An empty string means no disease-specific fields.
	 */
	String getHtmlLayout();

	/** Returns the property IDs of all fields bound by {@link #bindFields}. */
	Collection<String> getFieldIds();

	/**
	 * Adds this section's fields to the given field group and panel.
	 * Called once after the section's CustomLayout has been installed.
	 */
	void bindFields(FieldGroup fieldGroup, CustomLayout panel, Disease disease);

	/**
	 * Removes this section's fields from the field group and panel,
	 * clearing their values so they don't bleed into the saved DTO.
	 * Called before a new section replaces this one.
	 */
	void unbindFields(FieldGroup fieldGroup, CustomLayout panel);

	/**
	 * Called when the test type changes so sections can update field visibility or
	 * auto-set the test result. No-op by default.
	 */
	default void onTestTypeChanged(PathogenTestType testType, Disease disease, AbstractField<PathogenTestResultType> testResultField) {
	}

	/** Returns the disease(s) this section handles. */
	Disease[] getDiseases();

	/** Factory: returns the correct section implementation for the given disease. */
	static DiseaseSectionLayout forDisease(Disease disease) {
		if (disease == null) {
			return new DefaultDiseaseSectionLayout();
		}
		switch (disease) {
		case TUBERCULOSIS:
		case LATENT_TUBERCULOSIS:
			return new TuberculosisDiseaseSectionLayout();
		case MEASLES:
			return new MeaslesDiseaseSectionLayout();
		case CRYPTOSPORIDIOSIS:
			return new CryptosporidiosisDiseaseSectionLayout();
		case INVASIVE_MENINGOCOCCAL_INFECTION:
			return new ImiDiseaseSectionLayout();
		case INVASIVE_PNEUMOCOCCAL_INFECTION:
			return new IpiDiseaseSectionLayout();
		case CSM:
			return new CsmDiseaseSectionLayout();
		case CORONAVIRUS:
			return new CoronavirusDiseaseSectionLayout();
		default:
			return new DefaultDiseaseSectionLayout();
		}
	}
}
