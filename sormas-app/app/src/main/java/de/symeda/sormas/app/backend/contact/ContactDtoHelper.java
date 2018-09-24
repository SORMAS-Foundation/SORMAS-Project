package de.symeda.sormas.app.backend.contact;

import java.util.List;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
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
    protected Call<List<ContactDto>> pullAllSince(long since) {
        return RetroProvider.getContactFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<ContactDto>> pullByUuids(List<String> uuids) {
        return RetroProvider.getContactFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<Integer> pushAll(List<ContactDto> contactDtos) {
        return RetroProvider.getContactFacade().pushAll(contactDtos);
    }

    @Override
    public void fillInnerFromDto(Contact target, ContactDto source) {
        target.setCaseUuid(source.getCaze() != null ? source.getCaze().getUuid() : null);
        target.setCaseDisease(source.getCaseDisease());
        target.setPerson(DatabaseHelper.getPersonDao().getByReferenceDto(source.getPerson()));

        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
        target.setReportDateTime(source.getReportDateTime());
        target.setContactOfficer(DatabaseHelper.getUserDao().getByReferenceDto(source.getContactOfficer()));

        target.setLastContactDate(source.getLastContactDate());
        target.setContactProximity(source.getContactProximity());
        target.setContactClassification(source.getContactClassification());
        target.setContactStatus(source.getContactStatus());
        target.setRelationToCase(source.getRelationToCase());
        target.setFollowUpStatus(source.getFollowUpStatus());
        target.setFollowUpComment(source.getFollowUpComment());
        target.setFollowUpUntil(source.getFollowUpUntil());

        target.setDescription(source.getDescription());

        target.setResultingCaseUuid(source.getResultingCase() != null ? source.getResultingCase().getUuid() : null);

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
    }

    @Override
    public void fillInnerFromAdo(ContactDto target, Contact source) {
        if (source.getPerson() != null) {
            Person person = DatabaseHelper.getPersonDao().queryForId(source.getPerson().getId());
            target.setPerson(PersonDtoHelper.toReferenceDto(person));
        } else {
            target.setPerson(null);
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

        target.setCaseDisease(source.getCaseDisease());

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
    }

    public static ContactReferenceDto toReferenceDto(Contact ado) {
        if (ado == null) {
            return null;
        }
        ContactReferenceDto dto = new ContactReferenceDto(ado.getUuid());

        return dto;
    }
}
