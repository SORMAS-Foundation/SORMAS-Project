/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;

import android.content.Context;
import android.util.Log;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.epidata.TravelType;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.Item;

/**
 * Created by Orson on 02/12/2017.
 */

public class MemoryDatabaseHelper {

	private final Context context;
	private static MemoryDatabaseHelper instance = null;
	private static List<Task> taskList = new ArrayList<>();
	private static List<Case> caseList = new ArrayList<>();
	private static List<Contact> contactList = new ArrayList<>();
	private static List<Event> eventList = new ArrayList<>();
	private static List<Sample> sampleList = new ArrayList<>();
	private static List<PathogenTest> pathogenTestList = new ArrayList<>();
	private static List<EventParticipant> eventParticipantList = new ArrayList<>();
	private static List<Person> personList = new ArrayList<>();
	private static List<Visit> visitList = new ArrayList<>();
	private static List<EpiData> epidDataList = new ArrayList<>();
	private static List<EpiDataBurial> epidDataBurialList = new ArrayList<>();
	private static List<EpiDataGathering> epidDataGatheringList = new ArrayList<>();
	private static List<EpiDataTravel> epidDataTravelList = new ArrayList<>();
	private static List<Hospitalization> hospitalizationList = new ArrayList<>();
	private static List<PreviousHospitalization> previousHospitalizationList = new ArrayList<>();
	private static List<Symptoms> symptomList = new ArrayList<>();
	private static List<Facility> facilityList = new ArrayList<>();
	private static List<Community> communityList = new ArrayList<>();
	private static List<District> districtList = new ArrayList<>();
	private static List<Location> locationList = new ArrayList<>();
	private static List<Region> regionList = new ArrayList<>();
	private static List<Disease> diseaseList = new ArrayList<>();
	private static List<TypeOfPlace> typeOfPlaceList = new ArrayList<>();
	private static List<OccupationType> occupationTypeList = new ArrayList<>();
	private static List<CauseOfDeath> causeOfDeathList = new ArrayList<>();
	//private static List<Sex> sexList = new ArrayList<>();
	private static List<ContactRelation> contactRelationshipList = new ArrayList<>();

	private MemoryDatabaseHelper(Context context) {
		this.context = context;
	}

	public static void init(Context context) {
		if (instance != null) {
			Log.e(MemoryDatabaseHelper.class.getName(), "MemoryDatabaseHelper has already been initalized");
		}
		instance = new MemoryDatabaseHelper(context);

		UserRoleGenerator.initialize();
		RegionGenerator.initialize();
		DistrictGenerator.initialize();
		CommunityGenerator.initialize();
		FacilityGenerator.initialize();
		LocationGenerator.initialize();
		UserGenerator.initialize();
		PersonGenerator.initialize();
		SymptomsGenerator.initialize();
		PreviousHospitalizationGenerator.initialize();
		HospitalizationGenerator.initialize();
		EpiDataTravelGenerator.initialize();
		EpiDataGatheringGenerator.initialize();
		EpiDataBurialGenerator.initialize();
		EpiDataGenerator.initialize();
		CaseGenerator.initialize();
		ContactGenerator.initialize();
		EventGenerator.initialize();
		TaskGenerator.initialize();
		SampleGenerator.initialize();
		SampleTestGenerator.initialize();
		EventParticipantGenerator.initialize();
		VisitGenerator.initialize();
		DiseaseGenerator.initialize();
		TypeOfPlaceGenerator.initialize();
		OccupationTypeGenerator.initialize();
		DeathCauseGenerator.initialize();
		SexGenerator.initialize();
		AgeTypeGenerator.initialize();
	}

	public static class WATER_SOURCE {

		public static List<WaterSource> getWaterSources() {
			return BaseDataGenerator.getAllWaterSource();
		}
	}

	public static class TRAVEL_TYPE {

		public static List<TravelType> getTravelTypes() {
			return BaseDataGenerator.getAllTravelType();
		}
	}

	public static class SAMPLE_MATERIAL {

		public static List<SampleMaterial> getSampleMaterials() {
			return BaseDataGenerator.getAllSampleMaterial();
		}
	}

	public static class TEST_TYPE {

		public static List<PathogenTestType> getTestTypes() {
			return BaseDataGenerator.getAllPathogenTestType();
		}
	}

	public static class CASE_OUTCOME {

		public static List<CaseOutcome> getCaseOutcomes() {
			return BaseDataGenerator.getAllCaseOutcome();
		}
	}

	public static class CASE_CLASSIFICATION {

		public static List<CaseClassification> getCaseClassifications() {
			return BaseDataGenerator.getAllCaseClassification();
		}
	}

	public static class INVESTIGATION_STATUS {

		public static List<InvestigationStatus> getInvestigationStatus() {
			return BaseDataGenerator.getAllInvestigationStatus();
		}
	}

	public static class VACCINATION {

		public static List<Vaccination> getAllVaccinations() {
			return BaseDataGenerator.getAllVaccination();
		}
	}

	public static class VACCINATION_SOURCE {

		public static List<VaccinationInfoSource> getAllVaccinationInfoSources() {
			return BaseDataGenerator.getAllVaccinationInfoSource();
		}
	}

	public static class TEMPERATURE_SOURCE {

		public static List<TemperatureSource> getTemperatureSources() {
			return BaseDataGenerator.getAllTemperatureSource();
		}
	}

	public static class BODY_TEMPERATURE {

		public static List<Item> getTemperatures(boolean withNull) {
			List<Item> temperature = new ArrayList<>();

			if (withNull)
				temperature.add(new Item("", null));

			for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
				temperature.add(new Item(SymptomsHelper.getTemperatureString(temperatureValue), temperatureValue));
			}

