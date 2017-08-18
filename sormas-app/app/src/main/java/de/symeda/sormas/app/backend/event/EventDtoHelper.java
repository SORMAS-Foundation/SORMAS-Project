package de.symeda.sormas.app.backend.event;

import java.util.List;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class EventDtoHelper extends AdoDtoHelper<Event, EventDto> {

    private LocationDtoHelper locationHelper;

    public EventDtoHelper() {
        locationHelper = new LocationDtoHelper();
    }

    @Override
    protected Class<Event> getAdoClass() {
        return Event.class;
    }

    @Override
    protected Class<EventDto> getDtoClass() {
        return EventDto.class;
    }

    @Override
    protected Call<List<EventDto>> pullAllSince(long since) {
        return RetroProvider.getEventFacade().pullAllSince(since);
    }

    @Override
    protected Call<Integer> pushAll(List<EventDto> eventDtos) {
        return RetroProvider.getEventFacade().pushAll(eventDtos);
    }

    @Override
    public void fillInnerFromDto(Event target, EventDto source) {

        target.setEventType(source.getEventType());
        target.setEventStatus(source.getEventStatus());
        target.setEventDesc(source.getEventDesc());
        target.setEventDate(source.getEventDate());
        target.setReportDateTime(source.getReportDateTime());
        target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
        target.setSurveillanceOfficer(DatabaseHelper.getUserDao().getByReferenceDto(source.getSurveillanceOfficer()));

        target.setEventLocation(locationHelper.fillOrCreateFromDto(target.getEventLocation(), source.getEventLocation()));
        target.setTypeOfPlace(source.getTypeOfPlace());
        target.setTypeOfPlaceText(source.getTypeOfPlaceText());
        target.setSrcFirstName(source.getSrcFirstName());
        target.setSrcLastName(source.getSrcLastName());
        target.setSrcTelNo(source.getSrcTelNo());
        target.setSrcEmail(source.getSrcEmail());
        target.setDisease(source.getDisease());

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
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

        dto.setReportLat(ado.getReportLat());
        dto.setReportLon(ado.getReportLon());
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
