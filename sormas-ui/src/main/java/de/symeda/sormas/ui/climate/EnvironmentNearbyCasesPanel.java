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

package de.symeda.sormas.ui.climate;

import static de.symeda.sormas.ui.climate.ClimateChartScript.string;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.climate.ClimateNearbyCasesDto;
import de.symeda.sormas.api.climate.ClimateNearbyCasesWeekDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * "Cases near this site" on the environment Climate data tab (decisions 95 and 96): weekly counts of cases placed within
 * the radius by their probable infection environment, else their home address, with cases without GPS in the same
 * community as a lighter segment. Closeness only: no wording or colour suggests cause or alarm (decision 84). Selecting a
 * week lists its cases.
 */
public class EnvironmentNearbyCasesPanel extends VerticalLayout {

	private static final long serialVersionUID = 8146730596233706146L;

	/** The radius options in km (UI spec); 2 km by default. */
	public static final List<Double> RADII = Arrays.asList(1.0, 2.0, 5.0);
	public static final double DEFAULT_RADIUS = 2.0;

	private static final long WEEK = 7L * 24 * 60 * 60 * 1000;

	private final EnvironmentReferenceDto environment;
	private final ComboBox<Disease> disease = new ComboBox<>(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
	private final ComboBox<Double> radius = new ComboBox<>(I18nProperties.getCaption(Captions.climateNearbyRadius), RADII);
	private final Label note = new Label();
	private final ClimateChart chart = new ClimateChart((series, key) -> weekClicked(key));

	private LocalDate from;
	private LocalDate to;
	private ClimateNearbyCasesDto nearby;
	private String lastScript;

	public EnvironmentNearbyCasesPanel(EnvironmentReferenceDto environment, boolean byCommunityOnly) {

		this.environment = environment;
		setMargin(false);
		setSpacing(false);
		setWidthFull();
		addStyleName(CssStyles.VSPACE_TOP_4);

		Label heading = new Label(I18nProperties.getCaption(Captions.climateNearbyCases));
		heading.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
		Label help = new Label(I18nProperties.getString(Strings.climateNearbyHelp));
		help.setWidthFull();

		disease.setItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, null, true));
		disease.setPlaceholder(I18nProperties.getCaption(Captions.climateNearbyAllDiseases));
		disease.setEmptySelectionCaption(I18nProperties.getCaption(Captions.climateNearbyAllDiseases));
		disease.addValueChangeListener(e -> reload());
		radius.setItemCaptionGenerator(km -> String.format(I18nProperties.getString(Strings.climateNearbyRadiusOption), ClimateCaptions.number(km)));
		radius.setEmptySelectionAllowed(false);
		radius.setValue(DEFAULT_RADIUS);
		radius.setVisible(!byCommunityOnly);
		radius.addValueChangeListener(e -> reload());
		CssStyles.style(CssStyles.VSPACE_NONE, disease, radius);
		HorizontalLayout filters = new HorizontalLayout(disease, radius);
		filters.setMargin(false);
		filters.addStyleName(CssStyles.CLIMATE_NEARBY_FILTERS);

		note.setWidthFull();
		chart.setWidthFull();
		chart.setHeight(220, Unit.PIXELS);

