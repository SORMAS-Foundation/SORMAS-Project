package de.symeda.sormas.app.backend.caze;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class CaseDtoHelper extends AdoDtoHelper<Case, CaseDataDto> {

    private SymptomsDtoHelper symptomsDtoHelper;
    private RegionDtoHelper regionDtoHelper;
    private UserDtoHelper userDtoHelper;

    public CaseDtoHelper() {
        symptomsDtoHelper = new SymptomsDtoHelper();
        regionDtoHelper = new RegionDtoHelper();
    }

    @Override
    public Case create() {
        return new Case();
    }

    @Override
    public CaseDataDto createDto() {
        return new CaseDataDto();
    }

    @Override
    public void fillInnerFromDto(Case ado, CaseDataDto dto) {

        ado.setCaseStatus(dto.getCaseStatus());
        ado.setDisease(dto.getDisease());
        if (dto.getHealthFacility() != null) {
            ado.setHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(dto.getHealthFacility().getUuid()));
        } else {
            ado.setHealthFacility(null);
        }
        if (dto.getPerson() != null) {
            ado.setPerson(DatabaseHelper.getPersonDao().queryUuid(dto.getPerson().getUuid()));
        } else {
            ado.setPerson(null);
        }
        ado.setInvestigatedDate(dto.getInvestigatedDate());
        ado.setReportDate(dto.getReportDate());
        if (dto.getReportingUser() != null) {
            ado.setReportingUser(DatabaseHelper.getUserDao().queryUuid(dto.getReportingUser().getUuid()));
        } else {
            ado.setReportingUser(null);
        }

        ado.setSymptoms(symptomsDtoHelper.fillOrCreateFromDto(ado.getSymptoms(), dto.getSymptoms()));

        if (dto.getRegion() != null) {
            ado.setRegion(DatabaseHelper.getRegionDao().queryUuid(dto.getRegion().getUuid()));
        } else {
            ado.setRegion(null);
        }

        if (dto.getDistrict() != null) {
            ado.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(dto.getDistrict().getUuid()));
        } else {
            ado.setDistrict(null);
        }

        if (dto.getCommunity() != null) {
            ado.setCommunity(DatabaseHelper.getCommunityDao().queryUuid(dto.getCommunity().getUuid()));
        } else {
            ado.setCommunity(null);
        }

        ado.setSurveillanceOfficer(DatabaseHelper.getUserDao().getByReferenceDto(dto.getSurveillanceOfficer()));

        // TODO user
    }

    @Override
    public void fillInnerFromAdo(CaseDataDto dto, Case ado) {

        dto.setCaseStatus(ado.getCaseStatus());
        dto.setDisease(ado.getDisease());
        if (ado.getHealthFacility() != null) {
            Facility facility = DatabaseHelper.getFacilityDao().queryForId(ado.getHealthFacility().getId());
            dto.setHealthFacility(AdoDtoHelper.toReferenceDto(facility));
        } else {
            dto.setHealthFacility(null);
        }

        if (ado.getPerson() != null) {
            Person person = DatabaseHelper.getPersonDao().queryForId(ado.getPerson().getId());
            dto.setPerson(PersonDtoHelper.toReferenceDto(person));
        }

        dto.setInvestigatedDate(ado.getInvestigatedDate());
        dto.setReportDate(ado.getReportDate());

        if (ado.getReportingUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getReportingUser().getId());
            dto.setReportingUser(UserDtoHelper.toReferenceDto(user));
        } else {
            dto.setReportingUser(null);
        }

        if (ado.getSymptoms() != null) {
            Symptoms symptoms = DatabaseHelper.getSymptomsDao().queryForId(ado.getSymptoms().getId());
            SymptomsDto symptomsDto = symptomsDtoHelper.adoToDto(symptoms);
            dto.setSymptoms(symptomsDto);
        } else {
            dto.setSymptoms(null);
        }

        if (ado.getRegion() != null) {
            Region region = DatabaseHelper.getRegionDao().queryForId(ado.getRegion().getId());
            ReferenceDto regionDto = AdoDtoHelper.toReferenceDto(region);
            dto.setRegion(regionDto);
        } else {
            dto.setRegion(null);
        }


        if (ado.getDistrict() != null) {
            District district = DatabaseHelper.getDistrictDao().queryForId(ado.getDistrict().getId());
            ReferenceDto districtDto = AdoDtoHelper.toReferenceDto(district);
            dto.setDistrict(districtDto);
        } else {
            dto.setDistrict(null);
        }

        if (ado.getCommunity() != null) {
            Community community = DatabaseHelper.getCommunityDao().queryForId(ado.getCommunity().getId());
            ReferenceDto communityDto = AdoDtoHelper.toReferenceDto(community);
            dto.setCommunity(communityDto);
        } else {
            dto.setCommunity(null);
        }

        if (ado.getSurveillanceOfficer() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getSurveillanceOfficer().getId());
            dto.setSurveillanceOfficer(UserDtoHelper.toReferenceDto(user));
        } else {
            dto.setSurveillanceOfficer(null);
        }
        // TODO user
    }
}
