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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseMalaModeOfTransmissionRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseMalaPathogenRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseOccupationRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePurposeOfTravelRef;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.exposure.TravelPurpose;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseValues;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;

/**
 * Verifies that MALA writes the file EpiPulse declares for it, with no database: a field model is a
 * list of definitions, and the file's shape is a question about that list alone.
 *
 * <p>
 * The four reference classes are checked here rather than in their own tests, since the failure
 * modes are the same kind: a constant left out, or matched to the wrong counterpart. Two deserve more
 * than a round-trip and get it:
 *
 * <ul>
 * <li>{@code EpipulseMalaModeOfTransmissionRef} pairs two SORMAS constants with two EpiPulse codes
 * whose names read as each other's definition: "mosquito-introduced" means the case <em>with</em>
 * a link to an imported one, "mosquito-indigenous" the case <em>without</em>. Swapping them
 * compiles, exports, and inverts the one distinction the variable exists to draw, so the pairing
 * is asserted against each constant's caption rather than its name.</li>
 * <li>{@code EpipulseOccupationRef} falls through to {@code OTH} rather than to nothing, the
 * opposite of every other lookup in that package. The two values that must <em>not</em> fall
 * through are named here so the exception to the exception is written down.</li>
 * </ul>
 */
public class MalaFieldModelTest {

	/**
	 * The variables the EpiPulse metadata marks {@code Repeatable = Yes} for MALA. The flags are
	 * per (subject code, variable) and are not generated into code, so they are named here.
	 */
	private static final List<EpipulseVariable> REPEATABLE = Arrays.asList(EpipulseVariable.PATHOGEN, EpipulseVariable.PLACE_OF_INFECTION);

