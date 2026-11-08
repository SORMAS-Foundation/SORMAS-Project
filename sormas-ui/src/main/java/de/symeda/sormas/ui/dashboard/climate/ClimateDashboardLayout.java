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

package de.symeda.sormas.ui.dashboard.climate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.Slider;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.climate.ClimateCaseClassificationFilter;
import de.symeda.sormas.api.climate.ClimateCaseCriteria;
import de.symeda.sormas.api.climate.ClimateCaseSeriesDto;
import de.symeda.sormas.api.climate.ClimateDashboardExportDto;
import de.symeda.sormas.api.climate.ClimateDistrictDto;
import de.symeda.sormas.api.climate.ClimateSeriesCriteria;
import de.symeda.sormas.api.climate.ClimateVariable;
import de.symeda.sormas.api.climate.ClimateWeeklySeriesDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * The Climate dashboard's content (decisions 119–121): the filters, applied with Apply; the display controls, which
 * redraw from the data already loaded; the notes and the chart.
 */
public class ClimateDashboardLayout extends VerticalLayout {

	private static final long serialVersionUID = -2716534416043418270L;

	/** The period on opening: the last 52 complete epi weeks (decision 120). */
	public static final int DEFAULT_WEEKS = 52;
	/** The longest period one request may cover. */
	public static final int MAX_YEARS = 5;
	public static final int MAX_WEEKS = 261;
	/** The longest climate shift, and the weeks of climate loaded before the period for it (decision 126). */
	public static final int MAX_LAG = 12;
	/** How many diseases each view shows at most (UI spec, Screen decisions). */
	public static final int PANELS_DISEASE_CAP = 4;
	public static final int HEATMAP_DISEASE_CAP = 10;
	/** How many years back the week choosers go. */
	static final int YEARS_OFFERED = 10;

	/** What the disease panels show. */
	public enum Measure {

		INCIDENCE(Captions.climateDashboardMeasureIncidence),
		COUNT(Captions.climateDashboardMeasureCount);

		private final String caption;

		Measure(String caption) {
			this.caption = caption;
		}

		@Override
		public String toString() {
			return I18nProperties.getCaption(caption);
		}
	}

	/** How the diseases are drawn (decision 129; overlaid lines are left out, decision 130). */
	public enum View {

		PANELS(Captions.climateDashboardViewPanels),
		HEATMAP(Captions.climateDashboardViewHeatmap);

		private final String caption;

		View(String caption) {
			this.caption = caption;
		}

		@Override
		public String toString() {
			return I18nProperties.getCaption(caption);
		}
	}

	/** The filters as last applied. */
	static final class Selection {

		private final RegionReferenceDto region;
		private final DistrictReferenceDto district;
		private final EpiWeek from;
		private final EpiWeek to;
		private final ClimateCaseClassificationFilter classification;

		Selection(RegionReferenceDto region, DistrictReferenceDto district, EpiWeek from, EpiWeek to, ClimateCaseClassificationFilter classification) {
			this.region = region;
			this.district = district;
			this.from = from;
			this.to = to;
			this.classification = classification;
		}

		RegionReferenceDto getRegion() {
			return region;
		}

		DistrictReferenceDto getDistrict() {
			return district;
		}

		EpiWeek getFrom() {
			return from;
		}

		EpiWeek getTo() {
			return to;
		}

		/**
		 * @return the place's name: the district, or "Guanacaste region"
		 */
		String getPlaceName() {
			return district != null ? district.getCaption() : region.getCaption();
		}
	}

	private final RegionReferenceDto userRegion;
	private final DistrictReferenceDto userDistrict;
	private final List<EpiWeek> offeredWeeks;
	private final List<Disease> offeredDiseases;

