package de.symeda.sormas.backend.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.RequestContextTO;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class AdoServiceWithUserFilterTest extends AbstractBeanTest {

	@Test
	public void testGetObsoleteUuidsSince() {

		createFeatureConfiguration(FeatureType.LIMITED_SYNCHRONIZATION, true, Map.of(FeatureTypeProperty.EXCLUDE_NO_CASE_CLASSIFIED_CASES, true));

		RequestContextHolder.setRequestContext(new RequestContextTO(true)); // simulate mobile call

		Date startDate = new Date();

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceOfficer(rdcf);
		loginWith(user);
		PersonDto person = creator.createPerson();
		CaseDataDto caze1 = creator.createCase(user.toReference(), person.toReference(), rdcf);
		CaseDataDto caze2 = creator.createCase(user.toReference(), person.toReference(), rdcf);
		CaseDataDto caze3 = creator.createCase(user.toReference(), person.toReference(), rdcf, c -> c.setCreationVersion("1.73.0"));
		ContactDto contactCaze3 = creator.createContact(user.toReference(), person.toReference(), caze3);
		CaseDataDto caze4 = creator.createCase(user.toReference(), person.toReference(), rdcf);
		ContactDto contactCaze4 = creator.createContact(user.toReference(), person.toReference(), caze4);

		assertEquals(0, getCaseFacade().getObsoleteUuidsSince(startDate).size());

		getCaseFacade().archive(caze1.getUuid(), new Date());
		assertEquals(1, getCaseFacade().getObsoleteUuidsSince(startDate).size());

		getCaseFacade().delete(caze2.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "Test"));
		assertEquals(2, getCaseFacade().getObsoleteUuidsSince(startDate).size());

		caze3.setCaseClassification(CaseClassification.NO_CASE);
		getCaseFacade().save(caze3);
		assertEquals(2, getCaseFacade().getObsoleteUuidsSince(startDate).size());

		caze4.setCaseClassification(CaseClassification.NO_CASE);
		getCaseFacade().save(caze4);
		assertEquals(3, getCaseFacade().getObsoleteUuidsSince(startDate).size());
		assertEquals(1, getContactFacade().getObsoleteUuidsSince(startDate).size());
	}

}
