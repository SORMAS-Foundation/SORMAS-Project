package de.symeda.sormas.backend.docgeneration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataProviderFacade;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.docgeneration.valueresolvers.ExternalPersonDataPropertyFiller;
import de.symeda.sormas.backend.docgeneration.valueresolvers.RootEntityPropertyFillersFactory;

class DocumentTemplatePropertiesTest extends AbstractUnitTest {

	@Mock
	private ConfigFacadeEjbLocal configFacade;
	@Mock
	private RootEntityPropertyFillersFactory propertyFillersFactory;
	@Mock
	private ExternalPersonDataProviderFacade provider;
	@Mock
	private DocumentWorkflow workflow;
	@InjectMocks
	private DocumentTemplateFacadeEjb facade;

	private final DocumentTemplateEntities entities = new DocumentTemplateEntities();

	@BeforeEach
	void setUp() {
		when(workflow.getRootEntityTypes()).thenReturn(Set.of(RootEntityType.ROOT_EXTERNAL_PERSON_DATA));
		when(configFacade.getDocgenerationNullReplacement()).thenReturn("missing");
	}

	@Test
	void fillsExternalPersonPropertiesSuccessfully() throws Exception {
		enableExternalPersonFiller();
		LocationDto address = new LocationDto();
		address.setCity("Paris");
		when(provider.findAppropriateDocumentAddressFor(any())).thenReturn(address);

		Properties properties = prepareProperties(true, "externalPersonData.city");

		assertEquals("Paris", properties.getProperty("externalPersonData.city"));
	}

	@Test
	void preservesErrorMarkerWhenExternalProviderFails() throws Exception {
		enableExternalPersonFiller();
		when(provider.findAppropriateDocumentAddressFor(any())).thenThrow(new IllegalStateException());

		Properties properties = prepareProperties(true, "externalPersonData.city");

		assertEquals("*** Error for property: [externalPersonData.city] ***", properties.getProperty("externalPersonData.city"));
	}

	@Test
	void reusesOneFillerAndOneExternalLookupAcrossPlaceholders() throws Exception {
		enableExternalPersonFiller();
		LocationDto address = new LocationDto();
		address.setCity("Paris");
		address.setStreet("Main Street");
		when(provider.findAppropriateDocumentAddressFor(any())).thenReturn(address);

		Properties properties = prepareProperties(false, "externalPerson_city", "externalPerson_street");

		assertEquals("Paris", properties.getProperty("externalPerson_city"));
		assertEquals("Main Street", properties.getProperty("externalPerson_street"));
		verify(propertyFillersFactory).createPropertyFiller(eq(RootEntityType.ROOT_EXTERNAL_PERSON_DATA), eq(entities), any());
		verify(provider).findAppropriateDocumentAddressFor(any());
	}

	@Test
	void createsNewCacheForEachDocument() throws Exception {
		enableExternalPersonFiller();
		LocationDto first = new LocationDto();
		first.setCity("Paris");
		LocationDto second = new LocationDto();
		second.setCity("Lyon");
		when(provider.findAppropriateDocumentAddressFor(any())).thenReturn(first, second);

		assertEquals("Paris", prepareProperties(true, "externalPersonData.city").getProperty("externalPersonData.city"));
		assertEquals("Lyon", prepareProperties(true, "externalPersonData.city").getProperty("externalPersonData.city"));
		verify(propertyFillersFactory, times(2)).createPropertyFiller(eq(RootEntityType.ROOT_EXTERNAL_PERSON_DATA), eq(entities), any());
		verify(provider, times(2)).findAppropriateDocumentAddressFor(any());
	}

	@Test
	void skipsExternalPersonWhenWorkflowDoesNotAllowIt() throws Exception {
		when(workflow.getRootEntityTypes()).thenReturn(Set.of(RootEntityType.ROOT_CASE));

		assertEquals("missing", prepareProperties(true, "externalPersonData.city").getProperty("externalPersonData.city"));
		verifyNoInteractions(propertyFillersFactory, provider);
	}

	private void enableExternalPersonFiller() {
		when(propertyFillersFactory.createPropertyFiller(eq(RootEntityType.ROOT_EXTERNAL_PERSON_DATA), eq(entities), any()))
			.thenAnswer(invocation -> new ExternalPersonDataPropertyFiller(invocation.getArgument(2), entities, provider));
	}

	private Properties prepareProperties(boolean docx, String... keys) throws Exception {
		when(workflow.isDocx()).thenReturn(docx);
		DocumentVariables variables = new DocumentVariables(new LinkedHashSet<>(Arrays.asList(keys)), Collections.emptySet());
		Method method = DocumentTemplateFacadeEjb.class.getDeclaredMethod(
			"prepareProperties",
			DocumentWorkflow.class,
			DocumentTemplateEntities.class,
			Properties.class,
			DocumentVariables.class);
		method.setAccessible(true);
		return (Properties) method.invoke(facade, workflow, entities, null, variables);
	}
}
