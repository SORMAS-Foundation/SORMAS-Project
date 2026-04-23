/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.exposure;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.AnimalCategory;
import de.symeda.sormas.api.exposure.AnimalContactType;
import de.symeda.sormas.api.exposure.AnimalLocation;
import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.api.exposure.ExposureContactFactor;
import de.symeda.sormas.api.exposure.ExposureProtectiveMeasure;
import de.symeda.sormas.api.exposure.ExposureRole;
import de.symeda.sormas.api.exposure.ExposureSetting;
import de.symeda.sormas.api.exposure.ExposureSubSetting;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.FomiteTransmissionLocation;
import de.symeda.sormas.api.exposure.GatheringType;
import de.symeda.sormas.api.exposure.HabitationType;
import de.symeda.sormas.api.exposure.SwimmingLocation;
import de.symeda.sormas.api.exposure.TravelAccommodation;
import de.symeda.sormas.api.exposure.TravelPurpose;
import de.symeda.sormas.api.exposure.ProphylaxisAdherence;
import de.symeda.sormas.api.exposure.TypeOfAnimal;
import de.symeda.sormas.api.exposure.TypeOfChildcareFacility;
import de.symeda.sormas.api.exposure.WorkEnvironment;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;

@Entity(name = "exposures")
public class Exposure extends AbstractDomainObject {

	private static final long serialVersionUID = -5570515874416024604L;

	public static final String TABLE_NAME = "exposures";

	public static final String EPI_DATA = "epiData";
	public static final String LOCATION = "location";
	public static final String EXPOSURE_TYPE = "exposureType";
	public static final String CONTACT_TO_CASE = "contactToCase";
	public static final String EXPOSURE_CATEGORY = "exposureCategory";
	public static final String EXPOSURE_SETTING = "exposureSetting";
	public static final String EXPOSURE_SETTING_DETAILS = "exposureSettingDetails";
	public static final String EXPOSURE_SUB_SETTING_DETAILS = "exposureSubSettingDetails";
	public static final String CONTACT_FACTOR_DETAILS = "contactFactorDetails";
	public static final String PROTECTIVE_MEASURE_DETAILS = "protectiveMeasureDetails";
	public static final String EXPOSURE_COMMENT = "exposureComment";
	public static final String CONDITION_OF_ANIMAL = "conditionOfAnimal";
	public static final String ANIMAL_CATEGORY = "animalCategory";
	public static final String ANIMAL_CATEGORY_DETAILS = "animalCategoryDetails";
	public static final String FOMITE_TRANSMISSION_LOCATION = "fomiteTransmissionLocation";
	public static final String SUB_SETTINGS = "subSettings";
	public static final String CONTACT_FACTORS = "contactFactors";
	public static final String PROTECTIVE_MEASURES = "protectiveMeasures";

	private EpiData epiData;
	private User reportingUser;
	private boolean probableInfectionEnvironment;
	private Date startDate;
	private Date endDate;
	private String description;
	private ExposureType exposureType;
	private String exposureTypeDetails;
	private Location location;
	private ExposureRole exposureRole;

	// Type of Place
	private TypeOfPlace typeOfPlace;
	private String typeOfPlaceDetails;
	private MeansOfTransport meansOfTransport;
	private String meansOfTransportDetails;
	private String connectionNumber;
	private String seatNumber;

	private WorkEnvironment workEnvironment;

	// Details
	private YesNoUnknown indoors;
	private YesNoUnknown outdoors;
	private YesNoUnknown wearingMask;
	private YesNoUnknown wearingPpe;
	private YesNoUnknown otherProtectiveMeasures;
	private String protectiveMeasuresDetails;
	private YesNoUnknown shortDistance;
	private YesNoUnknown longFaceToFaceContact;
	private YesNoUnknown animalMarket;
	private YesNoUnknown percutaneous;
	private YesNoUnknown contactToBodyFluids;
	private YesNoUnknown handlingSamples;
	private YesNoUnknown eatingRawAnimalProducts;
	private YesNoUnknown handlingAnimals;
	private AnimalCondition animalCondition;
	private YesNoUnknown animalVaccinated;
	private AnimalContactType animalContactType;
	private String animalContactTypeDetails;
	private YesNoUnknown bodyOfWater;
	private WaterSource waterSource;
	private String waterSourceDetails;
	private Contact contactToCase;
	private YesNoUnknown prophylaxis;
	private Date prophylaxisDate;
	private YesNoUnknown riskArea;

