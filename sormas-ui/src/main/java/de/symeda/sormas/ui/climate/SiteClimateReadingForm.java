/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.climate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.SiteClimateReadingDto;
import de.symeda.sormas.api.climate.SiteClimateReadingValidation;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Adds or corrects a site reading: a spot measurement taken at the environment. It is kept beside the daily record and
 * never changes it. The checks are those of manual entry, done again by the server.
 */
public class SiteClimateReadingForm extends VerticalLayout {

	private static final long serialVersionUID = 3407164213372150384L;

	private final SiteClimateReadingDto reading;
	private final Runnable onChanged;
	private Window window;

	private final DateTimeField readingDateTime = new DateTimeField(caption(SiteClimateReadingDto.READING_DATE_TIME));
	private final TextField airTemperature = numberField(SiteClimateReadingDto.AIR_TEMPERATURE);
	private final TextField rainfall = numberField(SiteClimateReadingDto.RAINFALL);
	private final TextField rainfallPeriodHours = numberField(SiteClimateReadingDto.RAINFALL_PERIOD_HOURS);
	private final TextField relativeHumidity = numberField(SiteClimateReadingDto.RELATIVE_HUMIDITY);
	private final TextArea notes = new TextArea(caption(SiteClimateReadingDto.NOTES));

	public static void openNew(EnvironmentReferenceDto environment, Runnable onChanged) {
		SiteClimateReadingDto reading = SiteClimateReadingDto.build(environment);
		reading.setReadingDateTime(new Date());
		open(reading, onChanged, true);
	}

	public static void openEdit(SiteClimateReadingDto reading, Runnable onChanged) {
		open(reading, onChanged, false);
	}

	private static void open(SiteClimateReadingDto reading, Runnable onChanged, boolean isNew) {
		SiteClimateReadingForm form = new SiteClimateReadingForm(reading, onChanged);
		form.window = VaadinUiUtil
			.showPopupWindow(form, I18nProperties.getString(isNew ? Strings.headingSiteClimateReadingNew : Strings.headingSiteClimateReading));
	}

	SiteClimateReadingForm(SiteClimateReadingDto reading, Runnable onChanged) {

		this.reading = reading;
		this.onChanged = onChanged;
		setMargin(true);
		setSpacing(true);
		setWidth(560, Unit.PIXELS);

		Label info = new Label(I18nProperties.getString(Strings.infoSiteClimateReading));
		info.setWidthFull();
		info.addStyleName(CssStyles.LABEL_SECONDARY);
		addComponent(info);

		readingDateTime.setDateFormat(DateFormatHelper.getDateFormatPattern() + " HH:mm");
		readingDateTime.setRangeEnd(LocalDateTime.now());
		notes.setWidthFull();
		notes.setRows(3);
		notes.setMaxLength(SiteClimateReadingValidation.NOTES_MAX_LENGTH);
		FormLayout fields = new FormLayout(readingDateTime, airTemperature, rainfall, rainfallPeriodHours, relativeHumidity, notes);
		fields.setMargin(false);
		CssStyles.style(CssStyles.VSPACE_NONE, readingDateTime, notes);
		addComponent(fields);

		if (reading.getReadingDateTime() != null) {
			readingDateTime.setValue(LocalDateTime.ofInstant(reading.getReadingDateTime().toInstant(), ZoneId.systemDefault()));
		}
		airTemperature.setValue(ClimateCaptions.number(reading.getAirTemperature()));
		rainfall.setValue(ClimateCaptions.number(reading.getRainfall()));
		rainfallPeriodHours.setValue(reading.getRainfallPeriodHours() != null ? String.valueOf(reading.getRainfallPeriodHours()) : "");
		relativeHumidity.setValue(ClimateCaptions.number(reading.getRelativeHumidity()));
		notes.setValue(StringUtils.defaultString(reading.getNotes()));

		Button cancel = ButtonHelper.createButton(Captions.actionCancel, e -> window.close());
		Button save = ButtonHelper.createButton(Captions.actionSave, e -> save(), ValoTheme.BUTTON_PRIMARY);
		HorizontalLayout buttons = new HorizontalLayout(cancel, save);
		addComponent(buttons);
		setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
	}

	/**
	 * @return true when the reading was saved
	 */
	boolean save() {

		try {
			reading.setAirTemperature(ClimateObservationForm.readNumber(airTemperature));
			reading.setRainfall(ClimateObservationForm.readNumber(rainfall));
			reading.setRainfallPeriodHours(readHours());
			reading.setRelativeHumidity(ClimateObservationForm.readNumber(relativeHumidity));
		} catch (NumberFormatException e) {
			return false;
		}
		reading.setReadingDateTime(
			readingDateTime.getValue() != null ? Date.from(readingDateTime.getValue().atZone(ZoneId.systemDefault()).toInstant()) : null);
		reading.setNotes(StringUtils.trimToNull(notes.getValue()));

		String error = SiteClimateReadingValidation.validate(reading, new Date());
		if (error == null) {
			try {
				FacadeProvider.getSiteClimateReadingFacade().saveReading(reading);
			} catch (ValidationRuntimeException e) {
				error = e.getMessage();
			}
		}
		if (error != null) {
			Notification.show(error, Notification.Type.ERROR_MESSAGE);
			return false;
		}
		Notification.show(I18nProperties.getString(Strings.messageSiteClimateReadingSaved), Notification.Type.TRAY_NOTIFICATION);
		if (window != null) {
			window.close();
		}
		onChanged.run();
		return true;
	}

	private Integer readHours() {
		rainfallPeriodHours.setComponentError(null);
		String text = StringUtils.trimToNull(rainfallPeriodHours.getValue());
		if (text == null) {
			return null;
		}
		try {
			return Integer.valueOf(text);
		} catch (NumberFormatException e) {
			rainfallPeriodHours.setComponentError(new UserError(I18nProperties.getString(Strings.messageClimateEnterWholeNumber)));
			throw e;
		}
	}

	DateTimeField getReadingDateTime() {
		return readingDateTime;
	}

	TextField getRainfall() {
		return rainfall;
	}

	TextField getRainfallPeriodHours() {
		return rainfallPeriodHours;
	}

	TextField getAirTemperature() {
		return airTemperature;
	}

	private static String caption(String property) {
		return I18nProperties.getPrefixCaption(SiteClimateReadingDto.I18N_PREFIX, property);
	}

	private static TextField numberField(String property) {
		TextField field = new TextField(caption(property));
		field.setWidth(120, Unit.PIXELS);
		field.addStyleName(CssStyles.VSPACE_NONE);
		return field;
	}
}
