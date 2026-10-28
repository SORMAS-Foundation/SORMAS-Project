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
import de.symeda.sormas.backend.personaldata.PersonalDataProviderFacadeEJB.PersonalDataProviderFacadeEJBLocal;

class RootEntityPropertyFillersFactoryTest extends AbstractUnitTest {

	@Mock
	private PersonalDataProviderFacadeEJBLocal provider;

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
			factory.createPropertyFiller(RootEntityType.ROOT_EXTERNAL_PERSON, new DocumentTemplateEntities(), reference -> null);
		RootEntityPropertyFiller second =
			factory.createPropertyFiller(RootEntityType.ROOT_EXTERNAL_PERSON, new DocumentTemplateEntities(), reference -> null);
		Properties firstProperties = new Properties();
		Properties secondProperties = new Properties();

		first.fillValueInto("externalPerson.city", firstProperties);
		second.fillValueInto("externalPerson.city", secondProperties);
		first.fillValueInto("externalPerson.city", firstProperties);

		assertNotSame(first, second);
		assertEquals("Paris", firstProperties.getProperty("externalPerson.city"));
		assertEquals("Lyon", secondProperties.getProperty("externalPerson.city"));
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
