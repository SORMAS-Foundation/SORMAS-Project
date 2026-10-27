package de.symeda.sormas.backend.docgeneration;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import de.symeda.sormas.api.EntityDtoAccessHelper;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.personaldata.PersonalDataAddressRequestDto;
import de.symeda.sormas.api.personaldata.PersonalDataProviderFacade;

public class ExternalPersonValueResolver implements RootEntityValueResolver {

	private static final Pattern BASENAME_PATTERN = Pattern.compile("^([^_.]+)([_.].*)?");

	/**
	 * Lazy-loaded
	 */
	@Nullable
	private LocationDto address;

	private boolean addressFetched;

	private final DocumentTemplateEntities entities;

	// TODO: this must be set
	private PersonalDataProviderFacade personalDataProviderFacade;

	public ExternalPersonValueResolver(DocumentTemplateEntities entities) {
		this.entities = entities;
	}

	@Override
	public ExternalPersonValueResolver build(DocumentTemplateEntities entities) {
		return new ExternalPersonValueResolver(entities);
	}

	@Override
	public void fillValues(Properties properties, String propertyKey) {
		String propertySeparator = propertyKey.contains("_") ? "_" : ".";

		String variableBaseName = getVariableBaseName(propertyKey);

		String propertyPath = buildPropertyPath(propertyKey, variableBaseName, propertySeparator);

		Object propertyValue;
		try {
			propertyValue = EntityDtoAccessHelper.getPropertyPathValueString(getAddress(), propertyPath, null);
		} catch (Exception e) {
			propertyValue = "*** " + e.getMessage().replaceAll("(Reference)?Dto$", "") + " ***";
		}
		if (propertyValue != null) {
			properties.put(propertyKey, propertyValue);
		}
	}

	private static @NotNull String buildPropertyPath(String propertyKey, String variableBaseName, String propertySeparator) {
		String propertyPath = propertyKey.replaceFirst("(?i)" + variableBaseName + "[" + propertySeparator + "]", "");
		if (!".".equals(propertySeparator)) {
			propertyPath = propertyPath.replaceAll(propertySeparator, ".");
		}
		return propertyPath;
	}

	private LocationDto getAddress() {
		if (addressFetched) {
			return address;
		}

		PersonalDataAddressRequestDto request = new PersonalDataAddressRequestDto();
		request.setCaseData((CaseDataDto) entities.getEntity(RootEntityType.ROOT_CASE));
		request.setPerson((PersonDto) entities.getEntity(RootEntityType.ROOT_PERSON));
		address = personalDataProviderFacade.findAppropriateDocumentAddressFor(request);
		addressFetched = true;

		return address;
	}

	private String getVariableBaseName(String propertyKey) {
		String propertyKeyLowerCase = propertyKey.toLowerCase();
		Matcher matcher = BASENAME_PATTERN.matcher(propertyKeyLowerCase);
		return matcher.matches() ? matcher.group(1) : "";
	}

	public RootEntityType handledType() {
		return RootEntityType.ROOT_EXTERNAL_PERSON;
	}
}
