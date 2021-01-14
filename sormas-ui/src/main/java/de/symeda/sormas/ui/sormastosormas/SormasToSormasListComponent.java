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
import java.util.function.Consumer;

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
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.PaginationList;

public class SormasToSormasListComponent extends VerticalLayout {

	private static final long serialVersionUID = -7189942121987530912L;

	private SormasToSormasList sormasToSormasList;

	public SormasToSormasListComponent(CaseDataDto caze, boolean shareEnabled) {
		CaseReferenceDto caseRef = caze.toReference();

		sormasToSormasList = new SormasToSormasList(
			new SormasToSormasShareInfoCriteria().caze(caseRef),
			caze.getSormasToSormasOriginInfo() == null,
			Captions.sormasToSormasCaseNotShared,
			(i) -> ControllerProvider.getSormasToSormasController().syncCase(caze, i));

		initLayout(
			caze.getSormasToSormasOriginInfo(),
			sormasToSormasList,
			shareEnabled ? e -> ControllerProvider.getSormasToSormasController().shareCaseFromDetailsPage(caze, this) : null,
			(e) -> ControllerProvider.getSormasToSormasController().returnCase(caze));
	}

	public SormasToSormasListComponent(ContactDto contact, boolean shareEnabled) {
		ContactReferenceDto contactRef = contact.toReference();

		sormasToSormasList = new SormasToSormasList(
			new SormasToSormasShareInfoCriteria().contact(contactRef),
			contact.getSormasToSormasOriginInfo() == null,
			Captions.sormasToSormasCaseNotShared,
			(i) -> ControllerProvider.getSormasToSormasController().syncContact(contact, i));

		initLayout(
			contact.getSormasToSormasOriginInfo(),
			sormasToSormasList,
			shareEnabled ? e -> ControllerProvider.getSormasToSormasController().shareContactFromDetailsPage(contact, this) : null,
			(e) -> ControllerProvider.getSormasToSormasController().returnContact(contact));
	}

	public SormasToSormasListComponent(SampleDto sample) {
		SampleReferenceDto sampleRef = sample.toReference();

		sormasToSormasList = new SormasToSormasList(
			new SormasToSormasShareInfoCriteria().sample(sampleRef),
			sample.getSormasToSormasOriginInfo() == null,
			Captions.sormasToSormasCaseNotShared,
			null);

		initLayout(sample.getSormasToSormasOriginInfo(), sormasToSormasList, null, null);
	}

	private void initLayout(
		SormasToSormasOriginInfoDto originInfo,
		SormasToSormasList sormasToSormasList,
		Button.ClickListener shareButtonClickListener,
		Button.ClickListener returnButtonClickListener) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		if (originInfo != null) {
			addComponent(buildSormasOriginInfo(originInfo, returnButtonClickListener));
		}

		addComponent(sormasToSormasList);
		sormasToSormasList.reload();

		Label header = new Label(I18nProperties.getCaption(Captions.sormasToSormasListTitle));
		header.addStyleName(CssStyles.H3);
		componentHeader.addComponent(header);

