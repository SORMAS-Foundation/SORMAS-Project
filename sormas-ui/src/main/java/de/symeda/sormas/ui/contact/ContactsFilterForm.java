package de.symeda.sormas.ui.contact;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.PopupDateField;
import com.vaadin.v7.ui.TextField;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDateType;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;

import java.util.Date;
import java.util.stream.Stream;

import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

public class ContactsFilterForm extends AbstractFilterForm<ContactCriteria> {

	private static final String DISTRICT_INFO_LABEL_ID = "infoContactsViewRegionDistrictFilter";

	private static final String WEEK_AND_DATE_FILTER = "moreFilters";

	private static final String MORE_FILTERS_HTML = filterLocs(ContactCriteria.REGION, ContactCriteria.DISTRICT,
			DISTRICT_INFO_LABEL_ID, ContactCriteria.CONTACT_OFFICER, ContactCriteria.REPORTING_USER_ROLE, ContactCriteria.FOLLOW_UP_UNTIL_TO) +
			filterLocs(ContactCriteria.QUARANTINE_TYPE, ContactDto.QUARANTINE_TO, ContactCriteria.QUARANTINE_ORDERED_VERBALLY, ContactCriteria.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT, ContactCriteria.QUARANTINE_NOT_ORDERED, ContactCriteria.ONLY_QUARANTINE_HELP_NEEDED, ContactCriteria.ONLY_HIGH_PRIORITY_CONTACTS) +
			loc(WEEK_AND_DATE_FILTER);

