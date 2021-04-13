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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.PopupDateField;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;

public class EpiWeekAndDateFilterComponent<DATE_TYPE> extends HorizontalLayout {

	private static final long serialVersionUID = 8752630393182185034L;

	private ComboBox dateFilterOptionFilter;
	private ComboBox dateTypeSelector;
	private ComboBox weekFromFilter;
	private ComboBox weekToFilter;
	private PopupDateField dateFromFilter;
	private PopupDateField dateToFilter;

	public EpiWeekAndDateFilterComponent(boolean fillAutomatically, boolean showCaption, String infoText, AbstractFilterForm parentFilterForm) {
		this(fillAutomatically, showCaption, infoText, null, null, null, parentFilterForm);
	}

	public EpiWeekAndDateFilterComponent(
		boolean fillAutomatically,
		boolean showCaption,
		String infoText,
		DATE_TYPE[] dateTypes,
		String dateTypePrompt,
		DATE_TYPE defaultDateType,
		AbstractFilterForm parentFilterForm) {
		setSpacing(true);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());

		dateFilterOptionFilter = new ComboBox();
		dateTypeSelector = new ComboBox();
		weekFromFilter = new ComboBox();
		weekToFilter = new ComboBox();
		dateFromFilter = new PopupDateField();
		dateToFilter = new PopupDateField();

		// Date filter options
		dateFilterOptionFilter.setId("dateFilterOption");
		dateFilterOptionFilter.setWidth(200, Unit.PIXELS);
		dateFilterOptionFilter.addItems((Object[]) DateFilterOption.values());
		dateFilterOptionFilter.setNullSelectionAllowed(false);
		dateFilterOptionFilter.select(DateFilterOption.EPI_WEEK);
		if (showCaption) {
			dateFilterOptionFilter.setCaption(I18nProperties.getCaption(Captions.dashboardCustomPeriod));
		}

		dateFilterOptionFilter.addValueChangeListener(e -> {
			if (e.getProperty().getValue() == DateFilterOption.DATE) {
				int newIndex = getComponentIndex(weekFromFilter);
				removeComponent(weekFromFilter);
				removeComponent(weekToFilter);
				addComponent(dateFromFilter, newIndex);
				addComponent(dateToFilter, newIndex + 1);

				if (fillAutomatically) {
					dateFromFilter.setValue(DateHelper.subtractDays(c.getTime(), 7));
				}
				if (fillAutomatically) {
					dateToFilter.setValue(c.getTime());
				}
			} else if (getComponentIndex(dateFromFilter) != -1) {
				int newIndex = getComponentIndex(dateFromFilter);
				removeComponent(dateFromFilter);
				removeComponent(dateToFilter);
				addComponent(weekFromFilter, newIndex);
				addComponent(weekToFilter, newIndex + 1);

				if (fillAutomatically) {
					weekFromFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
				}
				if (fillAutomatically) {
					weekToFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
				}
			}
		});
		addComponent(dateFilterOptionFilter);

		// New case date type selector
		if (dateTypes != null) {
			dateTypeSelector.setId("dateType");
			dateTypeSelector.setWidth(200, Unit.PIXELS);
			dateTypeSelector.addItems(dateTypes);
			if (dateTypePrompt != null) {
				dateTypeSelector.setInputPrompt(dateTypePrompt);
			}
			if (defaultDateType != null) {
				dateTypeSelector.select(defaultDateType);
			}
			if (showCaption) {
				CssStyles.style(dateTypeSelector, CssStyles.FORCE_CAPTION);
			}
			addComponent(dateTypeSelector);

			if (!StringUtils.isEmpty(infoText)) {
				Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
				infoLabel.setSizeUndefined();
				infoLabel.setDescription(infoText, ContentMode.HTML);
				CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
				addComponent(infoLabel);
			}
		}

		// Epi week filter
		List<EpiWeek> epiWeekList = DateHelper.createEpiWeekList(c.get(Calendar.YEAR), c.get(Calendar.WEEK_OF_YEAR));

		weekFromFilter.setId("weekFrom");
		weekFromFilter.setWidth(200, Unit.PIXELS);
		for (EpiWeek week : epiWeekList) {
			weekFromFilter.addItem(week);
		}
		weekFromFilter.setNullSelectionAllowed(false);
		if (fillAutomatically) {
			weekFromFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
		}
		if (showCaption) {
			weekFromFilter.setCaption(I18nProperties.getCaption(Captions.epiWeekFrom));
		}
		addComponent(weekFromFilter);

		weekToFilter.setId("weekTo");
		weekToFilter.setWidth(200, Unit.PIXELS);
		for (EpiWeek week : epiWeekList) {
			weekToFilter.addItem(week);
		}
		weekToFilter.setNullSelectionAllowed(false);
		if (fillAutomatically) {
			weekToFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
		}
		if (showCaption) {
			weekToFilter.setCaption(I18nProperties.getCaption(Captions.epiWeekTo));
		}
		addComponent(weekToFilter);

		// Date filter
		dateFromFilter.setId("dateFrom");
		dateFromFilter.setWidth(200, Unit.PIXELS);
		if (showCaption) {
			dateFromFilter.setCaption(I18nProperties.getCaption(Captions.from));
		}

		dateToFilter.setId("dateTo");
		dateToFilter.setWidth(200, Unit.PIXELS);
		if (showCaption) {
			dateToFilter.setCaption(I18nProperties.getCaption(Captions.to));
		}

		if (parentFilterForm != null) {
			dateFilterOptionFilter.addValueChangeListener(e -> parentFilterForm.onChange());
			dateTypeSelector.addValueChangeListener(e -> parentFilterForm.onChange());
			weekFromFilter.addValueChangeListener(e -> parentFilterForm.onChange());
			weekToFilter.addValueChangeListener(e -> parentFilterForm.onChange());
			dateFromFilter.addValueChangeListener(e -> parentFilterForm.onChange());
			dateToFilter.addValueChangeListener(e -> parentFilterForm.onChange());
		}
	}

	public ComboBox getDateFilterOptionFilter() {
		return dateFilterOptionFilter;
	}

	public ComboBox getDateTypeSelector() {
		return dateTypeSelector;
	}

	public ComboBox getWeekFromFilter() {
		return weekFromFilter;
	}

	public ComboBox getWeekToFilter() {
		return weekToFilter;
	}

	public PopupDateField getDateFromFilter() {
		return dateFromFilter;
	}

	public PopupDateField getDateToFilter() {
		return dateToFilter;
	}
}
