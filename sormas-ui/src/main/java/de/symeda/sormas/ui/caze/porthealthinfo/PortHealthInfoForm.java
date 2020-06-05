package de.symeda.sormas.ui.caze.porthealthinfo;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.porthealthinfo.ConveyanceType;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;

public class PortHealthInfoForm extends AbstractEditForm<PortHealthInfoDto> {
	
	private static final long serialVersionUID = 1L;
	
	private static final String POINT_OF_ENTRY_LAYOUT_HTML = 
			h3(I18nProperties.getString(Strings.headingPointOfEntryInformation)) +
			fluidRowLocs(PointOfEntryDto.POINT_OF_ENTRY_TYPE, CaseDataDto.POINT_OF_ENTRY);
	
	private static final String HTML_LAYOUT_AIRPORT =
			POINT_OF_ENTRY_LAYOUT_HTML +
			fluidRowLocs(PortHealthInfoDto.AIRLINE_NAME, PortHealthInfoDto.FLIGHT_NUMBER) +
			fluidRowLocs(PortHealthInfoDto.DEPARTURE_AIRPORT, PortHealthInfoDto.DEPARTURE_DATE_TIME) +
			fluidRowLocs(PortHealthInfoDto.FREE_SEATING, PortHealthInfoDto.SEAT_NUMBER) +
			fluidRowLocs(PortHealthInfoDto.ARRIVAL_DATE_TIME, PortHealthInfoDto.NUMBER_OF_TRANSIT_STOPS) +
			loc(PortHealthInfoDto.TRANSIT_STOP_DETAILS_1) +
			loc(PortHealthInfoDto.TRANSIT_STOP_DETAILS_2) +
			loc(PortHealthInfoDto.TRANSIT_STOP_DETAILS_3) +
			loc(PortHealthInfoDto.TRANSIT_STOP_DETAILS_4) +
			loc(PortHealthInfoDto.TRANSIT_STOP_DETAILS_5);

	private static final String HTML_LAYOUT_SEAPORT =
			POINT_OF_ENTRY_LAYOUT_HTML +
			fluidRowLocs(PortHealthInfoDto.VESSEL_NAME, PortHealthInfoDto.VESSEL_DETAILS) +
			fluidRowLocs(PortHealthInfoDto.PORT_OF_DEPARTURE, PortHealthInfoDto.LAST_PORT_OF_CALL) +
			fluidRowLocs(PortHealthInfoDto.DEPARTURE_DATE_TIME, PortHealthInfoDto.ARRIVAL_DATE_TIME);

	private static final String HTML_LAYOUT_GROUND_CROSSING =
			POINT_OF_ENTRY_LAYOUT_HTML +
			fluidRowLocs(PortHealthInfoDto.CONVEYANCE_TYPE, PortHealthInfoDto.CONVEYANCE_TYPE_DETAILS) +
			fluidRowLocs(PortHealthInfoDto.DEPARTURE_LOCATION, PortHealthInfoDto.FINAL_DESTINATION);

	private static final String HTML_LAYOUT_OTHER =
			POINT_OF_ENTRY_LAYOUT_HTML +
			loc(PortHealthInfoDto.DETAILS);

	private PointOfEntryDto pointOfEntry;
	private String pointOfEntryDetails;

	public PortHealthInfoForm(PointOfEntryDto pointOfEntry, String pointOfEntryDetails) {
		super(PortHealthInfoDto.class, PortHealthInfoDto.I18N_PREFIX);
		this.pointOfEntry = pointOfEntry;
		this.pointOfEntryDetails = pointOfEntryDetails;
		
		addFields();
	}

