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
package de.symeda.sormas.backend.hospitalization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationFacade;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "HospitalizationFacade")
public class HospitalizationFacadeEjb implements HospitalizationFacade {

	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private PreviousHospitalizationService previousHospitalizationService;

	public Hospitalization fillOrBuildEntity(HospitalizationDto source, Hospitalization target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, Hospitalization::new, checkChangeDate);

		target.setAdmittedToHealthFacility(source.getAdmittedToHealthFacility());
		target.setAdmissionDate(source.getAdmissionDate());
		target.setDischargeDate(source.getDischargeDate());
		target.setHospitalizedPreviously(source.getHospitalizedPreviously());
		target.setIsolated(source.getIsolated());
		target.setIsolationDate(source.getIsolationDate());
		target.setLeftAgainstAdvice(source.getLeftAgainstAdvice());
		target.setHospitalizationReason(source.getHospitalizationReason());
		target.setOtherHospitalizationReason(source.getOtherHospitalizationReason());

		List<PreviousHospitalization> previousHospitalizations = new ArrayList<>();
		for (PreviousHospitalizationDto prevDto : source.getPreviousHospitalizations()) {
			//prevHospitalization will be present in 1st level cache based on #10214
			PreviousHospitalization prevHosp = previousHospitalizationService.getByUuid(prevDto.getUuid());
			prevHosp = fillOrBuildEntity(prevDto, prevHosp, checkChangeDate);
			prevHosp.setHospitalization(target);
			previousHospitalizations.add(prevHosp);
		}
		if (!DataHelper.equalContains(target.getPreviousHospitalizations(), previousHospitalizations)) {
			// note: DataHelper.equal does not work here, because target.getAddresses may be a PersistentBag when using lazy loading
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.getPreviousHospitalizations().clear();
		target.getPreviousHospitalizations().addAll(previousHospitalizations);
		target.setIntensiveCareUnit(source.getIntensiveCareUnit());
		target.setIntensiveCareUnitStart(source.getIntensiveCareUnitStart());
		target.setIntensiveCareUnitEnd(source.getIntensiveCareUnitEnd());
		target.setOxygenPrescribed(source.getOxygenPrescribed());
		target.setStillHospitalized(source.getStillHospitalized());
		target.setIcuLengthOfStay(source.getIcuLengthOfStay());
		target.setDescription(source.getDescription());
		target.setCurrentlyHospitalized(source.getCurrentlyHospitalized());
		target.setDurationOfHospitalization(source.getDurationOfHospitalization());

		return target;
	}

	public PreviousHospitalization fillOrBuildEntity(PreviousHospitalizationDto source, PreviousHospitalization target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, PreviousHospitalization::new, checkChangeDate);

		if (!DataHelper.isSame(target.getRegion(), source.getRegion())) {
			target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		}

		target.setAdmittedToHealthFacility(source.getAdmittedToHealthFacility());
		target.setAdmissionDate(source.getAdmissionDate());
		target.setDischargeDate(source.getDischargeDate());

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));

		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());
		target.setHealthFacilityDepartment(source.getHealthFacilityDepartment());
		target.setIsolated(source.getIsolated());
		target.setIsolationDate(source.getIsolationDate());
		target.setDescription(source.getDescription());
		target.setHospitalizationReason(source.getHospitalizationReason());
		target.setOtherHospitalizationReason(source.getOtherHospitalizationReason());
		target.setIntensiveCareUnit(source.getIntensiveCareUnit());
		target.setIntensiveCareUnitStart(source.getIntensiveCareUnitStart());
		target.setIntensiveCareUnitEnd(source.getIntensiveCareUnitEnd());
		target.setIcuLengthOfStay(source.getIcuLengthOfStay());
		target.setOxygenPrescribed(source.getOxygenPrescribed());
		target.setStillHospitalized(source.getStillHospitalized());

		return target;
	}

	public static HospitalizationDto toDto(Hospitalization hospitalization) {

		if (hospitalization == null) {
			return null;
		}

		HospitalizationDto target = new HospitalizationDto();
		Hospitalization source = hospitalization;

		DtoHelper.fillDto(target, source);

		target.setAdmittedToHealthFacility(source.getAdmittedToHealthFacility());
		target.setAdmissionDate(source.getAdmissionDate());
		target.setDischargeDate(source.getDischargeDate());
		target.setHospitalizedPreviously(source.getHospitalizedPreviously());
		target.setIsolated(source.getIsolated());
		target.setIsolationDate(source.getIsolationDate());
		target.setLeftAgainstAdvice(source.getLeftAgainstAdvice());
		target.setHospitalizationReason(source.getHospitalizationReason());
		target.setOtherHospitalizationReason(source.getOtherHospitalizationReason());

		List<PreviousHospitalizationDto> previousHospitalizations = new ArrayList<>();
		for (PreviousHospitalization prevDto : source.getPreviousHospitalizations()) {
			PreviousHospitalizationDto prevHosp = toDto(prevDto);
			previousHospitalizations.add(prevHosp);
		}
		target.setPreviousHospitalizations(previousHospitalizations);
		target.setIntensiveCareUnit(source.getIntensiveCareUnit());
		target.setIntensiveCareUnitStart(source.getIntensiveCareUnitStart());
		target.setIntensiveCareUnitEnd(source.getIntensiveCareUnitEnd());
		target.setOxygenPrescribed(source.getOxygenPrescribed());
		target.setStillHospitalized(source.getStillHospitalized());
		target.setIcuLengthOfStay(source.getIcuLengthOfStay());
		target.setDescription(source.getDescription());
		target.setCurrentlyHospitalized(source.getCurrentlyHospitalized());
		target.setDurationOfHospitalization(source.getDurationOfHospitalization());

		return target;
	}

	public static PreviousHospitalizationDto toDto(PreviousHospitalization source) {

		if (source == null) {
			return null;
		}

		PreviousHospitalizationDto target = new PreviousHospitalizationDto();

		DtoHelper.fillDto(target, source);

		target.setAdmittedToHealthFacility(source.getAdmittedToHealthFacility());
		target.setAdmissionDate(source.getAdmissionDate());
		target.setDischargeDate(source.getDischargeDate());
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());
		target.setHealthFacilityDepartment(source.getHealthFacilityDepartment());
		target.setIsolated(source.getIsolated());
		target.setIsolationDate(source.getIsolationDate());
		target.setDescription(source.getDescription());
		target.setHospitalizationReason(source.getHospitalizationReason());
		target.setOtherHospitalizationReason(source.getOtherHospitalizationReason());
		target.setIntensiveCareUnit(source.getIntensiveCareUnit());
		target.setIntensiveCareUnitStart(source.getIntensiveCareUnitStart());
		target.setIntensiveCareUnitEnd(source.getIntensiveCareUnitEnd());
		target.setIcuLengthOfStay(source.getIcuLengthOfStay());
		target.setOxygenPrescribed(source.getOxygenPrescribed());
		target.setStillHospitalized(source.getStillHospitalized());

		return target;
	}

	@LocalBean
	@Stateless
	public static class HospitalizationFacadeEjbLocal extends HospitalizationFacadeEjb {

	}
}
