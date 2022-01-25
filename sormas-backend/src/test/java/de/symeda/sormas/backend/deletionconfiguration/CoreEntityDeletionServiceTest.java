package de.symeda.sormas.backend.deletionconfiguration;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CoreEntityDeletionServiceTest  extends AbstractBeanTest {

    @Test
    public void testCaseAutomaticDeletion(){

        final Date today = new Date();
        final Date caseReportAndOnsetDate = new LocalDate().minusDays(10).toDate();

        TestDataCreator.RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
        UserDto user = creator
                .createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
        PersonDto cazePerson = creator.createPerson("Case", "Person", Sex.MALE, 1980, 1, 1);
        CaseDataDto caze = creator.createCase(
                user.toReference(),
                cazePerson.toReference(),
                Disease.EVD,
                CaseClassification.PROBABLE,
                InvestigationStatus.PENDING,
                caseReportAndOnsetDate,
                rdcf);

        assertNotNull(getCaseFacade().getCaseDataByUuid(caze.getUuid()));

        CoreEntityDeletionService coreEntityDeletionService = new CoreEntityDeletionService();

        coreEntityDeletionService.executeAutomaticDeletion();

        assertNull(getCaseFacade().getCaseDataByUuid(caze.getUuid()));

    }

}
