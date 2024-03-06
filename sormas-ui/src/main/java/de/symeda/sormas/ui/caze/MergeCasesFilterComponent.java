package de.symeda.sormas.ui.caze;

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
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
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
public class MergeCasesFilterComponent extends VerticalLayout {

	// Layouts
	private HorizontalLayout firstRowLayout;
	private HorizontalLayout secondRowLayout;
	private HorizontalLayout thirdRowLayout;

	private DateField dfCreationDateFrom;
	private DateField dfCreationDateTo;
	private ComboBox<Disease> cbDisease;
	private TextField tfSearch;
	private TextField eventSearch;
	private TextField tfReportingUser;
	private CheckBox cbIgnoreRegion;
	private ComboBox<RegionReferenceDto> cbRegion;
	private ComboBox<DistrictReferenceDto> cbDistrict;
	private ComboBox<NewCaseDateType> cbNewCaseDateType;
	private DateField dfNewCaseDateFrom;
	private DateField dfNewCaseDateTo;
	private Button btnConfirmFilters;
	private Button btnResetFilters;

	private Binder<CaseCriteria> criteriaBinder = new Binder<>(CaseCriteria.class);
	private CaseCriteria criteria;
	private Binder<QueryDetails> queryDetailsBinder = new Binder<>(QueryDetails.class);
	private QueryDetails queryDetails;
	private Runnable filtersUpdatedCallback;
	private Consumer<Boolean> ignoreRegionCallback;

	private Label lblNumberOfDuplicates;
	private ComboBox<EntityRelevanceStatus> relevanceStatusFilter;

	public MergeCasesFilterComponent(CaseCriteria criteria, QueryDetails queryDetails) {

		setSpacing(false);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);

		addFirstRowLayout();
		if (UiUtil.disabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			addSecondRowLayout();
		}
		addThirdRowLayout();