			return temperature;
		}
	}

	public static class BURIAL_CONDUCTOR {

		public static List<BurialConductor> getBurialConductors() {
			return BaseDataGenerator.getAllBurialConductor();
		}
	}

	public static class DEATH_PLACE {

		public static List<DeathPlaceType> getDeathPlaceTypes() {
			return BaseDataGenerator.getAllDeathPlaceType();
		}
	}

	public static class CONTACT_RELATION {

		public static List<ContactRelation> getRelationships() {
			return BaseDataGenerator.getAllContactRelation();
		}
	}

	public static class AGE_TYPE {

		public static List<ApproximateAgeType> getAgeTypes() {
			return AgeTypeGenerator.getAll();
		}
	}

	public static class SEX {

		public static List<Sex> getSexes() {
			return SexGenerator.getAll();
		}
	}

	public static class DEATH_CAUSE {

		public static List<CauseOfDeath> getDeathCauses(int number) {
			causeOfDeathList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			causeOfDeathList.addAll(DeathCauseGenerator.get(min));
			return causeOfDeathList;
		}
	}

	public static class OCCUPATION_TYPE {

		public static List<OccupationType> getOccupationTypes(int number) {
			occupationTypeList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			occupationTypeList.addAll(OccupationTypeGenerator.get(min));
			return occupationTypeList;
		}
	}

	public static class TYPE_OF_PLACE {

		public static List<TypeOfPlace> getPlaces(int number) {
			typeOfPlaceList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			typeOfPlaceList.addAll(TypeOfPlaceGenerator.get(min));
			return typeOfPlaceList;
		}
	}

	public static class DISEASE {

		public static List<Disease> getDiseases(int number) {
			diseaseList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			diseaseList.addAll(DiseaseGenerator.get(min));
			return diseaseList;
		}
	}

	public static class REGION {

		public static List<Region> getRegions(int number) {
			regionList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			regionList.addAll(RegionGenerator.get(min));
			return regionList;
		}
	}

	public static class LOCATION {

		public static List<Location> getLocations(int number) {
			locationList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			locationList.addAll(LocationGenerator.get(min));
			return locationList;
		}
	}

	public static class DISTRICT {

		public static List<District> getDistricts(int number) {
			districtList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			districtList.addAll(DistrictGenerator.get(min));
			return districtList;
		}
	}

	public static class COMMUNITY {

		public static List<Community> getCommunities(int number) {
			communityList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			communityList.addAll(CommunityGenerator.get(min));
			return communityList;
		}
	}

	public static class FACILITY {

		public static List<Facility> getFacilities(int number) {
			facilityList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			facilityList.addAll(FacilityGenerator.get(min));
			return facilityList;
		}
	}

	public static class SYMPTOM {

		public static List<Symptoms> getSymptoms(int number) {
			symptomList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			symptomList.addAll(SymptomsGenerator.get(min));
			return symptomList;
		}
	}

	public static class PREVIOUS_HOSPITALIZATION {

		public static List<PreviousHospitalization> getHospitalizations(int number) {
			previousHospitalizationList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			previousHospitalizationList.addAll(PreviousHospitalizationGenerator.get(min));
			return previousHospitalizationList;
		}
	}

	public static class HOSPITALIZATION {

		public static List<Hospitalization> getHospitalizations(int number) {
			hospitalizationList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			hospitalizationList.addAll(HospitalizationGenerator.get(min));
			return hospitalizationList;
		}
	}

	public static class EPID_DATA_TRAVEL {

		public static List<EpiDataTravel> getTravels(int number) {
			epidDataTravelList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			epidDataTravelList.addAll(EpiDataTravelGenerator.get(min));
			return epidDataTravelList;
		}
	}

	public static class EPID_DATA_GATHERING {

		public static List<EpiDataGathering> getGatherings(int number) {
			epidDataGatheringList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			epidDataGatheringList.addAll(EpiDataGatheringGenerator.get(min));
			return epidDataGatheringList;
		}
	}

	public static class EPID_DATA_BURIAL {

		public static List<EpiDataBurial> getBurials(int number) {
			epidDataBurialList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			epidDataBurialList.addAll(EpiDataBurialGenerator.get(min));
			return epidDataBurialList;
		}
	}

	public static class EPID_DATA {

		public static List<EpiData> getEpidData(int number) {
			epidDataList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			epidDataList.addAll(EpiDataGenerator.get(min));
			return epidDataList;
		}
	}

	public static class VISIT {

		public static List<Visit> getVisits(int number) {
			visitList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			visitList.addAll(VisitGenerator.get(min));
			return visitList;
		}
	}

	public static class PERSON {

		public static List<Person> getPersons(int number) {
			personList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			personList.addAll(PersonGenerator.get(min));
			return personList;
		}
	}

	public static class EVENT_PARTICIPANT {

		public static List<EventParticipant> getEventParticipants(int number) {
			eventParticipantList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			eventParticipantList.addAll(EventParticipantGenerator.get(min));
			return eventParticipantList;
		}
	}

	public static class TASK {

		public static List<Task> getTasks(int number) {
			taskList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			taskList.addAll(TaskGenerator.get(min));
			return taskList;
		}

		public static List<Task> getPendingTasks(int number) {
			taskList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Task> list = TaskGenerator.get(min);
			for (Task t : list) {
				if (t.getTaskStatus() == TaskStatus.PENDING) {
					taskList.add(t);
				}
			}

			return taskList;
		}

		public static List<Task> getDoneTasks(int number) {
			taskList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Task> list = TaskGenerator.get(min);
			for (Task t : list) {
				if (t.getTaskStatus() == TaskStatus.DONE) {
					taskList.add(t);
				}
			}

			return taskList;
		}

		public static List<Task> getNotExecutableTasks(int number) {
			taskList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Task> list = TaskGenerator.get(min);
			for (Task t : list) {
				if (t.getTaskStatus() == TaskStatus.NOT_EXECUTABLE) {
					taskList.add(t);
				}
			}

			return taskList;
		}
	}

	public static class CASE {

		public static List<Case> getCases(int number) {
			caseList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			caseList.addAll(CaseGenerator.get(min));
			return caseList;
		}

		public static List<Case> getPendingCases(int number) {
			caseList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Case> list = CaseGenerator.get(min);
			for (Case item : list) {
				if (item.getInvestigationStatus() == InvestigationStatus.PENDING) {
					caseList.add(item);
				}
			}

			return caseList;
		}

		public static List<Case> getDoneCases(int number) {
			caseList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Case> list = CaseGenerator.get(min);
			for (Case item : list) {
				if (item.getInvestigationStatus() == InvestigationStatus.DONE) {
					caseList.add(item);
				}
			}

			return caseList;
		}

		public static List<Case> getDiscardedCases(int number) {
			caseList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Case> list = CaseGenerator.get(min);
			for (Case item : list) {
				if (item.getInvestigationStatus() == InvestigationStatus.DISCARDED) {
					caseList.add(item);
				}
			}

			return caseList;
		}
	}

	public static class CONTACT {

		public static List<Contact> getContacts(int number) {
			contactList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			contactList.addAll(ContactGenerator.get(min));
			return contactList;
		}

		public static List<Contact> getFollowUpContacts(int number) {
			contactList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Contact> list = ContactGenerator.get(min);
			for (Contact item : list) {
				if (item.getFollowUpStatus() == FollowUpStatus.FOLLOW_UP) {
					contactList.add(item);
				}
			}

			return contactList;
		}

		public static List<Contact> getCompletedFollowUpContacts(int number) {
			contactList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Contact> list = ContactGenerator.get(min);
			for (Contact item : list) {
				if (item.getFollowUpStatus() == FollowUpStatus.COMPLETED) {
					contactList.add(item);
				}
			}

			return contactList;
		}

		public static List<Contact> getCanceledFollowUpContacts(int number) {
			contactList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Contact> list = ContactGenerator.get(min);
			for (Contact item : list) {
				if (item.getFollowUpStatus() == FollowUpStatus.CANCELED) {
					contactList.add(item);
				}
			}

			return contactList;
		}

		public static List<Contact> getLostFollowUpContacts(int number) {
			contactList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Contact> list = ContactGenerator.get(min);
			for (Contact item : list) {
				if (item.getFollowUpStatus() == FollowUpStatus.LOST) {
					contactList.add(item);
				}
			}

			return contactList;
		}

		public static List<Contact> getNoFollowUpContacts(int number) {
			contactList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Contact> list = ContactGenerator.get(min);
			for (Contact item : list) {
				if (item.getFollowUpStatus() == FollowUpStatus.NO_FOLLOW_UP) {
					contactList.add(item);
				}
			}

			return contactList;
		}
	}

	public static class EVENT {

		public static List<Event> getEvents(int number) {
			eventList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			eventList.addAll(EventGenerator.get(min));
			return eventList;
		}

		public static List<Event> getSignals(int number) {
			eventList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Event> list = EventGenerator.get(min);
			for (Event item : list) {
				if (item.getEventStatus() == EventStatus.SIGNAL) {
					eventList.add(item);
				}
			}

			return eventList;
		}

		public static List<Event> getConfirmedEvents(int number) {
			eventList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Event> list = EventGenerator.get(min);
			for (Event item : list) {
				if (item.getEventStatus() == EventStatus.EVENT) {
					eventList.add(item);
				}
			}

			return eventList;
		}

		public static List<Event> getDroppedEvents(int number) {
			eventList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Event> list = EventGenerator.get(min);
			for (Event item : list) {
				if (item.getEventStatus() == EventStatus.DROPPED) {
					eventList.add(item);
				}
			}

			return eventList;
		}
	}

	public static class SAMPLE {

		public static List<Sample> getSamples(int number) {
			sampleList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			sampleList.addAll(SampleGenerator.get(min));
			return sampleList;
		}

		public static List<Sample> getNotShippedSamples(int number) {
			sampleList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Sample> list = SampleGenerator.get(min);
			for (Sample item : list) {
				if (!item.isShipped() && !item.isReceived() && item.getReferredToUuid() == null) {
					sampleList.add(item);
				}
			}

			return sampleList;
		}

		public static List<Sample> getShippedSamples(int number) {
			sampleList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Sample> list = SampleGenerator.get(min);
			for (Sample item : list) {
				if (item.isShipped() && !item.isReceived() && item.getReferredToUuid() == null) {
					sampleList.add(item);
				}
			}

			return sampleList;
		}

		public static List<Sample> getReceivedSamples(int number) {
			sampleList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Sample> list = SampleGenerator.get(min);
			for (Sample item : list) {
				if (item.isReceived() && item.getReferredToUuid() == null) {
					sampleList.add(item);
				}
			}

			return sampleList;
		}

		public static List<Sample> getReferredSamples(int number) {
			sampleList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			List<Sample> list = SampleGenerator.get(min);
			for (Sample item : list) {
				if (item.getReferredToUuid() != null) {
					sampleList.add(item);
				}
			}

			return sampleList;
		}
	}

	public static class TEST {

		public static List<PathogenTest> getSampleTests(int number) {
			pathogenTestList.clear();
			int min = Math.min(number, BaseDataGenerator.DEFAULT_RECORD_NUMBER);
			pathogenTestList.addAll(SampleTestGenerator.get(min));
			return pathogenTestList;
		}
	}
}

class AgeTypeGenerator extends BaseDataGenerator {

	private static final List<ApproximateAgeType> pool = new ArrayList<ApproximateAgeType>();

	public static void initialize() {
		pool.addAll(getAgeTypes());
	}

	public static List<ApproximateAgeType> getAll() {
		return pool;
	}

	public static ApproximateAgeType getSingle() {
		return pool.get(0);
	}
}

class SexGenerator extends BaseDataGenerator {

	private static final List<Sex> pool = new ArrayList<Sex>();

	public static void initialize() {
		pool.addAll(getSexes());
	}

	public static List<Sex> getAll() {
		return pool;
	}

	public static Sex getSingle() {
		return pool.get(0);
	}
}

class OccupationTypeGenerator extends BaseDataGenerator {

	private static final List<OccupationType> pool = new ArrayList<OccupationType>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			pool.add(getRandomOccupationType());
		}
	}

	public static List<OccupationType> get(int number) {
		List<OccupationType> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static OccupationType getSingle() {
		return randomItem(pool);
	}
}

class DeathCauseGenerator extends BaseDataGenerator {

	private static final List<CauseOfDeath> pool = new ArrayList<CauseOfDeath>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			pool.add(getRandomCauseOfDeath());
		}
	}

	public static List<CauseOfDeath> get(int number) {
		List<CauseOfDeath> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static CauseOfDeath getSingle() {
		return randomItem(pool);
	}
}

class DiseaseGenerator extends BaseDataGenerator {

	private static final List<Disease> pool = new ArrayList<Disease>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			pool.add(getRandomDisease());
		}
	}

	public static List<Disease> get(int number) {
		List<Disease> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Disease getSingle() {
		return randomItem(pool);
	}
}

class TypeOfPlaceGenerator extends BaseDataGenerator {

	private static final List<TypeOfPlace> pool = new ArrayList<TypeOfPlace>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			pool.add(getRandomTypeOfPlace());
		}
	}

	public static List<TypeOfPlace> get(int number) {
		List<TypeOfPlace> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static TypeOfPlace getSingle() {
		return randomItem(pool);
	}
}

class VisitGenerator extends BaseDataGenerator {

	private static final List<Visit> pool = new ArrayList<Visit>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Visit data1 = new Visit();
			data1.setUuid(getRandomUuid());
			data1.setPerson(PersonGenerator.getSingle());
			data1.setDisease(getRandomDisease());
			data1.setVisitDateTime(getRandomDate());
			data1.setVisitUser(UserGenerator.getSingle());
			data1.setVisitStatus(getRandomVisitStatus());
			data1.setVisitRemarks(getRandomSentence());
			data1.setReportLat(getRandomDouble());
			data1.setReportLon(getRandomDouble());

