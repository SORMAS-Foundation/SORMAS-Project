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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.epidata.CaseImportedStatus;
import de.symeda.sormas.api.epidata.ClusterType;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseClusterSettingRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseImportedStatusRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseSuspectedVehicleRef;
import de.symeda.sormas.api.exposure.InfectionSource;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.epidata.InfectionSourceSetConverter;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseAggregateFormat;

/**
 * Helpers shared by the disease field models for turning a raw column value into something the DTO
 * can hold.
 * 
 */
final class FieldReaders {

	// Separators and date format come from the SQL side that writes the aggregates.
	private static final Pattern COLLECTION_SPLIT = Pattern.compile(Pattern.quote(EpipulseAggregateFormat.COLLECTION_SEPARATOR));
	private static final Pattern RECORD_SPLIT = Pattern.compile(Pattern.quote(EpipulseAggregateFormat.RECORD_SEPARATOR));

	private FieldReaders() {
	}

	/**
	 * A reader that does nothing, for a column selected only so that another field's reader can
	 * consult it by alias.
	 */
	static BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> noop() {
		return (dto, row) -> {
		};
	}

	/**
	 * The {@code valueOf}, falling back to {@code null} on a blank or unrecognised value
	 */
	static <E extends Enum<E>> E parseEnum(Class<E> type, String value) {

		if (StringUtils.isBlank(value)) {
			return null;
		}

		try {
			return Enum.valueOf(type, value);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * As {@link #parseEnum}, plus a fallback reading the value as a persisted ordinal.
	 */
	static SymptomState parseSymptomState(String value) {

		if (StringUtils.isBlank(value)) {
			return null;
		}

		try {
			return SymptomState.valueOf(value);
		} catch (IllegalArgumentException e) {
			switch (value) {
			case "0":
				return SymptomState.YES;
			case "1":
				return SymptomState.NO;
			case "2":
				return SymptomState.UNKNOWN;
			default:
				return null;
			}
		}
	}

	/**
	 * Splits an aggregate into its trimmed, non-blank elements. A blank aggregate
	 * gives an empty list.
	 */
	static List<String> splitCollection(String aggregated) {

		List<String> elements = new ArrayList<>();
		if (StringUtils.isBlank(aggregated)) {
			return elements;
		}

		for (String element : COLLECTION_SPLIT.split(aggregated)) {
			if (StringUtils.isNotBlank(element)) {
				elements.add(element.trim());
			}
		}

		return elements;
	}

	/**
	 * Unflattens an aggregate of records into one value per record. Trailing empty
	 * fields are kept, so a record whose last values are missing still has all its fields.
	 *
	 * <p>
	 * A record with fewer than {@code minFields} fields is skipped, as is one {@code mapper} returns
	 * {@code null} for: one malformed record must not fail the export. Field order is a contract
	 * with the CTE that writes the aggregate only.
	 */
	static <T> List<T> parseRecords(String aggregated, int minFields, Function<String[], T> mapper) {

		List<T> records = new ArrayList<>();
		if (StringUtils.isBlank(aggregated)) {
			return records;
		}

		for (String record : COLLECTION_SPLIT.split(aggregated)) {
			String[] values = RECORD_SPLIT.split(record, -1);
			if (values.length < minFields) {
				continue;
			}
			T parsed = mapper.apply(values);
			if (parsed != null) {
				records.add(parsed);
			}
		}

		return records;
	}

	/**
	 * A formatter for the dates the aggregates write. One per parse: {@link SimpleDateFormat} is not
	 * thread-safe.
	 */
	static SimpleDateFormat dbDateFormat() {
		return new SimpleDateFormat(EpipulseAggregateFormat.DATE_PATTERN);
	}

	/**
	 * Maps a sample material aggregate to EpiPulse specimen codes, or
	 * {@code null} for a blank aggregate. Unmapped materials report nothing.
	 *
	 * <p>
	 * Several SORMAS materials share one EpiPulse code, so the aggregate's {@code DISTINCT} does not
	 * make the mapped list distinct - a case with a throat swab and a rectal swab would otherwise
	 * report {@code OTH} twice.
	 */
	static List<String> specimenCodes(String aggregated) {

		if (StringUtils.isBlank(aggregated)) {
			return null;
		}

		List<String> codes = new ArrayList<>();
		for (String materialName : splitCollection(aggregated)) {
			SampleMaterial material = parseEnum(SampleMaterial.class, materialName);
			String code = EpipulseLaboratoryMapper.mapSampleMaterialToEpipulseCode(material);
			if (code != null && !codes.contains(code)) {
				codes.add(code);
			}
		}

		return codes;
	}

	/**
	 * {@code ClinicalCriteriaStatus} - "the clinical criteria are met", which SORMAS answers with
	 * {@code clinicalconfirmation}, read from {@code alias}.
	 *
	 * <p>
	 * {@code UNKNOWN} reports nothing rather than {@code false}. EpiPulse types the variable as a
	 * BOOL with no third state, and "we could not establish the clinical picture" is not the same
	 * claim as "the criteria are not met".
	 */
	static BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> clinicalCriteriaStatus(String alias) {
		return (dto, row) -> {
			Boolean met = parseYesNo((String) row.get(alias));
			if (met != null) {
				dto.setClinicalCriteriaStatus(met);
			}
		};
	}

	/**
	 * {@code Imported} - an EpiPulse BOOL from the SORMAS {@link YesNoUnknown} read from
	 * {@code alias}. {@code UNKNOWN} reports nothing, for the same reason as
	 * {@link #clinicalCriteriaStatus}.
	 */
	static BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> imported(String alias) {
		return (dto, row) -> {
			Boolean imported = parseYesNo((String) row.get(alias));
			if (imported != null) {
				dto.setImported(imported);
			}
		};
	}

	/**
	 * {@code ImportedStatus} - the {@link CaseImportedStatus} read from {@code alias}, as the code
	 * {@link EpipulseImportedStatusRef} maps it to.
	 */
	static BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> importedStatus(String alias) {
		return (dto, row) -> {
			CaseImportedStatus importedStatus = parseEnum(CaseImportedStatus.class, (String) row.get(alias));
			if (importedStatus != null) {
				dto.setImportedStatus(EpipulseImportedStatusRef.codeFor(importedStatus));
			}
		};
	}

	/**
	 * {@code ClusterSetting} - the {@link ClusterType} read from {@code alias}. SORMAS records one,
	 * so the group carries at most one value.
	 *
	 * <p>
	 * The null check matters: {@code UNKNOWN} and {@code NOT_APPLICABLE} map to no code (not
	 * {@code OTH}), and skipping them keeps a {@code null} out of the list.
	 */
	static BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> clusterSetting(String alias) {
		return (dto, row) -> {
			ClusterType clusterType = parseEnum(ClusterType.class, (String) row.get(alias));
			String code = EpipulseClusterSettingRef.codeFor(clusterType);
			if (code != null) {
				List<String> clusterSettings = new ArrayList<>();
				clusterSettings.add(code);
				dto.setClusterSetting(clusterSettings);
			}
		};
	}

	/**
	 * {@code SuspectedVehicle} - the EpiPulse vehicle for the infection sources read from
	 * {@code alias}, a column {@link InfectionSourceSetConverter} writes.
	 *
	 * <p>
	 * The variable takes one value while the field records a set, so it is reported only when the
	 * recorded sources resolve to exactly one code. Sources without a code (the coarse {@code FOOD}
	 * and {@code ANIMAL}, {@code OTHER}, {@code UNKNOWN}, {@code NOT_APPLICABLE}) do not count
	 * against it; two different vehicles report nothing rather than one picked arbitrarily.
	 */
	static BiConsumer<EpipulseDiseaseExportEntryDto, EpipulseRow> suspectedVehicle(String alias) {
		return (dto, row) -> {
			String sourceNames = (String) row.get(alias);
			if (StringUtils.isBlank(sourceNames)) {
				return;
			}

			Set<String> codes = new HashSet<>();
			for (String sourceName : StringUtils.split(sourceNames, InfectionSourceSetConverter.SEPARATOR)) {
				String code = EpipulseSuspectedVehicleRef.codeFor(parseEnum(InfectionSource.class, sourceName.trim()));
				if (code != null) {
					codes.add(code);
				}
			}

			if (codes.size() == 1) {
				dto.setSuspectedVehicle(codes.iterator().next());
			}
		};
	}

	/** {@code YES}/{@code NO} as a boolean; {@code UNKNOWN}, blank or unrecognised as {@code null}. */
	private static Boolean parseYesNo(String value) {

		YesNoUnknown yesNoUnknown = parseEnum(YesNoUnknown.class, value);
		if (yesNoUnknown == null || yesNoUnknown == YesNoUnknown.UNKNOWN) {
			return null;
		}

		return yesNoUnknown == YesNoUnknown.YES;
	}

	/**
	 * Reads a column as a {@link ValueDef#sqlOnly} definition that only makes the value addressable.
	 * Shorthand for {@code ValueDef.sqlOnly(alias, expression, noop())}.
	 */
	static ValueDef addressable(String alias, String selectExpression) {
		return ValueDef.sqlOnly(alias, selectExpression, noop());
	}
}
