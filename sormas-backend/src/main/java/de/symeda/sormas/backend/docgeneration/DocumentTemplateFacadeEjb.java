/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import de.symeda.sormas.api.EntityDtoAccessHelper;
import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateFacade;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;

@Stateless(name = "DocumentTemplateFacade")
public class DocumentTemplateFacadeEjb implements DocumentTemplateFacade {

	private static final Pattern BASENAME_PATTERN = Pattern.compile("^([^_.]+)([_.].*)?");

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@EJB
	private PersonFacadeEjbLocal personFacade;

	@EJB
	private CaseFacadeEjbLocal caseFacade;

	@EJB
	private ContactFacadeEjbLocal contactFacade;

	@EJB
	private UserFacadeEjbLocal userFacade;

	@EJB
	private RegionFacadeEjbLocal regionFacade;

	@EJB
	private DistrictFacadeEjbLocal districtFacade;

	@EJB
	private CommunityFacadeEjbLocal communityFacade;

	@EJB
	private FacilityFacadeEjbLocal facilityFacade;

	@EJB
	private PointOfEntryFacadeEjbLocal pointOfEntryFacade;

	@EJB
	private EventFacadeEjbLocal eventFacade;

	@EJB
	private SampleFacadeEjbLocal sampleFacade;

	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;

	private TemplateEngine templateEngine = new TemplateEngine();

	@Override
	public byte[] generateDocumentDocxFromEntities(
		DocumentWorkflow documentWorkflow,
		String templateName,
		Map<String, Object> entities,
		Properties extraProperties)
		throws DocumentTemplateException {
		if (!documentWorkflow.isDocx()) {
			throw new DocumentTemplateException(
				String.format(I18nProperties.getString(Strings.messageWrongTemplateFileType), documentWorkflow, documentWorkflow.getFileExtension()));
		}

		// 1. Read template from custom directory
		File templateFile = getTemplateFile(documentWorkflow, templateName);

		// 2. Extract document variables
		DocumentVariables documentVariables = getTemplateVariablesDocx(templateFile);

		// 3. prepare properties
		Properties properties = prepareProperties(documentWorkflow, entities, extraProperties, documentVariables);

		// 4. generate document
		return generateDocumentDocx(templateFile, properties);
	}

	@Override
	public String generateDocumentTxtFromEntities(
		DocumentWorkflow documentWorkflow,
		String templateName,
		Map<String, Object> entities,
		Properties extraProperties)
		throws DocumentTemplateException {
		if (documentWorkflow.isDocx()) {
			throw new DocumentTemplateException(
				String.format(I18nProperties.getString(Strings.messageWrongTemplateFileType), documentWorkflow, documentWorkflow.getFileExtension()));
		}

		// 1. Read template from custom directory
		File templateFile = getTemplateFile(documentWorkflow, templateName);

		// 2. Extract document variables
		DocumentVariables documentVariables = getTemplateVariablesTxt(templateFile);

		// 3. prepare properties
		Properties properties = prepareProperties(documentWorkflow, entities, extraProperties, documentVariables);

		// 4. generate document
		return generateDocumentTxt(templateFile, properties);
	}