	@Override
	protected void addFields() {
		if (pointOfEntry == null) {
			return;
		}

		TextField tfPointOfEntryType = addCustomField(PointOfEntryDto.POINT_OF_ENTRY_TYPE, String.class, TextField.class);
		tfPointOfEntryType.setValue(pointOfEntry.getPointOfEntryType().toString());
		tfPointOfEntryType.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.POINT_OF_ENTRY_TYPE));
		tfPointOfEntryType.setReadOnly(true);
		TextField tfPointOfEntry = addCustomField(CaseDataDto.POINT_OF_ENTRY, PointOfEntryReferenceDto.class, TextField.class);
		tfPointOfEntry.setValue(InfrastructureHelper.buildPointOfEntryString(pointOfEntry.getUuid(), pointOfEntry.getName(), pointOfEntryDetails));
		tfPointOfEntry.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.POINT_OF_ENTRY));
		tfPointOfEntry.setReadOnly(true);

		switch (pointOfEntry.getPointOfEntryType()) {
		case AIRPORT:
			addAirportFields();
			break;
		case SEAPORT:
			addSeaportFields();
			break;
		case GROUND_CROSSING:
			addGroundCrossingFields();
			break;
		case OTHER:
			addOtherFields();
			break;
		}
	}

	private void addAirportFields() {
		addFields(PortHealthInfoDto.AIRLINE_NAME, PortHealthInfoDto.FLIGHT_NUMBER, PortHealthInfoDto.DEPARTURE_AIRPORT, PortHealthInfoDto.SEAT_NUMBER,
				PortHealthInfoDto.TRANSIT_STOP_DETAILS_1, PortHealthInfoDto.TRANSIT_STOP_DETAILS_2, PortHealthInfoDto.TRANSIT_STOP_DETAILS_3,
				PortHealthInfoDto.TRANSIT_STOP_DETAILS_4, PortHealthInfoDto.TRANSIT_STOP_DETAILS_5);
		DateTimeField dfDepartureDateTime = addField(PortHealthInfoDto.DEPARTURE_DATE_TIME, DateTimeField.class);
		DateTimeField dfArrivalDateTime = addField(PortHealthInfoDto.ARRIVAL_DATE_TIME, DateTimeField.class);
		addField(PortHealthInfoDto.FREE_SEATING, OptionGroup.class);
		ComboBox cbNumberOfTransitStops = addField(PortHealthInfoDto.NUMBER_OF_TRANSIT_STOPS, ComboBox.class);
		
		cbNumberOfTransitStops.addItems(DataHelper.buildIntegerList(0, 5));
		
		// Visibility
		FieldHelper.setVisibleWhen(getFieldGroup(), PortHealthInfoDto.TRANSIT_STOP_DETAILS_1, PortHealthInfoDto.NUMBER_OF_TRANSIT_STOPS, Arrays.asList(1, 2, 3, 4, 5), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), PortHealthInfoDto.TRANSIT_STOP_DETAILS_2, PortHealthInfoDto.NUMBER_OF_TRANSIT_STOPS, Arrays.asList(2, 3, 4, 5), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), PortHealthInfoDto.TRANSIT_STOP_DETAILS_3, PortHealthInfoDto.NUMBER_OF_TRANSIT_STOPS, Arrays.asList(3, 4, 5), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), PortHealthInfoDto.TRANSIT_STOP_DETAILS_4, PortHealthInfoDto.NUMBER_OF_TRANSIT_STOPS, Arrays.asList(4, 5), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), PortHealthInfoDto.TRANSIT_STOP_DETAILS_5, PortHealthInfoDto.NUMBER_OF_TRANSIT_STOPS, Arrays.asList(5), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), PortHealthInfoDto.SEAT_NUMBER, PortHealthInfoDto.FREE_SEATING, Arrays.asList(YesNoUnknown.NO), true);
		
		// Validations
		dfDepartureDateTime.addValidator(new DateComparisonValidator(dfDepartureDateTime, dfArrivalDateTime, true, false,
				I18nProperties.getValidationError(Validations.beforeDate, dfDepartureDateTime.getCaption(), dfArrivalDateTime.getCaption())));
		dfArrivalDateTime.addValidator(new DateComparisonValidator(dfArrivalDateTime, dfDepartureDateTime, false, false, 
				I18nProperties.getValidationError(Validations.afterDate, dfArrivalDateTime.getCaption(), dfDepartureDateTime.getCaption())));
	}

	private void addSeaportFields() {
		addFields(PortHealthInfoDto.VESSEL_NAME, PortHealthInfoDto.VESSEL_DETAILS, PortHealthInfoDto.PORT_OF_DEPARTURE, PortHealthInfoDto.LAST_PORT_OF_CALL);
		DateTimeField dfDepartureDateTime = addField(PortHealthInfoDto.DEPARTURE_DATE_TIME, DateTimeField.class);
		DateTimeField dfArrivalDateTime = addField(PortHealthInfoDto.ARRIVAL_DATE_TIME, DateTimeField.class);

		// Validations
		dfDepartureDateTime.addValidator(new DateComparisonValidator(dfDepartureDateTime, dfArrivalDateTime, true, false,
				I18nProperties.getValidationError(Validations.beforeDate, dfDepartureDateTime.getCaption(), dfArrivalDateTime.getCaption())));
		dfArrivalDateTime.addValidator(new DateComparisonValidator(dfArrivalDateTime, dfDepartureDateTime, false, false, 
				I18nProperties.getValidationError(Validations.afterDate, dfArrivalDateTime.getCaption(), dfDepartureDateTime.getCaption())));
	}

	private void addGroundCrossingFields() {
		addFields(PortHealthInfoDto.CONVEYANCE_TYPE_DETAILS, PortHealthInfoDto.DEPARTURE_LOCATION, PortHealthInfoDto.FINAL_DESTINATION);
		addField(PortHealthInfoDto.CONVEYANCE_TYPE, ComboBox.class);

		// Visibility
		FieldHelper.setVisibleWhen(getFieldGroup(), PortHealthInfoDto.CONVEYANCE_TYPE_DETAILS, PortHealthInfoDto.CONVEYANCE_TYPE, Arrays.asList(ConveyanceType.OTHER), true);
	}

	private void addOtherFields() {
		TextArea taDetails = addField(PortHealthInfoDto.DETAILS, TextArea.class);
		taDetails.setRows(5);
	}

	@Override
	protected String createHtmlLayout() {
		switch (pointOfEntry.getPointOfEntryType()) {
		case AIRPORT:
			return HTML_LAYOUT_AIRPORT;
		case SEAPORT:
			return HTML_LAYOUT_SEAPORT;
		case GROUND_CROSSING:
			return HTML_LAYOUT_GROUND_CROSSING;
		case OTHER:
			return HTML_LAYOUT_OTHER;
		default:
			throw new RuntimeException("Point of entry type is not initialized");
		}
	}

}