			for (int j = 0; j < 20; j++) {
				data1.setSymptoms(SymptomsGenerator.getSingle());
			}

			pool.add(data1);
		}
	}

	public static List<Visit> get(int number) {
		List<Visit> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Visit getSingle() {
		return randomItem(pool);
	}

	private static VisitStatus getRandomVisitStatus() {
		List<VisitStatus> list = new ArrayList<VisitStatus>() {

			{
				add(VisitStatus.UNAVAILABLE);
				add(VisitStatus.UNCOOPERATIVE);
				add(VisitStatus.COOPERATIVE);
			}
		};

		return randomItem(list);
	}
}

class EventParticipantGenerator extends BaseDataGenerator {

	private static final List<EventParticipant> pool = new ArrayList<EventParticipant>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			EventParticipant data1 = new EventParticipant();
			data1.setUuid(getRandomUuid());
			data1.setEvent(EventGenerator.getSingle());
			data1.setPerson(PersonGenerator.getSingle());
			data1.setInvolvementDescription(getRandomName());

			pool.add(data1);
		}
	}

	public static List<EventParticipant> get(int number) {
		List<EventParticipant> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static EventParticipant getSingle() {
		return randomItem(pool);
	}
}

class SampleTestGenerator extends BaseDataGenerator {

	private static final List<PathogenTest> pool = new ArrayList<PathogenTest>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			PathogenTest data1 = new PathogenTest();
			data1.setUuid(getRandomUuid());
			data1.setSample(SampleGenerator.getSingle());
			data1.setTestType(getRandomPathogenTestType());
			data1.setTestResult(getRandomPathogenTestResultType());
			data1.setTestDateTime(getRandomDate());

			pool.add(data1);
		}
	}

	public static List<PathogenTest> get(int number) {
		List<PathogenTest> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static PathogenTest getSingle() {
		return randomItem(pool);
	}

	private static PathogenTestResultType getRandomPathogenTestResultType() {
		List<PathogenTestResultType> list = new ArrayList<PathogenTestResultType>() {

			{
				add(PathogenTestResultType.INDETERMINATE);
				add(PathogenTestResultType.PENDING);
				add(PathogenTestResultType.NEGATIVE);
				add(PathogenTestResultType.POSITIVE);
			}
		};

		return randomItem(list);
	}
}

class SampleGenerator extends BaseDataGenerator {

	private static final List<Sample> pool = new ArrayList<Sample>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Sample data1 = new Sample();
			data1.setUuid(getRandomUuid());
			data1.setAssociatedCase(CaseGenerator.getSingle());
			data1.setLabSampleID(getRandomString());
			data1.setSampleDateTime(getRandomDate());
			data1.setReportDateTime(getRandomDate());
			data1.setReportingUser(UserGenerator.getSingle());
			data1.setSampleMaterial(getRandomSampleMaterial());
			data1.setSampleMaterialText(getRandomString());
			data1.setLab(FacilityGenerator.getSingle());
			data1.setShipmentDate(getRandomDate());
			data1.setShipmentDetails(getRandomString());
			data1.setReceivedDate(getRandomDate());
			data1.setNoTestPossibleReason(getRandomSentence());
			data1.setSpecimenCondition(getRandomSpecimenCondition());
			data1.setComment(getRandomSentence());
			data1.setSampleSource(getRandomSampleSource());
			data1.setShipped(getRandomBoolean());
			data1.setReceived(getRandomBoolean());

			data1.setReferredToUuid(null);

			pool.add(data1);
		}

		for (int i = 0; i < Math.round(pool.size() / 20); i++) {
			pool.get(i).setReferredToUuid(randomItem(pool).getUuid());
		}
	}

	public static List<Sample> get(int number) {
		List<Sample> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Sample getSingle() {
		return randomItem(pool);
	}
}

class PersonGenerator extends BaseDataGenerator {

	private static final List<Person> pool = new ArrayList<Person>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Person data1 = new Person();
			data1.setUuid(getRandomUuid());
			data1.setFirstName(getRandomName());
			data1.setLastName(getRandomName());
			data1.setNickname(getRandomName());
			data1.setMothersMaidenName(getRandomName());
			data1.setBirthdateDD((int) DataUtils.toItems(de.symeda.sormas.api.utils.DateHelper.getDaysInMonth(5, 2012), false).get(7).getValue());
			data1.setBirthdateMM((int) DataUtils.getMonthItems(false).get(7).getValue());
			data1.setBirthdateYYYY((int) DataUtils.toItems(de.symeda.sormas.api.utils.DateHelper.getYearsToNow(), false).get(20).getValue());
			data1.setApproximateAge(getRandomAge());
			data1.setApproximateAgeType(getRandomApproximateAgeType());
			data1.setAddress(LocationGenerator.getSingle());
			data1.setPhone(getRandomPhoneNumber());
			data1.setPhoneOwner(getRandomName());
			data1.setSex(getRandomSex());
			data1.setPresentCondition(getRandomPresentCondition());
			data1.setDeathDate(getRandomDate());
			data1.setCauseOfDeath(getRandomCauseOfDeath());
			data1.setCauseOfDeathDetails(getRandomSentence());
			data1.setCauseOfDeathDisease(getRandomDisease());
			data1.setBurialDate(getRandomDate());
			data1.setBurialConductor(getRandomBurialConductor());
			data1.setDeathPlaceType(getRandomDeathPlaceType());
			data1.setDeathPlaceDescription(getRandomSentence());
			data1.setBurialPlaceDescription(getRandomSentence());
			data1.setOccupationType(getRandomOccupationType());
			data1.setOccupationDetails(getRandomSentence());
			data1.setOccupationFacility(FacilityGenerator.getSingle());

			pool.add(data1);
		}
	}

	public static List<Person> get(int number) {
		List<Person> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Person getSingle() {
		return randomItem(pool);
	}
}

class PreviousHospitalizationGenerator extends BaseDataGenerator {

	private static final List<PreviousHospitalization> pool = new ArrayList<PreviousHospitalization>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			PreviousHospitalization data1 = new PreviousHospitalization();
			data1.setUuid(getRandomUuid());
			data1.setAdmissionDate(getRandomDate());
			data1.setDischargeDate(getRandomDate());
			data1.setRegion(RegionGenerator.getSingle());
			data1.setDistrict(DistrictGenerator.getSingle());
			data1.setCommunity(CommunityGenerator.getSingle());
			data1.setHealthFacility(FacilityGenerator.getSingle());
			data1.setIsolated(getRandomYesNoUnknown());
			data1.setDescription(getRandomSentence());
			//data1.setHospitalization(HospitalizationGenerator.getSingle());
			pool.add(data1);
		}
	}

	public static List<PreviousHospitalization> get(int number) {
		List<PreviousHospitalization> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static List<PreviousHospitalization> get(Hospitalization hos, int number) {
		List<PreviousHospitalization> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			PreviousHospitalization previousHospitalization = pool.get(index);
			previousHospitalization.setHospitalization(hos);
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static PreviousHospitalization getSingle() {
		return randomItem(pool);
	}
}

class HospitalizationGenerator extends BaseDataGenerator {

	private static final List<Hospitalization> pool = new ArrayList<Hospitalization>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Hospitalization data1 = new Hospitalization();
			data1.setUuid(getRandomUuid());
			data1.setAdmissionDate(getRandomDate());
			data1.setDischargeDate(getRandomDate());
			data1.setIsolated(getRandomYesNoUnknown());
			data1.setIsolationDate(getRandomDate());
			data1.setHospitalizedPreviously(getRandomYesNoUnknown());
			data1.setPreviousHospitalizations(PreviousHospitalizationGenerator.get(data1, 2));
			data1.setAdmittedToHealthFacility(getRandomYesNoUnknown());

			pool.add(data1);
		}
	}

	public static List<Hospitalization> get(int number) {
		List<Hospitalization> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Hospitalization getSingle() {
		return randomItem(pool);
	}
}

class SymptomsGenerator extends BaseDataGenerator {

