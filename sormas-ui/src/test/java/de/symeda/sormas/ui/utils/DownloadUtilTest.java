package de.symeda.sormas.ui.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.vaadin.server.StreamResource;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.AbstractUiBeanTest;

public class DownloadUtilTest extends AbstractUiBeanTest {

	@Test
	public void testCreateVisitsExportStreamResource() throws IOException {

		var rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator
			.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), caze.getDisease());
		VisitDto visit = creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit.getSymptoms().setAbdominalPain(SymptomState.YES);
		FacadeProvider.getVisitFacade().save(visit);

		PersonDto contactPerson2 = creator.createPerson("Contact2", "Person2");
		ContactDto contact2 =
			creator.createContact(user.toReference(), user.toReference(), contactPerson2.toReference(), caze, new Date(), null, caze.getDisease());
		VisitDto visit21 =
			creator.createVisit(caze.getDisease(), contactPerson2.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit21.getSymptoms().setAbdominalPain(SymptomState.YES);
		FacadeProvider.getVisitFacade().save(visit21);
		VisitDto visit22 =
			creator.createVisit(caze.getDisease(), contactPerson2.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		visit22.getSymptoms().setAgitation(SymptomState.YES);
		FacadeProvider.getVisitFacade().save(visit22);

		// this visit is older than the allowed offset days - should not affect our export
		VisitDto visit23 = creator.createVisit(
			caze.getDisease(),
			contactPerson2.toReference(),
			DateHelper.subtractDays(new Date(), FollowUpLogic.ALLOWED_DATE_OFFSET + 1),
			VisitStatus.COOPERATIVE,
			VisitOrigin.USER);
		visit23.getSymptoms().setAgitation(SymptomState.YES);
		FacadeProvider.getVisitFacade().save(visit23);

		PersonDto contactPerson3 = creator.createPerson("Contact3", "Person3");
		ContactDto contact3 = creator
			.createContact(user.toReference(), user.toReference(), contactPerson3.toReference(), caze, new Date(), new Date(), caze.getDisease());
		for (int i = 0; i < 3; i++) {
			creator.createVisit(caze.getDisease(), contactPerson3.toReference(), new Date(), VisitStatus.COOPERATIVE, VisitOrigin.USER);
		}

		StreamResource contactVisitsExport =
			DownloadUtil.createVisitsExportStreamResource(new ContactCriteria(), Collections::emptySet, ExportEntityName.CONTACT_FOLLOW_UPS);

		String expectedFileName = DownloadUtil.createFileNameWithCurrentDate(ExportEntityName.CONTACT_FOLLOW_UPS, ".csv");
		assertNotNull(contactVisitsExport);
		assertEquals(expectedFileName, contactVisitsExport.getStream().getFileName());
		InputStream stream = contactVisitsExport.getStream().getStream();

		final String shortDate = DateFormatHelper.formatDate(new Date());

		assertEquals(
			"\"Contact ID\",\"First name\",\"Last name\",\"Date and time of visit\",\"Person available and "
				+ "cooperative?\",\"Symptoms\",\"Date and time of visit\",\"Person available and cooperative?\","
				+ "\"Symptoms\",\"Date and time of visit\",\"Person available and cooperative?\",\"Symptoms\"\n"
				+ "\"\",\"\",\"\",\"Day 1\",\"Day 1\",\"Day 1\",\"Day 2\",\"Day 2\",\"Day 2\",\"Day 3\",\"Day 3\",\"Day" + " 3\"\n" + "\""
				+ contact.getUuid() + "\",\"Contact\",\"Person\",\"" + shortDate + "\",\"Available and cooperative\"," + "\"Abdominal pain\",,,,,,\n"
				+ "\"" + contact2.getUuid() + "\",\"Contact2\",\"Person2\",\"" + shortDate + "\",\"Available and "
				+ "cooperative\",\"Abdominal pain\",\"" + shortDate + "\",\"Available and cooperative\",\"\",,,\n" + "\"" + contact3.getUuid()
				+ "\",\"Contact3\",\"Person3\",\"" + shortDate + "\",\"Available and " + "cooperative\",\"\",\"" + shortDate
				+ "\",\"Available and cooperative\",\"\",\"" + shortDate + "\",\"Available and " + "cooperative\",\"\"\n",
			IOUtils.toString(stream, StandardCharsets.UTF_8.name()));
	}
}
