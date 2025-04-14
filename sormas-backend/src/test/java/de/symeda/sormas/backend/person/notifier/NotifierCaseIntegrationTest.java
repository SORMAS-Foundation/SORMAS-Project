/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.person.notifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.notifier.NotifierDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

/**
 * Integration tests for verifying the behavior of cases with associated notifiers.
 * This class extends {@link NotifierTestBase} to leverage common test setup and utilities.
 */
public class NotifierCaseIntegrationTest extends NotifierTestBase {

    private RDCF rdcf;
    private RDCF rdcf1;
    private UserDto nationalUser;
    private UserDto surveillanceSupervisor;
    private UserDto surveillanceOfficer;
    private UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities;

    /**
     * Initializes test data, including regions, districts, users, and facilities.
     * This method is called before each test to ensure a consistent test environment.
     */
    @Override
    public void init() {

        super.init();

        rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
        rdcf1 = creator.createRDCF("Region1", "District1", "Community1", "Facility1");
        surveillanceSupervisor = creator.createSurveillanceSupervisor(rdcf);
        surveillanceOfficer = creator.createSurveillanceOfficer(rdcf);
        surveillanceOfficerWithRestrictedAccessToAssignedEntities = creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf);
        nationalUser = creator.createNationalUser();
    }

    /**
     * Tests the creation of a case with an associated notifier.
     * Verifies that the notifier is correctly saved and linked to the case.
     */
    @Test
    void testCreateCaseWithNotifier() {

        String initialFirstName = "Charlie";
        String initialLastName = "Johnson";

        NotifierDto newNotifier =
            creator.createNotifier(initialFirstName, initialLastName, "charlie.johnson@example.com", "+123456789", "22 St Anotherplace", "info");
        NotifierDto initialNotifier = getNotifierFacade().save(newNotifier);

        CaseDataDto caze = createSimpleCase();

        caze.setNotifier(getNotifierFacade().getVersionReferenceByUuidAndDate(initialNotifier.getUuid(), null));

        CaseDataDto savedCaze = getCaseFacade().save(caze);

        assertThat("Notifier should not be null in the saved case", savedCaze.getNotifier(), is(notNullValue()));
        assertThat("Notifier UUID in the saved case should match the initial notifier UUID", savedCaze.getNotifier().getUuid(), is(initialNotifier.getUuid()));
    }

    /**
     * Tests the creation of a case with a specific version of a notifier.
     * Verifies that the correct version of the notifier is linked to the case
     * and that the notifier's details match the expected values.
     */
    @Test
    void testCreateCaseWithNotifierVersion() {

        String initialFirstName = "Charlie";
        String initialLastName = "Johnson";

        String updatedFirstName = "Charlie Updated";
        String updatedLastName = "Johnson Updated";

        NotifierDto newNotifier =
            creator.createNotifier(initialFirstName, initialLastName, "charlie.johnson@example.com", "+123456789", "22 St Anotherplace", "info");
        NotifierDto initialNotifier = getNotifierFacade().save(newNotifier);

        final Instant changeDate = initialNotifier.getChangeDate().toInstant();

        initialNotifier.setFirstName(updatedFirstName);
        initialNotifier.setLastName(updatedLastName);
        getNotifierFacade().save(initialNotifier);

        CaseDataDto caze = createSimpleCase();

        // Retrieve the notifier by UUID at the time of the first save
        NotifierDto pastNotifier = getNotifierFacade().getByUuidAndTime(initialNotifier.getUuid(), changeDate);

        caze.setNotifier(getNotifierFacade().getVersionReferenceByUuidAndDate(pastNotifier.getUuid(),  initialNotifier.getChangeDate()));

        CaseDataDto savedCaze = getCaseFacade().save(caze);

        assertThat("Notifier should not be null in the saved case", savedCaze.getNotifier(), is(notNullValue()));
        assertThat("Notifier UUID in the saved case should match the initial notifier UUID", savedCaze.getNotifier().getUuid(), is(initialNotifier.getUuid()));
        assertThat("Notifier first name in the saved case should match the initial first name", savedCaze.getNotifier().getFirstName(), is(initialFirstName));
        assertThat("Notifier last name in the saved case should match the initial last name", savedCaze.getNotifier().getLastName(), is(initialLastName));
    }

    /**
     * Creates a simple case with predefined attributes for testing purposes.
     * 
     * @return A {@link CaseDataDto} object representing the created case.
     */
    private CaseDataDto createSimpleCase() {
        PersonReferenceDto person = creator.createPerson().toReference();
        CaseDataDto caze = CaseDataDto.build(person, Disease.CORONAVIRUS);

        caze.setReportDate(new Date());
        caze.setReportingUser(surveillanceOfficer.toReference());
        caze.setCaseClassification(CaseClassification.PROBABLE);
        caze.setInvestigationStatus(InvestigationStatus.PENDING);
        caze.setResponsibleRegion(rdcf.region);
        caze.setResponsibleDistrict(rdcf.district);
        caze.setFacilityType(FacilityType.HOSPITAL);
        caze.setHealthFacility(rdcf.facility);

        ExposureDto exposure = ExposureDto.build(ExposureType.WORK);

        caze.getEpiData().setExposures(Collections.singletonList(exposure));
        return caze;
    }
}
