package de.symeda.sormas.backend.importexport.hierarchical.lab;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ColumnSpec;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportLevel;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportSchema;

public final class LabImportSchema {

	public static final String CASE = "case";
	public static final String PERSON = "person";
	public static final String SAMPLE = "sample";
	public static final String TEST = "test";
	public static final String AST = "ast";

	public static final String CASE_UUID = "case.uuid";
	public static final String CASE_EXTERNAL_ID = CASE + "." + CaseDataDto.EXTERNAL_ID;
	public static final String SAMPLE_FIELD_SAMPLE_ID = SAMPLE + "." + SampleDto.FIELD_SAMPLE_ID;
	public static final String AST_DRUG = "ast.drug";
	public static final String AST_VALUE = "ast.value";
	public static final String AST_METHOD = "ast.method";
	public static final String AST_INTERPRETATION = "ast.interpretation";

	public static final ImportLevel CASE_LEVEL = new ImportLevel(CASE, ImportLevel.Kind.ENTITY, CASE, PERSON);
	public static final ImportLevel SAMPLE_LEVEL = new ImportLevel(SAMPLE, ImportLevel.Kind.ENTITY, SAMPLE);
	public static final ImportLevel TEST_LEVEL = new ImportLevel(TEST, ImportLevel.Kind.ENTITY, TEST);
	public static final ImportLevel AST_LEVEL = new ImportLevel(AST, ImportLevel.Kind.EMBEDDED, AST);
	public static final ImportSchema SCHEMA = new ImportSchema(CASE_LEVEL, SAMPLE_LEVEL, TEST_LEVEL, AST_LEVEL);

	private LabImportSchema() {
	}

	public static List<ColumnSpec> columns() {
		return List.of(
			new ColumnSpec(CASE_UUID, I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.UUID)),
			property(CASE, CaseDataDto.I18N_PREFIX, CaseDataDto.EXTERNAL_ID),
			property(CASE, CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE),
			property(CASE, CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE_DETAILS),
			property(CASE, CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE),
			property(CASE, CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_REGION),
			property(CASE, CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_DISTRICT),
			property(CASE, CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_COMMUNITY),
			property(CASE, CaseDataDto.I18N_PREFIX, CaseDataDto.FACILITY_TYPE),
			property(CASE, CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY),
			property(CASE, CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS),
			property(PERSON, PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME),
			property(PERSON, PersonDto.I18N_PREFIX, PersonDto.LAST_NAME),
			property(PERSON, PersonDto.I18N_PREFIX, PersonDto.SEX),
			property(PERSON, PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_DD),
			property(PERSON, PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_MM),
			property(PERSON, PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_YYYY),
			property(PERSON, PersonDto.I18N_PREFIX, PersonDto.NATIONAL_HEALTH_ID),
			property(SAMPLE, SampleDto.I18N_PREFIX, SampleDto.FIELD_SAMPLE_ID),
			property(SAMPLE, SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME),
			property(SAMPLE, SampleDto.I18N_PREFIX, SampleDto.SAMPLE_MATERIAL),
			property(SAMPLE, SampleDto.I18N_PREFIX, SampleDto.SAMPLE_MATERIAL_TEXT),
			property(SAMPLE, SampleDto.I18N_PREFIX, SampleDto.LAB),
			property(SAMPLE, SampleDto.I18N_PREFIX, SampleDto.LAB_DETAILS),
			property(SAMPLE, SampleDto.I18N_PREFIX, SampleDto.LAB_SAMPLE_ID),
			property(SAMPLE, SampleDto.I18N_PREFIX, SampleDto.COMMENT),
			property(TEST, PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_TYPE),
			property(TEST, PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_TYPE_TEXT),
			property(TEST, PathogenTestDto.I18N_PREFIX, PathogenTestDto.TESTED_DISEASE),
			property(TEST, PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_DATE_TIME),
			property(TEST, PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT),
			property(TEST, PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_VERIFIED),
			property(TEST, PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT_TEXT),
			property(TEST, PathogenTestDto.I18N_PREFIX, PathogenTestDto.LAB),
			property(TEST, PathogenTestDto.I18N_PREFIX, PathogenTestDto.LAB_DETAILS),
			new ColumnSpec(AST_DRUG, I18nProperties.getCaption(Captions.hierarchicalImportAstDrug)),
			new ColumnSpec(AST_VALUE, I18nProperties.getCaption(Captions.hierarchicalImportAstValue)),
			new ColumnSpec(AST_METHOD, I18nProperties.getCaption(Captions.hierarchicalImportAstMethod)),
			new ColumnSpec(AST_INTERPRETATION, I18nProperties.getCaption(Captions.hierarchicalImportAstInterpretation)));
	}

	private static ColumnSpec property(String target, String i18nPrefix, String property) {
		return new ColumnSpec(target + "." + property, I18nProperties.getPrefixCaption(i18nPrefix, property), property);
	}
}
