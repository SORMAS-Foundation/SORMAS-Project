package de.symeda.sormas.backend.docgeneration.valueresolvers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import de.symeda.sormas.api.EntityDtoAccessHelper.IReferenceDtoResolver;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.backend.AbstractUnitTest;

class AbstractRootValuePropertyFillerTest extends AbstractUnitTest {

	@Mock
	private IReferenceDtoResolver resolver;

	@ParameterizedTest
	@ValueSource(strings = {
		"externalPersonData.city",
		"externalPerson_city",
		"externalPersonData.city" })
	void supportsBothSeparatorsAndCaseInsensitiveRoot(String key) {
		LocationDto address = new LocationDto();
		address.setCity("Paris");
		Properties properties = new Properties();

		filler(address).fillValueInto(key, properties);

		assertEquals("Paris", properties.getProperty(key));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"externalPersonData.region.epidCode",
		"externalPerson_region_epidCode" })
	void resolvesNestedReferences(String key) {
		LocationDto address = new LocationDto();
		RegionReferenceDto reference = new RegionReferenceDto();
		address.setRegion(reference);
		RegionDto region = new RegionDto();
		region.setEpidCode("REG-1");
		when(resolver.resolve(reference)).thenReturn(region);
		Properties properties = new Properties();

		filler(address).fillValueInto(key, properties);

		assertEquals("REG-1", properties.getProperty(key));
		verify(resolver).resolve(reference);
	}

	@Test
	void leavesMissingValuesUnset() {
		Properties properties = new Properties();
		filler(new LocationDto()).fillValueInto("externalPersonData.city", properties);
		assertFalse(properties.containsKey("externalPersonData.city"));
	}

	@Test
	void leavesNullEntityUnset() {
		Properties properties = new Properties();
		filler(null).fillValueInto("externalPersonData.city", properties);
		assertFalse(properties.containsKey("externalPersonData.city"));
	}

	@Test
	void marksInvalidPropertyWithoutChangingOtherProperties() {
		Properties properties = new Properties();
		properties.setProperty("existing", "preserved");

		filler(new LocationDto()).fillValueInto("externalPersonData.unknownProperty", properties);

		assertEquals(
			"*** Error for property: [externalPersonData.unknownProperty] ***",
			properties.getProperty("externalPersonData.unknownProperty"));
		assertEquals("preserved", properties.getProperty("existing"));
	}

	private AbstractRootValuePropertyFiller filler(HasUuid entity) {
		return new AbstractRootValuePropertyFiller(resolver) {

			@Override
			protected HasUuid getEntity() {
				return entity;
			}
		};
	}
}
