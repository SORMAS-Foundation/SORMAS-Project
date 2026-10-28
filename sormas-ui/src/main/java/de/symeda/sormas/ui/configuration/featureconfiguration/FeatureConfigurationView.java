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

package de.symeda.sormas.ui.configuration.featureconfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Administrator view for the server-level feature flags that have no region/disease scope.
 * Backed by the typed {@link FeatureType} enum and the feature-configuration facade.
 * <p>
 * Each feature is one read-only grid row: whether it is enabled, its caption and its current property values.
 * Clicking the edit icon in the first column opens a popup in which the feature can be enabled or disabled and its
 * property values can be changed.
 */
public class FeatureConfigurationView extends AbstractConfigurationView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/featureConfiguration";

	/**
	 * Shown in a cell when there is nothing to show, e.g. for a feature without properties.
	 */
	private static final String EMPTY_CELL = "-";

	private static final int EDIT_COLUMN_WIDTH = 60;

	/**
	 * Width of the edit popup in pixels: wide enough for the full feature caption as title and for a property table with
	 * the entity type and two property columns side by side.
	 */
	private static final int EDIT_WINDOW_WIDTH = 640;

	/**
	 * Width of the yes/no combo boxes and the text fields in the edit popup, in pixels.
	 */
	private static final int EDIT_FIELD_WIDTH = 150;

	/**
	 * Width of the label column in the edit popup, in pixels. All labels (and the entity type column) share it, so the
	 * "Enabled" field and all property fields below it start at the same position.
	 */
	private static final int EDIT_CAPTION_WIDTH = 280;

	/**
	 * The server features exposed for administrator toggling in this view, in {@link FeatureType} declaration order.
	 * Taken from {@link FeatureType#getAllServerFeatures()}, so every feature declared with {@code serverFeature = true}
	 * appears automatically; region/disease-based features are left out.
	 * <p>
	 * Example: {@code CASE_SURVEILANCE}, {@code EXTERNAL_MESSAGES} and {@code SAMPLE_ADD_PATHOGEN_TEST} are listed,
	 * {@code LINE_LISTING} (declared with {@code serverFeature = false}) is not.
	 */
	private static final List<FeatureType> CONFIGURABLE_FEATURES = Collections.unmodifiableList(FeatureType.getAllServerFeatures());

	// The following constants are derived from the FeatureType declarations only, which do not change at runtime, so they
	// are computed once when the class is loaded instead of on every grid render, popup and reload. Only the enabled
	// states and the stored property values come from the database and are loaded in loadFeatureStates().

	/**
	 * The supported properties of every configurable feature, see {@link #computeSupportedProperties(FeatureType)}.
	 */
	private static final Map<FeatureType, List<FeatureTypeProperty>> SUPPORTED_PROPERTIES =
		computePerFeature(FeatureConfigurationView::computeSupportedProperties);

	/**
	 * The entity types the properties of every configurable feature are stored for, see
	 * {@link #computePropertyScopes(FeatureType)}.
	 */
	private static final Map<FeatureType, List<DeletableEntityType>> PROPERTY_SCOPES =
		computePerFeature(FeatureConfigurationView::computePropertyScopes);

	/**
	 * The declared property defaults of every configurable feature per entity type, see
	 * {@link #computeDefaultPropertyValues(FeatureType)}.
	 */
	private static final Map<FeatureType, Map<DeletableEntityType, Map<FeatureTypeProperty, Object>>> DEFAULT_PROPERTY_VALUES =
		computePerFeature(FeatureConfigurationView::computeDefaultPropertyValues);

	/**
	 * Whether each listed feature is currently enabled, as loaded from the facade. A feature without an entry (not loaded
	 * yet) is shown as "-".
	 */
	private final Map<FeatureType, Boolean> enabledStates = new EnumMap<>(FeatureType.class);

	/**
	 * Current property values per feature and entity type, as loaded from the facade. The inner key is {@code null} for
	 * features without entity types, so a {@link HashMap} is used there instead of an {@link EnumMap}.
	 */
	private final Map<FeatureType, Map<DeletableEntityType, Map<FeatureTypeProperty, Object>>> currentProperties = new EnumMap<>(FeatureType.class);

	private Grid<FeatureType> grid;

	/**
	 * Data provider of the grid, kept so the feature type search can set its filter without casting
	 * {@link Grid#getDataProvider()}.
	 */
	private ListDataProvider<FeatureType> featureDataProvider;

	public FeatureConfigurationView() {

		super(VIEW_NAME);

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setStyleName("crud-main-layout");

		Label heading = new Label(I18nProperties.getCaption(Captions.featureConfiguration));
		heading.addStyleName(CssStyles.H2);
		layout.addComponent(heading);

		Label info = new Label(I18nProperties.getString(Strings.infoFeatureConfiguration));
		CssStyles.style(info, CssStyles.VSPACE_3);
		layout.addComponent(info);

		List<FeatureType> rows = new ArrayList<>();
		for (FeatureType featureType : CONFIGURABLE_FEATURES) {
			// the facade rejects non-server features, so never offer one for editing
			if (featureType == null || !featureType.isServerFeature()) {
				continue;
			}

			rows.add(featureType);
		}

		featureDataProvider = new ListDataProvider<>(rows);
		grid = createFeatureGrid(featureDataProvider);
		layout.addComponent(createFeatureTypeFilterLayout());
		layout.addComponent(grid);

		addComponent(layout);
	}

	/**
	 * Builds the read-only grid listing one row per feature with an edit icon, whether the feature is enabled, its
	 * caption and its current property values; a click on the edit icon opens the edit popup. By default, enabled
	 * features are listed first and disabled ones last, each group sorted by feature caption.
	 * <p>
	 * Example row for {@code TASK_MANAGEMENT}: edit icon | "Yes" | "Task management" | "ALLOW_FREE_EDITING = Yes".
	 * Example order: enabled "Campaigns", enabled "Task management", disabled "Aggregate reporting".
	 *
	 * @param dataProvider
	 *            provides the features to list, in display order; never {@code null}
	 * @return the grid, sized to show all rows without scrolling
	 */
	private Grid<FeatureType> createFeatureGrid(ListDataProvider<FeatureType> dataProvider) {

		Grid<FeatureType> featureGrid = new Grid<>();
		featureGrid.setWidth(100, Unit.PERCENTAGE);
		featureGrid.setSelectionMode(Grid.SelectionMode.NONE);
		featureGrid.setDataProvider(dataProvider);
		featureGrid.setHeightByRows(Math.max(1, dataProvider.getItems().size()));

		// edit icon as first column, like in ScheduledJobsGrid
		featureGrid.addColumn(featureType -> VaadinIcons.EDIT.getHtml(), new HtmlRenderer())
			.setId(FilteredGrid.ACTION_BTN_ID)
			.setCaption(I18nProperties.getCaption(Captions.edit))
			.setSortable(false)
			.setWidth(EDIT_COLUMN_WIDTH);

		// Column widths: the short columns get a fixed width, the text columns share the remaining width by expand ratio
		// and are not widened to their content, so all columns fit into the grid without a horizontal scrollbar.
		// Truncated cell texts are shown in full as tooltip.

		// sorted by the enabled state itself, not by the "Yes"/"No" text, so the order does not depend on the UI language
		featureGrid.addColumn(this::formatEnabledState)
			.setCaption(I18nProperties.getCaption(Captions.FeatureConfiguration_serverEnabled))
			.setComparator(this::compareEnabledStates)
			.setWidth(90);

		Grid.Column<FeatureType, String> featureTypeColumn = featureGrid.addColumn(FeatureType::toString)
			.setCaption(I18nProperties.getCaption(Captions.FeatureConfiguration_featureType))
			.setComparator(this::compareFeatureCaptions);
		configureTextColumn(featureTypeColumn, FeatureType::toString, 2, 200);

		configureTextColumn(
			featureGrid.addColumn(this::formatCurrentProperties)
				.setCaption(I18nProperties.getCaption(Captions.FeatureConfiguration_currentProperties)),
			this::formatCurrentProperties,
			3,
			250);

		// default order: enabled features first, then disabled ones, each group alphabetically by feature caption; set on the
		// data provider instead of the grid sort order, so the column headers show no sort indicators; a click on a header
		// sorts by that column first and keeps this order within equal values
		dataProvider.setSortComparator((first, second) -> {
			int byEnabledState = compareEnabledStates(first, second);
			return byEnabledState != 0 ? byEnabledState : compareFeatureCaptions(first, second);
		});

		// only a single click on the edit icon opens the popup; showOnDoubleClick = false, so a double-click on any other
		// cell does nothing
		featureGrid.addItemClickListener(new ShowDetailsListener<>(FilteredGrid.ACTION_BTN_ID, false, featureType -> {
			if (featureType != null) {
				openEditWindow(featureType);
			}
		}));

		return featureGrid;
	}

	/**
	 * Creates the filter bar above the grid: a search field that filters the rows by feature type only (the other
	 * columns are not searched) and a reset button that empties the search field.
	 * <p>
	 * Example: "dashboard" leaves the rows {@code DASHBOARD_SURVEILLANCE}, {@code DASHBOARD_CONTACTS},
	 * {@code DASHBOARD_CAMPAIGNS} and {@code DASHBOARD_SAMPLES}; clicking "Reset filters" empties the field and shows all
	 * rows again.
	 */
	private HorizontalLayout createFeatureTypeFilterLayout() {

		TextField searchField = new TextField(I18nProperties.getCaption(Captions.FeatureConfiguration_featureType));
		searchField.setPlaceholder(I18nProperties.getCaption(Captions.actionSearch));
		searchField.setWidth(300, Unit.PIXELS);
		searchField.addValueChangeListener(e -> filterByFeatureType(e == null ? null : e.getValue()));

		// clearing the field fires its value change listener, which removes the filter; filter directly as well in case
		// the field was already empty and no value change is fired
		Button resetButton = ButtonHelper.createButton(Captions.actionResetFilters, e -> {
			searchField.clear();
			filterByFeatureType(null);
		}, CssStyles.FORCE_CAPTION);

		HorizontalLayout filterLayout = new HorizontalLayout(searchField, resetButton);
		filterLayout.setSpacing(true);
		return filterLayout;
	}

	/**
	 * Shows only the rows whose feature type matches the search text (see {@link #matchesFeatureType}) and resizes the
	 * grid to the remaining rows. A {@code null} or blank search text shows all rows.
	 * <p>
	 * Example: " Task " leaves {@code TASK_MANAGEMENT}, {@code ASSIGN_TASKS_TO_HIGHER_LEVEL}, {@code TASK_NOTIFICATIONS}
	 * and the four {@code TASK_GENERATION_*} rows, so the grid shrinks to 7 rows.
	 */
	private void filterByFeatureType(String searchText) {

		if (grid == null || featureDataProvider == null) {
			return;
		}

		String search = searchText == null ? "" : searchText.trim().toLowerCase(Locale.ROOT);
		if (search.isEmpty()) {
			featureDataProvider.clearFilters();
		} else {
			featureDataProvider.setFilter(featureType -> matchesFeatureType(featureType, search));
		}

		// size() applies the filter set above; setHeightByRows rejects 0, so an empty result keeps one (empty) row
		grid.setHeightByRows(Math.max(1, featureDataProvider.size(new Query<>())));
	}

	/**
	 * Checks whether the caption or the constant name of the feature type contains the search text, ignoring case.
	 * <p>
	 * Example: with the search text "external_messages", {@code MANUAL_EXTERNAL_MESSAGES} matches by its name; with
	 * "follow-up", {@code CASE_FOLLOWUP} matches by its caption "Case follow-up". A {@code null} feature
	 * type never matches.
	 *
	 * @param search
	 *            the search text, already trimmed and lower-cased; {@code null} or empty matches every feature type
	 */
	private boolean matchesFeatureType(FeatureType featureType, String search) {

		if (featureType == null) {
			return false;
		}
		if (search == null || search.isEmpty()) {
			return true;
		}

		String caption = featureType.toString();
		return (caption != null && caption.toLowerCase(Locale.ROOT).contains(search)) || featureType.name().toLowerCase(Locale.ROOT).contains(search);
	}

	/**
	 * Lets a text column share the free grid width by expand ratio instead of growing to its content, keeps it at least
	 * {@code minimumWidth} pixels wide and shows the full text as tooltip.
	 * <p>
	 * Example: with expand ratios 2 and 3, the "Current properties" column gets 3/5 of the width left after the
	 * fixed-width columns, but never less than 250 pixels.
	 */
	private void configureTextColumn(
		Grid.Column<FeatureType, String> column,
		ValueProvider<FeatureType, String> tooltipProvider,
		int expandRatio,
		double minimumWidth) {

		column.setMinimumWidthFromContent(false);
		column.setMinimumWidth(minimumWidth);
		column.setExpandRatio(expandRatio);
		column.setDescriptionGenerator(featureType -> featureType == null ? null : tooltipProvider.apply(featureType));
	}

	/**
	 * Orders features by their enabled state: enabled first, then disabled, then features whose state has not been loaded
	 * yet. {@code null} features count as not loaded.
	 * <p>
	 * Example: an enabled {@code CAMPAIGNS} comes before a disabled {@code AGGREGATE_REPORTING}, which comes before a
	 * feature without a loaded state.
	 */
	private int compareEnabledStates(FeatureType first, FeatureType second) {
		return Integer.compare(getEnabledSortRank(first), getEnabledSortRank(second));
	}

	/**
	 * Gives the position of the feature's enabled state in the sort order: 0 for enabled, 1 for disabled, 2 for not loaded
	 * yet or {@code null}.
	 */
	private int getEnabledSortRank(FeatureType featureType) {

		Boolean enabled = featureType == null ? null : enabledStates.get(featureType);
		if (enabled == null) {
			return 2;
		}
		return enabled ? 0 : 1;
	}

	/**
	 * Orders features alphabetically by caption, ignoring case; {@code null} features and captions come last.
	 * <p>
	 * Example: "Aggregate reporting" comes before "campaigns", which comes before "Task management".
	 */
	private int compareFeatureCaptions(FeatureType first, FeatureType second) {

		String firstCaption = first == null ? null : first.toString();
		String secondCaption = second == null ? null : second.toString();
		return Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER).compare(firstCaption, secondCaption);
	}

	/**
	 * Formats whether the feature is currently enabled.
	 * <p>
	 * Example: an enabled {@code CASE_SURVEILANCE} gives "Yes", a disabled {@code CAMPAIGNS} gives "No"; a feature whose
	 * state has not been loaded yet gives "-".
	 */
	private String formatEnabledState(FeatureType featureType) {

		Boolean enabled = enabledStates.get(featureType);
		return enabled == null ? EMPTY_CELL : formatBoolean(enabled);
	}

	/**
	 * Lists the current property values, prefixed with the entity type for features configured per entity type.
	 * <p>
	 * Example: {@code TASK_MANAGEMENT} gives "ALLOW_FREE_EDITING = Yes"; {@code AUTOMATIC_ARCHIVING} gives
	 * "CASE: THRESHOLD_IN_DAYS = 180; CONTACT: THRESHOLD_IN_DAYS = 90; ..."; {@code CAMPAIGNS} gives "-".
	 */
	private String formatCurrentProperties(FeatureType featureType) {

		Map<DeletableEntityType, Map<FeatureTypeProperty, Object>> byEntityType = currentProperties.get(featureType);
		if (byEntityType == null || byEntityType.isEmpty()) {
			return EMPTY_CELL;
		}

		List<String> parts = new ArrayList<>();
		for (DeletableEntityType entityType : getPropertyScopes(featureType)) {
			String properties = formatProperties(byEntityType.get(entityType));
			parts.add(entityType == null ? properties : I18nProperties.getEnumCaption(entityType) + ": " + properties);
		}

		return parts.isEmpty() ? EMPTY_CELL : String.join("; ", parts);
	}

	/**
	 * Formats a property map as "Property = value" pairs.
	 * <p>
	 * Example: {@code {SHARE_SAMPLES=true, SHARE_IMMUNIZATIONS=false}} gives "SHARE_SAMPLES = Yes, SHARE_IMMUNIZATIONS = No"
	 * (enum captions where present); {@code null} or an empty map gives "-".
	 */
	private String formatProperties(Map<FeatureTypeProperty, Object> properties) {

		if (properties == null || properties.isEmpty()) {
			return EMPTY_CELL;
		}

		String result = properties.entrySet()
			.stream()
			.filter(entry -> entry.getKey() != null)
			.map(entry -> I18nProperties.getEnumCaption(entry.getKey()) + " = " + formatValue(entry.getValue()))
			.collect(Collectors.joining(", "));
		return result.isEmpty() ? EMPTY_CELL : result;
	}

	/**
	 * Formats a property value for display: booleans as "Yes"/"No", {@code null} as "-", anything else via
	 * {@link String#valueOf(Object)} (e.g. {@code 90} gives "90").
	 */
	private String formatValue(Object value) {

		if (value == null) {
			return EMPTY_CELL;
		}
		if (value instanceof Boolean) {
			return formatBoolean((Boolean) value);
		}
		return String.valueOf(value);
	}

	private String formatBoolean(boolean value) {
		return I18nProperties.getString(value ? Strings.yes : Strings.no);
	}

	/**
	 * Returns the supported properties of the feature from {@link #SUPPORTED_PROPERTIES}; a feature that is not
	 * precomputed there (not configurable, or {@code null}) is computed on demand, so no caller gets {@code null}.
	 * <p>
	 * Example: {@code CONTACT_TRACING} gives [AUTOMATIC_RESPONSIBILITY_ASSIGNMENT, ALLOW_FREE_FOLLOW_UP_OVERWRITE];
	 * {@code CAMPAIGNS} gives an empty list.
	 */
	private List<FeatureTypeProperty> getSupportedProperties(FeatureType featureType) {

		List<FeatureTypeProperty> properties = SUPPORTED_PROPERTIES.get(featureType);
		return properties != null ? properties : computeSupportedProperties(featureType);
	}

	/**
	 * Returns the entity types the properties of the feature are stored for, from {@link #PROPERTY_SCOPES}; a feature
	 * that is not precomputed there is computed on demand.
	 * <p>
	 * Example: {@code AUTOMATIC_ARCHIVING} gives [CASE, CONTACT, EVENT, EVENT_PARTICIPANT, IMMUNIZATION, TRAVEL_ENTRY];
	 * {@code TASK_MANAGEMENT} gives [null].
	 */
	private List<DeletableEntityType> getPropertyScopes(FeatureType featureType) {

		List<DeletableEntityType> scopes = PROPERTY_SCOPES.get(featureType);
		return scopes != null ? scopes : computePropertyScopes(featureType);
	}

	/**
	 * Returns the declared property defaults of the feature per entity type, from {@link #DEFAULT_PROPERTY_VALUES}; a
	 * feature that is not precomputed there is computed on demand.
	 * <p>
	 * Example: {@code AUTOMATIC_ARCHIVING} gives {CASE={THRESHOLD_IN_DAYS=90}, CONTACT={THRESHOLD_IN_DAYS=90}, ...};
	 * {@code CAMPAIGNS} gives an empty map.
	 */
	private Map<DeletableEntityType, Map<FeatureTypeProperty, Object>> getDefaultPropertyValues(FeatureType featureType) {

		Map<DeletableEntityType, Map<FeatureTypeProperty, Object>> defaults = DEFAULT_PROPERTY_VALUES.get(featureType);
		return defaults != null ? defaults : computeDefaultPropertyValues(featureType);
	}

	/**
	 * Applies the computation to every configurable feature and returns the unmodifiable results per feature. It is
	 * static, like the computations, because it initializes the static constants when the class is loaded.
	 * <p>
	 * Example: {@code computePerFeature(FeatureConfigurationView::computeSupportedProperties)} gives
	 * {AGGREGATE_REPORTING=[], ..., CONTACT_TRACING=[AUTOMATIC_RESPONSIBILITY_ASSIGNMENT, ALLOW_FREE_FOLLOW_UP_OVERWRITE], ...}.
	 */
	private static <T> Map<FeatureType, T> computePerFeature(Function<FeatureType, T> computation) {

		Map<FeatureType, T> result = new EnumMap<>(FeatureType.class);
		for (FeatureType featureType : CONFIGURABLE_FEATURES) {
			if (featureType != null) {
				result.put(featureType, computation.apply(featureType));
			}
		}

		return Collections.unmodifiableMap(result);
	}

	/**
	 * Computes the supported properties of the feature in declaration order, without {@code null} keys, as an
	 * unmodifiable list.
	 * <p>
	 * Example: {@code CONTACT_TRACING} gives [AUTOMATIC_RESPONSIBILITY_ASSIGNMENT, ALLOW_FREE_FOLLOW_UP_OVERWRITE];
	 * {@code CAMPAIGNS} and {@code null} give an empty list.
	 */
	private static List<FeatureTypeProperty> computeSupportedProperties(FeatureType featureType) {

		if (featureType == null || featureType.getSupportedPropertyDefaults() == null) {
			return Collections.emptyList();
		}

		return Collections
			.unmodifiableList(featureType.getSupportedPropertyDefaults().keySet().stream().filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Computes the entity types the properties of the feature are stored for: its entity types, or a single {@code null}
	 * scope for features that are not configured per entity type, as an unmodifiable list.
	 * <p>
	 * Example: {@code AUTOMATIC_ARCHIVING} gives [CASE, CONTACT, EVENT, EVENT_PARTICIPANT, IMMUNIZATION, TRAVEL_ENTRY];
	 * {@code TASK_MANAGEMENT} gives [null].
	 */
	private static List<DeletableEntityType> computePropertyScopes(FeatureType featureType) {

		List<DeletableEntityType> entityTypes = featureType == null ? null : featureType.getEntityTypes();
		if (entityTypes == null || entityTypes.isEmpty()) {
			return Collections.singletonList(null);
		}

		List<DeletableEntityType> scopes = entityTypes.stream().filter(Objects::nonNull).collect(Collectors.toList());
		return scopes.isEmpty() ? Collections.singletonList(null) : Collections.unmodifiableList(scopes);
	}

	/**
	 * Returns the value a property input starts with: the stored value, or the default declared on the
	 * {@link FeatureType} if nothing is stored.
	 * <p>
	 * Example: {@code THRESHOLD_IN_DAYS} of {@code AUTOMATIC_ARCHIVING} gives the stored {@code 180}, or the default
	 * {@code 90} if no value is stored for that entity type.
	 */
	private static Object getStartValue(FeatureType featureType, Map<FeatureTypeProperty, Object> current, FeatureTypeProperty property) {

		if (current != null && current.get(property) != null) {
			return current.get(property);
		}

		Map<FeatureTypeProperty, Object> defaults = featureType == null ? null : featureType.getSupportedPropertyDefaults();
		return defaults == null ? null : defaults.get(property);
	}

	/**
	 * Computes the defaults declared on the {@link FeatureType} for every property and entity type, without {@code null}
	 * defaults (a property without a declared default keeps its stored value), as unmodifiable maps. Features without
	 * supported properties give an empty map, so nothing is stored for them.
	 * <p>
	 * Example: {@code AUTOMATIC_ARCHIVING} gives {CASE={THRESHOLD_IN_DAYS=90}, CONTACT={THRESHOLD_IN_DAYS=90}, ...};
	 * {@code LIMITED_SYNCHRONIZATION} gives {null={EXCLUDE_NO_CASE_CLASSIFIED_CASES=false, MAX_CHANGE_DATE_PERIOD=-1}};
	 * {@code CAMPAIGNS} gives an empty map.
	 */
	private static Map<DeletableEntityType, Map<FeatureTypeProperty, Object>> computeDefaultPropertyValues(FeatureType featureType) {

		// LinkedHashMap keeps the entity type order and accepts the null scope
		Map<DeletableEntityType, Map<FeatureTypeProperty, Object>> defaultsByEntityType = new LinkedHashMap<>();
		List<FeatureTypeProperty> properties = computeSupportedProperties(featureType);
		if (properties.isEmpty()) {
			return Collections.unmodifiableMap(defaultsByEntityType);
		}

		for (DeletableEntityType entityType : computePropertyScopes(featureType)) {
			Map<FeatureTypeProperty, Object> defaults = new HashMap<>();
			for (FeatureTypeProperty property : properties) {
				Object defaultValue = getStartValue(featureType, null, property);
				if (defaultValue != null) {
					defaults.put(property, defaultValue);
				}
			}

			if (!defaults.isEmpty()) {
				defaultsByEntityType.put(entityType, Collections.unmodifiableMap(defaults));
			}
		}

		return Collections.unmodifiableMap(defaultsByEntityType);
	}

	/**
	 * Opens the edit popup of a feature: an "Enabled" yes/no combo box and, for features with supported properties, their
	 * inputs below the "Current properties" heading (see {@link #createPropertiesLayout}). All labels have the same width,
	 * so all inputs are aligned below each other. The properties only apply to an enabled feature, so they are only shown
	 * while "Enabled" is "Yes". Every change of "Enabled" resets the property inputs to the defaults declared on the
	 * {@link FeatureType}. On save, the enabled state and the property values are stored via the facade; a disabled
	 * feature gets the declared defaults stored as property values (see {@link #saveFeature}).
	 * <p>
	 * Example: for {@code CAMPAIGNS} the popup only shows "Enabled [Yes]"; for {@code LIMITED_SYNCHRONIZATION} with
	 * "Enabled [No]" it shows no properties, and selecting "Yes" shows "EXCLUDE_NO_CASE_CLASSIFIED_CASES [No]" and
	 * "MAX_CHANGE_DATE_PERIOD [-1]" (the declared defaults); for {@code AUTOMATIC_ARCHIVING} with the stored
	 * THRESHOLD_IN_DAYS 180, switching "Enabled" to "No" and back to "Yes" shows the default 90 in every row of the entity
	 * type table.
	 */
	private void openEditWindow(FeatureType featureType) {

		VerticalLayout content = new VerticalLayout();
		content.setMargin(true);
		content.setSpacing(true);
		content.setWidth(100, Unit.PERCENTAGE);

		// without a loaded state the popup would start with "No" and disable an enabled feature on save, so load it first
		if (!enabledStates.containsKey(featureType)) {
			loadFeatureState(featureType);
		}

		GridLayout enabledLayout = new GridLayout(2, 1);
		enabledLayout.setSpacing(true);
		ComboBox<Boolean> enabledField = createBooleanField(Boolean.TRUE.equals(enabledStates.get(featureType)));
		addLabelledRow(enabledLayout, 0, I18nProperties.getCaption(Captions.FeatureConfiguration_serverEnabled), enabledField);
		content.addComponent(enabledLayout);

		// LinkedHashMap keeps the entity type order and accepts the null scope
		Map<DeletableEntityType, Map<FeatureTypeProperty, Component>> fields = new LinkedHashMap<>();
		// one action per property input that sets it back to the default declared on the FeatureType
		List<Runnable> resetToDefaults = new ArrayList<>();
		List<FeatureTypeProperty> properties = getSupportedProperties(featureType);
		if (!properties.isEmpty()) {
			VerticalLayout propertiesSection = new VerticalLayout();
			propertiesSection.setMargin(false);
			propertiesSection.setSpacing(true);

			Label propertiesHeading = new Label(I18nProperties.getCaption(Captions.FeatureConfiguration_currentProperties));
			propertiesHeading.addStyleName(CssStyles.H3);
			propertiesSection.addComponent(propertiesHeading);
			propertiesSection.addComponent(createPropertiesLayout(featureType, properties, fields, resetToDefaults));
			content.addComponent(propertiesSection);

			// the properties only apply to an enabled feature, so they are only shown while "Enabled" is "Yes"; switching the
			// feature on or off starts its properties again from the declared defaults
			propertiesSection.setVisible(Boolean.TRUE.equals(enabledField.getValue()));
			enabledField.addValueChangeListener(e -> {
				propertiesSection.setVisible(e != null && Boolean.TRUE.equals(e.getValue()));
				resetToDefaults.forEach(Runnable::run);
			});
		}

		Button cancelButton = ButtonHelper.createButton(Captions.actionCancel, null);
		Button saveButton = ButtonHelper.createButton(Captions.actionSave, null, ValoTheme.BUTTON_PRIMARY);
		HorizontalLayout buttons = new HorizontalLayout(cancelButton, saveButton);
		content.addComponent(buttons);
		content.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);

		Window window = VaadinUiUtil.showPopupWindow(content, featureType.toString());
		window.setWidth(EDIT_WINDOW_WIDTH, Unit.PIXELS);
		window.center();
		cancelButton.addClickListener(e -> window.close());
		saveButton.addClickListener(e -> {
			if (saveFeature(featureType, Boolean.TRUE.equals(enabledField.getValue()), fields)) {
				window.close();
			}
		});
	}

	/**
	 * Adds a row with a label of the fixed width {@link #EDIT_CAPTION_WIDTH} in column 0 and the input in column 1.
	 * <p>
	 * Example: {@code ("MAX_CHANGE_DATE_PERIOD", textField)} adds the row "MAX_CHANGE_DATE_PERIOD [-1]", whose text field
	 * starts at the same position as the "Enabled" combo box above it.
	 */
	private void addLabelledRow(GridLayout layout, int row, String caption, Component field) {

		Label label = new Label(caption);
		label.setWidth(EDIT_CAPTION_WIDTH, Unit.PIXELS);
		layout.addComponent(label, 0, row);
		layout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		layout.addComponent(field, 1, row);
	}

	/**
	 * Lays out the property inputs of a feature, registers them in {@code fields} per entity type and adds one action per
	 * input to {@code resetToDefaults} that sets it back to the default declared on the {@link FeatureType}:
	 * <ul>
	 * <li>features without entity types: one labelled row per property;</li>
	 * <li>features configured per entity type: a table with one row per entity type and one column per property, labelled
	 * by the column headers.</li>
	 * </ul>
	 * Both have no fixed total width and a label (or entity type) column of {@link #EDIT_CAPTION_WIDTH}, so the inputs are
	 * aligned with the "Enabled" combo box.
	 * <p>
	 * Example: {@code TASK_MANAGEMENT} gives the row "ALLOW_FREE_EDITING [No]"; {@code AUTOMATIC_ARCHIVING} gives a table
	 * with the header "Entity types | THRESHOLD_IN_DAYS" and the rows "CASE | [90]", "CONTACT | [90]", ...
	 */
	private Component createPropertiesLayout(
		FeatureType featureType,
		List<FeatureTypeProperty> properties,
		Map<DeletableEntityType, Map<FeatureTypeProperty, Component>> fields,
		List<Runnable> resetToDefaults) {

		Map<DeletableEntityType, Map<FeatureTypeProperty, Object>> byEntityType = currentProperties.getOrDefault(featureType, Collections.emptyMap());
		List<DeletableEntityType> scopes = getPropertyScopes(featureType);

		// features without entity types have the single null scope
		if (scopes.size() == 1 && scopes.get(0) == null) {
			GridLayout rows = new GridLayout(2, properties.size());
			rows.setSpacing(true);
			Map<FeatureTypeProperty, Component> scopeFields = new LinkedHashMap<>();
			for (int row = 0; row < properties.size(); row++) {
				FeatureTypeProperty property = properties.get(row);
				Component field = createPropertyField(
					property,
					getStartValue(featureType, byEntityType.get(null), property),
					getStartValue(featureType, null, property),
					resetToDefaults);
				addLabelledRow(rows, row, I18nProperties.getEnumCaption(property), field);
				scopeFields.put(property, field);
			}
			fields.put(null, scopeFields);
			return rows;
		}

		GridLayout table = new GridLayout(properties.size() + 1, scopes.size() + 1);
		table.setSpacing(true);

		Label entityTypesHeader = createHeaderLabel(I18nProperties.getCaption(Captions.FeatureConfiguration_entityTypes));
		entityTypesHeader.setWidth(EDIT_CAPTION_WIDTH, Unit.PIXELS);
		table.addComponent(entityTypesHeader, 0, 0);
		for (int column = 0; column < properties.size(); column++) {
			table.addComponent(createHeaderLabel(I18nProperties.getEnumCaption(properties.get(column))), column + 1, 0);
		}

		for (int row = 0; row < scopes.size(); row++) {
			DeletableEntityType entityType = scopes.get(row);
			Label entityLabel = new Label(entityType == null ? EMPTY_CELL : I18nProperties.getEnumCaption(entityType));
			entityLabel.setWidth(EDIT_CAPTION_WIDTH, Unit.PIXELS);
			table.addComponent(entityLabel, 0, row + 1);
			table.setComponentAlignment(entityLabel, Alignment.MIDDLE_LEFT);

			Map<FeatureTypeProperty, Object> current = byEntityType.get(entityType);
			Map<FeatureTypeProperty, Component> scopeFields = new LinkedHashMap<>();
			for (int column = 0; column < properties.size(); column++) {
				FeatureTypeProperty property = properties.get(column);
				Component field = createPropertyField(
					property,
					getStartValue(featureType, current, property),
					getStartValue(featureType, null, property),
					resetToDefaults);
				scopeFields.put(property, field);
				table.addComponent(field, column + 1, row + 1);
			}
			fields.put(entityType, scopeFields);
		}

		return table;
	}

	/**
	 * Names a property input in messages: the property caption, prefixed with the entity type caption for features
	 * configured per entity type, like the "Current properties" column of the grid.
	 * <p>
	 * Example: {@code (CONTACT, THRESHOLD_IN_DAYS)} gives "Contact: THRESHOLD_IN_DAYS"; {@code (null, ALLOW_FREE_EDITING)}
	 * gives "ALLOW_FREE_EDITING".
	 */
	private String getFieldCaption(DeletableEntityType entityType, FeatureTypeProperty property) {

		String propertyCaption = property == null ? EMPTY_CELL : I18nProperties.getEnumCaption(property);
		return entityType == null ? propertyCaption : I18nProperties.getEnumCaption(entityType) + ": " + propertyCaption;
	}

	/**
	 * Creates a bold label for a column header of the entity type table in the edit popup.
	 * <p>
	 * Example: {@code "THRESHOLD_IN_DAYS"} gives a bold "THRESHOLD_IN_DAYS" above the threshold column.
	 */
	private Label createHeaderLabel(String text) {

		Label label = new Label(text);
		label.addStyleName(CssStyles.LABEL_BOLD);
		return label;
	}

	/**
	 * Creates a yes/no combo box without an empty option, starting with the given value.
	 * <p>
	 * Example: {@code true} gives a combo box showing "Yes", from which "No" can be selected.
	 */
	private ComboBox<Boolean> createBooleanField(boolean value) {

		ComboBox<Boolean> comboBox = new ComboBox<>(null, Arrays.asList(Boolean.TRUE, Boolean.FALSE));
		comboBox.setItemCaptionGenerator(item -> item == null ? EMPTY_CELL : formatBoolean(item));
		comboBox.setEmptySelectionAllowed(false);
		comboBox.setTextInputAllowed(false);
		comboBox.setWidth(EDIT_FIELD_WIDTH, Unit.PIXELS);
		comboBox.setValue(value);
		return comboBox;
	}

	/**
	 * Creates the input for one property, without a label (the caller adds it): a yes/no combo box for {@link Boolean}, a
	 * text field for {@link Integer} and {@link String}, and a read-only label for any other return type so the value
	 * stays visible. For the combo box and the text field, an action that sets the input to {@code defaultValue} is added
	 * to {@code resetToDefaults}; the read-only label has nothing to reset.
	 * <p>
	 * Example: {@code (SHARE_SAMPLES, false, true)} gives a combo box showing "No" whose reset action selects "Yes";
	 * {@code (THRESHOLD_IN_DAYS, 180, 90)} gives a text field containing "180" whose reset action sets it to "90".
	 */
	private Component createPropertyField(FeatureTypeProperty property, Object value, Object defaultValue, List<Runnable> resetToDefaults) {

		Class<?> returnType = property.getReturnType();

		if (Boolean.class.equals(returnType)) {
			ComboBox<Boolean> comboBox = createBooleanField(Boolean.TRUE.equals(value));
			if (resetToDefaults != null) {
				resetToDefaults.add(() -> comboBox.setValue(Boolean.TRUE.equals(defaultValue)));
			}
			return comboBox;
		}

		if (Integer.class.equals(returnType) || String.class.equals(returnType)) {
			TextField textField = new TextField(null, value == null ? "" : String.valueOf(value));
			textField.setWidth(EDIT_FIELD_WIDTH, Unit.PIXELS);
			if (resetToDefaults != null) {
				resetToDefaults.add(() -> textField.setValue(defaultValue == null ? "" : String.valueOf(defaultValue)));
			}
			return textField;
		}

		return new Label(formatValue(value));
	}

	/**
	 * Stores the enabled state and the property values (per entity type) via the facade. For an enabled feature, the
	 * values are read from the popup inputs and validated first; nothing is stored if an input is invalid. For a disabled
	 * feature, the inputs are hidden and ignored, and the defaults declared on the {@link FeatureType} are stored instead.
	 * <p>
	 * Example: enabled, "180" in a THRESHOLD_IN_DAYS field is stored as {@code 180}, and "abc" shows an error notification
	 * naming "THRESHOLD_IN_DAYS" and nothing is stored; disabled, {@code AUTOMATIC_ARCHIVING} gets THRESHOLD_IN_DAYS
	 * {@code 90} stored for every entity type, whatever the inputs contain.
	 *
	 * @param fields
	 *            the property inputs per entity type; empty for features without supported properties
	 * @return {@code true} if the values were stored and the popup can be closed, {@code false} if an input is invalid
	 */
	private boolean saveFeature(FeatureType featureType, boolean enabled, Map<DeletableEntityType, Map<FeatureTypeProperty, Component>> fields) {

		Map<DeletableEntityType, Map<FeatureTypeProperty, Object>> valuesByEntityType;
		if (enabled) {
			valuesByEntityType = new LinkedHashMap<>();

			for (Map.Entry<DeletableEntityType, Map<FeatureTypeProperty, Component>> scope : fields.entrySet()) {
				Map<FeatureTypeProperty, Object> values = new HashMap<>();

				for (Map.Entry<FeatureTypeProperty, Component> entry : scope.getValue().entrySet()) {
					FeatureTypeProperty property = entry.getKey();
					Component field = entry.getValue();

					if (field instanceof ComboBox) {
						values.put(property, Boolean.TRUE.equals(((ComboBox<?>) field).getValue()));
					} else if (field instanceof TextField) {
						TextField textField = (TextField) field;
						// clears the marker of an earlier failed save
						textField.setComponentError(null);
						String text = textField.getValue() == null ? "" : textField.getValue().trim();
						if (Integer.class.equals(property.getReturnType())) {
							try {
								values.put(property, Integer.valueOf(text));
							} catch (NumberFormatException ex) {
								// the fields in the entity type table have no caption, so the entity type and property captions name
								// the field, and the field itself is marked
								String message = I18nProperties.getValidationError(
									Validations.onlyIntegerNumbersAllowed,
									getFieldCaption(scope.getKey(), property));
								textField.setComponentError(new UserError(message));
								Notification.show(message, Notification.Type.ERROR_MESSAGE);
								return false;
							}
						} else {
							values.put(property, text);
						}
					}
					// read-only labels are not stored, so their value is left unchanged
				}

				valuesByEntityType.put(scope.getKey(), values);
			}
		} else {
			// the inputs of a disabled feature are hidden, so they are neither validated nor stored; the declared defaults are
			// stored instead
			valuesByEntityType = getDefaultPropertyValues(featureType);
		}

		FacadeProvider.getFeatureConfigurationFacade().setServerFeatureEnabled(featureType, enabled);
		valuesByEntityType.forEach(
			(entityType, values) -> FacadeProvider.getFeatureConfigurationFacade().setServerFeatureProperties(featureType, entityType, values));

		// the facade only writes the configurations of this feature type, so only this one is reloaded; refreshAll (in memory,
		// no facade calls) instead of refreshItem, so the row moves to its new position if its enabled state changed
		loadFeatureState(featureType);
		if (featureDataProvider != null) {
			featureDataProvider.refreshAll();
		}

		VaadinUiUtil.showSimplePopupWindow(
			I18nProperties.getString(Strings.headingFeatureConfiguration),
			I18nProperties.getString(Strings.messageFeatureConfigurationSaved));
		return true;
	}

	/**
	 * Reloads the enabled state and the property values of every listed feature from the facade and redraws the grid.
	 * Used when the view is entered, because another administrator may have changed the configuration in the meantime.
	 * <p>
	 * Example: after another administrator disabled {@code CAMPAIGNS}, re-entering the view shows "No" in its row.
	 */
	private void loadFeatureStates() {

		enabledStates.clear();
		currentProperties.clear();
		for (FeatureType featureType : featureDataProvider == null ? Collections.<FeatureType> emptyList() : featureDataProvider.getItems()) {
			loadFeatureState(featureType);
		}

		if (featureDataProvider != null) {
			featureDataProvider.refreshAll();
		}
	}

	/**
	 * Reloads the enabled state and the property values of one feature from the facade; the grid is not redrawn. The
	 * property entry of the feature is removed first, so no outdated value is left if the feature has no properties. A {@code null}
	 * feature is ignored.
	 * <p>
	 * Example: after saving {@code TASK_MANAGEMENT} with "ALLOW_FREE_EDITING = No", only {@code TASK_MANAGEMENT} is
	 * queried again and {@link #currentProperties} holds {null={ALLOW_FREE_EDITING=false}} for it.
	 */
	private void loadFeatureState(FeatureType featureType) {

		if (featureType == null) {
			return;
		}

		currentProperties.remove(featureType);
		enabledStates.put(featureType, FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(featureType));

		List<FeatureTypeProperty> supportedProperties = getSupportedProperties(featureType);
		if (supportedProperties.isEmpty()) {
			return;
		}

		Map<DeletableEntityType, Map<FeatureTypeProperty, Object>> byEntityType = new HashMap<>();
		for (DeletableEntityType entityType : getPropertyScopes(featureType)) {
			Map<FeatureTypeProperty, Object> properties =
				FacadeProvider.getFeatureConfigurationFacade().getServerFeatureProperties(featureType, entityType);

			// re-order in declaration order so the grid text is stable; the facade returns an unordered map
			Map<FeatureTypeProperty, Object> ordered = new LinkedHashMap<>();
			if (properties != null) {
				for (FeatureTypeProperty property : supportedProperties) {
					if (properties.containsKey(property)) {
						ordered.put(property, properties.get(property));
					}
				}
			}
			byEntityType.put(entityType, ordered);
		}
		currentProperties.put(featureType, byEntityType);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		loadFeatureStates();
	}
}
