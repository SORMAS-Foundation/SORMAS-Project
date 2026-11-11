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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateVariable;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateLinkingService;
import de.symeda.sormas.backend.climate.ClimateLocation;
import de.symeda.sormas.backend.climate.ClimateObservation;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.climate.ClimateCaptions;
import de.symeda.sormas.ui.dashboard.climate.ClimateDashboardLayout.Measure;

class ClimateDashboardLayoutTest extends AbstractUiBeanTest {

	/** The diseases the tests mark climate-sensitive, all with case surveillance. */
	static final Disease[] MARKED = {
		Disease.MALARIA,
		Disease.DENGUE,
		Disease.CHIKUNGUNYA,
		Disease.YELLOW_FEVER,
		Disease.WEST_NILE_FEVER,
		Disease.CHOLERA,
		Disease.TYPHOID_FEVER,
		Disease.CSM,
		Disease.LASSA };

	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	/** The last complete epi week, the end of the default period. */
	private EpiWeek lastWeek;

	@BeforeEach
	void setUpClimate() {
		getFeatureConfigurationFacade().setServerFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF("Guanacaste", "Bagaces", "Community", "Facility");
		user = useNationalAdminLogin();
		lastWeek = DateHelper.getPreviousEpiWeek(new Date());
		markClimateSensitive(getDiseaseConfigurationFacade(), MARKED);
	}

	@Test
	void testNationalUserStartsWithTheChooseRegionCard() {

		ClimateDashboardLayout layout = new ClimateDashboardLayout(nationalUser(), JurisdictionLevel.NATION);

		assertTrue(layout.getEmptyCard().isVisible());
		assertEquals(
			"Choose a region and at least one disease under Place, period and diseases, then press Apply to see the chart.",
			layout.getEmptyCard().getValue());
		assertTrue(layout.getDiseaseGroup().getValue().isEmpty(), "no disease ticked at first");
		assertTrue(isInside(layout.getDiseaseGroup(), layout.getFilterCard()), "the diseases are chosen with the place and period");
		assertFalse(layout.getContent().isVisible(), "no blank chart before a region is chosen");
		assertFalse(layout.getDisplayCard().isVisible(), "no display controls before a region is applied: they would change nothing");
		assertFalse(
			layout.getDiseaseGroup().getStyleName().contains(com.vaadin.ui.themes.ValoTheme.OPTIONGROUP_HORIZONTAL),
			"one disease checkbox per line");
		assertTrue(
			layout.getRegionFilter().getStyleName().contains(de.symeda.sormas.ui.utils.CssStyles.VSPACE_NONE),
			"compact cards: no margin under the fields");
		assertTrue(layout.getRegionFilter().isEnabled());
		assertTrue(layout.getDistrictFilter().isEnabled());
		assertEquals(lastWeek, layout.getToFilter().getValue(), "the default period ends with the last complete week");
		assertEquals(
			DateHelper.getEpiWeek(DateHelper.subtractDays(DateHelper.getEpiWeekStart(lastWeek), 7 * 51)),
			layout.getFromFilter().getValue(),
			"and covers 52 weeks");

		layout.getRegionFilter().setValue(rdcf.region);
		layout.apply();

		assertNotNull(layout.getSelection(), "applied without a disease");
		assertTrue(layout.getEmptyCard().isVisible());
		assertEquals("Tick at least one disease under Place, period and diseases to see the chart.", layout.getEmptyCard().getValue());
		assertFalse(layout.getContent().isVisible(), "no chart without a disease");
		assertFalse(layout.getDisplayCard().isVisible());

		layout.getDiseaseGroup().select(Disease.DENGUE);
		assertNotNull(layout.getChart().getScript(), "a tick draws at once, without Apply");
		Label exportLink = new Label("Export CSV");
		layout.addBelowChart(exportLink);
		assertTrue(isInside(exportLink, layout.getContent()), "the export link sits under the chart and is shown and hidden with it");
		assertFalse(layout.getEmptyCard().isVisible());
		assertTrue(layout.getContent().isVisible());
		assertTrue(layout.getDisplayCard().isVisible());
		assertNull(layout.getSelection().getDistrict());
		assertEquals(52, layout.getClimate().getShownWeeks().size());
		assertEquals(ClimateDashboardLayout.MAX_LAG, layout.getClimate().getLeadWeeks(), "12 weeks of climate before the period, for the lag");
		assertEquals(1, layout.getClimate().getDistrictSeries().size(), "the region's districts, for the status line");
	}