	private Properties prepareProperties(
		DocumentWorkflow documentWorkflow,
		Map<String, Object> entities,
		Properties extraProperties,
		DocumentVariables documentVariables) {
		Properties properties = new Properties();

		// 1. Map template variables to entity data if possible
		// Naming conventions according sormas-api/src/main/resources/doc/SORMAS_Data_Dictionary.xlsx, e.g.:
		// <CaseDataDto>.person.firstName
		// <CaseDataDto>.quarantineFrom
		// Generic access as implemented in DataDictionaryGenerator.java

		EntityDtoAccessHelper.IReferenceDtoResolver referenceDtoResolver = getReferenceDtoResolver();

		String propertySeparator = documentWorkflow.isDocx() ? "." : "_";
		for (String propertyKey : documentVariables.getVariables()) {
			if (isEntityVariable(documentWorkflow, propertyKey)) {
				String variableBaseName = getVariableBaseName(propertyKey);
				Object entity = entities.get(variableBaseName);
				if (entity instanceof HasUuid) {
					if (documentWorkflow.isDocx() || propertyKey.contains(propertySeparator)) {
						String propertyPath = propertyKey.replaceFirst("(?i)" + variableBaseName + "[" + propertySeparator + "]", "");
						if (!".".equals(propertySeparator)) {
							propertyPath = propertyPath.replaceAll(propertySeparator, ".");
						}
						Object propertyValue;
						try {
							propertyValue = EntityDtoAccessHelper.getPropertyPathValueString((HasUuid) entity, propertyPath, referenceDtoResolver);
						} catch (Exception e) {
							propertyValue = "*** " + e.getMessage().replaceAll("(Reference)?Dto$", "") + " ***";
						}
						if (propertyValue != null) {
							properties.put(propertyKey, propertyValue);
						}
					}
				}
			}
		}
		if (!documentWorkflow.isDocx()) {
			for (String entityKey : entities.keySet()) {
				Object entity = entities.get(entityKey);
				if (ReferenceDto.class.isAssignableFrom(entity.getClass())) {
					entity = referenceDtoResolver.resolve((ReferenceDto) entity);
				}
				properties.put(entityKey, entity);
			}
		}

		// 2. merge extra properties

		if (extraProperties != null) {
			for (String extraPropertyKey : extraProperties.stringPropertyNames()) {
				String propertyValue = extraProperties.getProperty(extraPropertyKey);
				properties.setProperty(extraPropertyKey, propertyValue);
			}
		}

		// 3. fill null properties
		String nullReplacement = configFacade.getDocgenerationNullReplacement();
		if (nullReplacement.isEmpty()) {
			nullReplacement = " ";
		}

		for (String variable : documentVariables.getVariables()) {
			Object property = properties.get(variable);
			if ((property == null || StringUtils.isBlank(property.toString())) && !documentVariables.isNullableVariable(variable)) {
				properties.setProperty(variable, nullReplacement);
			}
		}
		properties.put("F", new ObjectFormatter());
		return properties;
	}

	private byte[] generateDocumentDocx(File templateFile, Properties properties) throws DocumentTemplateException {
		return templateEngine.generateDocumentDocx(properties, templateFile);
	}

	private String generateDocumentTxt(File templateFile, Properties properties) {
		return templateEngine.generateDocumentTxt(properties, templateFile);
	}

