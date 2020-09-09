package de.symeda.sormas.backend.docgeneration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.FieldExtractor;
import fr.opensagres.xdocreport.template.FieldsExtractor;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

@Stateless
@LocalBean
public class TemplateEngineService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	public Set<String> getPlaceholders(String templatePath) throws IOException, XDocReportException, ParseException {
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(new FileInputStream(templatePath), TemplateEngineKind.Velocity);
		FieldsExtractor<FieldExtractor> extractor = FieldsExtractor.create();
		report.extractFields(extractor);

		RuntimeInstance runtimeInstance = new RuntimeInstance();

		Set<String> placeholders = new HashSet<>();
		for (FieldExtractor field : extractor.getFields()) {
			String fieldName = field.getName();
			SimpleNode fieldNode = runtimeInstance.parse(new StringReader("$" + fieldName), "");
			System.out.println(fieldNode.getClass().getSimpleName());
			System.out.println(fieldNode.toString());
			Token token = fieldNode.getFirstToken().next;
			System.out.println(token.toString());
			placeholders.add(fieldName);
		}
		return placeholders;
	}

	public String generateDocument(Map<String, String> replacements, String templatePath, String outputDirectory)
		throws IOException, XDocReportException {
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(new FileInputStream(templatePath), TemplateEngineKind.Velocity);

		IContext context = report.createContext();
		for (String key : replacements.keySet()) {
			context.put(key, replacements.get(key));
		}

		String outputFile = outputDirectory + File.separator + "Out_" + FilenameUtils.getBaseName(templatePath) + ".docx";
		File file = new File(outputFile);
		report.process(context, new FileOutputStream(file));

		return outputFile;
	}
}
