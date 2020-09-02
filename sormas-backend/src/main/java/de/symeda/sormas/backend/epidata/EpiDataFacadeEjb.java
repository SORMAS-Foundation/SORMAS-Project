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
package de.symeda.sormas.backend.epidata;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataFacade;
import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "EpiDataFacade")
public class EpiDataFacadeEjb implements EpiDataFacade {

	@EJB
	private EpiDataService service;
	@EJB
	private EpiDataBurialService burialService;
	@EJB
	private EpiDataGatheringService gatheringService;
	@EJB
	private EpiDataTravelService travelService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;

	public EpiData fromDto(EpiDataDto source) {

		if (source == null) {
			return null;
		}

		EpiData target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new EpiData();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setBurialAttended(source.getBurialAttended());
		target.setGatheringAttended(source.getGatheringAttended());
		target.setTraveled(source.getTraveled());

		target.setDirectContactConfirmedCase(source.getDirectContactConfirmedCase());
		target.setDirectContactProbableCase(source.getDirectContactProbableCase());
		target.setCloseContactProbableCase(source.getCloseContactProbableCase());
		target.setAreaConfirmedCases(source.getAreaConfirmedCases());

		target.setProcessingConfirmedCaseFluidUnsafe(source.getProcessingConfirmedCaseFluidUnsafe());
		target.setPercutaneousCaseBlood(source.getPercutaneousCaseBlood());
		target.setDirectContactDeadUnsafe(source.getDirectContactDeadUnsafe());

		target.setProcessingSuspectedCaseSampleUnsafe(source.getProcessingSuspectedCaseSampleUnsafe());
		target.setAreaInfectedAnimals(source.getAreaInfectedAnimals());
		target.setSickDeadAnimals(source.getSickDeadAnimals());
		target.setSickDeadAnimalsDetails(source.getSickDeadAnimalsDetails());
		target.setSickDeadAnimalsDate(source.getSickDeadAnimalsDate());
		target.setSickDeadAnimalsLocation(source.getSickDeadAnimalsLocation());
		target.setEatingRawAnimalsInInfectedArea(source.getEatingRawAnimalsInInfectedArea());
		target.setEatingRawAnimals(source.getEatingRawAnimals());
		target.setEatingRawAnimalsDetails(source.getEatingRawAnimalsDetails());

		target.setRodents(source.getRodents());
		target.setBats(source.getBats());
		target.setPrimates(source.getPrimates());
		target.setSwine(source.getSwine());
		target.setBirds(source.getBirds());
		target.setRabbits(source.getRabbits());
		target.setCattle(source.getCattle());
		target.setDogs(source.getDogs());
		target.setCats(source.getCats());
		target.setCanidae(source.getCanidae());
		target.setOtherAnimals(source.getOtherAnimals());
		target.setOtherAnimalsDetails(source.getOtherAnimalsDetails());
		target.setWaterSource(source.getWaterSource());
		target.setWaterSourceOther(source.getWaterSourceOther());
		target.setWaterBody(source.getWaterBody());
		target.setWaterBodyDetails(source.getWaterBodyDetails());
		target.setTickBite(source.getTickBite());
		target.setFleaBite(source.getFleaBite());
		target.setKindOfExposureBite(source.getKindOfExposureBite());
		target.setKindOfExposureTouch(source.getKindOfExposureTouch());
		target.setKindOfExposureScratch(source.getKindOfExposureScratch());
		target.setKindOfExposureLick(source.getKindOfExposureLick());
		target.setKindOfExposureOther(source.getKindOfExposureOther());
		target.setKindOfExposureDetails(source.getKindOfExposureDetails());
		target.setDateOfLastExposure(source.getDateOfLastExposure());
		target.setPlaceOfLastExposure(source.getPlaceOfLastExposure());
		target.setAnimalCondition(source.getAnimalCondition());
		target.setAnimalVaccinationStatus(source.getAnimalVaccinationStatus());
		target.setProphylaxisStatus(source.getProphylaxisStatus());
		target.setDateOfProphylaxis(source.getDateOfProphylaxis());
		target.setVisitedHealthFacility(source.getVisitedHealthFacility());
		target.setContactWithSourceRespiratoryCase(source.getContactWithSourceRespiratoryCase());
		target.setVisitedAnimalMarket(source.getVisitedAnimalMarket());
		target.setCamels(source.getCamels());
		target.setSnakes(source.getSnakes());

		List<EpiDataBurial> burials = new ArrayList<>();
		for (EpiDataBurialDto burialDto : source.getBurials()) {
			EpiDataBurial burial = fromDto(burialDto);
			burial.setEpiData(target);
			burials.add(burial);
		}
		if (!DataHelper.equal(target.getBurials(), burials)) {
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.getBurials().clear();
		target.getBurials().addAll(burials);

		List<EpiDataGathering> gatherings = new ArrayList<>();
		for (EpiDataGatheringDto gatheringDto : source.getGatherings()) {
			EpiDataGathering gathering = fromDto(gatheringDto);
			gathering.setEpiData(target);
			gatherings.add(gathering);
		}
		if (!DataHelper.equal(target.getGatherings(), gatherings)) {
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.getGatherings().clear();
		target.getGatherings().addAll(gatherings);

		List<EpiDataTravel> travels = new ArrayList<>();
		for (EpiDataTravelDto travelDto : source.getTravels()) {
			EpiDataTravel travel = fromDto(travelDto);
			travel.setEpiData(target);
			travels.add(travel);
		}
		if (!DataHelper.equal(target.getTravels(), travels)) {
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.getTravels().clear();
		target.getTravels().addAll(travels);

		return target;
	}

	public EpiDataBurial fromDto(EpiDataBurialDto dto) {
		if (dto == null) {
			return null;
		}

		EpiDataBurial burial = burialService.getByUuid(dto.getUuid());
		if (burial == null) {
			burial = new EpiDataBurial();
			burial.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				burial.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		}

		EpiDataBurial target = burial;
		EpiDataBurialDto source = dto;
		DtoHelper.validateDto(source, target);

		target.setBurialAddress(locationFacade.fromDto(source.getBurialAddress()));
		target.setBurialDateFrom(source.getBurialDateFrom());
		target.setBurialDateTo(source.getBurialDateTo());
		target.setBurialIll(source.getBurialIll());
		target.setBurialPersonName(source.getBurialPersonName());
		target.setBurialRelation(source.getBurialRelation());
		target.setBurialTouching(source.getBurialTouching());

		return burial;
	}

	public EpiDataGathering fromDto(EpiDataGatheringDto dto) {

		if (dto == null) {
			return null;
		}

		EpiDataGathering gathering = gatheringService.getByUuid(dto.getUuid());
		if (gathering == null) {
			gathering = new EpiDataGathering();
			gathering.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				gathering.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		}

		EpiDataGathering target = gathering;
		EpiDataGatheringDto source = dto;
		DtoHelper.validateDto(source, target);

		target.setDescription(source.getDescription());
		target.setGatheringAddress(locationFacade.fromDto(source.getGatheringAddress()));
		target.setGatheringDate(source.getGatheringDate());

		return gathering;
	}

	public EpiDataTravel fromDto(EpiDataTravelDto dto) {

		if (dto == null) {
			return null;
		}

		EpiDataTravel travel = travelService.getByUuid(dto.getUuid());
		if (travel == null) {
			travel = new EpiDataTravel();
			travel.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				travel.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		}

		EpiDataTravel target = travel;
		EpiDataTravelDto source = dto;
		DtoHelper.validateDto(source, target);

		target.setTravelDateFrom(source.getTravelDateFrom());
		target.setTravelDateTo(source.getTravelDateTo());
		target.setTravelDestination(source.getTravelDestination());
		target.setTravelType(source.getTravelType());

		return travel;
	}

	public static EpiDataDto toDto(EpiData epiData) {

		if (epiData == null) {
			return null;
		}

		EpiDataDto target = new EpiDataDto();
		EpiData source = epiData;

		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());

		target.setBurialAttended(source.getBurialAttended());
		target.setGatheringAttended(source.getGatheringAttended());
		target.setTraveled(source.getTraveled());

		target.setDirectContactConfirmedCase(source.getDirectContactConfirmedCase());
		target.setDirectContactProbableCase(source.getDirectContactProbableCase());
		target.setCloseContactProbableCase(source.getCloseContactProbableCase());
		target.setAreaConfirmedCases(source.getAreaConfirmedCases());

		target.setProcessingConfirmedCaseFluidUnsafe(source.getProcessingConfirmedCaseFluidUnsafe());
		target.setPercutaneousCaseBlood(source.getPercutaneousCaseBlood());
		target.setDirectContactDeadUnsafe(source.getDirectContactDeadUnsafe());

		target.setProcessingSuspectedCaseSampleUnsafe(source.getProcessingSuspectedCaseSampleUnsafe());
		target.setAreaInfectedAnimals(source.getAreaInfectedAnimals());
		target.setSickDeadAnimals(source.getSickDeadAnimals());
		target.setSickDeadAnimalsDetails(source.getSickDeadAnimalsDetails());
		target.setSickDeadAnimalsDate(source.getSickDeadAnimalsDate());
		target.setSickDeadAnimalsLocation(source.getSickDeadAnimalsLocation());
		target.setEatingRawAnimalsInInfectedArea(source.getEatingRawAnimalsInInfectedArea());
		target.setEatingRawAnimals(source.getEatingRawAnimals());
		target.setEatingRawAnimalsDetails(source.getEatingRawAnimalsDetails());

		target.setRodents(source.getRodents());
		target.setBats(source.getBats());
		target.setPrimates(source.getPrimates());
		target.setSwine(source.getSwine());
		target.setBirds(source.getBirds());
		target.setRabbits(source.getRabbits());
		target.setCattle(source.getCattle());
		target.setDogs(source.getDogs());
		target.setCats(source.getCats());
		target.setCanidae(source.getCanidae());
		target.setOtherAnimals(source.getOtherAnimals());
		target.setOtherAnimalsDetails(source.getOtherAnimalsDetails());
		target.setWaterSource(source.getWaterSource());
		target.setWaterSourceOther(source.getWaterSourceOther());
		target.setWaterBody(source.getWaterBody());
		target.setWaterBodyDetails(source.getWaterBodyDetails());
		target.setTickBite(source.getTickBite());
		target.setFleaBite(source.getFleaBite());
		target.setKindOfExposureBite(source.getKindOfExposureBite());
		target.setKindOfExposureTouch(source.getKindOfExposureTouch());
		target.setKindOfExposureScratch(source.getKindOfExposureScratch());
		target.setKindOfExposureLick(source.getKindOfExposureLick());
		target.setKindOfExposureOther(source.getKindOfExposureOther());
		target.setKindOfExposureDetails(source.getKindOfExposureDetails());
		target.setDateOfLastExposure(source.getDateOfLastExposure());
		target.setPlaceOfLastExposure(source.getPlaceOfLastExposure());
		target.setAnimalCondition(source.getAnimalCondition());
		target.setAnimalVaccinationStatus(source.getAnimalVaccinationStatus());
		target.setProphylaxisStatus(source.getProphylaxisStatus());
		target.setDateOfProphylaxis(source.getDateOfProphylaxis());
		target.setVisitedHealthFacility(source.getVisitedHealthFacility());
		target.setContactWithSourceRespiratoryCase(source.getContactWithSourceRespiratoryCase());
		target.setVisitedAnimalMarket(source.getVisitedAnimalMarket());
		target.setCamels(source.getCamels());
		target.setSnakes(source.getSnakes());

		List<EpiDataBurialDto> burialDtos = new ArrayList<>();
		for (EpiDataBurial burial : source.getBurials()) {
			EpiDataBurialDto burialDto = toDto(burial);
			burialDtos.add(burialDto);
		}
		target.setBurials(burialDtos);

		List<EpiDataGatheringDto> gatheringDtos = new ArrayList<>();
		for (EpiDataGathering gathering : source.getGatherings()) {
			EpiDataGatheringDto gatheringDto = toDto(gathering);
			gatheringDtos.add(gatheringDto);
		}
		target.setGatherings(gatheringDtos);

		List<EpiDataTravelDto> travelDtos = new ArrayList<>();
		for (EpiDataTravel travel : source.getTravels()) {
			EpiDataTravelDto travelDto = toDto(travel);
			travelDtos.add(travelDto);
		}
		target.setTravels(travelDtos);

		return target;
	}

	public static EpiDataBurialDto toDto(EpiDataBurial burial) {

		if (burial == null) {
			return null;
		}

		EpiDataBurialDto target = new EpiDataBurialDto();
		EpiDataBurial source = burial;

		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());

		target.setBurialAddress(LocationFacadeEjb.toDto(source.getBurialAddress()));
		target.setBurialDateFrom(source.getBurialDateFrom());
		target.setBurialDateTo(source.getBurialDateTo());
		target.setBurialIll(source.getBurialIll());
		target.setBurialPersonName(source.getBurialPersonName());
		target.setBurialRelation(source.getBurialRelation());
		target.setBurialTouching(source.getBurialTouching());

		return target;
	}

	public static EpiDataGatheringDto toDto(EpiDataGathering gathering) {

		if (gathering == null) {
			return null;
		}

		EpiDataGatheringDto target = new EpiDataGatheringDto();
		EpiDataGathering source = gathering;

		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());

		target.setDescription(source.getDescription());
		target.setGatheringAddress(LocationFacadeEjb.toDto(source.getGatheringAddress()));
		target.setGatheringDate(source.getGatheringDate());

		return target;
	}

	public static EpiDataTravelDto toDto(EpiDataTravel travel) {

		if (travel == null) {
			return null;
		}

		EpiDataTravelDto target = new EpiDataTravelDto();
		EpiDataTravel source = travel;

		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());

		target.setTravelDateFrom(source.getTravelDateFrom());
		target.setTravelDateTo(source.getTravelDateTo());
		target.setTravelDestination(source.getTravelDestination());
		target.setTravelType(source.getTravelType());

		return target;
	}

	@LocalBean
	@Stateless
	public static class EpiDataFacadeEjbLocal extends EpiDataFacadeEjb {

	}
}
