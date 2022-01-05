/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.caze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.EnumMap;
import java.util.Map;

import org.junit.Test;

public class CaseLogicTest {

	@Test
	public void testCalculateReinfectionStatus() {

		// Confirmed reinfection
		Map<ReinfectionDetail, Boolean> reinfectionDetails = new EnumMap<>(ReinfectionDetail.class);
		reinfectionDetails.put(ReinfectionDetail.GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN, true);
		reinfectionDetails.put(ReinfectionDetail.GENOME_SEQUENCE_CURRENT_INFECTION_KNOWN, true);
		reinfectionDetails.put(ReinfectionDetail.GENOME_SEQUENCES_NOT_MATCHING, true);
		assertEquals(ReinfectionStatus.CONFIRMED, CaseLogic.calculateReinfectionStatus(reinfectionDetails));

		reinfectionDetails.replace(ReinfectionDetail.GENOME_SEQUENCES_NOT_MATCHING, false);
		assertNull(CaseLogic.calculateReinfectionStatus(reinfectionDetails));
		reinfectionDetails.remove(ReinfectionDetail.GENOME_SEQUENCES_NOT_MATCHING);
		assertNull(CaseLogic.calculateReinfectionStatus(reinfectionDetails));

		// Probable reinfection
		reinfectionDetails.clear();
		reinfectionDetails.put(ReinfectionDetail.GENOME_SEQUENCE_CURRENT_INFECTION_KNOWN, true);
		reinfectionDetails.put(ReinfectionDetail.ACUTE_RESPIRATORY_ILLNESS_OVERCOME, true);
		reinfectionDetails.put(ReinfectionDetail.TESTED_NEGATIVE_AFTER_PREVIOUS_INFECTION, true);
		reinfectionDetails.put(ReinfectionDetail.GENOME_COPY_NUMBER_ABOVE_THRESHOLD, true);
		assertEquals(ReinfectionStatus.PROBABLE, CaseLogic.calculateReinfectionStatus(reinfectionDetails));

		reinfectionDetails.put(ReinfectionDetail.GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN, false);
		assertEquals(ReinfectionStatus.PROBABLE, CaseLogic.calculateReinfectionStatus(reinfectionDetails));
		reinfectionDetails.replace(ReinfectionDetail.GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN, true);
		assertNull(CaseLogic.calculateReinfectionStatus(reinfectionDetails));

		reinfectionDetails.remove(ReinfectionDetail.GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN);
		assertEquals(ReinfectionStatus.PROBABLE, CaseLogic.calculateReinfectionStatus(reinfectionDetails));
		reinfectionDetails.remove(ReinfectionDetail.ACUTE_RESPIRATORY_ILLNESS_OVERCOME);
		assertNull(CaseLogic.calculateReinfectionStatus(reinfectionDetails));
		reinfectionDetails.put(ReinfectionDetail.ACUTE_RESPIRATORY_ILLNESS_OVERCOME, true);
		reinfectionDetails.remove(ReinfectionDetail.TESTED_NEGATIVE_AFTER_PREVIOUS_INFECTION);
		assertNull(CaseLogic.calculateReinfectionStatus(reinfectionDetails));
		reinfectionDetails.put(ReinfectionDetail.TESTED_NEGATIVE_AFTER_PREVIOUS_INFECTION, true);
		reinfectionDetails.remove(ReinfectionDetail.GENOME_COPY_NUMBER_ABOVE_THRESHOLD);
		assertNull(CaseLogic.calculateReinfectionStatus(reinfectionDetails));

		// Possible reinfection
		reinfectionDetails.clear();
		reinfectionDetails.put(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION, true);
		reinfectionDetails.put(ReinfectionDetail.LAST_PCR_DETECTION_NOT_RECENT, true);
		reinfectionDetails.put(ReinfectionDetail.GENOME_COPY_NUMBER_BELOW_THRESHOLD, true);
		assertEquals(ReinfectionStatus.POSSIBLE, CaseLogic.calculateReinfectionStatus(reinfectionDetails));

		reinfectionDetails.remove(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION);
		assertNull(CaseLogic.calculateReinfectionStatus(reinfectionDetails));
		reinfectionDetails.put(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION, true);
		reinfectionDetails.remove(ReinfectionDetail.LAST_PCR_DETECTION_NOT_RECENT);
		assertNull(CaseLogic.calculateReinfectionStatus(reinfectionDetails));
		reinfectionDetails.put(ReinfectionDetail.LAST_PCR_DETECTION_NOT_RECENT, true);
		reinfectionDetails.remove(ReinfectionDetail.GENOME_COPY_NUMBER_BELOW_THRESHOLD);
		assertNull(CaseLogic.calculateReinfectionStatus(reinfectionDetails));
	}
}
