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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseCaseClassificationRef;

/**
 * Verifies which cases each subject code notifies.
 *
 * <p>
 * Focus is both inclusion and exclusion, because classification rules directly control export rows.
 */
public class EpipulseCaseInclusionTest {

	@Test
	@DisplayName("TUBE notifies confirmed, probable and possible cases only")
	public void tubeNotifiesConfirmedProbableAndPossible() {

		Set<CaseClassification> notified = EpipulseCaseInclusion.notifiedBy(EpipulseSubjectCode.TUBE);

		assertThat(
			notified,
			containsInAnyOrder(
				CaseClassification.CONFIRMED,
				CaseClassification.CONFIRMED_NO_SYMPTOMS,
				CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS,
				CaseClassification.PROBABLE,
				CaseClassification.SUSPECT));

		// Unclassified and ruled-out cases are not notifications.
		assertThat(notified.contains(CaseClassification.NOT_CLASSIFIED), is(false));
		assertThat(notified.contains(CaseClassification.NO_CASE), is(false));

		assertThat(EpipulseCaseInclusion.isNarrowed(EpipulseSubjectCode.TUBE), is(true));
	}

	@Test
	@DisplayName("DENGUE notifies confirmed and probable cases only")
	public void dengueNotifiesConfirmedAndProbable() {

		Set<CaseClassification> notified = EpipulseCaseInclusion.notifiedBy(EpipulseSubjectCode.DENGUE);

		assertThat(
			notified,
			containsInAnyOrder(
				CaseClassification.CONFIRMED,
				CaseClassification.CONFIRMED_NO_SYMPTOMS,
				CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS,
				CaseClassification.PROBABLE));

		// The metadata value set has no POSS for DENGUE.
		assertThat(notified.contains(CaseClassification.SUSPECT), is(false));
		assertThat(notified.contains(CaseClassification.NOT_CLASSIFIED), is(false));
		assertThat(notified.contains(CaseClassification.NO_CASE), is(false));

		assertThat(EpipulseCaseInclusion.isNarrowed(EpipulseSubjectCode.DENGUE), is(true));
	}

	@Test
	@DisplayName("MALA notifies confirmed cases only")
	public void malaNotifiesConfirmedOnly() {

		Set<CaseClassification> notified = EpipulseCaseInclusion.notifiedBy(EpipulseSubjectCode.MALA);

		assertThat(
			notified,
			containsInAnyOrder(CaseClassification.CONFIRMED, CaseClassification.CONFIRMED_NO_SYMPTOMS, CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS));

		// The metadata value set has neither PROB nor POSS for MALA.
		assertThat(notified.contains(CaseClassification.PROBABLE), is(false));
		assertThat(notified.contains(CaseClassification.SUSPECT), is(false));
		assertThat(notified.contains(CaseClassification.NOT_CLASSIFIED), is(false));
		assertThat(notified.contains(CaseClassification.NO_CASE), is(false));

		assertThat(EpipulseCaseInclusion.isNarrowed(EpipulseSubjectCode.MALA), is(true));
	}

	@Test
	@DisplayName("a subject code with no rule notifies every case of its disease")
	public void aSubjectCodeWithNoRuleNotifiesEveryCase() {

		// Default behavior for non-enrolled codes is no narrowing.
		for (EpipulseSubjectCode subjectCode : EpipulseSubjectCode.values()) {
			if (subjectCode != EpipulseSubjectCode.TUBE && subjectCode != EpipulseSubjectCode.DENGUE && subjectCode != EpipulseSubjectCode.MALA) {
				assertThat(subjectCode.name(), EpipulseCaseInclusion.notifiedBy(subjectCode), is(empty()));
				assertThat(subjectCode.name(), EpipulseCaseInclusion.isNarrowed(subjectCode), is(false));
			}
		}
	}

	@Test
	@DisplayName("all CONFIRMED* classifications are confirmations")
	public void theSymptomQualifiedConfirmationsAreConfirmations() {

		// Symptom-qualified confirmations must map as confirmed.
		assertThat(EpipulseCaseClassificationRef.getByCaseClassification(CaseClassification.CONFIRMED), is(EpipulseCaseClassificationRef.CONF));
		assertThat(
			EpipulseCaseClassificationRef.getByCaseClassification(CaseClassification.CONFIRMED_NO_SYMPTOMS),
			is(EpipulseCaseClassificationRef.CONF));
		assertThat(
			EpipulseCaseClassificationRef.getByCaseClassification(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS),
			is(EpipulseCaseClassificationRef.CONF));

		assertThat(EpipulseCaseClassificationRef.getByCaseClassification(CaseClassification.SUSPECT), is(EpipulseCaseClassificationRef.POSS));
		assertThat(EpipulseCaseClassificationRef.getByCaseClassification(CaseClassification.PROBABLE), is(EpipulseCaseClassificationRef.PROB));
	}

	@Test
	@DisplayName("no EpiPulse code claims an unclassified or ruled-out case")
	public void noCodeClaimsAnUnclassifiedOrRuledOutCase() {

		// NOT_CLASSIFIED and NO_CASE must remain unmapped.
		for (CaseClassification classification : new CaseClassification[] {
			CaseClassification.NOT_CLASSIFIED,
			CaseClassification.NO_CASE }) {

			assertThat(classification.name(), EpipulseCaseClassificationRef.getByCaseClassification(classification), is(nullRef()));
		}

		assertThat(EpipulseCaseClassificationRef.getByCaseClassification(null), is(nullRef()));
	}

	@Test
	@DisplayName("every EpiPulse classification code stands for at least one SORMAS classification")
	public void everyCodeStandsForAClassification() {

		for (EpipulseCaseClassificationRef code : EpipulseCaseClassificationRef.values()) {
			assertThat(code.name(), code.getCaseClassifications(), is(not(empty())));
		}
	}

	private static EpipulseCaseClassificationRef nullRef() {
		return null;
	}
}