	@Override
	public List<String> getAvailableTemplates(DocumentWorkflow documentWorkflow) {
		File workflowTemplateDir = new File(getWorkflowTemplateDirPath(documentWorkflow).toUri());
		if (!workflowTemplateDir.exists() || !workflowTemplateDir.isDirectory()) {
			return Collections.emptyList();
		}
		File[] availableTemplates =
			workflowTemplateDir.listFiles((d, name) -> name.toLowerCase().endsWith("." + documentWorkflow.getFileExtension()));
		if (availableTemplates == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(availableTemplates).map(File::getName).sorted(String::compareTo).collect(Collectors.toList());
	}

	@Override
	public boolean isExistingTemplate(DocumentWorkflow documentWorkflow, String templateName) {
		File templateFile = new File(getWorkflowTemplateDirPath(documentWorkflow).resolve(templateName).toUri());
		return templateFile.exists();
	}

	@Override
	public DocumentVariables getDocumentVariables(DocumentWorkflow documentWorkflow, String templateName) throws DocumentTemplateException {
		File templateFile = getTemplateFile(documentWorkflow, templateName);
		DocumentVariables documentVariables =
			documentWorkflow.isDocx() ? getTemplateVariablesDocx(templateFile) : getTemplateVariablesTxt(templateFile);
		Set<String> propertyKeys = documentVariables.getVariables();
		documentVariables.setAdditionalVariables(
			propertyKeys.stream().filter(e -> !isEntityVariable(documentWorkflow, e)).sorted(String::compareTo).collect(Collectors.toList()));
		propertyKeys.stream()
			.filter(e -> isEntityVariable(documentWorkflow, e))
			.forEach(e -> documentVariables.addUsedEntity(getVariableBaseName(e)));
		return documentVariables;
	}

	@Override
	public void writeDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName, byte[] document) throws DocumentTemplateException {
		if (!documentWorkflow.getFileExtension().equalsIgnoreCase(FilenameUtils.getExtension(templateName))) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.headingWrongFileType));
		}
		String path = FilenameUtils.getPath(templateName);
		if (StringUtils.isNotBlank(path)) {
			throw new DocumentTemplateException(String.format(I18nProperties.getString(Strings.errorIllegalFilename), templateName));
		}

		ByteArrayInputStream templateInputStream = new ByteArrayInputStream(document);
		if (documentWorkflow.isDocx()) {
			templateEngine.validateTemplateDocx(templateInputStream);
		} else {
			templateEngine.validateTemplateTxt(templateInputStream);
		}

		Path workflowTemplateDirPath = getWorkflowTemplateDirPath(documentWorkflow);
		try {
			Files.createDirectories(workflowTemplateDirPath);
		} catch (IOException e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorCreatingTemplateDirectory));
		}
		try (FileOutputStream fileOutputStream =
			new FileOutputStream(new File(workflowTemplateDirPath.resolve(FilenameUtils.getName(templateName)).toUri()))) {
			fileOutputStream.write(document);
		} catch (IOException e) {
			throw new DocumentTemplateException(I18nProperties.getString(Strings.errorWritingTemplate));
		}
	}

	@Override
	public boolean deleteDocumentTemplate(DocumentWorkflow documentWorkflow, String fileName) throws DocumentTemplateException {
		File templateFile = new File(getWorkflowTemplateDirPath(documentWorkflow).resolve(fileName).toUri());
		if (templateFile.exists() && templateFile.isFile()) {
			return templateFile.delete();
		} else {
			throw new DocumentTemplateException(String.format(I18nProperties.getString(Strings.errorFileNotFound), fileName));
		}
	}

	@Override
	public byte[] getDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName) throws DocumentTemplateException {
		try {
			return FileUtils.readFileToByteArray(getTemplateFile(documentWorkflow, templateName));
		} catch (IOException e) {
			throw new DocumentTemplateException(String.format(I18nProperties.getString(Strings.errorReadingTemplate), templateName));
		}
	}

	private File getTemplateFile(DocumentWorkflow documentWorkflow, String templateName) throws DocumentTemplateException {
		File templateFile = new File(getWorkflowTemplateDirPath(documentWorkflow).resolve(templateName).toString());

		if (!templateFile.exists()) {
			throw new DocumentTemplateException(String.format(I18nProperties.getString(Strings.errorFileNotFound), templateName));
		}
		return templateFile;
	}

	private DocumentVariables getTemplateVariablesDocx(File templateFile) throws DocumentTemplateException {
		return templateEngine.extractTemplateVariablesDocx(templateFile);
	}

	private DocumentVariables getTemplateVariablesTxt(File templateFile) throws DocumentTemplateException {
		return templateEngine.extractTemplateVariablesTxt(templateFile);
	}

	private boolean isEntityVariable(DocumentWorkflow documentWorkflow, String propertyKey) {
		if (propertyKey == null) {
			return false;
		}
		String basename = getVariableBaseName(propertyKey);
		return documentWorkflow.getRootEntityNames().contains(basename);
	}

	private String getVariableBaseName(String propertyKey) {
		String propertyKeyLowerCase = propertyKey.toLowerCase();
		Matcher matcher = BASENAME_PATTERN.matcher(propertyKeyLowerCase);
		return matcher.matches() ? matcher.group(1) : "";
	}

	private Path getWorkflowTemplateDirPath(DocumentWorkflow documentWorkflow) {
		return Paths.get(configFacade.getCustomFilesPath()).resolve("docgeneration").resolve(documentWorkflow.getTemplateDirectory());
	}

	private EntityDtoAccessHelper.IReferenceDtoResolver getReferenceDtoResolver() {
		EntityDtoAccessHelper.IReferenceDtoResolver referenceDtoResolver = referenceDto -> {
			if (referenceDto != null) {
				String uuid = referenceDto.getUuid();
				Class<? extends ReferenceDto> referenceDtoClass = referenceDto.getClass();
				if (PersonReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return personFacade.getPersonByUuid(uuid);
				} else if (CaseReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return caseFacade.getCaseDataByUuid(uuid);
				} else if (ContactReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return contactFacade.getContactByUuid(uuid);
				} else if (UserReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return userFacade.getByUuid(uuid);
				} else if (RegionReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return regionFacade.getRegionByUuid(uuid);
				} else if (DistrictReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return districtFacade.getDistrictByUuid(uuid);
				} else if (CommunityReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return communityFacade.getByUuid(uuid);
				} else if (FacilityReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return facilityFacade.getByUuid(uuid);
				} else if (PointOfEntryReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return pointOfEntryFacade.getByUuid(uuid);
				} else if (EventReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return eventFacade.getEventByUuid(uuid);
				} else if (EventParticipantReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return eventParticipantFacade.getByUuid(uuid);
				} else if (SampleReferenceDto.class.isAssignableFrom(referenceDtoClass)) {
					return sampleFacade.getSampleByUuid(uuid);
				}
			}
			return null;
		};
		return new EntityDtoAccessHelper.CachedReferenceDtoResolver(referenceDtoResolver);
	}

	public class ObjectFormatter {

		public Object format(Object value) {
			return EntityDtoAccessHelper.formatObject(value);
		}
	}

	@LocalBean
	@Stateless
	public static class DocumentTemplateFacadeEjbLocal extends DocumentTemplateFacadeEjb {
	}
}
