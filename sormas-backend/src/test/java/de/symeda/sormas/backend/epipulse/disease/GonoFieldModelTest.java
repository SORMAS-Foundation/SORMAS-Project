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
import de.symeda.sormas.api.clinicalcourse.HivStatus;
import de.symeda.sormas.api.epidata.ProbableRouteOfTransmission;
import de.symeda.sormas.api.epidata.TypeOfClinicalService;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseClinicalServiceTypeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStiHivStatusRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStiModeOfTransmissionRef;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseValues;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;

/**
 * Verifies that GONO writes the file EpiPulse declares for it, with no database: a field model is a
 * list of definitions, and the file's shape is a question about that list alone.
 *
 * <p>
 * The three reference classes are checked here rather than in their own tests. Each renames a
 * SORMAS enum into EpiPulse's spelling, so what can go wrong is a constant left out or matched to the
 * wrong counterpart. Two of them are complete one-to-one lists, so a round-trip over every constant
 * is the right assertion rather than a sample.
 */
public class GonoFieldModelTest {

	/**
	 * The variables the EpiPulse metadata marks {@code Repeatable = Yes} for GONO. The flags are
	 * per (subject code, variable) and are not generated into code, so they are named here.
	 */
	private static final List<EpipulseVariable> REPEATABLE = Arrays.asList(EpipulseVariable.PLACE_OF_INFECTION, EpipulseVariable.SITE_OF_INFECTION);

	private static FieldModel fieldModel() {
		return EpipulseFieldModels.forSubjectCode(EpipulseSubjectCode.GONO);
	}

	@Test
	@DisplayName("GONO has a model of its own rather than the shared one, and selects gonorrhoea cases")
	public void gonoHasAModelOfItsOwn() {

		assertThat(EpipulseFieldModels.hasDedicatedModel(EpipulseSubjectCode.GONO), is(true));
		assertThat(EpipulseSubjectCode.GONO.getDisease(), is(Disease.GONOCOCCAL_INFECTION));
	}

	@Test
	@DisplayName("every variable EpiPulse declares for GONO is a column, and none is written twice")
	public void everyDeclaredVariableIsAColumn() {

		EpipulseDiseaseExportEntryDto emptyCase = new EpipulseDiseaseExportEntryDto();
		emptyCase.setSubjectCode(EpipulseSubjectCode.GONO);

		// One empty case rather than none.
		List<String> columns = fieldModel().layout(Collections.singletonList(emptyCase)).columnNames();

		Set<String> declared = EpipulseSubjectCodeVariables.of(EpipulseSubjectCode.GONO)
			.stream()
			.map(EpipulseVariable::getVariableName)
			.collect(Collectors.toCollection(TreeSet::new));

		assertEquals(declared, new TreeSet<>(columns), "the columns GONO writes");
		assertEquals(declared.size(), columns.size(), "no variable is written twice");
	}

	@Test
	@DisplayName("CountryOfNationality is among them, which the shared default model never emitted")
	public void countryOfNationalityIsAmongThem() {

		assertThat(new EpipulseValues(fieldModel()).emits(EpipulseVariable.COUNTRY_OF_NATIONALITY), is(true));

		// and AgeMonth is not, which is why the shared block is filtered
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

		// The realistic export rather than a corner case: PlaceOfInfection needs dated travel and
		// SiteOfInfection needs one of nine symptom columns answered yes. An unfloored group would
		// vanish from the header and the submission would be the wrong shape.
		List<String> columns = fieldModel().layout(Collections.singletonList(new EpipulseDiseaseExportEntryDto())).columnNames();

		for (EpipulseVariable variable : REPEATABLE) {
			assertThat(
				variable.getVariableName() + " is absent from the header of an empty export",
				columns.contains(variable.getVariableName()),
				is(true));
		}
	}

	// ---------------------------------------------------------------------------------------
	// the three STI value sets
	// ---------------------------------------------------------------------------------------

