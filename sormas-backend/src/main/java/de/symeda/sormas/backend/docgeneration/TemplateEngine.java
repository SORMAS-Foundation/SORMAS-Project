package de.symeda.sormas.backend.docgeneration;

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

import de.symeda.sormas.api.docgeneneration.DocumentVariables;
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

	public DocumentVariables extractTemplateVariablesDocx(File templateFile) throws IOException, XDocReportException {
		FileInputStream templateInputStream = new FileInputStream(templateFile);
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(templateInputStream, TemplateEngineKind.Velocity);

		FieldsExtractor<FieldExtractor> extractor = FieldsExtractor.create();
		report.extractFields(extractor);

		return filterExtractedVariables(extractor);
	}

	public DocumentVariables extractTemplateVariablesTxt(File templateFile) throws IOException, ParseException {
		FileReader templateFileReader = new FileReader(templateFile);
		String templateName = templateFile.getName();

		FieldsExtractor<FieldExtractor> extractor = getFieldExtractorTxt(templateFileReader, templateName);

		return filterExtractedVariables(extractor);
	}

	public byte[] generateDocumentDocx(Properties properties, File templateFile) throws IOException, XDocReportException {
		FileInputStream templateInputStream = new FileInputStream(templateFile);
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(templateInputStream, TemplateEngineKind.Velocity);
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
	}

	public String generateDocumentTxt(Properties properties, File templateFile) {
		VelocityEngine velocityEngine = new VelocityEngine();
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

	public void validateTemplateDocx(InputStream templateInputStream) {
		try {
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(templateInputStream, TemplateEngineKind.Velocity);
			FieldsExtractor<FieldExtractor> extractor = FieldsExtractor.create();
			report.extractFields(extractor);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public void validateTemplateTxt(InputStream templateInputStream) {
		try {
			getFieldExtractorTxt(new InputStreamReader(templateInputStream), "validate");
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private FieldsExtractor<FieldExtractor> getFieldExtractorTxt(Reader templateFileReader, String templateName) throws ParseException {
		FieldsExtractor<FieldExtractor> extractor = FieldsExtractor.create();
		ExtractVariablesVelocityVisitor visitor = new ExtractVariablesVelocityVisitor(extractor);
		SimpleNode document = RuntimeSingleton.parse(templateFileReader, templateName);
		document.jjtAccept(visitor, null);
		return extractor;
	}

	private DocumentVariables filterExtractedVariables(FieldsExtractor<FieldExtractor> extractor) {
		Set<String> variables = new HashSet<>();
		Set<String> nullablVariables = new HashSet<>();
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
					nullablVariables.add(variable);
				} else {
					nullablVariables.remove(variable);
				}
			}
		}
		return new DocumentVariables(variables, nullablVariables);
	}
}
