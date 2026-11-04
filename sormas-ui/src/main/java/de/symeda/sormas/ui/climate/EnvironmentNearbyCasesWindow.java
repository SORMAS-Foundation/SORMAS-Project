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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.climate.ClimateNearbyCasesDto;
import de.symeda.sormas.api.climate.ClimateNearbyCasesWeekDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * The cases near an environment in one week (decision 96), read through the case facade so jurisdiction and
 * pseudonymisation stay SORMAS's own. Each case opens in the Cases module.
 */
public final class EnvironmentNearbyCasesWindow {

	/** The most cases listed at once. */
	public static final int MAX_CASES = 100;

	private EnvironmentNearbyCasesWindow() {
	}

	/** One listed case with how it was placed. */
	static final class Row {

		private final CaseDataDto caze;
		private final String placedBy;

		Row(CaseDataDto caze, String placedBy) {
			this.caze = caze;
			this.placedBy = placedBy;
		}

		Date caseDate() {
			Date onset = caze.getSymptoms() != null ? caze.getSymptoms().getOnsetDate() : null;
			return onset != null ? onset : caze.getReportDate();
		}
	}

	public static void open(ClimateNearbyCasesDto nearby, ClimateNearbyCasesWeekDto week) {

		List<Row> rows = rows(week);
		int total = week.getCount() + week.getCommunityCount();

		VerticalLayout content = new VerticalLayout();
		content.setWidth(760, com.vaadin.server.Sizeable.Unit.PIXELS);
		Label help = new Label(I18nProperties.getString(Strings.climateNearbyHelp));
		help.setWidthFull();
		help.addStyleName(CssStyles.LABEL_SECONDARY);
		content.addComponent(help);
		if (total > rows.size()) {
			content.addComponent(new Label(String.format(I18nProperties.getString(Strings.climateNearbyTooMany), rows.size(), total)));
		}

		Window[] window = new Window[1];
		Grid<Row> grid = new Grid<>();
		grid.setWidthFull();
		grid.setHeightByRows(Math.max(1, Math.min(rows.size(), 10)));
		grid.addComponentColumn(row -> {
			Button link = new Button(DataHelper.getShortUuid(row.caze.getUuid()), e -> {
				window[0].close();
				ControllerProvider.getCaseController().navigateToCase(row.caze.getUuid());
			});
			link.addStyleName(ValoTheme.BUTTON_LINK);
			return link;
		}).setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.UUID));
		grid.addColumn(row -> row.caze.getDisease()).setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
		grid.addColumn(row -> row.caze.getCaseClassification())
			.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
		grid.addColumn(row -> DateFormatHelper.formatDate(row.caseDate())).setCaption(I18nProperties.getCaption(Captions.climateNearbyCaseDate));
		grid.addColumn(row -> row.placedBy).setCaption(I18nProperties.getCaption(Captions.climateNearbyPlacedBy));
		grid.setItems(rows);
		content.addComponent(grid);

		window[0] = VaadinUiUtil
			.showPopupWindow(content, String.format(I18nProperties.getString(Strings.headingClimateNearbyWeek), week.getWeek().toString()));
	}

	/**
	 * @return the week's cases, at most {@link #MAX_CASES}: those placed by probable infection environment, by home address,
	 *         then by community; cases the user may no longer see are left out
	 */
	static List<Row> rows(ClimateNearbyCasesWeekDto week) {

		Set<String> exposure = new HashSet<>(week.getExposureCaseUuids());
		Set<String> community = new HashSet<>(week.getCommunityCaseUuids());
		List<String> uuids = new ArrayList<>(week.getExposureCaseUuids());
		week.getCaseUuids().stream().filter(uuid -> !exposure.contains(uuid)).forEach(uuids::add);
		uuids.addAll(week.getCommunityCaseUuids());
		if (uuids.size() > MAX_CASES) {
			uuids = uuids.subList(0, MAX_CASES);
		}

		List<CaseDataDto> cases = uuids.isEmpty() ? new ArrayList<>() : FacadeProvider.getCaseFacade().getByUuids(uuids);
		List<Row> rows = new ArrayList<>(cases.size());
		for (String uuid : uuids) {
			cases.stream().filter(c -> c.getUuid().equals(uuid)).findFirst().ifPresent(caze -> {
				String placedBy = exposure.contains(uuid)
					? I18nProperties.getString(Strings.climateNearbyPlacedExposure)
					: community.contains(uuid)
						? I18nProperties.getString(Strings.climateNearbyPlacedCommunity)
						: I18nProperties.getString(Strings.climateNearbyPlacedHome);
				rows.add(new Row(caze, placedBy));
			});
		}
		return rows;
	}
}
