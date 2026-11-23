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

package de.symeda.sormas.ui.clinicalcourse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

/**
 * Tests that {@link ClinicalVisitForm} passes the #14457 check ("No complications" Yes together with a complication Yes)
 * through to the symptoms form it embeds, so that ClinicalCourseController can block saving a clinical visit.
 */
class ClinicalVisitFormTest extends AbstractUiBeanTest {

	@Test
	void shouldReportContradictionOfTheEmbeddedSymptomsForm() {
		ClinicalVisitForm form = createForm(Disease.MUMPS);
		SymptomsForm symptomsForm = getSymptomsForm(form);
		((NullableOptionGroup) symptomsForm.getField(SymptomsDto.ORCHITIS)).setNullableValue(SymptomState.YES);
		((NullableOptionGroup) symptomsForm.getField(SymptomsDto.NO_COMPLICATIONS)).setNullableValue(YesNoUnknown.YES);

		assertTrue(form.hasContradictingComplications());
		assertEquals(symptomsForm.getContradictingComplicationsMessage(), form.getContradictingComplicationsMessage());
		assertFalse(form.getContradictingComplicationsMessage().isEmpty());
	}

	@Test
	void shouldNotReportContradictionForConsistentSymptoms() {
		ClinicalVisitForm form = createForm(Disease.MUMPS);
		SymptomsForm symptomsForm = getSymptomsForm(form);
		((NullableOptionGroup) symptomsForm.getField(SymptomsDto.ORCHITIS)).setNullableValue(SymptomState.YES);
		((NullableOptionGroup) symptomsForm.getField(SymptomsDto.NO_COMPLICATIONS)).setNullableValue(YesNoUnknown.NO);

		assertFalse(form.hasContradictingComplications());
		assertEquals("", form.getContradictingComplicationsMessage());
	}

	@Test
	void shouldNotReportContradictionForNewVisit() {
		ClinicalVisitForm form = createForm(Disease.MUMPS);

		assertFalse(form.hasContradictingComplications());
		assertEquals("", form.getContradictingComplicationsMessage());
	}

	@Test
	void shouldNotReportContradictionForDiseasesWithoutNoComplications() {
		ClinicalVisitForm form = createForm(Disease.DENGUE);
		((NullableOptionGroup) getSymptomsForm(form).getField(SymptomsDto.MENINGITIS)).setNullableValue(SymptomState.YES);

		assertFalse(form.hasContradictingComplications());
		assertEquals("", form.getContradictingComplicationsMessage());
	}

	private ClinicalVisitForm createForm(Disease disease) {
		// the form reads the user's data access level from UiUtil, which needs a current UI
		mockCurrentUiWithFullDataAccess();
		ClinicalVisitForm form = new ClinicalVisitForm(false, disease, null, null, false, true);
		form.setValue(ClinicalVisitDto.build(null, disease));
		return form;
	}

	/** The symptoms form is bound as the field of {@link ClinicalVisitDto#SYMPTOMS}. */
	private static SymptomsForm getSymptomsForm(ClinicalVisitForm form) {
		return form.getField(ClinicalVisitDto.SYMPTOMS);
	}
}
