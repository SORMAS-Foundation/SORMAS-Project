package de.symeda.sormas.ui.utils;

import static de.symeda.sormas.ui.utils.CssStyles.CHECKBOX_FILTER_INLINE;
import static de.symeda.sormas.ui.utils.CssStyles.LABEL_BOLD;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_4;

import java.util.Calendar;
import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.PopupDateField;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;

public class BirthdateRangeFilterComponent extends HorizontalLayout {

	private static final long serialVersionUID = 8752630393182144501L;

	private final PopupDateField dateFromFilter;
	private final PopupDateField dateToFilter;
	private final CheckBox includePartialMatch;

	public BirthdateRangeFilterComponent(boolean showCaption, AbstractFilterForm parentFilterForm) {
		setSpacing(true);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());

		dateFromFilter = new PopupDateField();
		dateToFilter = new PopupDateField();
		includePartialMatch = new CheckBox();

		Label birthdateLabel = new Label();
		birthdateLabel.setValue(I18nProperties.getCaption(Captions.birthdateFilter));
		birthdateLabel.addStyleName(CHECKBOX_FILTER_INLINE);
		birthdateLabel.addStyleName(LABEL_BOLD);
		birthdateLabel.addStyleName(VSPACE_TOP_4);
		addComponent(birthdateLabel);

		addComponent(dateFromFilter);
		addComponent(dateToFilter);

		// Date filter
		dateFromFilter.setDateFormat(DateFormatHelper.getDateFormatPattern());
		dateFromFilter.setId("dateFrom");
		dateFromFilter.setWidth(200, Unit.PIXELS);
		if (showCaption) {
			dateFromFilter.setCaption(I18nProperties.getCaption(Captions.from));
		}
		dateFromFilter.setInputPrompt(I18nProperties.getString(Strings.promptBirthdateFrom));

		dateToFilter.setDateFormat(DateFormatHelper.getDateFormatPattern());
		dateToFilter.setId("dateTo");
		dateToFilter.setWidth(200, Unit.PIXELS);
		if (showCaption) {
			dateToFilter.setCaption(I18nProperties.getCaption(Captions.to));
		}
		dateToFilter.setInputPrompt(I18nProperties.getString(Strings.promptBirthdateTo));

		dateFromFilter.addValueChangeListener(e -> {
			Date dateFrom = (Date) e.getProperty().getValue();
			Date dateTo = dateToFilter.getValue();
			notifyIfIncorrectRange(dateFrom, dateTo);
			parentFilterForm.onChange();
		});

		dateToFilter.addValueChangeListener(e -> {
			Date dateTo = (Date) e.getProperty().getValue();
			Date dateFrom = dateFromFilter.getValue();
			notifyIfIncorrectRange(dateFrom, dateTo);
			parentFilterForm.onChange();
		});

		includePartialMatch.setCaption(I18nProperties.getCaption(Captions.includePartialBirthdates));
		includePartialMatch.addStyleName(VSPACE_TOP_4);
		addComponent(includePartialMatch);

		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription(I18nProperties.getString(Strings.infoBirthdateFilter));
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
		addComponent(infoLabel);
	}

	private static void notifyIfIncorrectRange(Date dateFrom, Date dateTo) {
		if (dateFrom != null & dateTo != null) {
			if (DateHelper.isDateAfter(dateFrom, dateTo)) {
				Notification notification = new Notification(
					I18nProperties.getString(Strings.headingIncorrectDateRange),
					I18nProperties.getString(Strings.messageIncorrectDateRange),
					Notification.Type.WARNING_MESSAGE,
					false);

				notification.setDelayMsec(-1);
				notification.show(Page.getCurrent());
			}
		}
	}

	public PopupDateField getDateFromFilter() {
		return dateFromFilter;
	}

	public PopupDateField getDateToFilter() {
		return dateToFilter;
	}

	public CheckBox getIncludePartialMatch() {
		return includePartialMatch;
	}
}
