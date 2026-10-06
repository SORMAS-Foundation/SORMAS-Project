package de.symeda.sormas.backend.importexport.hierarchical.lab;

import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.AST_DRUG;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.AST_INTERPRETATION;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.AST_LEVEL;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.AST_METHOD;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.AST_VALUE;
import static de.symeda.sormas.backend.importexport.hierarchical.lab.LabImportSchema.TEST;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.FieldChangeDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.therapy.Drug;
import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;
import de.symeda.sormas.api.utils.AnnotationFieldHelper;
import de.symeda.sormas.backend.importexport.hierarchical.engine.CellWriteException;
import de.symeda.sormas.backend.importexport.hierarchical.engine.CellWriter;
import de.symeda.sormas.backend.importexport.hierarchical.engine.FieldDiff;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PlannedRecord;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PropertyPaths;

final class AstFolder {

	private static final String DRUG_SUSCEPTIBILITY = PathogenTestDto.DRUG_SUSCEPTIBILITY;

	private final CellWriter cellWriter;

	AstFolder(CellWriter cellWriter) {
		this.cellWriter = cellWriter;
	}

	void fold(PlannedRecord testRecord) {
		List<ImportNode> astRows = testRecord.node().children(AST_LEVEL);
		if (astRows.isEmpty()) {
			return;
		}
		PathogenTestDto test = testRecord.target(TEST);
		if (test.getDrugSusceptibility() == null) {
			test.setDrugSusceptibility(DrugSusceptibilityDto.build());
		}
		List<String> applicableFields =
			AnnotationFieldHelper.getFieldNamesWithMatchingDiseaseAndTestAnnotations(DrugSusceptibilityDto.class, test.getTestedDisease(), test.getTestType());
		if (applicableFields.isEmpty()) {
			testRecord.reportIssue(AST_DRUG, I18nProperties.getValidationError(Validations.importHierarchicalAstNotForTestType, test.getTestType()));
			return;
		}
		Set<Drug> seen = new HashSet<>();
		for (ImportNode row : astRows) {
			Optional<Drug> drug = drug(row.cell(AST_DRUG));
			if (drug.isEmpty()) {
				row.reportIssue(AST_DRUG, I18nProperties.getValidationError(Validations.importHierarchicalUnknownDrug, row.cell(AST_DRUG)));
				continue;
			}
			if (!seen.add(drug.get())) {
				row.reportIssue(AST_DRUG, I18nProperties.getValidationError(Validations.importHierarchicalDuplicateDrug, drug.get()));
				continue;
			}
			String prefix = fieldPrefix(drug.get());
			if (!applicableFields.contains(prefix + "Susceptibility")) {
				row.reportIssue(
					AST_DRUG,
					I18nProperties.getValidationError(Validations.importHierarchicalDrugNotApplicable, drug.get(), test.getTestedDisease()));
				continue;
			}
			write(testRecord, row, drug.get(), AST_VALUE, prefix + "Mic");
			write(testRecord, row, drug.get(), AST_METHOD, prefix + "Method");
			write(testRecord, row, drug.get(), AST_INTERPRETATION, prefix + "Susceptibility");
		}
	}

	private void write(PlannedRecord testRecord, ImportNode row, Drug drug, String header, String field) {
		String value = row.cell(header);
		if (value.isEmpty()) {
			return;
		}
		String[] path = {
			DRUG_SUSCEPTIBILITY,
			field };
		try {
			cellWriter.write(testRecord.targets(), TEST, path, header, value);
		} catch (CellWriteException e) {
			row.reportIssue(header, e.getMessage());
			return;
		}
		if (!testRecord.isNew()) {
			Object before = PropertyPaths.read(testRecord.originalTarget(TEST), path);
			Object after = PropertyPaths.read(testRecord.target(TEST), path);
			if (!FieldDiff.sameValue(before, after)) {
				testRecord.addChange(new FieldChangeDto(drug + " " + header, FieldDiff.display(before), FieldDiff.display(after)));
			}
		}
	}

	private static Optional<Drug> drug(String value) {
		return Arrays.stream(Drug.values())
			.filter(drug -> drug.name().equalsIgnoreCase(value) || drug.toString().equalsIgnoreCase(value))
			.findFirst();
	}

	static String fieldPrefix(Drug drug) {
		String[] words = drug.name().toLowerCase().split("_");
		StringBuilder prefix = new StringBuilder(words[0]);
		for (int i = 1; i < words.length; i++) {
			prefix.append(StringUtils.capitalize(words[i]));
		}
		return prefix.toString();
	}
}
