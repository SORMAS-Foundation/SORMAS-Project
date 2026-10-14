package de.symeda.sormas.backend.importexport.hierarchical.caze;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.importexport.ImportColumn;
import de.symeda.sormas.api.importexport.hierarchical.CaseTemplateLayout;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ColumnSpec;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportLevel;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportSchema;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PropertyPaths;

public final class CaseTemplateSchema {

	public static final String CASE = "case";
	public static final String PERSON = "person";
	public static final String SAMPLE = "sample";
	public static final String TEST = "test";

	public static final String CASE_REFERENCE_NUMBER = "CaseData." + CaseDataDto.CASE_REFERENCE_NUMBER;
	public static final String EPID_NUMBER = "CaseData." + CaseDataDto.EPID_NUMBER;
	public static final String EXTERNAL_TOKEN = "CaseData." + CaseDataDto.EXTERNAL_TOKEN;
	public static final String FIELD_SAMPLE_ID = "Sample." + SampleDto.FIELD_SAMPLE_ID;

	public static final ImportLevel CASE_LEVEL = level(CaseTemplateLayout.CASE);
	public static final ImportLevel SAMPLE_LEVEL = level(CaseTemplateLayout.SAMPLE);
	public static final ImportLevel TEST_LEVEL = level(CaseTemplateLayout.TEST);
	public static final ImportSchema SCHEMA = new ImportSchema(CASE_LEVEL, SAMPLE_LEVEL, TEST_LEVEL);

	private static final String PERSON_PREFIX = CaseDataDto.PERSON + ".";
	private static final Map<String, Class<?>> TARGET_TYPES =
		Map.of(CASE, CaseDataDto.class, PERSON, PersonDto.class, SAMPLE, SampleDto.class, TEST, PathogenTestDto.class);

	private CaseTemplateSchema() {
	}

	public static List<ColumnSpec> columns(List<ImportColumn> importColumns) {
		return importColumns.stream()
			.map(column -> column(column.getEntityName(), column.getColumnName(), column.getCaption()))
			.collect(Collectors.toList());
	}

	public static Optional<ColumnSpec> resolve(String header) {
		String entity = CaseTemplateLayout.entityOf(header);
		if (CaseTemplateLayout.levelOf(entity).isEmpty()) {
			return Optional.empty();
		}
		ColumnSpec column = column(entity, CaseTemplateLayout.propertyOf(header), header);
		return PropertyPaths.resolves(TARGET_TYPES.get(column.target()), column.propertyPath()) ? Optional.of(column) : Optional.empty();
	}

	private static ColumnSpec column(String entity, String columnName, String caption) {
		String header = CaseTemplateLayout.join(entity, columnName);
		String level = CaseTemplateLayout.levelOf(entity).orElse("");
		if (CaseTemplateLayout.CASE.equals(level) && columnName.startsWith(PERSON_PREFIX)) {
			return ColumnSpec.forTarget(header, caption, PERSON, columnName.substring(PERSON_PREFIX.length()).split("\\."));
		}
		String target = CaseTemplateLayout.CASE.equals(level) ? CASE : level;
		return ColumnSpec.forTarget(header, caption, target, columnName.split("\\."));
	}

	private static ImportLevel level(String name) {
		return new ImportLevel(name, ImportLevel.Kind.ENTITY, CaseTemplateLayout.entitiesOf(name).toArray(new String[0]));
	}
}
