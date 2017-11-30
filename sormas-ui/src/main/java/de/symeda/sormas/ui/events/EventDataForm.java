package de.symeda.sormas.ui.events;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class EventDataForm extends AbstractEditForm<EventDto> {
	
	private static final String STATUS_CHANGE = "statusChange";
	
	private static final String HTML_LAYOUT = 
			LayoutUtil.h3(CssStyles.VSPACE_3, "Event data") +
			LayoutUtil.divCss(CssStyles.VSPACE_2,
					LayoutUtil.fluidRowCss(CssStyles.VSPACE_4,
							LayoutUtil.fluidColumn(12, 0,
									LayoutUtil.fluidRowLocs(EventDto.UUID, EventDto.EVENT_TYPE)
							)
					) +
					LayoutUtil.fluidRowCss(CssStyles.VSPACE_4,
							LayoutUtil.fluidColumn(12, 0,
									LayoutUtil.fluidRowLocs(EventDto.DISEASE, EventDto.DISEASE_DETAILS)
							)
					) +
					LayoutUtil.fluidRowCss(CssStyles.VSPACE_4,
							LayoutUtil.fluidColumnCss(null, 4, 0,
									LayoutUtil.fluidRowLocs(EventDto.EVENT_DATE)),
							LayoutUtil.fluidColumnCss(null, 8, 0,
									LayoutUtil.fluidRowLocs(EventDto.EVENT_STATUS))
					) +
					LayoutUtil.fluidRowCss(CssStyles.VSPACE_4,
							LayoutUtil.fluidColumn(12, 0, 
									LayoutUtil.fluidRowLocs(EventDto.EVENT_DESC))
					)
			) +
			LayoutUtil.h3(CssStyles.VSPACE_3, "Source of information") +
			LayoutUtil.divCss(CssStyles.VSPACE_2,
					LayoutUtil.fluidRowCss(CssStyles.VSPACE_4, 
							LayoutUtil.fluidColumn(12, 0, 
									LayoutUtil.fluidRowLocs(EventDto.SRC_FIRST_NAME, EventDto.SRC_LAST_NAME) +
									LayoutUtil.fluidRowLocs(EventDto.SRC_TEL_NO, EventDto.SRC_EMAIL)
							)
					)
					
			) +
			LayoutUtil.h3(CssStyles.VSPACE_3, "Location") +
			LayoutUtil.divCss(CssStyles.VSPACE_2,
					LayoutUtil.fluidRowCss(CssStyles.VSPACE_4, 
							LayoutUtil.fluidColumn(8, 0, 
									LayoutUtil.fluidRowLocs(EventDto.EVENT_LOCATION)
							),
							LayoutUtil.fluidColumn(4, 0,
									LayoutUtil.fluidRowLocs(EventDto.TYPE_OF_PLACE) +
									LayoutUtil.fluidRowLocs(EventDto.TYPE_OF_PLACE_TEXT)
							)
					)
			) +
			LayoutUtil.fluidRowCss(CssStyles.VSPACE_4,
					LayoutUtil.fluidColumn(12,  0,
							LayoutUtil.fluidRowLocs(EventDto.SURVEILLANCE_OFFICER, EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER)
					)
			);
	
	private final VerticalLayout statusChangeLayout;
	
	public EventDataForm() {
		super(EventDto.class, EventDto.I18N_PREFIX);
		statusChangeLayout = new VerticalLayout();
		statusChangeLayout.setSpacing(false);
		statusChangeLayout.setMargin(false);
		getContent().addComponent(statusChangeLayout, STATUS_CHANGE);
	}

	@Override
	protected void addFields() {
		addField(EventDto.UUID, TextField.class);
		addField(EventDto.EVENT_TYPE, OptionGroup.class);
		addField(EventDto.DISEASE, ComboBox.class).setNullSelectionAllowed(true);
		addField(EventDto.DISEASE_DETAILS, TextField.class);
		DateField eventDate = addField(EventDto.EVENT_DATE, DateField.class);
		addField(EventDto.EVENT_STATUS, OptionGroup.class);
		addField(EventDto.EVENT_DESC, TextArea.class).setRows(2);
		addField(EventDto.EVENT_LOCATION, LocationEditForm.class).setCaption(null);

		LocationEditForm locationForm = (LocationEditForm) getFieldGroup().getField(EventDto.EVENT_LOCATION);
		ComboBox districtField = (ComboBox) locationForm.getFieldGroup().getField(LocationDto.DISTRICT);
		ComboBox surveillanceOfficerField = addField(EventDto.SURVEILLANCE_OFFICER, ComboBox.class);
		surveillanceOfficerField.setNullSelectionAllowed(true);
		
		ComboBox typeOfPlace = addField(EventDto.TYPE_OF_PLACE, ComboBox.class);
		typeOfPlace.setNullSelectionAllowed(true);
		addField(EventDto.TYPE_OF_PLACE_TEXT, TextField.class);		
		addField(EventDto.REPORT_DATE_TIME, DateTimeField.class);
		addField(EventDto.REPORTING_USER, ComboBox.class);
		TextField srcFirstName = addField(EventDto.SRC_FIRST_NAME, TextField.class);
		TextField srcLastName = addField(EventDto.SRC_LAST_NAME, TextField.class);
		TextField srcTelNo = addField(EventDto.SRC_TEL_NO, TextField.class);
		addField(EventDto.SRC_EMAIL, TextField.class);
		
		setReadOnly(true, EventDto.UUID, EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), EventDto.TYPE_OF_PLACE_TEXT, EventDto.TYPE_OF_PLACE, Arrays.asList(TypeOfPlace.OTHER), true);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(EventDto.DISEASE_DETAILS), EventDto.DISEASE, Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), EventDto.DISEASE, Arrays.asList(EventDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		
		setRequired(true, EventDto.EVENT_TYPE, EventDto.EVENT_STATUS, EventDto.UUID, EventDto.EVENT_DESC,
				EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER);
		setTypeOfPlaceTextRequirement();
		locationForm.setFieldsRequirement(true, LocationDto.REGION, LocationDto.DISTRICT);
		
		districtField.addValueChangeListener(e -> {
			List<UserReferenceDto> assignableSurveillanceOfficers = FacadeProvider.getUserFacade().getAssignableUsersByDistrict((DistrictReferenceDto) districtField.getValue(), false, UserRole.SURVEILLANCE_OFFICER);
			surveillanceOfficerField.removeAllItems();
			surveillanceOfficerField.select(0);
			surveillanceOfficerField.addItems(assignableSurveillanceOfficers);
		});
		
		FieldHelper.makeFieldSoftRequired(eventDate, typeOfPlace, surveillanceOfficerField);
		FieldHelper.makeTextFieldSoftRequired(srcFirstName, srcLastName, srcTelNo);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
	@SuppressWarnings("rawtypes")
	public void setTypeOfPlaceTextRequirement() {
		FieldGroup fieldGroup = getFieldGroup();
		ComboBox typeOfPlaceField = (ComboBox) fieldGroup.getField(EventDto.TYPE_OF_PLACE);
		TextField typeOfPlaceTextField = (TextField) fieldGroup.getField(EventDto.TYPE_OF_PLACE_TEXT);
		((AbstractField) typeOfPlaceField).setImmediate(true);
		
		// initialize
		{
			typeOfPlaceTextField.setRequired(typeOfPlaceField.getValue() == TypeOfPlace.OTHER);
		}
		
		typeOfPlaceField.addValueChangeListener(event -> {
			typeOfPlaceTextField.setRequired(typeOfPlaceField.getValue() == TypeOfPlace.OTHER);
		});
	}
	
}
