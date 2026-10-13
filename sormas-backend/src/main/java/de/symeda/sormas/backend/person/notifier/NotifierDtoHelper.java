package de.symeda.sormas.backend.person.notifier;

import java.util.Date;

import de.symeda.sormas.api.person.notifier.NotifierDto;
import de.symeda.sormas.api.person.notifier.NotifierReferenceDto;
import de.symeda.sormas.backend.util.DtoHelper;

public class NotifierDtoHelper {

    /**
     * Converts a Notifier entity to a NotifierReferenceDto with an optional version date.
     *
     * @param source
     *            The Notifier entity to convert.
     * @param versionDate
     *            The version date to set in the reference DTO. If null, the entity's change date is used.
     * @return The corresponding NotifierReferenceDto, or null if the source is null.
     */
    public static NotifierReferenceDto toVersionReferenceDto(Notifier source, Date versionDate) {

        if (source == null) {
            return null;
        }
        NotifierReferenceDto target = new NotifierReferenceDto(source.getUuid());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        if (versionDate != null) {
            target.setVersionDate(versionDate);
        } else {
            target.setVersionDate(source.getChangeDate());
        }
        return target;
    }

    /**
     * Converts a Notifier entity to a NotifierReferenceDto.
     *
     * @param source
     *            The Notifier entity to convert.
     * @return The corresponding NotifierReferenceDto, or null if the source is null.
     */
    public static NotifierReferenceDto toReferenceDto(Notifier source) {

        if (source == null) {
            return null;
        }
        return toVersionReferenceDto(source, null);
    }

    /**
     * Converts a Notifier entity to a NotifierDto.
     *
     * @param source
     *            The Notifier entity to convert.
     * @return The corresponding NotifierDto, or null if the source is null.
     */
    public static NotifierDto toDto(Notifier source) {

        if (source == null) {
            return null;
        }

        return DtoHelper.createAndFillDto(NotifierDto::new, source, target -> {
            target.setRegistrationNumber(source.getRegistrationNumber());
            target.setFirstName(source.getFirstName());
            target.setLastName(source.getLastName());
            target.setAddress(source.getAddress());
            target.setPhone(source.getPhone());
            target.setEmail(source.getEmail());
        });
    }
}
