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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import de.symeda.sormas.api.clinicalcourse.HivStatus;
import de.symeda.sormas.api.epidata.ProbableRouteOfTransmission;
import de.symeda.sormas.api.epidata.TypeOfClinicalService;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseClinicalServiceTypeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStiHivStatusRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStiModeOfTransmissionRef;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;

/**
 * Shared STI risk/service field block.
 *
 * <p>
 * Centralizes the seven STI columns used by SYPH and GONO. Callers provide the
 * {@code epidata}/{@code healthconditions} aliases.
 */
final class StiRiskFields {

	private StiRiskFields() {
	}

	/**
	 * Adds the seven STI risk/service columns in export order.
	 *
	 * @param epiDataAlias
	 *            alias used for joined {@code epidata}
	 * @param healthConditionsAlias
	 *            alias used for joined {@code healthconditions}
	 */
	static void addStiRiskFields(List<ValueDef> fields, String epiDataAlias, String healthConditionsAlias) {

		fields.add(
			ValueDef.direct(
				"type_of_clinical_service",
				epiDataAlias + ".typeofclinicalservice",
				StiRiskFields::readClinicalServiceType,
				EpipulseVariable.CLINICAL_SERVICE_TYPE,
				EpipulseMapping::clinicalServiceType));

		fields.add(
			ValueDef.direct(
				"probable_route_of_transmission",
				epiDataAlias + ".probablerouteoftransmission",
				StiRiskFields::readModeOfTransmission,
				EpipulseVariable.MODE_OF_TRANSMISSION,
				EpipulseMapping::modeOfTransmission));

		fields.add(
			ValueDef.direct(
				"hiv_status",
				healthConditionsAlias + ".hivstatus",
				StiRiskFields::readHivStatus,
				EpipulseVariable.HIV_STATUS,
				EpipulseMapping::hivStatus));

		addYesNoUnknown(
			fields,
			"hiv_prep",
			healthConditionsAlias + ".hivprep",
			EpipulseVariable.HIV_PR_EP,
			EpipulseMapping::hivPrEP,
			EpipulseDiseaseExportEntryDto::setHivPrEP);

		addYesNoUnknown(
			fields,
			"sti_prophylaxis",
			healthConditionsAlias + ".stiprophylaxis",
			EpipulseVariable.ANTIBIOTIC_PROPHYLAXIS,
			EpipulseMapping::antibioticProphylaxis,
			EpipulseDiseaseExportEntryDto::setAntibioticProphylaxis);

		addYesNoUnknown(
			fields,
			"sex_worker",
			epiDataAlias + ".sexworker",
			EpipulseVariable.SEX_WORKER,
			EpipulseMapping::sexWorker,
			EpipulseDiseaseExportEntryDto::setSexWorker);

		addYesNoUnknown(
			fields,
			"contact_with_sex_worker",
			epiDataAlias + ".contactwithsexworker",
			EpipulseVariable.CONTACT_SW,
			EpipulseMapping::contactSW,
			EpipulseDiseaseExportEntryDto::setContactSW);
	}

	/**
	 * Adds one BOOL-like field mapped from {@link YesNoUnknown}; {@code UNKNOWN} stays blank.
	 */
	private static void addYesNoUnknown(
		List<ValueDef> fields,
		String alias,
		String selectExpression,
		EpipulseVariable variable,
		Function<EpipulseDiseaseExportEntryDto, String> csvValue,
		BiConsumer<EpipulseDiseaseExportEntryDto, Boolean> setter) {

		fields.add(ValueDef.direct(alias, selectExpression, (dto, row) -> {

			YesNoUnknown answer = FieldReaders.parseEnum(YesNoUnknown.class, (String) row.get(alias));

			if (answer != null && answer != YesNoUnknown.UNKNOWN) {
				setter.accept(dto, answer == YesNoUnknown.YES);
			}
		}, variable, csvValue));
	}

	private static void readClinicalServiceType(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		TypeOfClinicalService service = FieldReaders.parseEnum(TypeOfClinicalService.class, (String) row.get("type_of_clinical_service"));

		dto.setClinicalServiceType(EpipulseClinicalServiceTypeRef.codeFor(service));
	}

	private static void readModeOfTransmission(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		ProbableRouteOfTransmission route =
			FieldReaders.parseEnum(ProbableRouteOfTransmission.class, (String) row.get("probable_route_of_transmission"));

		dto.setModeOfTransmission(EpipulseStiModeOfTransmissionRef.codeFor(route));
	}

	private static void readHivStatus(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		HivStatus hivStatus = FieldReaders.parseEnum(HivStatus.class, (String) row.get("hiv_status"));

		dto.setHivStatus(EpipulseStiHivStatusRef.codeFor(hivStatus));
	}
}