		if (shareButtonClickListener != null) {
			Button shareButtonButton =
				ButtonHelper.createIconButton(Captions.sormasToSormasShare, VaadinIcons.SHARE, shareButtonClickListener, ValoTheme.BUTTON_PRIMARY);

			componentHeader.addComponent(shareButtonButton);
			componentHeader.setComponentAlignment(shareButtonButton, Alignment.MIDDLE_RIGHT);
		}
	}

	public void reloadList() {
		sormasToSormasList.reload();
	}

	private static class SormasToSormasList extends PaginationList<SormasToSormasShareInfoDto> {

		private static final long serialVersionUID = -4659924105492791566L;

		private final SormasToSormasShareInfoCriteria criteria;
		private final Label placeholderLabel;
		private final Consumer<SormasToSormasShareInfoDto> syncListener;

		public SormasToSormasList(
			SormasToSormasShareInfoCriteria criteria,
			boolean showPlaceholder,
			String placeholderCaptionTag,
			Consumer<SormasToSormasShareInfoDto> syncListener) {
			super(5);

			this.criteria = criteria;

			this.placeholderLabel = new Label(placeholderCaptionTag != null ? I18nProperties.getCaption(placeholderCaptionTag) : null);
			this.placeholderLabel.setVisible(showPlaceholder);
			this.syncListener = syncListener;
		}

		@Override
		public void reload() {
			List<SormasToSormasShareInfoDto> shareInfos =
				FacadeProvider.getSormasToSormasFacade().getShareInfoIndexList(criteria, 0, maxDisplayedEntries * 20);

			setEntries(shareInfos);
			if (shareInfos.isEmpty()) {
				updatePaginationLayout();
				listLayout.addComponent(placeholderLabel);
			} else {
				listLayout.removeComponent(placeholderLabel);
				showPage(1);
			}
		}

		@Override
		protected void drawDisplayedEntries() {
			List<SormasToSormasShareInfoDto> displayedEntries = getDisplayedEntries();

			for (SormasToSormasShareInfoDto shareInfo : displayedEntries) {
				SormasToSormasShareListEntry listEntry = new SormasToSormasShareListEntry(shareInfo, syncListener);
				listLayout.addComponent(listEntry);
			}
		}
	}

	private static class SormasToSormasShareListEntry extends HorizontalLayout {

		private static final long serialVersionUID = 7462585357530141263L;

		public SormasToSormasShareListEntry(
			SormasToSormasShareInfoDto shareInfo,
			Consumer<SormasToSormasShareInfoDto> syncListener) {
			setMargin(false);
			setSpacing(true);
			setWidth(100, Unit.PERCENTAGE);
			addStyleName(CssStyles.SORMAS_LIST_ENTRY);

			VerticalLayout infoLayout = new VerticalLayout();
			infoLayout.setWidth(100, Unit.PERCENTAGE);
			infoLayout.setMargin(false);
			infoLayout.setSpacing(false);

			Label targetLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedWith) + " " + shareInfo.getTarget());
			targetLabel.addStyleName(CssStyles.LABEL_BOLD);
			infoLayout.addComponent(targetLabel);

			Label senderLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedBy) + ": " + shareInfo.getSender().getCaption());
			infoLayout.addComponent(senderLabel);

			Label shareDateLabel = new Label(
				I18nProperties.getCaption(Captions.sormasToSormasSharedDate) + ": " + DateFormatHelper.formatDate(shareInfo.getCreationDate()));
			infoLayout.addComponent(shareDateLabel);

			if (!DataHelper.isNullOrEmpty(shareInfo.getComment())) {
				infoLayout.addComponent(new Label(shareInfo.getComment()));
			}

			addComponent(infoLayout);
			setExpandRatio(infoLayout, 1);

			if (syncListener != null && !shareInfo.isOwnershipHandedOver()) {
				addComponent(ButtonHelper.createIconButton(Captions.sormasToSormasSync, VaadinIcons.REFRESH, (e) -> {
					syncListener.accept(shareInfo);
				}));
			}
		}
	}

	private HorizontalLayout buildSormasOriginInfo(SormasToSormasOriginInfoDto originInfo, Button.ClickListener returnButtonClickListener) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(false);
		layout.setWidthFull();

		VerticalLayout infoLayout = new VerticalLayout();
		infoLayout.setMargin(false);
		infoLayout.setSpacing(false);
		infoLayout.setStyleName(CssStyles.VSPACE_3);

		ServerAccessDataReferenceDto serverAccessDataRef =
			FacadeProvider.getSormasToSormasFacade().getOrganizationRef(originInfo.getOrganizationId());

		Label senderOrganizationLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSentFrom) + " " + serverAccessDataRef);
		senderOrganizationLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_WHITE_SPACE_NORMAL);
		infoLayout.addComponent(senderOrganizationLabel);
		infoLayout.addComponent(new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedBy) + ": " + originInfo.getSenderName()));

		if (originInfo.getSenderEmail() != null) {
			infoLayout.addComponent(new Label(originInfo.getSenderEmail()));
		}

		if (originInfo.getSenderPhoneNumber() != null) {
			infoLayout.addComponent(new Label(originInfo.getSenderPhoneNumber()));
		}

		Label shareDateLabel = new Label(
			I18nProperties.getCaption(Captions.sormasToSormasSharedDate) + ": " + DateFormatHelper.formatDate(originInfo.getCreationDate()));
		infoLayout.addComponent(shareDateLabel);

		if (!DataHelper.isNullOrEmpty(originInfo.getComment())) {
			infoLayout.addComponent(new Label(originInfo.getComment()));
		}

		layout.addComponent(infoLayout);
		layout.setExpandRatio(infoLayout, 1);

		if (originInfo.isOwnershipHandedOver() && returnButtonClickListener != null) {
			layout.addComponent(ButtonHelper.createIconButton(Captions.sormasToSormasReturn, VaadinIcons.REPLY, returnButtonClickListener));
		}

		return layout;
	}
}
