package de.symeda.sormas.backend.externalpersondata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fasterxml.jackson.core.JsonProcessingException;

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
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.audit.AuditLoggerEjb;

class ExternalPersonDataProviderFacadeEJBTest extends AbstractBeanTest {

	@InjectMocks
	private ExternalPersonDataProviderFacadeEJB victim;

	@Mock
	private SystemConfigurationValueFacade systemConfigurationValueFacade;

	@Mock
	private AuditLoggerEjb.AuditLoggerEjbLocal auditLogger;

	@Mock
	private ExternalPersonDataProviderAdapterFacade adapter;

	@Test
	void findsSummaryAndAuditsNationalHealthId() throws NamingException {
		// PREPARE
		ExternalPersonDataSummaryDto summary = new ExternalPersonDataSummaryDto();
		when(adapter.findDataByNationalHealthId("123")).thenReturn(summary);
		mockProviderLookup();

		// EXECUTE
		ExternalPersonDataSummaryDto result = victim.findDataByNationalHealthId("123");

		// CHECK
		assertSame(summary, result);
		verify(adapter).findDataByNationalHealthId("123");
		assertAudit("findDataByNationalHealthId", Map.of("nationalHealthId", "123"));
	}

	@Test
	void findsSummariesAndAuditsAllNationalHealthIds() throws NamingException {
		// PREPARE
		List<String> ids = List.of("123", "456");
		Map<String, ExternalPersonDataSummaryDto> summaries = Map.of("123", new ExternalPersonDataSummaryDto());
		when(adapter.findSummariesByNationalHealthIds(ids)).thenReturn(summaries);
		mockProviderLookup();

		// EXECUTE
		Map<String, ExternalPersonDataSummaryDto> result = victim.findSummariesByNationalHealthIds(ids);

		// CHECK
		assertSame(summaries, result);
		verify(adapter).findSummariesByNationalHealthIds(ids);
		assertAudit("findSummariesByNationalHealthIds", Map.of("nationalHealthId", "123,456"));
	}

	@Test
	void findsDisplayDataAndAuditsNationalHealthId() throws NamingException {
		// PREPARE
		ExternalPersonDataForDisplayDto displayData = new ExternalPersonDataForDisplayDto();
		when(adapter.findDisplayDataByNationalHealthId("123")).thenReturn(displayData);
		mockProviderLookup();

		// EXECUTE
		ExternalPersonDataForDisplayDto result = victim.findDisplayDataByNationalHealthId("123");

		// CHECK
		assertSame(displayData, result);
		verify(adapter).findDisplayDataByNationalHealthId("123");
		assertAudit("findDisplayDataByNationalHealthId", Map.of("nationalHealthId", "123"));
	}

	@Test
	void findsDocumentAddressAndAuditsRequest() throws NamingException {
		// PREPARE
		ExternalPersonDataAddressRequestDto request = new ExternalPersonDataAddressRequestDto();
		LocationDto address = new LocationDto();
		when(adapter.findAppropriateDocumentAddressFor(request)).thenReturn(address);
		mockProviderLookup();

		// EXECUTE
		LocationDto result = victim.findAppropriateDocumentAddressFor(request);

		// CHECK
		assertSame(address, result);
		verify(adapter).findAppropriateDocumentAddressFor(request);
		assertAudit("findAppropriateDocumentAddressFor", Map.of("ExternalPersonDataAddressRequestDto", request.toString()));
	}

