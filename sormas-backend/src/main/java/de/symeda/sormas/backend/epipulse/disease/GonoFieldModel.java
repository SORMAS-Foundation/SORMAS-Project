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
import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseCountryOfBirthSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseCountryOfNationalitySql;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePlaceOfInfectionSql;
import de.symeda.sormas.backend.epipulse.sql.GonoSql;

/**
 * Field model for GONO.
 *
 * <p>
 * Uses metadata-filtered shared fields plus STI risk/service mappings from
 * {@link StiRiskFields}. {@code ClinicalCriteriaStatus} and {@code EpiLinked} remain declared but
 * intentionally blank.
 */
final class GonoFieldModel {

	private GonoFieldModel() {
	}

	/**
	 * Builds GONO fields with disease and shared SQL fragments.
	 */
	static FieldModel create() {

		return new FieldModel(
			fields(),
			GonoSql.spec()
				.plus(EpipulseCountryOfBirthSql.spec())
				.plus(EpipulseCountryOfNationalitySql.spec())
				.plus(EpipulsePlaceOfInfectionSql.spec(EpipulsePlaceOfInfectionSql.Rule.LOWEST_LEVEL)),
			0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = new ArrayList<>();

		CommonFields.addOperationalFields(fields);

		// The shared catalogue covers more than GONO reports, so each column is kept only if
		// EpiPulse defines it for this subject code - the same filter DefaultFieldModel applies.
		for (ValueDef field : CommonFields.genericCsvDefs()) {
			if (EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.GONO, field.getVariable())) {
				fields.add(field);
			}
		}

		CountryOfBirthFields.addCountryOfBirth(fields);
		CountryOfNationalityFields.addCountryOfNationality(fields);

		PlaceOfInfectionFields.addPlaceOfInfectionSource(fields);
		fields.add(ValueDef.repeated(EpipulseVariable.PLACE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.placeOfInfection(dto))));

		fields.add(ValueDef.calculated(EpipulseVariable.PATHOGEN_DETECTION_RESULT, EpipulseMapping::pathogenDetectionResult));

		addSiteOfInfection(fields);
		StiRiskFields.addStiRiskFields(fields, "gono_epidata", "gono_health");
		addNotExpected(fields);

		return fields;
	}

	/**
	 * Adds {@code SiteOfInfection} from symptom columns collapsed to EpiPulse codes.
	 */
	private static void addSiteOfInfection(List<ValueDef> fields) {

		fields.add(FieldReaders.addressable("site_anorectal", "symptom.gonococcalinfectionsiteanorectal"));
		fields.add(FieldReaders.addressable("site_genital", "symptom.gonococcalinfectionsitegenital"));
		fields.add(FieldReaders.addressable("site_pharyngeal", "symptom.gonococcalinfectionsitepharyngeal"));
		fields.add(FieldReaders.addressable("site_blood", "symptom.gonococcalinfectionsiteblood"));
		fields.add(FieldReaders.addressable("site_csf", "symptom.gonococcalinfectionsitecerebrospinalfluid"));
		fields.add(FieldReaders.addressable("site_eye", "symptom.gonococcalinfectionsiteeye"));
		fields.add(FieldReaders.addressable("site_joint_fluid", "symptom.gonococcalinfectionsitejointfluid"));
		fields
			.add(ValueDef.sqlOnly("site_other", "symptom.gonococcalinfectionsiteother", (dto, row) -> dto.setSiteOfInfection(siteOfInfection(row))));

		fields.add(ValueDef.repeated(EpipulseVariable.SITE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.siteOfInfection(dto))));
	}

	/**
	 * The two columns EpiPulse declares and does not expect an answer to.
	 */
	private static void addNotExpected(List<ValueDef> fields) {

		fields.add(ValueDef.blank(EpipulseVariable.CLINICAL_CRITERIA_STATUS));
		fields.add(ValueDef.blank(EpipulseVariable.EPI_LINKED));
	}

	/**
	 * Maps recorded infection sites to EpiPulse codes, deduplicating {@code OTH}.
	 */
	private static List<String> siteOfInfection(EpipulseRow row) {

		List<String> sites = new ArrayList<>();

		if (recorded(row, "site_anorectal")) {
			sites.add("AR");
		}
		if (recorded(row, "site_genital")) {
			sites.add("GEN");
		}
		for (String otherSite : Arrays.asList("site_blood", "site_csf", "site_eye", "site_joint_fluid", "site_other")) {
			if (recorded(row, otherSite)) {
				sites.add("OTH");
				break;
			}
		}
		if (recorded(row, "site_pharyngeal")) {
			sites.add("PH");
		}

		return sites;
	}

	/** Whether this symptom column says the site was found, as opposed to anything else. */
	private static boolean recorded(EpipulseRow row, String alias) {
		return FieldReaders.parseSymptomState((String) row.get(alias)) == SymptomState.YES;
	}
}
