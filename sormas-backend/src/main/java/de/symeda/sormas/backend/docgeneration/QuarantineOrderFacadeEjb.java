package de.symeda.sormas.backend.docgeneration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import fr.opensagres.xdocreport.core.XDocReportException;

@Stateless(name = "QuarantineOrderFacade")
public class QuarantineOrderFacadeEjb implements QuarantineOrderFacade {

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@EJB
	private CaseFacadeEjbLocal caseFacade;

	@EJB
	private TemplateEngineService templateEngineService;

	@Override
	public ByteArrayInputStream getGeneratedDocument(String templateName, String caseUuid, Properties extraProperties) {
		// 1. Read template from custom directory
		String workflowTemplateDir = configFacade.getCustomFilesPath() + File.separator + "docgeneration" + File.separator + "quarantine";
		String templateFileName = workflowTemplateDir + File.separator + templateName;
		File templateFile = new File(templateFileName);

		if (!templateFile.exists()) {
			throw new IllegalArgumentException("Template file '" + templateName + "' not found.");
		}

		Set<String> propertyKeys;
		try {
			propertyKeys = templateEngineService.extractTemplateVariables(new FileInputStream(templateFile));
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not read template file '" + templateName + "'.");
		} catch (XDocReportException e) {
			throw new IllegalArgumentException("Could not process template file '" + templateName + "'.");
		}

		Properties properties = new Properties();

		// 2. Map template properties to case data if possible
		// Naming conventions according sormas-api/src/main/resources/doc/SORMAS_Data_Dictionary.xlsx, e.g.:
		// Case.person.firstName
		// Case.quarantineFrom
		// Generic access as implemented in DataDictionaryGenerator.java
		// see also: DownloadUtil.createCsvExportStreamResource

		CaseDataDto caseData = caseFacade.getCaseDataByUuid(caseUuid);
		for (String key : propertyKeys) {
			if (key.startsWith("case.")) {
				// get data from case
			}
		}

		// 3. merge extra properties

		// 4. generate document

		return null;
	}
}
