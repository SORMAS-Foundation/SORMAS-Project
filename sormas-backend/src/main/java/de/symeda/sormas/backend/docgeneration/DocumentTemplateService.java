/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.io.FilenameUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.survey.Survey;
import de.symeda.sormas.backend.survey.SurveyService;

@Stateless
@LocalBean
public class DocumentTemplateService extends BaseAdoService<DocumentTemplate> {

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private SurveyService surveyService;

	public DocumentTemplateService() {
		super(DocumentTemplate.class);
	}

	public boolean deletePermanent(DocumentTemplate documentTemplate, DocumentWorkflow documentWorkflow) {
		boolean fileDeleted = deleteTemplateFile(documentTemplate);
		if (fileDeleted) {
			if (documentWorkflow != null) {
				Survey survey;
				switch (documentWorkflow) {
				case SURVEY_DOCUMENT:
					survey = documentTemplate.getSurveyDocTemplate();
					if (survey != null) {
						survey.setDocumentTemplate(null);
						surveyService.ensurePersisted(survey);
					}
					break;
				case SURVEY_EMAIL:
					survey = documentTemplate.getSurveyEmailTemplate();
					if (survey != null) {
						survey.setEmailTemplate(null);
						surveyService.ensurePersisted(survey);
					}
					break;
				}
			}
			super.deletePermanent(documentTemplate);
		}

		return fileDeleted;
	}

	public boolean deleteTemplateFile(DocumentTemplate documentTemplate) {
		String fileName = documentTemplate.getFileName();
		File templateFile = new File(getTemplateDirPath(documentTemplate).resolve(fileName).toUri());

		boolean deleted = false;
		boolean exists = templateFile.exists();
		if (!exists) {
			return true;
		}

		if (templateFile.isFile()) {
			deleted = templateFile.delete();
		}

		return deleted;
	}

	private Path getTemplateDirPath(DocumentTemplate documentTemplate) {
		return getTemplateDirPath(documentTemplate.getWorkflow(), documentTemplate.getDisease());
	}

	private Path getTemplateDirPath(DocumentWorkflow documentWorkflow, Disease disease) {
		Path path = Paths.get(configFacade.getCustomFilesPath()).resolve("docgeneration").resolve(documentWorkflow.getTemplateDirectory());

		if (disease != null) {
			path = path.resolve(disease.name());
		}

		return path;
	}

	public boolean existsFile(DocumentWorkflow documentWorkflow, Disease disease, String templateName) {
		File templateFile = new File(this.getTemplateDirPath(documentWorkflow, disease).resolve(templateName).toUri());
		return templateFile.exists();
	}

	public void ensurePersisted(DocumentTemplate documentTemplate, byte[] document) throws DocumentTemplateException {
		Path workflowTemplateDirPath = getTemplateDirPath(documentTemplate.getWorkflow(), documentTemplate.getDisease());
		try {
			Files.createDirectories(workflowTemplateDirPath);
		} catch (IOException e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorCreatingTemplateDirectory));
		}

		try (FileOutputStream fileOutputStream =
			new FileOutputStream(new File(workflowTemplateDirPath.resolve(FilenameUtils.getName(documentTemplate.getFileName())).toUri()))) {
			fileOutputStream.write(document);
			ensurePersisted(documentTemplate);
		} catch (IOException e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorWritingTemplate));
		}
	}

	public File getTemplateFile(DocumentTemplate template) throws DocumentTemplateException {
		File templateFile = new File(getTemplateDirPath(template).resolve(template.getFileName()).toString());

		if (!templateFile.exists()) {
			throw new DocumentTemplateException(String.format(I18nProperties.getString(Strings.errorFileNotFound), template.getFileName()));
		}
		return templateFile;
	}

	public Map<DocumentWorkflow, List<File>> getAllTemplateFiles() {
		Map<DocumentWorkflow, List<File>> templateFiles = new HashMap<>();

		for (DocumentWorkflow workflow : DocumentWorkflow.values()) {
			Path templateDirPath = getTemplateDirPath(workflow, null);
			File templateDir = templateDirPath.toFile();

			if (templateDir.exists() && templateDir.isDirectory()) {
				templateFiles.put(workflow, Stream.of(templateDir.listFiles()).filter(File::isFile).collect(Collectors.toList()));
			}
		}

		return templateFiles;
	}
}
