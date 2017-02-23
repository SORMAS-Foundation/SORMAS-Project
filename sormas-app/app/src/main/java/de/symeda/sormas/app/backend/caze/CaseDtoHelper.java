package de.symeda.sormas.app.backend.caze;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
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
    private HospitalizationDtoHelper hospitalizationDtoHelper;

    public CaseDtoHelper() {
        symptomsDtoHelper = new SymptomsDtoHelper();
        hospitalizationDtoHelper = new HospitalizationDtoHelper();
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
    public void fillInnerFromDto(Case target, CaseDataDto source) {

        target.setCaseClassification(source.getCaseClassification());
        target.setInvestigationStatus(source.getInvestigationStatus());
        target.setDisease(source.getDisease());
        if (source.getHealthFacility() != null) {
            target.setHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(source.getHealthFacility().getUuid()));
        } else {
            target.setHealthFacility(null);
        }
        if (source.getPerson() != null) {
            target.setPerson(DatabaseHelper.getPersonDao().queryUuid(source.getPerson().getUuid()));
        } else {
            target.setPerson(null);
        }
        target.setInvestigatedDate(source.getInvestigatedDate());
        target.setReportDate(source.getReportDate());
        if (source.getReportingUser() != null) {
            target.setReportingUser(DatabaseHelper.getUserDao().queryUuid(source.getReportingUser().getUuid()));
        } else {
            target.setReportingUser(null);
        }

        target.setSymptoms(symptomsDtoHelper.fillOrCreateFromDto(target.getSymptoms(), source.getSymptoms()));

        if (source.getRegion() != null) {
            target.setRegion(DatabaseHelper.getRegionDao().queryUuid(source.getRegion().getUuid()));
        } else {
            target.setRegion(null);
        }

        if (source.getDistrict() != null) {
            target.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(source.getDistrict().getUuid()));
        } else {
            target.setDistrict(null);
        }

        if (source.getCommunity() != null) {
            target.setCommunity(DatabaseHelper.getCommunityDao().queryUuid(source.getCommunity().getUuid()));
        } else {
            target.setCommunity(null);
        }

        if (source.getHospitalization() != null) {
            target.setHospitalization(DatabaseHelper.getHospitalizationDao().queryUuid(source.getHospitalization().getUuid()));
        } else {
            target.setHospitalization(null);
        }

        target.setSurveillanceOfficer(DatabaseHelper.getUserDao().getByReferenceDto(source.getSurveillanceOfficer()));

        // TODO user
    }

    @Override
    public void fillInnerFromAdo(CaseDataDto target, Case source) {

        target.setCaseClassification(source.getCaseClassification());
        target.setInvestigationStatus(source.getInvestigationStatus());

        target.setDisease(source.getDisease());
        if (source.getHealthFacility() != null) {
            Facility facility = DatabaseHelper.getFacilityDao().queryForId(source.getHealthFacility().getId());
            target.setHealthFacility(FacilityDtoHelper.toReferenceDto(facility));
        } else {
            target.setHealthFacility(null);
        }

        if (source.getPerson() != null) {
            Person person = DatabaseHelper.getPersonDao().queryForId(source.getPerson().getId());
            target.setPerson(PersonDtoHelper.toReferenceDto(person));
        }

        target.setInvestigatedDate(source.getInvestigatedDate());
        target.setReportDate(source.getReportDate());

        if (source.getReportingUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(source.getReportingUser().getId());
            target.setReportingUser(UserDtoHelper.toReferenceDto(user));
        } else {
            target.setReportingUser(null);
        }

        if (source.getSymptoms() != null) {
            Symptoms symptoms = DatabaseHelper.getSymptomsDao().queryForId(source.getSymptoms().getId());
            SymptomsDto symptomsDto = symptomsDtoHelper.adoToDto(symptoms);
            target.setSymptoms(symptomsDto);
        } else {
            target.setSymptoms(null);
        }

        if (source.getRegion() != null) {
            Region region = DatabaseHelper.getRegionDao().queryForId(source.getRegion().getId());
            target.setRegion(RegionDtoHelper.toReferenceDto(region));
        } else {
            target.setRegion(null);
        }


        if (source.getDistrict() != null) {
            District district = DatabaseHelper.getDistrictDao().queryForId(source.getDistrict().getId());
            target.setDistrict(DistrictDtoHelper.toReferenceDto(district));
        } else {
            target.setDistrict(null);
        }

        if (source.getCommunity() != null) {
            Community community = DatabaseHelper.getCommunityDao().queryForId(source.getCommunity().getId());
            target.setCommunity(CommunityDtoHelper.toReferenceDto(community));
        } else {
            target.setCommunity(null);
        }

        if (source.getSurveillanceOfficer() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(source.getSurveillanceOfficer().getId());
            target.setSurveillanceOfficer(UserDtoHelper.toReferenceDto(user));
        } else {
            target.setSurveillanceOfficer(null);
        }

        if (source.getHospitalization() != null) {
            Hospitalization hospitalization = DatabaseHelper.getHospitalizationDao().queryForId(source.getHospitalization().getId());
            target.setHospitalization(hospitalizationDtoHelper.adoToDto(hospitalization));
        } else {
            target.setHospitalization(null);
        }
        // TODO user
    }


    public static CaseReferenceDto toReferenceDto(Case ado) {
        if (ado == null) {
            return null;
        }
        CaseReferenceDto dto = new CaseReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }
}
