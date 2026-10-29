package de.symeda.sormas.backend.docgeneration.valueresolvers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataAddressRequestDto;
import de.symeda.sormas.api.externalpersondata.ExternalPersonDataProviderFacade;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.AbstractUnitTest;

class ExternalPersonPropertyFillerTest extends AbstractUnitTest {

	@Mock
	private ExternalPersonDataProviderFacade provider;

	private final DocumentTemplateEntities entities = new DocumentTemplateEntities();
	private final CaseDataDto caseData = new CaseDataDto();
	private final PersonDto person = new PersonDto();
	private ExternalPersonPropertyFiller filler;

	@BeforeEach
	void setUp() {
		entities.addEntity(RootEntityType.ROOT_CASE, caseData);
		entities.addEntity(RootEntityType.ROOT_PERSON, person);
		filler = new ExternalPersonPropertyFiller(null, entities, provider);
	}

	@Test
	void fillsAddressAndPassesCaseAndPersonToProvider() {
		LocationDto address = new LocationDto();
		address.setCity("Paris");
		when(provider.findAppropriateDocumentAddressFor(any())).thenReturn(address);
		Properties properties = new Properties();

		filler.fillValueInto("externalPersonData.city", properties);

		assertEquals("Paris", properties.getProperty("externalPersonData.city"));
		ArgumentCaptor<ExternalPersonDataAddressRequestDto> request = ArgumentCaptor.forClass(ExternalPersonDataAddressRequestDto.class);
		verify(provider).findAppropriateDocumentAddressFor(request.capture());
		assertSame(caseData, request.getValue().getCaseData());
		assertSame(person, request.getValue().getPerson());
	}

	@Test
	void writesErrorMarkerWhenProviderFailsWithoutMessage() {
		when(provider.findAppropriateDocumentAddressFor(any())).thenThrow(new IllegalStateException());
		Properties properties = new Properties();

		filler.fillValueInto("externalPersonData.city", properties);

		assertEquals("*** Error for property: [externalPersonData.city] ***", properties.getProperty("externalPersonData.city"));
		verify(provider).findAppropriateDocumentAddressFor(any());
	}

	@Test
	void fetchesAddressOnlyOnceForMultipleProperties() {
		LocationDto address = new LocationDto();
		address.setCity("Paris");
		address.setStreet("Main Street");
		when(provider.findAppropriateDocumentAddressFor(any())).thenReturn(address);
		Properties properties = new Properties();

		filler.fillValueInto("externalPersonData.city", properties);
		filler.fillValueInto("externalPersonData.street", properties);
		filler.fillValueInto("externalPersonData.city", properties);

		assertEquals("Paris", properties.getProperty("externalPersonData.city"));
		assertEquals("Main Street", properties.getProperty("externalPersonData.street"));
		verify(provider).findAppropriateDocumentAddressFor(any());
		verifyNoMoreInteractions(provider);
	}

	@Test
	void doesNotFetchAddressUntilAPropertyIsRequested() {
		verifyNoInteractions(provider);
	}
}
