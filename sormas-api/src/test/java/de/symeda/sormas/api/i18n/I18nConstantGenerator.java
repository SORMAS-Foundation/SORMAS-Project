package de.symeda.sormas.api.i18n;

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
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

/**
 * Generates Constants out of the corresponding property files.
 * 
 * @see Captions
 * @see Strings
 * @see Validations
 */
public class I18nConstantGenerator {

	private static final String FILE_PATH_PATTERN = "src/main/java/de/symeda/sormas/api/i18n/%s.java";

	private final String propertiesFileName;
	private final String outputClassName;
	private final String outputClassFilePath;
	private final boolean ignoreChildren;

	/**
	 * @param propertiesFileName
	 *            The properties file to read the contant keys from.
	 * @param outputClassName
	 *            Name of the constants class.
	 * @param ignoreChildren
	 */
	public I18nConstantGenerator(String propertiesFileName, String outputClassName, boolean ignoreChildren) {

		this.propertiesFileName = propertiesFileName;
		this.outputClassName = outputClassName;
		this.outputClassFilePath = String.format(FILE_PATH_PATTERN, outputClassName);
		this.ignoreChildren = ignoreChildren;
	}

	/**
	 * @return The properties file to look up the what constants need to be generated.
	 */
	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	/**
	 * @return Class name of the Constants file.
	 */
	public String getOutputClassName() {
		return outputClassName;
	}

	/**
	 * @return Path to the Constants file.
	 */
	public String getOutputClassFilePath() {
		return outputClassFilePath;
	}

	public boolean isIgnoreChildren() {
		return ignoreChildren;
	}

	private void generateI18nConstantClass() throws IOException {

		Path path = Paths.get(outputClassFilePath);
		String sep = determineLineSeparator(path);

		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeI18nConstantClass(writer, sep);
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

	/**
	 * @param propertiesFileName
	 *            The properties file to read the contant keys from.
	 * @param outputClassName
	 *            Name of the constants class.
	 * @param ignoreChildren
	 * @param writer
	 *            Writes the java file into this {@code writer}.
	 * @throws IOException
	 */
	void writeI18nConstantClass(Writer writer, String sep) throws IOException {

		Properties properties = new Properties();
		InputStream inputStream = I18nProperties.class.getClassLoader().getResourceAsStream(propertiesFileName);
		if (null != inputStream) {
			properties.load(inputStream);
		}

		Enumeration<?> e = properties.propertyNames();

		writer.append("package de.symeda.sormas.api.i18n;").append(sep + sep);
		writer.append("import javax.annotation.Generated;").append(sep + sep);
		writer.append("@Generated(value = \"" + getClass().getCanonicalName() + "\")").append(sep);
		writer.append("public interface " + outputClassName + " {").append(sep + sep);
		writer.append("\t/*")
			.append(sep)
			.append("\t * Hint for SonarQube issues:")
			.append(sep)
			.append("\t * 1. java:S115: Violation of name convention for constants of this class is accepted: Close as false positive.")
			.append(sep)
			.append("\t */")
			.append(sep + sep);

		Collection<String> orderedKeys = new TreeSet<String>(new Comparator<String>() {

			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (ignoreChildren && (key.contains(".") || key.contains("-") || key.contains("_"))) {
				continue;
			}
			orderedKeys.add(key);
		}

		for (String key : orderedKeys) {
			String constant = key.replaceAll("[\\.\\-]", "_");
			writer.append("\tString " + constant + " = \"" + key + "\";").append(sep);
		}

		writer.append("}").append(sep);
		writer.flush();
		writer.close();
	}

	static List<I18nConstantGenerator> buildConfig() {

		List<I18nConstantGenerator> config = new ArrayList<>();
		config.add(new I18nConstantGenerator("captions.properties", "Captions", false));
		config.add(new I18nConstantGenerator("strings.properties", "Strings", false));
		config.add(new I18nConstantGenerator("validations.properties", "Validations", false));
		config.add(new I18nConstantGenerator("countriess.properties", "Countries", false));
		config.add(new I18nConstantGenerator("descriptions.properties", "Descriptions", false));

		return config;
	}

	/**
	 * Updates i18n Constant classes.
	 */
	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();

		// Check if this program is started with the module directory as working directory.
		Path path = Paths.get(FILE_PATH_PATTERN.split("/")[0]);
		if (!Files.exists(path)) {
			throw new IOException(
				String.format(
					"Path '%s' not found. Please make sure the working directory is set to the module path.",
					path.toAbsolutePath().toString()));
		}

		List<I18nConstantGenerator> generators = buildConfig();
		for (I18nConstantGenerator generator : generators) {
			try {
				generator.generateI18nConstantClass();
			} catch (IOException e) {
				// This generator is manually run by developers, so print to console is permitted.
				System.out.println("Failure writing " + generator.outputClassName);
				throw e;
			}
		}

		System.out
			.println(String.format("Generation finished. %s ms, generated classes: %s", System.currentTimeMillis() - startTime, generators.size()));
	}
}
