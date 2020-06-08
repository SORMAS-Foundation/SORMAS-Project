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

package de.symeda.sormas.app.backend.epidata;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataDtoHelper extends AdoDtoHelper<EpiData, EpiDataDto> {

	private EpiDataBurialDtoHelper burialDtoHelper;
	private EpiDataGatheringDtoHelper gatheringDtoHelper;
	private EpiDataTravelDtoHelper travelDtoHelper;

	public EpiDataDtoHelper() {
		burialDtoHelper = new EpiDataBurialDtoHelper();
		gatheringDtoHelper = new EpiDataGatheringDtoHelper();
		travelDtoHelper = new EpiDataTravelDtoHelper();
	}

	@Override
	protected Class<EpiData> getAdoClass() {
		return EpiData.class;
	}

	@Override
	protected Class<EpiDataDto> getDtoClass() {
		return EpiDataDto.class;
	}

	@Override
	protected Call<List<EpiDataDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<EpiDataDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<EpiDataDto> epiDataDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(EpiData target, EpiDataDto source) {
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
		target.setCattle(source.getCattle());
		target.setOtherAnimals(source.getOtherAnimals());
		target.setOtherAnimalsDetails(source.getOtherAnimalsDetails());
		target.setWaterSource(source.getWaterSource());
		target.setWaterSourceOther(source.getWaterSourceOther());
		target.setWaterBody(source.getWaterBody());
		target.setWaterBodyDetails(source.getWaterBodyDetails());
		target.setTickBite(source.getTickBite());
		target.setDateOfLastExposure(source.getDateOfLastExposure());
		target.setPlaceOfLastExposure(source.getPlaceOfLastExposure());
		target.setAnimalCondition(source.getAnimalCondition());
		target.setFleaBite(source.getFleaBite());

		target.setKindOfExposureBite(source.getKindOfExposureBite());
		target.setKindOfExposureLick(source.getKindOfExposureLick());
		target.setKindOfExposureScratch(source.getKindOfExposureScratch());
		target.setKindOfExposureTouch(source.getKindOfExposureTouch());
		target.setKindOfExposureOther(source.getKindOfExposureOther());
		target.setKindOfExposureDetails(source.getKindOfExposureDetails());

		target.setAnimalVaccinationStatus(source.getAnimalVaccinationStatus());

		target.setDogs(source.getDogs());
		target.setCats(source.getCats());
		target.setCanidae(source.getCanidae());
		target.setRabbits(source.getRabbits());

		target.setProphylaxisStatus(source.getProphylaxisStatus());
		target.setDateOfProphylaxis(source.getDateOfProphylaxis());
		target.setVisitedHealthFacility(source.getVisitedHealthFacility());
		target.setContactWithSourceRespiratoryCase(source.getContactWithSourceRespiratoryCase());
		target.setVisitedAnimalMarket(source.getVisitedAnimalMarket());
		target.setCamels(source.getCamels());
		target.setSnakes(source.getSnakes());

		// just recreate all of this and throw the old stuff away
		List<EpiDataBurial> burials = new ArrayList<>();
		if (!source.getBurials().isEmpty()) {
			for (EpiDataBurialDto burialDto : source.getBurials()) {
				EpiDataBurial burial = burialDtoHelper.fillOrCreateFromDto(null, burialDto);
				burial.setEpiData(target);
				burials.add(burial);
			}
		}
		target.setBurials(burials);

		// just recreate all of this and throw the old stuff away
		List<EpiDataGathering> gatherings = new ArrayList<>();
		if (!source.getGatherings().isEmpty()) {
			for (EpiDataGatheringDto gatheringDto : source.getGatherings()) {
				EpiDataGathering gathering = gatheringDtoHelper.fillOrCreateFromDto(null, gatheringDto);
				gathering.setEpiData(target);
				gatherings.add(gathering);
			}
		}
		target.setGatherings(gatherings);

		// just recreate all of this and throw the old stuff away
		List<EpiDataTravel> travels = new ArrayList<>();
		if (!source.getTravels().isEmpty()) {
			for (EpiDataTravelDto travelDto : source.getTravels()) {
				EpiDataTravel travel = travelDtoHelper.fillOrCreateFromDto(null, travelDto);
				travel.setEpiData(target);
				travels.add(travel);
			}
		}
		target.setTravels(travels);
	}

	@Override
	public void fillInnerFromAdo(EpiDataDto target, EpiData source) {

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
		target.setCattle(source.getCattle());
		target.setOtherAnimals(source.getOtherAnimals());
		target.setOtherAnimalsDetails(source.getOtherAnimalsDetails());
		target.setWaterSource(source.getWaterSource());
		target.setWaterSourceOther(source.getWaterSourceOther());
		target.setWaterBody(source.getWaterBody());
		target.setWaterBodyDetails(source.getWaterBodyDetails());
		target.setTickBite(source.getTickBite());
		target.setDateOfLastExposure(source.getDateOfLastExposure());
		target.setPlaceOfLastExposure(source.getPlaceOfLastExposure());
		target.setAnimalCondition(source.getAnimalCondition());
		target.setFleaBite(source.getFleaBite());

		target.setKindOfExposureBite(source.getKindOfExposureBite());
		target.setKindOfExposureLick(source.getKindOfExposureLick());
		target.setKindOfExposureScratch(source.getKindOfExposureScratch());
		target.setKindOfExposureTouch(source.getKindOfExposureTouch());
		target.setKindOfExposureOther(source.getKindOfExposureOther());
		target.setKindOfExposureDetails(source.getKindOfExposureDetails());

		target.setAnimalVaccinationStatus(source.getAnimalVaccinationStatus());

		target.setDogs(source.getDogs());
		target.setCats(source.getCats());
		target.setCanidae(source.getCanidae());
		target.setRabbits(source.getRabbits());

		target.setProphylaxisStatus(source.getProphylaxisStatus());
		target.setDateOfProphylaxis(source.getDateOfProphylaxis());
		target.setVisitedHealthFacility(source.getVisitedHealthFacility());
		target.setContactWithSourceRespiratoryCase(source.getContactWithSourceRespiratoryCase());
		target.setVisitedAnimalMarket(source.getVisitedAnimalMarket());
		target.setCamels(source.getCamels());
		target.setSnakes(source.getSnakes());

		List<EpiDataBurialDto> burialDtos = new ArrayList<>();
		if (!source.getBurials().isEmpty()) {
			for (EpiDataBurial burial : source.getBurials()) {
				EpiDataBurialDto burialDto = burialDtoHelper.adoToDto(burial);
				burialDtos.add(burialDto);
			}
		}
		target.setBurials(burialDtos);

		List<EpiDataGatheringDto> gatheringDtos = new ArrayList<>();
		if (!source.getGatherings().isEmpty()) {
			for (EpiDataGathering gathering : source.getGatherings()) {
				EpiDataGatheringDto gatheringDto = gatheringDtoHelper.adoToDto(gathering);
				gatheringDtos.add(gatheringDto);
			}
		}
		target.setGatherings(gatheringDtos);

		List<EpiDataTravelDto> travelDtos = new ArrayList<>();
		if (!source.getTravels().isEmpty()) {
			for (EpiDataTravel travel : source.getTravels()) {
				EpiDataTravelDto travelDto = travelDtoHelper.adoToDto(travel);
				travelDtos.add(travelDto);
			}
		}
		target.setTravels(travelDtos);
	}
}
