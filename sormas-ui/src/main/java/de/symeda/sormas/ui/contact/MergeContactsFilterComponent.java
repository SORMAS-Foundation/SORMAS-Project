package de.symeda.sormas.ui.contact;

import java.time.ZoneId;
import java.util.function.Consumer;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.QueryDetails;

@SuppressWarnings("serial")
public class MergeContactsFilterComponent extends VerticalLayout {

	// Layouts
	private HorizontalLayout firstRowLayout;
	private HorizontalLayout secondRowLayout;

	private DateField dfCreationDateFrom;
	private DateField dfCreationDateTo;
	private ComboBox<Disease> cbDisease;
	private TextField tfSearch;
	private TextField eventSearch;
	private TextField tfReportingUser;
	private CheckBox cbIgnoreRegion;
	private ComboBox<RegionReferenceDto> cbRegion;
	private ComboBox<DistrictReferenceDto> cbDistrict;
	private Button btnConfirmFilters;
	private Button btnResetFilters;

	private Binder<ContactCriteria> criteriaBinder = new Binder<>(ContactCriteria.class);
	private ContactCriteria criteria;
	private Binder<QueryDetails> queryDetailsBinder = new Binder<>(QueryDetails.class);
	private QueryDetails queryDetails;
	private Runnable filtersUpdatedCallback;
	private Consumer<Boolean> ignoreRegionCallback;

	private Label lblNumberOfDuplicates;

	public MergeContactsFilterComponent(ContactCriteria criteria, QueryDetails queryDetails) {

		setSpacing(false);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);

		addFirstRowLayout();
		addSecondRowLayout();

