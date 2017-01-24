package de.symeda.sormas.app.backend.event;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;

public class EventDtoHelper extends AdoDtoHelper<Event, EventDto> {

    private LocationDtoHelper locationHelper;

    public EventDtoHelper() {
        locationHelper = new LocationDtoHelper();
    }

    @Override
    public Event create() {
        return new Event();
    }

    @Override
    public EventDto createDto() {
        return new EventDto();
    }

    @Override
    public void fillInnerFromDto(Event ado, EventDto dto) {

        ado.setEventType(dto.getEventType());
        ado.setEventStatus(dto.getEventStatus());
        ado.setEventDesc(dto.getEventDesc());
        ado.setEventDate(dto.getEventDate());
        ado.setReportDateTime(dto.getReportDateTime());

        if(dto.getReportingUser()!=null) {
            ado.setReportingUser(DatabaseHelper.getUserDao().queryUuid(dto.getReportingUser().getUuid()));
        }
        else {
            ado.setReportingUser(null);
        }

        if(dto.getEventLocation()!=null) {
            ado.setEventLocation(DatabaseHelper.getLocationDao().queryUuid(dto.getEventLocation().getUuid()));
        }
        else {
            ado.setEventLocation(null);
        }

        ado.setTypeOfPlace(dto.getTypeOfPlace());
        ado.setSrcFirstName(dto.getSrcFirstName());
        ado.setSrcLastName(dto.getSrcLastName());
        ado.setSrcTelNo(dto.getSrcTelNo());
        ado.setSrcEmail(dto.getSrcEmail());
        ado.setDisease(dto.getDisease());

        if (dto.getSurveillanceOfficer() != null) {
            ado.setSurveillanceOfficer(DatabaseHelper.getUserDao().queryUuid(dto.getSurveillanceOfficer().getUuid()));
        } else {
            ado.setSurveillanceOfficer(null);
        }

        ado.setTypeOfPlaceText(dto.getTypeOfPlaceText());
        
    }

    @Override
    public void fillInnerFromAdo(EventDto dto, Event ado) {

        dto.setEventType(ado.getEventType());
        dto.setEventStatus(ado.getEventStatus());
        dto.setEventDesc(ado.getEventDesc());
        dto.setEventDate(ado.getEventDate());
        dto.setReportDateTime(ado.getReportDateTime());

        if (ado.getReportingUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getReportingUser().getId());
            dto.setReportingUser(UserDtoHelper.toReferenceDto(user));
        } else {
            dto.setReportingUser(null);
        }

        if (ado.getEventLocation() != null) {
            Location location = DatabaseHelper.getLocationDao().queryForId(ado.getEventLocation().getId());
            dto.setEventLocation(locationHelper.adoToDto(location));
        } else {
            dto.setEventLocation(null);
        }

        dto.setTypeOfPlace(ado.getTypeOfPlace());
        dto.setSrcFirstName(ado.getSrcFirstName());
        dto.setSrcLastName(ado.getSrcLastName());
        dto.setSrcTelNo(ado.getSrcTelNo());
        dto.setSrcEmail(ado.getSrcEmail());
        dto.setDisease(ado.getDisease());

        if (ado.getSurveillanceOfficer() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(ado.getSurveillanceOfficer().getId());
            dto.setSurveillanceOfficer(UserDtoHelper.toReferenceDto(user));
        } else {
            dto.setSurveillanceOfficer(null);
        }

        dto.setTypeOfPlaceText(ado.getTypeOfPlaceText());
    }

    public static EventReferenceDto toReferenceDto(Event ado) {
        if (ado == null) {
            return null;
        }
        EventReferenceDto dto = new EventReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }
}
