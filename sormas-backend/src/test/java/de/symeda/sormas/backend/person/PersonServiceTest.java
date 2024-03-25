package de.symeda.sormas.backend.person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class PersonServiceTest extends AbstractBeanTest {

	@Test
	public void testIsPermittedAssociation() {
		FeatureConfigurationIndexDto featureConfiguration =
				new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.TRAVEL_ENTRIES);

		assertTrue(getPersonService().isPermittedAssociation(PersonAssociation.ALL));
		assertTrue(getPersonService().isPermittedAssociation(PersonAssociation.CASE));
		assertTrue(getPersonService().isPermittedAssociation(PersonAssociation.CONTACT));
		assertTrue(getPersonService().isPermittedAssociation(PersonAssociation.EVENT_PARTICIPANT));
		assertTrue(getPersonService().isPermittedAssociation(PersonAssociation.IMMUNIZATION));
		assertThat(
			getPersonService().getPermittedAssociations(),
			contains(
				PersonAssociation.ALL,
				PersonAssociation.CASE,
				PersonAssociation.CONTACT,
				PersonAssociation.EVENT_PARTICIPANT,
				PersonAssociation.IMMUNIZATION));

		// TravelEntry only active for Germany
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_GERMANY);
		assertTrue(getPersonService().isPermittedAssociation(PersonAssociation.TRAVEL_ENTRY));
		assertThat(
			getPersonService().getPermittedAssociations(),
			contains(
				PersonAssociation.ALL,
				PersonAssociation.CASE,
				PersonAssociation.CONTACT,
				PersonAssociation.EVENT_PARTICIPANT,
				PersonAssociation.IMMUNIZATION,
				PersonAssociation.TRAVEL_ENTRY));
	}
}
