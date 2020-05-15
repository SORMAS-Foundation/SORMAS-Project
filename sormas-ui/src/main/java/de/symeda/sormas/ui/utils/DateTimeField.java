/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.AbstractSelect.NewItemHandler;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.DateField;

import de.symeda.sormas.api.utils.DateHelper;

@SuppressWarnings("serial")
public class DateTimeField extends CustomField<Date> {

	private static final String CAPTION_PROPERTY_ID = "caption";

	private DateField dateField;
	private ComboBox timeField;

	private Converter<Date, ?> converter;
	boolean converterSet;

	@Override
	protected Component initContent() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setWidth(100, Unit.PERCENTAGE);

		dateField = new DateField();
		dateField.setId(this.getId() + "_" + "date");
		dateField.setWidth(100, Unit.PERCENTAGE);
		dateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		dateField.setLenient(true);
		layout.addComponent(dateField);
		layout.setExpandRatio(dateField, 0.5f);
		
		if (!converterSet) {
			dateField.setConverter(converter);
			converterSet = true;
		}
		
		timeField = new ComboBox();
		timeField.setId(this.getId() + "_" + "time");
		timeField.addContainerProperty(CAPTION_PROPERTY_ID, String.class, null);
		timeField.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);

		// fill
		for (int hours=0; hours<=23; hours++) {
			for (int minutes = 0; minutes<=59; minutes+=15) {
				ensureTimeEntry(hours, minutes);
			}
		}

		timeField.setNewItemsAllowed(true);
		timeField.setNewItemHandler(new NewItemHandler() {
			@Override
			public void addNewItem(String newItemCaption) {
				Date date = DateHelper.parseTime(newItemCaption);
				timeField.setValue(ensureTimeEntry(date));
			}
		});

		timeField.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(timeField);
		layout.setExpandRatio(timeField, 0.5f);

		// value cn't be set on readOnly fields
		dateField.setReadOnly(false);
		timeField.setReadOnly(false);

		// set field values based on internal value
		setInternalValue(super.getInternalValue());

		dateField.setReadOnly(isReadOnly());
		timeField.setReadOnly(isReadOnly());

		Property.ValueChangeListener validationValueChangeListener = new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				markAsDirty();
			}
		};
		dateField.addValueChangeListener(validationValueChangeListener);
		timeField.addValueChangeListener(validationValueChangeListener);

		return layout;
	}

	@Override
	public Class<? extends Date> getType() {
		return Date.class;
	}

	@Override
	protected void setInternalValue(Date newValue) {
		super.setInternalValue(newValue);

		if (dateField != null && timeField != null) {

			if (newValue != null) {
				dateField.setValue(new LocalDate(newValue).toDate());
				timeField.setValue(ensureTimeEntry(newValue));
			}
			else {
				dateField.setValue(null);
				timeField.setValue(null);
			}
		}
	}

	@Override
	protected Date getInternalValue() {

		if (dateField != null && timeField != null) {
			Date date = dateField.getValue();
			if (date != null) {
				Integer totalMinutes = (Integer)timeField.getValue();
				if (totalMinutes != null) {
					DateTime dateTime = new DateTime(date);
					dateTime = dateTime.withHourOfDay((totalMinutes / 60) % 24).withMinuteOfHour( totalMinutes % 60);
					date = dateTime.toDate();
				}
				return date;
			}
			return null;
		}

		return super.getInternalValue();
	}

	/**
	 * @return itemId of the entry
	 */
	private Object ensureTimeEntry(Date time) {
		if (time == null) {
			return null;
		}
		int totalMinutes = new DateTime(time).minuteOfDay().get();
		return ensureTimeEntry((totalMinutes / 60)%24, totalMinutes % 60);
	}

	/**
	 * @return itemId of the entry
	 */
	@SuppressWarnings("unchecked")
	private Object ensureTimeEntry(int hours, int minutes) {
		int itemId = hours*60 + minutes;
		if (!timeField.containsId(itemId)) {
			timeField.addItem(itemId).getItemProperty(CAPTION_PROPERTY_ID).setValue(String.format("%1$02d:%2$02d", hours, minutes));

			// don't do this on initialization
			if (timeField.getParent() != null) {
				// order the entries by time
				((IndexedContainer)timeField.getContainerDataSource())
				.sort(new String[] {CAPTION_PROPERTY_ID}, new boolean[]{true});
			}
		}
		return itemId;
	}

	@Override
	public void setConverter(Converter<Date, ?> converter) {
		this.converter = converter;
		
		if (dateField != null) {
			dateField.setConverter(converter);
			converterSet = true;
		}
	}
}