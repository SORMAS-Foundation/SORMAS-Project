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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareTree;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.PaginationList;

public class SormasToSormasListComponent extends VerticalLayout {

	private static final long serialVersionUID = -7189942121987530912L;

	private Label shareListLabel;
	private final SormasToSormasList sormasToSormasList;

	private ShareDataLoader loadShares;

	public SormasToSormasListComponent(CaseDataDto caze, boolean shareEnabled) {

		sormasToSormasList = new SormasToSormasList(
			caze.getSormasToSormasOriginInfo() == null,
			Captions.sormasToSormasCaseNotShared,
			(i) -> ControllerProvider.getSormasToSormasController().syncCase(caze, i),
			(i) -> ControllerProvider.getSormasToSormasController().revokeShare(i, this::reloadList));

		initLayout(
			caze.getSormasToSormasOriginInfo(),
			() -> FacadeProvider.getSormasToSormasCaseFacade().getAllShares(caze.getUuid()),
			shareEnabled ? e -> ControllerProvider.getSormasToSormasController().shareCaseFromDetailsPage(caze, this) : null,
			(e) -> ControllerProvider.getSormasToSormasController().returnCase(caze));
	}

	public SormasToSormasListComponent(ContactDto contact, boolean shareEnabled) {
		sormasToSormasList = new SormasToSormasList(
			contact.getSormasToSormasOriginInfo() == null,
			Captions.sormasToSormasContactNotShared,
			(i) -> ControllerProvider.getSormasToSormasController().syncContact(contact, i),
			(i) -> ControllerProvider.getSormasToSormasController().revokeShare(i, this::reloadList));

		initLayout(
			contact.getSormasToSormasOriginInfo(),
			() -> FacadeProvider.getSormasToSormasContactFacade().getAllShares(contact.getUuid()),
			shareEnabled ? e -> ControllerProvider.getSormasToSormasController().shareContactFromDetailsPage(contact, this) : null,
			(e) -> ControllerProvider.getSormasToSormasController().returnContact(contact));
	}

	public SormasToSormasListComponent(SampleDto sample) {
		sormasToSormasList = new SormasToSormasList(sample.getSormasToSormasOriginInfo() == null, Captions.sormasToSormasSampleNotShared, null, null);

		initLayout(
			sample.getSormasToSormasOriginInfo(),
			() -> FacadeProvider.getSormasToSormasShareInfoFacade()
				.getIndexList(new SormasToSormasShareInfoCriteria().sample(sample.toReference()), null, null)
				.stream()
				.map(s -> new SormasToSormasShareTree(s, Collections.emptyList()))
				.collect(Collectors.toList()),
			null,
			null);
	}

	public SormasToSormasListComponent(EventDto event, boolean shareEnabled) {

		sormasToSormasList = new SormasToSormasList(
			event.getSormasToSormasOriginInfo() == null,
			Captions.sormasToSormasEventNotShared,
			(i) -> ControllerProvider.getSormasToSormasController().syncEvent(event, i),
			(i) -> ControllerProvider.getSormasToSormasController().revokeShare(i, this::reloadList));

		initLayout(
			event.getSormasToSormasOriginInfo(),
			() -> FacadeProvider.getSormasToSormasEventFacade().getAllShares(event.getUuid()),
			shareEnabled ? e -> ControllerProvider.getSormasToSormasController().shareEventFromDetailsPage(event, this) : null,
			(e) -> ControllerProvider.getSormasToSormasController().returnEvent(event));
	}

	public SormasToSormasListComponent(EventParticipantDto eventParticipant, boolean shareEnabled) {
		sormasToSormasList = new SormasToSormasList(
			eventParticipant.getSormasToSormasOriginInfo() == null,
			Captions.sormasToSormasEventParticipantNotShared,
			null,
			null);

		initLayout(
			eventParticipant.getSormasToSormasOriginInfo(),
			() -> FacadeProvider.getSormasToSormasEventFacade().getAllShares(eventParticipant.getEvent().getUuid()),
			null,
			null);
	}

