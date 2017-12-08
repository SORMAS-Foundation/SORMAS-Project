package de.symeda.sormas.app;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.visit.Visit;

/**
 * Created by Mate Strysewske on 14.06.2017.
 */

public class TestEntityCreator {

    public static Person createPerson(String firstName, String lastName) {
        Person person = DatabaseHelper.getPersonDao().build();
        person.setFirstName(firstName);
        person.setLastName(lastName);

        try {
            DatabaseHelper.getPersonDao().saveAndSnapshot(person);
            DatabaseHelper.getPersonDao().accept(person);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getPersonDao().queryForId(person.getId());
    }

    public static Case createCase() {
        Disease disease = Disease.EVD;
        Region region = DatabaseHelper.getRegionDao().queryUuid(TestHelper.REGION_UUID);
        District district = DatabaseHelper.getDistrictDao().queryUuid(TestHelper.DISTRICT_UUID);
        Community community = DatabaseHelper.getCommunityDao().queryUuid(TestHelper.COMMUNITY_UUID);
        Facility facility = DatabaseHelper.getFacilityDao().queryUuid(TestHelper.FACILITY_UUID);
        CaseClassification caseClassification = CaseClassification.SUSPECT;
        InvestigationStatus investigationStatus = InvestigationStatus.PENDING;

        Case caze = DatabaseHelper.getCaseDao().build(createPerson("Salomon", "Kalou"));
        caze.setDisease(disease);
        caze.setRegion(region);
        caze.setDistrict(district);
        caze.setCommunity(community);
        caze.setHealthFacility(facility);
        caze.setCaseClassification(caseClassification);
        caze.setInvestigationStatus(investigationStatus);

        try {
            DatabaseHelper.getCaseDao().saveAndSnapshot(caze);
            DatabaseHelper.getCaseDao().accept(caze);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getCaseDao().queryForIdWithEmbedded(caze.getId());
    }

    public static Contact createContact() {
        Person person = createPerson("Thierry", "Henry");
        Case caze = createCase();

        Contact contact = DatabaseHelper.getContactDao().build();
        contact.setPerson(person);
        contact.setCaze(caze);

        try {
            DatabaseHelper.getContactDao().saveAndSnapshot(contact);
            DatabaseHelper.getContactDao().accept(contact);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getContactDao().queryForIdWithEmbedded(contact.getId());
    }

    public static Event createEvent() {
        EventType eventType = EventType.RUMOR;
        String eventDesc = "FirstEventDescription";
        Date eventDate = DateHelper.subtractDays(new Date(), 2);
        TypeOfPlace typeOfPlace = TypeOfPlace.PUBLIC_PLACE;
        String srcFirstName = "Emil";
        String srcLastName = "Mpenza";
        String srcTelNo = "0150123123123";

        Event event = DatabaseHelper.getEventDao().build();
        event.setEventType(eventType);
        event.setEventDesc(eventDesc);
        event.setEventDate(eventDate);
        event.setTypeOfPlace(typeOfPlace);
        event.setSrcFirstName(srcFirstName);
        event.setSrcLastName(srcLastName);
        event.setSrcTelNo(srcTelNo);

        try {
            DatabaseHelper.getEventDao().saveAndSnapshot(event);
            DatabaseHelper.getEventDao().accept(event);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getEventDao().queryForIdWithEmbedded(event.getId());
    }

    public static Sample createSample() {
        Case caze = createCase();
        Date sampleDateTime = DateHelper.subtractDays(new Date(), 1);
        Facility lab = DatabaseHelper.getFacilityDao().queryForAll().get(0);
        SampleMaterial material = SampleMaterial.BLOOD;

        Sample sample = DatabaseHelper.getSampleDao().build(caze);
        sample.setSampleDateTime(sampleDateTime);
        sample.setLab(lab);
        sample.setSampleMaterial(material);

        try {
            DatabaseHelper.getSampleDao().saveAndSnapshot(sample);
            DatabaseHelper.getSampleDao().accept(sample);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getSampleDao().queryForIdWithEmbedded(sample.getId());
    }

    public static PreviousHospitalization addPreviousHospitalization(Case caze) {
        PreviousHospitalization prevHosp = DatabaseHelper.getPreviousHospitalizationDao().build();
        prevHosp.setHospitalization(caze.getHospitalization());
        caze.getHospitalization().getPreviousHospitalizations().add(prevHosp);
        return prevHosp;
    }

    public static EpiDataBurial createEpiDataBurial(Case caze) {
        EpiDataBurial burial = DatabaseHelper.getEpiDataBurialDao().build();
        burial.setEpiData(caze.getEpiData());

        try {
            DatabaseHelper.getEpiDataBurialDao().saveAndSnapshot(burial);
            DatabaseHelper.getEpiDataBurialDao().accept(burial);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getEpiDataBurialDao().queryForIdWithEmbedded(burial.getId());
    }

    public static EpiDataGathering createEpiDataGathering(Case caze) {
        EpiDataGathering gathering = DatabaseHelper.getEpiDataGatheringDao().build();
        gathering.setEpiData(caze.getEpiData());

        try {
            DatabaseHelper.getEpiDataGatheringDao().saveAndSnapshot(gathering);
            DatabaseHelper.getEpiDataGatheringDao().accept(gathering);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getEpiDataGatheringDao().queryForIdWithEmbedded(gathering.getId());
    }

    public static EpiDataTravel createEpiDataTravel(Case caze) {
        EpiDataTravel travel = DatabaseHelper.getEpiDataTravelDao().build();
        travel.setEpiData(caze.getEpiData());

        try {
            DatabaseHelper.getEpiDataTravelDao().saveAndSnapshot(travel);
            DatabaseHelper.getEpiDataTravelDao().accept(travel);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getEpiDataTravelDao().queryForIdWithEmbedded(travel.getId());
    }

    public static Visit createVisit(Contact contact) throws DaoException {
        Visit visit = DatabaseHelper.getVisitDao().build(contact.getUuid());
        Symptoms symptoms = DatabaseHelper.getSymptomsDao().build();
        visit.setSymptoms(symptoms);
        visit.setVisitUser(ConfigProvider.getUser());

        DatabaseHelper.getVisitDao().saveAndSnapshot(visit);
        DatabaseHelper.getVisitDao().accept(visit);

        return DatabaseHelper.getVisitDao().queryForIdWithEmbedded(visit.getId());
    }

    public static EventParticipant createEventParticipant(Event event) {
        Person person = createPerson("Demba", "Ba");

        EventParticipant eventParticipant = DatabaseHelper.getEventParticipantDao().build();
        eventParticipant.setEvent(event);
        eventParticipant.setPerson(person);

        try {
            DatabaseHelper.getEventParticipantDao().saveAndSnapshot(eventParticipant);
            DatabaseHelper.getEventParticipantDao().accept(eventParticipant);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getEventParticipantDao().queryForIdWithEmbedded(eventParticipant.getId());
    }

    public static SampleTest createSampleTest(Sample sample) {
        SampleTestType sampleTestType = SampleTestType.RAPID_TEST;
        SampleTestResultType sampleTestResultType = SampleTestResultType.NEGATIVE;
        Date sampleTestDateTime = new Date();

        SampleTest sampleTest = DatabaseHelper.getSampleTestDao().build();
        sampleTest.setSample(sample);
        sampleTest.setTestType(sampleTestType);
        sampleTest.setTestResult(sampleTestResultType);
        sampleTest.setTestDateTime(sampleTestDateTime);

        try {
            DatabaseHelper.getSampleTestDao().saveAndSnapshot(sampleTest);
            DatabaseHelper.getSampleTestDao().accept(sampleTest);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getSampleTestDao().queryForIdWithEmbedded(sampleTest.getId());
    }

    public static Task createCaseTask(Case caze, TaskStatus taskStatus, User user) {
        TaskDto taskDto = new TaskDto();
        Task task = new TaskDtoHelper().fillOrCreateFromDto(null, taskDto);
        task.setUuid(DataHelper.createUuid());
        task.setCreationDate(new Date());
        task.setChangeDate(new Date());
        task.setTaskContext(TaskContext.CASE);
        task.setTaskType(TaskType.CASE_INVESTIGATION);
        task.setTaskStatus(taskStatus);
        task.setCaze(caze);
        task.setAssigneeUser(user);

        try {
            DatabaseHelper.getTaskDao().saveAndSnapshot(task);
            DatabaseHelper.getTaskDao().accept(task);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return DatabaseHelper.getTaskDao().queryForIdWithEmbedded(task.getId());
    }

    public static WeeklyReport createWeeklyReport(EpiWeek epiWeek) {
        WeeklyReport weeklyReport;

        try {
            weeklyReport = DatabaseHelper.getWeeklyReportDao().create(epiWeek);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        return weeklyReport;
    }

}