	// Exposure sub-types
	private GatheringType gatheringType;
	private String gatheringDetails;
	private HabitationType habitationType;
	private String habitationDetails;
	private TypeOfAnimal typeOfAnimal;
	private String typeOfAnimalDetails;
	private TypeOfChildcareFacility typeOfChildcareFacility;
	private String childcareFacilityDetails;

	// Fields specific to ExposureType.BURIAL
	private YesNoUnknown physicalContactDuringPreparation;
	private YesNoUnknown physicalContactWithBody;
	private YesNoUnknown deceasedPersonIll;
	private String deceasedPersonName;
	private String deceasedPersonRelation;

	// Fields specific to ExposureType.GATHERING
	private YesNoUnknown largeAttendanceNumber;

	// Fields specific to Giardiasis and Cryptosporidiosis
	private TravelAccommodation travelAccommodation;
	private String travelAccommodationType;
	private SwimmingLocation swimmingLocation;
	private String swimmingLocationType;
	private AnimalLocation animalLocation;
	private String animalLocationText;
	private YesNoUnknown internationalSwimming;
	private YesNoUnknown domesticSwimming;
	private String sexualExposureText;
	private YesNoUnknown rawFoodContact;
	private String rawFoodContactText;
	private String symptomaticIndividualText;

	private ExposureCategory exposureCategory;
	private ExposureSetting exposureSetting;
	private String exposureSettingDetails;
	private String exposureSubSettingDetails;
	private String contactFactorDetails;
	private String protectiveMeasureDetails;
	private String exposureComment;

	private AnimalCondition conditionOfAnimal;
	private AnimalCategory animalCategory;
	private String animalCategoryDetails;

	private ProphylaxisAdherence prophylaxisAdherence;
	private String prophylaxisAdherenceDetails;
	private TravelPurpose travelPurpose;
	private String travelPurposeDetails;

	private FomiteTransmissionLocation fomiteTransmissionLocation;

	private Set<ExposureSubSetting> subSettings = new HashSet<>();
	private Set<ExposureContactFactor> contactFactors = new HashSet<>();
	private Set<ExposureProtectiveMeasure> protectiveMeasures = new HashSet<>();

	@ManyToOne
	@JoinColumn(nullable = false)
	public EpiData getEpiData() {
		return epiData;
	}

