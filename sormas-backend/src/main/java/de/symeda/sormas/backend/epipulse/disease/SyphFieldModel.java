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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseSiteOfInfectionRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStageSyphDetailedRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStageSyphRef;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectionSite;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectiousness;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisStage;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseCountryOfBirthSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseCountryOfNationalitySql;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePlaceOfInfectionSql;
import de.symeda.sormas.backend.epipulse.sql.SyphSql;

/**
 * SYPH (Acquired Syphilis) - filtered shared block (Age, not AgeMonth), two repeatable groups floored.
 * 
 * Six columns previously blank now derived from {@link StiRiskFields} (clinical service, transmission,
 * HIV status/prophylaxis, sex-work questions). Two remain blank by spec: ClinicalCriteriaStatus and
 * EpiLinked (EU case definition = lab-confirmed only, {see {@link ValueDef#blank}).
 */
final class SyphFieldModel {

	private SyphFieldModel() {
	}

	static FieldModel create() {
		return new FieldModel(
			fields(),
			SyphSql.spec()
				.plus(EpipulseCountryOfBirthSql.spec())
				.plus(EpipulseCountryOfNationalitySql.spec())
				.plus(EpipulsePlaceOfInfectionSql.spec(EpipulsePlaceOfInfectionSql.Rule.LOWEST_LEVEL)),
			0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = new ArrayList<>();

		CommonFields.addOperationalFields(fields);

		// The shared catalogue covers more than SYPH reports, so each column is kept only if
		// EpiPulse defines it for this subject code - the same filter DefaultFieldModel applies.
		for (ValueDef field : CommonFields.genericCsvDefs()) {
			if (EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.SYPH, field.getVariable())) {
				fields.add(field);
			}
		}

		CountryOfBirthFields.addCountryOfBirth(fields);
		CountryOfNationalityFields.addCountryOfNationality(fields);

		PlaceOfInfectionFields.addPlaceOfInfectionSource(fields);
		fields.add(ValueDef.repeated(EpipulseVariable.PLACE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.placeOfInfection(dto))));

		addSyphilisStageAndSite(fields);
		StiRiskFields.addStiRiskFields(fields, "syph_epidata", "syph_health");

		fields.add(ValueDef.calculated(EpipulseVariable.PATHOGEN_DETECTION_RESULT, EpipulseMapping::pathogenDetectionResult));

		addNotExpected(fields);

		return fields;
	}

	/**
	 * The three columns from the case's {@code symptoms} row.
	 *
	 * <p>
	 * {@code StageSYPH} and {@code StageSYPHdetailed} are read independently rather than one being
	 * derived from the other.
	 */
	private static void addSyphilisStageAndSite(List<ValueDef> fields) {

		fields.add(
			ValueDef.sqlOnly(
				"syphilis_infection_site",
				"symptom.syphilisinfectionsite",
				(dto, row) -> dto.setSiteOfInfection(siteOfInfection((String) row.get("syphilis_infection_site")))));
		fields.add(ValueDef.repeated(EpipulseVariable.SITE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.siteOfInfection(dto))));

		fields.add(
			ValueDef.direct(
				"syphilis_infectiousness",
				"symptom.syphilisinfectiousness",
				(dto, row) -> dto.setStageSyph(
					EpipulseStageSyphRef.codeFor(FieldReaders.parseEnum(SyphilisInfectiousness.class, (String) row.get("syphilis_infectiousness")))),
				EpipulseVariable.STAGE_SYPH,
				EpipulseMapping::stageSyph));

		fields.add(
			ValueDef.direct(
				"syphilis_stage",
				"symptom.syphilisstage",
				(dto, row) -> dto.setStageSyphDetailed(
					EpipulseStageSyphDetailedRef.codeFor(FieldReaders.parseEnum(SyphilisStage.class, (String) row.get("syphilis_stage")))),
				EpipulseVariable.STAGE_SYP_HDETAILED,
				EpipulseMapping::stageSyphDetailed));
	}

	/** A single recorded site, as the one-element list the repeatable group expects. */
	private static List<String> siteOfInfection(String recorded) {

		String code = EpipulseSiteOfInfectionRef.codeFor(FieldReaders.parseEnum(SyphilisInfectionSite.class, recorded));

		return code == null ? Collections.emptyList() : Collections.singletonList(code);
	}

	/**
	 * The two columns EpiPulse declares and does not expect an answer to.
	 */
	private static void addNotExpected(List<ValueDef> fields) {

		fields.add(ValueDef.blank(EpipulseVariable.CLINICAL_CRITERIA_STATUS));
		fields.add(ValueDef.blank(EpipulseVariable.EPI_LINKED));
	}
}