	@Test
	void testDistrictUserHasRegionAndDistrictFixed() {

		UserDto districtUser = nationalUser();
		districtUser.setRegion(rdcf.region);
		districtUser.setDistrict(rdcf.district);

		ClimateDashboardLayout layout = new ClimateDashboardLayout(districtUser, JurisdictionLevel.DISTRICT);

		assertFalse(layout.getRegionFilter().isEnabled());
		assertFalse(layout.getDistrictFilter().isEnabled());
		assertEquals(rdcf.district, layout.getDistrictFilter().getValue());
		assertNotNull(layout.getSelection(), "applied on opening");
		assertEquals(rdcf.district, layout.getSelection().getDistrict());
		assertTrue(layout.getClimate().getDistrictSeries().isEmpty());
		assertFalse(text(layout).contains("only those in your jurisdiction"));

		ClimateDashboardLayout communityLayout = new ClimateDashboardLayout(districtUser, JurisdictionLevel.COMMUNITY);
		assertTrue(
			text(communityLayout).contains("Cases: only those in your jurisdiction. The climate is the whole district's."),
			"below district level the counts may not be the district's");
	}

	@Test
	void testRegionUserChoosesTheDistrict() {

		UserDto regionUser = nationalUser();
		regionUser.setRegion(rdcf.region);

		ClimateDashboardLayout layout = new ClimateDashboardLayout(regionUser, JurisdictionLevel.REGION);

		assertFalse(layout.getRegionFilter().isEnabled());
		assertTrue(layout.getDistrictFilter().isEnabled());
		assertNull(layout.getSelection().getDistrict());

		layout.getDistrictFilter().setValue(rdcf.district);
		layout.apply();
		assertEquals(rdcf.district, layout.getSelection().getDistrict());
	}

	@Test
	void testPeriodChecks() {

		ClimateDashboardLayout layout = regionLayout();
		ClimateDashboardLayout.Selection applied = layout.getSelection();

		layout.getFromFilter().setValue(lastWeek);
		layout.getToFilter().setValue(DateHelper.getPreviousEpiWeek(lastWeek));
		layout.apply();
		assertEquals(applied, layout.getSelection(), "a start after the end is refused");

		List<EpiWeek> offered = ClimateDashboardLayout.offeredWeeks(new Date());
		layout.getFromFilter().setValue(offered.get(ClimateDashboardLayout.MAX_WEEKS + 1));
		layout.getToFilter().setValue(lastWeek);
		layout.apply();
		assertEquals(applied, layout.getSelection(), "more than 5 years is refused");

		layout.getFromFilter().setValue(offered.get(ClimateDashboardLayout.MAX_WEEKS));
		layout.apply();
		assertEquals(ClimateDashboardLayout.MAX_WEEKS, layout.getClimate().getShownWeeks().size(), "5 years are allowed");
	}

