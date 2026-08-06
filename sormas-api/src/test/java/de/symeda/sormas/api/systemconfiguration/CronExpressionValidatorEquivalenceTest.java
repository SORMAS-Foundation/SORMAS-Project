/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.systemconfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class CronExpressionValidatorEquivalenceTest {

	private static final Pattern PATTERN = Pattern.compile(CronExpressionValidator.VALUE_PATTERN);

	@Test
	public void patternAndIsValidAgreeAcrossGeneratedExpressions() {

		Random random = new Random(20260723L);
		List<String> disagreements = new ArrayList<>();

		for (int attempt = 0; attempt < 200000; attempt++) {
			StringBuilder expression = new StringBuilder();
			for (int field = 0; field < CronExpressionValidator.FIELD_COUNT; field++) {
				if (field > 0) {
					expression.append(' ');
				}
				expression.append(randomField(random));
			}
			String candidate = expression.toString();
			if (PATTERN.matcher(candidate).matches() != CronExpressionValidator.isValid(candidate)) {
				disagreements.add(candidate);
			}
		}

		assertEquals(List.of(), disagreements.subList(0, Math.min(disagreements.size(), 5)));
		assertTrue(disagreements.isEmpty(), disagreements.size() + " disagreements");
	}

	@Test
	public void patternAndIsValidAgreeOnEdgeCases() {

		for (String candidate : List.of("", "   ", "a b c d e f", "0 0 0 0 0", "0 0 0 0 0 0 0", "0,0 0 0 1 1 0")) {
			assertEquals(CronExpressionValidator.isValid(candidate), PATTERN.matcher(candidate).matches(), candidate);
		}
	}

	@Test
	public void patternAcceptsSupportedFormsDirectlyBecauseTheGenerativeTestCannotProveThisDirection() {

		List<String> mustBeAccepted = List.of(
			"0 15 1 * * *",
			"0 */10 * * * *",
			"0 */2 * * * *",
			"0 0 * * * *",
			"0 5 1 * * *",
			"0 40 2 * * *",
			"0 0 1,13 * * *",
			"0 0 1-5 * * *",
			"0 0 1-5/2 * * *",
			"*/10 0 0 * * *",
			"0 0 */23 * * *",
			"0 59 23 31 12 7",
			"0 05 1 * * *",
			"0 0 01 * * *",
			"0 0 1 01 01 0");

		for (String candidate : mustBeAccepted) {
			assertTrue(PATTERN.matcher(candidate).matches(), candidate);
		}
	}

	private String randomField(Random random) {

		int shape = random.nextInt(100);
		if (shape < 25) {
			return "*";
		}
		if (shape < 40) {
			return "*/" + random.nextInt(71);
		}
		if (shape < 60) {
			return String.valueOf(random.nextInt(71));
		}
		if (shape < 75) {
			return random.nextInt(71) + "-" + random.nextInt(71);
		}
		if (shape < 88) {
			return random.nextInt(71) + "/" + random.nextInt(71);
		}
		return random.nextInt(71) + "," + random.nextInt(71);
	}
}
