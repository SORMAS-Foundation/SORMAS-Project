package de.symeda.sormas.backend.docgeneration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.FieldExtractor;
import fr.opensagres.xdocreport.template.FieldsExtractor;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

public class TemplateEngine {

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("([{] *([A-Za-z0-9.]+) *[}]| *([A-Za-z0-9.]+) *)");

	public Set<String> extractTemplateVariables(InputStream templateFile) throws IOException, XDocReportException {
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(templateFile, TemplateEngineKind.Velocity);

		FieldsExtractor<FieldExtractor> extractor = FieldsExtractor.create();
		report.extractFields(extractor);

		Set<String> variables = new HashSet<>();
		for (FieldExtractor field : extractor.getFields()) {
			String fieldName = field.getName();
			Matcher matcher = VARIABLE_PATTERN.matcher(fieldName);
			if (matcher.matches()) {
				String withBrackets = matcher.group(2);
				String withoutBrackets = matcher.group(3);
				if (withBrackets != null) {
					variables.add(withBrackets);
				} else if (withoutBrackets != null) {
					variables.add(withoutBrackets);
				}
			}
		}
		return variables;
	}

	public InputStream generateDocument(Properties properties, InputStream templateFile) throws IOException, XDocReportException {
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(templateFile, TemplateEngineKind.Velocity);

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
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	public void validateTemplate(InputStream templateFile) {
		try {
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(templateFile, TemplateEngineKind.Velocity);
			FieldsExtractor<FieldExtractor> extractor = FieldsExtractor.create();
			report.extractFields(extractor);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
}