	@Test
	void testDiseasesAreCappedAndLoadedWhenTicked() {

		ClimateDashboardLayout layout = new ClimateDashboardLayout(nationalUser(), JurisdictionLevel.NATION);
		layout.getRegionFilter().setValue(rdcf.region);
		layout.apply();
		List<Disease> offered = layout.getOfferedDiseases();
		assertTrue(offered.size() > ClimateDashboardLayout.PANELS_DISEASE_CAP, "the default climate-sensitive list: " + offered);
		assertTrue(offered.contains(Disease.DENGUE));
		assertTrue(layout.getCases().isEmpty(), "nothing ticked, nothing loaded");

		layout.getDiseaseGroup().setValue(new java.util.HashSet<>(offered.subList(0, ClimateDashboardLayout.PANELS_DISEASE_CAP)));
		assertEquals(offered.subList(0, ClimateDashboardLayout.PANELS_DISEASE_CAP), layout.selectedDiseases());
		assertEquals(ClimateDashboardLayout.PANELS_DISEASE_CAP, layout.getCases().size());
		Disease fifth = offered.get(ClimateDashboardLayout.PANELS_DISEASE_CAP);
		assertFalse(layout.getDiseaseGroup().getItemEnabledProvider().test(fifth), "the others are disabled at the cap");
		assertTrue(text(layout.getFilterCard()).contains("Up to 4 diseases can be shown in this view."), "the note under the diseases");

		Disease first = offered.get(0);
		layout.getDiseaseGroup().deselect(first);
		assertTrue(layout.getDiseaseGroup().getItemEnabledProvider().test(fifth));
		layout.getDiseaseGroup().select(fifth);
		assertTrue(layout.getCases().containsKey(fifth), "a ticked disease that was not loaded is loaded");
		assertTrue(layout.getChart().getScript().contains(fifth.toString()));
	}

	@Test
	void testNoDiseaseMarkedClimateSensitive() {

		for (Disease disease : MARKED) {
			de.symeda.sormas.api.disease.DiseaseConfigurationDto configuration = getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
			configuration.setClimateSensitive(false);
			getDiseaseConfigurationFacade().saveDiseaseConfiguration(configuration);
		}
		getDiseaseConfigurationFacade().loadData();

		ClimateDashboardLayout layout = new ClimateDashboardLayout(nationalUser(), JurisdictionLevel.NATION);
		assertTrue(layout.getOfferedDiseases().isEmpty(), "no shipped list: only what an admin marks");
		String note = "No climate-sensitive diseases are configured.";
		assertTrue(layout.getDiseaseGroup().isVisible(), "the Diseases caption stays, with the note under it");
		assertTrue(text(layout.getFilterCard()).contains(note), text(layout.getFilterCard()));

		layout.getRegionFilter().setValue(rdcf.region);
		layout.apply();
		assertEquals(note, layout.getEmptyCard().getValue());
		assertFalse(layout.getContent().isVisible());
	}

	@Test
	void testCountsWithoutPopulationAndIncidenceWithIt() {

		ClimateDashboardLayout layout = regionLayout();
		assertEquals(Measure.COUNT, layout.getMeasureGroup().getValue(), "no population data");
		assertFalse(layout.getMeasureGroup().getItemEnabledProvider().test(Measure.INCIDENCE));
		assertTrue(text(layout.getDisplayCard()).contains("Showing counts: no population data for Bagaces."), text(layout.getDisplayCard()));

		creator.createPopulationData(rdcf.region, rdcf.district, 100_000, new Date());
		layout.apply();
		assertEquals(Measure.INCIDENCE, layout.getMeasureGroup().getValue(), "incidence, the default, once every district has a population");

		layout.getMeasureGroup().setValue(Measure.COUNT);
		layout.apply();
		assertEquals(Measure.COUNT, layout.getMeasureGroup().getValue(), "the user's choice is kept");
		layout.getMeasureGroup().setValue(Measure.INCIDENCE);
		assertFalse(text(layout.getDisplayCard()).contains("Showing counts"));
		assertTrue(layout.getChart().getScript().contains("(per 100,000)"));
	}

