package de.symeda.sormas.backend.personaldata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.audit.AuditLoggerFacade;
import de.symeda.sormas.api.audit.ExternalSystemCallAuditRequest;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.personaldata.CreatableEntityDto;
import de.symeda.sormas.api.personaldata.PersonalDataAddressRequestDto;
import de.symeda.sormas.api.personaldata.PersonalDataForDisplayDto;
import de.symeda.sormas.api.personaldata.PersonalDataProviderAdapterFacade;
import de.symeda.sormas.api.personaldata.PersonalDataProviderFacade;
import de.symeda.sormas.api.personaldata.PersonalDataSearchRequest;
import de.symeda.sormas.api.personaldata.PersonalDataSearchResponse;
import de.symeda.sormas.api.personaldata.PersonalDataSummaryDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.json.ObjectMapperProvider;

@Stateless(name = "ExternalMessageFacade")
public class PersonalDataProviderFacadeEJB implements PersonalDataProviderFacade {

	private static final Logger logger = LoggerFactory.getLogger(PersonalDataProviderFacadeEJB.class);
	public static final String PERSONAL_DATA_PROVIDER = "PERSONAL_DATA_PROVIDER";

	@EJB
	private SystemConfigurationValueFacade systemConfigurationValueFacade;

	@EJB
	private AuditLoggerFacade auditLogger;

	private static final String PERSONAL_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY = "PERSONAL_DATA_PROVIDER_ADAPTER";

	@Override
	public @Nullable PersonalDataSummaryDto findDataByNationalHealthId(String nationalHealthId) {
		auditLogger.logExternalSystemCall(buildAuditRequestWithNationalHealthId("findDataByNationalHealthId", nationalHealthId));
		return getPersonalDataProvider().findDataByNationalHealthId(nationalHealthId);
	}

	private static @NotNull ExternalSystemCallAuditRequest buildAuditRequestWithNationalHealthId(String actionType, String nationalHealthId) {
		ExternalSystemCallAuditRequest request = buildAuditRequest(actionType);
		request.setDetails(Map.of("nationalHealthId", nationalHealthId));
		return request;
	}

	private static @NotNull ExternalSystemCallAuditRequest buildAuditRequest(String actionType) {
		ExternalSystemCallAuditRequest request = new ExternalSystemCallAuditRequest();
		request.setActionType(actionType);
		request.setSystemName(PERSONAL_DATA_PROVIDER);
		return request;
	}

	private static @NotNull ExternalSystemCallAuditRequest buildAuditRequestWithDetails(String actionType, Map<String, Object> details) {
		ExternalSystemCallAuditRequest request = buildAuditRequest(actionType);

		if (MapUtils.isNotEmpty(details)) {
			ObjectMapper mapper = ObjectMapperProvider.getInstance();

			request.setDetails(details.entrySet().stream().filter(entry -> entry.getValue() != null).map(entry -> {
				Object entryValue = entry.getValue();
				String typedValue;
				if (entryValue instanceof String) {
					typedValue = (String) entryValue;
				} else {
					typedValue = ObjectMapperProvider.writeValueAsStringFailSafe(entryValue);
				}
				return Tuple.of(entry.getKey(), typedValue);
			}).collect(Collectors.toMap(Tuple::getFirst, Tuple::getSecond)));
		}

		return request;
	}

	@Override
	public @Nullable PersonalDataForDisplayDto findDisplayDataByNationalHealthId(String nationalHealthId) {
		auditLogger.logExternalSystemCall(buildAuditRequestWithNationalHealthId("findDisplayDataByNationalHealthId", nationalHealthId));

		return getPersonalDataProvider().findDisplayDataByNationalHealthId(nationalHealthId);
	}

	@Override
	public @Nullable LocationDto findAppropriateDocumentAddressFor(PersonalDataAddressRequestDto request) {
		auditLogger.logExternalSystemCall(
			buildAuditRequestWithDetails("findAppropriateDocumentAddressFor", Map.of("PersonalDataAddressRequestDto", request)));

		return getPersonalDataProvider().findAppropriateDocumentAddressFor(request);
	}

	@Override
	public PersonalDataSearchResponse search(PersonalDataSearchRequest request) {
		auditLogger.logExternalSystemCall(buildAuditRequestWithDetails("search", Map.of("PersonalDataSearchRequest", request)));

		return getPersonalDataProvider().search(request);
	}

	@Override
	public List<CreatableEntityDto> getCreatableEntityDtos() {
		return getPersonalDataProvider().getCreatableEntityDtos();
	}

	@Override
	public <T extends EntityDto> T createDtoFrom(String nationalHealthId, Class<T> clazzToCreate) {
		auditLogger.logExternalSystemCall(
			buildAuditRequestWithDetails("createDtoFrom", Map.of("nationalHealthId", nationalHealthId, "clazzToCreate", clazzToCreate.getName())));

		return getPersonalDataProvider().createDtoFrom(nationalHealthId, clazzToCreate);
	}

	private PersonalDataProviderAdapterFacade getPersonalDataProvider() {
		String personalDataProviderJndi = systemConfigurationValueFacade.getValue(PERSONAL_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY);

		if (StringUtils.isBlank(personalDataProviderJndi)) {
			throw new IllegalStateException(String.format("System configuration [%s] is missing", PERSONAL_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY));
		}

		try {
			return (PersonalDataProviderAdapterFacade) new InitialContext().lookup(personalDataProviderJndi);
		} catch (NamingException e) {
			throw new RuntimeException(
				String.format("Could not look up SurveyAsExternalMessageAdapterFacade via JNDI: [%s]", personalDataProviderJndi),
				e);
		}
	}

	@LocalBean
	@Stateless
	public static class PersonalDataProviderFacadeEJBLocal extends PersonalDataProviderFacadeEJB {

	}
}
