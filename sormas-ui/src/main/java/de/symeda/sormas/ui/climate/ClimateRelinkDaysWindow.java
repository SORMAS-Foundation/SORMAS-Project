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

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateJobIndexDto;
import de.symeda.sormas.api.climate.ClimateObservationEntryDto;
import de.symeda.sormas.api.climate.ClimateRelinkDayDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * The days a district relink did not copy, because the new point already had equal or better data: the district's own
 * value (still on the old point) beside the value the new point kept. A user who may correct climate data can enter the
 * district's value as a manual value, which then wins.
 */
public class ClimateRelinkDaysWindow extends VerticalLayout {

	private static final long serialVersionUID = 3150936580917542210L;

	private final ClimateJobIndexDto relink;
	private final Label summary = new Label();
	private final Grid<ClimateRelinkDayDto> days = new Grid<>();

	public static void open(ClimateJobIndexDto relink) {
		ClimateRelinkDaysWindow window = new ClimateRelinkDaysWindow(relink);
		VaadinUiUtil.showPopupWindow(window, String.format(I18nProperties.getString(Strings.headingClimateRelinkDays), relink.getDistrictName()));
	}

	private ClimateRelinkDaysWindow(ClimateJobIndexDto relink) {

		this.relink = relink;
		setMargin(true);
		setSpacing(true);
		setWidth(1000, Unit.PIXELS);

		summary.setWidthFull();
		Label correctionInfo = new Label(I18nProperties.getString(Strings.infoClimateRelinkCorrection));
		correctionInfo.setWidthFull();
		correctionInfo.addStyleName(CssStyles.LABEL_SECONDARY);

		days.setWidthFull();
		days.addColumn(d -> DateFormatHelper.formatDate(d.getDate())).setCaption(caption(ClimateRelinkDayDto.DATE));
		days.addColumn(d -> ClimateCaptions.values(d.getDistrictValue())).setCaption(caption(ClimateRelinkDayDto.DISTRICT_VALUE));
		days.addColumn(d -> ClimateCaptions.values(d.getKeptValue())).setCaption(caption(ClimateRelinkDayDto.KEPT_VALUE));
		days.addColumn(d -> I18nProperties.getString(d.isSameValues() ? Strings.climateRelinkSame : Strings.climateRelinkDiffers))
			.setCaption(caption(ClimateRelinkDayDto.SAME_VALUES));
		if (UiUtil.permitted(UserRight.CLIMATE_EDIT)) {
			days.addComponentColumn(this::correctButton);
		}

		addComponents(summary, correctionInfo, days);
		load();
	}

	private void load() {

		List<ClimateRelinkDayDto> relinkDays = FacadeProvider.getClimateJobFacade().getRelinkDays(relink.getUuid());
		long differing = relinkDays.stream().filter(d -> !d.isSameValues()).count();
		String movedBy = relink.getStartedBy() != null ? String.format(I18nProperties.getString(Strings.climateRelinkBy), relink.getStartedBy()) : "";
		summary.setValue(
			String.format(
				I18nProperties.getString(Strings.infoClimateRelinkDays),
				relink.getDistrictName(),
				DateFormatHelper.formatDate(relink.getCreationDate()),
				movedBy,
				ClimateJobsGrid.rows(relink),
				ClimateSummaryStrip.formatCount(differing)));
		days.setItems(relinkDays);
		days.setHeightByRows(Math.max(1, Math.min(relinkDays.size(), 12)));
	}

	private Button correctButton(ClimateRelinkDayDto day) {

		Button correct = ButtonHelper.createButton(Captions.climateUseDistrictValue, e -> {
			ClimateObservationEntryDto entry = day.getDistrictValue().toEntry();
			entry.setClimateLocationUuid(day.getKeptValue().getClimateLocationUuid());
			try {
				FacadeProvider.getClimateObservationFacade().saveManualEntry(entry);
			} catch (ValidationRuntimeException ex) {
				Notification.show(ex.getMessage(), Notification.Type.ERROR_MESSAGE);
				return;
			}
			Notification.show(I18nProperties.getString(Strings.messageClimateObservationSaved), Notification.Type.TRAY_NOTIFICATION);
			load();
		}, ValoTheme.BUTTON_SMALL);
		correct.setVisible(!day.isSameValues());
		return correct;
	}

	private static String caption(String property) {
		return I18nProperties.getPrefixCaption(ClimateRelinkDayDto.I18N_PREFIX, property);
	}
}
