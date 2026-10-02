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

package de.symeda.sormas.api.epipulse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import de.symeda.sormas.api.utils.CSVUtils;

/**
 * Generates the Java classes that transcribe the EpiPulse cases metadata.
 *
 * <p>
 * Both outputs are transcriptions of one spreadsheet, so both are generated: a hand-edit to either
 * is a silent divergence from the specification SORMAS reports against, and nothing downstream can
 * tell the difference between a column name that came from the metadata and one somebody typed.
 *
 * <ul>
 * <li>{@link EpipulseVariable} - every variable EpiPulse defines, and its exact column name
 * <li>{@link EpipulseSubjectCodeVariables} - which of them each subject code defines
 * </ul>
 *
 * <p>
 * Run it from the {@code sormas-api} module directory, the same way {@code I18nConstantGenerator}
 * is run, after replacing {@value #METADATA_CSV_PATH} with a fresh export. It rewrites both files
 * in full; {@link EpipulseVariablesUpdatedTest} fails the build when they no longer match the CSV,
 * so a forgotten rerun does not reach a release.
 *
 * @see EpipulseVariablesUpdatedTest
 * @see de.symeda.sormas.api.i18n.I18nConstantGenerator
 */
public abstract class EpipulseVariableGenerator {

	/**
	 * Where the metadata comes from. Named in the header of every generated file so that a reader
	 * who wants to check a column against the specification knows which specification that is.
	 */
	private static final String METADATA_SOURCE_URL = "https://www.ecdc.europa.eu/en/publications-data/epipulse-cases-metadata";

	/**
	 * The export of {@link #METADATA_SOURCE_URL} the generated files currently transcribe, read from
	 * the classpath. Replace it with a fresh export and rerun to take up a new revision of the
	 * metadata.
	 */
	private static final String METADATA_CSV_RESOURCE = "/epipulse/EpiPulseCasesMetadata_2026-09-17_subset.csv";

	/**
	 * Where {@link #METADATA_CSV_RESOURCE} sits in the source tree, relative to the repository root.
	 * Used for messages and for the header of every generated file, which needs a path a reader can
	 * open rather than a classpath entry.
	 */
	private static final String METADATA_CSV_PATH = "sormas-api/src/main/resources" + METADATA_CSV_RESOURCE;

	private static final String FILE_PATH_PATTERN = "src/main/java/de/symeda/sormas/api/epipulse/%s.java";

	/**
	 * Written into the {@code @Generated} annotation of each output. A literal rather than
	 * {@code getClass()}, which on the nested generators below would name the subclass.
	 */
	private static final String GENERATOR_NAME = "de.symeda.sormas.api.epipulse.EpipulseVariableGenerator";

	/**
	 * The subject codes read from the metadata: those reported as one record per case of one
	 * disease.
	 *
	 * <p>
	 * A code listed here with no rows in the export is an error rather than an empty entry, so a
	 * typo and a metadata revision that drops a disease both stop the generator instead of quietly
	 * shrinking the enum.
	 */
	private static final Set<String> CASE_BASED_SUBJECT_CODES = Collections.unmodifiableSet(
		new TreeSet<>(
			Arrays.asList(
				"BRUC",
				"CAMP",
				"CCHF",
				"CHIK",
				"CHLAM",
				"CONSYPH",
				"DENGUE",
				"DIPH",
				"ECHI",
				"GONO",
				"HAEINF",
				"HEPA",
				"HEPB",
				"HEPC",
				"HIVAIDS",
				"LEGI",
				"LGV",
				"LIST",
				"LYMENEURO",
				"MALA",
				"MEAS",
				"MENI",
				"MPOX",
				"MUMP",
				"PERT",
				"PNEU",
				"POLI",
				"RUBE",
				"SALM",
				"SHIG",
				"STEC",
				"SYPH",
				"TBE",
				"TETA",
				"TOXO",
				"TRIC",
				"TUBE",
				"VCJD",
				"WNF",
				"ZIKA")));

	private static final String COLUMN_SUBJECT_CODE = "Subject code";
	private static final String COLUMN_VARIABLE = "Variable";
	private static final String COLUMN_VARIABLE_NAME = "Variable name";

	/**
	 * The columns whose value the metadata may give differently per subject code. Counted rather
	 * than transcribed: see the class javadoc of {@link EpipulseVariable}.
	 */
	private static final List<String> PER_SUBJECT_CODE_COLUMNS = Arrays.asList("Required", "Repeatable", "Variable type");

