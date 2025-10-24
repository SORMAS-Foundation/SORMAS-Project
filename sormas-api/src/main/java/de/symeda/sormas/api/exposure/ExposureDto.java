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

package de.symeda.sormas.api.exposure;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependantOn;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.CONTACT_TRACING })
public class ExposureDto extends PseudonymizableDto {

	private static final long serialVersionUID = 6551672739041643946L;

	public static final String I18N_PREFIX = "Exposure";

	public static final String REPORTING_USER = "reportingUser";
	public static final String PROBABLE_INFECTION_ENVIRONMENT = "probableInfectionEnvironment";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String DESCRIPTION = "description";
	public static final String EXPOSURE_TYPE = "exposureType";
	public static final String EXPOSURE_TYPE_DETAILS = "exposureTypeDetails";
	public static final String LOCATION = "location";
	public static final String TYPE_OF_PLACE = "typeOfPlace";
	public static final String TYPE_OF_PLACE_DETAILS = "typeOfPlaceDetails";
	public static final String MEANS_OF_TRANSPORT = "meansOfTransport";
	public static final String MEANS_OF_TRANSPORT_DETAILS = "meansOfTransportDetails";
	public static final String CONNECTION_NUMBER = "connectionNumber";
	public static final String SEAT_NUMBER = "seatNumber";
	public static final String WORK_ENVIRONMENT = "workEnvironment";
	public static final String INDOORS = "indoors";
	public static final String OUTDOORS = "outdoors";
	public static final String WEARING_MASK = "wearingMask";
	public static final String WEARING_PPE = "wearingPpe";
	public static final String OTHER_PROTECTIVE_MEASURES = "otherProtectiveMeasures";
	public static final String PROTECTIVE_MEASURES_DETAILS = "protectiveMeasuresDetails";
	public static final String SHORT_DISTANCE = "shortDistance";
	public static final String LONG_FACE_TO_FACE_CONTACT = "longFaceToFaceContact";
	public static final String ANIMAL_MARKET = "animalMarket";
	public static final String PERCUTANEOUS = "percutaneous";
	public static final String CONTACT_TO_BODY_FLUIDS = "contactToBodyFluids";
	public static final String HANDLING_SAMPLES = "handlingSamples";
	public static final String EATING_RAW_ANIMAL_PRODUCTS = "eatingRawAnimalProducts";
	public static final String HANDLING_ANIMALS = "handlingAnimals";
	public static final String ANIMAL_CONDITION = "animalCondition";
	public static final String ANIMAL_VACCINATED = "animalVaccinated";
	public static final String ANIMAL_CONTACT_TYPE = "animalContactType";
	public static final String ANIMAL_CONTACT_TYPE_DETAILS = "animalContactTypeDetails";
	public static final String BODY_OF_WATER = "bodyOfWater";
	public static final String WATER_SOURCE = "waterSource";
	public static final String WATER_SOURCE_DETAILS = "waterSourceDetails";
	public static final String CONTACT_TO_CASE = "contactToCase";
	public static final String PROPHYLAXIS = "prophylaxis";
	public static final String PROPHYLAXIS_DATE = "prophylaxisDate";
	public static final String RISK_AREA = "riskArea";
	public static final String GATHERING_TYPE = "gatheringType";
	public static final String GATHERING_DETAILS = "gatheringDetails";
	public static final String HABITATION_TYPE = "habitationType";
	public static final String HABITATION_DETAILS = "habitationDetails";
	public static final String TYPE_OF_ANIMAL = "typeOfAnimal";
	public static final String TYPE_OF_ANIMAL_DETAILS = "typeOfAnimalDetails";
	public static final String TYPE_OF_CHILDCARE_FACILITY = "typeOfChildcareFacility";
	public static final String CHILDCARE_FACILITY_DETAILS = "childcareFacilityDetails";
	public static final String PHYSICAL_CONTACT_DURING_PREPARATION = "physicalContactDuringPreparation";
	public static final String PHYSICAL_CONTACT_WITH_BODY = "physicalContactWithBody";
	public static final String DECEASED_PERSON_ILL = "deceasedPersonIll";
	public static final String DECEASED_PERSON_NAME = "deceasedPersonName";
	public static final String DECEASED_PERSON_RELATION = "deceasedPersonRelation";
	public static final String EXPOSURE_ROLE = "exposureRole";
	public static final String LARGE_ATTENDANCE_NUMBER = "largeAttendanceNumber";
	public static final String TRAVEL_ACCOMMODATION = "travelAccommodation";
	public static final String TRAVEL_ACCOMMODATION_TYPE = "travelAccommodationType";
	public static final String SWIMMING_LOCATION = "swimmingLocation";
	public static final String SWIMMING_LOCATION_TYPE = "swimmingLocationType";
	public static final String ANIMAL_LOCATION = "animalLocation";
	public static final String ANIMAL_LOCATION_TEXT = "animalLocationText";
	public static final String DOMESTIC_SWIMMING = "domesticSwimming";
	public static final String INTERNATIONAL_SWIMMING = "internationalSwimming";
	public static final String SEXUAL_EXPOSURE_TEXT = "sexualExposureText";
	public static final String RAW_FOOD_CONTACT = "rawFoodContact";
	public static final String RAW_FOOD_CONTACT_TEXT = "rawFoodContactText";
	public static final String SYMPTOMATIC_INDIVIDUAL_TEXT = "symptomaticIndividualText";