		setValue(criteria, queryDetails);
	}

	public void setValue(CaseCriteria criteria, QueryDetails queryDetails) {
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
		dfCreationDateFrom.setId(CaseCriteria.CREATION_DATE_FROM);
		dfCreationDateFrom.setWidth(120, Unit.PIXELS);
		dfCreationDateFrom.setPlaceholder(I18nProperties.getString(Strings.promptCreationDateFrom));
		dfCreationDateFrom.setCaption(I18nProperties.getCaption(Captions.creationDate));
		criteriaBinder.forField(dfCreationDateFrom)
			.withConverter(new LocalDateToDateConverter(ZoneId.systemDefault()))
			.bind(CaseCriteria.CREATION_DATE_FROM);
		firstRowLayout.addComponent(dfCreationDateFrom);

		dfCreationDateTo = new DateField();
		dfCreationDateTo.setId(CaseCriteria.CREATION_DATE_TO);
		dfCreationDateTo.setWidth(120, Unit.PIXELS);
		CssStyles.style(dfCreationDateTo, CssStyles.FORCE_CAPTION);
		dfCreationDateTo.setPlaceholder(I18nProperties.getString(Strings.promptDateTo));
		criteriaBinder.forField(dfCreationDateTo)
			.withConverter(new LocalDateToDateConverter(ZoneId.systemDefault()))
			.bind(CaseCriteria.CREATION_DATE_TO);
		firstRowLayout.addComponent(dfCreationDateTo);

		cbDisease = new ComboBox<>();
		cbDisease.setId(CaseDataDto.DISEASE);
		cbDisease.setWidth(200, Unit.PIXELS);
		CssStyles.style(cbDisease, CssStyles.FORCE_CAPTION);
		cbDisease.setPlaceholder(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
		cbDisease.setItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		criteriaBinder.bind(cbDisease, CaseDataDto.DISEASE);
		firstRowLayout.addComponent(cbDisease);

		tfSearch = new TextField();
		tfSearch.setId(CaseCriteria.CASE_LIKE);
		tfSearch.setWidth(200, Unit.PIXELS);
		CssStyles.style(tfSearch, CssStyles.FORCE_CAPTION);
		tfSearch.setPlaceholder(I18nProperties.getString(Strings.promptCasesSearchField));
		criteriaBinder.bind(tfSearch, CaseCriteria.CASE_LIKE);
		firstRowLayout.addComponent(tfSearch);

		// Temporarily disabled because #9054 has introduced CaseService.hasAnyToManyJoin which leads to an error 
		// when trying to search for duplicates by any property related to events (documented in #11712)
//		eventSearch = new TextField();
//		eventSearch.setId(CaseCriteria.EVENT_LIKE);
//		eventSearch.setWidth(200, Unit.PIXELS);
//		CssStyles.style(eventSearch, CssStyles.FORCE_CAPTION);
//		eventSearch.setPlaceholder(I18nProperties.getString(Strings.promptCaseOrContactEventSearchField));
//		criteriaBinder.bind(eventSearch, CaseCriteria.EVENT_LIKE);
//		firstRowLayout.addComponent(eventSearch);

		tfReportingUser = new TextField();
		tfReportingUser.setId(CaseCriteria.REPORTING_USER_LIKE);
		tfReportingUser.setWidth(200, Unit.PIXELS);
		CssStyles.style(tfReportingUser, CssStyles.FORCE_CAPTION);
		tfReportingUser.setPlaceholder(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORTING_USER));
		criteriaBinder.bind(tfReportingUser, CaseCriteria.REPORTING_USER_LIKE);
		firstRowLayout.addComponent(tfReportingUser);
		firstRowLayout.setExpandRatio(tfReportingUser, 1);

		addComponent(firstRowLayout);
	}

	private void addSecondRowLayout() {

		secondRowLayout = new HorizontalLayout();
		secondRowLayout.setMargin(false);
		secondRowLayout.setWidth(100, Unit.PERCENTAGE);

		cbRegion = new ComboBox<>();
		cbDistrict = new ComboBox<>();
		cbRegion.setItemCaptionGenerator(ReferenceDto::buildCaption);
		cbDistrict.setItemCaptionGenerator(ReferenceDto::buildCaption);

		cbRegion.setId(CaseDataDto.REGION);
		cbRegion.setWidth(200, Unit.PIXELS);
		CssStyles.style(cbRegion, CssStyles.FORCE_CAPTION);
		cbRegion.setPlaceholder(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
		cbRegion.setItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		criteriaBinder.bind(cbRegion, CaseDataDto.REGION);
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

		cbDistrict.setId(CaseDataDto.DISTRICT);
		cbDistrict.setWidth(200, Unit.PIXELS);
		CssStyles.style(cbDistrict, CssStyles.FORCE_CAPTION);
		cbDistrict.setPlaceholder(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
		criteriaBinder.bind(cbDistrict, CaseDataDto.DISTRICT);
		secondRowLayout.addComponent(cbDistrict);

		cbIgnoreRegion = new CheckBox();
		cbIgnoreRegion.setId(Captions.caseFilterWithDifferentRegion);
		CssStyles.style(cbIgnoreRegion, CssStyles.CHECKBOX_FILTER_INLINE);
		cbIgnoreRegion.setCaption(I18nProperties.getCaption(Captions.caseFilterWithDifferentRegion));
		cbIgnoreRegion.addValueChangeListener(e -> ignoreRegionCallback.accept(e.getValue()));
		secondRowLayout.addComponent(cbIgnoreRegion);
		secondRowLayout.setComponentAlignment(cbIgnoreRegion, Alignment.MIDDLE_LEFT);
		secondRowLayout.setExpandRatio(cbIgnoreRegion, 1);

		addComponent(secondRowLayout);
	}

	private void addThirdRowLayout() {
		thirdRowLayout = new HorizontalLayout();
		thirdRowLayout.setSpacing(true);
		thirdRowLayout.setMargin(false);
		thirdRowLayout.setWidth(100, Unit.PERCENTAGE);

		cbNewCaseDateType = new ComboBox<>();
		dfNewCaseDateFrom = new DateField();
		dfNewCaseDateTo = new DateField();
		ComboBox<Integer> cbResultLimit = new ComboBox<>();

		cbNewCaseDateType.setId(CaseCriteria.NEW_CASE_DATE_TYPE);
		cbNewCaseDateType.setWidth(200, Unit.PIXELS);
		cbNewCaseDateType.setPlaceholder(I18nProperties.getString(Strings.promptNewCaseDateType));
		cbNewCaseDateType.setCaption(I18nProperties.getCaption(Captions.caseNewCaseDate));
		cbNewCaseDateType.setItems(NewCaseDateType.values());
		criteriaBinder.bind(cbNewCaseDateType, CaseCriteria.NEW_CASE_DATE_TYPE);
		cbNewCaseDateType.addValueChangeListener(event -> {
			dfNewCaseDateFrom.setEnabled(event.getValue() != null);
			dfNewCaseDateTo.setEnabled(event.getValue() != null);
		});
		thirdRowLayout.addComponent(cbNewCaseDateType);

		dfNewCaseDateFrom.setId(CaseCriteria.NEW_CASE_DATE_FROM);
		dfNewCaseDateFrom.setWidth(120, Unit.PIXELS);
		CssStyles.style(dfNewCaseDateFrom, CssStyles.FORCE_CAPTION);
		dfNewCaseDateFrom.setPlaceholder(I18nProperties.getString(Strings.promptCasesDateFrom));
		criteriaBinder.forField(dfNewCaseDateFrom)
			.withConverter(new LocalDateToDateConverter(ZoneId.systemDefault()))
			.bind(CaseCriteria.NEW_CASE_DATE_FROM);
		dfNewCaseDateFrom.setEnabled(false);
		thirdRowLayout.addComponent(dfNewCaseDateFrom);

		dfNewCaseDateTo.setId(CaseCriteria.NEW_CASE_DATE_TO);
		dfNewCaseDateTo.setWidth(120, Unit.PIXELS);
		CssStyles.style(dfNewCaseDateTo, CssStyles.FORCE_CAPTION);
		dfNewCaseDateTo.setPlaceholder(I18nProperties.getString(Strings.promptDateTo));
		criteriaBinder.forField(dfNewCaseDateTo)
			.withConverter(new LocalDateToDateConverter(ZoneId.systemDefault()))
			.bind(CaseCriteria.NEW_CASE_DATE_TO);
		dfNewCaseDateTo.setEnabled(false);
		thirdRowLayout.addComponent(dfNewCaseDateTo);

		cbResultLimit.setId(QueryDetails.RESULT_LIMIT);
		cbResultLimit.setWidth(200, Unit.PIXELS);
		cbResultLimit.setCaption(I18nProperties.getCaption(Captions.QueryDetails_resultLimit));
		cbResultLimit.setItems(50, 100, 500, 1000);
		cbResultLimit.setEmptySelectionAllowed(false);
		queryDetailsBinder.bind(cbResultLimit, QueryDetails.RESULT_LIMIT);
		thirdRowLayout.addComponent(cbResultLimit);

		btnConfirmFilters = ButtonHelper.createButton(Captions.actionConfirmFilters, event -> {
			try {
				criteriaBinder.writeBean(criteria);
				queryDetailsBinder.writeBean(queryDetails);
				filtersUpdatedCallback.run();
			} catch (ValidationException e) {
				// No validation needed
			}
		}, CssStyles.FORCE_CAPTION, ValoTheme.BUTTON_PRIMARY);

		thirdRowLayout.addComponent(btnConfirmFilters);

		btnResetFilters = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(MergeCasesView.class).remove(CaseCriteria.class);
			filtersUpdatedCallback.run();
		}, CssStyles.FORCE_CAPTION);

		thirdRowLayout.addComponent(btnResetFilters);

		HorizontalLayout relevanceStatusFilterLayout = new HorizontalLayout();

		lblNumberOfDuplicates = new Label("");
		lblNumberOfDuplicates.setId("numberOfDuplicates");
		CssStyles.style(
			lblNumberOfDuplicates,
			CssStyles.FORCE_CAPTION,
			CssStyles.LABEL_ROUNDED_CORNERS,
			CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT,
			CssStyles.LABEL_BOLD);
		relevanceStatusFilterLayout.addComponent(lblNumberOfDuplicates);
		relevanceStatusFilterLayout.setComponentAlignment(lblNumberOfDuplicates, Alignment.BOTTOM_LEFT);

		relevanceStatusFilter = new ComboBox<>();
		relevanceStatusFilter.setId(CaseCriteria.ENTITY_RELEVANCE_STATUS);
		relevanceStatusFilter.setWidth(210, Unit.PIXELS);
		relevanceStatusFilter.setEnabled(true);
		relevanceStatusFilter.setEmptySelectionAllowed(false);
		relevanceStatusFilter.setItems(EntityRelevanceStatus.getAllExceptDeleted());
		relevanceStatusFilter.setItemCaptionGenerator(item -> {
			switch (item) {
			case ACTIVE:
				return I18nProperties.getCaption(Captions.caseActiveCases);
			case ARCHIVED:
				return I18nProperties.getCaption(Captions.caseArchivedCases);
			case ACTIVE_AND_ARCHIVED:
				return I18nProperties.getCaption(Captions.caseAllActiveAndArchivedCases);
			default:
				return item.toString();
			}
		});

		criteriaBinder.bind(relevanceStatusFilter, CaseCriteria.ENTITY_RELEVANCE_STATUS);
		relevanceStatusFilterLayout.addComponent(relevanceStatusFilter);
		relevanceStatusFilterLayout.setComponentAlignment(relevanceStatusFilter, Alignment.BOTTOM_LEFT);

		thirdRowLayout.addComponent(relevanceStatusFilterLayout);
		thirdRowLayout.setComponentAlignment(relevanceStatusFilterLayout, Alignment.BOTTOM_RIGHT);
		thirdRowLayout.setExpandRatio(relevanceStatusFilterLayout, 1);

		addComponent(thirdRowLayout);
	}

	public void updateDuplicateCountLabel(int count) {
		lblNumberOfDuplicates.setValue(String.format(I18nProperties.getCaption(Captions.caseNumberOfDuplicatesDetected), count));
	}

	public void setFiltersUpdatedCallback(Runnable filtersUpdatedCallback) {
		this.filtersUpdatedCallback = filtersUpdatedCallback;
	}

	public void setIgnoreRegionCallback(Consumer<Boolean> ignoreRegionCallback) {
		this.ignoreRegionCallback = ignoreRegionCallback;
	}
}