	@Test
	void searchesAndAuditsRequest() throws NamingException {
		// PREPARE
		ExternalPersonDataSearchRequest request = new ExternalPersonDataSearchRequest();
		request.setLastName("Smith");
		request.setOffset(10);
		request.setLimit(5);
		ExternalPersonDataSearchResponse response = new ExternalPersonDataSearchResponse();
		when(adapter.find(request)).thenReturn(response);
		mockProviderLookup();

		// EXECUTE
		ExternalPersonDataSearchResponse result = victim.find(request);

		// CHECK
		assertSame(response, result);
		verify(adapter).find(request);
		assertAudit("search", Map.of("ExternalPersonDataSearchRequest", request.toString()));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {
		"false",
		"invalid" })
	void rejectsDisabledProvider(String enabled) {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_PROVIDER_ENABLED_KEY)).thenReturn(enabled);

		// EXECUTE
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> victim.findDataByNationalHealthId("123"));

		// CHECK
		assertEquals(
			String.format(
				"System configuration [%s] is not marked as true (enabled)",
				ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_PROVIDER_ENABLED_KEY),
			exception.getMessage());
		verifyNoInteractions(adapter);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = "   ")
	void rejectsMissingProviderJndi(String jndi) {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_PROVIDER_ENABLED_KEY)).thenReturn("true");
		when(systemConfigurationValueFacade.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY))
			.thenReturn(jndi);

		// EXECUTE
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> victim.findDataByNationalHealthId("123"));

		// CHECK
		assertEquals(
			String.format(
				"System configuration [%s] is missing",
				ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY),
			exception.getMessage());
		verifyNoInteractions(adapter);
	}

	@Test
	void preservesJndiLookupFailureCause() throws NamingException {
		// PREPARE
		configureProvider();
		NamingException failure = new NamingException("Provider unavailable");
		when(InitialContext.doLookup("providerJndi")).thenThrow(failure);

		// EXECUTE
		RuntimeException exception = assertThrows(RuntimeException.class, () -> victim.findDataByNationalHealthId("123"));

		// CHECK
		assertSame(failure, exception.getCause());
		verifyNoInteractions(adapter);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = "   ")
	void usesDefaultHtmlAttributesForBlankConfiguration(String configuration) {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_HTML_SANITIZATION_CONFIG_KEY))
			.thenReturn(configuration);

		// EXECUTE
		Map<String, Set<String>> result = victim.getAllowedAttributesByTag();

		// CHECK
		assertEquals(
			Map.of(
				"dt",
				Set.of("style"),
				"dl",
				Set.of("style"),
				"dd",
				Set.of("style"),
				"h3",
				Set.of("style"),
				"section",
				Set.of("style"),
				"div",
				Set.of("style")),
			result);
		verifyNoInteractions(auditLogger, adapter);
	}

	@Test
	void readsConfiguredHtmlAttributes() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_HTML_SANITIZATION_CONFIG_KEY))
			.thenReturn("{\"a\":[\"href\",\"title\"],\"p\":[]}");

		// EXECUTE
		Map<String, Set<String>> result = victim.getAllowedAttributesByTag();

		// CHECK
		assertEquals(Map.of("a", Set.of("href", "title"), "p", Set.of()), result);
	}

	@Test
	void allowsEmptyHtmlAttributeConfiguration() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_HTML_SANITIZATION_CONFIG_KEY))
			.thenReturn("{}");

		// EXECUTE
		Map<String, Set<String>> result = victim.getAllowedAttributesByTag();

		// CHECK
		assertEquals(Map.of(), result);
	}

	@Test
	void rejectsMalformedHtmlAttributeConfiguration() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_HTML_SANITIZATION_CONFIG_KEY))
			.thenReturn("invalid json");

		// EXECUTE
		RuntimeException exception = assertThrows(RuntimeException.class, () -> victim.getAllowedAttributesByTag());

		// CHECK
		assertInstanceOf(JsonProcessingException.class, exception.getCause());
	}

	private void configureProvider() {
		when(systemConfigurationValueFacade.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_PROVIDER_ENABLED_KEY)).thenReturn("true");
		when(systemConfigurationValueFacade.getValue(ExternalPersonDataProviderFacade.EXTERNAL_PERSON_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY))
			.thenReturn("providerJndi");
	}

	private void mockProviderLookup() throws NamingException {
		configureProvider();
		when(InitialContext.doLookup("providerJndi")).thenReturn(adapter);
	}

	private void assertAudit(String action, Map<String, String> details) {
		ArgumentCaptor<ExternalSystemCallAuditRequest> request = ArgumentCaptor.forClass(ExternalSystemCallAuditRequest.class);
		verify(auditLogger).logExternalSystemCall(request.capture());
		assertEquals(ExternalPersonDataProviderFacadeEJB.EXTERNAL_PERSON_DATA_PROVIDER, request.getValue().getSystemName());
		assertEquals(action, request.getValue().getActionType());
		assertEquals(details, request.getValue().getDetails());
	}
}
