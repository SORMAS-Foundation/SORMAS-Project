package de.symeda.sormas.ui.environment;

import java.util.Date;
import java.util.function.Consumer;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EnvironmentSelectionField extends CustomField<EnvironmentIndexDto> {

	public static final String SELECT_ENVIRONMENT = "selectEnvironment";
	public static final String CREATE_ENVIRONMENT = "createEnvironment";

	private VerticalLayout mainLayout;
	private EnvironmentSelectionGrid environmentGrid;
	private final String infoPickOrCreateEnvironment;

	private RadioButtonGroup<String> rbSelectEnvironment;
	private RadioButtonGroup<String> rbCreateEnvironment;
	private Consumer<Boolean> selectionChangeCallback;
	private final TextField searchField;
	private final EnvironmentCriteria criteria;
	private final boolean allowCreation;
	private Consumer<EnvironmentCriteria> setDefaultFilters;
	EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter;
	Button applyButton;
	HorizontalLayout weekAndDateFilterLayout;

	public EnvironmentSelectionField(EnvironmentReferenceDto referenceDto) {
		this.infoPickOrCreateEnvironment = I18nProperties.getString(Strings.infoPickOrCreateEnvironmentForEvent);
		this.searchField = new TextField();
		this.criteria = new EnvironmentCriteria();
		criteria.setEvent(referenceDto.getEvent());
		this.allowCreation = true;
		this.weekAndDateFilterLayout = buildWeekAndDateFilter();
		initializeGrid();
	}

	private void addInfoComponent() {
		mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoPickOrCreateEnvironment));
	}

	public HorizontalLayout createFilterBar() {

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setMargin(false);
		filterLayout.setSizeUndefined();

		searchField.setId("search");
		searchField.setWidth(200, Unit.PIXELS);

		searchField.setCaption(I18nProperties.getString(Strings.promptEnvironmentSearchField));

		searchField.addValueChangeListener(e -> updateGrid(e.getValue()));

		filterLayout.addComponent(searchField);

		return filterLayout;
	}

	private void updateGrid(String freeText) {
		criteria.setFreeText(freeText);

		environmentGrid.setCriteria(criteria);
		environmentGrid.getSelectedItems();
	}

	public void initializeGrid() {

		environmentGrid = new EnvironmentSelectionGrid(criteria);
		environmentGrid.addSelectionListener(e -> {

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getAllSelectedItems().isEmpty());
			}
		});
	}

	@Override
	protected Component initContent() {
		mainLayout = new VerticalLayout();
		mainLayout.setMargin(false);
		mainLayout.setSpacing(true);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		addInfoComponent();

		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterLayout.addComponent(createFilterBar());
		filterLayout.addComponent(weekAndDateFilterLayout);

		mainLayout.addComponent(filterLayout);
		addSelectEnvironmentRadioGroup();
		mainLayout.addComponent(environmentGrid);
		addCreateEnvironmentRadioGroup();

		if (rbSelectEnvironment != null) {
			rbSelectEnvironment.setValue(SELECT_ENVIRONMENT);
		}

		return mainLayout;

	}

	public HorizontalLayout buildWeekAndDateFilter() {

		applyButton = ButtonHelper.createButton(Captions.actionApplyDateFilter, null);

		weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, null);

		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentDateTo));

		applyButton.addClickListener(e -> {

			DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			Date toDate = null;
			Date fromDate = null;
			if (dateFilterOption == DateFilterOption.DATE) {
				if (weekAndDateFilter.getDateToFilter().getValue() != null) {
					toDate = DateHelper.getEndOfDay(weekAndDateFilter.getDateToFilter().getValue());
				}
				if (weekAndDateFilter.getDateFromFilter().getValue() != null) {
					fromDate = DateHelper.getStartOfDay(weekAndDateFilter.getDateFromFilter().getValue());
				}
			} else {
				fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
				toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
			}

			if (setDefaultFilters != null) {
				setDefaultFilters.accept(criteria);
				fromDate = fromDate == null ? criteria.getReportDateFrom() : fromDate;
				toDate = toDate == null ? criteria.getReportDateTo() : toDate;
			}

			applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
			criteria.reportDateBetween(fromDate, toDate, dateFilterOption);
			environmentGrid.setCriteria(criteria);
			environmentGrid.getSelectedItems();
		});

		Button resetButton = ButtonHelper.createButton(Captions.caseEventsResetDateFilter, null);

		resetButton.addClickListener(e -> {

			weekAndDateFilter.getDateFromFilter().setValue(null);
			weekAndDateFilter.getDateToFilter().setValue(null);
			weekAndDateFilter.getWeekFromFilter().setValue(null);
			weekAndDateFilter.getWeekToFilter().setValue(null);

			criteria.freeText(null);
			if (setDefaultFilters != null) {
				setDefaultFilters.accept(criteria);
			} else {
				criteria.reportDateBetween(null, null, null);
			}

			environmentGrid.setCriteria(criteria);
			environmentGrid.getSelectedItems();
		});

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);
		dateFilterRowLayout.addComponent(applyButton);
		dateFilterRowLayout.addComponent(resetButton);

		return dateFilterRowLayout;
	}

	private void addCreateEnvironmentRadioGroup() {
		if (!allowCreation) {
			return;
		}

		rbCreateEnvironment = new RadioButtonGroup<>();
		rbCreateEnvironment.setItems(CREATE_ENVIRONMENT);
		rbCreateEnvironment.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.eventNewEnvironment));
		rbCreateEnvironment.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbSelectEnvironment.setValue(null);
				environmentGrid.setEnabled(false);
				environmentGrid.deselectAll();
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		mainLayout.addComponent(rbCreateEnvironment);
	}

	private void addSelectEnvironmentRadioGroup() {
		// No need to display the select radio if creation is not allowed
		if (!allowCreation) {
			return;
		}
		rbSelectEnvironment = new RadioButtonGroup<>();
		rbSelectEnvironment.setItems(SELECT_ENVIRONMENT);
		rbSelectEnvironment.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.environmentSelect));
		CssStyles.style(rbSelectEnvironment, CssStyles.VSPACE_NONE);
		rbSelectEnvironment.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbCreateEnvironment.setValue(null);
				environmentGrid.setEnabled(true);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(!environmentGrid.getSelectedItems().isEmpty());

				}
			}
		});

		mainLayout.addComponent(rbSelectEnvironment);
	}

	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}

	@Override
	protected void doSetValue(EnvironmentIndexDto newValue) {
		if (rbSelectEnvironment != null) {
			rbSelectEnvironment.setValue(SELECT_ENVIRONMENT);
		}

		if (newValue != null) {
			environmentGrid.select(newValue);
		}
	}

	@Override
	public EnvironmentIndexDto getValue() {
		if (environmentGrid != null) {
			return environmentGrid.getSelectedItems().stream().findFirst().orElse(null);
		}

		return null;
	}
}
