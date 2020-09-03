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

package de.symeda.sormas.app.backend.caze.porthealthinfo;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class PortHealthInfoDtoHelper extends AdoDtoHelper<PortHealthInfo, PortHealthInfoDto> {

	@Override
	protected Class<PortHealthInfo> getAdoClass() {
		return PortHealthInfo.class;
	}

	@Override
	protected Class<PortHealthInfoDto> getDtoClass() {
		return PortHealthInfoDto.class;
	}

	@Override
	protected Call<List<PortHealthInfoDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PortHealthInfoDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<PortHealthInfoDto> dtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(PortHealthInfo target, PortHealthInfoDto source) {
		target.setAirlineName(source.getAirlineName());
		target.setFlightNumber(source.getFlightNumber());
		target.setDepartureDateTime(source.getDepartureDateTime());
		target.setArrivalDateTime(source.getArrivalDateTime());
		target.setFreeSeating(source.getFreeSeating());
		target.setSeatNumber(source.getSeatNumber());
		target.setDepartureAirport(source.getDepartureAirport());
		target.setNumberOfTransitStops(source.getNumberOfTransitStops());
		target.setTransitStopDetails1(source.getTransitStopDetails1());
		target.setTransitStopDetails2(source.getTransitStopDetails2());
		target.setTransitStopDetails3(source.getTransitStopDetails3());
		target.setTransitStopDetails4(source.getTransitStopDetails4());
		target.setTransitStopDetails5(source.getTransitStopDetails5());
		target.setVesselName(source.getVesselName());
		target.setVesselDetails(source.getVesselDetails());
		target.setPortOfDeparture(source.getPortOfDeparture());
		target.setLastPortOfCall(source.getLastPortOfCall());
		target.setConveyanceType(source.getConveyanceType());
		target.setConveyanceTypeDetails(source.getConveyanceTypeDetails());
		target.setDepartureLocation(source.getDepartureLocation());
		target.setFinalDestination(source.getFinalDestination());
		target.setDetails(source.getDetails());
	}

	@Override
	public void fillInnerFromAdo(PortHealthInfoDto target, PortHealthInfo source) {
		target.setAirlineName(source.getAirlineName());
		target.setFlightNumber(source.getFlightNumber());
		target.setDepartureDateTime(source.getDepartureDateTime());
		target.setArrivalDateTime(source.getArrivalDateTime());
		target.setFreeSeating(source.getFreeSeating());
		target.setSeatNumber(source.getSeatNumber());
		target.setDepartureAirport(source.getDepartureAirport());
		target.setNumberOfTransitStops(source.getNumberOfTransitStops());
		target.setTransitStopDetails1(source.getTransitStopDetails1());
		target.setTransitStopDetails2(source.getTransitStopDetails2());
		target.setTransitStopDetails3(source.getTransitStopDetails3());
		target.setTransitStopDetails4(source.getTransitStopDetails4());
		target.setTransitStopDetails5(source.getTransitStopDetails5());
		target.setVesselName(source.getVesselName());
		target.setVesselDetails(source.getVesselDetails());
		target.setPortOfDeparture(source.getPortOfDeparture());
		target.setLastPortOfCall(source.getLastPortOfCall());
		target.setConveyanceType(source.getConveyanceType());
		target.setConveyanceTypeDetails(source.getConveyanceTypeDetails());
		target.setDepartureLocation(source.getDepartureLocation());
		target.setFinalDestination(source.getFinalDestination());
		target.setDetails(source.getDetails());
	}
}