	private final String outputClassName;
	private final String outputClassFilePath;

	private EpipulseVariableGenerator(String outputClassName) {

		this.outputClassName = outputClassName;
		this.outputClassFilePath = String.format(FILE_PATH_PATTERN, outputClassName);
	}

	/**
	 * @return Class name of the generated file.
	 */
	public String getOutputClassName() {
		return outputClassName;
	}

	/**
	 * @return Path to the generated file, relative to the module directory.
	 */
	public String getOutputClassFilePath() {
		return outputClassFilePath;
	}

	/**
	 * Writes the complete generated file, so that what the metadata says is the whole content of it
	 * and there is no hand-maintained region to preserve.
	 */
	abstract void writeClass(Writer writer, String sep, EpipulseMetadata metadata) throws IOException;

	private void generateClass(EpipulseMetadata metadata) throws IOException {

		Path path = Paths.get(outputClassFilePath);
		String sep = determineLineSeparator(path);

		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeClass(writer, sep, metadata);
		}
	}

	/**
	 * Try to determine line separator from file
	 */
	private static String determineLineSeparator(Path path) {

		if (Files.exists(path)) {
			try (Scanner s = new Scanner(path.toFile())) {
				String sep = s.findWithinHorizon("\\R", 0);
				if (StringUtils.isNoneEmpty(sep)) {
					return sep;
				}
			} catch (FileNotFoundException e) {
				throw new UncheckedIOException(e);
			}
		}
		return System.lineSeparator();
	}

	private static void writeFileHeader(Writer writer, String sep) throws IOException {

		writer.append("/*******************************************************************************").append(sep);
		writer.append(" * SORMAS® - Surveillance Outbreak Response Management & Analysis System").append(sep);
		writer.append(" * Copyright © 2016-2026 SORMAS Foundation gGmbH").append(sep);
		writer.append(" *").append(sep);
		writer.append(" * This program is free software: you can redistribute it and/or modify").append(sep);
		writer.append(" * it under the terms of the GNU General Public License as published by").append(sep);
		writer.append(" * the Free Software Foundation, either version 3 of the License, or").append(sep);
		writer.append(" * (at your option) any later version.").append(sep);
		writer.append(" *").append(sep);
		writer.append(" * This program is distributed in the hope that it will be useful,").append(sep);
		writer.append(" * but WITHOUT ANY WARRANTY; without even the implied warranty of").append(sep);
		writer.append(" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the").append(sep);
		writer.append(" * GNU General Public License for more details.").append(sep);
		writer.append(" *").append(sep);
		writer.append(" * You should have received a copy of the GNU General Public License").append(sep);
		writer.append(" * along with this program. If not, see <https://www.gnu.org/licenses/>.").append(sep);
		writer.append(" *******************************************************************************/").append(sep);
		writer.append(sep);
		writer.append("package de.symeda.sormas.api.epipulse;").append(sep);
		writer.append(sep);
	}

	/**
	 * The paragraph that says where the content came from, written into both outputs so that
	 * neither can be read as hand-maintained.
	 */
	private static void writeProvenance(Writer writer, String sep, EpipulseMetadata metadata) throws IOException {

		writer.append(" * <p>").append(sep);
		writer.append(" * <strong>Generated</strong> from the EpiPulse cases metadata published at").append(sep);
		writer.append(" * <a href=\"").append(METADATA_SOURCE_URL).append("\">ECDC</a>:").append(sep);
		writer.append(" * the ").append(String.valueOf(metadata.getVariables().size())).append(" variables of the ");
		writer.append(String.valueOf(metadata.getSubjectCodes().size())).append(" case-based subject codes in").append(sep);
		writer.append(" * {@code ").append(metadata.getSourcePath()).append("}. Rerun").append(sep);
		writer.append(" * {@code EpipulseVariableGenerator} from the {@code sormas-api} module directory when the metadata").append(sep);
		writer.append(" * changes; do not hand-edit this file.").append(sep);
	}

	private static void writeGeneratedAnnotation(Writer writer, String sep) throws IOException {
		writer.append("@Generated(value = \"").append(GENERATOR_NAME).append("\")").append(sep);
	}

	// --- EpipulseVariable -------------------------------------------------------------------

	private static final class VariableEnumGenerator extends EpipulseVariableGenerator {

		private VariableEnumGenerator() {
			super("EpipulseVariable");
		}

		@Override
		void writeClass(Writer writer, String sep, EpipulseMetadata metadata) throws IOException {

			writeFileHeader(writer, sep);

			writer.append("import javax.annotation.Generated;").append(sep);
			writer.append(sep);

			writer.append("/**").append(sep);
			writer.append(" * Every variable EpiPulse defines for the case-based subject codes SORMAS reports, and the exact").append(sep);
			writer.append(" * name each one carries as a CSV column.").append(sep);
			writer.append(" *").append(sep);
			writeProvenance(writer, sep, metadata);
			writer.append(" */").append(sep);
			writeGeneratedAnnotation(writer, sep);
			writer.append("public enum EpipulseVariable {").append(sep);
			writer.append(sep);

			int remaining = metadata.getVariables().size();
			for (Variable variable : metadata.getVariables()) {
				writer.append("\t/** ").append(variable.getLabel()).append(" - ").append(variable.getSubjectCodeSummary()).append(" */").append(sep);
				writer.append("\t").append(variable.getConstantName()).append("(\"").append(variable.getName()).append("\")");
				writer.append(--remaining == 0 ? ";" : ",").append(sep);
			}

			writer.append(sep);
			writer.append("\tprivate final String variableName;").append(sep);
			writer.append(sep);
			writer.append("\tEpipulseVariable(String variableName) {").append(sep);
			writer.append("\t\tthis.variableName = variableName;").append(sep);
			writer.append("\t}").append(sep);
			writer.append(sep);
			writer.append("\t/**").append(sep);
			writer.append("\t * The variable's name exactly as EpiPulse spells it, which is the CSV column header.").append(sep);
			writer.append("\t */").append(sep);
			writer.append("\tpublic String getVariableName() {").append(sep);
			writer.append("\t\treturn variableName;").append(sep);
			writer.append("\t}").append(sep);
			writer.append(sep);
			writer.append("\t@Override").append(sep);
			writer.append("\tpublic String toString() {").append(sep);
			writer.append("\t\treturn variableName;").append(sep);
			writer.append("\t}").append(sep);
			writer.append("}").append(sep);
			writer.flush();
		}
	}

	// --- EpipulseSubjectCodeVariables -------------------------------------------------------

	private static final class SubjectCodeVariablesGenerator extends EpipulseVariableGenerator {

		private SubjectCodeVariablesGenerator() {
			super("EpipulseSubjectCodeVariables");
		}

		@Override
		void writeClass(Writer writer, String sep, EpipulseMetadata metadata) throws IOException {

			writeFileHeader(writer, sep);

			writer.append("import java.util.Collections;").append(sep);
			writer.append("import java.util.EnumMap;").append(sep);
			writer.append("import java.util.EnumSet;").append(sep);
			writer.append("import java.util.Map;").append(sep);
			writer.append("import java.util.Set;").append(sep);
			writer.append(sep);
			writer.append("import javax.annotation.Generated;").append(sep);
			writer.append(sep);

			writer.append("/**").append(sep);
			writer.append(" * Which variables EpiPulse defines for each subject code.").append(sep);
			writer.append(" *").append(sep);
			writeProvenance(writer, sep, metadata);
			writer.append(" *").append(sep);
			writer.append(" * <p>").append(sep);
			writer.append(" * Covers only the subject codes SORMAS can export, i.e. the constants of").append(sep);
			writer.append(" * {@link EpipulseSubjectCode}, not every case-based code in the metadata. Required and repeatable").append(sep);
			writer.append(" * flags are deliberately not here; they are per (subject code, variable) too, but nothing needs").append(sep);
			writer.append(" * them yet and a second generated table would have to be kept in step.").append(sep);
			writer.append(" */").append(sep);
			writeGeneratedAnnotation(writer, sep);
			writer.append("public final class EpipulseSubjectCodeVariables {").append(sep);
			writer.append(sep);
			writer.append("\tprivate static final Map<EpipulseSubjectCode, Set<EpipulseVariable>> BY_CODE =").append(sep);
			writer.append("\t\tnew EnumMap<>(EpipulseSubjectCode.class);").append(sep);
			writer.append(sep);
			writer.append("\tstatic {").append(sep);

			for (EpipulseSubjectCode subjectCode : metadata.getExportedSubjectCodes()) {
				writer.append("\t\tBY_CODE.put(").append(sep);
				writer.append("\t\t\tEpipulseSubjectCode.").append(subjectCode.name()).append(",").append(sep);
				writer.append("\t\t\tEnumSet.of(").append(sep);

				SortedSet<String> constants = metadata.getConstantNames(subjectCode.name());
				int remaining = constants.size();
				for (String constant : constants) {
					writer.append("\t\t\t\tEpipulseVariable.").append(constant).append(--remaining == 0 ? "));" : ",").append(sep);
				}
			}

			writer.append("\t}").append(sep);
			writer.append(sep);
			writer.append("\tprivate EpipulseSubjectCodeVariables() {").append(sep);
			writer.append("\t}").append(sep);
			writer.append(sep);
			writer.append("\t/**").append(sep);
			writer.append("\t * @return every variable EpiPulse defines for this subject code, never {@code null}").append(sep);
			writer.append("\t */").append(sep);
			writer.append("\tpublic static Set<EpipulseVariable> of(EpipulseSubjectCode subjectCode) {").append(sep);
			writer.append("\t\treturn Collections.unmodifiableSet(BY_CODE.getOrDefault(subjectCode, EnumSet.noneOf(EpipulseVariable.class)));")
				.append(sep);
			writer.append("\t}").append(sep);
			writer.append(sep);
			writer.append("\t/**").append(sep);
			writer.append("\t * @return whether EpiPulse defines this variable for this subject code, and therefore whether an").append(sep);
			writer.append("\t *         export of that code may carry a column for it").append(sep);
			writer.append("\t */").append(sep);
			writer.append("\tpublic static boolean defines(EpipulseSubjectCode subjectCode, EpipulseVariable variable) {").append(sep);
			writer.append("\t\treturn of(subjectCode).contains(variable);").append(sep);
			writer.append("\t}").append(sep);
			writer.append("}").append(sep);
			writer.flush();
		}
	}

	// --- metadata ---------------------------------------------------------------------------

	/**
	 * One variable of the metadata, gathered from every row that defines it.
	 */
	static final class Variable {

		private final String name;
		private final String label;
		private final SortedSet<String> subjectCodes;

		private Variable(String name, String label, SortedSet<String> subjectCodes) {

			this.name = name;
			this.label = label;
			this.subjectCodes = subjectCodes;
		}

		/**
		 * @return the name exactly as EpiPulse spells it, which is the CSV column header
		 */
		String getName() {
			return name;
		}

		/**
		 * @return the human-readable name, for the constant's javadoc
		 */
		String getLabel() {
			return label;
		}

		/**
		 * @return the constant's name, {@code UPPER_SNAKE_CASE} of {@link #getName()}
		 */
		String getConstantName() {
			return toConstantName(name);
		}

		/**
		 * The subject codes that define this variable, abbreviated for a one-line javadoc: a few
		 * are enough to place a variable, and {@code Age} would otherwise spend a line on 38 codes.
		 * {@link EpipulseSubjectCodeVariables} is the authority on membership.
		 */
		String getSubjectCodeSummary() {

			List<String> shown = new ArrayList<>(subjectCodes);
			if (shown.size() <= JAVADOC_SUBJECT_CODE_LIMIT) {
				return String.join(", ", shown);
			}
			return String.join(", ", shown.subList(0, JAVADOC_SUBJECT_CODE_LIMIT))
				+ String.format(" +%d more", shown.size() - JAVADOC_SUBJECT_CODE_LIMIT);
		}
	}

	private static final int JAVADOC_SUBJECT_CODE_LIMIT = 6;

	/**
	 * The case-based part of one metadata export, read and checked.
	 */
	static final class EpipulseMetadata {

		private final String sourcePath;
		private final List<Variable> variables;
		private final SortedSet<String> subjectCodes;
		private final SortedMap<String, SortedSet<String>> constantNamesByCode;
		private final Map<String, Integer> varyingCounts;

		private EpipulseMetadata(
			String sourcePath,
			List<Variable> variables,
			SortedSet<String> subjectCodes,
			SortedMap<String, SortedSet<String>> constantNamesByCode,
			Map<String, Integer> varyingCounts) {

			this.sourcePath = sourcePath;
			this.variables = Collections.unmodifiableList(variables);
			this.subjectCodes = subjectCodes;
			this.constantNamesByCode = constantNamesByCode;
			this.varyingCounts = varyingCounts;
		}

		/**
		 * @return where the transcribed export lives, relative to the repository root
		 */
		String getSourcePath() {
			return sourcePath;
		}

		/**
		 * @return every case-based variable, ordered as the enum constants are
		 */
		List<Variable> getVariables() {
			return variables;
		}

		SortedSet<String> getSubjectCodes() {
			return subjectCodes;
		}

		/**
		 * @return the constants of the variables this subject code defines, in enum order
		 */
		SortedSet<String> getConstantNames(String subjectCode) {
			return constantNamesByCode.getOrDefault(subjectCode, new TreeSet<>());
		}

		/**
		 * @return how many variables the metadata answers differently per subject code for this
		 *         column, which is why {@link EpipulseVariable} carries none of them
		 */
		String getVaryingCount(String column) {
			return String.valueOf(varyingCounts.getOrDefault(column, 0));
		}

		int countCodesDefining(String variableName) {

			String constant = toConstantName(variableName);
			return (int) getExportedSubjectCodes().stream().filter(code -> getConstantNames(code.name()).contains(constant)).count();
		}

		/**
		 * The subject codes SORMAS exports, which are the rows of
		 * {@link EpipulseSubjectCodeVariables}. Aggregate codes are skipped: they report counts
		 * rather than cases, so their variables are not in this file's scope.
		 */
		List<EpipulseSubjectCode> getExportedSubjectCodes() {

			return Arrays.stream(EpipulseSubjectCode.values()).filter(code -> !code.isAggregatedReporting()).collect(Collectors.toList());
		}
	}

	/**
	 * {@code UPPER_SNAKE_CASE} of a variable name, splitting where a word starts rather than at
	 * every capital, so that {@code AgeMonth} becomes {@code AGE_MONTH} and an acronym stays whole:
	 * {@code ASTMethod} becomes {@code AST_METHOD} rather than {@code A_S_T_METHOD}.
	 */
	static String toConstantName(String variableName) {

		return variableName.replaceAll("(?<=[a-z0-9])(?=[A-Z])", "_").replaceAll("(?<=[A-Z])(?=[A-Z][a-z])", "_").replaceAll("_+", "_").toUpperCase();
	}

	/**
	 * Reads the export, keeps the case-based subject codes, and checks that what it holds can be
	 * transcribed at all.
	 */
	static EpipulseMetadata readMetadata() throws IOException {

		InputStream metadata = EpipulseVariableGenerator.class.getResourceAsStream(METADATA_CSV_RESOURCE);
		if (metadata == null) {
			throw new IOException(String.format("'%s' is not on the classpath; it is expected at %s", METADATA_CSV_RESOURCE, METADATA_CSV_PATH));
		}

		List<String[]> rows;
		try (CSVReader reader = CSVUtils.createBomCsvReader(metadata)) {
			rows = reader.readAll();
		} catch (CsvException e) {
			throw new IOException("Could not parse " + METADATA_CSV_PATH, e);
		}

		if (rows.isEmpty()) {
			throw new IOException(METADATA_CSV_PATH + " is empty");
		}

		List<String> header = Arrays.asList(rows.get(0));
		int subjectCodeColumn = requireColumn(header, COLUMN_SUBJECT_CODE);
		int variableColumn = requireColumn(header, COLUMN_VARIABLE);
		int variableNameColumn = requireColumn(header, COLUMN_VARIABLE_NAME);

		// Gathered per variable rather than per row: a variable is defined once but appears once
		// per subject code that uses it, and the enum has one constant for all of them.
		SortedMap<String, List<String[]>> rowsByVariable = new TreeMap<>();
		SortedSet<String> subjectCodes = new TreeSet<>();
		SortedMap<String, SortedSet<String>> constantNamesByCode = new TreeMap<>();

		for (String[] row : rows.subList(1, rows.size())) {
			String subjectCode = value(row, subjectCodeColumn);
			if (!CASE_BASED_SUBJECT_CODES.contains(subjectCode)) {
				continue;
			}

			String variableName = value(row, variableColumn);
			subjectCodes.add(subjectCode);
			rowsByVariable.computeIfAbsent(variableName, name -> new ArrayList<>()).add(row);
			constantNamesByCode.computeIfAbsent(subjectCode, code -> new TreeSet<>()).add(toConstantName(variableName));
		}

		Collection<String> missingCodes = new TreeSet<>(CASE_BASED_SUBJECT_CODES);
		missingCodes.removeAll(subjectCodes);
		if (!missingCodes.isEmpty()) {
			throw new IOException(
				String.format("No rows in %s for the subject code(s) %s. Has the metadata dropped them?", METADATA_CSV_PATH, missingCodes));
		}

		List<Variable> variables = new ArrayList<>();
		Map<String, String> namesByConstant = new TreeMap<>();
		for (Map.Entry<String, List<String[]>> entry : rowsByVariable.entrySet()) {
			String variableName = entry.getKey();

			if (!variableName.matches("[A-Za-z][A-Za-z0-9_]*")) {
				throw new IOException(String.format("Variable '%s' is not a valid Java identifier and cannot become a constant", variableName));
			}

			String constant = toConstantName(variableName);
			String clashing = namesByConstant.put(constant, variableName);
			if (clashing != null) {
				throw new IOException(String.format("Variables '%s' and '%s' both become the constant %s", clashing, variableName, constant));
			}

			SortedSet<String> definedBy = new TreeSet<>();
			for (String[] row : entry.getValue()) {
				definedBy.add(value(row, subjectCodeColumn));
			}

			variables.add(new Variable(variableName, readLabel(entry.getValue(), variableNameColumn, variableName), definedBy));
		}

		Map<String, Integer> varyingCounts = new TreeMap<>();
		for (String column : PER_SUBJECT_CODE_COLUMNS) {
			int index = requireColumn(header, column);
			int varying = 0;
			for (List<String[]> variableRows : rowsByVariable.values()) {
				if (variableRows.stream().map(row -> value(row, index)).collect(Collectors.toSet()).size() > 1) {
					varying++;
				}
			}
			varyingCounts.put(column, varying);
		}

		return new EpipulseMetadata(METADATA_CSV_PATH, variables, subjectCodes, constantNamesByCode, varyingCounts);
	}

	/**
	 * The label for a variable's javadoc.
	 */
	private static String readLabel(List<String[]> variableRows, int variableNameColumn, String fallback) {

		Pattern whitespace = Pattern.compile("\\s+");
		Set<String> labels = new LinkedHashSet<>();
		for (String[] row : variableRows) {
			String label = whitespace.matcher(value(row, variableNameColumn)).replaceAll(" ").trim();
			if (!label.isEmpty()) {
				labels.add(label);
			}
		}

		String label = labels.stream().max(Comparator.comparingInt(String::length).thenComparing(Comparator.reverseOrder())).orElse(fallback);

		// A label is written straight into Javadoc, so avoid closing the comment early.
		return label.replace("*/", "*/");
	}

	private static int requireColumn(List<String> header, String column) throws IOException {

		int index = header.indexOf(column);
		if (index < 0) {
			throw new IOException(String.format("%s has no '%s' column; its columns are %s", METADATA_CSV_PATH, column, header));
		}
		return index;
	}

	private static String value(String[] row, int index) {
		return index < row.length && row[index] != null ? row[index].trim() : "";
	}

	static List<EpipulseVariableGenerator> buildConfig() {

		List<EpipulseVariableGenerator> config = new ArrayList<>();
		config.add(new VariableEnumGenerator());
		config.add(new SubjectCodeVariablesGenerator());

		return config;
	}

	/**
	 * Updates the generated EpiPulse metadata classes.
	 */
	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();

		// Check if this program is started with the module directory as working directory.
		Path path = Paths.get(FILE_PATH_PATTERN.split("/")[0]);
		if (!Files.exists(path)) {
			throw new IOException(
				String.format("Path '%s' not found. Please make sure the working directory is set to the module path.", path.toAbsolutePath()));
		}

		EpipulseMetadata metadata = readMetadata();

		List<EpipulseVariableGenerator> generators = buildConfig();
		for (EpipulseVariableGenerator generator : generators) {
			try {
				generator.generateClass(metadata);
			} catch (IOException e) {
				// This generator is manually run by developers, so print to console is permitted.
				System.out.println("Failure writing " + generator.getOutputClassName());
				throw e;
			}
		}

		System.out.println(
			String.format(
				"Generation finished. %s ms, %d variables across %d case-based subject codes, generated classes: %d",
				System.currentTimeMillis() - startTime,
				metadata.getVariables().size(),
				metadata.getSubjectCodes().size(),
				generators.size()));
	}
}