	protected ContactsFilterForm() {
		super(ContactCriteria.class, ContactIndexDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[]{ContactIndexDto.CONTACT_CLASSIFICATION, ContactIndexDto.DISEASE, ContactIndexDto.CASE_CLASSIFICATION, ContactIndexDto.CONTACT_CATEGORY, ContactIndexDto.FOLLOW_UP_STATUS, ContactCriteria.NAME_UUID_CASE_LIKE};
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML;
	}

	@Override
	protected void addFields() {
		addField(FieldConfiguration.pixelSized(ContactIndexDto.CONTACT_CLASSIFICATION, 140));
		addField(FieldConfiguration.pixelSized(ContactIndexDto.DISEASE, 140));

		ComboBox caseClassificationField = addField(FieldConfiguration.pixelSized(ContactIndexDto.CASE_CLASSIFICATION, 140));
		caseClassificationField.setDescription(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_CLASSIFICATION));

		if (isGermanServer()) {
			addField(FieldConfiguration.pixelSized(ContactIndexDto.CONTACT_CATEGORY, 140));
		}

		addField(FieldConfiguration.pixelSized(ContactIndexDto.FOLLOW_UP_STATUS, 140));

		TextField searchField = addField(FieldConfiguration.withCaptionAndPixelSized(ContactCriteria.NAME_UUID_CASE_LIKE, I18nProperties.getString(Strings.promptContactsSearchField), 200));
		searchField.setNullRepresentation("");
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {
		UserDto user = UserProvider.getCurrent().getUser();

		if (user.getRegion() == null) {
			ComboBox regionField = addField(moreFiltersContainer, FieldConfiguration.withCaptionAndPixelSized(ContactCriteria.REGION, I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.REGION_UUID), 240));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		}

		ComboBox districtField = addField(moreFiltersContainer, FieldConfiguration.withCaptionAndPixelSized(ContactCriteria.DISTRICT, I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.DISTRICT_UUID), 240));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));

		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription(I18nProperties.getString(Strings.infoContactsViewRegionDistrictFilter), ContentMode.HTML);
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY, AbstractFilterForm.FILTER_ITEM_STYLE);
		moreFiltersContainer.addComponent(infoLabel, DISTRICT_INFO_LABEL_ID);

		addField(moreFiltersContainer, FieldConfiguration.withCaptionAndPixelSized(ContactCriteria.CONTACT_OFFICER, I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER_UUID), 140));
		addField(moreFiltersContainer, FieldConfiguration.withCaptionAndPixelSized(ContactCriteria.REPORTING_USER_ROLE, I18nProperties.getString(Strings.reportedBy), 140));
		addField(moreFiltersContainer, FieldConfiguration.withCaptionAndPixelSized(ContactCriteria.FOLLOW_UP_UNTIL_TO, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.FOLLOW_UP_UNTIL), 200));
		addField(moreFiltersContainer, FieldConfiguration.withCaptionAndPixelSized(ContactCriteria.QUARANTINE_TYPE, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.QUARANTINE), 140));

		addField(moreFiltersContainer, FieldConfiguration.withCaptionAndPixelSized(ContactDto.QUARANTINE_TO, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.QUARANTINE_TO), 140));

		if (isGermanServer()) {
			addField(moreFiltersContainer, CheckBox.class, FieldConfiguration.withCaptionAndStyle(ContactCriteria.QUARANTINE_ORDERED_VERBALLY, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.QUARANTINE_ORDERED_VERBALLY), null, CssStyles.CHECKBOX_FILTER_INLINE));
			addField(moreFiltersContainer, CheckBox.class, FieldConfiguration.withCaptionAndStyle(ContactCriteria.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT), null, CssStyles.CHECKBOX_FILTER_INLINE));
			addField(moreFiltersContainer, CheckBox.class, FieldConfiguration.withCaptionAndStyle(ContactCriteria.QUARANTINE_NOT_ORDERED, I18nProperties.getCaption(Captions.contactQuarantineNotOrdered), null, CssStyles.CHECKBOX_FILTER_INLINE));
		}

		addField(moreFiltersContainer, CheckBox.class, FieldConfiguration.withCaptionAndStyle(ContactCriteria.ONLY_QUARANTINE_HELP_NEEDED, I18nProperties.getCaption(Captions.contactOnlyQuarantineHelpNeeded), null, CssStyles.CHECKBOX_FILTER_INLINE));
		addField(moreFiltersContainer, CheckBox.class, FieldConfiguration.withCaptionAndStyle(ContactCriteria.ONLY_HIGH_PRIORITY_CONTACTS, I18nProperties.getCaption(Captions.contactOnlyHighPriorityContacts), null, CssStyles.CHECKBOX_FILTER_INLINE));

		moreFiltersContainer.addComponent(buildWeekAndDateFilter(), WEEK_AND_DATE_FILTER);
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		switch (propertyId) {
			case ContactCriteria.REGION: {
				getField(ContactCriteria.DISTRICT).setValue(null);
				break;
			}
			case ContactCriteria.FOLLOW_UP_UNTIL_TO: {
				getValue().followUpUntilToPrecise(event.getProperty().getValue() != null);
				break;
			}
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(ContactCriteria newValue) {
		UserDto user = UserProvider.getCurrent().getUser();

		RegionReferenceDto region = newValue.getRegion();

		ComboBox districtField = (ComboBox) getField(ContactCriteria.DISTRICT);
		if (user.getRegion() != null && user.getDistrict() == null) {
			districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
			districtField.setEnabled(true);
		} else {
			if (region != null) {
				districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
				districtField.setEnabled(true);
			} else {
				districtField.setEnabled(false);
			}
		}

		ComboBox officerField = (ComboBox) getField(ContactCriteria.CONTACT_OFFICER);
		if (user.getRegion() != null) {
			officerField.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(user.getRegion(), UserRole.CONTACT_OFFICER));
		} else if (region != null) {
			officerField.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(region, UserRole.CONTACT_OFFICER));
		} else {
			officerField.removeAllItems();
		}

		// Date/Epi week filter
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter = (EpiWeekAndDateFilterComponent<NewCaseDateType>) dateFilterLayout.getComponent(0);

		ContactDateType contactDateType = newValue.getReportDateFrom() != null ? ContactDateType.REPORT_DATE
				: newValue.getLastContactDateFrom() != null ? ContactDateType.LAST_CONTACT_DATE : null;
		weekAndDateFilter.getDateTypeSelector().setValue(contactDateType);
		weekAndDateFilter.getDateFilterOptionFilter().setValue(newValue.getDateFilterOption());
		Date dateFrom = contactDateType == ContactDateType.REPORT_DATE ? newValue.getReportDateFrom()
				: contactDateType == ContactDateType.LAST_CONTACT_DATE ? newValue.getLastContactDateFrom() : null;
		Date dateTo = contactDateType == ContactDateType.REPORT_DATE ? newValue.getReportDateTo()
				: contactDateType == ContactDateType.LAST_CONTACT_DATE ? newValue.getLastContactDateTo() : null;

		if (DateFilterOption.EPI_WEEK.equals(newValue.getDateFilterOption())) {
			weekAndDateFilter.getWeekFromFilter().setValue(dateFrom == null ? null : DateHelper.getEpiWeek(dateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(dateTo == null ? null : DateHelper.getEpiWeek(dateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(dateFrom);
			weekAndDateFilter.getDateToFilter().setValue(dateTo);
		}
	}

	@Override
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter = (EpiWeekAndDateFilterComponent<NewCaseDateType>) dateFilterLayout.getComponent(0);

		return super.streamFieldsForEmptyCheck(layout)
				.filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}

	private boolean isGermanServer() {
		return FacadeProvider.getConfigFacade().isGermanServer();
	}

	private HorizontalLayout buildWeekAndDateFilter() {
		Button applyButton = ButtonHelper.createButton(Captions.actionApplyDateFilter, null);

		EpiWeekAndDateFilterComponent<ContactDateType> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(applyButton, false, false, null, ContactDateType.class, I18nProperties.getString(Strings.promptContactDateType), ContactDateType.REPORT_DATE);
		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactDateTo));

		applyButton.addClickListener(e -> {
			ContactCriteria criteria = getValue();

			DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			Date fromDate, toDate;
			if (dateFilterOption == DateFilterOption.DATE) {
				fromDate = DateHelper.getStartOfDay(weekAndDateFilter.getDateFromFilter().getValue());
				toDate = DateHelper.getEndOfDay(weekAndDateFilter.getDateToFilter().getValue());
			} else {
				fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
				toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
			}
			if ((fromDate != null && toDate != null) || (fromDate == null && toDate == null)) {
				applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
				ContactDateType contactDateType = (ContactDateType) weekAndDateFilter.getDateTypeSelector().getValue();
				if (contactDateType == ContactDateType.LAST_CONTACT_DATE) {
					criteria.lastContactDateBetween(fromDate, toDate);
					criteria.reportDateBetween(null, null);
				} else {
					criteria.reportDateBetween(fromDate, toDate);
					criteria.lastContactDateBetween(null, null);
				}
				criteria.dateFilterOption(dateFilterOption);

				fireValueChange(true);
			} else {
				if (dateFilterOption == DateFilterOption.DATE) {
					Notification notification = new Notification(I18nProperties.getString(Strings.headingMissingDateFilter),
							I18nProperties.getString(Strings.messageMissingDateFilter), Notification.Type.WARNING_MESSAGE, false);
					notification.setDelayMsec(-1);
					notification.show(Page.getCurrent());
				} else {
					Notification notification = new Notification(I18nProperties.getString(Strings.headingMissingEpiWeekFilter),
							I18nProperties.getString(Strings.messageMissingEpiWeekFilter), Notification.Type.WARNING_MESSAGE, false);
					notification.setDelayMsec(-1);
					notification.show(Page.getCurrent());
				}
			}
		});

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);
		dateFilterRowLayout.addComponent(applyButton);

		return dateFilterRowLayout;
	}

	public void setSearchFieldEnabled(boolean enabled) {
		this.getField(ContactCriteria.NAME_UUID_CASE_LIKE).setEnabled(enabled);
	}
}
