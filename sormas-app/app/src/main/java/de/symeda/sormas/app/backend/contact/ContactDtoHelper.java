package de.symeda.sormas.app.backend.contact;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.symptoms.SymptomsDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;

/**
 * Created by Stefan Szczesny on 29.11.2016.
 */
public class ContactDtoHelper extends AdoDtoHelper<Contact, ContactDto> {

    private SymptomsDtoHelper symptomsDtoHelper;
    private RegionDtoHelper regionDtoHelper;
    private UserDtoHelper userDtoHelper;
    private CaseDtoHelper caseDtoHelper;

    public ContactDtoHelper() {
        symptomsDtoHelper = new SymptomsDtoHelper();
        regionDtoHelper = new RegionDtoHelper();
        caseDtoHelper = new CaseDtoHelper();
    }

    @Override
    public Contact create() {
        return new Contact();
    }

    @Override
    public ContactDto createDto() {
        return new ContactDto();
    }

    @Override
    public void fillInnerFromDto(Contact ado, ContactDto dto) {
        if (dto.getCaze() != null) {
            ado.setCaze(DatabaseHelper.getCaseDao().queryUuid(dto.getCaze().getUuid()));
        } else {
            ado.setCaze(null);
        }

        if (dto.getPerson() != null) {
            ado.setPerson(DatabaseHelper.getPersonDao().queryUuid(dto.getPerson().getUuid()));
        } else {
            ado.setPerson(null);
        }

        if (dto.getReportingUser() != null) {
            ado.setReportingUser(DatabaseHelper.getUserDao().queryUuid(dto.getReportingUser().getUuid()));
        } else {
            ado.setReportingUser(null);
        }

        ado.setReportDateTime(dto.getReportDateTime());
        ado.setLastContactDate(dto.getLastContactDate());
        ado.setContactProximity(dto.getContactProximity());
        ado.setContactClassification(dto.getContactClassification());
        ado.setRelationToCase(dto.getRelationToCase());
        ado.setFollowUpStatus(dto.getFollowUpStatus());
        ado.setFollowUpUntil(dto.getFollowUpUntil());

        if (dto.getContactOfficer() != null) {
            ado.setContactOfficer(DatabaseHelper.getUserDao().queryUuid(dto.getContactOfficer().getUuid()));
        } else {
            ado.setContactOfficer(null);
        }

        ado.setDescription(dto.getDescription());
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
        dto.setFollowUpUntil(ado.getFollowUpUntil());
        if (ado.getContactOfficer() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getContactOfficer().getId());
            dto.setContactOfficer(UserDtoHelper.toReferenceDto(user));
        } else {
            dto.setContactOfficer(null);
        }
        dto.setDescription(ado.getDescription());
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
