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
	
	@Override
	public HospitalizationDto saveHospitalization(HospitalizationDto dto) {
		Hospitalization hospitalization = fromDto(dto);
		service.ensurePersisted(hospitalization);
		
		return toDto(hospitalization);
	}
	
	@Override
	public HospitalizationDto getHospitalizationByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}
	
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
		
		target.setAdmissionDate(source.getAdmissionDate());
		target.setDischargeDate(source.getDischargeDate());
		target.setHospitalizedPreviously(source.getHospitalizedPreviously());
		target.setIsolated(source.getIsolated());
		target.setIsolationDate(source.getIsolationDate());
		
		// It would be better to merge with the existing hospitalizations
		List<PreviousHospitalization> previousHospitalizations = new ArrayList<>();
		for (PreviousHospitalizationDto prevDto : source.getPreviousHospitalizations()) {
			PreviousHospitalization prevHosp = fromDto(prevDto);
			prevHosp.setHospitalization(target);
			previousHospitalizations.add(prevHosp);
		}
		if (!DataHelper.equal(target.getPreviousHospitalizations(), previousHospitalizations)) {
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.setPreviousHospitalizations(previousHospitalizations);
		
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
		
		target.setAdmissionDate(source.getAdmissionDate());
		target.setDischargeDate(source.getDischargeDate());
		target.setHospitalizedPreviously(source.getHospitalizedPreviously());
		target.setIsolated(source.getIsolated());
		target.setIsolationDate(source.getIsolationDate());
		
		List<PreviousHospitalizationDto> previousHospitalizations = new ArrayList<>();
		for (PreviousHospitalization prevDto : source.getPreviousHospitalizations()) {
			PreviousHospitalizationDto prevHosp = toDto(prevDto);
			previousHospitalizations.add(prevHosp);
		}
		target.setPreviousHospitalizations(previousHospitalizations);
		
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
		target.setIsolated(source.getIsolated());
		target.setDescription(source.getDescription());

		return target;
	}
	
	@LocalBean
	@Stateless
	public static class HospitalizationFacadeEjbLocal extends HospitalizationFacadeEjb {
	}
	
}
