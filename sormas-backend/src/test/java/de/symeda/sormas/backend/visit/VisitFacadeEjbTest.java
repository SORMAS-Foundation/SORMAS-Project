package de.symeda.sormas.backend.visit;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.ExternalVisitDto;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitStatus;
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
}
