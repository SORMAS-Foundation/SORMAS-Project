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

package de.symeda.sormas.api.epipulse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Tests for {@code Hospitalisation}: "History of hospitalisation due to the disease or related
 * complications. Hospitalisation defined as at least one overnight stay."
 *
 * <p>
 * A stay counts when it was admitted, admitted <em>for this disease</em>, and lasted long enough.
 * The case reports true if either its current stay or any previous one passes all three. The
 * boundary is one night, not two: the rule is
 * {@code getDaysBetween(admission, discharge) > 1} against a count that includes both endpoints.
 * The first two tests pin that boundary from either side.
 *
 * <p>
 * The SORMAS-connection notes for this variable say {@code dischargeDate - admissionDate > 1 day},
 * which would be two nights. EpiPulse's own definition is one night, and that is what these tests
 * assert.
 */
public class EpipulseHospitalisationTest {

	private static final Date DAY_1 = DateHelper.getDateZero(2024, 3, 1);
	private static final Date DAY_2 = DateHelper.getDateZero(2024, 3, 2);
	private static final Date DAY_5 = DateHelper.getDateZero(2024, 3, 5);

	@Test
	@DisplayName("a stay spanning a single night counts")
	public void oneOvernightCounts() {
		assertEquals("true", hospitalisation(current(YesNoUnknown.YES, HospitalizationReasonType.REPORTED_DISEASE, DAY_1, DAY_2)));
	}

	@Test
	@DisplayName("admitted and discharged the same day does not count")
	public void sameDayDoesNotCount() {
		assertEquals("false", hospitalisation(current(YesNoUnknown.YES, HospitalizationReasonType.REPORTED_DISEASE, DAY_1, DAY_1)));
	}

	@Test
	@DisplayName("a patient still in hospital counts, and does not throw")
	public void openStayCounts() {
		// The regression this class exists for: getDaysBetween hands a null discharge date to
		// ChronoUnit.DAYS.between, which throws NPE --- failing the whole export, not just this row.
		// An ongoing stay cannot be the same-day discharge the overnight test excludes, because a
		// day case has a discharge date.
		assertEquals("true", hospitalisation(current(YesNoUnknown.YES, HospitalizationReasonType.REPORTED_DISEASE, DAY_1, null)));
	}

	@Test
	@DisplayName("a recorded admission with no admission date counts, and does not throw")
	public void missingAdmissionDateCounts() {
		assertEquals("true", hospitalisation(current(YesNoUnknown.YES, HospitalizationReasonType.REPORTED_DISEASE, null, DAY_5)));
	}

	@Test
	@DisplayName("a long stay for some other reason does not count")
	public void otherReasonDoesNotCount() {
		assertEquals("false", hospitalisation(current(YesNoUnknown.YES, HospitalizationReasonType.OTHER, DAY_1, DAY_5)));
	}

	@Test
	@DisplayName("a case never admitted does not count, whatever the dates say")
	public void notAdmittedDoesNotCount() {

		assertEquals("false", hospitalisation(current(YesNoUnknown.NO, HospitalizationReasonType.REPORTED_DISEASE, DAY_1, DAY_5)));
		assertEquals("false", hospitalisation(current(YesNoUnknown.UNKNOWN, HospitalizationReasonType.REPORTED_DISEASE, DAY_1, DAY_5)));
		assertEquals("false", hospitalisation(current(null, HospitalizationReasonType.REPORTED_DISEASE, DAY_1, DAY_5)));
	}

	@Test
	@DisplayName("a case with no hospitalisation at all reports false rather than throwing")
	public void noHospitalizationReportsFalse() {
		assertEquals("false", hospitalisation(new EpipulseDiseaseExportEntryDto()));
	}

	@Test
	@DisplayName("a qualifying previous stay counts even when the current one does not")
	public void previousStayCounts() {

		EpipulseDiseaseExportEntryDto entry = current(YesNoUnknown.NO, null, null, null);
		entry.setPreviousHospitalizations(
			Arrays.asList(
				previous(YesNoUnknown.YES, HospitalizationReasonType.OTHER, DAY_1, DAY_5),
				previous(YesNoUnknown.YES, HospitalizationReasonType.REPORTED_DISEASE, DAY_1, DAY_2)));

		assertEquals("true", hospitalisation(entry));
	}

	@Test
	@DisplayName("previous stays that all fail the test leave the case false")
	public void previousStaysThatFailLeaveItFalse() {

		EpipulseDiseaseExportEntryDto entry = current(YesNoUnknown.NO, null, null, null);
		entry.setPreviousHospitalizations(
			Arrays.asList(
				previous(YesNoUnknown.YES, HospitalizationReasonType.OTHER, DAY_1, DAY_5),
				previous(YesNoUnknown.YES, HospitalizationReasonType.REPORTED_DISEASE, DAY_1, DAY_1)));

		assertEquals("false", hospitalisation(entry));
	}

	private static String hospitalisation(EpipulseDiseaseExportEntryDto entry) {
		return EpipulseMapping.hospitalisation(entry);
	}

	private static EpipulseDiseaseExportEntryDto current(YesNoUnknown admitted, HospitalizationReasonType reason, Date admission, Date discharge) {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setAdmittedToHealthFacility(admitted);
		entry.setHospitalizationReason(reason);
		entry.setAdmissionDate(admission);
		entry.setDischargeDate(discharge);

		return entry;
	}

	private static EpipulseHospitalizationCheckDto previous(YesNoUnknown admitted, HospitalizationReasonType reason, Date admission, Date discharge) {

		EpipulseHospitalizationCheckDto check = new EpipulseHospitalizationCheckDto();
		check.setAdmittedToHealthFacility(admitted);
		check.setHospitalizationReason(reason);
		check.setAdmissionDate(admission);
		check.setDischargeDate(discharge);

		return check;
	}
}