	private void initLayout(
		SormasToSormasOriginInfoDto originInfo,
		ShareDataLoader loadShares,
		Button.ClickListener shareButtonClickListener,
		Button.ClickListener returnButtonClickListener) {
		this.loadShares = loadShares;

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

		shareListLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedWith));
		shareListLabel.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.VSPACE_4);
		addComponent(shareListLabel);

		addComponent(sormasToSormasList);
		reloadList();

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
		List<SormasToSormasShareTree> shareInfos;
		try {
			shareInfos = loadShares.load();

			List<SormasToSormasShareInfoDto> shareInfoList = getShareInfoList(shareInfos);
			SormasToSormasShareInfoDto ownerShare = getOwnerShare(shareInfos);

			if (ownerShare != null) {
				VerticalLayout ownerShareLayout = buildOwnerShareLayout(ownerShare);
				ownerShareLayout.addStyleName(CssStyles.VSPACE_3);

				addComponent(ownerShareLayout, getComponentIndex(shareListLabel));

				shareInfoList = shareInfoList.stream().filter(s -> !s.getUuid().equals(ownerShare.getUuid())).collect(Collectors.toList());
			}

			sormasToSormasList.setData(shareInfoList);

		} catch (SormasToSormasException e) {
			sormasToSormasList.showPlaceholder("Failed to load shares");
		}

	}

	private List<SormasToSormasShareInfoDto> getShareInfoList(List<SormasToSormasShareTree> shareInfos) {
		return shareInfos.stream()
			.map((s -> Stream.concat(Stream.of(s.getShare()), getShareInfoList(s.getReShares()).stream())))
			.reduce(Stream.empty(), Stream::concat)
			.collect(Collectors.toList());
	}

	private SormasToSormasShareInfoDto getOwnerShare(List<SormasToSormasShareTree> shareInfos) {
		Optional<SormasToSormasShareTree> sharedWithOwnership = shareInfos.stream().filter(s -> s.getShare().isOwnershipHandedOver()).findFirst();
		if (sharedWithOwnership.isPresent()) {
			SormasToSormasShareTree ownerTree = sharedWithOwnership.get();
			SormasToSormasShareInfoDto ownerShare = getOwnerShare(ownerTree.getReShares());

			return ownerShare == null ? ownerTree.getShare() : ownerShare;
		}

		return null;
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

	private VerticalLayout buildOwnerShareLayout(SormasToSormasShareInfoDto shareInfo) {
		VerticalLayout infoLayout = new VerticalLayout();
		infoLayout.setWidth(100, Unit.PERCENTAGE);
		infoLayout.setMargin(false);
		infoLayout.setSpacing(false);
		infoLayout.addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		Label targetLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasOwnedBy) + " " + shareInfo.getTarget());
		targetLabel.addStyleName(CssStyles.LABEL_BOLD);
		infoLayout.addComponent(targetLabel);

		Label senderLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedBy) + ": " + shareInfo.getSender().getCaption());
		infoLayout.addComponent(senderLabel);

		Label shareDateLabel =
			new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedDate) + ": " + DateFormatHelper.formatDate(shareInfo.getCreationDate()));
		infoLayout.addComponent(shareDateLabel);

		if (!DataHelper.isNullOrEmpty(shareInfo.getComment())) {
			infoLayout.addComponent(new Label(shareInfo.getComment()));
		}

		return infoLayout;
	}

	private interface ShareDataLoader {

		List<SormasToSormasShareTree> load() throws SormasToSormasException;

	}
	private static class SormasToSormasList extends PaginationList<SormasToSormasShareInfoDto> {

		private static final long serialVersionUID = -4659924105492791566L;
		private String defaultPlaceHolderText;
		private final Label placeholderLabel;
		private final Consumer<SormasToSormasShareInfoDto> syncListener;

		private final Consumer<SormasToSormasShareInfoDto> revokeListener;

		public SormasToSormasList(
			boolean showPlaceholder,
			String placeholderCaptionTag,
			Consumer<SormasToSormasShareInfoDto> syncListener,
			Consumer<SormasToSormasShareInfoDto> revokeListener) {
			super(5);

			this.defaultPlaceHolderText = placeholderCaptionTag != null ? I18nProperties.getCaption(placeholderCaptionTag) : null;
			this.placeholderLabel = new Label(defaultPlaceHolderText);
			this.placeholderLabel.setVisible(showPlaceholder);
			this.syncListener = syncListener;
			this.revokeListener = revokeListener;
		}

		@Override
		public void reload() {
		}

		public void setData(List<SormasToSormasShareInfoDto> data) {
			setEntries(data);
			if (data.isEmpty()) {
				showPlaceholder(null);
			} else {
				listLayout.removeComponent(placeholderLabel);
				showPage(1);
			}
		}

		public void showPlaceholder(String placeholderText) {
			setEntries(Collections.emptyList());
			updatePaginationLayout();

			placeholderLabel.setValue(placeholderText == null ? defaultPlaceHolderText : placeholderText);
			listLayout.addComponent(placeholderLabel);
		}

		@Override
		protected void drawDisplayedEntries() {
			List<SormasToSormasShareInfoDto> displayedEntries = getDisplayedEntries();

			for (int i = 0; i < displayedEntries.size(); i++) {
				SormasToSormasShareInfoDto shareInfo = displayedEntries.get(i);
				SormasToSormasShareListEntry listEntry = new SormasToSormasShareListEntry(shareInfo, syncListener, revokeListener::accept);
				if (i == 0) {
					listEntry.addStyleName(CssStyles.SORMAS_LIST_ENTRY_NO_BORDER);
				}
				listLayout.addComponent(listEntry);
			}
		}

	}
	private static class SormasToSormasShareListEntry extends HorizontalLayout {

		private static final long serialVersionUID = 7462585357530141263L;

		public SormasToSormasShareListEntry(
			SormasToSormasShareInfoDto shareInfo,
			Consumer<SormasToSormasShareInfoDto> syncListener,
			Consumer<SormasToSormasShareInfoDto> revokeListener) {
			setMargin(false);
			setSpacing(true);
			setWidth(100, Unit.PERCENTAGE);
			addStyleName(CssStyles.SORMAS_LIST_ENTRY);

			VerticalLayout infoLayout = new VerticalLayout();
			infoLayout.setWidth(100, Unit.PERCENTAGE);
			infoLayout.setMargin(false);
			infoLayout.setSpacing(false);

			Label targetLabel = new Label(shareInfo.getTarget().getCaption());
			targetLabel.addStyleName(CssStyles.LABEL_BOLD);
			infoLayout.addComponent(targetLabel);

			Label senderLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedBy) + ": " + shareInfo.getSender().getCaption());
			infoLayout.addComponent(senderLabel);

			if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_ACCEPT_REJECT)) {
				Label statusLabel =
					new Label(I18nProperties.getCaption(Captions.SormasToSormasShareRequest_status) + ": " + shareInfo.getRequestStatus());
				infoLayout.addComponent(statusLabel);
			}

			Label shareDateLabel = new Label(
				I18nProperties.getCaption(Captions.sormasToSormasSharedDate) + ": " + DateFormatHelper.formatDate(shareInfo.getCreationDate()));
			infoLayout.addComponent(shareDateLabel);

			if (!DataHelper.isNullOrEmpty(shareInfo.getComment())) {
				infoLayout.addComponent(new Label(shareInfo.getComment()));
			}

			addComponent(infoLayout);
			setExpandRatio(infoLayout, 1);

			if (syncListener != null && shareInfo.getRequestStatus() == ShareRequestStatus.ACCEPTED && !shareInfo.isOwnershipHandedOver()) {
				addComponent(ButtonHelper.createIconButton(Captions.sormasToSormasSync, VaadinIcons.REFRESH, (e) -> {
					syncListener.accept(shareInfo);
				}));
			}

			if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_ACCEPT_REJECT)
				&& revokeListener != null
				&& shareInfo.getRequestStatus() == ShareRequestStatus.PENDING
				&& !shareInfo.isOwnershipHandedOver()) {
				addComponent(ButtonHelper.createIconButton(Captions.sormasToSormasRevokeShare, VaadinIcons.TRASH, (e) -> {
					revokeListener.accept(shareInfo);
				}));
			}

		}

	}
}