	@Test
	void testChartPanelsBandsShadingAndDetails() {

		Long location = districtLocation(10.52657, -84.90845);
		EpiWeek week = DateHelper.getPreviousEpiWeek(lastWeek);
		// three reference years with 1 mm a day in the week and its neighbours, then 30 mm a day: unusual
		int year = week.getYear();
		for (int y = year - 3; y < year; y++) {
			for (int w = week.getWeek() - 1; w <= week.getWeek() + 1; w++) {
				fillWeek(location, new EpiWeek(y, w), 1.0);
			}
		}
		fillWeek(location, week, 30.0);
		fillWeek(location, lastWeek, 1.0);
		creator.createCase(user.toReference(), rdcf, c -> {
			c.setDisease(Disease.DENGUE);
			c.setCaseClassification(CaseClassification.SUSPECT);
			c.setReportDate(DateHelper.getEpiWeekStart(lastWeek));
			c.getSymptoms().setOnsetDate(null);
		});

		ClimateDashboardLayout layout = regionLayout();
		layout.getDistrictFilter().setValue(rdcf.district);
		layout.apply();
		layout.getDiseaseGroup().setValue(java.util.Collections.singleton(Disease.DENGUE));
		String script = layout.getChart().getScript();
		dump("dashboard-panels.js", script);

		assertTrue(script.contains(Disease.DENGUE + " (cases)"), "a panel per disease, named with its measure");
		assertTrue(script.contains("Usual range not shown: too few years of data"), "the reason when there is no usual range");
		assertTrue(script.contains("Rain (mm)") && script.contains("Temp (°C)") && script.contains("Humidity (%)"));
		assertTrue(script.contains(ClimateDashboardChart.SHADING_COLOR), "the unusual week is shaded");
		assertTrue(script.contains("Unusual week: rainfall"));
		assertTrue(script.contains("Normal range"));
		assertTrue(script.contains(" 1 case (1 by report date)"), "the details line counts the report-date fallback");
		assertTrue(script.contains("above normal"));
		assertTrue(script.contains("no data (0 of 7 days)"), "a week without data is a gap, never 0");
		assertEquals("Normal range: " + (year - 5) + "–" + (year - 1), layout.getChart().getStatus().getValue());
		assertFalse(layout.getChart().getCredit().isVisible(), "imported data only");
		assertTrue(script.contains("c.pointerY<top+h+8"), "the details line follows the part of the chart in view");
		assertEquals(
			chartHeight(ClimateDashboardChart.panelHeights(1, 0, 3, ClimateDashboardChart.fillHeight(0))),
			layout.getChart().getChartHeight(),
			"rows in pixels, fitting the window (" + ClimateDashboardChart.DEFAULT_WINDOW_HEIGHT + " px without a page)");

		layout.getShadeSelect().setValue(null);
		assertFalse(layout.getChart().getScript().contains(ClimateDashboardChart.SHADING_COLOR), "shading can be switched off");

		layout.getClimateGroup().deselect(ClimateVariable.HUMIDITY, ClimateVariable.TEMPERATURE);
		assertFalse(layout.getChart().getScript().contains("Humidity (%)"));
		dump("dashboard-fill.js", layout.getChart().getScript());
		assertEquals(
			chartHeight(ClimateDashboardChart.panelHeights(1, 0, 1, ClimateDashboardChart.fillHeight(0))),
			layout.getChart().getChartHeight(),
			"two panels share the window too");
		layout.getClimateGroup().clear();
		assertTrue(layout.getChart().getScript().contains(Disease.DENGUE + " (cases)"), "the disease panel alone");
		layout.getDiseaseGroup().clear();
		assertTrue(layout.getEmptyCard().isVisible(), "a filled note, never a blank chart");
		assertFalse(layout.getContent().isVisible());
	}