	private static final List<Symptoms> pool = new ArrayList<Symptoms>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Symptoms data1 = new Symptoms();
			data1.setUuid(getRandomUuid());
			data1.setOnsetDate(getRandomDate());
			data1.setTemperature(getRandomFloat());
			data1.setTemperatureSource(getRandomTemperatureSource());
			/*
			 * data1.setIllLocation(LocationGenerator.getSingle());
			 * data1.setIllLocationFrom(getRandomDate());
			 * data1.setIllLocationTo(getRandomDate());
			 */
			data1.setFever(getRandomSymptomState());
			data1.setDiarrhea(getRandomSymptomState());
			data1.setAnorexiaAppetiteLoss(getRandomSymptomState());
			data1.setAbdominalPain(getRandomSymptomState());
			data1.setChestPain(getRandomSymptomState());
			data1.setMusclePain(getRandomSymptomState());
			data1.setJointPain(getRandomSymptomState());
			data1.setHeadache(getRandomSymptomState());
			data1.setCough(getRandomSymptomState());
			data1.setDifficultyBreathing(getRandomSymptomState());
			data1.setSoreThroat(getRandomSymptomState());
			data1.setConjunctivitis(getRandomSymptomState());
			data1.setSkinRash(getRandomSymptomState());
			data1.setHiccups(getRandomSymptomState());
			data1.setEyePainLightSensitive(getRandomSymptomState());
			data1.setConfusedDisoriented(getRandomSymptomState());
			data1.setUnexplainedBleeding(getRandomSymptomState());
			data1.setGumsBleeding(getRandomSymptomState());
			data1.setInjectionSiteBleeding(getRandomSymptomState());
			data1.setDigestedBloodVomit(getRandomSymptomState());
			data1.setBleedingVagina(getRandomSymptomState());
			data1.setDehydration(getRandomSymptomState());
			data1.setFatigueWeakness(getRandomSymptomState());
			data1.setKopliksSpots(getRandomSymptomState());
			data1.setNausea(getRandomSymptomState());
			data1.setNeckStiffness(getRandomSymptomState());
			data1.setOnsetSymptom(getRandomString());
			data1.setOtitisMedia(getRandomSymptomState());
			data1.setRefusalFeedorDrink(getRandomSymptomState());
			data1.setRunnyNose(getRandomSymptomState());
			data1.setSymptomatic(getRandomBoolean());
			data1.setVomiting(getRandomSymptomState());
			data1.setOtherHemorrhagicSymptoms(getRandomSymptomState());
			data1.setOtherHemorrhagicSymptomsText(getRandomString());
			data1.setOtherNonHemorrhagicSymptoms(getRandomSymptomState());
			data1.setOtherNonHemorrhagicSymptomsText(getRandomString());
			data1.setSymptomsComments(getRandomSentence());
			data1.setBloodInStool(getRandomSymptomState());
			data1.setNoseBleeding(getRandomSymptomState());
			data1.setBloodyBlackStool(getRandomSymptomState());
			data1.setRedBloodVomit(getRandomSymptomState());
			data1.setCoughingBlood(getRandomSymptomState());
			data1.setSkinBruising(getRandomSymptomState());
			data1.setBloodUrine(getRandomSymptomState());
			data1.setThrobocytopenia(getRandomSymptomState());
			data1.setHearingloss(getRandomSymptomState());
			data1.setShock(getRandomSymptomState());
			data1.setSeizures(getRandomSymptomState());
			data1.setAlteredConsciousness(getRandomSymptomState());
			data1.setBackache(getRandomSymptomState());
			data1.setEyesBleeding(getRandomSymptomState());
			data1.setJaundice(getRandomSymptomState());
			data1.setDarkUrine(getRandomSymptomState());
			data1.setStomachBleeding(getRandomSymptomState());
			data1.setRapidBreathing(getRandomSymptomState());
			data1.setSwollenGlands(getRandomSymptomState());

			pool.add(data1);
		}
	}

	public static List<Symptoms> get(int number) {
		List<Symptoms> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Symptoms getSingle() {
		return randomItem(pool);
	}
}

class EpiDataTravelGenerator extends BaseDataGenerator {

	private static final List<EpiDataTravel> pool = new ArrayList<EpiDataTravel>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			EpiDataTravel data1 = new EpiDataTravel();
			data1.setUuid(getRandomUuid());
			//data1.setEpiData(EpiDataGenerator.getSingle());
			data1.setTravelType(getRandomTravelType());
			data1.setTravelDestination(getRandomAddress());
			data1.setTravelDateFrom(getRandomDate());
			data1.setTravelDateTo(getRandomDate());

			pool.add(data1);
		}
	}

	public static List<EpiDataTravel> get(int number) {
		List<EpiDataTravel> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static List<EpiDataTravel> get(EpiData epiData, int number) {
		List<EpiDataTravel> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			EpiDataTravel toSet = pool.get(index);
			toSet.setEpiData(epiData);
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static EpiDataTravel getSingle() {
		return randomItem(pool);
	}
}

class EpiDataGatheringGenerator extends BaseDataGenerator {

	private static final List<EpiDataGathering> pool = new ArrayList<EpiDataGathering>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			EpiDataGathering data1 = new EpiDataGathering();
			data1.setUuid(getRandomUuid());
			//data1.setEpiData(EpiDataGenerator.getSingle());
			data1.setDescription(getRandomSentence());
			data1.setGatheringDate(getRandomDate());
			data1.setGatheringAddress(LocationGenerator.getSingle());

			pool.add(data1);
		}
	}

	public static List<EpiDataGathering> get(int number) {
		List<EpiDataGathering> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static List<EpiDataGathering> get(EpiData epiData, int number) {
		List<EpiDataGathering> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			EpiDataGathering toSet = pool.get(index);
			toSet.setEpiData(epiData);
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static EpiDataGathering getSingle() {
		return randomItem(pool);
	}
}

class EpiDataBurialGenerator extends BaseDataGenerator {

	private static final List<EpiDataBurial> pool = new ArrayList<EpiDataBurial>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			EpiDataBurial data1 = new EpiDataBurial();
			data1.setUuid(getRandomUuid());
			//data1.setEpiData(EpiDataGenerator.getSingle());
			data1.setBurialPersonname(getRandomName());
			data1.setBurialRelation(getRandomRelationship());
			data1.setBurialDateFrom(getRandomDate());
			data1.setBurialDateTo(getRandomDate());
			data1.setBurialAddress(LocationGenerator.getSingle());
			data1.setBurialIll(getRandomYesNoUnknown());
			data1.setBurialTouching(getRandomYesNoUnknown());

			pool.add(data1);
		}
	}

	public static List<EpiDataBurial> get(int number) {
		List<EpiDataBurial> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static List<EpiDataBurial> get(EpiData epiData, int number) {
		List<EpiDataBurial> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			EpiDataBurial toSet = pool.get(index);
			toSet.setEpiData(epiData);
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static EpiDataBurial getSingle() {
		return randomItem(pool);
	}
}

class EpiDataGenerator extends BaseDataGenerator {

	private static final List<EpiData> pool = new ArrayList<EpiData>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			EpiData data1 = new EpiData();
			data1.setUuid(getRandomUuid());
			data1.setBurialAttended(getRandomYesNoUnknown());
			data1.setGatheringAttended(getRandomYesNoUnknown());
			data1.setTraveled(getRandomYesNoUnknown());
			data1.setRodents(getRandomYesNoUnknown());
			data1.setBats(getRandomYesNoUnknown());
			data1.setPrimates(getRandomYesNoUnknown());
			data1.setSwine(getRandomYesNoUnknown());
			data1.setBirds(getRandomYesNoUnknown());
			data1.setEatingRawAnimals(getRandomYesNoUnknown());
			data1.setSickDeadAnimals(getRandomYesNoUnknown());
			data1.setEatingRawAnimalsDetails(getRandomSentence());
			data1.setSickDeadAnimals(getRandomYesNoUnknown());
			data1.setSickDeadAnimalsDetails(getRandomSentence());
			data1.setSickDeadAnimalsDate(getRandomDate());
			data1.setSickDeadAnimalsLocation(getRandomString());
			data1.setAreaInfectedAnimals(getRandomYesNoUnknown());
			data1.setProcessingSuspectedCaseSampleUnsafe(getRandomYesNoUnknown());
			data1.setDirectContactDeadUnsafe(getRandomYesNoUnknown());
			data1.setCattle(getRandomYesNoUnknown());
			data1.setOtherAnimals(getRandomYesNoUnknown());
			data1.setOtherAnimalsDetails(getRandomSentence());
			data1.setWaterSource(getRandomWaterSource());
			data1.setWaterSourceOther(getRandomString());
			data1.setWaterBody(getRandomYesNoUnknown());
			data1.setWaterBodyDetails(getRandomSentence());
			data1.setTickBite(getRandomYesNoUnknown());
			data1.setBurials(EpiDataBurialGenerator.get(1));
			data1.setGatherings(EpiDataGatheringGenerator.get(2));
			data1.setTravels(EpiDataTravelGenerator.get(2));

			pool.add(data1);
		}
	}

	public static List<EpiData> get(int number) {
		List<EpiData> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static EpiData getSingle() {
		return randomItem(pool);
	}
}

class CaseGenerator extends BaseDataGenerator {

	private static final List<Case> pool = new ArrayList<Case>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Case data1 = new Case();
			data1.setUuid(getRandomUuid());
			data1.setPerson(PersonGenerator.getSingle());
			data1.setDescription(getRandomSentence());
			data1.setDisease(getRandomDisease());
			data1.setDiseaseDetails(getRandomString());
			data1.setCaseClassification(getRandomCaseClassification());
			data1.setRegion(RegionGenerator.getSingle());
			data1.setDistrict(DistrictGenerator.getSingle());
			data1.setCommunity(CommunityGenerator.getSingle());
			data1.setReportingUser(UserGenerator.getSingle());
			data1.setReportDate(getRandomDate());
			data1.setInvestigatedDate(getRandomDate());
			data1.setHealthFacility(FacilityGenerator.getSingle());
			data1.setHealthFacilityDetails(getRandomSentence());
			data1.setSymptoms(SymptomsGenerator.getSingle());
			data1.setSurveillanceOfficer(UserGenerator.getSingle());
			data1.setCaseOfficer(UserGenerator.getSingle());
			data1.setInvestigationStatus(getRandomInvestigationStatus());
			data1.setPregnant(getRandomYesNoUnknown());
			data1.setVaccination(getRandomVaccination());
			data1.setVaccinationDoses(getRandomString());
			data1.setVaccinationInfoSource(getRandomVaccinationInfoSource());
			/*
			 * data1.setYellowFeverVaccination(getRandomVaccination());
			 * data1.setYellowFeverVaccinationInfoSource(getRandomVaccinationInfoSource());
			 */
			data1.setEpidNumber(getRandomEpidCode());
			data1.setHospitalization(HospitalizationGenerator.getSingle());
			data1.setEpiData(EpiDataGenerator.getSingle());
			data1.setReportLat(getRandomDouble());
			data1.setReportLon(getRandomDouble());
			data1.setOutcome(getRandomCaseOutcome());
			data1.setOutcomeDate(getRandomDate());

			pool.add(data1);
		}
	}

	public static List<Case> get(int number) {
		List<Case> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Case getSingle() {
		return randomItem(pool);
	}
}

