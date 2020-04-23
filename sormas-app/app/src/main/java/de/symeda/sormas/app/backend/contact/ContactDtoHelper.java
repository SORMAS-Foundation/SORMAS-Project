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

package de.symeda.sormas.app.backend.contact;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Stefan Szczesny on 29.11.2016.
 */
public class ContactDtoHelper extends AdoDtoHelper<Contact, ContactDto> {

    public ContactDtoHelper() {
    }

    @Override
    protected Class<Contact> getAdoClass() {
        return Contact.class;
    }

    @Override
    protected Class<ContactDto> getDtoClass() {
        return ContactDto.class;
    }

    @Override
    protected Call<List<ContactDto>> pullAllSince(long since) throws NoConnectionException {
        return RetroProvider.getContactFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<ContactDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return RetroProvider.getContactFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<ContactDto> contactDtos) throws NoConnectionException {
        return RetroProvider.getContactFacade().pushAll(contactDtos);
    }

    @Override
    public void fillInnerFromDto(Contact target, ContactDto source) {
        target.setCaseUuid(source.getCaze() != null ? source.getCaze().getUuid() : null);
        target.setDisease(source.getDisease());
        target.setDiseaseDetails(source.getDiseaseDetails());
        target.setPerson(DatabaseHelper.getPersonDao().getByReferenceDto(source.getPerson()));

        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
        target.setReportDateTime(source.getReportDateTime());
        target.setContactOfficer(DatabaseHelper.getUserDao().getByReferenceDto(source.getContactOfficer()));

        target.setLastContactDate(source.getLastContactDate());
        target.setContactProximity(source.getContactProximity());
        target.setContactClassification(source.getContactClassification());
        target.setContactStatus(source.getContactStatus());
        target.setRelationToCase(source.getRelationToCase());
        target.setRelationDescription(source.getRelationDescription());
        target.setFollowUpStatus(source.getFollowUpStatus());
        target.setFollowUpComment(source.getFollowUpComment());
        target.setFollowUpUntil(source.getFollowUpUntil());

        target.setDescription(source.getDescription());

        target.setResultingCaseUuid(source.getResultingCase() != null ? source.getResultingCase().getUuid() : null);

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
        target.setExternalID(source.getExternalID());
        target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
        target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));

        target.setHighPriority(source.isHighPriority());
        target.setImmunosuppressiveTherapyBasicDisease(source.getImmunosuppressiveTherapyBasicDisease());
        target.setImmunosuppressiveTherapyBasicDiseaseDetails(source.getImmunosuppressiveTherapyBasicDiseaseDetails());
        target.setCareForPeopleOver60(source.getCareForPeopleOver60());

        target.setQuarantine(source.getQuarantine());
        target.setQuarantineFrom(source.getQuarantineFrom());
        target.setQuarantineTo(source.getQuarantineTo());

        target.setCaseIdExternalSystem(source.getCaseIdExternalSystem());
        target.setCaseOrEventInformation(source.getCaseOrEventInformation());

        target.setQuarantineOrderMeans(source.getQuarantineOrderMeans());
        target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
    }

    @Override
    public void fillInnerFromAdo(ContactDto target, Contact source) {
        if (source.getPerson() != null) {
            Person person = DatabaseHelper.getPersonDao().queryForId(source.getPerson().getId());
            target.setPerson(PersonDtoHelper.toReferenceDto(person));
        } else {
            target.setPerson(null);
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
        if (source.getCaseUuid() != null) {
            target.setCaze(new CaseReferenceDto(source.getCaseUuid()));
        } else {
            target.setCaze(null);
        }
        if (source.getResultingCaseUuid() != null) {
            target.setResultingCase(new CaseReferenceDto(source.getResultingCaseUuid()));
        } else {
            target.setResultingCase(null);
        }

        target.setDisease(source.getDisease());
        target.setDiseaseDetails(source.getDiseaseDetails());

        if (source.getReportingUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(source.getReportingUser().getId());
            target.setReportingUser(UserDtoHelper.toReferenceDto(user));
        } else {
            target.setReportingUser(null);
        }
        target.setReportDateTime(source.getReportDateTime());

        target.setLastContactDate(source.getLastContactDate());
        target.setContactProximity(source.getContactProximity());
        target.setContactClassification(source.getContactClassification());
        target.setContactStatus(source.getContactStatus());
        target.setRelationToCase(source.getRelationToCase());
        target.setRelationDescription(source.getRelationDescription());
        target.setFollowUpStatus(source.getFollowUpStatus());
        target.setFollowUpComment(source.getFollowUpComment());
        target.setFollowUpUntil(source.getFollowUpUntil());

        if (source.getContactOfficer() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(source.getContactOfficer().getId());
            target.setContactOfficer(UserDtoHelper.toReferenceDto(user));
        } else {
            target.setContactOfficer(null);
        }

        target.setDescription(source.getDescription());
        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
        target.setExternalID(source.getExternalID());

        target.setHighPriority(source.isHighPriority());
        target.setImmunosuppressiveTherapyBasicDisease(source.getImmunosuppressiveTherapyBasicDisease());
        target.setImmunosuppressiveTherapyBasicDiseaseDetails(source.getImmunosuppressiveTherapyBasicDiseaseDetails());
        target.setCareForPeopleOver60(source.getCareForPeopleOver60());

        target.setQuarantine(source.getQuarantine());
        target.setQuarantineFrom(source.getQuarantineFrom());
        target.setQuarantineTo(source.getQuarantineTo());

        target.setCaseIdExternalSystem(source.getCaseIdExternalSystem());
        target.setCaseOrEventInformation(source.getCaseOrEventInformation());

        target.setQuarantineOrderMeans(source.getQuarantineOrderMeans());
        target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
    }

    public static ContactReferenceDto toReferenceDto(Contact ado) {
        if (ado == null) {
            return null;
        }
        ContactReferenceDto dto = new ContactReferenceDto(ado.getUuid());

        return dto;
    }
}