	@Test
	void testLagShiftsTheClimatePanelsAndTheShading() {

		Long location = districtLocation(10.52657, -84.90845);
		EpiWeek week = DateHelper.getPreviousEpiWeek(lastWeek);
		int year = week.getYear();
		for (int y = year - 3; y < year; y++) {
			for (int w = week.getWeek() - 1; w <= week.getWeek() + 1; w++) {
				fillWeek(location, new EpiWeek(y, w), 1.0);
			}
		}
		fillWeek(location, week, 30.0);

		ClimateDashboardLayout layout = regionLayout();
		layout.getDistrictFilter().setValue(rdcf.district);
		layout.apply();
		// the unusual week is the 51st of the 52 shown (index 50)
		assertTrue(layout.getChart().getScript().contains("{from:49.5,to:50.5,"), "shaded where it happened");
		assertEquals("Shift climate by: 0 weeks", layout.getLagSlider().getCaption());

		layout.getLagSlider().setValue(1d);
		String script = layout.getChart().getScript();
		dump("dashboard-lag.js", script);
		assertEquals("Shift climate by: 1 week", layout.getLagSlider().getCaption());
		assertTrue(script.contains("{from:50.5,to:51.5,"), "the shading moves with the climate panels");
		assertFalse(script.contains("{from:49.5,to:50.5,"));
		assertTrue(script.contains("— 1 week earlier"), "the panel names state the shift");
		assertTrue(
			script.contains(String.format("Climate of week %d/%d (1 week earlier):", week.getWeek(), week.getYear())),
			"the last week shows the climate of the week before");

		layout.getLagSlider().setValue((double) ClimateDashboardLayout.MAX_LAG);
		List<EpiWeek> shown = layout.getClimate().getShownWeeks().stream().map(w -> w.getWeek()).collect(Collectors.toList());
		EpiWeek leadStart = layout.getClimate().getWeeks().get(0).getWeek();
		assertTrue(
			ClimateDashboardChart
				.details(
					shown.get(0),
					0,
					layout.getClimate(),
					java.util.Collections.emptyList(),
					java.util.EnumSet.allOf(ClimateVariable.class),
					Measure.COUNT,
					12)
				.contains(String.format("Climate of week %d/%d (12 weeks earlier):", leadStart.getWeek(), leadStart.getYear())),
			"the first week still has climate: 12 weeks are loaded before the period");
		assertEquals(52, layout.getClimate().getShownWeeks().size(), "no new request: the same series");
	}

	@Test
	void testHeatmapView() {

		Long location = districtLocation(10.52657, -84.90845);
		EpiWeek week = DateHelper.getPreviousEpiWeek(lastWeek);
		int year = week.getYear();
		for (int y = year - 3; y < year; y++) {
			for (int w = week.getWeek() - 1; w <= week.getWeek() + 1; w++) {
				fillWeek(location, new EpiWeek(y, w), 1.0);
			}
		}
		fillWeek(location, week, 30.0);
		for (int i = 0; i < 4; i++) {
			creator.createCase(user.toReference(), rdcf, c -> {
				c.setDisease(Disease.DENGUE);
				c.setCaseClassification(CaseClassification.SUSPECT);
				c.setReportDate(DateHelper.getEpiWeekStart(lastWeek));
				c.getSymptoms().setOnsetDate(DateHelper.getEpiWeekStart(lastWeek));
			});
		}

		ClimateDashboardLayout layout = regionLayout();
		layout.getDistrictFilter().setValue(rdcf.district);
		layout.apply();
		List<Disease> offered = layout.getOfferedDiseases();

		layout.getViewGroup().setValue(ClimateDashboardLayout.View.HEATMAP);
		layout.getDiseaseGroup()
			.setValue(new java.util.HashSet<>(offered.subList(0, Math.min(offered.size(), ClimateDashboardLayout.HEATMAP_DISEASE_CAP))));
		assertEquals(
			Math.min(offered.size(), ClimateDashboardLayout.HEATMAP_DISEASE_CAP),
			layout.selectedDiseases().size(),
			"up to 10 in the heatmap");
		String script = layout.getChart().getScript();
		dump("dashboard-heatmap.js", script);
		assertTrue(script.contains("type:'heatmap'"), "one heatmap row per disease");
		assertTrue(script.contains("colorAxis:{min:0,max:1"), "coloured by the share of each disease's busiest week");
		assertTrue(script.contains("colorAxis:false"), "the other series keep their colours");
		assertTrue(script.contains("Each row relative to its busiest week in the period (cases)"));
		assertTrue(script.contains("borderColor:'" + ClimateDashboardChart.OUTLINE_COLOR + "',borderWidth:2"), "the unusual week is outlined");
		assertTrue(script.contains("Rain (mm)"), "the climate panels stay below");
		assertTrue(script.contains("4 cases"), "the details line shows the real figures");
		int[] heatmapHeights = ClimateDashboardChart
			.panelHeights(0, ClimateDashboardChart.HEATMAP_ROW * layout.selectedDiseases().size(), 3, ClimateDashboardChart.fillHeight(0));
		assertEquals(ClimateDashboardChart.HEATMAP_ROW * layout.selectedDiseases().size(), heatmapHeights[0], "the heatmap keeps its rows");
		assertEquals(chartHeight(heatmapHeights), layout.getChart().getChartHeight());

		layout.getViewGroup().setValue(ClimateDashboardLayout.View.PANELS);
		assertEquals(offered.subList(0, ClimateDashboardLayout.PANELS_DISEASE_CAP), layout.selectedDiseases(), "back to panels keeps the first 4");
		assertEquals(ClimateDashboardLayout.PANELS_DISEASE_CAP, layout.getDiseaseGroup().getValue().size());
		assertFalse(layout.getChart().getScript().contains("type:'heatmap'"));
	}

