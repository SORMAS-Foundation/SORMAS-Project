/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.specialcaseaccess;

import java.util.List;

import javax.validation.constraints.Null;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.specialcaseaccess.SpecialCaseAccessDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.PaginationList;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class SpecialCaseAccessSideComponent extends SideComponent {

	public SpecialCaseAccessSideComponent(CaseReferenceDto caze) {
		super(I18nProperties.getString(Strings.headingSpecailCaseAccess));

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		SpecialCaseAccessList list = new SpecialCaseAccessList(caze);
		addComponent(list);
		list.reload();

		addCreateButton(
			I18nProperties
				.getCaption(Captions.specialCaseAccessNew),
			() -> ControllerProvider.getSpecialCaseAccessController().create(caze, list::reload));
	}

	private static class SpecialCaseAccessList extends PaginationList<SpecialCaseAccessDto> {

		private final CaseReferenceDto caze;

		public SpecialCaseAccessList(CaseReferenceDto caze) {
			super(5);

			this.caze = caze;
		}

		@Override
		public void reload() {
			List<SpecialCaseAccessDto> specialAccesses = FacadeProvider.getSpecialCaseAccessFacade().getAllActiveByCase(caze);
			setEntries(specialAccesses);
			if (!specialAccesses.isEmpty()) {
				showPage(1);
			} else {
				listLayout.removeAllComponents();
				updatePaginationLayout();
				listLayout.addComponent(new Label(I18nProperties.getCaption(Captions.specailCaseAccessNoAccessGranted)));
			}
		}

		@Override
		protected void drawDisplayedEntries() {
			listLayout.removeAllComponents();
			for (SpecialCaseAccessDto specialCaseAccess : getDisplayedEntries()) {
				SpecialCaseAccessEntry entry = new SpecialCaseAccessEntry(specialCaseAccess);

				entry.addActionButton(
					specialCaseAccess.getUuid(),
					event -> ControllerProvider.getSpecialCaseAccessController().edit(specialCaseAccess, this::reload),
					true);
				listLayout.addComponent(entry);
			}
		}
	}

	private static class SpecialCaseAccessEntry extends SideComponentField {

		public SpecialCaseAccessEntry(SpecialCaseAccessDto specialCaseAccess) {
//			VerticalLayout mainLayout = new VerticalLayout();
//			mainLayout.setWidth(100, Unit.PERCENTAGE);
//			mainLayout.setMargin(false);
//			mainLayout.setSpacing(false);
//			addComponentToField(mainLayout);
			Language userLanguage = UserProvider.getCurrent().getUser().getLanguage();

			addComponentToField(createRow(null, specialCaseAccess.getAssignedTo().getCaption()));
			addComponentToField(
				createRow(
					I18nProperties.getPrefixCaption(SpecialCaseAccessDto.I18N_PREFIX, SpecialCaseAccessDto.END_DATE_TIME),
					DateHelper.formatLocalDate(specialCaseAccess.getEndDateTime(), userLanguage)));
			addComponentToField(
				createRow(
					I18nProperties.getPrefixCaption(SpecialCaseAccessDto.I18N_PREFIX, SpecialCaseAccessDto.ASSIGNED_BY),
					specialCaseAccess.getAssignedBy().getCaption()));
			addComponentToField(
				createRow(
					I18nProperties.getPrefixCaption(SpecialCaseAccessDto.I18N_PREFIX, SpecialCaseAccessDto.ASSIGNMENT_DATE),
					DateHelper.formatLocalDate(specialCaseAccess.getAssignmentDate(), userLanguage)));
		}

		private HorizontalLayout createRow(@Null String label, Object value) {
			HorizontalLayout row = new HorizontalLayout();
			row.setMargin(false);
			row.setSpacing(false);

			if (label != null) {
				Label rowLabel = new Label(DataHelper.toStringNullable(label) + ":");
				CssStyles.style(rowLabel, CssStyles.HSPACE_RIGHT_4);
				row.addComponent(rowLabel);
			}

			Label rowValue = new Label(DataHelper.toStringNullable(value));
			if (label == null) {
				rowValue.addStyleName(CssStyles.LABEL_BOLD);
			}
			row.addComponent(rowValue);

			return row;
		}

	}
}
