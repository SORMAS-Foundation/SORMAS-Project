/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.caze;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;

public class CaseClassificationValidatorTest extends AbstractBeanTest {

	public static final String INVALID_CASE_CLASSIFICATION = "invalid case classification";

	@Test
	public void testCaseClassificationValidator() {

		final TestDataCreator creator = new TestDataCreator();
		final TestDataCreator.RDCF rdcf = creator.createRDCF("region", "district", "community", "facility");
		final UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		final PersonDto cazePerson = creator.createPerson("Case", "Person");
		final CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.CORONAVIRUS,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		final SymptomsDto symptoms = SymptomsDto.build();
		caze.setSymptoms(symptoms);

		final CaseClassificationValidator validator = new CaseClassificationValidator(caze.getUuid(), INVALID_CASE_CLASSIFICATION);

		// assert classifications when no symptoms & no lab result
		valid(CaseClassification.NOT_CLASSIFIED, validator);
		invalid(CaseClassification.SUSPECT, validator);
		valid(CaseClassification.PROBABLE, validator);
		invalid(CaseClassification.CONFIRMED, validator);
		invalid(CaseClassification.CONFIRMED_NO_SYMPTOMS, validator);
		invalid(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS, validator);
		valid(CaseClassification.NO_CASE, validator);

		// assert classifications when no symptoms & non-positive lab result
		final SampleDto sample =
			creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility.toReference());

		valid(CaseClassification.NOT_CLASSIFIED, validator);
		invalid(CaseClassification.SUSPECT, validator);
		valid(CaseClassification.PROBABLE, validator);
		invalid(CaseClassification.CONFIRMED, validator);
		invalid(CaseClassification.CONFIRMED_NO_SYMPTOMS, validator);
		invalid(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS, validator);
		valid(CaseClassification.NO_CASE, validator);

		// assert classifications when no symptoms & positive lab result
		sample.setPathogenTestResult(PathogenTestResultType.POSITIVE);
		getSampleFacade().saveSample(sample);

		valid(CaseClassification.NOT_CLASSIFIED, validator);
		invalid(CaseClassification.SUSPECT, validator);
		valid(CaseClassification.PROBABLE, validator);
		invalid(CaseClassification.CONFIRMED, validator);
		invalid(CaseClassification.CONFIRMED_NO_SYMPTOMS, validator);
		valid(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS, validator);
		valid(CaseClassification.NO_CASE, validator);

		// assert classifications when no symptoms & positive lab result
		sample.setPathogenTestResult(PathogenTestResultType.POSITIVE);
		getSampleFacade().saveSample(sample);

		valid(CaseClassification.NOT_CLASSIFIED, validator);
		invalid(CaseClassification.SUSPECT, validator);
		valid(CaseClassification.PROBABLE, validator);
		invalid(CaseClassification.CONFIRMED, validator);
		invalid(CaseClassification.CONFIRMED_NO_SYMPTOMS, validator);
		valid(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS, validator);
		valid(CaseClassification.NO_CASE, validator);

		// assert classifications when other symptoms & positive lab result
		caze.getSymptoms().setFever(SymptomState.YES);
		CaseDataDto savedCase1 = getCaseFacade().saveCase(caze);

		valid(CaseClassification.NOT_CLASSIFIED, validator);
		invalid(CaseClassification.SUSPECT, validator);
		valid(CaseClassification.PROBABLE, validator);
		invalid(CaseClassification.CONFIRMED, validator);
		valid(CaseClassification.CONFIRMED_NO_SYMPTOMS, validator);
		invalid(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS, validator);
		valid(CaseClassification.NO_CASE, validator);

		// assert classifications when other & covid symptoms & positive lab result
		savedCase1.getSymptoms().setPneumoniaClinicalOrRadiologic(SymptomState.YES);
		CaseDataDto savedCase2 = getCaseFacade().saveCase(savedCase1);

		valid(CaseClassification.NOT_CLASSIFIED, validator);
		valid(CaseClassification.SUSPECT, validator);
		valid(CaseClassification.PROBABLE, validator);
		valid(CaseClassification.CONFIRMED, validator);
		valid(CaseClassification.CONFIRMED_NO_SYMPTOMS, validator);
		invalid(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS, validator);
		valid(CaseClassification.NO_CASE, validator);

		// assert classifications when other & covid symptoms & negative lab result
		sample.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
		getSampleFacade().saveSample(sample);

		valid(CaseClassification.NOT_CLASSIFIED, validator);
		valid(CaseClassification.SUSPECT, validator);
		valid(CaseClassification.PROBABLE, validator);
		invalid(CaseClassification.CONFIRMED, validator);
		invalid(CaseClassification.CONFIRMED_NO_SYMPTOMS, validator);
		invalid(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS, validator);
		valid(CaseClassification.NO_CASE, validator);
	}

	private void invalid(CaseClassification caseClassification, CaseClassificationValidator validator) {
		Assert.assertFalse(validator.isValidValue(caseClassification));
	}

	private void valid(CaseClassification caseClassification, CaseClassificationValidator validator) {
		Assert.assertTrue(validator.isValidValue(caseClassification));
	}
}
