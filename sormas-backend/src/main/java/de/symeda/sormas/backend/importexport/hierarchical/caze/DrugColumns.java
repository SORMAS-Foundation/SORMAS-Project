package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static de.symeda.sormas.backend.importexport.hierarchical.caze.CaseTemplateSchema.TEST;

import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.utils.AnnotationFieldHelper;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;

final class DrugColumns {

	private static final String PREFIX = "DrugSusceptibility." + PathogenTestDto.DRUG_SUSCEPTIBILITY + ".";
	private static final List<String> SUFFIXES = List.of("Susceptibility", "Method", "Mic");

	private DrugColumns() {
	}

	static void reportInapplicable(PlannedRecord testRecord) {
		List<String> filled = testRecord.node().cells().keySet().stream().filter(header -> header.startsWith(PREFIX)).collect(Collectors.toList());
		if (filled.isEmpty()) {
			return;
		}
		PathogenTestDto test = testRecord.target(TEST);
		List<String> applicable =
			AnnotationFieldHelper.getFieldNamesWithMatchingDiseaseAndTestAnnotations(DrugSusceptibilityDto.class, test.getTestedDisease(), test.getTestType());
		if (applicable.isEmpty()) {
			testRecord.reportIssue(filled.get(0), I18nProperties.getValidationError(Validations.importHierarchicalAstNotForTestType, test.getTestType()));
			return;
		}
		for (String header : filled) {
			String drug = drugOf(header.substring(PREFIX.length()));
			if (!applicable.contains(drug + "Susceptibility")) {
				testRecord
					.reportIssue(header, I18nProperties.getValidationError(Validations.importHierarchicalDrugNotApplicable, drug, test.getTestedDisease()));
			}
		}
	}

	private static String drugOf(String field) {
		return SUFFIXES.stream().filter(field::endsWith).findFirst().map(suffix -> field.substring(0, field.length() - suffix.length())).orElse(field);
	}
}