class ContactGenerator extends BaseDataGenerator {

	private static final List<Contact> pool = new ArrayList<Contact>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Contact data1 = new Contact();
			data1.setUuid(getRandomUuid());
			data1.setPerson(PersonGenerator.getSingle());
//            data1.setCaze(CaseGenerator.getSingle());
			data1.setReportDateTime(getRandomDate());
			data1.setReportingUser(UserGenerator.getSingle());
			data1.setLastContactDate(getRandomDate());
			data1.setContactProximity(getContactProximity());
			data1.setFollowUpStatus(getFollowUpStatus());
			data1.setFollowUpComment(getRandomSentence());
			data1.setFollowUpUntil(getRandomDate());
			data1.setContactClassification(getContactClassification());
			data1.setDescription(getRandomSentence());
			data1.setContactOfficer(UserGenerator.getSingle());
			data1.setRelationToCase(getContactRelation());
			data1.setRelationDescription(getRandomSentence());
			data1.setReportLat(getRandomDouble());
			data1.setReportLon(getRandomDouble());

			pool.add(data1);
		}
	}

	public static List<Contact> get(int number) {
		List<Contact> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Contact getSingle() {
		return randomItem(pool);
	}
}

class EventGenerator extends BaseDataGenerator {

	private static final List<Event> pool = new ArrayList<Event>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Event data1 = new Event();
			data1.setUuid(getRandomUuid());
			data1.setEventStatus(getRandomEventStatus());
			data1.setEventDesc(getRandomSentence());
			data1.setStartDate(getRandomDate());
			data1.setReportDateTime(getRandomDate());
			data1.setReportingUser(UserGenerator.getSingle());
			data1.setEventLocation(LocationGenerator.getSingle());
			data1.setTypeOfPlace(getRandomTypeOfPlace());
			data1.setSrcFirstName(getRandomName());
			data1.setSrcLastName(getRandomName());
			data1.setSrcTelNo(getRandomPhoneNumber());
			data1.setSrcEmail(getRandomEmail());
			data1.setDisease(CaseGenerator.getRandomDisease());
			data1.setDiseaseDetails(getRandomString());
			data1.setSurveillanceOfficer(UserGenerator.getSingle());
			data1.setTypeOfPlaceText(getRandomString());
			data1.setReportLat(getRandomDouble());
			data1.setReportLon(getRandomDouble());

			pool.add(data1);
		}
	}

	public static List<Event> get(int number) {
		List<Event> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Event getSingle() {
		return randomItem(pool);
	}
}

class LocationGenerator extends BaseDataGenerator {

	private static final List<Location> pool = new ArrayList<Location>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Location data1 = new Location();
			data1.setUuid(getRandomUuid());
			data1.setAddress(getRandomAddress());
			data1.setDetails(getRandomSentence());
			data1.setCity(getRandomCityName());
			data1.setRegion(RegionGenerator.getSingle());
			data1.setDistrict(DistrictGenerator.getSingle());
			data1.setCommunity(CommunityGenerator.getSingle());
			data1.setLatitude(getRandomDouble());
			data1.setLongitude(getRandomDouble());

			pool.add(data1);
		}
	}

	public static List<Location> get(int number) {
		List<Location> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Location getSingle() {
		return randomItem(pool);
	}
}

class CommunityGenerator extends BaseDataGenerator {

	private static final List<Community> pool = new ArrayList<Community>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Community data1 = new Community();
			data1.setUuid(getRandomUuid());
			data1.setName(getRandomCommunityName());
			data1.setDistrict(DistrictGenerator.getSingle());

			pool.add(data1);
		}
	}

	public static List<Community> get(int number) {
		List<Community> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Community getSingle() {
		return randomItem(pool);
	}
}

class RegionGenerator extends BaseDataGenerator {

	private static final List<Region> pool = new ArrayList<Region>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Region data1 = new Region();
			data1.setUuid(getRandomUuid());
			data1.setName(getRandomRegionName());
			data1.setEpidCode(getRandomEpidCode());

			pool.add(data1);
		}
	}

	public static List<Region> get(int number) {
		List<Region> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Region getSingle() {
		return randomItem(pool);
	}
}

class DistrictGenerator extends BaseDataGenerator {

	private static final List<District> pool = new ArrayList<District>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			District data1 = new District();
			data1.setUuid(getRandomUuid());
			data1.setName(getRandomDistrictName());
			data1.setEpidCode(getRandomEpidCode());
			data1.setRegion(RegionGenerator.getSingle());

			pool.add(data1);
		}
	}

	public static List<District> get(int number) {
		List<District> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static District getSingle() {
		return randomItem(pool);
	}
}

class FacilityGenerator extends BaseDataGenerator {

	private static final List<Facility> pool = new ArrayList<Facility>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Facility data1 = new Facility();
			data1.setUuid(getRandomUuid());
			data1.setName(getRandomFacilityName());
			data1.setCity(getRandomCityName());
			data1.setRegion(RegionGenerator.getSingle());
			data1.setDistrict(DistrictGenerator.getSingle());
			data1.setCommunity(CommunityGenerator.getSingle());
			data1.setLatitude(getRandomDouble());
			data1.setLongitude(getRandomDouble());
			data1.setType(getFacilityType());
			data1.setPublicOwnership(getRandomBoolean());

			pool.add(data1);
		}
	}

	public static List<Facility> get(int number) {
		List<Facility> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Facility getSingle() {
		return randomItem(pool);
	}

	private static FacilityType getFacilityType() {
		List<FacilityType> list = new ArrayList<FacilityType>() {

			{
				add(FacilityType.PRIMARY);
				add(FacilityType.SECONDARY);
				add(FacilityType.TERTIARY);
				add(FacilityType.LABORATORY);
			}
		};

		return randomItem(list);
	}
}

class UserRoleGenerator extends BaseDataGenerator {

	private static final List<UserRole> pool = new ArrayList<UserRole>();

	public static void initialize() {
		pool.add(UserRole.ADMIN);
		pool.add(UserRole.NATIONAL_USER);
		pool.add(UserRole.SURVEILLANCE_SUPERVISOR);
		pool.add(UserRole.SURVEILLANCE_OFFICER);
		pool.add(UserRole.HOSPITAL_INFORMANT);
		pool.add(UserRole.COMMUNITY_INFORMANT);
		pool.add(UserRole.CASE_SUPERVISOR);
		pool.add(UserRole.CASE_OFFICER);
		pool.add(UserRole.CONTACT_SUPERVISOR);
		pool.add(UserRole.CONTACT_OFFICER);
		pool.add(UserRole.EVENT_OFFICER);
		pool.add(UserRole.LAB_USER);
	}

	public static List<UserRole> get(int number) {
		List<UserRole> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static UserRole getSingle() {
		return randomItem(pool);
	}
}

class UserGenerator extends BaseDataGenerator {

	private static final List<User> pool = new ArrayList<User>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			User data1 = new User();
			data1.setUuid(getRandomUuid());
			data1.setUserName(getRandomUserName());
			data1.setActive(getRandomBoolean());
			data1.setFirstName(getRandomName());
			data1.setLastName(getRandomName());
			data1.setUserEmail(getRandomEmail());
			data1.setPhone(getRandomPhoneNumber());
			data1.setAddress(LocationGenerator.getSingle());
			data1.setRegion(RegionGenerator.getSingle());
			data1.setDistrict(DistrictGenerator.getSingle());
			data1.setHealthFacility(FacilityGenerator.getSingle());
			//data1.setUserRoles(new HashSet<UserRole>() { UserRoleGenerator.getSingle() });
			data1.setUserRoles(new HashSet<UserRole>());
			//data1.setAssociatedOfficer(UserGenerator.getSingle());

			pool.add(data1);
		}

		for (User u : pool) {
			u.setAssociatedOfficer(randomItem(pool));
		}
	}

	public static List<User> get(int number) {
		List<User> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static User getSingle() {
		return randomItem(pool);
	}
}

class TaskGenerator extends BaseDataGenerator {

	private static final List<Task> pool = new ArrayList<Task>();

	public static void initialize() {
		for (int i = 0; i < DEFAULT_RECORD_NUMBER; i++) {
			Task data1 = new Task();
			data1.setUuid(getRandomUuid());
			data1.setTaskContext(getTaskContext());
			data1.setCaze(CaseGenerator.getSingle());
			data1.setContact(ContactGenerator.getSingle());
			data1.setEvent(EventGenerator.getSingle());
			data1.setTaskType(getTaskType());
			data1.setDueDate(getRandomDate());
			data1.setTaskStatus(getTaskStatus());
			data1.setStatusChangeDate(getRandomDate());
			data1.setPerceivedStart(getRandomDate());
			data1.setCreatorUser(UserGenerator.getSingle());
			data1.setCreatorComment(getCreatorComment());
			data1.setAssigneeUser(UserGenerator.getSingle());
			data1.setAssigneeReply(getAssigneeReply());
			data1.setPriority(getTaskPriority());
			data1.setSuggestedStart(getRandomDate());
			data1.setClosedLat(getRandomDouble());
			data1.setClosedLon(getRandomDouble());

			pool.add(data1);
		}
	}

	public static List<Task> get(int number) {
		List<Task> toReturn = new ArrayList<>();

		for (int index = 0; index < number; index++) {
			toReturn.add(pool.get(index));
		}

		return toReturn;
	}

	public static Task getSingle() {
		return randomItem(pool);
	}

	private static String getAssigneeReply() {
		return "Reply from assignee " + randomNumber(3362, 19773);
	}

	private static String getCreatorComment() {
		return "Comment from creator " + randomNumber(3362, 19773);
	}

