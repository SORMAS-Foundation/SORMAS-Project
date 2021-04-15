/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.event;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.backend.sormastosormas.SormasToSormasOriginInfoDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class EventDtoHelper extends AdoDtoHelper<Event, EventDto> {

	private LocationDtoHelper locationHelper;

	private SormasToSormasOriginInfoDtoHelper sormasToSormasOriginInfoDtoHelper = new SormasToSormasOriginInfoDtoHelper();

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
	protected Call<List<EventDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getEventFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<EventDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getEventFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<EventDto> eventDtos) throws NoConnectionException {
		return RetroProvider.getEventFacade().pushAll(eventDtos);
	}

	@Override
	public void fillInnerFromDto(Event target, EventDto source) {

		target.setEventStatus(source.getEventStatus());
		target.setRiskLevel(source.getRiskLevel());
		target.setEventInvestigationStatus(source.getEventInvestigationStatus());
		target.setEventInvestigationStartDate(source.getEventInvestigationStartDate());
		target.setEventInvestigationEndDate(source.getEventInvestigationEndDate());
		target.setExternalId(source.getExternalId());
		target.setExternalToken(source.getExternalToken());
		target.setEventTitle(source.getEventTitle());
		target.setEventDesc(source.getEventDesc());
		target.setNosocomial(source.getNosocomial());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
		target.setEvolutionDate(source.getEvolutionDate());
		target.setEvolutionComment(source.getEvolutionComment());
		target.setResponsibleUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getResponsibleUser()));

		target.setEventLocation(locationHelper.fillOrCreateFromDto(target.getEventLocation(), source.getEventLocation()));
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setTypeOfPlaceText(source.getTypeOfPlaceText());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setTravelDate(source.getTravelDate());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setWorkEnvironment(source.getWorkEnvironment());

		target.setSrcType(source.getSrcType());
		target.setSrcInstitutionalPartnerType(source.getSrcInstitutionalPartnerType());
		target.setSrcInstitutionalPartnerTypeDetails(source.getSrcInstitutionalPartnerTypeDetails());
		target.setSrcFirstName(source.getSrcFirstName());
		target.setSrcLastName(source.getSrcLastName());
		target.setSrcTelNo(source.getSrcTelNo());
		target.setSrcEmail(source.getSrcEmail());
		target.setSrcMediaWebsite(source.getSrcMediaWebsite());
		target.setSrcMediaName(source.getSrcMediaName());
		target.setSrcMediaDetails(source.getSrcMediaDetails());

		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setTransregionalOutbreak(source.getTransregionalOutbreak());
		target.setDiseaseTransmissionMode(source.getDiseaseTransmissionMode());
		target.setSuperordinateEventUuid(source.getSuperordinateEvent() != null ? source.getSuperordinateEvent().getUuid() : null);

		target.setSormasToSormasOriginInfo(
			sormasToSormasOriginInfoDtoHelper.fillOrCreateFromDto(target.getSormasToSormasOriginInfo(), source.getSormasToSormasOriginInfo()));
		target.setOwnershipHandedOver(source.isOwnershipHandedOver());

		target.setPseudonymized(source.isPseudonymized());
		target.setEventManagementStatus(source.getEventManagementStatus());

		target.setInfectionPathCertainty(source.getInfectionPathCertainty());
		target.setHumanTransmissionMode(source.getHumanTransmissionMode());
		target.setParenteralTransmissionMode(source.getParenteralTransmissionMode());
		target.setMedicallyAssociatedTransmissionMode(source.getMedicallyAssociatedTransmissionMode());

		target.setInternalId(source.getInternalId());
	}

	@Override
	public void fillInnerFromAdo(EventDto target, Event source) {

		target.setEventStatus(source.getEventStatus());
		target.setRiskLevel(source.getRiskLevel());
		target.setEventInvestigationStatus(source.getEventInvestigationStatus());
		target.setEventInvestigationStartDate(source.getEventInvestigationStartDate());
		target.setEventInvestigationEndDate(source.getEventInvestigationEndDate());
		target.setExternalId(source.getExternalId());
		target.setExternalToken(source.getExternalToken());
		target.setEventTitle(source.getEventTitle());
		target.setEventDesc(source.getEventDesc());
		target.setNosocomial(source.getNosocomial());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setEvolutionDate(source.getEvolutionDate());
		target.setEvolutionComment(source.getEvolutionComment());

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

		if (source.getSuperordinateEventUuid() != null) {
			target.setSuperordinateEvent(new EventReferenceDto(source.getSuperordinateEventUuid()));
		} else {
			target.setSuperordinateEvent(null);
		}

		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setTravelDate(source.getTravelDate());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setWorkEnvironment(source.getWorkEnvironment());

		target.setSrcType(source.getSrcType());
		target.setSrcInstitutionalPartnerType(source.getSrcInstitutionalPartnerType());
		target.setSrcInstitutionalPartnerTypeDetails(source.getSrcInstitutionalPartnerTypeDetails());
		target.setSrcFirstName(source.getSrcFirstName());
		target.setSrcLastName(source.getSrcLastName());
		target.setSrcTelNo(source.getSrcTelNo());
		target.setSrcEmail(source.getSrcEmail());
		target.setSrcMediaWebsite(source.getSrcMediaWebsite());
		target.setSrcMediaName(source.getSrcMediaName());
		target.setSrcMediaDetails(source.getSrcMediaDetails());

		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());

		if (source.getResponsibleUser() != null) {
			User user = DatabaseHelper.getUserDao().queryForId(source.getResponsibleUser().getId());
			target.setResponsibleUser(UserDtoHelper.toReferenceDto(user));
		} else {
			target.setResponsibleUser(null);
		}

		target.setTypeOfPlaceText(source.getTypeOfPlaceText());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setTransregionalOutbreak(source.getTransregionalOutbreak());
		target.setDiseaseTransmissionMode(source.getDiseaseTransmissionMode());

		if (source.getSormasToSormasOriginInfo() != null) {
			target.setSormasToSormasOriginInfo(sormasToSormasOriginInfoDtoHelper.adoToDto(source.getSormasToSormasOriginInfo()));
		}
		target.setOwnershipHandedOver(source.isOwnershipHandedOver());

		target.setPseudonymized(source.isPseudonymized());

		target.setEventManagementStatus(source.getEventManagementStatus());

		target.setInfectionPathCertainty(source.getInfectionPathCertainty());
		target.setHumanTransmissionMode(source.getHumanTransmissionMode());
		target.setParenteralTransmissionMode(source.getParenteralTransmissionMode());
		target.setMedicallyAssociatedTransmissionMode(source.getMedicallyAssociatedTransmissionMode());

		target.setInternalId(source.getInternalId());
	}

	public static EventReferenceDto toReferenceDto(Event ado) {
		if (ado == null) {
			return null;
		}
		EventReferenceDto dto = new EventReferenceDto(ado.getUuid());

		return dto;
	}
}
