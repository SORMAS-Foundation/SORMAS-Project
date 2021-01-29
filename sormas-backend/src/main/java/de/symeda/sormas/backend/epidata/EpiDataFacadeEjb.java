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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataFacade;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.exposure.ExposureService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "EpiDataFacade")
public class EpiDataFacadeEjb implements EpiDataFacade {

	@EJB
	private EpiDataService service;
	@EJB
	private ExposureService exposureService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private ContactService contactService;
	@EJB
	private UserService userService;

	public EpiData fromDto(EpiDataDto source, boolean checkChangeDate) {

		if (source == null) {
			return null;
		}

		EpiData target = DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), EpiData::new, checkChangeDate);

		target.setExposureDetailsKnown(source.getExposureDetailsKnown());
		target.setContactWithSourceCaseKnown(source.getContactWithSourceCaseKnown());
		target.setHighTransmissionRiskArea(source.getHighTransmissionRiskArea());
		target.setLargeOutbreaksArea(source.getLargeOutbreaksArea());
		target.setAreaInfectedAnimals(source.getAreaInfectedAnimals());

		List<Exposure> exposures = new ArrayList<>();
		for (ExposureDto exposureDto : source.getExposures()) {
			Exposure exposure = fromExposureDto(exposureDto, checkChangeDate);
			exposure.setEpiData(target);
			exposures.add(exposure);
		}
		if (!DataHelper.equal(target.getExposures(), exposures)) {
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.getExposures().clear();
		target.getExposures().addAll(exposures);

		return target;
	}

	public Exposure fromExposureDto(ExposureDto source, boolean checkChangeDate) {

		if (source == null) {
			return null;
		}

		Exposure target = DtoHelper.fillOrBuildEntity(source, exposureService.getByUuid(source.getUuid()), Exposure::new, checkChangeDate);

		target.setAnimalCondition(source.getAnimalCondition());
		target.setTypeOfAnimal(source.getTypeOfAnimal());
		target.setTypeOfAnimalDetails(source.getTypeOfAnimalDetails());
		target.setAnimalContactType(source.getAnimalContactType());
		target.setAnimalContactTypeDetails(source.getAnimalContactTypeDetails());
		target.setAnimalMarket(source.getAnimalMarket());
		target.setAnimalVaccinated(source.getAnimalVaccinated());
		target.setContactToBodyFluids(source.getContactToBodyFluids());
		target.setContactToCase(contactService.getByReferenceDto(source.getContactToCase()));
		target.setDeceasedPersonIll(source.getDeceasedPersonIll());
		target.setDeceasedPersonName(source.getDeceasedPersonName());
		target.setDeceasedPersonRelation(source.getDeceasedPersonRelation());
		target.setDescription(source.getDescription());
		target.setEatingRawAnimalProducts(source.getEatingRawAnimalProducts());
		target.setEndDate(source.getEndDate());
		target.setExposureType(source.getExposureType());
		target.setExposureTypeDetails(source.getExposureTypeDetails());
		target.setGatheringDetails(source.getGatheringDetails());
		target.setGatheringType(source.getGatheringType());
		target.setHabitationDetails(source.getHabitationDetails());
		target.setHabitationType(source.getHabitationType());
		target.setHandlingAnimals(source.getHandlingAnimals());
		target.setHandlingSamples(source.getHandlingSamples());
		target.setIndoors(source.getIndoors());
		target.setLocation(locationFacade.fromDto(source.getLocation(), checkChangeDate));
		target.setLongFaceToFaceContact(source.getLongFaceToFaceContact());
		target.setOtherProtectiveMeasures(source.getOtherProtectiveMeasures());
		target.setProtectiveMeasuresDetails(source.getProtectiveMeasuresDetails());
		target.setOutdoors(source.getOutdoors());
		target.setPercutaneous(source.getPercutaneous());
		target.setPhysicalContactDuringPreparation(source.getPhysicalContactDuringPreparation());
		target.setPhysicalContactWithBody(source.getPhysicalContactWithBody());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setShortDistance(source.getShortDistance());
		target.setStartDate(source.getStartDate());
		target.setWearingMask(source.getWearingMask());
		target.setWearingPpe(source.getWearingPpe());
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setTypeOfPlaceDetails(source.getTypeOfPlaceDetails());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setSeatNumber(source.getSeatNumber());
		target.setBodyOfWater(source.getBodyOfWater());
		target.setWaterSource(source.getWaterSource());
		target.setWaterSourceDetails(source.getWaterSourceDetails());
		target.setProphylaxis(source.getProphylaxis());
		target.setProphylaxisDate(source.getProphylaxisDate());
		target.setRiskArea(source.getRiskArea());
		target.setPatientExpositionRole(source.getPatientExpositionRole());

		return target;
	}

	public static EpiDataDto toDto(EpiData epiData) {

		if (epiData == null) {
			return null;
		}

		EpiDataDto target = new EpiDataDto();
		EpiData source = epiData;

		DtoHelper.fillDto(target, source);

		target.setExposureDetailsKnown(source.getExposureDetailsKnown());
		target.setContactWithSourceCaseKnown(source.getContactWithSourceCaseKnown());
		target.setHighTransmissionRiskArea(source.getHighTransmissionRiskArea());
		target.setLargeOutbreaksArea(source.getLargeOutbreaksArea());
		target.setAreaInfectedAnimals(source.getAreaInfectedAnimals());

		List<ExposureDto> exposureDtos = new ArrayList<>();
		for (Exposure exposure : source.getExposures()) {
			ExposureDto exposureDto = toExposureDto(exposure);
			exposureDtos.add(exposureDto);
		}
		target.setExposures(exposureDtos);

		return target;
	}

	public static ExposureDto toExposureDto(Exposure source) {

		if (source == null) {
			return null;
		}

		ExposureDto target = new ExposureDto();

		DtoHelper.fillDto(target, source);

		target.setAnimalCondition(source.getAnimalCondition());
		target.setTypeOfAnimal(source.getTypeOfAnimal());
		target.setTypeOfAnimalDetails(source.getTypeOfAnimalDetails());
		target.setAnimalContactType(source.getAnimalContactType());
		target.setAnimalContactTypeDetails(source.getAnimalContactTypeDetails());
		target.setAnimalMarket(source.getAnimalMarket());
		target.setAnimalVaccinated(source.getAnimalVaccinated());
		target.setContactToBodyFluids(source.getContactToBodyFluids());
		target.setContactToCase(ContactFacadeEjb.toReferenceDto(source.getContactToCase()));
		target.setDeceasedPersonIll(source.getDeceasedPersonIll());
		target.setDeceasedPersonName(source.getDeceasedPersonName());
		target.setDeceasedPersonRelation(source.getDeceasedPersonRelation());
		target.setDescription(source.getDescription());
		target.setEatingRawAnimalProducts(source.getEatingRawAnimalProducts());
		target.setEndDate(source.getEndDate());
		target.setExposureType(source.getExposureType());
		target.setExposureTypeDetails(source.getExposureTypeDetails());
		target.setGatheringDetails(source.getGatheringDetails());
		target.setGatheringType(source.getGatheringType());
		target.setHabitationDetails(source.getHabitationDetails());
		target.setHabitationType(source.getHabitationType());
		target.setHandlingAnimals(source.getHandlingAnimals());
		target.setHandlingSamples(source.getHandlingSamples());
		target.setIndoors(source.getIndoors());
		target.setLocation(LocationFacadeEjb.toDto(source.getLocation()));
		target.setLongFaceToFaceContact(source.getLongFaceToFaceContact());
		target.setOtherProtectiveMeasures(source.getOtherProtectiveMeasures());
		target.setProtectiveMeasuresDetails(source.getProtectiveMeasuresDetails());
		target.setOutdoors(source.getOutdoors());
		target.setPercutaneous(source.getPercutaneous());
		target.setPhysicalContactDuringPreparation(source.getPhysicalContactDuringPreparation());
		target.setPhysicalContactWithBody(source.getPhysicalContactWithBody());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setShortDistance(source.getShortDistance());
		target.setStartDate(source.getStartDate());
		target.setWearingMask(source.getWearingMask());
		target.setWearingPpe(source.getWearingPpe());
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setTypeOfPlaceDetails(source.getTypeOfPlaceDetails());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setSeatNumber(source.getSeatNumber());
		target.setBodyOfWater(source.getBodyOfWater());
		target.setWaterSource(source.getWaterSource());
		target.setWaterSourceDetails(source.getWaterSourceDetails());
		target.setProphylaxis(source.getProphylaxis());
		target.setProphylaxisDate(source.getProphylaxisDate());
		target.setRiskArea(source.getRiskArea());
		target.setPatientExpositionRole(source.getPatientExpositionRole());

		return target;
	}

	@LocalBean
	@Stateless
	public static class EpiDataFacadeEjbLocal extends EpiDataFacadeEjb {
	}
}