	public void setEpiData(EpiData epiData) {
		this.epiData = epiData;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Column
	public boolean isProbableInfectionEnvironment() {
		return probableInfectionEnvironment;
	}

	public void setProbableInfectionEnvironment(boolean probableInfectionEnvironment) {
		this.probableInfectionEnvironment = probableInfectionEnvironment;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(columnDefinition = "text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Enumerated(EnumType.STRING)
	public ExposureType getExposureType() {
		return exposureType;
	}

	public void setExposureType(ExposureType exposureType) {
		this.exposureType = exposureType;
	}

	@Column(columnDefinition = "text")
	public String getExposureTypeDetails() {
		return exposureTypeDetails;
	}

	public void setExposureTypeDetails(String exposureTypeDetails) {
		this.exposureTypeDetails = exposureTypeDetails;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	public Location getLocation() {
		if (location == null) {
			location = new Location();
		}
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Enumerated(EnumType.STRING)
	public ExposureRole getExposureRole() {
		return exposureRole;
	}

	public void setExposureRole(ExposureRole exposureRole) {
		this.exposureRole = exposureRole;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getIndoors() {
		return indoors;
	}

	public void setIndoors(YesNoUnknown indoors) {
		this.indoors = indoors;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getOutdoors() {
		return outdoors;
	}

	public void setOutdoors(YesNoUnknown outdoors) {
		this.outdoors = outdoors;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getWearingMask() {
		return wearingMask;
	}

	public void setWearingMask(YesNoUnknown wearingMask) {
		this.wearingMask = wearingMask;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getWearingPpe() {
		return wearingPpe;
	}

	public void setWearingPpe(YesNoUnknown wearingPpe) {
		this.wearingPpe = wearingPpe;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getOtherProtectiveMeasures() {
		return otherProtectiveMeasures;
	}

	public void setOtherProtectiveMeasures(YesNoUnknown otherProtectiveMeasures) {
		this.otherProtectiveMeasures = otherProtectiveMeasures;
	}

	@Column(columnDefinition = "text")
	public String getProtectiveMeasuresDetails() {
		return protectiveMeasuresDetails;
	}

	public void setProtectiveMeasuresDetails(String protectiveMeasuresDetails) {
		this.protectiveMeasuresDetails = protectiveMeasuresDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getShortDistance() {
		return shortDistance;
	}

	public void setShortDistance(YesNoUnknown shortDistance) {
		this.shortDistance = shortDistance;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getLongFaceToFaceContact() {
		return longFaceToFaceContact;
	}

	public void setLongFaceToFaceContact(YesNoUnknown longFaceToFaceContact) {
		this.longFaceToFaceContact = longFaceToFaceContact;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAnimalMarket() {
		return animalMarket;
	}

	public void setAnimalMarket(YesNoUnknown animalMarket) {
		this.animalMarket = animalMarket;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPercutaneous() {
		return percutaneous;
	}

	public void setPercutaneous(YesNoUnknown percutaneous) {
		this.percutaneous = percutaneous;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getContactToBodyFluids() {
		return contactToBodyFluids;
	}

	public void setContactToBodyFluids(YesNoUnknown contactToBodyFluids) {
		this.contactToBodyFluids = contactToBodyFluids;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHandlingSamples() {
		return handlingSamples;
	}

	public void setHandlingSamples(YesNoUnknown handlingSamples) {
		this.handlingSamples = handlingSamples;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getEatingRawAnimalProducts() {
		return eatingRawAnimalProducts;
	}

	public void setEatingRawAnimalProducts(YesNoUnknown eatingRawAnimalProducts) {
		this.eatingRawAnimalProducts = eatingRawAnimalProducts;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHandlingAnimals() {
		return handlingAnimals;
	}

	public void setHandlingAnimals(YesNoUnknown handlingAnimals) {
		this.handlingAnimals = handlingAnimals;
	}

	@Enumerated(EnumType.STRING)
	public AnimalCondition getAnimalCondition() {
		return animalCondition;
	}

	public void setAnimalCondition(AnimalCondition animalCondition) {
		this.animalCondition = animalCondition;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAnimalVaccinated() {
		return animalVaccinated;
	}

	public void setAnimalVaccinated(YesNoUnknown animalVaccinated) {
		this.animalVaccinated = animalVaccinated;
	}

	@Enumerated(EnumType.STRING)
	public AnimalContactType getAnimalContactType() {
		return animalContactType;
	}

	public void setAnimalContactType(AnimalContactType animalContactType) {
		this.animalContactType = animalContactType;
	}

	@Column(columnDefinition = "text")
	public String getAnimalContactTypeDetails() {
		return animalContactTypeDetails;
	}

	public void setAnimalContactTypeDetails(String animalContactTypeDetails) {
		this.animalContactTypeDetails = animalContactTypeDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getBodyOfWater() {
		return bodyOfWater;
	}

	public void setBodyOfWater(YesNoUnknown bodyOfWater) {
		this.bodyOfWater = bodyOfWater;
	}

	@Enumerated(EnumType.STRING)
	public WaterSource getWaterSource() {
		return waterSource;
	}

	public void setWaterSource(WaterSource waterSource) {
		this.waterSource = waterSource;
	}

	@Column(columnDefinition = "text")
	public String getWaterSourceDetails() {
		return waterSourceDetails;
	}

	public void setWaterSourceDetails(String waterSourceDetails) {
		this.waterSourceDetails = waterSourceDetails;
	}

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn
	public Contact getContactToCase() {
		return contactToCase;
	}

	public void setContactToCase(Contact contactToCase) {
		this.contactToCase = contactToCase;
	}

	@Enumerated(EnumType.STRING)
	public GatheringType getGatheringType() {
		return gatheringType;
	}

	public void setGatheringType(GatheringType gatheringType) {
		this.gatheringType = gatheringType;
	}

	@Column(columnDefinition = "text")
	public String getGatheringDetails() {
		return gatheringDetails;
	}

	public void setGatheringDetails(String gatheringDetails) {
		this.gatheringDetails = gatheringDetails;
	}

	@Enumerated(EnumType.STRING)
	public HabitationType getHabitationType() {
		return habitationType;
	}

	public void setHabitationType(HabitationType habitationType) {
		this.habitationType = habitationType;
	}

	@Column(columnDefinition = "text")
	public String getHabitationDetails() {
		return habitationDetails;
	}

	public void setHabitationDetails(String habitationDetails) {
		this.habitationDetails = habitationDetails;
	}

	@Enumerated(EnumType.STRING)
	public TypeOfAnimal getTypeOfAnimal() {
		return typeOfAnimal;
	}

	public void setTypeOfAnimal(TypeOfAnimal typeOfAnimal) {
		this.typeOfAnimal = typeOfAnimal;
	}

	@Column(columnDefinition = "text")
	public String getTypeOfAnimalDetails() {
		return typeOfAnimalDetails;
	}

	public void setTypeOfAnimalDetails(String typeOfAnimalDetails) {
		this.typeOfAnimalDetails = typeOfAnimalDetails;
	}

	@Enumerated(EnumType.STRING)
	public TypeOfChildcareFacility getTypeOfChildcareFacility() {
		return typeOfChildcareFacility;
	}

	public void setTypeOfChildcareFacility(TypeOfChildcareFacility typeOfChildcareFacility) {
		this.typeOfChildcareFacility = typeOfChildcareFacility;
	}

	@Column(columnDefinition = "text")
	public String getChildcareFacilityDetails() {
		return childcareFacilityDetails;
	}

	public void setChildcareFacilityDetails(String childcareFacilityDetails) {
		this.childcareFacilityDetails = childcareFacilityDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPhysicalContactDuringPreparation() {
		return physicalContactDuringPreparation;
	}

	public void setPhysicalContactDuringPreparation(YesNoUnknown physicalContactDuringPreparation) {
		this.physicalContactDuringPreparation = physicalContactDuringPreparation;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPhysicalContactWithBody() {
		return physicalContactWithBody;
	}

	public void setPhysicalContactWithBody(YesNoUnknown physicalContactWithBody) {
		this.physicalContactWithBody = physicalContactWithBody;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getDeceasedPersonIll() {
		return deceasedPersonIll;
	}

	public void setDeceasedPersonIll(YesNoUnknown deceasedPersonIll) {
		this.deceasedPersonIll = deceasedPersonIll;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getDeceasedPersonName() {
		return deceasedPersonName;
	}

	public void setDeceasedPersonName(String deceasedPersonName) {
		this.deceasedPersonName = deceasedPersonName;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getDeceasedPersonRelation() {
		return deceasedPersonRelation;
	}

	public void setDeceasedPersonRelation(String deceasedPersonRelation) {
		this.deceasedPersonRelation = deceasedPersonRelation;
	}

	@Enumerated(EnumType.STRING)
	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}

	public void setTypeOfPlace(TypeOfPlace typeOfPlace) {
		this.typeOfPlace = typeOfPlace;
	}

	@Column(columnDefinition = "text")
	public String getTypeOfPlaceDetails() {
		return typeOfPlaceDetails;
	}

	public void setTypeOfPlaceDetails(String typeOfPlaceDetails) {
		this.typeOfPlaceDetails = typeOfPlaceDetails;
	}

	@Enumerated(EnumType.STRING)
	public MeansOfTransport getMeansOfTransport() {
		return meansOfTransport;
	}

	public void setMeansOfTransport(MeansOfTransport meansOfTransport) {
		this.meansOfTransport = meansOfTransport;
	}

	@Column(columnDefinition = "text")
	public String getMeansOfTransportDetails() {
		return meansOfTransportDetails;
	}

	public void setMeansOfTransportDetails(String meansOfTransportDetails) {
		this.meansOfTransportDetails = meansOfTransportDetails;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getConnectionNumber() {
		return connectionNumber;
	}

	public void setConnectionNumber(String connectionNumber) {
		this.connectionNumber = connectionNumber;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSeatNumber() {
		return seatNumber;
	}

	@Enumerated(EnumType.STRING)
	public WorkEnvironment getWorkEnvironment() {
		return workEnvironment;
	}

	public void setWorkEnvironment(WorkEnvironment workEnvironment) {
		this.workEnvironment = workEnvironment;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getProphylaxis() {
		return prophylaxis;
	}

	public void setProphylaxis(YesNoUnknown prophylaxis) {
		this.prophylaxis = prophylaxis;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getProphylaxisDate() {
		return prophylaxisDate;
	}

	public void setProphylaxisDate(Date prophylaxisDate) {
		this.prophylaxisDate = prophylaxisDate;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getRiskArea() {
		return riskArea;
	}

	public void setRiskArea(YesNoUnknown riskArea) {
		this.riskArea = riskArea;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getLargeAttendanceNumber() {
		return largeAttendanceNumber;
	}

	public void setLargeAttendanceNumber(YesNoUnknown largeAttendanceNumber) {
		this.largeAttendanceNumber = largeAttendanceNumber;
	}

	@Enumerated(EnumType.STRING)
	public TravelAccommodation getTravelAccommodation() {
		return travelAccommodation;
	}

	public void setTravelAccommodation(TravelAccommodation travelAccommodation) {
		this.travelAccommodation = travelAccommodation;
	}

	public String getTravelAccommodationType() {
		return travelAccommodationType;
	}

	public void setTravelAccommodationType(String travelAccommodationType) {
		this.travelAccommodationType = travelAccommodationType;
	}

	@Enumerated(EnumType.STRING)
	public SwimmingLocation getSwimmingLocation() {
		return swimmingLocation;
	}

	public void setSwimmingLocation(SwimmingLocation swimmingLocation) {
		this.swimmingLocation = swimmingLocation;
	}

	public String getSwimmingLocationType() {
		return swimmingLocationType;
	}

	public void setSwimmingLocationType(String swimmingLocationType) {
		this.swimmingLocationType = swimmingLocationType;
	}

	@Enumerated(EnumType.STRING)
	public AnimalLocation getAnimalLocation() {
		return animalLocation;
	}

	public void setAnimalLocation(AnimalLocation animalLocation) {
		this.animalLocation = animalLocation;
	}

	public String getAnimalLocationText() {
		return animalLocationText;
	}

	public void setAnimalLocationText(String animalLocationText) {
		this.animalLocationText = animalLocationText;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getInternationalSwimming() {
		return internationalSwimming;
	}

	public void setInternationalSwimming(YesNoUnknown internationalSwimming) {
		this.internationalSwimming = internationalSwimming;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getDomesticSwimming() {
		return domesticSwimming;
	}

	public void setDomesticSwimming(YesNoUnknown domesticSwimming) {
		this.domesticSwimming = domesticSwimming;
	}

	public String getSexualExposureText() {
		return sexualExposureText;
	}

	public void setSexualExposureText(String sexualExposureText) {
		this.sexualExposureText = sexualExposureText;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getRawFoodContact() {
		return rawFoodContact;
	}

	public void setRawFoodContact(YesNoUnknown rawFoodContact) {
		this.rawFoodContact = rawFoodContact;
	}

	public String getRawFoodContactText() {
		return rawFoodContactText;
	}

	public void setRawFoodContactText(String rawFoodContactText) {
		this.rawFoodContactText = rawFoodContactText;
	}

	public String getSymptomaticIndividualText() {
		return symptomaticIndividualText;
	}

	public void setSymptomaticIndividualText(String symptomaticIndividualText) {
		this.symptomaticIndividualText = symptomaticIndividualText;
	}

	@Enumerated(EnumType.STRING)
	public ExposureCategory getExposureCategory() {
		return exposureCategory;
	}

	public void setExposureCategory(ExposureCategory exposureCategory) {
		this.exposureCategory = exposureCategory;
	}

	@Enumerated(EnumType.STRING)
	public ExposureSetting getExposureSetting() {
		return exposureSetting;
	}

	public void setExposureSetting(ExposureSetting exposureSetting) {
		this.exposureSetting = exposureSetting;
	}

	@Column(columnDefinition = "text")
	public String getExposureSettingDetails() {
		return exposureSettingDetails;
	}

	public void setExposureSettingDetails(String exposureSettingDetails) {
		this.exposureSettingDetails = exposureSettingDetails;
	}

	@Column(columnDefinition = "text")
	public String getExposureSubSettingDetails() {
		return exposureSubSettingDetails;
	}

	public void setExposureSubSettingDetails(String exposureSubSettingDetails) {
		this.exposureSubSettingDetails = exposureSubSettingDetails;
	}

	@Column(columnDefinition = "text")
	public String getContactFactorDetails() {
		return contactFactorDetails;
	}

	public void setContactFactorDetails(String contactFactorDetails) {
		this.contactFactorDetails = contactFactorDetails;
	}

	@Column(columnDefinition = "text")
	public String getProtectiveMeasureDetails() {
		return protectiveMeasureDetails;
	}

	public void setProtectiveMeasureDetails(String protectiveMeasureDetails) {
		this.protectiveMeasureDetails = protectiveMeasureDetails;
	}

	@Column(columnDefinition = "text")
	public String getExposureComment() {
		return exposureComment;
	}

	public void setExposureComment(String exposureComment) {
		this.exposureComment = exposureComment;
	}

	@Enumerated(EnumType.STRING)
	public AnimalCondition getConditionOfAnimal() {
		return conditionOfAnimal;
	}

	public void setConditionOfAnimal(AnimalCondition conditionOfAnimal) {
		this.conditionOfAnimal = conditionOfAnimal;
	}

	@Enumerated(EnumType.STRING)
	public AnimalCategory getAnimalCategory() {
		return animalCategory;
	}

	public void setAnimalCategory(AnimalCategory animalCategory) {
		this.animalCategory = animalCategory;
	}

	@Column(columnDefinition = "text")
	public String getAnimalCategoryDetails() {
		return animalCategoryDetails;
	}

	public void setAnimalCategoryDetails(String animalCategoryDetails) {
		this.animalCategoryDetails = animalCategoryDetails;
	}

	@Enumerated(EnumType.STRING)
	public FomiteTransmissionLocation getFomiteTransmissionLocation() {
		return fomiteTransmissionLocation;
	}

	public void setFomiteTransmissionLocation(FomiteTransmissionLocation fomiteTransmissionLocation) {
		this.fomiteTransmissionLocation = fomiteTransmissionLocation;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "exposures_subsettings",
		joinColumns = @JoinColumn(name = "exposure_id", referencedColumnName = Exposure.ID, nullable = false))
	@Column(name = "subsetting", nullable = false)
	public Set<ExposureSubSetting> getSubSettings() {
		return subSettings;
	}

	public void setSubSettings(Set<ExposureSubSetting> subSettings) {
		this.subSettings = subSettings;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "exposures_contactfactors",
		joinColumns = @JoinColumn(name = "exposure_id", referencedColumnName = Exposure.ID, nullable = false))
	@Column(name = "contactfactor", nullable = false)
	public Set<ExposureContactFactor> getContactFactors() {
		return contactFactors;
	}

	public void setContactFactors(Set<ExposureContactFactor> contactFactors) {
		this.contactFactors = contactFactors;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "exposures_protectivemeasures",
		joinColumns = @JoinColumn(name = "exposure_id", referencedColumnName = Exposure.ID, nullable = false))
	@Column(name = "protectivemeasure", nullable = false)
	public Set<ExposureProtectiveMeasure> getProtectiveMeasures() {
		return protectiveMeasures;
	}

	public void setProtectiveMeasures(Set<ExposureProtectiveMeasure> protectiveMeasures) {
		this.protectiveMeasures = protectiveMeasures;
	}

	@Enumerated(EnumType.STRING)
	public TravelPurpose getTravelPurpose() {
		return travelPurpose;
	}

	public void setTravelPurpose(TravelPurpose travelPurpose) {
		this.travelPurpose = travelPurpose;
	}

	@Column(columnDefinition = "text")
	public String getTravelPurposeDetails() {
		return travelPurposeDetails;
	}

	public void setTravelPurposeDetails(String travelPurposeDetails) {
		this.travelPurposeDetails = travelPurposeDetails;
	}

	@Enumerated(EnumType.STRING)
	public ProphylaxisAdherence getProphylaxisAdherence() {
		return prophylaxisAdherence;
	}

	public void setProphylaxisAdherence(ProphylaxisAdherence prophylaxisAdherence) {
		this.prophylaxisAdherence = prophylaxisAdherence;
	}

	public String getProphylaxisAdherenceDetails() {
		return prophylaxisAdherenceDetails;
	}

	public void setProphylaxisAdherenceDetails(String prophylaxisAdherenceDetails) {
		this.prophylaxisAdherenceDetails = prophylaxisAdherenceDetails;
	}

}
