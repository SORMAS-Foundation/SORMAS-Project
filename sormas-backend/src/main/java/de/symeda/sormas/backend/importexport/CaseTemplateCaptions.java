package de.symeda.sormas.backend.importexport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportColumn;
import de.symeda.sormas.api.person.PersonDto;

final class CaseTemplateCaptions {

	private static final String CASE_ENTITY = "CaseData";
	private static final String REQUIRED_MARKER = " *";
	private static final Set<String> PLACE_OF_STAY_COLUMNS = new HashSet<>(
		Arrays.asList(
			CaseDataDto.REGION,
			CaseDataDto.DISTRICT,
			CaseDataDto.COMMUNITY,
			CaseDataDto.FACILITY_TYPE,
			CaseDataDto.HEALTH_FACILITY,
			CaseDataDto.HEALTH_FACILITY_DETAILS));
	private static final Set<String> REQUIRED_COLUMNS = new HashSet<>(
		Arrays.asList(
			CaseDataDto.DISEASE,
			CaseDataDto.PERSON + "." + PersonDto.FIRST_NAME,
			CaseDataDto.PERSON + "." + PersonDto.LAST_NAME,
			CaseDataDto.RESPONSIBLE_REGION,
			CaseDataDto.RESPONSIBLE_DISTRICT));

	private CaseTemplateCaptions() {
	}

	static List<ImportColumn> apply(List<ImportColumn> columns) {
		List<String> grouped = new ArrayList<>();
		String rootEntity = CASE_ENTITY;
		Map<String, Integer> captionCounts = countCaptions(columns.stream().map(ImportColumn::getCaption).collect(Collectors.toList()));
		for (ImportColumn column : columns) {
			if (!column.getColumnName().contains(".")) {
				rootEntity = column.getEntityName();
			}
			grouped.add(
				captionCounts.get(column.getCaption()) > 1
					? groupOf(column, rootEntity, columns) + " - " + column.getCaption()
					: column.getCaption());
		}

		Map<String, Integer> groupedCounts = countCaptions(grouped);
		List<ImportColumn> result = new ArrayList<>();
		for (int index = 0; index < columns.size(); index++) {
			ImportColumn column = columns.get(index);
			String caption = grouped.get(index);
			if (groupedCounts.get(caption) > 1) {
				caption += " (" + leafOf(column) + ")";
			}
			if (REQUIRED_COLUMNS.contains(column.getColumnName())) {
				caption += REQUIRED_MARKER;
			}
			result.add(new ImportColumn(column.getEntityName(), column.getColumnName(), caption, describe(column)));
		}
		return result;
	}

	private static Map<String, Integer> countCaptions(List<String> captions) {
		Map<String, Integer> counts = new HashMap<>();
		captions.forEach(caption -> counts.merge(caption, 1, Integer::sum));
		return counts;
	}

	private static String leafOf(ImportColumn column) {
		String[] parts = column.getColumnName().split("\\.");
		return parts[parts.length - 1];
	}

	private static String describe(ImportColumn column) {
		String description = column.getDataDescription();
		if (CaseDataDto.HEALTH_FACILITY.equals(column.getColumnName())) {
			return description + " " + I18nProperties.getString(Strings.importTemplateHealthFacilityRequired);
		}
		if (CaseDataDto.POINT_OF_ENTRY.equals(column.getColumnName())) {
			return description + " " + I18nProperties.getString(Strings.importTemplatePointOfEntryRequired);
		}
		return description;
	}

	private static String groupOf(ImportColumn column, String rootEntity, List<ImportColumn> columns) {
		String[] parts = column.getColumnName().split("\\.");
		if (parts.length == 1) {
			return CASE_ENTITY.equals(column.getEntityName()) && PLACE_OF_STAY_COLUMNS.contains(parts[0])
				? I18nProperties.getCaption(Captions.casePlaceOfStay)
				: I18nProperties.getPrefixCaption(column.getEntityName(), column.getEntityName());
		}

		String parentField = parts[parts.length - 2];
		String parentEntity = parts.length == 2 ? rootEntity : entityOfSiblings(columns, parts[0] + ".", column);
		return I18nProperties.getPrefixCaption(parentEntity, parentField);
	}

	private static String entityOfSiblings(List<ImportColumn> columns, String prefix, ImportColumn column) {
		return columns.stream()
			.filter(candidate -> candidate.getColumnName().startsWith(prefix) && candidate.getColumnName().indexOf('.', prefix.length()) < 0)
			.map(ImportColumn::getEntityName)
			.findFirst()
			.orElse(column.getEntityName());
	}
}