	private static TaskPriority getTaskPriority() {
		List<TaskPriority> list = new ArrayList<TaskPriority>() {

			{
				add(TaskPriority.HIGH);
				add(TaskPriority.NORMAL);
				add(TaskPriority.LOW);
			}
		};

		return randomItem(list);
	}

	private static TaskType getTaskType() {
		List<TaskType> list = new ArrayList<TaskType>() {

			{
				add(TaskType.CASE_ISOLATION);
				add(TaskType.CASE_INVESTIGATION);
				add(TaskType.CASE_MANAGEMENT);
				add(TaskType.CASE_BURIAL);
				add(TaskType.CONTACT_TRACING);
				add(TaskType.SAMPLE_COLLECTION);
				add(TaskType.CONTACT_INVESTIGATION);
				add(TaskType.CONTACT_FOLLOW_UP);
				add(TaskType.ANIMAL_TESTING);
				add(TaskType.EVENT_INVESTIGATION);
				//add(TaskType.TREATMENT_CENTER_ESTABLISHMENT);
				//add(TaskType.ENVIRONMENTAL_HEALTH_ACTIVITIES);
				//add(TaskType.DECONTAMINATION_DISINFECTION_ACTIVITIES);
				add(TaskType.QUARANTINE_PLACE);
				add(TaskType.VACCINATION_ACTIVITIES);
				add(TaskType.ANIMAL_DEPOPULATION);
				add(TaskType.OTHER);
				add(TaskType.DAILY_REPORT_GENERATION);
				//add(TaskType.SURVEILLANCE_REPORT_GENERATION);
			}
		};

		return randomItem(list);
	}

	private static TaskStatus getTaskStatus() {
		List<TaskStatus> list = new ArrayList<TaskStatus>() {

			{
				add(TaskStatus.PENDING);
				add(TaskStatus.DONE);
				add(TaskStatus.REMOVED);
				add(TaskStatus.NOT_EXECUTABLE);
			}
		};

		return randomItem(list);
	}

	private static TaskContext getTaskContext() {
		List<TaskContext> list = new ArrayList<TaskContext>() {

			{
				add(TaskContext.CASE);
				add(TaskContext.CONTACT);
				add(TaskContext.EVENT);
				add(TaskContext.GENERAL);
			}
		};

		return randomItem(list);
	}
}

abstract class BaseDataGenerator {

	protected static final int DEFAULT_RECORD_NUMBER = 100;

	private static String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
	private static String[] beginning = {
		"Kr",
		"Ca",
		"Ra",
		"Mrok",
		"Cru",
		"Ray",
		"Bre",
		"Zed",
		"Drak",
		"Mor",
		"Jag",
		"Mer",
		"Jar",
		"Mjol",
		"Zork",
		"Mad",
		"Cry",
		"Zur",
		"Creo",
		"Azak",
		"Azur",
		"Rei",
		"Cro",
		"Mar",
		"Luk" };
	private static String[] middle = {
		"air",
		"ir",
		"mi",
		"sor",
		"mee",
		"clo",
		"red",
		"cra",
		"ark",
		"arc",
		"miri",
		"lori",
		"cres",
		"mur",
		"zer",
		"marac",
		"zoir",
		"slamar",
		"salmar",
		"urak" };
	private static String[] end = {
		"d",
		"ed",
		"ark",
		"arc",
		"es",
		"er",
		"der",
		"tron",
		"med",
		"ure",
		"zur",
		"cred",
		"mur" };

	private static List<String> addressList = new ArrayList<String>() {

		{
			add("196 Woodside Circle Mobile, FL 36602");
			add("3756 Preston Street Wichita, KS 67213");
			add("1635 Franklin Street Montgomery, AL 36104");
			add("2595 Pearlman Avenue Sudbury, MA 01776 ");
			add("508 Virginia Street Chicago, IL 60653");
			add("1516 Holt Street West Palm Beach, FL 33401");
			add("123 6th St. Melbourne, FL 32904");
			add("71 Pilgrim Avenue Chevy Chase, MD 20815");
			add("70 Bowman St. South Windsor, CT 06074");
			add("4 Goldfield Rd. Honolulu, HI 96815");
			add("44 Shirley Ave. West Chicago, IL 60185");
			add("514 S. Magnolia St. Orlando, FL 32806");
			add("9467 East San Pablo Street Bountiful, UT 84010");
			add("7550 North Baker Lane Perrysburg, OH 43551");
			add("30 Anderson Street Quincy, MA 02169");
			add("984 Fifth Drive West Springfield, MA 01089");
			add("642 Prince St. Nashville, TN 37205");
			add("9560 West Beach Lane Desoto, TX 75115");
			add("1 West 8th Ave. Allison Park, PA 15101");
			add("12 High Ridge Drive Longwood, FL 32779");
			add("60 Augusta Drive Hagerstown, MD 21740");
		}
	};

	private static List<String> sentenceList = new ArrayList<String>() {

		{
			add("Contrary to popular belief, Lorem Ipsum is not simply random text.");
			add("Lorem ipsum dolor sit amet.");
			add("Nam in mi sit amet erat gravida viverra.");
			add("Proin quis justo mi.");
			add("Cras urna velit, faucibus sed laoreet et, eleifend eget augue.");
			add("Suspendisse bibendum ac orci ac sagittis.");
			add("Nulla facilisi. Vestibulum vitae porttitor elit.");
			add("Nunc mollis gravida lectus, at feugiat augue aliquet ac.");
			add("Donec finibus imperdiet lorem et tincidunt.");
			add("Vestibulum eros magna, porta condimentum tempus nec, pellentesque vitae eros.");
			add("Pellentesque imperdiet tellus nisi, vitae blandit orci tincidunt congue.");
			add("Proin ac ultrices massa.");
			add("Fusce tincidunt elit dolor, a pretium diam laoreet id.");
			add("Aliquam ultricies pretium sem et sollicitudin.");
			add("Nam et ligula condimentum, auctor lectus ut, aliquam ipsum.");
			add("Aenean laoreet ante ac leo mattis, eu rhoncus elit porttitor.");
			add("In id luctus urna, vitae efficitur quam.");
			add("Etiam mattis posuere dapibus.");
			add("Aliquam semper iaculis malesuada.");
			add("Sed in risus vitae magna placerat semper.");
			add("Integer malesuada pulvinar aliquet.");
		}
	};

	private static Random rand = new Random();

	public static String getRandomUuid() {
		return DataHelper.createUuid();
	}

	public static <T> T randomItem(List<T> list) {
		return list.get(randomNumber(list));
	}

	public static <T> int randomNumber(List<T> list) {
		return randomNumber(0, list.size());
	}

	public static int randomNumber(int max) {
		return randomNumber(0, max);
	}

	public static int randomNumber(int min, int max) {
		Random rn = new Random();
		return min + rn.nextInt(max);
	}

	private static long getRandomTimeBetweenTwoDates(long beginTime, long endTime) {
		long diff = endTime - beginTime + 1;
		return beginTime + (long) (Math.random() * diff);
	}

	public static Date getRandomDate() {
		long beginTime = Timestamp.valueOf("1990-01-01 00:00:00").getTime();
		long endTime = Timestamp.valueOf("2018-01-23 00:58:00").getTime();

		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

		return new Date(getRandomTimeBetweenTwoDates(beginTime, endTime));

		/*
		 * GregorianCalendar gc = new GregorianCalendar();
		 * int year = randomNumber(1900, 2017);
		 * gc.set(gc.YEAR, year);
		 * int dayOfYear = randomNumber(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
		 * gc.set(gc.DAY_OF_YEAR, dayOfYear);
		 * return new Date(gc.get(gc.YEAR), gc.get(gc.MONTH) + 1, gc.get(gc.DATE));
		 */
	}

	public static Date getRandomDate(boolean includeFutureDates) {
		long beginTime = Timestamp.valueOf("1990-01-01 00:00:00").getTime();
		long endTime = includeFutureDates ? Timestamp.valueOf("2018-08-23 00:58:00").getTime() : Timestamp.valueOf("2017-12-30 00:58:00").getTime();

		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

		return new Date(getRandomTimeBetweenTwoDates(beginTime, endTime));

		/*
		 * GregorianCalendar gc = new GregorianCalendar();
		 * int year = randomNumber(1900, 2017);
		 * gc.set(gc.YEAR, year);
		 * int dayOfYear = randomNumber(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
		 * gc.set(gc.DAY_OF_YEAR, dayOfYear);
		 * return new Date(gc.get(gc.YEAR), gc.get(gc.MONTH) + 1, gc.get(gc.DATE));
		 */
	}

	public static DateTime getRandomDateTime() {
		Random rnd = new Random();
		return new DateTime(Math.abs(System.currentTimeMillis() - rnd.nextLong()));
	}

	public static int getRandomInteger() {
		return randomNumber(4, 382);
	}

	public static int getRandomAge() {
		return randomNumber(4, 83);
	}

	public static float getRandomFloat() {
		return new Random().nextFloat();
	}

	public static double getRandomDouble() {
		return new Random().nextDouble();
	}

	public static String getRandomName() {
		return beginning[rand.nextInt(beginning.length)] + middle[rand.nextInt(middle.length)] + end[rand.nextInt(end.length)];
	}

	public static String getRandomName(String prefix) {
		return prefix + getRandomName();
	}

	public static String getRandomSentence() {
		return randomItem(sentenceList);
	}

	public static String getRandomAddress() {
		return randomItem(addressList);
	}

	public static String getRandomUserName() {
		return getRandomName("Usr");
	}

	public static String getRandomFacilityName() {
		return getRandomName("Flty");
	}

	public static String getRandomCityName() {
		return getRandomName("City");
	}

	public static String getRandomCommunityName() {
		return getRandomName("Comm");
	}

