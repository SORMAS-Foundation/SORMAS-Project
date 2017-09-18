package de.symeda.sormas.app.backend.caze;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataDtoHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.HospitalizationDtoHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
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
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.DataUtils;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class CaseDtoHelper extends AdoDtoHelper<Case, CaseDataDto> {

    private SymptomsDtoHelper  symptomsDtoHelper = new SymptomsDtoHelper();
    private HospitalizationDtoHelper hospitalizationDtoHelper = new HospitalizationDtoHelper();
    private EpiDataDtoHelper epiDataDtoHelper = new EpiDataDtoHelper();


    @Override
    protected Class<Case> getAdoClass() {
        return Case.class;
    }

    @Override
    protected Class<CaseDataDto> getDtoClass() {
        return CaseDataDto.class;
    }

    @Override
    protected Call<List<CaseDataDto>> pullAllSince(long since) {
        return RetroProvider.getCaseFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<CaseDataDto>> pullByUuids(List<String> uuids) {
        return RetroProvider.getCaseFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<Integer> pushAll(List<CaseDataDto> caseDataDtos) {
        return RetroProvider.getCaseFacade().pushAll(caseDataDtos);
    }

    @Override
    public void fillInnerFromDto(Case target, CaseDataDto source) {

        target.setCaseClassification(source.getCaseClassification());
        target.setInvestigationStatus(source.getInvestigationStatus());
        target.setDisease(source.getDisease());
        target.setHealthFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getHealthFacility()));
        target.setHealthFacilityDetails(source.getHealthFacilityDetails());
        target.setPerson(DatabaseHelper.getPersonDao().getByReferenceDto(source.getPerson()));
        target.setInvestigatedDate(source.getInvestigatedDate());

        target.setReportDate(source.getReportDate());
        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));

        target.setSymptoms(symptomsDtoHelper.fillOrCreateFromDto(target.getSymptoms(), source.getSymptoms()));

        target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
        target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
        target.setCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getCommunity()));

        target.setHospitalization(hospitalizationDtoHelper.fillOrCreateFromDto(target.getHospitalization(), source.getHospitalization()));
        target.setEpiData(epiDataDtoHelper.fillOrCreateFromDto(target.getEpiData(), source.getEpiData()));

        target.setSurveillanceOfficer(DatabaseHelper.getUserDao().getByReferenceDto(source.getSurveillanceOfficer()));
        target.setPregnant(source.getPregnant());
        target.setMeaslesVaccination(source.getMeaslesVaccination());
        target.setMeaslesDoses(source.getMeaslesDoses());
        target.setMeaslesVaccinationInfoSource(source.getMeaslesVaccinationInfoSource());
        target.setYellowFeverVaccination(source.getYellowFeverVaccination());
        target.setYellowFeverVaccinationInfoSource(source.getYellowFeverVaccinationInfoSource());
        target.setEpidNumber(source.getEpidNumber());

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
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
        target.setHealthFacilityDetails(source.getHealthFacilityDetails());

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

        if (source.getEpiData() != null) {
            EpiData epiData = DatabaseHelper.getEpiDataDao().queryForId(source.getEpiData().getId());
            target.setEpiData(epiDataDtoHelper.adoToDto(epiData));
        } else {
            target.setEpiData(null);
        }

        target.setPregnant(source.getPregnant());
        target.setMeaslesVaccination(source.getMeaslesVaccination());
        target.setMeaslesDoses(source.getMeaslesDoses());
        target.setMeaslesVaccinationInfoSource(source.getMeaslesVaccinationInfoSource());
        target.setYellowFeverVaccination(source.getYellowFeverVaccination());
        target.setYellowFeverVaccinationInfoSource(source.getYellowFeverVaccinationInfoSource());
        target.setEpidNumber(source.getEpidNumber());

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
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
