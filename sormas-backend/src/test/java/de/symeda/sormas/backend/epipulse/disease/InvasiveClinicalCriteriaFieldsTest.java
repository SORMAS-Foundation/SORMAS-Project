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

package de.symeda.sormas.backend.epipulse.disease;

import static de.symeda.sormas.backend.epipulse.disease.InvasiveClinicalCriteriaFields.MENI_PNEUMONIA_CODE;
import static de.symeda.sormas.backend.epipulse.disease.InvasiveClinicalCriteriaFields.PNEU_PNEUMONIA_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.symptoms.SymptomState;

/**
 * The {@code ClinicalCriteria} ladder, rung by rung. No database: the classifier is a pure
 * function of four symptom states, and the export that feeds it is covered in
 * {@code MeningococcalEpipulseExportTest}.
 */
public class InvasiveClinicalCriteriaFieldsTest {

	/**
	 * The ladder as MENI walks it, with the case not marked asymptomatic -- that gate has tests of
	 * its own below.
	 */
	private static String meniCode(SymptomState meningitis, SymptomState septicaemia, SymptomState pneumonia) {
		return InvasiveClinicalCriteriaFields.clinicalCriteria(meningitis, septicaemia, pneumonia, SymptomState.NO, MENI_PNEUMONIA_CODE);
	}

	@Test
	@DisplayName("the ladder reports the most specific presentation recorded")
	public void theLadderReportsTheMostSpecificPresentationRecorded() {

		assertEquals("MENISEPTI", meniCode(SymptomState.YES, SymptomState.YES, SymptomState.YES));
		assertEquals("MENISEPTI", meniCode(SymptomState.YES, SymptomState.YES, SymptomState.NO));
		assertEquals("MENI", meniCode(SymptomState.YES, SymptomState.NO, SymptomState.YES));
		assertEquals("SEPTI", meniCode(SymptomState.NO, SymptomState.YES, SymptomState.YES));
		assertEquals(MENI_PNEUMONIA_CODE, meniCode(SymptomState.NO, SymptomState.NO, SymptomState.YES));
	}

	@Test
	@DisplayName("the two diseases differ only in the code for pneumonia")
	public void theTwoDiseasesDifferOnlyInTheCodeForPneumonia() {

		// the one rung whose code is a parameter
		assertEquals(
			PNEU_PNEUMONIA_CODE,
			InvasiveClinicalCriteriaFields
				.clinicalCriteria(SymptomState.NO, SymptomState.NO, SymptomState.YES, SymptomState.NO, PNEU_PNEUMONIA_CODE));

		// every other rung is shared, so the code passed in must not reach them
		SymptomState[][] sharedRungs = {
			{
				SymptomState.YES,
				SymptomState.YES },
			{
				SymptomState.YES,
				SymptomState.NO },
			{
				SymptomState.NO,
				SymptomState.YES } };

		for (SymptomState[] rung : sharedRungs) {
			assertEquals(
				meniCode(rung[0], rung[1], SymptomState.NO),
				InvasiveClinicalCriteriaFields.clinicalCriteria(rung[0], rung[1], SymptomState.NO, SymptomState.NO, PNEU_PNEUMONIA_CODE));
		}
	}

	@Test
	@DisplayName("an asymptomatic case reports nothing, whatever else was recorded beside it")
	public void anAsymptomaticCaseReportsNothing() {

		assertNull(
			InvasiveClinicalCriteriaFields
				.clinicalCriteria(SymptomState.YES, SymptomState.YES, SymptomState.YES, SymptomState.YES, MENI_PNEUMONIA_CODE));
	}

	@Test
	@DisplayName("a presentation outside the three reports nothing rather than OTH")
	public void aPresentationOutsideTheThreeReportsNothingRatherThanOth() {

		assertNull(
			InvasiveClinicalCriteriaFields.clinicalCriteria(SymptomState.NO, SymptomState.NO, SymptomState.NO, SymptomState.NO, MENI_PNEUMONIA_CODE));

		// no symptoms row at all: the left join hands the reader nulls throughout
		assertNull(InvasiveClinicalCriteriaFields.clinicalCriteria(null, null, null, null, MENI_PNEUMONIA_CODE));
	}

	@Test
	@DisplayName("only YES counts as present")
	public void onlyYesCountsAsPresent() {

		// Neither value set can say "unknown", so UNKNOWN and NO have to read the same.
		for (SymptomState notPresent : new SymptomState[] {
			SymptomState.NO,
			SymptomState.UNKNOWN,
			null }) {

			assertNull(meniCode(notPresent, notPresent, notPresent), "meningitis/septicaemia/pneumonia = " + notPresent);
		}
	}
}
