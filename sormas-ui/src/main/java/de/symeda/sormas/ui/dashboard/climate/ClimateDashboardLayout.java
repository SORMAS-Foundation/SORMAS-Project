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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
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
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

/**
 * The Climate dashboard's content (decisions 119–121 and 135): the chart in 8 columns on the left; on the right, in 4
 * columns, the "Place and period" card, whose filters take effect with Apply, and the "Display" card, whose controls
 * redraw from the data already loaded, each with its note under it. On narrow screens the cards come first.
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

	private static final String MAIN_LOC = "main";
	private static final String SIDE_LOC = "side";

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
	private final Label jurisdictionNote = note(Strings.climateDashboardJurisdiction);
	private final Label diseaseNote = note(null);
	private final Label shadeNote = note(null);
	private final Label measureNote = note(null);
	private final SideComponent filterCard = new SideComponent(I18nProperties.getCaption(Captions.climateDashboardPlacePeriodDiseases));
	private final SideComponent displayCard = new SideComponent(I18nProperties.getCaption(Captions.climateDashboardDisplay));
	private final ClimateDashboardChart chart = new ClimateDashboardChart();

	private Selection selection;
	private ClimateWeeklySeriesDto climate;
	private final Map<Disease, ClimateCaseSeriesDto> cases = new EnumMap<>(Disease.class);
	/** Set while the controls are changed by code, so their listeners do not redraw. */
	private boolean updating;
	/** The measure the user chose; counts are shown instead while incidence is not available (decision 74). */
	private Measure preferredMeasure = Measure.INCIDENCE;
	/** Removes the window resize listener when the layout is detached. */
	private com.vaadin.shared.Registration resizeRegistration;
	/** The window height the chart was last drawn for. */
	private int drawnForHeight;

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

		// 8 columns of chart and 4 of cards, as on the environment Climate data tab, capped at 1600 px instead of its 1280 px
		// and left-aligned like it (climate.scss, decision 140), so a 5-year chart has room without stretching on large
		// screens; below the large-screen width the cards come first (climate.scss), so Apply is not under the chart
		CustomLayout split = new CustomLayout();
		split.setWidthFull();
		split.setTemplateContents(
			LayoutUtil.fluidRowCss(
				CssStyles.CLIMATE_DASHBOARD_SPLIT,
				LayoutUtil.fluidColumnLoc(8, 0, 12, 0, MAIN_LOC),
				new LayoutUtil.FluidColumn(CssStyles.CLIMATE_DASHBOARD_SIDE, 4, 0, 12, 0, SIDE_LOC, null)));
		addComponent(split);

		emptyCard.setValue(I18nProperties.getString(Strings.climateDashboardChooseRegion));
		emptyCard.setWidthFull();
		emptyCard.addStyleName(CssStyles.CLIMATE_DASHBOARD_EMPTY);
		content.setMargin(false);
		content.setSpacing(false);
		content.setWidthFull();
		content.addComponent(chart);
		VerticalLayout main = new VerticalLayout(emptyCard, content);
		main.setMargin(false);
		main.setWidthFull();
		split.addComponent(main, MAIN_LOC);

		jurisdictionNote.setVisible(isBelowDistrict(jurisdictionLevel) && userDistrict != null);
		VerticalLayout side = new VerticalLayout(createFilters(), createControls());
		side.setMargin(false);
		side.setSpacing(false);
		side.setWidthFull();
		split.addComponent(side, SIDE_LOC);

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

	/**
	 * @return the filters and the diseases in their card, applied with Apply (decisions 135, 137)
	 */
	private SideComponent createFilters() {

		regionFilter.setCaption(I18nProperties.getPrefixCaption(ClimateDistrictDto.I18N_PREFIX, ClimateDistrictDto.REGION));
		regionFilter.setRequiredIndicatorVisible(true);
		regionFilter.setItemCaptionGenerator(ReferenceDto::buildCaption);
		regionFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		regionFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = e.getValue();
			districtFilter.setItems(region != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()) : Collections.emptyList());
			if (!updating) {
				districtFilter.clear();
			}
		});

		districtFilter.setCaption(I18nProperties.getPrefixCaption(ClimateDistrictDto.I18N_PREFIX, ClimateDistrictDto.DISTRICT));
		districtFilter.setItemCaptionGenerator(ReferenceDto::buildCaption);

		classificationFilter.setCaption(I18nProperties.getCaption(Captions.climateDashboardClassification));
		classificationFilter.setItems(ClimateCaseClassificationFilter.values());
		classificationFilter.setEmptySelectionAllowed(false);

		for (ComboBox<EpiWeek> weekFilter : Arrays.asList(fromFilter, toFilter)) {
			weekFilter.setItems(offeredWeeks);
			weekFilter.setItemCaptionGenerator(EpiWeek::toShortString);
			weekFilter.setEmptySelectionAllowed(false);
		}
		fromFilter.setCaption(I18nProperties.getCaption(Captions.climateDashboardFrom));
		toFilter.setCaption(I18nProperties.getCaption(Captions.climateDashboardTo));

		Button applyButton = ButtonHelper.createButton(Captions.actionApply, e -> apply(), ValoTheme.BUTTON_PRIMARY);
		Button resetButton = ButtonHelper.createButton(Captions.actionReset, e -> {
			resetFilters();
			apply();
		});

		for (ComboBox<?> filter : Arrays.asList(regionFilter, districtFilter, classificationFilter, fromFilter, toFilter)) {
			filter.setWidthFull();
			// no bottom margin under each field: the card's spacing is enough (decision 136)
			filter.addStyleName(CssStyles.VSPACE_NONE);
		}
		HorizontalLayout weeks = new HorizontalLayout(fromFilter, toFilter);
		weeks.setMargin(false);
		weeks.setWidthFull();
		HorizontalLayout buttons = new HorizontalLayout(applyButton, resetButton);
		buttons.setMargin(false);

		// the diseases are chosen with the place and period, none ticked at first; the chart needs at least one (decision 137)
		diseaseGroup.setCaption(I18nProperties.getCaption(Captions.climateDashboardDiseases));
		diseaseGroup.setItems(offeredDiseases);
		// one checkbox per line (decision 136)
		diseaseGroup.setItemEnabledProvider(disease -> diseaseGroup.getValue().contains(disease) || diseaseGroup.getValue().size() < diseaseCap());
		// kept with its caption when no disease is marked, so the note sits where the checkboxes would be (decision 138)
		diseaseGroup.addStyleName(CssStyles.VSPACE_NONE);
		diseaseGroup.addValueChangeListener(e -> {
			// the cap is shown by disabling the other boxes, so the provider is asked again after each change
			diseaseGroup.getDataProvider().refreshAll();
			updateDiseaseNote();
			// once applied, a tick takes effect at once, without Apply
			if (!updating && selection != null) {
				loadCases(selectedDiseases());
				redraw();
			}
		});
		updateDiseaseNote();

		filterCard.addStyleNames(CssStyles.SIDE_COMPONENT, CssStyles.SIDE_COMPONENT_FLUID);
		filterCard.addComponent(stack(regionFilter, districtFilter, classificationFilter, weeks, withNote(diseaseGroup, diseaseNote), buttons, jurisdictionNote));
		return filterCard;
	}

	/**
	 * Says under the diseases why none can be ticked, or that the view's cap is reached.
	 */
	private void updateDiseaseNote() {
		setNote(
			diseaseNote,
			offeredDiseases.isEmpty()
				? I18nProperties.getString(Strings.climateDashboardNoDiseases)
				: diseaseGroup.getValue().size() >= diseaseCap()
					? String.format(I18nProperties.getString(Strings.climateDashboardDiseaseCap), diseaseCap())
					: null);
	}

	/**
	 * @return the display controls in their card, each note under the control it explains (decision 135); the card is
	 *         shown once filters are applied, since before that its controls change nothing
	 */
	private SideComponent createControls() {

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
		lagSlider.setWidthFull();
		lagSlider.setCaption(lagCaption(0));
		lagSlider.addValueChangeListener(e -> {
			lagSlider.setCaption(lagCaption(getLag()));
			redrawUnlessUpdating();
		});

		shadeSelect.setWidthFull();
		for (Component control : Arrays.asList(viewGroup, climateGroup, shadeSelect, measureGroup)) {
			control.addStyleName(CssStyles.VSPACE_NONE);
		}
		displayCard.addStyleNames(CssStyles.SIDE_COMPONENT, CssStyles.SIDE_COMPONENT_FLUID);
		displayCard.addComponent(stack(viewGroup, climateGroup, withNote(shadeSelect, shadeNote), withNote(measureGroup, measureNote), lagSlider));
		return displayCard;
	}

	/**
	 * @return the components one under the other, with the usual spacing
	 */
	private static VerticalLayout stack(Component... components) {
		VerticalLayout stack = new VerticalLayout(components);
		stack.setMargin(false);
		stack.setWidthFull();
		return stack;
	}

	/**
	 * @return the control with its note right under it
	 */
	private static VerticalLayout withNote(Component control, Label note) {
		VerticalLayout box = new VerticalLayout(control, note);
		box.setMargin(false);
		box.setSpacing(false);
		box.setWidthFull();
		return box;
	}

	/**
	 * @return a note for under a control: the given text, or hidden until a text is set
	 */
	private static Label note(String text) {
		Label note = new Label(text != null ? I18nProperties.getString(text) : null, ContentMode.TEXT);
		note.setWidthFull();
		note.addStyleName(CssStyles.CLIMATE_DASHBOARD_NOTE);
		note.setVisible(text != null);
		return note;
	}

	private static void setNote(Label note, String text) {
		note.setValue(text);
		note.setVisible(text != null);
	}

	private void redrawUnlessUpdating() {
		if (!updating) {
			redraw();
		}
	}

	/**
	 * Sets the filters to their defaults: the user's region and district (fixed when their jurisdiction sets them), confirmed
	 * cases only, and the last 52 complete weeks.
	 */
	void resetFilters() {

		updating = true;
		try {
			regionFilter.setValue(userRegion);
			regionFilter.setEnabled(userRegion == null);
			districtFilter.setValue(userDistrict);
			districtFilter.setEnabled(userDistrict == null);
			// confirmed cases by default (decision 145)
			classificationFilter.setValue(ClimateCaseClassificationFilter.CONFIRMED);
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
		showChart(false, Strings.climateDashboardChooseRegion);
	}

	/**
	 * Shows the chart with its display controls and the export link under it, or instead the card saying what is missing.
	 */
	private void showChart(boolean shown, String missing) {
		emptyCard.setValue(missing != null ? I18nProperties.getString(missing) : null);
		emptyCard.setVisible(!shown);
		content.setVisible(shown);
		displayCard.setVisible(shown);
	}

	/**
	 * Draws the notes and the chart again from the data already loaded.
	 */
	void redraw() {

		if (selection == null) {
			return;
		}
		// no chart, display controls or export until a disease is ticked (decision 137)
		if (selectedDiseases().isEmpty()) {
			showChart(false, offeredDiseases.isEmpty() ? Strings.climateDashboardNoDiseases : Strings.climateDashboardChooseDisease);
			return;
		}
		showChart(true, null);

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

		// each note under the control it explains (decision 135)
		updateDiseaseNote();
		List<DistrictReferenceDto> withoutPopulation = districtsWithoutPopulation();
		setNote(
			measureNote,
			withoutPopulation.isEmpty()
				? null
				: String.format(
					I18nProperties.getString(Strings.climateDashboardCountsNoPopulation),
					withoutPopulation.stream().map(DistrictReferenceDto::getCaption).collect(Collectors.joining(", "))));
		setNote(
			shadeNote,
			shadeSelect.isEnabled() ? null : String.format(I18nProperties.getString(Strings.climateDashboardShadeNoNormals), climate.getMinHistoryYears()));

		drawnForHeight = windowHeight();
		chart.update(selection, climate, selectedCaseSeries(), selectedVariables(), shadeSelect.getValue(), measureGroup.getValue(), getLag(), viewGroup.getValue() == View.HEATMAP, drawnForHeight);
	}

	/**
	 * @return the browser window's height, which the chart fits (decision 136), or 0 when there is no page (tests)
	 */
	private static int windowHeight() {
		return Page.getCurrent() != null ? Page.getCurrent().getBrowserWindowHeight() : 0;
	}

	@Override
	public void attach() {
		super.attach();
		// the chart fits the window, so it is drawn again when the window's height changes
		if (Page.getCurrent() != null) {
			resizeRegistration = Page.getCurrent().addBrowserWindowResizeListener(e -> {
				if (e.getHeight() != drawnForHeight) {
					redraw();
				}
			});
		}
	}

	@Override
	public void detach() {
		if (resizeRegistration != null) {
			resizeRegistration.remove();
			resizeRegistration = null;
		}
		super.detach();
	}

	/**
	 * Adds a component under the chart, right-aligned, shown and hidden with the chart (the export link, decisions 139 and 142).
	 */
	void addBelowChart(Component component) {
		component.addStyleName(CssStyles.VSPACE_TOP_4);
		content.addComponent(component);
		content.setComponentAlignment(component, Alignment.MIDDLE_RIGHT);
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

	ComboBox<ClimateCaseClassificationFilter> getClassificationFilter() {
		return classificationFilter;
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

	SideComponent getFilterCard() {
		return filterCard;
	}

	SideComponent getDisplayCard() {
		return displayCard;
	}

	Label getJurisdictionNote() {
		return jurisdictionNote;
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
