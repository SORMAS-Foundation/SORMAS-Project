package de.symeda.sormas.ui.utils;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.utils.DateHelper;

@SuppressWarnings("serial")
public class DateTimeField extends CustomField<Date> {
	
	private static final String CAPTION_PROPERTY_ID = "caption";

	private DateField dateField;
	private ComboBox timeField;
	
	@Override
	protected Component initContent() {
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setWidth(100, Unit.PERCENTAGE);
		
		dateField = new DateField();
		dateField.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(dateField);
		layout.setExpandRatio(dateField, 0.5f);
		
		timeField = new ComboBox();
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
				Date date = DateHelper.parseHourMinute(newItemCaption);
				timeField.setValue(ensureTimeEntry(date));
			}
		});
		
		timeField.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(timeField);
		layout.setExpandRatio(timeField, 0.5f);
		
		// set field values based on internal value
		setInternalValue(super.getInternalValue());
		
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
				dateField.setValue(DateUtils.truncate(newValue, Calendar.DATE));
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
			Integer totalMinutes = (Integer)timeField.getValue();
			if (date != null) {
				if (totalMinutes != null) {
					date = DateUtils.setHours(date, (totalMinutes / 60) % 24);
					date = DateUtils.setMinutes(date, totalMinutes % 60);
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
		int totalMinutes = Integer.valueOf((int)DateUtils.getFragmentInMinutes(time, Calendar.DATE)) % (24*60);
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
	
}