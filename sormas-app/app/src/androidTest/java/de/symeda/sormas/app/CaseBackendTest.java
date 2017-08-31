package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.backend.user.User;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Mate Strysewske on 14.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class CaseBackendTest {

    @Rule
    public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

    @Before
    public void initTest() {
        TestHelper.initTestEnvironment();
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
    public void shouldCreatePreviousHospitalization() {
        // Assure that there are no previous hospitalizations in the app to start with
        assertThat(DatabaseHelper.getPreviousHospitalizationDao().queryForAll().size(), is(0));

        Case caze = TestEntityCreator.createCase();
        TestEntityCreator.createPreviousHospitalization(caze);

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
        mergeCase.getSymptoms().setIllLocation((Location) caze.getSymptoms().getIllLocation().clone());
        mergeCase.setHospitalization((Hospitalization) caze.getHospitalization().clone());
        mergeCase.setEpiData((EpiData) caze.getEpiData().clone());
        mergeCase.setId(null);
        mergeCase.getPerson().setId(null);
        mergeCase.getPerson().getAddress().setId(null);
        mergeCase.getSymptoms().setId(null);
        mergeCase.getSymptoms().getIllLocation().setId(null);
        mergeCase.getHospitalization().setId(null);
        mergeCase.getEpiData().setId(null);

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
        Case updatedCase = DatabaseHelper.getCaseDao().queryUuid(caze.getUuid());
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

        caze.setMeaslesVaccination(Vaccination.VACCINATED);

        DatabaseHelper.getCaseDao().saveAndSnapshot(caze);
        caze = DatabaseHelper.getCaseDao().queryUuid(caze.getUuid());

        // Snapshot should be present and entity should be modified after saving
        assertThat(caze.isModified(), is(true));
        assertNotNull(DatabaseHelper.getCaseDao().querySnapshotByUuid(caze.getUuid()));

        DatabaseHelper.getCaseDao().accept(caze);
        caze = DatabaseHelper.getCaseDao().queryUuid(caze.getUuid());

        // Snapshot should be removed and entity should not be modified after accepting
        assertNull(DatabaseHelper.getCaseDao().querySnapshotByUuid(caze.getUuid()));
        assertThat(caze.isModified(), is(false));
    }

    @Test
    public void shouldUpdateUnreadStatus() throws DaoException {
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = TestEntityCreator.createCase();
        EpiDataBurial burial = TestEntityCreator.createEpiDataBurial(caze);

        caze.setLocalChangeDate(DateHelper.addSeconds(caze.getLocalChangeDate(), 6));

        // Updated case should be unread
        assertThat(caze.isUnreadOrChildUnread(), is(true));

        caseDao.markAsRead(caze);
        caze = DatabaseHelper.getCaseDao().queryUuid(caze.getUuid());
        // Case shouldn't be marked as unread after markAsRead has been called
        assertThat(caze.isUnreadOrChildUnread(), is(false));
        // UUID of embedded object should still be the same
        EpiDataBurial burialFromDB = DatabaseHelper.getEpiDataBurialDao().queryUuid(burial.getUuid());
        assertEquals(burial.getUuid(), burialFromDB.getUuid());
    }

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
        mergeCase.getSymptoms().setIllLocation((Location) caze.getSymptoms().getIllLocation().clone());
        mergeCase.setHospitalization((Hospitalization) caze.getHospitalization().clone());
        mergeCase.setEpiData((EpiData) caze.getEpiData().clone());
        mergeCase.setId(null);
        mergeCase.getPerson().setId(null);
        mergeCase.getPerson().getAddress().setId(null);
        mergeCase.getSymptoms().setId(null);
        mergeCase.getSymptoms().getIllLocation().setId(null);
        mergeCase.getHospitalization().setId(null);
        mergeCase.getEpiData().setId(null);
        mergeCase.setEpidNumber("ServerEpidNumber");

        caseDao.mergeOrCreate(mergeCase);
        DatabaseHelper.getPersonDao().mergeOrCreate(mergeCase.getPerson());

        assertThat(syncLogDao.countOf(), is(1L));
    }

    @Test
    public void shouldUpdateCaseAndAssociatedEntitiesOnMove() {
        CaseDao caseDao = DatabaseHelper.getCaseDao();
        Case caze = TestEntityCreator.createCase();
        User user = DatabaseHelper.getUserDao().queryUuid(TestHelper.USER_UUID);

        TaskDao taskDao = DatabaseHelper.getTaskDao();
        Task pendingTask = TestEntityCreator.createCaseTask(caze, TaskStatus.PENDING, user);
        Task doneTask = TestEntityCreator.createCaseTask(caze, TaskStatus.DONE, user);

        caze.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(TestHelper.SECOND_DISTRICT_UUID));
        caze.setCommunity(DatabaseHelper.getCommunityDao().queryUuid(TestHelper.SECOND_COMMUNITY_UUID));
        caze.setHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(TestHelper.SECOND_FACILITY_UUID));

        try {
            caseDao.moveCase(caze);
        } catch (DaoException e) {
            throw new RuntimeException();
        }

        caze = caseDao.queryUuid(caze.getUuid());
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
        assertEquals(previousHospitalizations.size(), 1);
    }

}