	@Test
	void testDistrictWithoutPoint() {

		ClimateDashboardLayout layout = regionLayout();
		layout.getDistrictFilter().setValue(rdcf.district);
		layout.apply();

		assertTrue(layout.getChart().getScript().contains("No climate data for Bagaces: the district has no point."));
		assertFalse(layout.getShadeSelect().isEnabled(), "no normals, no shading");
		assertTrue(
			text(layout.getDisplayCard()).contains("Shading needs normal ranges (3 years of climate history)."),
			text(layout.getDisplayCard()));
		assertEquals("No climate data yet", layout.getChart().getStatus().getValue());
	}

	@Test
	void testPanelsFitTheWindow() {

		// a laptop showing about 695 px of page: 1 disease and 3 climate panels, details strip, axis and legend all fit
		int laptop = 695;
		int[] four = ClimateDashboardChart.panelHeights(1, 0, 3, ClimateDashboardChart.fillHeight(laptop));
		assertEquals(laptop - ClimateDashboardChart.WINDOW_MARGIN, chartHeight(four), 1.0, "the whole chart fits the window");
		assertTrue(four[0] > four[1], "a disease panel is a little taller than a climate panel");
		assertTrue(four[1] >= ClimateDashboardChart.MIN_PANEL);

		// 4 diseases and 3 climate panels do not fit: each keeps the minimum and the page scrolls
		int[] seven = ClimateDashboardChart.panelHeights(4, 0, 3, ClimateDashboardChart.fillHeight(laptop));
		assertTrue(java.util.Arrays.stream(seven).allMatch(h -> h == ClimateDashboardChart.MIN_PANEL), java.util.Arrays.toString(seven));
		assertTrue(chartHeight(seven) > laptop);

		// a taller window gives taller panels
		int[] two = ClimateDashboardChart.panelHeights(1, 0, 1, ClimateDashboardChart.fillHeight(1000));
		assertEquals(1000 - ClimateDashboardChart.WINDOW_MARGIN, chartHeight(two), 1.0);
		assertTrue(two[0] > 2 * ClimateDashboardChart.DISEASE_PANEL, two[0] + " px");

		// the heatmap keeps its rows; the climate panel takes the rest
		int[] heatmap = ClimateDashboardChart.panelHeights(0, 5 * ClimateDashboardChart.HEATMAP_ROW, 1, ClimateDashboardChart.fillHeight(laptop));
		assertEquals(5 * ClimateDashboardChart.HEATMAP_ROW, heatmap[0]);
		assertEquals(laptop - ClimateDashboardChart.WINDOW_MARGIN, chartHeight(heatmap), 1.0);

		assertEquals(0, ClimateDashboardChart.panelHeights(0, 0, 0, 500).length);
	}

	/**
	 * @return the chart's height for these panels: the room for the details line, each panel under its name band, the gaps
	 *         and the room for the axis and legend
	 */
	private static int chartHeight(int[] panels) {
		return ClimateDashboardChart.PLOT_TOP
			+ java.util.Arrays.stream(panels).sum()
			+ panels.length * ClimateDashboardChart.TITLE_BAND
			+ (panels.length - 1) * ClimateDashboardChart.PANEL_GAP
			+ ClimateDashboardChart.MARGIN_BOTTOM;
	}