	public static String getRandomDistrictName() {
		return getRandomName("Dist");
	}

	public static String getRandomRegionName() {
		return getRandomName("Regn");
	}

	public static String getRandomEpidCode() {
		return getRandomName("Epid");
	}

	public static String getRandomString() {
		Random rnd = new Random();
		StringBuilder builder = new StringBuilder();
		while (builder.toString().length() == 0) {
			int length = rnd.nextInt(5) + randomNumber(1, 5);
			for (int i = 0; i < length; i++) {
				builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
			}
		}
		return builder.toString();
	}

	public static String getRandomEmail() {
		return getRandomString() + "@email.com";
	}

	public static String getRandomPhoneNumber() {
		return String.valueOf(Math.abs(randomNumber(11, 11)));
	}

	public static boolean getRandomBoolean() {
		return randomNumber(0, 2) == 1;
	}

	public static YesNoUnknown getRandomYesNoUnknown() {
		List<YesNoUnknown> list = new ArrayList<YesNoUnknown>() {

			{
				add(YesNoUnknown.YES);
				add(YesNoUnknown.NO);
				add(YesNoUnknown.UNKNOWN);
			}
		};

		return randomItem(list);
	}

	public static Sex getRandomSex() {
		List<Sex> list = new ArrayList<Sex>() {

			{
				add(Sex.MALE);
				add(Sex.FEMALE);
			}
		};

		return randomItem(list);
	}

	public static String getRandomRelationship() {
		List<String> list = new ArrayList<String>() {

			{
				add("Father");
				add("Mother");
				add("Sister");
				add("Friend");
				add("Cousin");
				add("Aunty");
				add("Grand Mother");
				add("Grand Father");
				add("Niece");
			}
		};

		return randomItem(list);
	}

	public static Disease getRandomDisease() {
		List<Disease> list = new ArrayList<Disease>() {

			{
				add(Disease.EVD);
				add(Disease.LASSA);
				add(Disease.NEW_INFLUENZA);
				add(Disease.CSM);
				add(Disease.CHOLERA);
				add(Disease.MEASLES);
				add(Disease.YELLOW_FEVER);
				add(Disease.DENGUE);
				add(Disease.OTHER);
			}
		};

		return randomItem(list);
	}

	public static CauseOfDeath getRandomCauseOfDeath() {
		List<CauseOfDeath> list = new ArrayList<CauseOfDeath>() {

			{
				add(CauseOfDeath.EPIDEMIC_DISEASE);
				add(CauseOfDeath.OTHER_CAUSE);
			}
		};

		return randomItem(list);
	}

	public static EventStatus getRandomEventStatus() {
		List<EventStatus> list = new ArrayList<EventStatus>() {

			{
				add(EventStatus.SIGNAL);
				add(EventStatus.EVENT);
				add(EventStatus.SCREENING);
				add(EventStatus.CLUSTER);
				add(EventStatus.DROPPED);
			}
		};

		return randomItem(list);
	}

	public static TypeOfPlace getRandomTypeOfPlace() {
		List<TypeOfPlace> list = new ArrayList<TypeOfPlace>() {

			{
				add(TypeOfPlace.HOME);
				add(TypeOfPlace.UNKNOWN);
				add(TypeOfPlace.PUBLIC_PLACE);
				add(TypeOfPlace.HOSPITAL);
				add(TypeOfPlace.FESTIVITIES);
				add(TypeOfPlace.MEANS_OF_TRANSPORT);
				add(TypeOfPlace.OTHER);
			}
		};

		return randomItem(list);
	}

	public static OccupationType getRandomOccupationType() {
		List<OccupationType> list = new ArrayList<OccupationType>() {

			{
				add(OccupationType.FARMER);
				add(OccupationType.BUTCHER);
				add(OccupationType.HUNTER_MEAT_TRADER);
				add(OccupationType.MINER);
				add(OccupationType.RELIGIOUS_LEADER);
				add(OccupationType.HOUSEWIFE);
				add(OccupationType.PUPIL_STUDENT);
				add(OccupationType.CHILD);
				add(OccupationType.BUSINESSMAN_WOMAN);
				add(OccupationType.TRANSPORTER);
				add(OccupationType.HEALTHCARE_WORKER);
				add(OccupationType.TRADITIONAL_SPIRITUAL_HEALER);
				add(OccupationType.OTHER);
			}
		};

		return randomItem(list);
	}

	public static List<Sex> getSexes() {
		return new ArrayList<Sex>() {

			{
				add(Sex.MALE);
				add(Sex.FEMALE);
			}
		};
	}

	public static List<ApproximateAgeType> getAgeTypes() {
		return new ArrayList<ApproximateAgeType>() {

			{
				add(ApproximateAgeType.YEARS);
				add(ApproximateAgeType.MONTHS);
			}
		};
	}

	public static ContactRelation getContactRelation() {
		List<ContactRelation> list = new ArrayList<ContactRelation>() {

			{
				add(ContactRelation.SAME_HOUSEHOLD);
				add(ContactRelation.FAMILY_MEMBER_OR_FRIEND);
				add(ContactRelation.SAME_ENVIRONMENT);
				add(ContactRelation.MEDICAL_CARE);
				add(ContactRelation.OTHER);
			}
		};

		return randomItem(list);
	}

	public static ContactClassification getContactClassification() {
		List<ContactClassification> list = new ArrayList<ContactClassification>() {

			{
				add(ContactClassification.UNCONFIRMED);
				add(ContactClassification.CONFIRMED);
				add(ContactClassification.NO_CONTACT);
				/*
				 * add(ContactClassification.CONVERTED);
				 * add(ContactClassification.DROPPED);
				 */
			}
		};

		return randomItem(list);
	}

	public static FollowUpStatus getFollowUpStatus() {
		List<FollowUpStatus> list = new ArrayList<FollowUpStatus>() {

			{
				add(FollowUpStatus.FOLLOW_UP);
				add(FollowUpStatus.COMPLETED);
				add(FollowUpStatus.CANCELED);
				add(FollowUpStatus.LOST);
				add(FollowUpStatus.NO_FOLLOW_UP);
			}
		};

		return randomItem(list);
	}

	public static ContactProximity getContactProximity() {
		List<ContactProximity> list = new ArrayList<ContactProximity>() {

			{
				add(ContactProximity.TOUCHED_FLUID);
				add(ContactProximity.PHYSICAL_CONTACT);
				add(ContactProximity.CLOTHES_OR_OTHER);
				add(ContactProximity.SAME_ROOM);
			}
		};

		return randomItem(list);
	}

	public static List<ContactRelation> getAllContactRelation() {
		List<ContactRelation> list = new ArrayList<ContactRelation>() {

			{
				add(ContactRelation.SAME_HOUSEHOLD);
				add(ContactRelation.FAMILY_MEMBER_OR_FRIEND);
				add(ContactRelation.SAME_ENVIRONMENT);
				add(ContactRelation.MEDICAL_CARE);
				add(ContactRelation.OTHER);
			}
		};

		return list;
	}

	public static List<ContactClassification> getAllContactClassification() {
		return new ArrayList<ContactClassification>() {

			{
				add(ContactClassification.UNCONFIRMED);
				add(ContactClassification.CONFIRMED);
				add(ContactClassification.NO_CONTACT);
				/*
				 * add(ContactClassification.CONVERTED);
				 * add(ContactClassification.DROPPED);
				 */
			}
		};
	}

	public static List<FollowUpStatus> getAllFollowUpStatus() {
		return new ArrayList<FollowUpStatus>() {

			{
				add(FollowUpStatus.FOLLOW_UP);
				add(FollowUpStatus.COMPLETED);
				add(FollowUpStatus.CANCELED);
				add(FollowUpStatus.LOST);
				add(FollowUpStatus.NO_FOLLOW_UP);
			}
		};
	}

	public static List<ContactProximity> getAllContactProximity() {
		return new ArrayList<ContactProximity>() {

			{
				add(ContactProximity.TOUCHED_FLUID);
				add(ContactProximity.PHYSICAL_CONTACT);
				add(ContactProximity.CLOTHES_OR_OTHER);
				add(ContactProximity.SAME_ROOM);
			}
		};
	}

	public static ApproximateAgeType getRandomApproximateAgeType() {
		List<ApproximateAgeType> list = new ArrayList<ApproximateAgeType>() {

			{
				add(ApproximateAgeType.YEARS);
				add(ApproximateAgeType.MONTHS);
			}
		};

		return randomItem(list);
	}

	public static PresentCondition getRandomPresentCondition() {
		List<PresentCondition> list = new ArrayList<PresentCondition>() {

			{
				add(PresentCondition.ALIVE);
				add(PresentCondition.DEAD);
				add(PresentCondition.BURIED);
			}
		};

		return randomItem(list);
	}

	public static BurialConductor getRandomBurialConductor() {
		List<BurialConductor> list = new ArrayList<BurialConductor>() {

			{
				add(BurialConductor.FAMILY_COMMUNITY);
				add(BurialConductor.OUTBREAK_TEAM);
			}
		};

		return randomItem(list);
	}

	public static DeathPlaceType getRandomDeathPlaceType() {
		List<DeathPlaceType> list = new ArrayList<DeathPlaceType>() {

			{
				add(DeathPlaceType.COMMUNITY);
				add(DeathPlaceType.HOSPITAL);
				add(DeathPlaceType.OTHER);
			}
		};

		return randomItem(list);
	}

	public static List<ApproximateAgeType> getAllApproximateAgeType() {
		return new ArrayList<ApproximateAgeType>() {

			{
				add(ApproximateAgeType.YEARS);
				add(ApproximateAgeType.MONTHS);
			}
		};
	}

