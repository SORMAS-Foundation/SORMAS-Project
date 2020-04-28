/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.caze;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.backend.caze.maternalhistory.MaternalHistoryDtoHelper;
import de.symeda.sormas.app.backend.caze.porthealthinfo.PortHealthInfoDtoHelper;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalCourseDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataDtoHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.HospitalizationDtoHelper;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntryDtoHelper;
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
import de.symeda.sormas.app.backend.therapy.Therapy;
import de.symeda.sormas.app.backend.therapy.TherapyDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class CaseDtoHelper extends AdoDtoHelper<Case, CaseDataDto> {

    private SymptomsDtoHelper  symptomsDtoHelper = new SymptomsDtoHelper();
    private HospitalizationDtoHelper hospitalizationDtoHelper = new HospitalizationDtoHelper();
    private EpiDataDtoHelper epiDataDtoHelper = new EpiDataDtoHelper();
    private TherapyDtoHelper therapyDtoHelper = new TherapyDtoHelper();
    private ClinicalCourseDtoHelper clinicalCourseDtoHelper = new ClinicalCourseDtoHelper();
    private MaternalHistoryDtoHelper maternalHistoryDtoHelper = new MaternalHistoryDtoHelper();
    private PortHealthInfoDtoHelper portHealthInfoDtoHelper = new PortHealthInfoDtoHelper();

    @Override
    protected Class<Case> getAdoClass() {
        return Case.class;
    }

    @Override
    protected Class<CaseDataDto> getDtoClass() {
        return CaseDataDto.class;
    }

    @Override
    protected Call<List<CaseDataDto>> pullAllSince(long since) throws NoConnectionException {
        return RetroProvider.getCaseFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<CaseDataDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return RetroProvider.getCaseFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<CaseDataDto> caseDataDtos) throws NoConnectionException {
        return RetroProvider.getCaseFacade().pushAll(caseDataDtos);
    }

    @Override
    public void fillInnerFromDto(Case target, CaseDataDto source) {

        target.setCaseClassification(source.getCaseClassification());
        target.setClassificationUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getClassificationUser()));
        target.setClassificationDate(source.getClassificationDate());
        target.setClassificationComment(source.getClassificationComment());

        target.setInvestigationStatus(source.getInvestigationStatus());
        target.setDisease(source.getDisease());
        target.setDiseaseDetails(source.getDiseaseDetails());
        target.setPlagueType(source.getPlagueType());
        target.setDengueFeverType(source.getDengueFeverType());

        target.setHealthFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getHealthFacility()));
        target.setHealthFacilityDetails(source.getHealthFacilityDetails());
        target.setPerson(DatabaseHelper.getPersonDao().getByReferenceDto(source.getPerson()));
        target.setInvestigatedDate(source.getInvestigatedDate());
        target.setDistrictLevelDate(source.getDistrictLevelDate());

        target.setReportDate(source.getReportDate());
        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));

        target.setSymptoms(symptomsDtoHelper.fillOrCreateFromDto(target.getSymptoms(), source.getSymptoms()));

        target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
        target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
        target.setCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getCommunity()));
        target.setPointOfEntry(DatabaseHelper.getPointOfEntryDao().getByReferenceDto(source.getPointOfEntry()));
        target.setPointOfEntryDetails(source.getPointOfEntryDetails());

        target.setHospitalization(hospitalizationDtoHelper.fillOrCreateFromDto(target.getHospitalization(), source.getHospitalization()));
        target.setEpiData(epiDataDtoHelper.fillOrCreateFromDto(target.getEpiData(), source.getEpiData()));
        target.setTherapy(therapyDtoHelper.fillOrCreateFromDto(target.getTherapy(), source.getTherapy()));
        target.setClinicalCourse(clinicalCourseDtoHelper.fillOrCreateFromDto(target.getClinicalCourse(), source.getClinicalCourse()));
        target.setMaternalHistory(maternalHistoryDtoHelper.fillOrCreateFromDto(target.getMaternalHistory(), source.getMaternalHistory()));
        target.setPortHealthInfo(portHealthInfoDtoHelper.fillOrCreateFromDto(target.getPortHealthInfo(), source.getPortHealthInfo()));

        target.setSurveillanceOfficer(DatabaseHelper.getUserDao().getByReferenceDto(source.getSurveillanceOfficer()));
        target.setClinicianName(source.getClinicianName());
        target.setClinicianPhone(source.getClinicianPhone());
        target.setClinicianEmail(source.getClinicianEmail());
        target.setPregnant(source.getPregnant());
        target.setVaccination(source.getVaccination());
        target.setVaccinationDoses(source.getVaccinationDoses());
        target.setVaccinationInfoSource(source.getVaccinationInfoSource());
        target.setSmallpoxVaccinationScar(source.getSmallpoxVaccinationScar());
        target.setSmallpoxVaccinationReceived(source.getSmallpoxVaccinationReceived());
        target.setVaccinationDate(source.getVaccinationDate());
        target.setEpidNumber(source.getEpidNumber());
        target.setCaseOrigin(source.getCaseOrigin());

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

        target.setOutcome(source.getOutcome());
        target.setOutcomeDate(source.getOutcomeDate());
        target.setSequelae(source.getSequelae());
        target.setSequelaeDetails(source.getSequelaeDetails());
        target.setNotifyingClinic(source.getNotifyingClinic());
        target.setNotifyingClinicDetails(source.getNotifyingClinicDetails());

        target.setVaccine(source.getVaccine());
        target.setRabiesType(source.getRabiesType());

        target.setCreationVersion(source.getCreationVersion());

        target.setAdditionalDetails(source.getAdditionalDetails());
        target.setExternalID(source.getExternalID());

        target.setQuarantine(source.getQuarantine());
        target.setQuarantineFrom(source.getQuarantineFrom());
        target.setQuarantineTo(source.getQuarantineTo());
        target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
        target.setQuarantineOrderedVerbally(source.isQuarantineOrderedVerbally());
        target.setQuarantineOrderedOfficialDocument(source.isQuarantineOrderedOfficialDocument());
        target.setQuarantineOrderedVerballyDate(source.getQuarantineOrderedVerballyDate());
        target.setQuarantineOrderedOfficialDocumentDate(source.getQuarantineOrderedOfficialDocumentDate());
        target.setQuarantineHomePossible(source.getQuarantineHomePossible());
        target.setQuarantineHomePossibleComment(source.getQuarantineHomePossibleComment());
        target.setQuarantineHomeSupplyEnsured(source.getQuarantineHomeSupplyEnsured());
        target.setQuarantineHomeSupplyEnsuredComment(source.getQuarantineHomeSupplyEnsuredComment());
        target.setReportingType(source.getReportingType());
    }

    @Override
    public void fillInnerFromAdo(CaseDataDto target, Case source) {

        target.setCaseClassification(source.getCaseClassification());
        if (source.getClassificationUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(source.getClassificationUser().getId());
            target.setClassificationUser(UserDtoHelper.toReferenceDto(user));
        } else {
            target.setClassificationUser(null);
        }
        target.setClassificationDate(source.getClassificationDate());
        target.setClassificationComment(source.getClassificationComment());

        target.setInvestigationStatus(source.getInvestigationStatus());

        target.setDisease(source.getDisease());
        target.setDiseaseDetails(source.getDiseaseDetails());
        target.setPlagueType(source.getPlagueType());
        target.setDengueFeverType(source.getDengueFeverType());

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
        target.setDistrictLevelDate(source.getDistrictLevelDate());

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

        if (source.getPointOfEntry() != null) {
            PointOfEntry pointOfEntry = DatabaseHelper.getPointOfEntryDao().queryForId(source.getPointOfEntry().getId());
            target.setPointOfEntry(PointOfEntryDtoHelper.toReferenceDto(pointOfEntry));
        } else {
            target.setPointOfEntry(null);
        }
        target.setPointOfEntryDetails(source.getPointOfEntryDetails());

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

        if (source.getTherapy() != null) {
            Therapy therapy = DatabaseHelper.getTherapyDao().queryForId(source.getTherapy().getId());
            target.setTherapy(therapyDtoHelper.adoToDto(therapy));
        } else {
            target.setTherapy(null);
        }

        if (source.getClinicalCourse() != null) {
            ClinicalCourse clinicalCourse = DatabaseHelper.getClinicalCourseDao().queryForId(source.getClinicalCourse().getId());
            target.setClinicalCourse(clinicalCourseDtoHelper.adoToDto(clinicalCourse));
        } else {
            target.setClinicalCourse(null);
        }

        if (source.getMaternalHistory() != null) {
            target.setMaternalHistory(maternalHistoryDtoHelper.adoToDto(DatabaseHelper.getMaternalHistoryDao().queryForId(source.getMaternalHistory().getId())));
        } else {
            target.setMaternalHistory(null);
        }

        if (source.getPortHealthInfo() != null) {
            target.setPortHealthInfo(portHealthInfoDtoHelper.adoToDto(DatabaseHelper.getPortHealthInfoDao().queryForId(source.getPortHealthInfo().getId())));
        } else {
            target.setPortHealthInfo(null);
        }

        target.setClinicianName(source.getClinicianName());
        target.setClinicianPhone(source.getClinicianPhone());
        target.setClinicianEmail(source.getClinicianEmail());
        target.setPregnant(source.getPregnant());
        target.setVaccination(source.getVaccination());
        target.setVaccinationDoses(source.getVaccinationDoses());
        target.setVaccinationInfoSource(source.getVaccinationInfoSource());
        target.setSmallpoxVaccinationScar(source.getSmallpoxVaccinationScar());
        target.setSmallpoxVaccinationReceived(source.getSmallpoxVaccinationReceived());
        target.setVaccinationDate(source.getVaccinationDate());
        target.setEpidNumber(source.getEpidNumber());
        target.setCaseOrigin(source.getCaseOrigin());

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

        target.setOutcome(source.getOutcome());
        target.setOutcomeDate(source.getOutcomeDate());
        target.setSequelae(source.getSequelae());
        target.setSequelaeDetails(source.getSequelaeDetails());
        target.setNotifyingClinic(source.getNotifyingClinic());
        target.setNotifyingClinicDetails(source.getNotifyingClinicDetails());

        target.setVaccine(source.getVaccine());
        target.setRabiesType(source.getRabiesType());

        target.setCreationVersion(source.getCreationVersion());

        target.setAdditionalDetails(source.getAdditionalDetails());
        target.setExternalID(source.getExternalID());

        target.setQuarantine(source.getQuarantine());
        target.setQuarantineFrom(source.getQuarantineFrom());
        target.setQuarantineTo(source.getQuarantineTo());
        target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
        target.setQuarantineOrderedVerbally(source.isQuarantineOrderedVerbally());
        target.setQuarantineOrderedOfficialDocument(source.isQuarantineOrderedOfficialDocument());
        target.setQuarantineOrderedVerballyDate(source.getQuarantineOrderedVerballyDate());
        target.setQuarantineOrderedOfficialDocumentDate(source.getQuarantineOrderedOfficialDocumentDate());
        target.setQuarantineHomePossible(source.getQuarantineHomePossible());
        target.setQuarantineHomePossibleComment(source.getQuarantineHomePossibleComment());
        target.setQuarantineHomeSupplyEnsured(source.getQuarantineHomeSupplyEnsured());
        target.setQuarantineHomeSupplyEnsuredComment(source.getQuarantineHomeSupplyEnsuredComment());
        target.setReportingType(source.getReportingType());
    }

    public static CaseReferenceDto toReferenceDto(Case ado) {
        if (ado == null) {
            return null;
        }
        CaseReferenceDto dto = new CaseReferenceDto(ado.getUuid());

        return dto;
    }

}
