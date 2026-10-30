package de.symeda.sormas.backend.externalpersondata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.constraints.NotEmpty;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.audit.ExternalSystemCallAuditRequest;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataAddressRequestDto;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataForDisplayDto;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataProviderAdapterFacade;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataProviderFacade;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchRequest;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataSearchResponse;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataSummaryDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.audit.AuditLoggerEjb;
import de.symeda.sormas.backend.json.ObjectMapperProvider;

@Stateless(name = "ExternalPersonDataProviderFacade")
public class ExternalPersonDataProviderFacadeEJB implements ExternalPersonDataProviderFacade {

	private static final Logger logger = LoggerFactory.getLogger(ExternalPersonDataProviderFacadeEJB.class);
	public static final String EXTERNAL_PERSON_DATA_PROVIDER = "EXTERNAL_PERSON_DATA_PROVIDER";

	@EJB
	private SystemConfigurationValueFacade systemConfigurationValueFacade;

	@EJB
	private AuditLoggerEjb.AuditLoggerEjbLocal auditLogger;

	@Override
	public @Nullable ExternalPersonDataSummaryDto findDataByNationalHealthId(String nationalHealthId) {
		auditLogger.logExternalSystemCall(buildAuditRequestWithNationalHealthId("findDataByNationalHealthId", nationalHealthId));
		return getExternalPersonDataProvider().findDataByNationalHealthId(nationalHealthId);
	}

	@Override
	public Map<String, ExternalPersonDataSummaryDto> findSummariesByNationalHealthIds(List<@NotEmpty String> nationalHealthId) {
		auditLogger
			.logExternalSystemCall(buildAuditRequestWithNationalHealthId("findSummariesByNationalHealthIds", String.join(",", nationalHealthId)));
		return getExternalPersonDataProvider().findSummariesByNationalHealthIds(nationalHealthId);
	}

	private static @NotNull ExternalSystemCallAuditRequest buildAuditRequestWithNationalHealthId(String actionType, String nationalHealthId) {
		ExternalSystemCallAuditRequest request = buildAuditRequest(actionType);
		request.setDetails(Map.of("nationalHealthId", nationalHealthId));
		return request;
	}

	private static @NotNull ExternalSystemCallAuditRequest buildAuditRequest(String actionType) {
		ExternalSystemCallAuditRequest request = new ExternalSystemCallAuditRequest();
		request.setActionType(actionType);
		request.setSystemName(EXTERNAL_PERSON_DATA_PROVIDER);
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
	public @Nullable ExternalPersonDataForDisplayDto findDisplayDataByNationalHealthId(String nationalHealthId) {
		auditLogger.logExternalSystemCall(buildAuditRequestWithNationalHealthId("findDisplayDataByNationalHealthId", nationalHealthId));

		return getExternalPersonDataProvider().findDisplayDataByNationalHealthId(nationalHealthId);
	}

	@Override
	public @Nullable LocationDto findAppropriateDocumentAddressFor(ExternalPersonDataAddressRequestDto request) {
		auditLogger.logExternalSystemCall(
			buildAuditRequestWithDetails("findAppropriateDocumentAddressFor", Map.of("ExternalPersonDataAddressRequestDto", request)));

		return getExternalPersonDataProvider().findAppropriateDocumentAddressFor(request);
	}

	@Override
	public ExternalPersonDataSearchResponse search(ExternalPersonDataSearchRequest request) {
		auditLogger.logExternalSystemCall(buildAuditRequestWithDetails("search", Map.of("ExternalPersonDataSearchRequest", request)));

		return getExternalPersonDataProvider().search(request);
	}

	private ExternalPersonDataProviderAdapterFacade getExternalPersonDataProvider() {
		String externalPersonDataProviderJndi = systemConfigurationValueFacade.getValue(EXTERNAL_PERSON_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY);

		if (StringUtils.isBlank(externalPersonDataProviderJndi)) {
			throw new IllegalStateException(
				String.format("System configuration [%s] is missing", EXTERNAL_PERSON_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY));
		}

		try {
			return (ExternalPersonDataProviderAdapterFacade) new InitialContext().lookup(externalPersonDataProviderJndi);
		} catch (NamingException e) {
			throw new RuntimeException(
				String.format("Could not look up SurveyAsExternalMessageAdapterFacade via JNDI: [%s]", externalPersonDataProviderJndi),
				e);
		}
	}

	@LocalBean
	@Stateless
	public static class ExternalPersonDataProviderFacadeEJBLocal extends ExternalPersonDataProviderFacadeEJB {

	}
}