	private final ComboBox<RegionReferenceDto> regionFilter = new ComboBox<>();
	private final ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>();
	private final ComboBox<ClimateCaseClassificationFilter> classificationFilter = new ComboBox<>();
	private final ComboBox<EpiWeek> fromFilter = new ComboBox<>();
	private final ComboBox<EpiWeek> toFilter = new ComboBox<>();
	private final Label emptyCard = new Label();
	private final VerticalLayout content = new VerticalLayout();
	private final RadioButtonGroup<View> viewGroup = new RadioButtonGroup<>();
	private final CheckBoxGroup<Disease> diseaseGroup = new CheckBoxGroup<>();
	private final CheckBoxGroup<ClimateVariable> climateGroup = new CheckBoxGroup<>();
	private final ComboBox<ClimateVariable> shadeSelect = new ComboBox<>();
	private final RadioButtonGroup<Measure> measureGroup = new RadioButtonGroup<>();
	private final Slider lagSlider = new Slider(0, MAX_LAG);
	private final CssLayout controls = new CssLayout();
	private final VerticalLayout notes = new VerticalLayout();
	private final ClimateDashboardChart chart = new ClimateDashboardChart();

	private Selection selection;
	private ClimateWeeklySeriesDto climate;
	private final Map<Disease, ClimateCaseSeriesDto> cases = new EnumMap<>(Disease.class);
	/** Set while the controls are changed by code, so their listeners do not redraw. */
	private boolean updating;
	/** Told whether filters are applied, for example to enable the export. */
	private java.util.function.Consumer<Boolean> selectionListener = applied -> {
	};
	/** The measure the user chose; counts are shown instead while incidence is not available (decision 74). */
	private Measure preferredMeasure = Measure.INCIDENCE;

	/**
	 * @param user
	 *            the user, whose region and district are fixed by their jurisdiction (decision 119)
	 */
	public ClimateDashboardLayout(UserDto user, JurisdictionLevel jurisdictionLevel) {

		setMargin(true);
		setSpacing(true);
		setWidthFull();

		userRegion = user != null ? user.getRegion() : null;
		userDistrict = user != null && userRegion != null ? user.getDistrict() : null;
		offeredWeeks = offeredWeeks(new Date());
		Set<Disease> climateSensitive = new LinkedHashSet<>(FacadeProvider.getDiseaseConfigurationFacade().getAllClimateSensitiveDiseases());
		offeredDiseases = FacadeProvider.getDiseaseConfigurationFacade()
			.getAllDiseases(true, null, true)
			.stream()
			.filter(climateSensitive::contains)
			.sorted(Comparator.comparing(Disease::toString, String.CASE_INSENSITIVE_ORDER))
			.collect(Collectors.toList());

		addComponent(createFilters());
		if (isBelowDistrict(jurisdictionLevel) && userDistrict != null) {
			Label jurisdictionNote = new Label(I18nProperties.getString(Strings.climateDashboardJurisdiction));
			jurisdictionNote.addStyleName(CssStyles.VSPACE_TOP_NONE);
			addComponent(jurisdictionNote);
		}

		emptyCard.setValue(I18nProperties.getString(Strings.climateDashboardChooseRegion));
		emptyCard.setWidthFull();
		emptyCard.addStyleName(CssStyles.CLIMATE_DASHBOARD_EMPTY);
		addComponent(emptyCard);

		content.setMargin(false);
		content.setSpacing(true);
		content.setWidthFull();
		content.addComponent(createControls());
		notes.setMargin(false);
		notes.setSpacing(false);
		content.addComponents(notes, chart);
		addComponent(content);

		resetFilters();
		if (selectedRegion() != null) {
			apply();
		} else {
			showEmpty();
		}
	}

	private static boolean isBelowDistrict(JurisdictionLevel level) {
		return level == JurisdictionLevel.COMMUNITY
			|| level == JurisdictionLevel.HEALTH_FACILITY
			|| level == JurisdictionLevel.POINT_OF_ENTRY
			|| level == JurisdictionLevel.LABORATORY
			|| level == JurisdictionLevel.EXTERNAL_LABORATORY;
	}

	/**
	 * @return the epi weeks the choosers offer, newest first: the current week back to week 1 of {@link #YEARS_OFFERED}
	 *         years ago
	 */
	static List<EpiWeek> offeredWeeks(Date today) {
		EpiWeek current = DateHelper.getEpiWeek(today);
		EpiWeek first = new EpiWeek(current.getYear() - YEARS_OFFERED, 1);
		List<EpiWeek> weeks = new ArrayList<>(DateHelper.createEpiWeekListFromInterval(first, current));
		Collections.reverse(weeks);
		return weeks;
	}

