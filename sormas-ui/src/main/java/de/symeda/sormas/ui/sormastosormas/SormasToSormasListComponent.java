/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSourceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.PaginationList;

public class SormasToSormasListComponent extends VerticalLayout {

	private static final long serialVersionUID = -7189942121987530912L;

	private SormasToSormasList sormasToSormasList;

	public SormasToSormasListComponent(CaseDataDto caze) {
		CaseReferenceDto caseRef = caze.toReference();

		sormasToSormasList = new SormasToSormasList(new SormasToSormasShareInfoCriteria().caze(caseRef), Captions.sormasToSormasCaseNotShared);

		initLayout(
			caze.getSormasToSormasSource(),
			sormasToSormasList,
			e -> ControllerProvider.getSormasToSormasController().shareCaseToSormas(caseRef, this));
	}

	public SormasToSormasListComponent(ContactDto contact) {
		ContactReferenceDto contactRef = contact.toReference();

		sormasToSormasList = new SormasToSormasList(new SormasToSormasShareInfoCriteria().contact(contactRef), Captions.sormasToSormasCaseNotShared);

		initLayout(
			contact.getSormasToSormasSource(),
			sormasToSormasList,
			e -> ControllerProvider.getSormasToSormasController().shareContactToSormas(contactRef, this));
	}

	private void initLayout(SormasToSormasSourceDto sormasSource, SormasToSormasList sormasToSormasList, Button.ClickListener clickListener) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		if (sormasSource != null) {
			addComponent(buildSormasSourceInfo(sormasSource));
		}

		addComponent(sormasToSormasList);
		sormasToSormasList.reload();

		Label header = new Label(I18nProperties.getCaption(Captions.sormasToSormasListTitle));
		header.addStyleName(CssStyles.H3);
		componentHeader.addComponent(header);

		Button shareButtonButton =
			ButtonHelper.createIconButton(Captions.sormasToSormasShare, VaadinIcons.SHARE, clickListener, ValoTheme.BUTTON_PRIMARY);

		componentHeader.addComponent(shareButtonButton);
		componentHeader.setComponentAlignment(shareButtonButton, Alignment.MIDDLE_RIGHT);
	}

	public void reloadList() {
		sormasToSormasList.reload();
	}

	private static class SormasToSormasList extends PaginationList<SormasToSormasShareInfoDto> {

		private static final long serialVersionUID = -4659924105492791566L;

		private final SormasToSormasShareInfoCriteria criteria;
		private final String placeholderCaptionTag;

		public SormasToSormasList(SormasToSormasShareInfoCriteria criteria, String placeholderCaptionTag) {
			super(5);

			this.criteria = criteria;
			this.placeholderCaptionTag = placeholderCaptionTag;
		}

		@Override
		public void reload() {
			List<SormasToSormasShareInfoDto> shareInfos =
				FacadeProvider.getSormasToSormasFacade().getShareInfoIndexList(criteria, 0, maxDisplayedEntries * 20);

			setEntries(shareInfos);
			if (!shareInfos.isEmpty()) {
				showPage(1);
			} else {
				updatePaginationLayout();
				Label noEventLabel = new Label(I18nProperties.getCaption(placeholderCaptionTag));
				listLayout.addComponent(noEventLabel);
			}
		}

		@Override
		protected void drawDisplayedEntries() {
			List<SormasToSormasShareInfoDto> displayedEntries = getDisplayedEntries();

			for (SormasToSormasShareInfoDto shareInfo : displayedEntries) {
				SormasToSormasShareListEntry listEntry = new SormasToSormasShareListEntry(shareInfo);
				listLayout.addComponent(listEntry);
			}
		}
	}

	private static class SormasToSormasShareListEntry extends HorizontalLayout {

		private static final long serialVersionUID = 7462585357530141263L;

		public SormasToSormasShareListEntry(SormasToSormasShareInfoDto shareInfo) {
			setMargin(false);
			setSpacing(true);
			setWidth(100, Unit.PERCENTAGE);
			addStyleName(CssStyles.SORMAS_LIST_ENTRY);

			VerticalLayout mainLayout = new VerticalLayout();
			mainLayout.setWidth(100, Unit.PERCENTAGE);
			mainLayout.setMargin(false);
			mainLayout.setSpacing(false);
			addComponent(mainLayout);
			setExpandRatio(mainLayout, 1);

			Label healthDepartmentLabel =
				new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedWith) + " " + shareInfo.getHealthDepartmentId());
			healthDepartmentLabel.addStyleName(CssStyles.LABEL_BOLD);
			mainLayout.addComponent(healthDepartmentLabel);

			Label materialLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedBy) + ": " + shareInfo.getSender().getCaption());
			mainLayout.addComponent(materialLabel);
		}
	}

	private VerticalLayout buildSormasSourceInfo(SormasToSormasSourceDto sormasSource) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		layout.setStyleName(CssStyles.VSPACE_3);

		Label healthDepartmentLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSentBy) + " " + sormasSource.getHealthDepartment());
		healthDepartmentLabel.addStyleName(CssStyles.LABEL_BOLD);
		layout.addComponent(healthDepartmentLabel);
		layout.addComponent(new Label(sormasSource.getSenderName()));

		if (sormasSource.getSenderEmail() != null) {
			layout.addComponent(new Label(sormasSource.getSenderEmail()));
		}

		if (sormasSource.getSenderPhoneNumber() != null) {
			layout.addComponent(new Label(sormasSource.getSenderPhoneNumber()));
		}

		return layout;
	}
}
