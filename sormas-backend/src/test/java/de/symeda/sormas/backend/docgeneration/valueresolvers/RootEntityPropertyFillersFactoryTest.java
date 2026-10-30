package de.symeda.sormas.backend.docgeneration.valueresolvers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateEntities;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.externalpersondata.ExternalPersonDataProviderFacadeEJB.ExternalPersonDataProviderFacadeEJBLocal;

class RootEntityPropertyFillersFactoryTest extends AbstractUnitTest {

	@Mock
	private ExternalPersonDataProviderFacadeEJBLocal provider;

	@InjectMocks
	private RootEntityPropertyFillersFactory factory;

	@Test
	void createsIndependentFillersForSeparateDocuments() {
		LocationDto firstAddress = new LocationDto();
		firstAddress.setCity("Paris");
		LocationDto secondAddress = new LocationDto();
		secondAddress.setCity("Lyon");
		when(provider.findAppropriateDocumentAddressFor(any())).thenReturn(firstAddress, secondAddress);
		RootEntityPropertyFiller first =
			factory.createPropertyFiller(RootEntityType.ROOT_EXTERNAL_PERSON_DATA, new DocumentTemplateEntities(), reference -> null);
		RootEntityPropertyFiller second =
			factory.createPropertyFiller(RootEntityType.ROOT_EXTERNAL_PERSON_DATA, new DocumentTemplateEntities(), reference -> null);
		Properties firstProperties = new Properties();
		Properties secondProperties = new Properties();

		first.fillValueInto("externalPersonData.city", firstProperties);
		second.fillValueInto("externalPersonData.city", secondProperties);
		first.fillValueInto("externalPersonData.city", firstProperties);

		assertNotSame(first, second);
		assertEquals("Paris", firstProperties.getProperty("externalPersonData.city"));
		assertEquals("Lyon", secondProperties.getProperty("externalPersonData.city"));
		verify(provider, times(2)).findAppropriateDocumentAddressFor(any());
	}

	@Test
	void rejectsDataModelEntities() {
		assertThrows(
			IllegalStateException.class,
			() -> factory.createPropertyFiller(RootEntityType.ROOT_CASE, new DocumentTemplateEntities(), reference -> null));
	}

	@Test
	void rejectsUnsupportedSyntheticEntities() {
		assertThrows(
			IllegalStateException.class,
			() -> factory.createPropertyFiller(RootEntityType.ROOT_GENERAL, new DocumentTemplateEntities(), reference -> null));
	}
}