	@SensitiveData
	private UserReferenceDto reportingUser;
	@HideForCountriesExcept
	private boolean probableInfectionEnvironment;
	private Date startDate;
	private Date endDate;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String description;
	@NotNull(message = Validations.requiredField)
	private ExposureType exposureType;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String exposureTypeDetails;
	@Valid
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private LocationDto location;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private ExposureRole exposureRole;

	// Type of Place
	private TypeOfPlace typeOfPlace;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String typeOfPlaceDetails;
	private MeansOfTransport meansOfTransport;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String meansOfTransportDetails;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String connectionNumber;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String seatNumber;

	private WorkEnvironment workEnvironment;

	// Details
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private YesNoUnknown indoors;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private YesNoUnknown outdoors;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private YesNoUnknown wearingMask;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private YesNoUnknown wearingPpe;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private YesNoUnknown otherProtectiveMeasures;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private String protectiveMeasuresDetails;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private YesNoUnknown shortDistance;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private YesNoUnknown longFaceToFaceContact;
	@Diseases({
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown animalMarket;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown percutaneous;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown contactToBodyFluids;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown handlingSamples;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown eatingRawAnimalProducts;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.MONKEYPOX,
		Disease.PLAGUE,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown handlingAnimals;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private AnimalCondition animalCondition;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private YesNoUnknown animalVaccinated;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private AnimalContactType animalContactType;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String animalContactTypeDetails;
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private AnimalLocation animalLocation;
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private String animalLocationText;
	@Diseases({
		Disease.AFP,
		Disease.CHOLERA,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown bodyOfWater;
	@Diseases({
		Disease.AFP,
		Disease.CHOLERA,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private WaterSource waterSource;
	@SensitiveData
	@Diseases({
		Disease.AFP,
		Disease.CHOLERA,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String waterSourceDetails;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	@PersonalData
	private ContactReferenceDto contactToCase;
	private YesNoUnknown prophylaxis;
	private Date prophylaxisDate;
	@Diseases(value = {
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS }, hide = true)
	private YesNoUnknown riskArea;

	// Exposure sub-types
	private GatheringType gatheringType;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String gatheringDetails;
	private HabitationType habitationType;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String habitationDetails;
	private TypeOfAnimal typeOfAnimal;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String typeOfAnimalDetails;
	@Diseases({
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	private TypeOfChildcareFacility typeOfChildcareFacility;
	@SensitiveData
	@Diseases({
		Disease.RESPIRATORY_SYNCYTIAL_VIRUS })
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String childcareFacilityDetails;

	// Fields specific to ExposureType.BURIAL
	private YesNoUnknown physicalContactDuringPreparation;
	private YesNoUnknown physicalContactWithBody;
	private YesNoUnknown deceasedPersonIll;
	@PersonalData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String deceasedPersonName;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String deceasedPersonRelation;
	private YesNoUnknown largeAttendanceNumber;

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	@SensitiveData
	private TravelAccommodation travelAccommodation;

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String travelAccommodationType;

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	@DependantOn(INTERNATIONAL_SWIMMING)
	private SwimmingLocation swimmingLocation;

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String swimmingLocationType;

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private YesNoUnknown domesticSwimming;
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	private YesNoUnknown internationalSwimming;

	@Diseases(Disease.GIARDIASIS)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String sexualExposureText;
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS })
	@SensitiveData
	private YesNoUnknown rawFoodContact;
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String rawFoodContactText;
	@Diseases({
		Disease.CRYPTOSPORIDIOSIS })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String symptomaticIndividualText;

	public static ExposureDto build(ExposureType exposureType) {

		ExposureDto exposure = new ExposureDto();
		exposure.setUuid(DataHelper.createUuid());
		exposure.setExposureType(exposureType);
		LocationDto location = LocationDto.build();
		exposure.setLocation(location);
		return exposure;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public boolean isProbableInfectionEnvironment() {
		return probableInfectionEnvironment;
	}

	public void setProbableInfectionEnvironment(boolean probableInfectionEnvironment) {
		this.probableInfectionEnvironment = probableInfectionEnvironment;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ExposureType getExposureType() {
		return exposureType;
	}

	public void setExposureType(ExposureType exposureType) {
		this.exposureType = exposureType;
	}

	public String getExposureTypeDetails() {
		return exposureTypeDetails;
	}

	public void setExposureTypeDetails(String exposureTypeDetails) {
		this.exposureTypeDetails = exposureTypeDetails;
	}

	public LocationDto getLocation() {
		return location;
	}

	public void setLocation(LocationDto location) {
		this.location = location;
	}

	public ExposureRole getExposureRole() {
		return exposureRole;
	}

	public void setExposureRole(ExposureRole exposureRole) {
		this.exposureRole = exposureRole;
	}

	public YesNoUnknown getIndoors() {
		return indoors;
	}

	public void setIndoors(YesNoUnknown indoors) {
		this.indoors = indoors;
	}

	public YesNoUnknown getOutdoors() {
		return outdoors;
	}

	public void setOutdoors(YesNoUnknown outdoors) {
		this.outdoors = outdoors;
	}

	public YesNoUnknown getWearingMask() {
		return wearingMask;
	}

	public void setWearingMask(YesNoUnknown wearingMask) {
		this.wearingMask = wearingMask;
	}

	public YesNoUnknown getWearingPpe() {
		return wearingPpe;
	}

	public void setWearingPpe(YesNoUnknown wearingPpe) {
		this.wearingPpe = wearingPpe;
	}

	public YesNoUnknown getOtherProtectiveMeasures() {
		return otherProtectiveMeasures;
	}

	public void setOtherProtectiveMeasures(YesNoUnknown otherProtectiveMeasures) {
		this.otherProtectiveMeasures = otherProtectiveMeasures;
	}

	public String getProtectiveMeasuresDetails() {
		return protectiveMeasuresDetails;
	}

	public void setProtectiveMeasuresDetails(String protectiveMeasuresDetails) {
		this.protectiveMeasuresDetails = protectiveMeasuresDetails;
	}

	public YesNoUnknown getShortDistance() {
		return shortDistance;
	}

	public void setShortDistance(YesNoUnknown shortDistance) {
		this.shortDistance = shortDistance;
	}

	public YesNoUnknown getLongFaceToFaceContact() {
		return longFaceToFaceContact;
	}

	public void setLongFaceToFaceContact(YesNoUnknown longFaceToFaceContact) {
		this.longFaceToFaceContact = longFaceToFaceContact;
	}

	public YesNoUnknown getAnimalMarket() {
		return animalMarket;
	}

	public void setAnimalMarket(YesNoUnknown animalMarket) {
		this.animalMarket = animalMarket;
	}

	public YesNoUnknown getPercutaneous() {
		return percutaneous;
	}

	public void setPercutaneous(YesNoUnknown percutaneous) {
		this.percutaneous = percutaneous;
	}

	public YesNoUnknown getContactToBodyFluids() {
		return contactToBodyFluids;
	}

	public void setContactToBodyFluids(YesNoUnknown contactToBodyFluids) {
		this.contactToBodyFluids = contactToBodyFluids;
	}

	public YesNoUnknown getHandlingSamples() {
		return handlingSamples;
	}

	public void setHandlingSamples(YesNoUnknown handlingSamples) {
		this.handlingSamples = handlingSamples;
	}

	public YesNoUnknown getEatingRawAnimalProducts() {
		return eatingRawAnimalProducts;
	}

	public void setEatingRawAnimalProducts(YesNoUnknown eatingRawAnimalProducts) {
		this.eatingRawAnimalProducts = eatingRawAnimalProducts;
	}

	public YesNoUnknown getHandlingAnimals() {
		return handlingAnimals;
	}

	public void setHandlingAnimals(YesNoUnknown handlingAnimals) {
		this.handlingAnimals = handlingAnimals;
	}

	public AnimalCondition getAnimalCondition() {
		return animalCondition;
	}

	public void setAnimalCondition(AnimalCondition animalCondition) {
		this.animalCondition = animalCondition;
	}

	public YesNoUnknown getAnimalVaccinated() {
		return animalVaccinated;
	}

	public void setAnimalVaccinated(YesNoUnknown animalVaccinated) {
		this.animalVaccinated = animalVaccinated;
	}

	public AnimalContactType getAnimalContactType() {
		return animalContactType;
	}

	public void setAnimalContactType(AnimalContactType animalContactType) {
		this.animalContactType = animalContactType;
	}

	public String getAnimalContactTypeDetails() {
		return animalContactTypeDetails;
	}

	public void setAnimalContactTypeDetails(String animalContactTypeDetails) {
		this.animalContactTypeDetails = animalContactTypeDetails;
	}

	public YesNoUnknown getBodyOfWater() {
		return bodyOfWater;
	}

	public void setBodyOfWater(YesNoUnknown bodyOfWater) {
		this.bodyOfWater = bodyOfWater;
	}

	public WaterSource getWaterSource() {
		return waterSource;
	}

	public void setWaterSource(WaterSource waterSource) {
		this.waterSource = waterSource;
	}

	public String getWaterSourceDetails() {
		return waterSourceDetails;
	}

	public void setWaterSourceDetails(String waterSourceDetails) {
		this.waterSourceDetails = waterSourceDetails;
	}

	public ContactReferenceDto getContactToCase() {
		return contactToCase;
	}

	public void setContactToCase(ContactReferenceDto contactToCase) {
		this.contactToCase = contactToCase;
	}

	public GatheringType getGatheringType() {
		return gatheringType;
	}

	public void setGatheringType(GatheringType gatheringType) {
		this.gatheringType = gatheringType;
	}

	public String getGatheringDetails() {
		return gatheringDetails;
	}

	public void setGatheringDetails(String gatheringDetails) {
		this.gatheringDetails = gatheringDetails;
	}

	public HabitationType getHabitationType() {
		return habitationType;
	}

	public void setHabitationType(HabitationType habitationType) {
		this.habitationType = habitationType;
	}

	public String getHabitationDetails() {
		return habitationDetails;
	}

	public void setHabitationDetails(String habitationDetails) {
		this.habitationDetails = habitationDetails;
	}

	public TypeOfAnimal getTypeOfAnimal() {
		return typeOfAnimal;
	}

	public void setTypeOfAnimal(TypeOfAnimal typeOfAnimal) {
		this.typeOfAnimal = typeOfAnimal;
	}

	public String getTypeOfAnimalDetails() {
		return typeOfAnimalDetails;
	}

	public void setTypeOfAnimalDetails(String typeOfAnimalDetails) {
		this.typeOfAnimalDetails = typeOfAnimalDetails;
	}

	public TypeOfChildcareFacility getTypeOfChildcareFacility() {
		return typeOfChildcareFacility;
	}

	public void setTypeOfChildcareFacility(TypeOfChildcareFacility typeOfChildcareFacility) {
		this.typeOfChildcareFacility = typeOfChildcareFacility;
	}

	public String getChildcareFacilityDetails() {
		return childcareFacilityDetails;
	}

	public void setChildcareFacilityDetails(String childcareFacilityDetails) {
		this.childcareFacilityDetails = childcareFacilityDetails;
	}

	public YesNoUnknown getPhysicalContactDuringPreparation() {
		return physicalContactDuringPreparation;
	}

	public void setPhysicalContactDuringPreparation(YesNoUnknown physicalContactDuringPreparation) {
		this.physicalContactDuringPreparation = physicalContactDuringPreparation;
	}

	public YesNoUnknown getPhysicalContactWithBody() {
		return physicalContactWithBody;
	}

	public void setPhysicalContactWithBody(YesNoUnknown physicalContactWithBody) {
		this.physicalContactWithBody = physicalContactWithBody;
	}

	public YesNoUnknown getDeceasedPersonIll() {
		return deceasedPersonIll;
	}

	public void setDeceasedPersonIll(YesNoUnknown deceasedPersonIll) {
		this.deceasedPersonIll = deceasedPersonIll;
	}

	public String getDeceasedPersonName() {
		return deceasedPersonName;
	}

	public void setDeceasedPersonName(String deceasedPersonName) {
		this.deceasedPersonName = deceasedPersonName;
	}

	public String getDeceasedPersonRelation() {
		return deceasedPersonRelation;
	}

	public void setDeceasedPersonRelation(String deceasedPersonRelation) {
		this.deceasedPersonRelation = deceasedPersonRelation;
	}

	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}

	public void setTypeOfPlace(TypeOfPlace typeOfPlace) {
		this.typeOfPlace = typeOfPlace;
	}

	public String getTypeOfPlaceDetails() {
		return typeOfPlaceDetails;
	}

	public void setTypeOfPlaceDetails(String typeOfPlaceDetails) {
		this.typeOfPlaceDetails = typeOfPlaceDetails;
	}

	public MeansOfTransport getMeansOfTransport() {
		return meansOfTransport;
	}

	public void setMeansOfTransport(MeansOfTransport meansOfTransport) {
		this.meansOfTransport = meansOfTransport;
	}

	public String getMeansOfTransportDetails() {
		return meansOfTransportDetails;
	}

	public void setMeansOfTransportDetails(String meansOfTransportDetails) {
		this.meansOfTransportDetails = meansOfTransportDetails;
	}

	public String getConnectionNumber() {
		return connectionNumber;
	}

	public void setConnectionNumber(String connectionNumber) {
		this.connectionNumber = connectionNumber;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public WorkEnvironment getWorkEnvironment() {
		return workEnvironment;
	}

	public void setWorkEnvironment(WorkEnvironment workEnvironment) {
		this.workEnvironment = workEnvironment;
	}

	public YesNoUnknown getProphylaxis() {
		return prophylaxis;
	}

	public void setProphylaxis(YesNoUnknown prophylaxis) {
		this.prophylaxis = prophylaxis;
	}

	public Date getProphylaxisDate() {
		return prophylaxisDate;
	}

	public void setProphylaxisDate(Date prophylaxisDate) {
		this.prophylaxisDate = prophylaxisDate;
	}

	public YesNoUnknown getRiskArea() {
		return riskArea;
	}

	public void setRiskArea(YesNoUnknown riskArea) {
		this.riskArea = riskArea;
	}

	public YesNoUnknown getLargeAttendanceNumber() {
		return largeAttendanceNumber;
	}

	public void setLargeAttendanceNumber(YesNoUnknown largeAttendanceNumber) {
		this.largeAttendanceNumber = largeAttendanceNumber;
	}

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

	public YesNoUnknown getDomesticSwimming() {
		return domesticSwimming;
	}

	public void setDomesticSwimming(YesNoUnknown domesticSwimming) {
		this.domesticSwimming = domesticSwimming;
	}

	public YesNoUnknown getInternationalSwimming() {
		return internationalSwimming;
	}

	public void setInternationalSwimming(YesNoUnknown internationalSwimming) {
		this.internationalSwimming = internationalSwimming;
	}

	public String getSexualExposureText() {
		return sexualExposureText;
	}

	public void setSexualExposureText(String sexualExposureText) {
		this.sexualExposureText = sexualExposureText;
	}

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

	@Override
	public ExposureDto clone() throws CloneNotSupportedException {
		ExposureDto clone = (ExposureDto) super.clone();
		clone.setLocation((LocationDto) clone.getLocation().clone());
		return clone;
	}
}
