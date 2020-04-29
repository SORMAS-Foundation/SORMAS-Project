package de.symeda.sormas.backend.visit;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.*;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * The class VisitFacadeEjbTest.
 */
public class VisitFacadeEjbTest  extends AbstractBeanTest {

    @Test
    public void testCreateExternalVisit() {
        TestDataCreator.RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
        UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ext", "Vis", UserRole.REST_EXTERNAL_VISITS_USER);
        PersonDto cazePerson = creator.createPerson("Case", "Person");
        CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
          InvestigationStatus.PENDING, new Date(), rdcf);
        PersonDto contactPerson = creator.createPerson("Contact", "Person");
        ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date());

        final ExternalVisitDto externalVisitDto = new ExternalVisitDto();
        final String contactUuid = contact.getUuid();
        externalVisitDto.setContactUuid(contactUuid);
        externalVisitDto.setVisitDateTime(new Date());
        externalVisitDto.setVisitStatus(VisitStatus.COOPERATIVE);
        final String visitRemarks = "Everything good";
        externalVisitDto.setVisitRemarks(visitRemarks);

        final VisitFacade visitFacade = getVisitFacade();
        visitFacade.saveExternalVisit(externalVisitDto);

        final VisitCriteria visitCriteria = new VisitCriteria();
        final List<VisitIndexDto> visitIndexList = visitFacade.getIndexList(visitCriteria.contact(new ContactReferenceDto(contact.getUuid())), 0, 100, null);
        Assert.assertNotNull(visitIndexList);
        Assert.assertEquals(1, visitIndexList.size());
        VisitIndexDto visitIndexDto = visitIndexList.get(0);
        Assert.assertNotNull(visitIndexDto.getVisitDateTime());
        Assert.assertEquals(VisitStatus.COOPERATIVE, visitIndexDto.getVisitStatus());
        Assert.assertEquals(visitRemarks, visitIndexDto.getVisitRemarks());
    }

    @Test
    public void testExportVisit() {
        TestDataCreator.RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
        UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
        PersonDto cazePerson = creator.createPerson("Case", "Person");
        CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
                InvestigationStatus.PENDING, new Date(), rdcf);

        PersonDto contactPerson = creator.createPerson("Contact", "Person");
        ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference()
                , caze, new Date(), new Date());
        VisitDto visit = creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE);
        visit.getSymptoms().setAbdominalPain(SymptomState.YES);
        getVisitFacade().saveVisit(visit);
        VisitDto visit2 = creator.createVisit(caze.getDisease(), contactPerson.toReference(), new Date(), VisitStatus.COOPERATIVE);
        visit2.getSymptoms().setAgitation(SymptomState.YES);
        getVisitFacade().saveVisit(visit2);

        final ContactReferenceDto contactReferenceDto = new ContactReferenceDto(contact.getUuid());
        final VisitCriteria visitCriteria = new VisitCriteria();
        visitCriteria.contact(contactReferenceDto);
        final List<VisitExportDto> visitsExportList = getVisitFacade().getVisitsExportList(visitCriteria,
                VisitExportType.CONTACT_VISITS, 0, 10, null);

        Assert.assertNotNull(visitsExportList);
        Assert.assertEquals(2, visitsExportList.size());

        final VisitExportDto visitExportDto1 = visitsExportList.get(1);
        Assert.assertEquals(visit.getUuid(), visitExportDto1.getUuid());
        Assert.assertEquals("Contact", visitExportDto1.getFirstName());
        Assert.assertEquals("Person", visitExportDto1.getLastName());
        Assert.assertEquals("EVD", visitExportDto1.getDiseaseFormatted());
        Assert.assertEquals(VisitStatus.COOPERATIVE, visitExportDto1.getVisitStatus());
        Assert.assertEquals(SymptomState.YES, visitExportDto1.getSymptoms().getAbdominalPain());

        final VisitExportDto visitExportDto2 = visitsExportList.get(0);
        Assert.assertEquals(visit2.getUuid(), visitExportDto2.getUuid());
        Assert.assertEquals("Contact", visitExportDto2.getFirstName());
        Assert.assertEquals("Person", visitExportDto2.getLastName());
        Assert.assertEquals("EVD", visitExportDto2.getDiseaseFormatted());
        Assert.assertEquals(VisitStatus.COOPERATIVE, visitExportDto2.getVisitStatus());
        Assert.assertEquals(SymptomState.YES, visitExportDto2.getSymptoms().getAgitation());
    }
}
