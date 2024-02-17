package de.symeda.sormas.backend.caze.porthealthinfo;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoFacade;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "PortHealthInfoFacade")
public class PortHealthInfoFacadeEjb implements PortHealthInfoFacade {

	@EJB
	private PortHealthInfoService portHealthInfoService;
	@EJB
	private UserService userService;

	public static PortHealthInfoDto toDto(PortHealthInfo source) {
		if (source == null) {
			return null;
		}

		PortHealthInfoDto target = new PortHealthInfoDto();
		DtoHelper.fillDto(target, source);

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

		return target;
	}

	public PortHealthInfo fillOrBuildEntity(@NotNull PortHealthInfoDto source, PortHealthInfo target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, PortHealthInfo::new, checkChangeDate);

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

		return target;
	}

	@Override
	public PortHealthInfoDto getByCaseUuid(String caseUuid) {

		return toPseudonymizedDto(portHealthInfoService.getByCaseUuid(caseUuid));
	}

	private PortHealthInfoDto toPseudonymizedDto(PortHealthInfo portHealthInfo) {
		Pseudonymizer<PortHealthInfoDto> pseudonymizer = Pseudonymizer.getDefaultWithPlaceHolder(userService);
		PortHealthInfoDto portHealthInfoDto = toDto(portHealthInfo);
		pseudonymizer.pseudonymizeDto(PortHealthInfoDto.class, portHealthInfoDto, portHealthInfoService.inJurisdictionOrOwned(portHealthInfo), null);
		return portHealthInfoDto;
	}

	@LocalBean
	@Stateless
	public static class PortHealthInfoFacadeEjbLocal extends PortHealthInfoFacadeEjb {

	}
}
