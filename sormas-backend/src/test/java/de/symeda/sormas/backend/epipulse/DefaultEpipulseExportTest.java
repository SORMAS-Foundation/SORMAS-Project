/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.epipulse;

import static de.symeda.sormas.api.epipulse.EpipulseVariable.DISEASE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.GENDER;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.HOSPITALISATION;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.NATIONAL_RECORD_ID;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.PATHOGEN_DETECTION_METHOD;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.REPORTING_COUNTRY;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SEROGROUP;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.SUBJECT_CODE;
import static de.symeda.sormas.api.epipulse.EpipulseVariable.VACCINATION_STATUS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.epipulse.disease.EpipulseFieldModels;

/**
 * Tests export behavior for subject codes handled by {@code DefaultFieldModel}.
 */
public class DefaultEpipulseExportTest extends AbstractEpipulseExportTest {

	/** Subject code used for main shared-model assertions. */
	private static final EpipulseSubjectCode SUBJECT_CODE_UNDER_TEST = EpipulseSubjectCode.MUMP;

	/** Subject code used to assert hospitalisation-block omission. */
	private static final EpipulseSubjectCode SUBJECT_CODE_OMITTING_HOSPITALISATION = EpipulseSubjectCode.POLI;

	/** Subject code used to assert vaccination-block omission. */
	private static final EpipulseSubjectCode SUBJECT_CODE_OMITTING_VACCINATION = EpipulseSubjectCode.CHIK;

	private TestDataCreator.RDCF rdcf;
	private UserDto surveillanceSupervisor;
	private Date periodStart;
	private Date periodEnd;
	private Date reportDate;

	@BeforeEach
	public void setUpDefaultExport() {

		configureLuxembourgFor(SUBJECT_CODE_UNDER_TEST, SUBJECT_CODE_OMITTING_HOSPITALISATION, SUBJECT_CODE_OMITTING_VACCINATION);

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		surveillanceSupervisor = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_SUPERVISOR);

		reportDate = DateHelper.subtractDays(new Date(), 10);
		periodStart = DateHelper.subtractDays(reportDate, 5);
		periodEnd = DateHelper.addDays(reportDate, 5);
	}

	@Test
	@DisplayName("the subject code under test is still served by the shared model")
	public void theSubjectCodeUnderTestIsStillServedByTheSharedModel() {

		// If this fails, the disease has gained a field model of its own and this suite is no
		// longer testing what it says it tests. Move it to another default code - see the note on
		// SUBJECT_CODE_UNDER_TEST for what makes one a good choice - rather than deleting this.
		for (EpipulseSubjectCode subjectCode : new EpipulseSubjectCode[] {
			SUBJECT_CODE_UNDER_TEST,
			SUBJECT_CODE_OMITTING_HOSPITALISATION,
			SUBJECT_CODE_OMITTING_VACCINATION }) {

			assertThat(subjectCode + " now has a dedicated field model", EpipulseFieldModels.hasDedicatedModel(subjectCode), is(false));
		}
	}

	@Test
	@DisplayName("a case is exported with the shared columns filled from the case and the configuration")
	public void aCaseIsExportedWithTheSharedColumns() {

		createCase("Anna", "Bauer");

		ExportedEntries export = runExport(SUBJECT_CODE_UNDER_TEST, periodStart, periodEnd);

		var entry = export.only();
		assertThat(export.value(entry, SUBJECT_CODE), is(SUBJECT_CODE_UNDER_TEST.name()));
		assertThat(export.value(entry, DISEASE), is(SUBJECT_CODE_UNDER_TEST.name()));
		assertThat(export.value(entry, REPORTING_COUNTRY), is(SERVER_COUNTRY_NUTS_CODE));
		assertThat(export.value(entry, GENDER), is("F"));
		assertThat(export.value(entry, NATIONAL_RECORD_ID), is(not(blankOrNullString())));
	}

	@Test
	@DisplayName("a subject code gets the opt-in blocks it declares and no others")
	public void aSubjectCodeGetsTheOptInBlocksItDeclares() {

		// None of these exports needs a case: which columns a model emits is settled when the
		// model is built, so all three run against an empty database and cost three statements.
		ExportedEntries declaresBoth = runExport(SUBJECT_CODE_UNDER_TEST, periodStart, periodEnd);
		ExportedEntries omitsHospitalisation = runExport(SUBJECT_CODE_OMITTING_HOSPITALISATION, periodStart, periodEnd);
		ExportedEntries omitsVaccination = runExport(SUBJECT_CODE_OMITTING_VACCINATION, periodStart, periodEnd);

		// Both directions matter, and one code cannot show them. Present is what proves the branch
		// in DefaultFieldModel.create was taken; absent is what proves the filter behind it works,
		// which is the whole reason a definition declared by eight of seventeen codes can sit in
		// the shared catalogue safely.
		assertThat(declaresBoth.emits(HOSPITALISATION), is(true));
		assertThat(declaresBoth.emits(VACCINATION_STATUS), is(true));

		// Each of these two omits one block and keeps the other, which is a sharper assertion than
		// a code with neither: it shows the filter acting per variable rather than per model.
		assertThat(omitsHospitalisation.emits(HOSPITALISATION), is(false));
		assertThat(omitsHospitalisation.emits(VACCINATION_STATUS), is(true));
		assertThat(omitsVaccination.emits(VACCINATION_STATUS), is(false));
		assertThat(omitsVaccination.emits(HOSPITALISATION), is(true));

		// Nothing disease-specific reaches a shared model, whatever the code declares. Serogroup is
		// only ever added by MENI. PathogenDetectionMethod is the sharper case: several codes served
		// here declare it, yet it has no generic form at all, because the codes it reports are keyed
		// by subject code. So unlike the two above it is absent by design rather than by filter -
		// see the note in DefaultFieldModel.
		assertThat(declaresBoth.emits(SEROGROUP), is(false));
		assertThat(declaresBoth.emits(PATHOGEN_DETECTION_METHOD), is(false));
	}

	private CaseDataDto createCase(String firstName, String lastName) {

		return creator.createCase(
			surveillanceSupervisor.toReference(),
			creator.createPerson(firstName, lastName, Sex.FEMALE, 1990, 6, 15).toReference(),
			SUBJECT_CODE_UNDER_TEST.getDisease(),
			CaseClassification.CONFIRMED,
			InvestigationStatus.DONE,
			reportDate,
			rdcf);
	}
}
