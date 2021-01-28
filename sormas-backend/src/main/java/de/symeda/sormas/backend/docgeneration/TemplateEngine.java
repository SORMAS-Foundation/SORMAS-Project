/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.docgeneration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.introspection.SecureUberspector;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.FieldExtractor;
import fr.opensagres.xdocreport.template.FieldsExtractor;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.velocity.internal.ExtractVariablesVelocityVisitor;

public class TemplateEngine {

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("([{] *(!)? *([A-Za-z0-9._]+) *[}]| *(!)? *([A-Za-z0-9._]+) *)");

	public DocumentVariables extractTemplateVariablesDocx(File templateFile) throws DocumentTemplateException {
		try {
			FileInputStream templateInputStream = new FileInputStream(templateFile);
			IXDocReport report = readXDocReport(templateInputStream);

			FieldsExtractor<FieldExtractor> extractor = FieldsExtractor.create();
			report.extractFields(extractor);

			return filterExtractedVariables(extractor);
		} catch (XDocReportException | IOException e) {
			throw new DocumentTemplateException(String.format(I18nProperties.getString(Strings.errorReadingTemplate), templateFile.getName()));
		}
	}

	public DocumentVariables extractTemplateVariablesTxt(File templateFile) throws DocumentTemplateException {
		try {
			FileReader templateFileReader = new FileReader(templateFile);
			String templateName = templateFile.getName();

			FieldsExtractor<FieldExtractor> extractor = getFieldExtractorTxt(templateFileReader, templateName);

			return filterExtractedVariables(extractor);
		} catch (IOException e) {
			throw new DocumentTemplateException(String.format(I18nProperties.getString(Strings.errorReadingTemplate), templateFile.getName()));
		}
	}

	public byte[] generateDocumentDocx(Properties properties, File templateFile) throws DocumentTemplateException {
		try {
			FileInputStream templateInputStream = new FileInputStream(templateFile);
			IXDocReport report = readXDocReport(templateInputStream);
			IContext context = report.createContext();

			for (Object key : properties.keySet()) {
				if (key instanceof String) {
					Object property = properties.get(key);
					if (property != null && !(property instanceof String && ((String) property).isEmpty())) {
						context.put((String) key, property);
					}
				}
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			report.process(context, outputStream);
			return outputStream.toByteArray();
		} catch (IOException | XDocReportException e) {
			throw new DocumentTemplateException(String.format(I18nProperties.getString(Strings.errorDocumentGeneration), templateFile.getName()));
		}
	}

	public String generateDocumentTxt(Properties properties, File templateFile) {
		VelocityEngine velocityEngine = new VelocityEngine();
		// Disable Reflection and Classloader related methods
		velocityEngine.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, SecureUberspector.class.getCanonicalName());
		// Disable Includes
		velocityEngine.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, NoIncludesEventHandler.class.getCanonicalName());
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, FilenameUtils.getFullPathNoEndSeparator(templateFile.getPath()));
		Template template = velocityEngine.getTemplate(templateFile.getName());
		VelocityContext velocityContext = new VelocityContext();

		for (Object key : properties.keySet()) {
			if (key instanceof String) {
				Object property = properties.get(key);
				if (property != null && !(property instanceof String && ((String) property).isEmpty())) {
					velocityContext.put((String) key, property);
				}
			}
		}

		StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);
		return stringWriter.toString();
	}

	public void validateTemplateDocx(InputStream templateInputStream) throws DocumentTemplateException {
		try {
			IXDocReport report = readXDocReport(templateInputStream);
			FieldsExtractor<FieldExtractor> extractor = FieldsExtractor.create();
			report.extractFields(extractor);
		} catch (XDocReportException | IOException e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorProcessingTemplate));
		}
	}

	protected IXDocReport readXDocReport(InputStream templateInputStream) throws DocumentTemplateException {
		ByteArrayOutputStream outStream;

		try {
			// Sanitize docx template for XXEs
			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);
			wordMLPackage.getDocumentModel();

			outStream = new ByteArrayOutputStream();
			wordMLPackage.save(outStream);
		} catch (Docx4JException | NullPointerException e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorTemplateFileCorrupt));
		}

		try {
			ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
			return XDocReportRegistry.getRegistry().loadReport(inStream, TemplateEngineKind.Velocity);
		} catch (IOException | XDocReportException | NullPointerException e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorProcessingTemplate));
		}
	}

	public void validateTemplateTxt(InputStream templateInputStream) throws DocumentTemplateException {
		getFieldExtractorTxt(new InputStreamReader(templateInputStream), "validate");
	}

	private FieldsExtractor<FieldExtractor> getFieldExtractorTxt(Reader templateFileReader, String templateName) throws DocumentTemplateException {
		FieldsExtractor<FieldExtractor> extractor = FieldsExtractor.create();
		ExtractVariablesVelocityVisitor visitor = new ExtractVariablesVelocityVisitor(extractor);
		try {
			SimpleNode document = RuntimeSingleton.parse(templateFileReader, templateName);
			document.jjtAccept(visitor, null);
			return extractor;
		} catch (ParseException e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorProcessingTemplate));
		}
	}

	private DocumentVariables filterExtractedVariables(FieldsExtractor<FieldExtractor> extractor) {
		Set<String> variables = new HashSet<>();
		Set<String> nullableVariables = new HashSet<>();
		for (FieldExtractor field : extractor.getFields()) {
			String fieldName = field.getName();
			Matcher matcher = VARIABLE_PATTERN.matcher(fieldName);
			if (matcher.matches()) {
				String withBrackets = matcher.group(3);
				String withoutBrackets = matcher.group(5);
				String variable = withBrackets != null ? withBrackets : withoutBrackets;
				if (variable != null) {
					variables.add(variable);
				}
				if (matcher.group(2) != null || matcher.group(4) != null) {
					nullableVariables.add(variable);
				} else {
					nullableVariables.remove(variable);
				}
			}
		}
		return new DocumentVariables(variables, nullableVariables);
	}
}
