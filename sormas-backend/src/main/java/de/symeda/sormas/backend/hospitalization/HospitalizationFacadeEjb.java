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

import java.sql.Timestamp;
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
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "HospitalizationFacade")
public class HospitalizationFacadeEjb implements HospitalizationFacade {

	@EJB
	private HospitalizationService service;
	@EJB
	private PreviousHospitalizationService prevHospService;
	@EJB
	private CaseService caseService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;

	public Hospitalization fromDto(HospitalizationDto dto) {

		if (dto == null) {
			return null;
		}

		Hospitalization hospitalization = service.getByUuid(dto.getUuid());
		if (hospitalization == null) {
			hospitalization = new Hospitalization();
			hospitalization.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				hospitalization.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		}

		Hospitalization target = hospitalization;
		HospitalizationDto source = dto;
		DtoHelper.validateDto(source, target);

		target.setAdmittedToHealthFacility(source.getAdmittedToHealthFacility());
		target.setAdmissionDate(source.getAdmissionDate());
		target.setDischargeDate(source.getDischargeDate());
		target.setHospitalizedPreviously(source.getHospitalizedPreviously());
		target.setIsolated(source.getIsolated());
		target.setIsolationDate(source.getIsolationDate());
		target.setLeftAgainstAdvice(source.getLeftAgainstAdvice());

		List<PreviousHospitalization> previousHospitalizations = new ArrayList<>();
		for (PreviousHospitalizationDto prevDto : source.getPreviousHospitalizations()) {
			PreviousHospitalization prevHosp = fromDto(prevDto);
			prevHosp.setHospitalization(target);
			previousHospitalizations.add(prevHosp);
		}
		if (!DataHelper.equal(target.getPreviousHospitalizations(), previousHospitalizations)) {
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.getPreviousHospitalizations().clear();
		target.getPreviousHospitalizations().addAll(previousHospitalizations);
		target.setIntensiveCareUnit(source.getIntensiveCareUnit());
		target.setIntensiveCareUnitStart(source.getIntensiveCareUnitStart());
		target.setIntensiveCareUnitEnd(source.getIntensiveCareUnitEnd());

		return hospitalization;
	}

	public PreviousHospitalization fromDto(PreviousHospitalizationDto dto) {

		if (dto == null) {
			return null;
		}

		PreviousHospitalization prevHospitalization = prevHospService.getByUuid(dto.getUuid());
		if (prevHospitalization == null) {
			prevHospitalization = new PreviousHospitalization();
			prevHospitalization.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				prevHospitalization.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		}

		PreviousHospitalization target = prevHospitalization;
		PreviousHospitalizationDto source = dto;
		DtoHelper.validateDto(source, target);

		target.setAdmissionDate(source.getAdmissionDate());
		target.setDischargeDate(source.getDischargeDate());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());
		target.setIsolated(source.getIsolated());
		target.setDescription(source.getDescription());

		return prevHospitalization;
	}

	public static HospitalizationDto toDto(Hospitalization hospitalization) {

		if (hospitalization == null) {
			return null;
		}

		HospitalizationDto target = new HospitalizationDto();
		Hospitalization source = hospitalization;

		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());

		target.setAdmittedToHealthFacility(source.getAdmittedToHealthFacility());
		target.setAdmissionDate(source.getAdmissionDate());
		target.setDischargeDate(source.getDischargeDate());
		target.setHospitalizedPreviously(source.getHospitalizedPreviously());
		target.setIsolated(source.getIsolated());
		target.setIsolationDate(source.getIsolationDate());
		target.setLeftAgainstAdvice(source.getLeftAgainstAdvice());

		List<PreviousHospitalizationDto> previousHospitalizations = new ArrayList<>();
		for (PreviousHospitalization prevDto : source.getPreviousHospitalizations()) {
			PreviousHospitalizationDto prevHosp = toDto(prevDto);
			previousHospitalizations.add(prevHosp);
		}
		target.setPreviousHospitalizations(previousHospitalizations);
		target.setIntensiveCareUnit(source.getIntensiveCareUnit());
		target.setIntensiveCareUnitStart(source.getIntensiveCareUnitStart());
		target.setIntensiveCareUnitEnd(source.getIntensiveCareUnitEnd());

		return target;
	}

	public static PreviousHospitalizationDto toDto(PreviousHospitalization hospitalization) {

		if (hospitalization == null) {
			return null;
		}

		PreviousHospitalizationDto target = new PreviousHospitalizationDto();
		PreviousHospitalization source = hospitalization;

		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());

		target.setAdmissionDate(source.getAdmissionDate());
		target.setDischargeDate(source.getDischargeDate());
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());
		target.setIsolated(source.getIsolated());
		target.setDescription(source.getDescription());

		return target;
	}

	@LocalBean
	@Stateless
	public static class HospitalizationFacadeEjbLocal extends HospitalizationFacadeEjb {

	}
}
