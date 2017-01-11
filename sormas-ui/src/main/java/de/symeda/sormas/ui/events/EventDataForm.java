package de.symeda.sormas.ui.events;

import java.util.Arrays;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.location.LocationForm;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class EventDataForm extends AbstractEditForm<EventDto> {
	
	private static final String STATUS_CHANGE = "statusChange";
	
	private static final String HTML_LAYOUT = 
			LayoutUtil.h3(CssStyles.VSPACE3, "Event data") +
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowCss(CssStyles.VSPACE4,
							LayoutUtil.fluidColumn(12, 0,
									LayoutUtil.fluidRowLocs(EventDto.UUID, EventDto.EVENT_TYPE, EventDto.DISEASE)
							)
					) +
					LayoutUtil.fluidRowCss(CssStyles.VSPACE4,
							LayoutUtil.fluidColumnCss(null, 4, 0,
									LayoutUtil.fluidRowLocs(EventDto.EVENT_DATE)),
							LayoutUtil.fluidColumnCss(null, 8, 0,
									LayoutUtil.fluidRowLocs(EventDto.EVENT_STATUS))
					) +
					LayoutUtil.fluidRowCss(CssStyles.VSPACE4,
							LayoutUtil.fluidColumn(12, 0,
									LayoutUtil.fluidRowLocs(EventDto.TYPE_OF_PLACE, EventDto.TYPE_OF_PLACE_TEXT)
							)
					) +
					LayoutUtil.fluidRowCss(CssStyles.VSPACE4,
							LayoutUtil.fluidColumn(12, 0, 
									LayoutUtil.fluidRowLocs(EventDto.EVENT_DESC))
					) +
					LayoutUtil.fluidRowCss(CssStyles.VSPACE4,
							LayoutUtil.fluidColumn(12,  0,
									LayoutUtil.fluidRowLocs(EventDto.SURVEILLANCE_OFFICER, EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER)
							)
					)
			) +
			LayoutUtil.h3(CssStyles.VSPACE3, "Source of information") +
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowCss(CssStyles.VSPACE4, 
							LayoutUtil.fluidColumn(12, 0, 
									LayoutUtil.fluidRowLocs(EventDto.SRC_FIRST_NAME, EventDto.SRC_LAST_NAME) +
									LayoutUtil.fluidRowLocs(EventDto.SRC_TEL_NO, EventDto.SRC_EMAIL)
							)
					)
					
			) +
			LayoutUtil.h3(CssStyles.VSPACE3, "Location") +
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowCss(CssStyles.VSPACE4, 
							LayoutUtil.fluidColumn(12, 0, 
									LayoutUtil.fluidRowLocs(EventDto.EVENT_LOCATION)
							)
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
		addField(EventDto.DISEASE, ComboBox.class);
		addField(EventDto.EVENT_DATE, DateField.class);
		addField(EventDto.EVENT_STATUS, OptionGroup.class);
		addField(EventDto.EVENT_DESC, TextArea.class).setRows(2);
		
		UserReferenceDto currentUser = LoginHelper.getCurrentUserAsReference();
		addField(EventDto.SURVEILLANCE_OFFICER, ComboBox.class).addItems(FacadeProvider.getUserFacade().getAssignableUsers(currentUser, UserRole.SURVEILLANCE_OFFICER));
		
		addField(EventDto.TYPE_OF_PLACE, ComboBox.class);
		addField(EventDto.TYPE_OF_PLACE_TEXT, TextField.class);		
		addField(EventDto.REPORT_DATE_TIME, DateField.class);
		addField(EventDto.REPORTING_USER, ComboBox.class);
		addField(EventDto.SRC_FIRST_NAME, TextField.class);
		addField(EventDto.SRC_LAST_NAME, TextField.class);
		addField(EventDto.SRC_TEL_NO, TextField.class);
		addField(EventDto.SRC_EMAIL, TextField.class);
		addField(EventDto.EVENT_LOCATION, LocationForm.class).setCaption(null);
		
		setReadOnly(true, EventDto.UUID, EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), EventDto.TYPE_OF_PLACE_TEXT, EventDto.TYPE_OF_PLACE, Arrays.asList(TypeOfPlace.OTHER), true, true);
		setRequired(true, EventDto.EVENT_TYPE, EventDto.EVENT_STATUS, EventDto.UUID, EventDto.EVENT_DESC,
				EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER, EventDto.TYPE_OF_PLACE, EventDto.SRC_FIRST_NAME,
				EventDto.SRC_LAST_NAME, EventDto.SRC_TEL_NO);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