	private HorizontalLayout createFilters() {

		regionFilter.setCaption(I18nProperties.getPrefixCaption(ClimateDistrictDto.I18N_PREFIX, ClimateDistrictDto.REGION));
		regionFilter.setRequiredIndicatorVisible(true);
		regionFilter.setItemCaptionGenerator(ReferenceDto::buildCaption);
		regionFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		regionFilter.setWidth(200, Unit.PIXELS);
		regionFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = e.getValue();
			districtFilter.setItems(region != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()) : Collections.emptyList());
			if (!updating) {
				districtFilter.clear();
			}
		});

		districtFilter.setCaption(I18nProperties.getPrefixCaption(ClimateDistrictDto.I18N_PREFIX, ClimateDistrictDto.DISTRICT));
		districtFilter.setItemCaptionGenerator(ReferenceDto::buildCaption);
		districtFilter.setWidth(200, Unit.PIXELS);

		classificationFilter.setCaption(I18nProperties.getCaption(Captions.climateDashboardClassification));
		classificationFilter.setItems(ClimateCaseClassificationFilter.values());
		classificationFilter.setEmptySelectionAllowed(false);
		classificationFilter.setWidth(220, Unit.PIXELS);

		for (ComboBox<EpiWeek> weekFilter : Arrays.asList(fromFilter, toFilter)) {
			weekFilter.setItems(offeredWeeks);
			weekFilter.setItemCaptionGenerator(EpiWeek::toShortString);
			weekFilter.setEmptySelectionAllowed(false);
			weekFilter.setWidth(140, Unit.PIXELS);
		}
		fromFilter.setCaption(I18nProperties.getCaption(Captions.climateDashboardFrom));
		toFilter.setCaption(I18nProperties.getCaption(Captions.climateDashboardTo));

		Button applyButton = ButtonHelper.createButton(Captions.actionApply, e -> apply(), ValoTheme.BUTTON_PRIMARY);
		Button resetButton = ButtonHelper.createButton(Captions.actionReset, e -> {
			resetFilters();
			apply();
		});

		HorizontalLayout filters = new HorizontalLayout(regionFilter, districtFilter, classificationFilter, fromFilter, toFilter, applyButton, resetButton);
		filters.setMargin(false);
		filters.setSpacing(true);
		filters.setComponentAlignment(applyButton, com.vaadin.ui.Alignment.BOTTOM_LEFT);
		filters.setComponentAlignment(resetButton, com.vaadin.ui.Alignment.BOTTOM_LEFT);
		return filters;
	}

	private CssLayout createControls() {


		viewGroup.setCaption(I18nProperties.getCaption(Captions.climateDashboardView));
		viewGroup.setItems(View.values());
		viewGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		viewGroup.setValue(View.PANELS);
		viewGroup.addValueChangeListener(e -> {
			if (e.getValue() == null) {
				return;
			}
			updating = true;
			try {
				// back to separate panels with more ticked than the cap: the first 4 stay
				List<Disease> kept = offeredDiseases.stream().filter(diseaseGroup.getValue()::contains).limit(diseaseCap()).collect(Collectors.toList());
				diseaseGroup.setValue(new LinkedHashSet<>(kept));
				diseaseGroup.getDataProvider().refreshAll();
			} finally {
				updating = false;
			}
			redrawUnlessUpdating();
		});

		diseaseGroup.setCaption(I18nProperties.getCaption(Captions.climateDashboardDiseases));
		diseaseGroup.setItems(offeredDiseases);
		diseaseGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		diseaseGroup.setItemEnabledProvider(disease -> diseaseGroup.getValue().contains(disease) || diseaseGroup.getValue().size() < diseaseCap());
		diseaseGroup.setVisible(!offeredDiseases.isEmpty());
		diseaseGroup.select(offeredDiseases.stream().limit(PANELS_DISEASE_CAP).toArray(Disease[]::new));
		diseaseGroup.addValueChangeListener(e -> {
			// the cap is shown by disabling the other boxes, so the provider is asked again after each change
			diseaseGroup.getDataProvider().refreshAll();
			if (!updating && selection != null) {
				loadCases(selectedDiseases());
				redraw();
			}
		});

		climateGroup.setCaption(I18nProperties.getCaption(Captions.climateDashboardClimate));
		climateGroup.setItems(ClimateVariable.values());
		climateGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		climateGroup.select(ClimateVariable.values());
		climateGroup.addValueChangeListener(e -> redrawUnlessUpdating());

		shadeSelect.setCaption(I18nProperties.getCaption(Captions.climateDashboardShade));
		shadeSelect.setItems(ClimateVariable.values());
		shadeSelect.setEmptySelectionCaption(I18nProperties.getCaption(Captions.climateDashboardShadeNone));
		shadeSelect.setTextInputAllowed(false);
		shadeSelect.setValue(ClimateVariable.RAINFALL);
		shadeSelect.setWidth(160, Unit.PIXELS);
		shadeSelect.addValueChangeListener(e -> redrawUnlessUpdating());

		measureGroup.setCaption(I18nProperties.getCaption(Captions.climateDashboardMeasure));
		measureGroup.setItems(Measure.values());
		measureGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		measureGroup.setValue(Measure.INCIDENCE);
		measureGroup.setItemEnabledProvider(measure -> measure != Measure.INCIDENCE || incidenceAvailable());
		measureGroup.addValueChangeListener(e -> {
			if (!updating && e.getValue() != null) {
				preferredMeasure = e.getValue();
				redraw();
			}
		});

		// the shift is display-only: a move redraws from the loaded series, with no new request (decision 126)
		lagSlider.setResolution(0);
		lagSlider.setValue(0d);
		lagSlider.setWidth(200, Unit.PIXELS);
		lagSlider.setCaption(lagCaption(0));
		lagSlider.addValueChangeListener(e -> {
			lagSlider.setCaption(lagCaption(getLag()));
			redrawUnlessUpdating();
		});

		controls.addStyleName(CssStyles.CLIMATE_DASHBOARD_CONTROLS);
		controls.setWidthFull();
		addControl(viewGroup);
		addControl(diseaseGroup);
		addControl(climateGroup);
		addControl(shadeSelect);
		addControl(measureGroup);
		addControl(lagSlider);
		return controls;
	}

	/**
	 * Adds a control to the wrapping row; each gets a box of its own, because a CssLayout puts a child's caption beside it
	 * as a separate element, which the flex row would otherwise place next to the control.
	 */
	void addControl(com.vaadin.ui.Component control) {
		CssLayout box = new CssLayout(control);
		controls.addComponent(box);
	}

	private void redrawUnlessUpdating() {
		if (!updating) {
			redraw();
		}
	}

	/**
	 * Sets the filters to their defaults: the user's region and district (fixed when their jurisdiction sets them), all
	 * but not a case, and the last 52 complete weeks.
	 */
	void resetFilters() {

		updating = true;
		try {
			regionFilter.setValue(userRegion);
			regionFilter.setEnabled(userRegion == null);
			districtFilter.setValue(userDistrict);
			districtFilter.setEnabled(userDistrict == null);
			classificationFilter.setValue(ClimateCaseClassificationFilter.ALL_BUT_NOT_A_CASE);
			// offeredWeeks.get(0) is the current week, not yet complete
			EpiWeek to = offeredWeeks.get(1);
			toFilter.setValue(to);
			fromFilter.setValue(offeredWeeks.get(DEFAULT_WEEKS));
		} finally {
			updating = false;
		}
	}

	/**
	 * Loads the climate and the cases for the filters, or says what is wrong with them.
	 */
	void apply() {

		RegionReferenceDto region = selectedRegion();
		if (region == null) {
			showEmpty();
			return;
		}
		EpiWeek from = fromFilter.getValue();
		EpiWeek to = toFilter.getValue();
		int fromIndex = offeredWeeks.indexOf(from);
		int toIndex = offeredWeeks.indexOf(to);
		if (fromIndex < toIndex) {
			warn(I18nProperties.getString(Strings.climateDashboardPeriodOrder));
			return;
		}
		if (fromIndex - toIndex + 1 > MAX_WEEKS) {
			warn(String.format(I18nProperties.getString(Strings.climateDashboardPeriodTooLong), MAX_YEARS));
			return;
		}

		selection = new Selection(region, districtFilter.getValue(), from, to, classificationFilter.getValue());
		// a region's districts tell the status line whether some of them already hold climate history
		climate = FacadeProvider.getClimateSeriesFacade()
			.getWeeklySeries(
				new ClimateSeriesCriteria().region(region)
					.district(selection.getDistrict())
					.weeks(from, to)
					.leadWeeks(MAX_LAG)
					.includeDistricts(selection.getDistrict() == null));
		cases.clear();
		loadCases(selectedDiseases());

		emptyCard.setVisible(false);
		content.setVisible(true);
		selectionListener.accept(true);
		redraw();
	}

	private static void warn(String message) {
		// no page in UI bean tests
		if (Page.getCurrent() != null) {
			Notification.show(message, Notification.Type.WARNING_MESSAGE);
		}
	}

	/**
	 * Loads the case series of the diseases that are not loaded yet; one request for all of them.
	 */
	private void loadCases(List<Disease> diseases) {

		List<Disease> missing = diseases.stream().filter(d -> !cases.containsKey(d)).collect(Collectors.toList());
		if (missing.isEmpty()) {
			return;
		}
		FacadeProvider.getClimateCaseFacade()
			.getCaseSeries(
				new ClimateCaseCriteria().region(selection.getRegion())
					.district(selection.getDistrict())
					.weeks(selection.getFrom(), selection.getTo())
					.diseases(missing)
					.classification(selection.classification))
			.forEach(series -> cases.put(series.getDisease(), series));
	}

	/**
	 * Called when the view is entered again: reloads with the filters as they were applied.
	 */
	public void refresh() {
		if (selection != null) {
			apply();
		}
	}

	private void showEmpty() {
		selection = null;
		climate = null;
		cases.clear();
		emptyCard.setVisible(true);
		content.setVisible(false);
		selectionListener.accept(false);
	}

	/**
	 * Draws the notes and the chart again from the data already loaded.
	 */
	void redraw() {

		if (selection == null) {
			return;
		}

		updating = true;
		try {
			// counts replace incidence when a district of the selection has no population data (decision 74)
			measureGroup.getDataProvider().refreshAll();
			measureGroup.setValue(preferredMeasure == Measure.INCIDENCE && incidenceAvailable() ? Measure.INCIDENCE : Measure.COUNT);
			boolean anyNormals = Arrays.stream(ClimateVariable.values()).anyMatch(climate::hasNormals);
			shadeSelect.setEnabled(anyNormals);
		} finally {
			updating = false;
		}

		notes.removeAllComponents();
		if (offeredDiseases.isEmpty()) {
			addNote(I18nProperties.getString(Strings.climateDashboardNoDiseases));
		} else if (diseaseGroup.getValue().size() >= diseaseCap()) {
			addNote(String.format(I18nProperties.getString(Strings.climateDashboardDiseaseCap), diseaseCap()));
		}
		List<DistrictReferenceDto> withoutPopulation = districtsWithoutPopulation();
		if (!withoutPopulation.isEmpty()) {
			addNote(
				String.format(
					I18nProperties.getString(Strings.climateDashboardCountsNoPopulation),
					withoutPopulation.stream().map(DistrictReferenceDto::getCaption).collect(Collectors.joining(", "))));
		}
		if (!shadeSelect.isEnabled()) {
			addNote(String.format(I18nProperties.getString(Strings.climateDashboardShadeNoNormals), climate.getMinHistoryYears()));
		}

		chart.update(selection, climate, selectedCaseSeries(), selectedVariables(), shadeSelect.getValue(), measureGroup.getValue(), getLag(), viewGroup.getValue() == View.HEATMAP);
	}

	void setSelectionListener(java.util.function.Consumer<Boolean> selectionListener) {
		this.selectionListener = selectionListener;
	}

	/**
	 * @return the export rows for the applied filters and the ticked diseases, or none before a region is applied
	 */
	List<ClimateDashboardExportDto> getExportRows() {
		if (selection == null) {
			return Collections.emptyList();
		}
		return FacadeProvider.getClimateCaseFacade()
			.getDashboardExportList(
				new ClimateCaseCriteria().region(selection.getRegion())
					.district(selection.getDistrict())
					.weeks(selection.getFrom(), selection.getTo())
					.diseases(selectedDiseases())
					.classification(selection.classification));
	}

	/**
	 * @return how many weeks earlier the climate panels show
	 */
	int getLag() {
		return lagSlider.getValue() != null ? lagSlider.getValue().intValue() : 0;
	}

	static String lagCaption(int lag) {
		return lag == 1
			? I18nProperties.getString(Strings.climateDashboardLagOne)
			: String.format(I18nProperties.getString(Strings.climateDashboardLag), lag);
	}

	private void addNote(String text) {
		Label note = new Label(text, ContentMode.TEXT);
		note.setWidthFull();
		notes.addComponent(note);
	}

	private int diseaseCap() {
		return viewGroup.getValue() == View.HEATMAP ? HEATMAP_DISEASE_CAP : PANELS_DISEASE_CAP;
	}

	private boolean incidenceAvailable() {
		return cases.isEmpty() || cases.values().stream().allMatch(ClimateCaseSeriesDto::isIncidenceAvailable);
	}

	private List<DistrictReferenceDto> districtsWithoutPopulation() {
		return cases.values().stream().findFirst().map(ClimateCaseSeriesDto::getDistrictsWithoutPopulation).orElse(Collections.emptyList());
	}

	private RegionReferenceDto selectedRegion() {
		return regionFilter.getValue();
	}

	/**
	 * @return the ticked diseases, in the order they are offered
	 */
	List<Disease> selectedDiseases() {
		return offeredDiseases.stream().filter(diseaseGroup.getValue()::contains).limit(diseaseCap()).collect(Collectors.toList());
	}

	private List<ClimateCaseSeriesDto> selectedCaseSeries() {
		return selectedDiseases().stream().map(cases::get).filter(java.util.Objects::nonNull).collect(Collectors.toList());
	}

	private Set<ClimateVariable> selectedVariables() {
		EnumSet<ClimateVariable> variables = EnumSet.noneOf(ClimateVariable.class);
		variables.addAll(climateGroup.getValue());
		return variables;
	}

	ComboBox<RegionReferenceDto> getRegionFilter() {
		return regionFilter;
	}

	ComboBox<DistrictReferenceDto> getDistrictFilter() {
		return districtFilter;
	}

	ComboBox<EpiWeek> getFromFilter() {
		return fromFilter;
	}

	ComboBox<EpiWeek> getToFilter() {
		return toFilter;
	}

	RadioButtonGroup<View> getViewGroup() {
		return viewGroup;
	}

	CheckBoxGroup<Disease> getDiseaseGroup() {
		return diseaseGroup;
	}

	CheckBoxGroup<ClimateVariable> getClimateGroup() {
		return climateGroup;
	}

	ComboBox<ClimateVariable> getShadeSelect() {
		return shadeSelect;
	}

	Slider getLagSlider() {
		return lagSlider;
	}

	RadioButtonGroup<Measure> getMeasureGroup() {
		return measureGroup;
	}

	Label getEmptyCard() {
		return emptyCard;
	}

	VerticalLayout getContent() {
		return content;
	}

	VerticalLayout getNotes() {
		return notes;
	}

	ClimateDashboardChart getChart() {
		return chart;
	}

	Selection getSelection() {
		return selection;
	}

	ClimateWeeklySeriesDto getClimate() {
		return climate;
	}

	Map<Disease, ClimateCaseSeriesDto> getCases() {
		return cases;
	}

	List<Disease> getOfferedDiseases() {
		return offeredDiseases;
	}
}
