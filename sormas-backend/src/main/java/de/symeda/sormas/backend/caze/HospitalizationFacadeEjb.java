package de.symeda.sormas.backend.caze;

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.HospitalizationDto;
import de.symeda.sormas.api.caze.HospitalizationFacade;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;

@Stateless(name = "HospitalizationFacade")
public class HospitalizationFacadeEjb implements HospitalizationFacade {

	@EJB
	private HospitalizationService service;
	@EJB
	private CaseService caseService;
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
		
		target.setAdmissionDate(source.getAdmissionDate());
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setHospitalized(source.getHospitalized());
		target.setHospitalizedPreviously(source.getHospitalizedPreviously());
		target.setIsolated(source.getIsolated());
		target.setIsolationDate(source.getIsolationDate());
		
		return hospitalization;
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
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setHospitalized(source.getHospitalized());
		target.setHospitalizedPreviously(source.getHospitalizedPreviously());
		target.setIsolated(source.getIsolated());
		target.setIsolationDate(source.getIsolationDate());
		
		return target;
	}

	@LocalBean
	@Stateless
	public static class HospitalizationFacadeEjbLocal extends HospitalizationFacadeEjb {
	}
	
}
