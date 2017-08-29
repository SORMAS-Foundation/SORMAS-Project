package de.symeda.sormas.app.backend.contact;

import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
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
    protected Call<Integer> pushAll(List<ContactDto> contactDtos) {
        return RetroProvider.getContactFacade().pushAll(contactDtos);
    }

    @Override
    public void fillInnerFromDto(Contact target, ContactDto source) {

        target.setCaze(DatabaseHelper.getCaseDao().getByReferenceDto(source.getCaze()));
        target.setPerson(DatabaseHelper.getPersonDao().getByReferenceDto(source.getPerson()));

        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
        target.setReportDateTime(source.getReportDateTime());
        target.setContactOfficer(DatabaseHelper.getUserDao().getByReferenceDto(source.getContactOfficer()));

        target.setLastContactDate(source.getLastContactDate());
        target.setContactProximity(source.getContactProximity());
        target.setContactClassification(source.getContactClassification());
        target.setRelationToCase(source.getRelationToCase());
        target.setFollowUpStatus(source.getFollowUpStatus());
        target.setFollowUpComment(source.getFollowUpComment());
        target.setFollowUpUntil(source.getFollowUpUntil());

        target.setDescription(source.getDescription());

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
    }

    @Override
    public void fillInnerFromAdo(ContactDto dto, Contact ado) {

        if (ado.getPerson() != null) {
            Person person = DatabaseHelper.getPersonDao().queryForId(ado.getPerson().getId());
            dto.setPerson(PersonDtoHelper.toReferenceDto(person));
        } else {
            dto.setPerson(null);
        }
        if (ado.getCaze() != null) {
            Case caze = DatabaseHelper.getCaseDao().queryForId(ado.getCaze().getId());
            dto.setCaze(CaseDtoHelper.toReferenceDto(caze));
        } else {
            dto.setCaze(null);
        }

        if (ado.getReportingUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getReportingUser().getId());
            dto.setReportingUser(UserDtoHelper.toReferenceDto(user));
        } else {
            dto.setReportingUser(null);
        }
        dto.setReportDateTime(ado.getReportDateTime());

        dto.setLastContactDate(ado.getLastContactDate());
        dto.setContactProximity(ado.getContactProximity());
        dto.setContactClassification(ado.getContactClassification());
        dto.setRelationToCase(ado.getRelationToCase());
        dto.setFollowUpStatus(ado.getFollowUpStatus());
        dto.setFollowUpComment(ado.getFollowUpComment());
        dto.setFollowUpUntil(ado.getFollowUpUntil());
        if (ado.getContactOfficer() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getContactOfficer().getId());
            dto.setContactOfficer(UserDtoHelper.toReferenceDto(user));
        } else {
            dto.setContactOfficer(null);
        }
        dto.setDescription(ado.getDescription());

        dto.setReportLat(ado.getReportLat());
        dto.setReportLon(ado.getReportLon());
    }

    public static ContactReferenceDto toReferenceDto(Contact ado) {
        if (ado == null) {
            return null;
        }
        ContactReferenceDto dto = new ContactReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }
}
