/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils.components;

import java.time.Month;
import java.util.function.Function;

import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;

public class BirthdateFields {

	private final String dayFieldName;
	private final String monthFieldName;
	private final String yearFieldName;

	private ComboBox birthDateDay;
	private ComboBox birthDateMonth;
	private ComboBox birthDateYear;

	public BirthdateFields(String dayFieldName, String monthFieldName, String yearFieldName) {
		this.dayFieldName = dayFieldName;
		this.monthFieldName = monthFieldName;
		this.yearFieldName = yearFieldName;
	}

	public void initFields(Function<String, ComboBox> addField, Runnable fieldChangeCallback) {

		birthDateDay = addField.apply(dayFieldName);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateDay.setNullSelectionAllowed(true);
		birthDateDay.setInputPrompt(I18nProperties.getString(Strings.day));
		birthDateDay.setCaption("");

		birthDateMonth = addField.apply(monthFieldName);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateMonth.setNullSelectionAllowed(true);
		birthDateMonth.addItems(DateHelper.getMonthsInYear());
		birthDateMonth.setPageLength(12);
		birthDateMonth.setInputPrompt(I18nProperties.getString(Strings.month));
		birthDateMonth.setCaption("");
		DateHelper.getMonthsInYear()
			.forEach(month -> birthDateMonth.setItemCaption(month, de.symeda.sormas.api.Month.values()[month - 1].toString()));
		setItemCaptionsForMonths(birthDateMonth);

		birthDateYear = addField.apply(yearFieldName);
		birthDateYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateYear.setNullSelectionAllowed(true);
		birthDateYear.addItems(DateHelper.getYearsToNow());
		birthDateYear.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID_TOSTRING);
		birthDateYear.setInputPrompt(I18nProperties.getString(Strings.year));
		birthDateDay.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) birthDateYear.getValue(), (Integer) birthDateMonth.getValue(), (Integer) e));
		birthDateMonth.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) birthDateYear.getValue(), (Integer) e, (Integer) birthDateDay.getValue()));
		birthDateYear.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) e, (Integer) birthDateMonth.getValue(), (Integer) birthDateDay.getValue()));

		birthDateYear.addValueChangeListener(e -> {
			updateListOfDays(birthDateDay, (Integer) e.getProperty().getValue(), (Integer) birthDateMonth.getValue());
			birthDateMonth.markAsDirty();
			birthDateDay.markAsDirty();

			fieldChangeCallback.run();
		});
		birthDateMonth.addValueChangeListener(e -> {
			updateListOfDays(birthDateDay, (Integer) birthDateYear.getValue(), (Integer) e.getProperty().getValue());
			birthDateYear.markAsDirty();
			birthDateDay.markAsDirty();

			fieldChangeCallback.run();
		});
		birthDateDay.addValueChangeListener(e -> {
			birthDateYear.markAsDirty();
			birthDateMonth.markAsDirty();

			fieldChangeCallback.run();
		});
	}

	public ComboBox getBirthDateDay() {
		return birthDateDay;
	}

	public ComboBox getBirthDateMonth() {
		return birthDateMonth;
	}

	public ComboBox getBirthDateYear() {
		return birthDateYear;
	}

	private void setItemCaptionsForMonths(AbstractSelect months) {
		months.setItemCaption(1, I18nProperties.getEnumCaption(Month.JANUARY));
		months.setItemCaption(2, I18nProperties.getEnumCaption(Month.FEBRUARY));
		months.setItemCaption(3, I18nProperties.getEnumCaption(Month.MARCH));
		months.setItemCaption(4, I18nProperties.getEnumCaption(Month.APRIL));
		months.setItemCaption(5, I18nProperties.getEnumCaption(Month.MAY));
		months.setItemCaption(6, I18nProperties.getEnumCaption(Month.JUNE));
		months.setItemCaption(7, I18nProperties.getEnumCaption(Month.JULY));
		months.setItemCaption(8, I18nProperties.getEnumCaption(Month.AUGUST));
		months.setItemCaption(9, I18nProperties.getEnumCaption(Month.SEPTEMBER));
		months.setItemCaption(10, I18nProperties.getEnumCaption(Month.OCTOBER));
		months.setItemCaption(11, I18nProperties.getEnumCaption(Month.NOVEMBER));
		months.setItemCaption(12, I18nProperties.getEnumCaption(Month.DECEMBER));
	}

	private void updateListOfDays(ComboBox birthDateDay, Integer selectedYear, Integer selectedMonth) {
		if (!birthDateDay.isReadOnly()) {
			Integer currentlySelected = (Integer) birthDateDay.getValue();
			birthDateDay.removeAllItems();
			birthDateDay.addItems(DateHelper.getDaysInMonth(selectedMonth, selectedYear));
			if (birthDateDay.containsId(currentlySelected)) {
				birthDateDay.setValue(currentlySelected);
			}
		}
	}
}
