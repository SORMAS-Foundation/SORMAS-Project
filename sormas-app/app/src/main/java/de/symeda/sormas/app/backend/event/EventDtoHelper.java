package de.symeda.sormas.app.backend.event;

import java.util.List;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
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
    protected Call<List<EventDto>> pullByUuids(List<String> uuids) {
        return RetroProvider.getEventFacade().pullByUuids(uuids);
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
        target.setDiseaseDetails(source.getDiseaseDetails());

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
    }

    @Override
    public void fillInnerFromAdo(EventDto target, Event source) {

        target.setEventType(source.getEventType());
        target.setEventStatus(source.getEventStatus());
        target.setEventDesc(source.getEventDesc());
        target.setEventDate(source.getEventDate());
        target.setReportDateTime(source.getReportDateTime());

        if (source.getReportingUser() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(source.getReportingUser().getId());
            target.setReportingUser(UserDtoHelper.toReferenceDto(user));
        } else {
            target.setReportingUser(null);
        }

        if (source.getEventLocation() != null) {
            Location location = DatabaseHelper.getLocationDao().queryForId(source.getEventLocation().getId());
            target.setEventLocation(locationHelper.adoToDto(location));
        } else {
            target.setEventLocation(null);
        }

        target.setTypeOfPlace(source.getTypeOfPlace());
        target.setSrcFirstName(source.getSrcFirstName());
        target.setSrcLastName(source.getSrcLastName());
        target.setSrcTelNo(source.getSrcTelNo());
        target.setSrcEmail(source.getSrcEmail());
        target.setDisease(source.getDisease());
        target.setDiseaseDetails(source.getDiseaseDetails());

        if (source.getSurveillanceOfficer() != null) {
            User user = DatabaseHelper.getUserDao().queryForId(source.getSurveillanceOfficer().getId());
            target.setSurveillanceOfficer(UserDtoHelper.toReferenceDto(user));
        } else {
            target.setSurveillanceOfficer(null);
        }

        target.setTypeOfPlaceText(source.getTypeOfPlaceText());

        target.setReportLat(source.getReportLat());
        target.setReportLon(source.getReportLon());
        target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
    }

    public static EventReferenceDto toReferenceDto(Event ado) {
        if (ado == null) {
            return null;
        }
        EventReferenceDto dto = new EventReferenceDto(ado.getUuid());

        return dto;
    }
}
