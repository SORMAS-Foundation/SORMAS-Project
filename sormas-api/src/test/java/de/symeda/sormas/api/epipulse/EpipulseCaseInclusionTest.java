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

import java.util.EnumSet;
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
	@DisplayName("TUBE, CONSYPH, PERT and MENI notify confirmed, probable and possible cases only")
	public void codesNotifyingConfirmedProbableAndPossible() {

		for (EpipulseSubjectCode subjectCode : EnumSet
			.of(EpipulseSubjectCode.TUBE, EpipulseSubjectCode.CONSYPH, EpipulseSubjectCode.PERT, EpipulseSubjectCode.MENI)) {

			Set<CaseClassification> notified = EpipulseCaseInclusion.notifiedBy(subjectCode);

			assertThat(
				subjectCode.name(),
				notified,
				containsInAnyOrder(
					CaseClassification.CONFIRMED,
					CaseClassification.CONFIRMED_NO_SYMPTOMS,
					CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS,
					CaseClassification.PROBABLE,
					CaseClassification.SUSPECT));

			// Unclassified and ruled-out cases are not notifications.
			assertThat(subjectCode.name(), notified.contains(CaseClassification.NOT_CLASSIFIED), is(false));
			assertThat(subjectCode.name(), notified.contains(CaseClassification.NO_CASE), is(false));

			assertThat(subjectCode.name(), EpipulseCaseInclusion.isNarrowed(subjectCode), is(true));
		}
	}

	@Test
	@DisplayName("SYPH and GONO notify confirmed, probable, possible and not yet classified cases")
	public void syphAndGonoNotifyClassifiedAndUnclassifiedCases() {

		for (EpipulseSubjectCode subjectCode : EnumSet.of(EpipulseSubjectCode.SYPH, EpipulseSubjectCode.GONO)) {

			Set<CaseClassification> notified = EpipulseCaseInclusion.notifiedBy(subjectCode);

			// "leave empty until case definitions are met": an unclassified case is sent, blank.
			assertThat(
				subjectCode.name(),
				notified,
				containsInAnyOrder(
					CaseClassification.CONFIRMED,
					CaseClassification.CONFIRMED_NO_SYMPTOMS,
					CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS,
					CaseClassification.PROBABLE,
					CaseClassification.SUSPECT,
					CaseClassification.NOT_CLASSIFIED));

			// A ruled-out case is still not a notification.
			assertThat(subjectCode.name(), notified.contains(CaseClassification.NO_CASE), is(false));

			assertThat(subjectCode.name(), EpipulseCaseInclusion.isNarrowed(subjectCode), is(true));
		}

		// CONSYPH shares the disease and the value set, but not the empty column.
		assertThat(EpipulseCaseInclusion.notifiedBy(EpipulseSubjectCode.CONSYPH).contains(CaseClassification.NOT_CLASSIFIED), is(false));
	}

	@Test
	@DisplayName("DENGUE, SALM and SHIG notify confirmed and probable cases only")
	public void dengueSalmAndShigNotifyConfirmedAndProbable() {

		for (EpipulseSubjectCode subjectCode : EnumSet.of(EpipulseSubjectCode.DENGUE, EpipulseSubjectCode.SALM, EpipulseSubjectCode.SHIG)) {

			Set<CaseClassification> notified = EpipulseCaseInclusion.notifiedBy(subjectCode);

			assertThat(
				subjectCode.name(),
				notified,
				containsInAnyOrder(
					CaseClassification.CONFIRMED,
					CaseClassification.CONFIRMED_NO_SYMPTOMS,
					CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS,
					CaseClassification.PROBABLE));

			// The metadata value set has no POSS for either code.
			assertThat(subjectCode.name(), notified.contains(CaseClassification.SUSPECT), is(false));
			assertThat(subjectCode.name(), notified.contains(CaseClassification.NOT_CLASSIFIED), is(false));
			assertThat(subjectCode.name(), notified.contains(CaseClassification.NO_CASE), is(false));

			assertThat(subjectCode.name(), EpipulseCaseInclusion.isNarrowed(subjectCode), is(true));
		}
	}

	@Test
	@DisplayName("MALA and PNEU notify confirmed cases only")
	public void malaAndPneuNotifyConfirmedOnly() {

		for (EpipulseSubjectCode subjectCode : EnumSet.of(EpipulseSubjectCode.MALA, EpipulseSubjectCode.PNEU)) {

			Set<CaseClassification> notified = EpipulseCaseInclusion.notifiedBy(subjectCode);

			assertThat(
				subjectCode.name(),
				notified,
				containsInAnyOrder(CaseClassification.CONFIRMED, CaseClassification.CONFIRMED_NO_SYMPTOMS, CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS));

			// The metadata value set has neither PROB nor POSS for either code.
			assertThat(subjectCode.name(), notified.contains(CaseClassification.PROBABLE), is(false));
			assertThat(subjectCode.name(), notified.contains(CaseClassification.SUSPECT), is(false));
			assertThat(subjectCode.name(), notified.contains(CaseClassification.NOT_CLASSIFIED), is(false));
			assertThat(subjectCode.name(), notified.contains(CaseClassification.NO_CASE), is(false));

			assertThat(subjectCode.name(), EpipulseCaseInclusion.isNarrowed(subjectCode), is(true));
		}
	}

	@Test
	@DisplayName("MEAS and RUBE notify confirmed, probable, possible and discarded cases")
	public void measAndRubeNotifyEveryClassifiedCaseIncludingDiscarded() {

		for (EpipulseSubjectCode subjectCode : EnumSet.of(EpipulseSubjectCode.MEAS, EpipulseSubjectCode.RUBE)) {

			Set<CaseClassification> notified = EpipulseCaseInclusion.notifiedBy(subjectCode);

			assertThat(
				subjectCode.name(),
				notified,
				containsInAnyOrder(
					CaseClassification.CONFIRMED,
					CaseClassification.CONFIRMED_NO_SYMPTOMS,
					CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS,
					CaseClassification.PROBABLE,
					CaseClassification.SUSPECT,
					CaseClassification.NO_CASE));

			// An unclassified case is still not a notification.
			assertThat(subjectCode.name(), notified.contains(CaseClassification.NOT_CLASSIFIED), is(false));

			assertThat(subjectCode.name(), EpipulseCaseInclusion.isNarrowed(subjectCode), is(true));
		}
	}

	@Test
	@DisplayName("DISCARDED is reported only by a code that declares it")
	public void discardedIsReportedOnlyWhereDeclared() {

		Set<EpipulseSubjectCode> declaring = EnumSet.of(EpipulseSubjectCode.MEAS, EpipulseSubjectCode.RUBE, EpipulseSubjectCode.DIPH);

		for (EpipulseSubjectCode subjectCode : EpipulseSubjectCode.values()) {
			assertThat(
				subjectCode.name(),
				EpipulseCaseInclusion.reports(subjectCode, EpipulseCaseClassificationRef.DISCARDED),
				is(declaring.contains(subjectCode)));
		}
		assertThat(EpipulseCaseInclusion.reports(null, EpipulseCaseClassificationRef.DISCARDED), is(false));

		// A code with no rule still writes only its own value set: CHIK has no POSS.
		assertThat(EpipulseCaseInclusion.reports(EpipulseSubjectCode.CHIK, EpipulseCaseClassificationRef.POSS), is(false));
		assertThat(EpipulseCaseInclusion.reports(EpipulseSubjectCode.MUMP, EpipulseCaseClassificationRef.POSS), is(true));
	}

	@Test
	@DisplayName("a subject code with no rule notifies every case of its disease")
	public void aSubjectCodeWithNoRuleNotifiesEveryCase() {

		// Default behavior for non-enrolled codes is no narrowing.
		Set<EpipulseSubjectCode> narrowed =
			EnumSet.of(EpipulseSubjectCode.TUBE, EpipulseSubjectCode.DENGUE, EpipulseSubjectCode.MALA, EpipulseSubjectCode.MEAS, EpipulseSubjectCode.RUBE, EpipulseSubjectCode.SALM,
			EpipulseSubjectCode.SHIG,
			EpipulseSubjectCode.SYPH,
			EpipulseSubjectCode.CONSYPH,
			EpipulseSubjectCode.PNEU,
			EpipulseSubjectCode.PERT,
			EpipulseSubjectCode.MENI,
			EpipulseSubjectCode.GONO);

		for (EpipulseSubjectCode subjectCode : EnumSet.complementOf(EnumSet.copyOf(narrowed))) {
			assertThat(subjectCode.name(), EpipulseCaseInclusion.notifiedBy(subjectCode), is(empty()));
			assertThat(subjectCode.name(), EpipulseCaseInclusion.isNarrowed(subjectCode), is(false));
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
	@DisplayName("no EpiPulse code claims an unclassified case, and a ruled-out case is DISCARDED")
	public void noCodeClaimsAnUnclassifiedCase() {

		// NOT_CLASSIFIED must remain unmapped.
		assertThat(EpipulseCaseClassificationRef.getByCaseClassification(CaseClassification.NOT_CLASSIFIED), is(nullRef()));
		assertThat(EpipulseCaseClassificationRef.getByCaseClassification(null), is(nullRef()));

		// Which subject codes may write it is EpipulseCaseInclusion.reports, tested above.
		assertThat(EpipulseCaseClassificationRef.getByCaseClassification(CaseClassification.NO_CASE), is(EpipulseCaseClassificationRef.DISCARDED));
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
