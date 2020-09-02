/*******************************************************************************
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.epidata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class EpiDataDto extends EntityDto {

	private static final long serialVersionUID = 6292411396563549093L;

	public static final String I18N_PREFIX = "EpiData";

	public static final String BURIAL_ATTENDED = "burialAttended";
	public static final String GATHERING_ATTENDED = "gatheringAttended";
	public static final String TRAVELED = "traveled";
	public static final String RODENTS = "rodents";
	public static final String BATS = "bats";
	public static final String PRIMATES = "primates";
	public static final String SWINE = "swine";
	public static final String BIRDS = "birds";
	public static final String RABBITS = "rabbits";
	public static final String CATTLE = "cattle";
	public static final String DOGS = "dogs";
	public static final String CATS = "cats";
	public static final String CANIDAE = "canidae";
	public static final String OTHER_ANIMALS = "otherAnimals";
	public static final String OTHER_ANIMALS_DETAILS = "otherAnimalsDetails";
	public static final String WATER_SOURCE = "waterSource";
	public static final String WATER_SOURCE_OTHER = "waterSourceOther";
	public static final String WATER_BODY = "waterBody";
	public static final String WATER_BODY_DETAILS = "waterBodyDetails";
	public static final String TICK_BITE = "tickBite";
	public static final String FLEA_BITE = "fleaBite";
	public static final String KIND_OF_EXPOSURE_BITE = "kindOfExposureBite";
	public static final String KIND_OF_EXPOSURE_TOUCH = "kindOfExposureTouch";
	public static final String KIND_OF_EXPOSURE_SCRATCH = "kindOfExposureScratch";
	public static final String KIND_OF_EXPOSURE_LICK = "kindOfExposureLick";
	public static final String KIND_OF_EXPOSURE_OTHER = "kindOfExposureOther";
	public static final String KIND_OF_EXPOSURE_DETAILS = "kindOfExposureDetails";
	public static final String DATE_OF_LAST_EXPOSURE = "dateOfLastExposure";
	public static final String PLACE_OF_LAST_EXPOSURE = "placeOfLastExposure";
	public static final String ANIMAL_CONDITION = "animalCondition";
	public static final String ANIMAL_VACCINATION_STATUS = "animalVaccinationStatus";
	public static final String PROPHYLAXIS_STATUS = "prophylaxisStatus";
	public static final String DATE_OF_PROPHYLAXIS = "dateOfProphylaxis";
	public static final String BURIALS = "burials";
	public static final String GATHERINGS = "gatherings";
	public static final String TRAVELS = "travels";
	public static final String DIRECT_CONTACT_CONFIRMED_CASE = "directContactConfirmedCase";
	public static final String DIRECT_CONTACT_PROBABLE_CASE = "directContactProbableCase";
	public static final String CLOSE_CONTACT_PROBABLE_CASE = "closeContactProbableCase";
	public static final String AREA_CONFIRMED_CASES = "areaConfirmedCases";
	public static final String PROCESSING_CONFIRMED_CASE_FLUID_UNSAFE = "processingConfirmedCaseFluidUnsafe";
	public static final String PERCUTANEOUS_CASE_BLOOD = "percutaneousCaseBlood";
	public static final String DIRECT_CONTACT_DEAD_UNSAFE = "directContactDeadUnsafe";
	public static final String PROCESSING_SUSPECTED_CASE_SAMPLE_UNSAFE = "processingSuspectedCaseSampleUnsafe";
	public static final String AREA_INFECTED_ANIMALS = "areaInfectedAnimals";
	public static final String SICK_DEAD_ANIMALS = "sickDeadAnimals";
	public static final String SICK_DEAD_ANIMALS_DETAILS = "sickDeadAnimalsDetails";
	public static final String SICK_DEAD_ANIMALS_DATE = "sickDeadAnimalsDate";
	public static final String SICK_DEAD_ANIMALS_LOCATION = "sickDeadAnimalsLocation";
	public static final String EATING_RAW_ANIMALS_IN_INFECTED_AREA = "eatingRawAnimalsInInfectedArea";
	public static final String EATING_RAW_ANIMALS = "eatingRawAnimals";
	public static final String EATING_RAW_ANIMALS_DETAILS = "eatingRawAnimalsDetails";
	public static final String VISITED_HEALTH_FACILIY = "visitedHealthFacility";
	public static final String CONTACT_WITH_SOURCE_RESPIRATORY_CASE = "contactWithSourceRespiratoryCase";
	public static final String VISITED_ANIMAL_MARKET = "visitedAnimalMarket";
	public static final String CAMELS = "camels";
	public static final String SNAKES = "snakes";

	public static final String[] ANIMAL_EXPOSURE_PROPERTIES = new String[] {
		SICK_DEAD_ANIMALS,
		RODENTS,
		BATS,
		PRIMATES,
		SWINE,
		BIRDS,
		RABBITS,
		CATTLE,
		DOGS,
		CATS,
		CANIDAE,
		OTHER_ANIMALS };

	public static final String[] ENVIRONMENTAL_EXPOSURE_PROPERTIES = new String[] {
		WATER_SOURCE,
		WATER_BODY,
		TICK_BITE,
		FLEA_BITE };

	public static final String[] EXPOSURE_DEPENDENT_PROPERTIES = new String[] {
		DATE_OF_LAST_EXPOSURE,
		PLACE_OF_LAST_EXPOSURE,
		ANIMAL_CONDITION,
		ANIMAL_VACCINATION_STATUS,
		PROPHYLAXIS_STATUS,
		KIND_OF_EXPOSURE_BITE,
		KIND_OF_EXPOSURE_TOUCH,
		KIND_OF_EXPOSURE_SCRATCH,
		KIND_OF_EXPOSURE_LICK,
		KIND_OF_EXPOSURE_OTHER };

	public static final String[] KIND_OF_EXPOSURE_PROPERTIES = new String[] {
		KIND_OF_EXPOSURE_BITE,
		KIND_OF_EXPOSURE_TOUCH,
		KIND_OF_EXPOSURE_SCRATCH,
		KIND_OF_EXPOSURE_LICK,
		KIND_OF_EXPOSURE_OTHER };

	// Fields are declared in the order they should appear in the import template

	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.UNSPECIFIED_VHF,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown burialAttended;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.NEW_INFLUENZA,
		Disease.POLIO,
		Disease.CSM,
		Disease.CHOLERA,
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.DENGUE,
		Disease.CORONAVIRUS,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown gatheringAttended;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.NEW_INFLUENZA,
		Disease.POLIO,
		Disease.CSM,
		Disease.CHOLERA,
		Disease.MEASLES,
		Disease.YELLOW_FEVER,
		Disease.DENGUE,
		Disease.UNSPECIFIED_VHF,
		Disease.MONKEYPOX,
		Disease.PLAGUE,
		Disease.RABIES,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown traveled;

	private List<EpiDataBurialDto> burials = new ArrayList<>();
	private List<EpiDataGatheringDto> gatherings = new ArrayList<>();
	private List<EpiDataTravelDto> travels = new ArrayList<>();

	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.POLIO,
		Disease.CSM,
		Disease.MEASLES,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown directContactConfirmedCase;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown directContactProbableCase;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown closeContactProbableCase;
	@Diseases({
		Disease.AFP,
		Disease.DENGUE,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.PLAGUE,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CORONAVIRUS,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown areaConfirmedCases;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown processingConfirmedCaseFluidUnsafe;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown percutaneousCaseBlood;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown directContactDeadUnsafe;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown processingSuspectedCaseSampleUnsafe;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown areaInfectedAnimals;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.POLIO,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown sickDeadAnimals;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.POLIO,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private String sickDeadAnimalsDetails;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.POLIO,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private Date sickDeadAnimalsDate;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private String sickDeadAnimalsLocation;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown eatingRawAnimalsInInfectedArea;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown eatingRawAnimals;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.NEW_INFLUENZA,
		Disease.ANTHRAX,
		Disease.POLIO,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private String eatingRawAnimalsDetails;
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
	private YesNoUnknown rodents;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.RABIES,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown bats;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.MONKEYPOX,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown primates;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown swine;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown birds;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.RABIES,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown rabbits;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown cattle;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.RABIES,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown dogs;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.RABIES,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown cats;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.RABIES,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown canidae;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.MONKEYPOX,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown otherAnimals;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.MONKEYPOX,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private String otherAnimalsDetails;
	@Diseases({
		Disease.AFP,
		Disease.CHOLERA,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private WaterSource waterSource;
	@Diseases({
		Disease.AFP,
		Disease.CHOLERA,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private String waterSourceOther;
	@Diseases({
		Disease.AFP,
		Disease.CHOLERA,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown waterBody;
	@Diseases({
		Disease.AFP,
		Disease.CHOLERA,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private String waterBodyDetails;
	@Diseases({
		Disease.AFP,
		Disease.EVD,
		Disease.GUINEA_WORM,
		Disease.LASSA,
		Disease.POLIO,
		Disease.UNSPECIFIED_VHF,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown tickBite;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.PLAGUE,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown fleaBite;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.RABIES,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown kindOfExposureBite;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.RABIES,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown kindOfExposureTouch;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.RABIES,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown kindOfExposureScratch;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.RABIES,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown kindOfExposureLick;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.RABIES,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown kindOfExposureOther;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.RABIES,
		Disease.POLIO,
		Disease.UNDEFINED,
		Disease.OTHER })
	private String kindOfExposureDetails;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MONKEYPOX,
		Disease.POLIO,
		Disease.RABIES,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private Date dateOfLastExposure;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MONKEYPOX,
		Disease.POLIO,
		Disease.RABIES,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private String placeOfLastExposure;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MONKEYPOX,
		Disease.POLIO,
		Disease.RABIES,
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private AnimalCondition animalCondition;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.RABIES,
		Disease.UNDEFINED,
		Disease.OTHER })
	private Vaccination animalVaccinationStatus;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.RABIES,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown prophylaxisStatus;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.RABIES,
		Disease.UNDEFINED,
		Disease.OTHER })
	private Date dateOfProphylaxis;
	@Diseases({
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown visitedHealthFacility;
	@Diseases({
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown contactWithSourceRespiratoryCase;
	@Diseases({
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown visitedAnimalMarket;
	@Diseases({
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown camels;
	@Diseases({
		Disease.CORONAVIRUS,
		Disease.UNDEFINED,
		Disease.OTHER })
	private YesNoUnknown snakes;

	@ImportIgnore
	public YesNoUnknown getBurialAttended() {
		return burialAttended;
	}

	public void setBurialAttended(YesNoUnknown burialAttended) {
		this.burialAttended = burialAttended;
	}

	@ImportIgnore
	public YesNoUnknown getGatheringAttended() {
		return gatheringAttended;
	}

	public void setGatheringAttended(YesNoUnknown gatheringAttended) {
		this.gatheringAttended = gatheringAttended;
	}

	@ImportIgnore
	public YesNoUnknown getTraveled() {
		return traveled;
	}

	public void setTraveled(YesNoUnknown traveled) {
		this.traveled = traveled;
	}

	public YesNoUnknown getRodents() {
		return rodents;
	}

	public void setRodents(YesNoUnknown rodents) {
		this.rodents = rodents;
	}

	public YesNoUnknown getBats() {
		return bats;
	}

	public void setBats(YesNoUnknown bats) {
		this.bats = bats;
	}

	public YesNoUnknown getPrimates() {
		return primates;
	}

	public void setPrimates(YesNoUnknown primates) {
		this.primates = primates;
	}

	public YesNoUnknown getSwine() {
		return swine;
	}

	public void setSwine(YesNoUnknown swine) {
		this.swine = swine;
	}

	public YesNoUnknown getBirds() {
		return birds;
	}

	public void setBirds(YesNoUnknown birds) {
		this.birds = birds;
	}

	public YesNoUnknown getRabbits() {
		return rabbits;
	}

	public void setRabbits(YesNoUnknown rabbits) {
		this.rabbits = rabbits;
	}

	public YesNoUnknown getCattle() {
		return cattle;
	}

	public void setCattle(YesNoUnknown cattle) {
		this.cattle = cattle;
	}

	public YesNoUnknown getDogs() {
		return dogs;
	}

	public void setDogs(YesNoUnknown dogs) {
		this.dogs = dogs;
	}

	public YesNoUnknown getCats() {
		return cats;
	}

	public void setCats(YesNoUnknown cats) {
		this.cats = cats;
	}

	public YesNoUnknown getCanidae() {
		return canidae;
	}

	public void setCanidae(YesNoUnknown canidae) {
		this.canidae = canidae;
	}

	public YesNoUnknown getOtherAnimals() {
		return otherAnimals;
	}

	public void setOtherAnimals(YesNoUnknown otherAnimals) {
		this.otherAnimals = otherAnimals;
	}

	public String getOtherAnimalsDetails() {
		return otherAnimalsDetails;
	}

	public void setOtherAnimalsDetails(String otherAnimalsDetails) {
		this.otherAnimalsDetails = otherAnimalsDetails;
	}

	public WaterSource getWaterSource() {
		return waterSource;
	}

	public void setWaterSource(WaterSource waterSource) {
		this.waterSource = waterSource;
	}

	public String getWaterSourceOther() {
		return waterSourceOther;
	}

	public void setWaterSourceOther(String waterSourceOther) {
		this.waterSourceOther = waterSourceOther;
	}

	public YesNoUnknown getWaterBody() {
		return waterBody;
	}

	public void setWaterBody(YesNoUnknown waterBody) {
		this.waterBody = waterBody;
	}

	public String getWaterBodyDetails() {
		return waterBodyDetails;
	}

	public void setWaterBodyDetails(String waterBodyDetails) {
		this.waterBodyDetails = waterBodyDetails;
	}

	public YesNoUnknown getTickBite() {
		return tickBite;
	}

	public void setTickBite(YesNoUnknown tickBite) {
		this.tickBite = tickBite;
	}

	public YesNoUnknown getKindOfExposureBite() {
		return kindOfExposureBite;
	}

	public void setKindOfExposureBite(YesNoUnknown kindOfExposureBite) {
		this.kindOfExposureBite = kindOfExposureBite;
	}

	public YesNoUnknown getKindOfExposureTouch() {
		return kindOfExposureTouch;
	}

	public void setKindOfExposureTouch(YesNoUnknown kindOfExposureTouch) {
		this.kindOfExposureTouch = kindOfExposureTouch;
	}

	public YesNoUnknown getKindOfExposureScratch() {
		return kindOfExposureScratch;
	}

	public void setKindOfExposureScratch(YesNoUnknown kindOfExposureScratch) {
		this.kindOfExposureScratch = kindOfExposureScratch;
	}

	public YesNoUnknown getKindOfExposureLick() {
		return kindOfExposureLick;
	}

	public void setKindOfExposureLick(YesNoUnknown kindOfExposureLick) {
		this.kindOfExposureLick = kindOfExposureLick;
	}

	public YesNoUnknown getKindOfExposureOther() {
		return kindOfExposureOther;
	}

	public void setKindOfExposureOther(YesNoUnknown kindOfExposureOther) {
		this.kindOfExposureOther = kindOfExposureOther;
	}

	public String getKindOfExposureDetails() {
		return kindOfExposureDetails;
	}

	public void setKindOfExposureDetails(String kindOfExposureDetails) {
		this.kindOfExposureDetails = kindOfExposureDetails;
	}

	public Date getDateOfLastExposure() {
		return dateOfLastExposure;
	}

	public void setDateOfLastExposure(Date dateOfLastExposure) {
		this.dateOfLastExposure = dateOfLastExposure;
	}

	public String getPlaceOfLastExposure() {
		return placeOfLastExposure;
	}

	public void setPlaceOfLastExposure(String placeOfLastExposure) {
		this.placeOfLastExposure = placeOfLastExposure;
	}

	public AnimalCondition getAnimalCondition() {
		return animalCondition;
	}

	public void setAnimalCondition(AnimalCondition animalCondition) {
		this.animalCondition = animalCondition;
	}

	public Vaccination getAnimalVaccinationStatus() {
		return animalVaccinationStatus;
	}

	public void setAnimalVaccinationStatus(Vaccination animalVaccinationStatus) {
		this.animalVaccinationStatus = animalVaccinationStatus;
	}

	public YesNoUnknown getProphylaxisStatus() {
		return prophylaxisStatus;
	}

	public void setProphylaxisStatus(YesNoUnknown prophylaxisStatus) {
		this.prophylaxisStatus = prophylaxisStatus;
	}

	public Date getDateOfProphylaxis() {
		return dateOfProphylaxis;
	}

	public void setDateOfProphylaxis(Date dateOfProphylaxis) {
		this.dateOfProphylaxis = dateOfProphylaxis;
	}

	public YesNoUnknown getFleaBite() {
		return fleaBite;
	}

	public void setFleaBite(YesNoUnknown fleaBite) {
		this.fleaBite = fleaBite;
	}

	@ImportIgnore
	public List<EpiDataBurialDto> getBurials() {
		return burials;
	}

	public void setBurials(List<EpiDataBurialDto> burials) {
		this.burials = burials;
	}

	@ImportIgnore
	public List<EpiDataGatheringDto> getGatherings() {
		return gatherings;
	}

	public void setGatherings(List<EpiDataGatheringDto> gatherings) {
		this.gatherings = gatherings;
	}

	@ImportIgnore
	public List<EpiDataTravelDto> getTravels() {
		return travels;
	}

	public void setTravels(List<EpiDataTravelDto> travels) {
		this.travels = travels;
	}

	public YesNoUnknown getDirectContactConfirmedCase() {
		return directContactConfirmedCase;
	}

	public void setDirectContactConfirmedCase(YesNoUnknown directContactConfirmedCase) {
		this.directContactConfirmedCase = directContactConfirmedCase;
	}

	public YesNoUnknown getProcessingConfirmedCaseFluidUnsafe() {
		return processingConfirmedCaseFluidUnsafe;
	}

	public void setProcessingConfirmedCaseFluidUnsafe(YesNoUnknown processingConfirmedCaseFluidUnsafe) {
		this.processingConfirmedCaseFluidUnsafe = processingConfirmedCaseFluidUnsafe;
	}

	public YesNoUnknown getPercutaneousCaseBlood() {
		return percutaneousCaseBlood;
	}

	public void setPercutaneousCaseBlood(YesNoUnknown percutaneousCaseBlood) {
		this.percutaneousCaseBlood = percutaneousCaseBlood;
	}

	public YesNoUnknown getDirectContactDeadUnsafe() {
		return directContactDeadUnsafe;
	}

	public void setDirectContactDeadUnsafe(YesNoUnknown directContactDeadUnsafe) {
		this.directContactDeadUnsafe = directContactDeadUnsafe;
	}

	public YesNoUnknown getProcessingSuspectedCaseSampleUnsafe() {
		return processingSuspectedCaseSampleUnsafe;
	}

	public void setProcessingSuspectedCaseSampleUnsafe(YesNoUnknown processingSuspectedCaseSampleUnsafe) {
		this.processingSuspectedCaseSampleUnsafe = processingSuspectedCaseSampleUnsafe;
	}

	public YesNoUnknown getAreaInfectedAnimals() {
		return areaInfectedAnimals;
	}

	public void setAreaInfectedAnimals(YesNoUnknown areaInfectedAnimals) {
		this.areaInfectedAnimals = areaInfectedAnimals;
	}

	public YesNoUnknown getSickDeadAnimals() {
		return sickDeadAnimals;
	}

	public void setSickDeadAnimals(YesNoUnknown sickDeadAnimals) {
		this.sickDeadAnimals = sickDeadAnimals;
	}

	public String getSickDeadAnimalsDetails() {
		return sickDeadAnimalsDetails;
	}

	public void setSickDeadAnimalsDetails(String sickDeadAnimalsDetails) {
		this.sickDeadAnimalsDetails = sickDeadAnimalsDetails;
	}

	public Date getSickDeadAnimalsDate() {
		return sickDeadAnimalsDate;
	}

	public void setSickDeadAnimalsDate(Date sickDeadAnimalsDate) {
		this.sickDeadAnimalsDate = sickDeadAnimalsDate;
	}

	public String getSickDeadAnimalsLocation() {
		return sickDeadAnimalsLocation;
	}

	public void setSickDeadAnimalsLocation(String sickDeadAnimalsLocation) {
		this.sickDeadAnimalsLocation = sickDeadAnimalsLocation;
	}

	public YesNoUnknown getEatingRawAnimalsInInfectedArea() {
		return eatingRawAnimalsInInfectedArea;
	}

	public void setEatingRawAnimalsInInfectedArea(YesNoUnknown eatingRawAnimalsInInfectedArea) {
		this.eatingRawAnimalsInInfectedArea = eatingRawAnimalsInInfectedArea;
	}

	public YesNoUnknown getEatingRawAnimals() {
		return eatingRawAnimals;
	}

	public void setEatingRawAnimals(YesNoUnknown eatingRawAnimals) {
		this.eatingRawAnimals = eatingRawAnimals;
	}

	public String getEatingRawAnimalsDetails() {
		return eatingRawAnimalsDetails;
	}

	public void setEatingRawAnimalsDetails(String eatingRawAnimalsDetails) {
		this.eatingRawAnimalsDetails = eatingRawAnimalsDetails;
	}

	public YesNoUnknown getDirectContactProbableCase() {
		return directContactProbableCase;
	}

	public void setDirectContactProbableCase(YesNoUnknown directContactProbableCase) {
		this.directContactProbableCase = directContactProbableCase;
	}

	public YesNoUnknown getCloseContactProbableCase() {
		return closeContactProbableCase;
	}

	public void setCloseContactProbableCase(YesNoUnknown closeContactProbableCase) {
		this.closeContactProbableCase = closeContactProbableCase;
	}

	public YesNoUnknown getAreaConfirmedCases() {
		return areaConfirmedCases;
	}

	public void setAreaConfirmedCases(YesNoUnknown areaConfirmedCases) {
		this.areaConfirmedCases = areaConfirmedCases;
	}

	public YesNoUnknown getVisitedHealthFacility() {
		return visitedHealthFacility;
	}

	public void setVisitedHealthFacility(YesNoUnknown visitedHealthFacility) {
		this.visitedHealthFacility = visitedHealthFacility;
	}

	public YesNoUnknown getContactWithSourceRespiratoryCase() {
		return contactWithSourceRespiratoryCase;
	}

	public void setContactWithSourceRespiratoryCase(YesNoUnknown contactWithSourceRespiratoryCase) {
		this.contactWithSourceRespiratoryCase = contactWithSourceRespiratoryCase;
	}

	public YesNoUnknown getVisitedAnimalMarket() {
		return visitedAnimalMarket;
	}

	public void setVisitedAnimalMarket(YesNoUnknown visitedAnimalMarket) {
		this.visitedAnimalMarket = visitedAnimalMarket;
	}

	public YesNoUnknown getCamels() {
		return camels;
	}

	public void setCamels(YesNoUnknown camels) {
		this.camels = camels;
	}

	public YesNoUnknown getSnakes() {
		return snakes;
	}

	public void setSnakes(YesNoUnknown snakes) {
		this.snakes = snakes;
	}

	public static EpiDataDto build() {

		EpiDataDto epiData = new EpiDataDto();
		epiData.setUuid(DataHelper.createUuid());
		return epiData;
	}
}