	/**
	 * Marks the diseases climate-sensitive, as an admin does in the disease configuration (decision 138: there is no shipped list).
	 */
	static void markClimateSensitive(de.symeda.sormas.api.disease.DiseaseConfigurationFacade facade, Disease... diseases) {
		for (Disease disease : diseases) {
			de.symeda.sormas.api.disease.DiseaseConfigurationDto configuration = facade.getDiseaseConfiguration(disease);
			configuration.setClimateSensitive(true);
			facade.saveDiseaseConfiguration(configuration);
		}
		facade.loadData();
	}

	private ClimateDashboardLayout regionLayout() {
		ClimateDashboardLayout layout = new ClimateDashboardLayout(nationalUser(), JurisdictionLevel.NATION);
		layout.getRegionFilter().setValue(rdcf.region);
		layout.getDiseaseGroup().select(Disease.DENGUE);
		layout.apply();
		return layout;
	}

	/**
	 * @return whether the component sits somewhere inside the container
	 */
	private static boolean isInside(com.vaadin.ui.Component component, com.vaadin.ui.Component container) {
		for (com.vaadin.ui.Component c = component; c != null; c = c.getParent()) {
			if (c == container) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return a user without region or district, as a national user is
	 */
	private UserDto nationalUser() {
		UserDto national = UserDto.build();
		national.setUserName("climate-dashboard");
		return national;
	}

	private Long districtLocation(double latitude, double longitude) {
		DistrictDto district = getDistrictFacade().getByUuid(rdcf.district.getUuid());
		district.setClimateLatitude(latitude);
		district.setClimateLongitude(longitude);
		getDistrictFacade().save(district);
		return executeInTransaction(em -> {
			District entity = getBean(DistrictService.class).getByUuid(rdcf.district.getUuid());
			if (entity.getClimateLocation() == null) {
				getBean(ClimateLinkingService.class).linkDistrict(entity);
			}
			return entity.getClimateLocation().getId();
		});
	}

	private void fillWeek(Long location, EpiWeek week, double dailyRainfall) {
		LocalDate monday = ClimateCaptions.toLocalDate(DateHelper.getEpiWeekStart(week));
		executeInTransaction(em -> {
			for (int i = 0; i < 7; i++) {
				ClimateObservation observation = new ClimateObservation();
				observation.setClimateLocation(em.find(ClimateLocation.class, location));
				observation.setObservationDate(ClimateCaptions.toDate(monday.plusDays(i)));
				observation.setSource(ClimateDataSource.IMPORT);
				observation.setRainfall(dailyRainfall);
				observation.setTemperatureMin(20.0);
				observation.setTemperatureMean(25.0);
				observation.setTemperatureMax(30.0);
				observation.setRelativeHumidity(70.0);
				observation.setHumidityMeasure(HumidityMeasure.DAILY_MEAN);
				em.persist(observation);
			}
		});
	}

	/**
	 * Writes a chart script to the folder named by the system property climate.chartDump, to render it with the bundled
	 * Highcharts in a browser; does nothing without the property.
	 */
	static void dump(String name, String script) {
		String folder = System.getProperty("climate.chartDump");
		if (folder == null) {
			return;
		}
		try {
			java.nio.file.Files.write(java.nio.file.Paths.get(folder, name), script.getBytes(java.nio.charset.StandardCharsets.UTF_8));
		} catch (java.io.IOException e) {
			throw new java.io.UncheckedIOException(e);
		}
	}

	static String text(Component component) {
		return collect(component).stream().collect(Collectors.joining("\n"));
	}

	private static List<String> collect(Component component) {
		List<String> lines = new java.util.ArrayList<>();
		if (component instanceof Label && component.isVisible()) {
			lines.add(((Label) component).getValue());
		}
		if (component instanceof com.vaadin.ui.HasComponents && component.isVisible()) {
			for (Component child : (com.vaadin.ui.HasComponents) component) {
				lines.addAll(collect(child));
			}
		}
		return lines;
	}
}