	@Test
	@DisplayName("every clinical service SORMAS records has a code, and every code a service")
	public void everyClinicalServiceHasACode() {

		// Thirteen codes, thirteen constants, one to one - the SORMAS enum was written from this
		// variable, so a round-trip over every constant is the assertion rather than a sample.
		List<String> unmapped = new ArrayList<>();

		for (TypeOfClinicalService service : TypeOfClinicalService.values()) {
			if (EpipulseClinicalServiceTypeRef.codeFor(service) == null) {
				unmapped.add(service.name());
			}
		}

		assertThat("clinical services GONO cannot express", unmapped, is(empty()));
		assertEquals(TypeOfClinicalService.values().length, EpipulseClinicalServiceTypeRef.values().length, "the services and their codes");

		// the pair most easily collapsed into one: EpiPulse keeps other primary care apart from other
		assertThat(EpipulseClinicalServiceTypeRef.codeFor(TypeOfClinicalService.OTHER_PRIMARY_CARE), is("OPC"));
		assertThat(EpipulseClinicalServiceTypeRef.codeFor(TypeOfClinicalService.OTHER), is("OTH"));
		assertThat(EpipulseClinicalServiceTypeRef.codeFor(null), is((String) null));
	}

	@Test
	@DisplayName("every probable route of transmission has a code, except the one that states none")
	public void everyRouteHasACode() {

		List<String> unmapped = new ArrayList<>();

		for (ProbableRouteOfTransmission route : ProbableRouteOfTransmission.values()) {
			if (route != ProbableRouteOfTransmission.UNKNOWN && EpipulseStiModeOfTransmissionRef.codeFor(route) == null) {
				unmapped.add(route.name());
			}
		}

		assertThat("routes GONO cannot express", unmapped, is(empty()));
		assertThat(EpipulseStiModeOfTransmissionRef.codeFor(ProbableRouteOfTransmission.MSM_HOMO_OR_BISEXUAL_MALE), is("MSM"));
		assertThat(EpipulseStiModeOfTransmissionRef.codeFor(ProbableRouteOfTransmission.HETEROSEXUAL_CONTACT), is("HETERO"));

		// UNKNOWN is not OTH: EpiPulse's OTH is a positive claim about the route - the
		// specification names injecting drug use and blood transfusion as the kind of thing it
		// covers - where UNKNOWN means the route was never established.
		assertThat(EpipulseStiModeOfTransmissionRef.codeFor(ProbableRouteOfTransmission.UNKNOWN), is((String) null));
		assertThat(EpipulseStiModeOfTransmissionRef.codeFor(null), is((String) null));
	}

	@Test
	@DisplayName("the two positive HIV statuses stay apart, and an unknown one reports nothing")
	public void hivStatusKeepsTheTwoPositivesApart() {

		// POSKNOWN and POSNEW differ by when HIV was diagnosed relative to this STI - within three
		// months or longer ago - and POS is the answer when the status is known and the date is
		// not. Collapsing any two of the three would lose the distinction the variable exists for.
		assertThat(EpipulseStiHivStatusRef.codeFor(HivStatus.NEGATIVE), is("NEG"));
		assertThat(EpipulseStiHivStatusRef.codeFor(HivStatus.POSITIVE), is("POS"));
		assertThat(EpipulseStiHivStatusRef.codeFor(HivStatus.KNOWN_POSITIVE), is("POSKNOWN"));
		assertThat(EpipulseStiHivStatusRef.codeFor(HivStatus.NEW_DIAGNOSIS), is("POSNEW"));

		// the specification says to leave the variable empty when the status is not known, and this
		// value set has no OTH for it to be confused with
		assertThat(EpipulseStiHivStatusRef.codeFor(HivStatus.UNKNOWN), is((String) null));
		assertThat(EpipulseStiHivStatusRef.codeFor(null), is((String) null));
	}
}
