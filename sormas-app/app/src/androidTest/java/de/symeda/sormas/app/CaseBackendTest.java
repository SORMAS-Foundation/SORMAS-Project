/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.caze.CaseEditAuthorization;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.backend.user.User;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CaseBackendTest {

    @Rule
    public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

    @Before
    public void initTest() {
        TestHelper.initTestEnvironment(false);
    }

    @Test
    public void shouldCreateCase() {
        // Assure that there are no cases in the app to start with
        assertThat(DatabaseHelper.getCaseDao().queryForAll().size(), is(0));

        TestEntityCreator.createCase();

        // Assure that the case has been successfully created
        assertThat(DatabaseHelper.getCaseDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreatePreviousHospitalization() throws DaoException {
        // Assure that there are no previous hospitalizations in the app to start with
        assertThat(DatabaseHelper.getPreviousHospitalizationDao().queryForAll().size(), is(0));

        Case caze = TestEntityCreator.createCase();
        TestEntityCreator.addPreviousHospitalization(caze);
        DatabaseHelper.getCaseDao().saveAndSnapshot(caze);

        // Assure that the previous hospitalization has been successfully created
        assertThat(DatabaseHelper.getPreviousHospitalizationDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreateEpiDataBurial() {
        // Assure that there are no burials in the app to start with
        assertThat(DatabaseHelper.getEpiDataBurialDao().queryForAll().size(), is(0));

        Case caze = TestEntityCreator.createCase();
        TestEntityCreator.createEpiDataBurial(caze);

        // Assure that the burial has been successfully created
        assertThat(DatabaseHelper.getEpiDataBurialDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreateEpiDataGathering() {
        // Assure that there are no burials in the app to start with
        assertThat(DatabaseHelper.getEpiDataGatheringDao().queryForAll().size(), is(0));

        Case caze = TestEntityCreator.createCase();
        TestEntityCreator.createEpiDataGathering(caze);

        // Assure that the burial has been successfully created
        assertThat(DatabaseHelper.getEpiDataGatheringDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreateEpiDataTravel() {
        // Assure that there are no burials in the app to start with
        assertThat(DatabaseHelper.getEpiDataTravelDao().queryForAll().size(), is(0));

        Case caze = TestEntityCreator.createCase();
        TestEntityCreator.createEpiDataTravel(caze);

        // Assure that the burial has been successfully created
        assertThat(DatabaseHelper.getEpiDataTravelDao().queryForAll().size(), is(1));
    }

    /**
     * This tests merging of cases, persons and locations.
     */
    @Test
    public void shouldMergeAsExpected() throws DaoException {
        // Get the current case object from the database
        Case caze = TestEntityCreator.createCase();

        caze.setEpidNumber("AppEpidNumber");
        caze.getHospitalization().setIsolated(YesNoUnknown.NO);
        caze.getPerson().setNickname("Hansi");
        caze.getSymptoms().setTemperature(37.0f);
        caze.getEpiData().setBats(YesNoUnknown.NO);
        caze.getPerson().getAddress().setCity("AppCity");

        DatabaseHelper.getCaseDao().saveAndSnapshot(caze);
        DatabaseHelper.getPersonDao().saveAndSnapshot(caze.getPerson());

        Case mergeCase = (Case) caze.clone();
        mergeCase.setPerson((Person) caze.getPerson().clone());
        mergeCase.getPerson().setAddress((Location) caze.getPerson().getAddress().clone());
        mergeCase.setSymptoms((Symptoms) caze.getSymptoms().clone());
        mergeCase.setHospitalization((Hospitalization) caze.getHospitalization().clone());
        mergeCase.setEpiData((EpiData) caze.getEpiData().clone());
        mergeCase.setId(null);
        mergeCase.getPerson().setId(null);
        mergeCase.getPerson().getAddress().setId(null);
        mergeCase.getSymptoms().setId(null);
        mergeCase.getHospitalization().setId(null);
        mergeCase.getEpiData().setId(null);
        mergeCase.getClinicalCourse().setId(null);
        mergeCase.getPortHealthInfo().setId(null);
        mergeCase.getTherapy().setId(null);
        mergeCase.getClinicalCourse().getHealthConditions().setId(null);
        mergeCase.getMaternalHistory().setId(null);

        mergeCase.setEpidNumber("ServerEpidNumber");
        mergeCase.getHospitalization().setIsolated(YesNoUnknown.YES);
        mergeCase.getPerson().setNickname("Franzi");
        mergeCase.getSymptoms().setTemperature(36.5f);
        mergeCase.getEpiData().setBats(YesNoUnknown.YES);
        mergeCase.getPerson().getAddress().setCity("ServerCity");

        // Assert that the cloning has worked properly
        assertThat(caze.getEpidNumber(), is("AppEpidNumber"));
        assertThat(mergeCase.getEpidNumber(), is("ServerEpidNumber"));
        assertThat(caze.getPerson().getNickname(), is("Hansi"));
        assertThat(mergeCase.getPerson().getNickname(), is("Franzi"));
        assertThat(caze.getPerson().getAddress().getCity(), is("AppCity"));
        assertThat(mergeCase.getPerson().getAddress().getCity(), is("ServerCity"));

        DatabaseHelper.getCaseDao().mergeOrCreate(mergeCase);
        DatabaseHelper.getPersonDao().mergeOrCreate(mergeCase.getPerson());

        // Assert that the merging algorithm has correctly changed or kept the respective values
        Case updatedCase = DatabaseHelper.getCaseDao().queryUuidWithEmbedded(caze.getUuid());
        assertThat(updatedCase.getEpidNumber(), is("ServerEpidNumber"));
        assertThat(updatedCase.getHospitalization().getIsolated(), is(YesNoUnknown.YES));
        assertThat(updatedCase.getSymptoms().getTemperature(), is(36.5f));
        assertThat(updatedCase.getEpiData().getBats(), is(YesNoUnknown.YES));
        assertThat(updatedCase.getPerson().getNickname(), is("Franzi"));
        assertThat(updatedCase.getPerson().getAddress().getCity(), is("ServerCity"));
    }

    @Test
    public void shouldAcceptAsExpected() throws DaoException {
        Case caze = TestEntityCreator.createCase();
        assertThat(caze.isModified(), is(false));

        caze.setVaccination(Vaccination.VACCINATED);

        DatabaseHelper.getCaseDao().saveAndSnapshot(caze);
        caze = DatabaseHelper.getCaseDao().queryUuidWithEmbedded(caze.getUuid());

        // Snapshot should be present and entity should be modified after saving
        assertThat(caze.isModified(), is(true));
        assertNotNull(DatabaseHelper.getCaseDao().querySnapshotByUuid(caze.getUuid()));

        DatabaseHelper.getCaseDao().accept(caze);
        caze = DatabaseHelper.getCaseDao().queryUuidWithEmbedded(caze.getUuid());

        // Snapshot should be removed and entity should not be modified after accepting
        assertNull(DatabaseHelper.getCaseDao().querySnapshotByUuid(caze.getUuid()));
        assertThat(caze.isModified(), is(false));
    }

    // TODO #704
//    @Test
//    public void shouldUpdateUnreadStatus() throws DaoException {
//        CaseDao caseDao = DatabaseHelper.getCaseDao();
//        Case caze = TestEntityCreator.createCase();
//        EpiDataBurial burial = TestEntityCreator.createEpiDataBurial(caze);
//
//        caze.setLocalChangeDate(DateHelper.addSeconds(caze.getLocalChangeDate(), 6));
//
//        // Updated case should be unread
//        assertThat(caze.isUnreadOrChildUnread(), is(true));
//
//        caseDao.markAsRead(caze);
//        caze = DatabaseHelper.getCaseDao().queryUuidWithEmbedded(caze.getUuid());
//        // Case shouldn't be marked as unread after markAsRead has been called
//        assertThat(caze.isUnreadOrChildUnread(), is(false));
//        // UUID of embedded object should still be the same
//        EpiDataBurial burialFromDB = DatabaseHelper.getEpiDataBurialDao().queryUuid(burial.getUuid());
//        assertEquals(burial.getUuid(), burialFromDB.getUuid());
//    }

    @Test
    public void shouldCreateSyncLogEntry() throws DaoException {
        SyncLogDao syncLogDao = DatabaseHelper.getSyncLogDao();
        assertThat(syncLogDao.countOf(), is(0L));

        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = TestEntityCreator.createCase();
        caze.setEpidNumber("AppEpidNumber");
        DatabaseHelper.getCaseDao().saveAndSnapshot(caze);
        DatabaseHelper.getPersonDao().saveAndSnapshot(caze.getPerson());

        Case mergeCase = (Case) caze.clone();
        mergeCase.setPerson((Person) caze.getPerson().clone());
        mergeCase.getPerson().setAddress((Location) caze.getPerson().getAddress().clone());
        mergeCase.setSymptoms((Symptoms) caze.getSymptoms().clone());
        mergeCase.setHospitalization((Hospitalization) caze.getHospitalization().clone());
        mergeCase.setEpiData((EpiData) caze.getEpiData().clone());
        mergeCase.setId(null);
        mergeCase.getPerson().setId(null);
        mergeCase.getPerson().getAddress().setId(null);
        mergeCase.getSymptoms().setId(null);
        mergeCase.getHospitalization().setId(null);
        mergeCase.getEpiData().setId(null);
        mergeCase.getClinicalCourse().setId(null);
        mergeCase.getPortHealthInfo().setId(null);
        mergeCase.getTherapy().setId(null);
        mergeCase.getClinicalCourse().getHealthConditions().setId(null);
        mergeCase.getMaternalHistory().setId(null);
        mergeCase.setEpidNumber("ServerEpidNumber");

        caseDao.mergeOrCreate(mergeCase);
        DatabaseHelper.getPersonDao().mergeOrCreate(mergeCase.getPerson());

        assertThat(syncLogDao.countOf(), is(1L));
    }

    @Test
    public void shouldUpdateCaseAndAssociatedEntitiesOnMove() throws DaoException {
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = TestEntityCreator.createCase();
        User user = DatabaseHelper.getUserDao().queryUuid(TestHelper.USER_UUID);

        TaskDao taskDao = DatabaseHelper.getTaskDao();
        Task pendingTask = TestEntityCreator.createCaseTask(caze, TaskStatus.PENDING, user);
        Task doneTask = TestEntityCreator.createCaseTask(caze, TaskStatus.DONE, user);

        Case existingCase = caseDao.queryUuidWithEmbedded(caze.getUuid());
        caze.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(TestHelper.SECOND_DISTRICT_UUID));
        caze.setCommunity(DatabaseHelper.getCommunityDao().queryUuid(TestHelper.SECOND_COMMUNITY_UUID));
        caze.setHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(TestHelper.SECOND_FACILITY_UUID));
        caseDao.createPreviousHospitalizationAndUpdateHospitalization(caze, existingCase);
        caseDao.saveAndSnapshot(caze);
        caze = caseDao.queryUuidWithEmbedded(caze.getUuid());

        pendingTask = taskDao.queryUuid(pendingTask.getUuid());
        doneTask = taskDao.queryUuid(doneTask.getUuid());

        // Case should have the new region, district, community and facility set
        assertEquals(caze.getRegion().getUuid(), TestHelper.REGION_UUID);
        assertEquals(caze.getDistrict().getUuid(), TestHelper.SECOND_DISTRICT_UUID);
        assertEquals(caze.getCommunity().getUuid(), TestHelper.SECOND_COMMUNITY_UUID);
        assertEquals(caze.getHealthFacility().getUuid(), TestHelper.SECOND_FACILITY_UUID);

        // The case officer should have changed
        assertEquals(caze.getSurveillanceOfficer().getUuid(), TestHelper.SECOND_USER_UUID);

        // Pending task should have been reassigned to the second user, done task should still be assigned to the first one
        assertEquals(pendingTask.getAssigneeUser().getUuid(), TestHelper.SECOND_USER_UUID);
        assertEquals(doneTask.getAssigneeUser().getUuid(), TestHelper.USER_UUID);

        // A previous hospitalization with the former facility should have been created
        List<PreviousHospitalization> previousHospitalizations = caze.getHospitalization().getPreviousHospitalizations();
        assertEquals(1, previousHospitalizations.size());
    }

    @Test
    public void shouldeMergeCollectionAsExpected() throws DaoException {

        CaseDao caseDao = DatabaseHelper.getCaseDao();
        CaseDtoHelper caseDtoHelper = new CaseDtoHelper();

        // create existing data for app
        Case caze = TestEntityCreator.createCase();
        TestEntityCreator.addPreviousHospitalization(caze);
        caseDao.saveAndSnapshot(caze);
        caseDao.accept(caze);
        caze = caseDao.queryForIdWithEmbedded(caze.getId());

        // add previous hospitalization on "server-side"
        CaseDataDto serverCaseDto = caseDtoHelper.adoToDto(caze);
        serverCaseDto.getHospitalization().getPreviousHospitalizations().get(0).setDescription("Server-side change");
        PreviousHospitalizationDto previousHospitalizationDto = new PreviousHospitalizationDto();
        previousHospitalizationDto.setUuid(DataHelper.createUuid());
        previousHospitalizationDto.setCreationDate(new Date()); // now
        previousHospitalizationDto.setChangeDate(new Date());
        serverCaseDto.getHospitalization().getPreviousHospitalizations().add(previousHospitalizationDto);

        // add previous hospitalization on app-side
        caze.getHospitalization().getPreviousHospitalizations().get(0).setDescription("App-side change");
        TestEntityCreator.addPreviousHospitalization(caze);
        caseDao.saveAndSnapshot(caze);

        // merge server case
        Case serverCase = caseDtoHelper.fillOrCreateFromDto(null, serverCaseDto);
        DatabaseHelper.getCaseDao().mergeOrCreate(serverCase);
        Case mergedCase = DatabaseHelper.getCaseDao().queryUuidWithEmbedded(serverCase.getUuid());
        assertEquals(3, mergedCase.getHospitalization().getPreviousHospitalizations().size());

        caseDao.accept(mergedCase);
        mergedCase = DatabaseHelper.getCaseDao().queryUuidWithEmbedded(mergedCase.getUuid());
        assertFalse(mergedCase.isModifiedOrChildModified());
    }

    @Test
    public void shouldPullWithCollection() throws DaoException {

        CaseDtoHelper caseDtoHelper = new CaseDtoHelper();

        Person person = TestEntityCreator.createPerson("Some", "Guy");
        PersonReferenceDto personDto = new PersonDtoHelper().toReferenceDto(person);

        CaseDataDto serverCaseDto = new CaseDataDto();
        // TODO find a better way to fill DTO with default embedded objects
        TestDtoCreator.fillNewDto(serverCaseDto);
        serverCaseDto.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
        serverCaseDto.setInvestigationStatus(InvestigationStatus.PENDING);
        serverCaseDto.setPerson(personDto);

        SymptomsDto symptomsDto = new SymptomsDto();
        TestDtoCreator.fillNewDto(symptomsDto);
        serverCaseDto.setSymptoms(symptomsDto);

        HospitalizationDto hospitalizationDto = new HospitalizationDto();
        TestDtoCreator.fillNewDto(hospitalizationDto);
        serverCaseDto.setHospitalization(hospitalizationDto);

        EpiDataDto epiDataDto = new EpiDataDto();
        TestDtoCreator.fillNewDto(epiDataDto);
        serverCaseDto.setEpiData(epiDataDto);

        EpiDataBurialDto epiDataBurialDto = new EpiDataBurialDto();
        TestDtoCreator.fillNewDto(epiDataBurialDto);
        LocationDto epiDataBurialLocationDto = new LocationDto();
        TestDtoCreator.fillNewDto(epiDataBurialLocationDto);
        epiDataBurialDto.setBurialAddress(epiDataBurialLocationDto);
        epiDataDto.getBurials().add(epiDataBurialDto);

        // merge server case
        Case serverCase = caseDtoHelper.fillOrCreateFromDto(null, serverCaseDto);
        DatabaseHelper.getCaseDao().mergeOrCreate(serverCase);
        Case mergedCase = DatabaseHelper.getCaseDao().queryUuidWithEmbedded(serverCase.getUuid());
        assertEquals(1, mergedCase.getEpiData().getBurials().size());
    }

    @Test
    public void shouldDeleteWithDependingEntities() throws DaoException, SQLException {
        // Assure that there are no cases or depending entities in the app to start with
        assertThat(DatabaseHelper.getCaseDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getContactDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getVisitDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getSampleDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getSampleTestDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getTaskDao().queryForAll().size(), is(0));

        Case caze = TestEntityCreator.createCase();
        Contact contact = TestEntityCreator.createContact(caze);
        TestEntityCreator.createVisit(contact);
        Sample sample = TestEntityCreator.createSample(caze);
        TestEntityCreator.createSampleTest(sample);
        TestEntityCreator.createCaseTask(caze, TaskStatus.PENDING, caze.getReportingUser());

        // Assure that the case and depending entities have been successfully created
        assertThat(DatabaseHelper.getCaseDao().queryForAll().size(), is(1));
        assertThat(DatabaseHelper.getContactDao().queryForAll().size(), is(1));
        assertThat(DatabaseHelper.getVisitDao().queryForAll().size(), is(1));
        assertThat(DatabaseHelper.getSampleDao().queryForAll().size(), is(1));
        assertThat(DatabaseHelper.getSampleTestDao().queryForAll().size(), is(1));
        assertThat(DatabaseHelper.getTaskDao().queryForAll().size(), is(1));

        DatabaseHelper.getCaseDao().deleteCaseAndAllDependingEntities(caze.getUuid());

        // Assure that there are no cases or depending entities in the app after the deletion
        assertThat(DatabaseHelper.getCaseDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getContactDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getVisitDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getSampleDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getSampleTestDao().queryForAll().size(), is(0));
        assertThat(DatabaseHelper.getTaskDao().queryForAll().size(), is(0));
    }

    @Test
    public void testEditCasePermissionWhenRegionMatch() throws DaoException, SQLException {
        Case caze = TestEntityCreator.createCase();
        assertTrue(CaseEditAuthorization.isCaseEditAllowed(caze));
    }

    @Test
    public void testEditCasePermissionWhenRegionDoesNotMatchAndCaseNotCreatedByUser() throws DaoException, SQLException {
        Case caze = TestEntityCreator.createCase();
        caze.setRegion(null);
        caze.setDistrict(null);
        caze.setHealthFacility(null);

        UserRole userRole = UserRole.HOSPITAL_INFORMANT;
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(userRole);

        ConfigProvider.getUser().setUserRoles(userRoles);
        ConfigProvider.getUser().setUuid("");

        assertFalse(CaseEditAuthorization.isCaseEditAllowed(caze));
    }
}
