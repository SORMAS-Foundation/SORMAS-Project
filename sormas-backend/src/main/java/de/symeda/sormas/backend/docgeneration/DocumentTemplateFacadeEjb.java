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

import de.symeda.sormas.api.FacadeProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.parser.ParseException;

import de.symeda.sormas.api.EntityDtoAccessHelper;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateFacade;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import fr.opensagres.xdocreport.core.XDocReportException;

@Stateless(name = "DocumentTemplateFacade")
public class DocumentTemplateFacadeEjb implements DocumentTemplateFacade {

	private static final String DEFAULT_NULL_REPLACEMENT = "./.";
	private static final Pattern BASENAME_PATTERN = Pattern.compile("^([^_.]+)([_.].*)?");

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;

	@EJB
	private UserFacadeEjb.UserFacadeEjbLocal userFacade;

	@EJB
	private RegionFacadeEjb.RegionFacadeEjbLocal regionFacade;

	@EJB
	private DistrictFacadeEjb.DistrictFacadeEjbLocal districtFacade;

	@EJB
	private CommunityFacadeEjb.CommunityFacadeEjbLocal communityFacade;

	@EJB
	private FacilityFacadeEjb.FacilityFacadeEjbLocal facilityFacade;

	@EJB
	private PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal pointOfEntryFacade;

	@EJB
	private EventFacadeEjb.EventFacadeEjbLocal eventFacade;

	private TemplateEngine templateEngine = new TemplateEngine();

	@Override
	public byte[] generateDocumentDocxFromEntities(
		DocumentWorkflow documentWorkflow,
		String templateName,
		Map<String, Object> entities,
		Properties extraProperties)
		throws IOException {
		if (!documentWorkflow.isDocx()) {
			throw new IllegalArgumentException("Workflow " + documentWorkflow + " is not a .docs workflow");
		}

		// 1. Read template from custom directory
		File templateFile = getTemplateFile(documentWorkflow, templateName);

		// 2. Extract document variables
		Set<String> propertyKeys = getTemplateVariablesDocx(templateFile);

		// 3. prepare properties
		Properties properties = prepareProperties(documentWorkflow, entities, extraProperties, propertyKeys);

		// 4. generate document
		return generateDocumentDocx(templateFile, properties);
	}

	@Override
	public String generateDocumentTxtFromEntities(
		DocumentWorkflow documentWorkflow,
		String templateName,
		Map<String, Object> entities,
		Properties extraProperties)
		throws IOException {
		if (documentWorkflow.isDocx()) {
			throw new IllegalArgumentException("Workflow " + documentWorkflow + " is a .docs workflow");
		}

		// 1. Read template from custom directory
		File templateFile = getTemplateFile(documentWorkflow, templateName);

		// 2. Extract document variables
		Set<String> propertyKeys = getTemplateVariablesTxt(templateFile);

		// 3. prepare properties
		Properties properties = prepareProperties(documentWorkflow, entities, extraProperties, propertyKeys);

		// 4. generate document
		return generateDocumentTxt(templateFile, properties);
	}