		addComponents(heading, help, filters, note, chart);
	}

	/**
	 * Shows the period; called whenever the tab's period changes.
	 */
	public void update(LocalDate from, LocalDate to) {
		this.from = from;
		this.to = to;
		reload();
	}

	private void reload() {

		if (from == null) {
			return;
		}
		double km = radius.getValue() != null ? radius.getValue() : DEFAULT_RADIUS;
		nearby = FacadeProvider.getClimateCaseFacade().getNearbyCases(environment, disease.getValue(), km, from, to);

		int community = nearby.getCommunityTotal();
		int total = nearby.getWeeks().stream().mapToInt(ClimateNearbyCasesWeekDto::getCount).sum() + community;
		if (nearby.isByCommunityOnly()) {
			note.setValue(I18nProperties.getString(Strings.climateNearbyByCommunityOnly));
		} else if (total == 0) {
			note.setValue(I18nProperties.getString(Strings.climateNearbyNone));
		} else if (community > 0) {
			note.setValue(String.format(I18nProperties.getString(Strings.climateNearbyCommunityNote), community));
		} else {
			note.setValue("");
		}
		note.setVisible(!note.getValue().isEmpty());
		lastScript = script(nearby, from, to);
		chart.setHcjs(lastScript);
	}

	private static String script(ClimateNearbyCasesDto nearby, LocalDate from, LocalDate to) {

		String radiusName = String.format(I18nProperties.getString(Strings.climateNearbyRadiusOption), ClimateCaptions.number(nearby.getRadiusKm()));
		StringBuilder details = new StringBuilder("var details = [");
		StringBuilder within = new StringBuilder();
		StringBuilder community = new StringBuilder();
		List<ClimateNearbyCasesWeekDto> weeks = nearby.getWeeks();
		for (int i = 0; i < weeks.size(); i++) {
			ClimateNearbyCasesWeekDto week = weeks.get(i);
			long x = middle(week.getWeek());
			details.append(string(ClimateChartScript.html(weekDetails(nearby, week)))).append(',');
			within.append("{x:").append(x).append(",y:").append(week.getCount()).append(",key:'").append(i).append("',i:").append(i).append("},");
			community.append("{x:")
				.append(x)
				.append(",y:")
				.append(week.getCommunityCount())
				.append(",key:'")
				.append(i)
				.append("',i:")
				.append(i)
				.append("},");
		}
		details.append("];\n");

		StringBuilder js = new StringBuilder(details);
		js.append("var options = {chart:{type:'column',marginLeft:")
			.append(ClimateChartScript.MARGIN_LEFT)
			.append(",marginRight:")
			.append(ClimateChartScript.MARGIN_RIGHT)
			.append(",marginTop:28,animation:false},title:{text:null},credits:{enabled:false},exporting:{enabled:false},time:{useUTC:true},")
			.append("legend:{enabled:true,itemStyle:{fontWeight:'normal'}},")
			.append("xAxis:{type:'datetime',min:")
			.append(ClimateChartScript.axisMin(from))
			.append(",max:")
			.append(ClimateChartScript.axisMax(to))
			.append(",dateTimeLabelFormats:{day:'")
			.append(ClimateChartScript.dayMonthFormat())
			.append("',week:'")
			.append(ClimateChartScript.dayMonthFormat())
			.append("'}},")
			.append("yAxis:{min:0,allowDecimals:false,title:{text:")
			.append(string(I18nProperties.getString(Strings.climateNearbyAxis)))
			.append(",rotation:0,align:'high',textAlign:'left',offset:0,x:2,y:-5,reserveSpace:false,style:{fontWeight:'normal',color:'#555555'}},")
			.append("showLastLabel:false,gridLineDashStyle:'Dot'},")
			.append("tooltip:{useHTML:true,formatter:function(){return details[this.point.i];}},")
			.append("plotOptions:{column:{stacking:'normal',pointRange:")
			.append(WEEK)
			.append(
				",pointPadding:0.08,groupPadding:0,borderWidth:0,cursor:'pointer',animation:false,states:{inactive:{opacity:1}},point:{events:{click:")
			.append(ClimateChart.clickHandler())
			.append("}}}},series:[");
		if (!nearby.isByCommunityOnly()) {
			js.append("{id:'within',name:").append(string(radiusName)).append(",color:'#5b7083',index:1,data:[").append(within).append("]},");
		}
		js.append("{id:'community',name:")
			.append(
				string(
					I18nProperties
						.getString(nearby.isByCommunityOnly() ? Strings.climateNearbySeriesCommunityOnly : Strings.climateNearbySeriesCommunity)))
			.append(",color:'#b8c4ce',index:0,data:[")
			.append(community)
			.append("]}]};");
		return js.toString();
	}

	/**
	 * @return "Wk 44-2026 (26.10 - 01.11): 3 within 2 km (1 by probable infection environment, 2 by home address) · 1
	 *         without GPS in the same community"
	 */
	static String weekDetails(ClimateNearbyCasesDto nearby, ClimateNearbyCasesWeekDto week) {
		StringBuilder text = new StringBuilder(week.getWeek().toString()).append(": ");
		if (!nearby.isByCommunityOnly()) {
			text.append(
				String.format(
					I18nProperties.getString(Strings.climateNearbyWeekRadius),
					week.getCount(),
					ClimateCaptions.number(nearby.getRadiusKm()),
					week.getExposureCount(),
					week.getHomeCount()))
				.append(" · ");
		}
		text.append(String.format(I18nProperties.getString(Strings.climateNearbyWeekCommunity), week.getCommunityCount()));
		if (week.getCount() + week.getCommunityCount() > 0) {
			text.append(" · ").append(I18nProperties.getString(Strings.climateNearbyWeekClick));
		}
		return text.toString();
	}

	/**
	 * @return the middle of the epi week on the datetime axis, so its column covers the week's days
	 */
	private static long middle(EpiWeek week) {
		LocalDate monday = ClimateCaptions.toLocalDate(DateHelper.getEpiWeekStart(week));
		return ClimateChartScript.x(monday) + WEEK / 2 - 12L * 60 * 60 * 1000;
	}

	private void weekClicked(String key) {
		try {
			int index = Integer.parseInt(key);
			if (nearby != null && index >= 0 && index < nearby.getWeeks().size()) {
				EnvironmentNearbyCasesWindow.open(nearby, nearby.getWeeks().get(index));
			}
		} catch (NumberFormatException e) {
			// not a week
		}
	}

	ClimateNearbyCasesDto getNearby() {
		return nearby;
	}

	String getScript() {
		return lastScript;
	}

	ComboBox<Double> getRadius() {
		return radius;
	}
}