		setValue(criteria, queryDetails);
	}

	private void setValue(ContactCriteria criteria, QueryDetails queryDetails) {
		this.criteria = criteria;
		this.queryDetails = queryDetails;

		criteriaBinder.readBean(this.criteria);
		queryDetailsBinder.readBean(this.queryDetails);
	}

	private void addFirstRowLayout() {

		firstRowLayout = new HorizontalLayout();
		firstRowLayout.setMargin(false);
		firstRowLayout.setWidth(100, Unit.PERCENTAGE);

		dfCreationDateFrom = new DateField();
		dfCreationDateFrom.setId(ContactCriteria.CREATION_DATE_FROM);
		dfCreationDateFrom.setWidth(200, Unit.PIXELS);
		dfCreationDateFrom.setPlaceholder(I18nProperties.getString(Strings.promptCreationDateFrom));
		dfCreationDateFrom.setCaption(I18nProperties.getCaption(Captions.creationDate));
		criteriaBinder.forField(dfCreationDateFrom)
			.withConverter(new LocalDateToDateConverter(ZoneId.systemDefault()))
			.bind(ContactCriteria.CREATION_DATE_FROM);
		firstRowLayout.addComponent(dfCreationDateFrom);

		dfCreationDateTo = new DateField();
		dfCreationDateTo.setId(ContactCriteria.CREATION_DATE_TO);
		dfCreationDateTo.setWidth(200, Unit.PIXELS);
		CssStyles.style(dfCreationDateTo, CssStyles.FORCE_CAPTION);
		dfCreationDateTo.setPlaceholder(I18nProperties.getString(Strings.promptDateTo));
		criteriaBinder.forField(dfCreationDateTo)
			.withConverter(new LocalDateToDateConverter(ZoneId.systemDefault()))
			.bind(ContactCriteria.CREATION_DATE_TO);
		firstRowLayout.addComponent(dfCreationDateTo);

		cbDisease = new ComboBox<>();
		cbDisease.setId(ContactDto.DISEASE);
		cbDisease.setWidth(200, Unit.PIXELS);
		CssStyles.style(cbDisease, CssStyles.FORCE_CAPTION);
		cbDisease.setPlaceholder(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.DISEASE));
		cbDisease.setItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		criteriaBinder.bind(cbDisease, ContactDto.DISEASE);
		firstRowLayout.addComponent(cbDisease);

		tfSearch = new TextField();
		tfSearch.setId(ContactCriteria.CONTACT_OR_CASE_LIKE);
		tfSearch.setWidth(200, Unit.PIXELS);
		CssStyles.style(tfSearch, CssStyles.FORCE_CAPTION);
		tfSearch.setPlaceholder(I18nProperties.getString(Strings.promptContactsSearchField));
		criteriaBinder.bind(tfSearch, ContactCriteria.CONTACT_OR_CASE_LIKE);
		firstRowLayout.addComponent(tfSearch);

		eventSearch = new TextField();
		eventSearch.setId(ContactCriteria.EVENT_LIKE);
		eventSearch.setWidth(200, Unit.PIXELS);
		CssStyles.style(eventSearch, CssStyles.FORCE_CAPTION);
		eventSearch.setPlaceholder(I18nProperties.getString(Strings.promptCaseOrContactEventSearchField));
		criteriaBinder.bind(eventSearch, ContactCriteria.EVENT_LIKE);
		firstRowLayout.addComponent(eventSearch);

		tfReportingUser = new TextField();
		tfReportingUser.setId(ContactCriteria.REPORTING_USER_LIKE);
		tfReportingUser.setWidth(200, Unit.PIXELS);
		CssStyles.style(tfReportingUser, CssStyles.FORCE_CAPTION);
		tfReportingUser.setPlaceholder(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.REPORTING_USER));
		criteriaBinder.bind(tfReportingUser, ContactCriteria.REPORTING_USER_LIKE);
		firstRowLayout.addComponent(tfReportingUser);

		cbIgnoreRegion = new CheckBox();
		cbIgnoreRegion.setId(Captions.contactFilterWithDifferentRegion);
		CssStyles.style(cbIgnoreRegion, CssStyles.CHECKBOX_FILTER_INLINE);
		cbIgnoreRegion.setCaption(I18nProperties.getCaption(Captions.contactFilterWithDifferentRegion));
		cbIgnoreRegion.addValueChangeListener(e -> {
			ignoreRegionCallback.accept(e.getValue());
		});
		firstRowLayout.addComponent(cbIgnoreRegion);
		firstRowLayout.setComponentAlignment(cbIgnoreRegion, Alignment.MIDDLE_RIGHT);
		firstRowLayout.setExpandRatio(cbIgnoreRegion, 1);

		addComponent(firstRowLayout);
	}

	private void addSecondRowLayout() {

		secondRowLayout = new HorizontalLayout();
		secondRowLayout.setMargin(false);
		secondRowLayout.setWidth(100, Unit.PERCENTAGE);

		if (UiUtil.disabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			cbRegion = new ComboBox<>();
			cbDistrict = new ComboBox<>();
			cbRegion.setItemCaptionGenerator(item -> item.buildCaption());
			cbDistrict.setItemCaptionGenerator(item -> item.buildCaption());

			cbRegion.setId(ContactDto.REGION);
			cbRegion.setWidth(200, Unit.PIXELS);
			CssStyles.style(cbRegion, CssStyles.FORCE_CAPTION);
			cbRegion.setPlaceholder(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.REGION));
			cbRegion.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
			criteriaBinder.bind(cbRegion, ContactDto.REGION);
			cbRegion.addValueChangeListener(e -> {
				RegionReferenceDto region = e.getValue();
				cbDistrict.clear();
				if (region != null) {
					cbDistrict.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
					cbDistrict.setEnabled(true);
				} else {
					cbDistrict.setEnabled(false);
				}
			});
			secondRowLayout.addComponent(cbRegion);
			if (UiUtil.getUser().getRegion() != null) {
				cbRegion.setEnabled(false);
			}

			cbDistrict.setId(ContactDto.DISTRICT);
			cbDistrict.setWidth(200, Unit.PIXELS);
			CssStyles.style(cbDistrict, CssStyles.FORCE_CAPTION);
			cbDistrict.setPlaceholder(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.DISTRICT));
			criteriaBinder.bind(cbDistrict, ContactDto.DISTRICT);
			secondRowLayout.addComponent(cbDistrict);
		}

		ComboBox<Integer> cbResultLimit = new ComboBox<>();
		cbResultLimit.setId(QueryDetails.RESULT_LIMIT);
		cbResultLimit.setWidth(200, Unit.PIXELS);
		cbResultLimit.setCaption(I18nProperties.getCaption(Captions.QueryDetails_resultLimit));
		cbResultLimit.setItems(50, 100, 500, 1000);
		cbResultLimit.setEmptySelectionAllowed(false);
		queryDetailsBinder.bind(cbResultLimit, QueryDetails.RESULT_LIMIT);
		secondRowLayout.addComponent(cbResultLimit);

		btnConfirmFilters = ButtonHelper.createButton(Captions.actionConfirmFilters, event -> {
			try {
				criteriaBinder.writeBean(criteria);
				queryDetailsBinder.writeBean(queryDetails);
				filtersUpdatedCallback.run();
			} catch (ValidationException e) {
				// No validation needed
			}
		}, CssStyles.FORCE_CAPTION, ValoTheme.BUTTON_PRIMARY);

		secondRowLayout.addComponent(btnConfirmFilters);

		btnResetFilters = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(MergeContactsView.class).remove(ContactCriteria.class);
			filtersUpdatedCallback.run();
		}, CssStyles.FORCE_CAPTION);

		secondRowLayout.addComponent(btnResetFilters);

		lblNumberOfDuplicates = new Label("");
		lblNumberOfDuplicates.setId("numberOfDuplicates");
		CssStyles.style(
			lblNumberOfDuplicates,
			CssStyles.FORCE_CAPTION,
			CssStyles.LABEL_ROUNDED_CORNERS,
			CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT,
			CssStyles.LABEL_BOLD);
		secondRowLayout.addComponent(lblNumberOfDuplicates);
		secondRowLayout.setComponentAlignment(lblNumberOfDuplicates, Alignment.MIDDLE_RIGHT);
		secondRowLayout.setExpandRatio(lblNumberOfDuplicates, 1);

		queryDetailsBinder.bind(cbResultLimit, QueryDetails.RESULT_LIMIT);

		addComponent(secondRowLayout);
	}

	public void updateDuplicateCountLabel(int count) {
		lblNumberOfDuplicates.setValue(String.format(I18nProperties.getCaption(Captions.contactNumberOfDuplicatesDetected), count));
	}

	public void setFiltersUpdatedCallback(Runnable filtersUpdatedCallback) {
		this.filtersUpdatedCallback = filtersUpdatedCallback;
	}

	public void setIgnoreRegionCallback(Consumer<Boolean> ignoreRegionCallback) {
		this.ignoreRegionCallback = ignoreRegionCallback;
	}
}
