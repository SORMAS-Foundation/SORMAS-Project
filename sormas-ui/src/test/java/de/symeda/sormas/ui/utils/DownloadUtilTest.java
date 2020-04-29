package de.symeda.sormas.ui.utils;

import com.vaadin.server.StreamResource;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class DownloadUtilTest extends AbstractBeanTest {

    @Test
    public void testCreateContactVisitsExport() throws IOException {

        TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
        UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
                "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
        String userUuid = user.getUuid();
        PersonDto cazePerson = creator.createPerson("Case", "Person");
        CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
                InvestigationStatus.PENDING, new Date(), rdcf);

        PersonDto contactPerson = creator.createPerson("Contact", "Person");
        ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference()
                , caze, new Date(), new Date());
        VisitDto visit = creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE);
        visit.getSymptoms().setAbdominalPain(SymptomState.YES);
        FacadeProvider.getVisitFacade().saveVisit(visit);

        PersonDto contactPerson2 = creator.createPerson("Contact2", "Person2");
        ContactDto contact2 = creator.createContact(user.toReference(), user.toReference(), contactPerson2.toReference()
                , caze, new Date(), null);
        VisitDto visit21 = creator.createVisit(caze.getDisease(), contactPerson2.toReference(), new Date(), VisitStatus.COOPERATIVE);
        visit21.getSymptoms().setAbdominalPain(SymptomState.YES);
        FacadeProvider.getVisitFacade().saveVisit(visit21);
        VisitDto visit22 = creator.createVisit(caze.getDisease(), contactPerson2.toReference(), new Date(), VisitStatus.COOPERATIVE);
        visit22.getSymptoms().setAgitation(SymptomState.YES);
        FacadeProvider.getVisitFacade().saveVisit(visit22);

        // this visit is older than the allowed offset days - should not affect our export
        VisitDto visit23 = creator.createVisit(caze.getDisease(), contactPerson2.toReference(),
                DateHelper.subtractDays(new Date(), VisitDto.ALLOWED_CONTACT_DATE_OFFSET + 1), VisitStatus.COOPERATIVE);
        visit23.getSymptoms().setAgitation(SymptomState.YES);
        FacadeProvider.getVisitFacade().saveVisit(visit23);

        PersonDto contactPerson3 = creator.createPerson("Contact3", "Person3");
        ContactDto contact3 = creator.createContact(user.toReference(), user.toReference(), contactPerson3.toReference()
                , caze, new Date(), new Date());
        for (int i=0;i<3;i++){
            creator.createVisit(caze.getDisease(), contactPerson3.toReference(), new Date(), VisitStatus.COOPERATIVE);
        }

        StreamResource contactVisitsExport = DownloadUtil.createContactVisitsExport(new ContactCriteria(),
                "test_contact_follow_up_export.csv");

        Assert.assertNotNull(contactVisitsExport);
        Assert.assertEquals("test_contact_follow_up_export.csv", contactVisitsExport.getStream().getFileName());
        InputStream stream = contactVisitsExport.getStream().getStream();

        final String shortDate = DateHelper.formatLocalShortDate(new Date());

        Assert.assertEquals("\"Contact ID\",\"First name\",\"Last name\",\"Date and time of visit\",\"Person available and " +
                "cooperative?\",\"Symptoms\",\"Date and time of visit\",\"Person available and cooperative?\"," +
                "\"Symptoms\",\"Date and time of visit\",\"Person available and cooperative?\",\"Symptoms\"\n" +
                "\"\",\"\",\"\",\"Day 1\",\"Day 1\",\"Day 1\",\"Day 2\",\"Day 2\",\"Day 2\",\"Day 3\",\"Day 3\",\"Day" +
                " 3\"\n" +
                "\""+contact.getUuid()+"\",\"Contact\",\"Person\",\""+shortDate+"\",\"Available and cooperative\"," +
                "\"Abdominal pain\",,,,,,\n" +
                "\""+contact2.getUuid()+"\",\"Contact2\",\"Person2\",\""+shortDate+"\",\"Available and " +
                "cooperative\",\"Abdominal pain\",\""+shortDate+"\",\"Available and cooperative\",\"\",,,\n" +
                "\""+contact3.getUuid()+"\",\"Contact3\",\"Person3\",\""+shortDate+"\",\"Available and " +
                "cooperative\",\"\",\""+shortDate+"\",\"Available and cooperative\",\"\",\""+shortDate+"\",\"Available and " +
                "cooperative\",\"\"\n", IOUtils.toString(stream, StandardCharsets.UTF_8.name()));

    }
}