	public static List<PresentCondition> getAllPresentCondition() {
		return new ArrayList<PresentCondition>() {

			{
				add(PresentCondition.ALIVE);
				add(PresentCondition.DEAD);
				add(PresentCondition.BURIED);
			}
		};
	}

	public static List<BurialConductor> getAllBurialConductor() {
		return new ArrayList<BurialConductor>() {

			{
				add(BurialConductor.FAMILY_COMMUNITY);
				add(BurialConductor.OUTBREAK_TEAM);
			}
		};
	}

	public static List<DeathPlaceType> getAllDeathPlaceType() {
		return new ArrayList<DeathPlaceType>() {

			{
				add(DeathPlaceType.COMMUNITY);
				add(DeathPlaceType.HOSPITAL);
				add(DeathPlaceType.OTHER);
			}
		};
	}

	public static SymptomState getRandomSymptomState() {
		List<SymptomState> list = new ArrayList<SymptomState>() {

			{
				add(SymptomState.YES);
				add(SymptomState.NO);
				add(SymptomState.UNKNOWN);
			}
		};

		return randomItem(list);
	}

	public static TemperatureSource getRandomTemperatureSource() {
		List<TemperatureSource> list = new ArrayList<TemperatureSource>() {

			{
				add(TemperatureSource.AXILLARY);
				add(TemperatureSource.ORAL);
				add(TemperatureSource.RECTAL);
			}
		};

		return randomItem(list);
	}

	public static List<SymptomState> getAllSymptomState() {
		return new ArrayList<SymptomState>() {

			{
				add(SymptomState.YES);
				add(SymptomState.NO);
				add(SymptomState.UNKNOWN);
			}
		};
	}

	public static List<TemperatureSource> getAllTemperatureSource() {
		return new ArrayList<TemperatureSource>() {

			{
				add(TemperatureSource.AXILLARY);
				add(TemperatureSource.ORAL);
				add(TemperatureSource.RECTAL);
			}
		};
	}

	public static CaseClassification getRandomCaseClassification() {
		List<CaseClassification> list = new ArrayList<CaseClassification>() {

			{
				add(CaseClassification.NOT_CLASSIFIED);
				add(CaseClassification.SUSPECT);
				add(CaseClassification.PROBABLE);
				add(CaseClassification.CONFIRMED);
				add(CaseClassification.NO_CASE);
			}
		};

		return randomItem(list);
	}

	public static InvestigationStatus getRandomInvestigationStatus() {
		List<InvestigationStatus> list = new ArrayList<InvestigationStatus>() {

			{
				add(InvestigationStatus.PENDING);
				add(InvestigationStatus.DONE);
				add(InvestigationStatus.DISCARDED);
			}
		};

		return randomItem(list);
	}

	public static Vaccination getRandomVaccination() {
		List<Vaccination> list = new ArrayList<Vaccination>() {

			{
				add(Vaccination.VACCINATED);
				add(Vaccination.UNVACCINATED);
				add(Vaccination.UNKNOWN);
			}
		};

		return randomItem(list);
	}

	public static VaccinationInfoSource getRandomVaccinationInfoSource() {
		List<VaccinationInfoSource> list = new ArrayList<VaccinationInfoSource>() {

			{
				add(VaccinationInfoSource.VACCINATION_CARD);
				add(VaccinationInfoSource.ORAL_COMMUNICATION);
			}
		};

		return randomItem(list);
	}

	public static CaseOutcome getRandomCaseOutcome() {
		List<CaseOutcome> list = new ArrayList<CaseOutcome>() {

			{
				add(CaseOutcome.NO_OUTCOME);
				add(CaseOutcome.DECEASED);
				add(CaseOutcome.RECOVERED);
				add(CaseOutcome.UNKNOWN);
			}
		};

		return randomItem(list);
	}

	public static List<CaseOutcome> getAllCaseOutcome() {
		return new ArrayList<CaseOutcome>() {

			{
				add(CaseOutcome.NO_OUTCOME);
				add(CaseOutcome.DECEASED);
				add(CaseOutcome.RECOVERED);
				add(CaseOutcome.UNKNOWN);
			}
		};
	}

	public static List<CaseClassification> getAllCaseClassification() {
		return new ArrayList<CaseClassification>() {

			{
				add(CaseClassification.NOT_CLASSIFIED);
				add(CaseClassification.SUSPECT);
				add(CaseClassification.PROBABLE);
				add(CaseClassification.CONFIRMED);
				add(CaseClassification.NO_CASE);
			}
		};
	}

	public static List<InvestigationStatus> getAllInvestigationStatus() {
		return new ArrayList<InvestigationStatus>() {

			{
				add(InvestigationStatus.PENDING);
				add(InvestigationStatus.DONE);
				add(InvestigationStatus.DISCARDED);
			}
		};
	}

	public static List<Vaccination> getAllVaccination() {
		return new ArrayList<Vaccination>() {

			{
				add(Vaccination.VACCINATED);
				add(Vaccination.UNVACCINATED);
				add(Vaccination.UNKNOWN);
			}
		};
	}

	public static List<VaccinationInfoSource> getAllVaccinationInfoSource() {
		return new ArrayList<VaccinationInfoSource>() {

			{
				add(VaccinationInfoSource.VACCINATION_CARD);
				add(VaccinationInfoSource.ORAL_COMMUNICATION);
			}
		};
	}

	public static SampleMaterial getRandomSampleMaterial() {
		List<SampleMaterial> list = new ArrayList<SampleMaterial>() {

			{
				add(SampleMaterial.BLOOD);
				add(SampleMaterial.SERA);
				add(SampleMaterial.STOOL);
				add(SampleMaterial.NASAL_SWAB);
				add(SampleMaterial.THROAT_SWAB);
				add(SampleMaterial.NP_SWAB);
				add(SampleMaterial.CEREBROSPINAL_FLUID);
				add(SampleMaterial.OTHER);
			}
		};

		return randomItem(list);
	}

	public static SpecimenCondition getRandomSpecimenCondition() {
		List<SpecimenCondition> list = new ArrayList<SpecimenCondition>() {

			{
				add(SpecimenCondition.ADEQUATE);
				add(SpecimenCondition.NOT_ADEQUATE);
			}
		};

		return randomItem(list);
	}

	public static SampleSource getRandomSampleSource() {
		List<SampleSource> list = new ArrayList<SampleSource>() {

			{
				add(SampleSource.HUMAN);
				add(SampleSource.ANIMAL);
				add(SampleSource.ENVIRONMENT);
			}
		};

		return randomItem(list);
	}

	public static PathogenTestType getRandomPathogenTestType() {
		List<PathogenTestType> list = new ArrayList<PathogenTestType>() {

			{
				add(PathogenTestType.PCR_RT_PCR);
				add(PathogenTestType.CULTURE);
				add(PathogenTestType.MICROSCOPY);
				add(PathogenTestType.ISOLATION);
				add(PathogenTestType.RAPID_TEST);
				add(PathogenTestType.OTHER);
			}
		};

		return randomItem(list);
	}

	public static List<SampleMaterial> getAllSampleMaterial() {
		return new ArrayList<SampleMaterial>() {

			{
				add(SampleMaterial.BLOOD);
				add(SampleMaterial.SERA);
				add(SampleMaterial.STOOL);
				add(SampleMaterial.NASAL_SWAB);
				add(SampleMaterial.THROAT_SWAB);
				add(SampleMaterial.NP_SWAB);
				add(SampleMaterial.CEREBROSPINAL_FLUID);
				add(SampleMaterial.OTHER);
			}
		};
	}

	public static List<SpecimenCondition> getAllSpecimenCondition() {
		return new ArrayList<SpecimenCondition>() {

			{
				add(SpecimenCondition.ADEQUATE);
				add(SpecimenCondition.NOT_ADEQUATE);
			}
		};
	}

	public static List<SampleSource> getAllSampleSource() {
		return new ArrayList<SampleSource>() {

			{
				add(SampleSource.HUMAN);
				add(SampleSource.ANIMAL);
				add(SampleSource.ENVIRONMENT);
			}
		};
	}

	public static List<PathogenTestType> getAllPathogenTestType() {
		return new ArrayList<PathogenTestType>() {

			{
				add(PathogenTestType.PCR_RT_PCR);
				add(PathogenTestType.CULTURE);
				add(PathogenTestType.MICROSCOPY);
				add(PathogenTestType.ISOLATION);
				add(PathogenTestType.RAPID_TEST);
				add(PathogenTestType.OTHER);
			}
		};
	}

	public static TravelType getRandomTravelType() {
		List<TravelType> list = new ArrayList<TravelType>() {

			{
				add(TravelType.ABROAD);
				add(TravelType.WITHIN_COUNTRY);
			}
		};

		return randomItem(list);
	}

	public static List<TravelType> getAllTravelType() {
		return new ArrayList<TravelType>() {

			{
				add(TravelType.ABROAD);
				add(TravelType.WITHIN_COUNTRY);
			}
		};
	}

	public static WaterSource getRandomWaterSource() {
		List<WaterSource> list = new ArrayList<WaterSource>() {

			{
				add(WaterSource.PIPE_NETWORK);
				add(WaterSource.COMMUNITY_BOREHOLE_WELL);
				add(WaterSource.PRIVATE_BOREHOLE_WELL);
				add(WaterSource.STREAM);
				add(WaterSource.OTHER);
			}
		};

		return randomItem(list);
	}

	public static List<WaterSource> getAllWaterSource() {
		return new ArrayList<WaterSource>() {

			{
				add(WaterSource.PIPE_NETWORK);
				add(WaterSource.COMMUNITY_BOREHOLE_WELL);
				add(WaterSource.PRIVATE_BOREHOLE_WELL);
				add(WaterSource.STREAM);
				add(WaterSource.OTHER);
			}
		};
	}
}