	private static FieldModel fieldModel() {
		return EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.MALA);
	}

	@Test
	@DisplayName("MALA has a model of its own rather than the shared one, and selects malaria cases")
	public void malaHasAModelOfItsOwn() {

		assertThat(EpipulseFieldModels.hasDedicatedModel(EpipulseSubjectCode.MALA), is(true));
		assertThat(EpipulseSubjectCode.MALA.getDisease(), is(Disease.MALARIA));
	}

	@Test
	@DisplayName("every variable EpiPulse declares for MALA is a column, and none is written twice")
	public void everyDeclaredVariableIsAColumn() {

		EpipulseDiseaseExportEntryDto emptyCase = new EpipulseDiseaseExportEntryDto();
		emptyCase.setSubjectCode(EpipulseSubjectCode.MALA);

		// One empty case rather than none.
		List<String> columns = fieldModel().layout(Collections.singletonList(emptyCase)).columnNames();

		Set<String> declared = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.MALA)
			.stream()
			.map(EpipulseVariable::getVariableName)
			.collect(Collectors.toCollection(TreeSet::new));

		assertEquals(declared, new TreeSet<>(columns), "the columns MALA writes");
		assertEquals(declared.size(), columns.size(), "no variable is written twice");
	}

	@Test
	@DisplayName("AgeMonth is not among them, which is why the shared block is filtered")
	public void ageMonthIsNotAmongThem() {

		// The one column that would appear if this model took CommonFields.defs() wholesale as PERT
		// and MEAS do. Named on its own because the set assertion above would report it alongside
		// anything else and not say what it means.
		assertThat(new EpipulseValues(fieldModel()).emits(EpipulseVariable.AGE_MONTH), is(false));
	}

	@Test
	@DisplayName("a column group is repeatable exactly where EpiPulse says it is")
	public void aColumnGroupIsRepeatableExactlyWhereEpipulseSaysItIs() {

		EpipulseValues values = new EpipulseValues(fieldModel());
		List<String> wrong = new ArrayList<>();

		for (EpipulseVariable variable : EpipulseVariable.values()) {
			if (values.emits(variable) && values.isRepeated(variable) != REPEATABLE.contains(variable)) {
				wrong.add(variable.getVariableName());
			}
		}

		assertThat("columns whose repeatability disagrees with the metadata", wrong, is(empty()));
	}

	@Test
	@DisplayName("both repeatable groups survive an export in which no case has a value")
	public void bothRepeatableGroupsSurviveAnEmptyExport() {

		// The realistic export rather than a corner case: PlaceOfInfection needs travel inside the
		// incubation window and Pathogen needs a species on a verified positive test, and a case
		// can easily have neither.
		List<String> columns = fieldModel().layout(Collections.singletonList(new EpipulseDiseaseExportEntryDto())).columnNames();

		for (EpipulseVariable variable : REPEATABLE) {
			assertThat(
				variable.getVariableName() + " is absent from the header of an empty export",
				columns.contains(variable.getVariableName()),
				is(true));
		}
	}

	// ---------------------------------------------------------------------------------------
	// CaseClassification, which MALA narrows
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("CaseClassification is the one shared mapping, even though MALA's value set is narrower than it")
	public void caseClassificationIsTheOneSharedMapping() {

		// EpiPulse gives MALA the single value CONF, so POSS and PROB below are values it will
		// reject. Sending them is the decision rather than an oversight: the question a rejected
		// suspected case raises is whether it belonged in the submission at all, which is answered
		// by filtering the export per subject code and not by blanking one column. Every disease
		// keeps one mapping until that filtering exists.
		//
		// This test exists to make that decision visible. A change here should be accompanied by
		// the classification filter in filtered_cases, not by a per-code narrowing of the column.
		assertThat(EpipulseMapping.caseClassification(classifiedAs(CaseClassification.CONFIRMED)), is("CONF"));
		assertThat(EpipulseMapping.caseClassification(classifiedAs(CaseClassification.SUSPECT)), is("POSS"));
		assertThat(EpipulseMapping.caseClassification(classifiedAs(CaseClassification.PROBABLE)), is("PROB"));
		assertThat(EpipulseMapping.caseClassification(classifiedAs(null)), is((String) null));
	}

	// ---------------------------------------------------------------------------------------
	// ModeOfTransmission
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("mosquito-introduced is the case linked to an imported one, and indigenous the case that is not")
	public void theTwoLocalMosquitoCodesAreNotSwapped() {

		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.MOSQUITOES_WITH_STRONG_EPI_EVIDENCE), is("MOSQINTRO"));
		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.MOSQUITOES_WITHOUT_EVIDENCE), is("MOSQINDIG"));

		// the third local code, which is neither of those two: the mosquito was flown in
		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.MOSQUITOES_BY_AIR), is("MOSQIMP"));
	}

	@Test
	@DisplayName("both SORMAS spellings of healthcare-acquired and of mother-to-child reach one EpiPulse code")
	public void bothSormasSpellingsReachOneCode() {

		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.MEDICAL_CARE), is("HAI"));
		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.HEALTHCARE_ASSOCIATED), is("HAI"));
		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.FROM_MOTHER_TO_CHILD), is("MTCT"));
		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.MOTHER_TO_CHILD), is("MTCT"));
	}

	@Test
	@DisplayName("a transfusion or transplant is SOHO, which is the code SALM's set cannot express")
	public void aTransfusionOrTransplantIsSoho() {

		// SALM splits TRANSFU from ORGAN. MALA has one code covering both, so the same constant is answerable here.
		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.TRANSFUSION_TRANSPLANT_RECIPIENT), is("SOHO"));
	}

	@Test
	@DisplayName("a bite in an endemic country reports nothing, because every MALA code is about local transmission")
	public void anOrdinaryImportedCaseReportsNoModeOfTransmission() {

		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.MOSQUITOES_FROM_ENDEMIC_COUNTRY), is((String) null));

		// UNKNOWN is not OTH.
		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.UNKNOWN), is((String) null));
		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(ModeOfTransmission.BY_LAB), is((String) null));
		assertThat(EpipulseMalaModeOfTransmissionRef.codeFor(null), is((String) null));
	}

	// ---------------------------------------------------------------------------------------
	// Pathogen
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("every Plasmodium species SORMAS records has a MALA code, and every MALA code a species")
	public void everyPathogenCodeHasASpecieBehindIt() {

		List<String> unmapped = new ArrayList<>();

		for (EpipulseMalaPathogenRef ref : EpipulseMalaPathogenRef.values()) {
			for (PathogenSpecie specie : ref.getSpecies()) {
				if (!ref.name().equals(EpipulseMalaPathogenRef.codeFor(specie))) {
					unmapped.add(ref.name() + "/" + specie.name());
				}
			}
		}

		assertThat("pathogen codes that do not round-trip through their SORMAS constant", unmapped, is(empty()));
		assertEquals(7, EpipulseMalaPathogenRef.values().length, "the Plasmodium species MALA names");
	}

	@Test
	@DisplayName("both SORMAS spellings of an unspecified species reach PLASSPP")
	public void bothSpellingsOfAnUnspecifiedSpecieReachOneCode() {

		// SPP is captioned "Plasmodium spp" and NOT_SPECIFIED "Plasmodium species not specified" -
		// the same statement written twice. This is also the one place the mapping is not
		// one-to-one, which is why MalaFieldModel de-duplicates after mapping and not only before.
		assertThat(EpipulseMalaPathogenRef.codeFor(PathogenSpecie.SPP), is("PLASSPP"));
		assertThat(EpipulseMalaPathogenRef.codeFor(PathogenSpecie.NOT_SPECIFIED), is("PLASSPP"));
	}

	@Test
	@DisplayName("a coinfection marker names no species, so it reports none")
	public void aCoinfectionMarkerReportsNoSpecie() {

		// Pathogen is repeatable so that a coinfection is reported as the species involved, one
		// column each.
		assertThat(EpipulseMalaPathogenRef.codeFor(PathogenSpecie.COINFECTION), is((String) null));
		assertThat(EpipulseMalaPathogenRef.codeFor(PathogenSpecie.OTHER), is((String) null));
		assertThat(EpipulseMalaPathogenRef.codeFor(PathogenSpecie.UNKNOWN), is((String) null));
		assertThat(EpipulseMalaPathogenRef.codeFor(null), is((String) null));

		// and a species belonging to another disease's list is not a malaria pathogen
		assertThat(EpipulseMalaPathogenRef.codeFor(PathogenSpecie.SONNEI), is((String) null));
	}

	// ---------------------------------------------------------------------------------------
	// Occupation and PurposeOfTravel
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("Occupation names the airport and healthcare workers and calls every other stated occupation Other")
	public void occupationNamesTwoAndCallsTheRestOther() {

		// WORK_IN_AIRPORT is seeded in sormas_schema.sql against DENGUE and MALARIA, so AIRW is
		// reachable on a malaria case rather than aspirational.
		assertThat(EpipulseOccupationRef.codeFor("WORK_IN_AIRPORT"), is("AIRW"));
		assertThat(EpipulseOccupationRef.codeFor("HEALTHCARE_WORKER"), is("HCW"));

		// the inverted fall-through: a three-value list against the dozens SORMAS ships, so being
		// outside it is the ordinary case and OTH is a true statement about each of these
		assertThat(EpipulseOccupationRef.codeFor("LABORATORY_STAFF"), is("OTH"));
		assertThat(EpipulseOccupationRef.codeFor("BUTCHER"), is("OTH"));
		assertThat(EpipulseOccupationRef.codeFor("OTHER"), is("OTH"));
		// an occupation an instance added to the customizable enum, which has no constant here
		assertThat(EpipulseOccupationRef.codeFor("SOME_LOCALLY_DEFINED_JOB"), is("OTH"));
	}

	@Test
	@DisplayName("an occupation that was never stated is not Other")
	public void anUnstatedOccupationIsNotOther() {

		// The exception to the fall-through, and the whole reason it needs one: OTH would turn "not
		// stated" into "stated, and outside the list".
		assertThat(EpipulseOccupationRef.codeFor("UNKNOWN"), is((String) null));
		assertThat(EpipulseOccupationRef.codeFor("NOT_APPLICABLE"), is((String) null));
		assertThat(EpipulseOccupationRef.codeFor(""), is((String) null));
		assertThat(EpipulseOccupationRef.codeFor(null), is((String) null));
	}

	@Test
	@DisplayName("every travel purpose SORMAS records has a MALA code, except the one that states none")
	public void everyTravelPurposeHasACode() {

		// TravelPurpose was written from this list, so the two are expected to stay in step: a
		// constant added to one belongs in the other. UNKNOWN is the single exception.
		List<String> unmapped = new ArrayList<>();

		for (TravelPurpose purpose : TravelPurpose.values()) {
			if (purpose != TravelPurpose.UNKNOWN && EpipulsePurposeOfTravelRef.codeFor(purpose) == null) {
				unmapped.add(purpose.name());
			}
		}

		assertThat("travel purposes MALA cannot express", unmapped, is(empty()));
		assertEquals(9, EpipulsePurposeOfTravelRef.values().length, "the travel purposes MALA names");

		// the two most easily confused, which the captions rather than the names tell apart
		assertThat(EpipulsePurposeOfTravelRef.codeFor(TravelPurpose.TRAVELER_FROM_ENDEMIC_COUNTRY), is("ENDTRAV"));
		assertThat(EpipulsePurposeOfTravelRef.codeFor(TravelPurpose.MIGRATION), is("MIG"));

		// UNKNOWN is not OTH, for the reason it is not OTH anywhere else in this package
		assertThat(EpipulsePurposeOfTravelRef.codeFor(TravelPurpose.UNKNOWN), is((String) null));
		assertThat(EpipulsePurposeOfTravelRef.codeFor(null), is((String) null));
	}

	private static EpipulseDiseaseExportEntryDto classifiedAs(CaseClassification classification) {

		EpipulseDiseaseExportEntryDto entry = new EpipulseDiseaseExportEntryDto();
		entry.setCaseClassification(classification);

		return entry;
	}
}
