/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.caze.surveillancereport;

import java.util.function.Consumer;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class SurveillanceReportListComponent extends SideComponent {

	private static final long serialVersionUID = -8922146236400907575L;

	private final SurveillanceReportList list;

	public SurveillanceReportListComponent(CaseReferenceDto caze, Consumer<Runnable> actionCallback, UserRight editRight, boolean isEditAllowed) {
		super(I18nProperties.getString(Strings.headingSurveillanceReports), actionCallback);
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		list = new SurveillanceReportList(caze, editRight, isEditAllowed);
		addComponent(list);
		list.reload();

		if (UiUtil.permitted(isEditAllowed, editRight)) {
			addCreateButton(
				I18nProperties.getCaption(Captions.surveillanceReportNewReport),
				() -> ControllerProvider.getSurveillanceReportController().createSurveillanceReport(caze, list::reload));
		}
	}
}