	private Properties prepareProperties(
		DocumentWorkflow documentWorkflow,
		Map<String, Object> entities,
		Properties extraProperties,
		Set<String> propertyKeys) {
		Properties properties = new Properties();

		// 1. Map template variables to entity data if possible
		// Naming conventions according sormas-api/src/main/resources/doc/SORMAS_Data_Dictionary.xlsx, e.g.:
		// <CaseDataDto>.person.firstName
		// <CaseDataDto>.quarantineFrom
		// Generic access as implemented in DataDictionaryGenerator.java

		EntityDtoAccessHelper.IReferenceDtoResolver referenceDtoResolver = getReferenceDtoResolver();

		String propertySeparator = documentWorkflow.isDocx() ? "." : "_";
		for (String propertyKey : propertyKeys) {
			if (isEntityVariable(documentWorkflow, propertyKey)) {
				String variableBaseName = getVariableBaseName(propertyKey);
				Object entity = entities.get(variableBaseName);
				if (entity != null) {
					if (documentWorkflow.isDocx() || propertyKey.contains(propertySeparator)) {
						String propertyPath = propertyKey.replaceFirst(variableBaseName + propertySeparator, "");
						if (!".".equals(propertySeparator)) {
							propertyPath = propertyPath.replaceAll(propertySeparator, ".");
						}
						Object propertyValue;
						try {
							propertyValue = EntityDtoAccessHelper.getPropertyPathValueString(entity, propertyPath, referenceDtoResolver);
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
		for (String propertyKey : propertyKeys) {
			Object property = properties.get(propertyKey);
			if (property == null || StringUtils.isBlank(property.toString())) {
				properties.setProperty(propertyKey, DEFAULT_NULL_REPLACEMENT);
			}
		}
		properties.put("F", new ObjectFormatter());
		return properties;
	}

	private byte[] generateDocumentDocx(File templateFile, Properties properties) throws IOException {
		try {
			return templateEngine.generateDocumentDocx(properties, templateFile);
		} catch (XDocReportException e) {
			throw new RuntimeException(String.format(I18nProperties.getString(Strings.errorDocumentGeneration), e.getMessage()));
		}
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
	public List<String> getAdditionalVariables(DocumentWorkflow documentWorkflow, String templateName) throws IOException {
		File templateFile = getTemplateFile(documentWorkflow, templateName);
		Set<String> propertyKeys = documentWorkflow.isDocx() ? getTemplateVariablesDocx(templateFile) : getTemplateVariablesTxt(templateFile);
		return propertyKeys.stream().filter(e -> !isEntityVariable(documentWorkflow, e)).sorted(String::compareTo).collect(Collectors.toList());
	}

	@Override
	public void writeDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName, byte[] document) throws IOException {
		if (!documentWorkflow.getFileExtension().equalsIgnoreCase(FilenameUtils.getExtension(templateName))) {
			throw new IllegalArgumentException(I18nProperties.getString(Strings.headingWrongFileType));
		}
		String path = FilenameUtils.getPath(templateName);
		if (StringUtils.isNotBlank(path)) {
			throw new IllegalArgumentException(String.format(I18nProperties.getString(Strings.errorIllegalFilename), templateName));
		}

		ByteArrayInputStream templateInputStream = new ByteArrayInputStream(document);
		if (documentWorkflow.isDocx()) {
			templateEngine.validateTemplateDocx(templateInputStream);
		} else {
			templateEngine.validateTemplateTxt(templateInputStream);
		}

		Path workflowTemplateDirPath = getWorkflowTemplateDirPath(documentWorkflow);
		Files.createDirectories(workflowTemplateDirPath);
		try (FileOutputStream fileOutputStream =
			new FileOutputStream(new File(workflowTemplateDirPath.resolve(FilenameUtils.getName(templateName)).toUri()))) {
			fileOutputStream.write(document);
		}
	}

	@Override
	public boolean deleteDocumentTemplate(DocumentWorkflow documentWorkflow, String fileName) {
		File templateFile = new File(getWorkflowTemplateDirPath(documentWorkflow).resolve(fileName).toUri());
		if (templateFile.exists() && templateFile.isFile()) {
			return templateFile.delete();
		} else {
			throw new IllegalArgumentException(String.format(I18nProperties.getString(Strings.errorFileNotFound), fileName));
		}
	}

	@Override
	public byte[] getDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName) throws IOException {
		return FileUtils.readFileToByteArray(getTemplateFile(documentWorkflow, templateName));
	}

	private File getTemplateFile(DocumentWorkflow documentWorkflow, String templateName) {
		File templateFile = new File(getWorkflowTemplateDirPath(documentWorkflow).resolve(templateName).toString());

		if (!templateFile.exists()) {
			throw new IllegalArgumentException(String.format(I18nProperties.getString(Strings.errorFileNotFound), templateName));
		}
		return templateFile;
	}

	private Set<String> getTemplateVariablesDocx(File templateFile) throws IOException {
		try {
			return templateEngine.extractTemplateVariablesDocx(templateFile);
		} catch (XDocReportException e) {
			throw new RuntimeException(String.format(I18nProperties.getString(Strings.errorProcessingTemplate), templateFile.getName()));
		}
	}

	private Set<String> getTemplateVariablesTxt(File templateFile) throws IOException {
		try {
			return templateEngine.extractTemplateVariablesTxt(templateFile);
		} catch (ParseException e) {
			throw new RuntimeException(String.format(I18nProperties.getString(Strings.errorProcessingTemplate), templateFile.getName()));
		}
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
