/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.survey;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentFacade;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.survey.SurveyTokenCriteria;
import de.symeda.sormas.api.survey.SurveyTokenIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.PaginationList;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

public class SurveyListComponentLayout extends SideComponentLayout {

	private static final long serialVersionUID = -4364573774979104517L;

	public SurveyListComponentLayout(
		CaseReferenceDto caseRef,
		Disease disease,
		boolean isEditAllowed,
		boolean isEmailAllowed,
		Consumer<Runnable> actionWrapper,
		Runnable createCallback) {
		super(
			new SurveyListComponent(
				I18nProperties.getString(Strings.headingSurveySideComponent),
				caseRef,
				disease,
				new SurveyTokenCriteria().caseAssignedTo(caseRef),
				actionWrapper,
				createCallback,
				isEditAllowed,
				isEmailAllowed));
	}

	private static class SurveyListComponent extends SideComponent {

		private static final long serialVersionUID = 4793190763340951494L;

		public SurveyListComponent(
			String heading,
			CaseReferenceDto caseRef,
			Disease disease,
			SurveyTokenCriteria surveyTokenCriteria,
			Consumer<Runnable> actionWrapper,
			Runnable createCallback,
			boolean isEditAllowed,
			boolean isEmailAllowed) {
			super(heading, actionWrapper);

			setWidth(100, Unit.PERCENTAGE);
			setMargin(false);
			setSpacing(false);

			VerticalLayout uploadLayout = new VerticalLayout();
			uploadLayout.setSpacing(true);
			uploadLayout.setMargin(true);
			uploadLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);

			if (isEmailAllowed) {
				Button sendSurveyButton = ButtonHelper.createButton(Captions.surveySend, I18nProperties.getCaption(Captions.surveySend), (e) -> {
				}, ValoTheme.BUTTON_PRIMARY);
				sendSurveyButton.setWidth(100, Unit.PERCENTAGE);
				uploadLayout.addComponent(sendSurveyButton);
			}

			Button generateSurveyDocButton =
				ButtonHelper.createButton(Captions.surveyGenerate, I18nProperties.getCaption(Captions.surveyGenerate), (e) -> {
					ControllerProvider.getSurveyDocumentController()
						.generateSurveyDocument(RootEntityType.ROOT_CASE, caseRef, disease, createCallback);
				});
			if (!isEmailAllowed) {
				generateSurveyDocButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			}

			generateSurveyDocButton.setWidth(100, Unit.PERCENTAGE);
			uploadLayout.addComponent(generateSurveyDocButton);

			addCreateButton(ButtonHelper.createIconPopupButton(Captions.surveyNew, VaadinIcons.PLUS_CIRCLE, uploadLayout, ValoTheme.BUTTON_PRIMARY));

			SurveyList selfReportList = new SurveyList(surveyTokenCriteria, isEditAllowed);
			addComponent(selfReportList);
			selfReportList.reload();
		}
	}

	private static class SurveyList extends PaginationList<SurveyTokenIndexDto> {

		private static final long serialVersionUID = 2918333514559847082L;

		private static final int MAX_DISPLAYED_ENTRIES = 5;

		private final SurveyTokenCriteria surveyTokenCriteria;
		private final boolean isEditAllowed;
		private final Label noSurveyLabel;

		public SurveyList(SurveyTokenCriteria surveyTokenCriteria, boolean isEditAllowed) {
			super(MAX_DISPLAYED_ENTRIES);
			this.surveyTokenCriteria = surveyTokenCriteria;
			this.isEditAllowed = isEditAllowed;
			this.noSurveyLabel = new Label(I18nProperties.getString(Strings.infoNoSurveys));
		}

		@Override
		public void reload() {
			List<SurveyTokenIndexDto> surveyTokens = FacadeProvider.getSurveyTokenFacade().getIndexList(surveyTokenCriteria, 0, null, null);

			setEntries(surveyTokens);
			if (!surveyTokens.isEmpty()) {
				showPage(1);
			} else {
				listLayout.removeAllComponents();
				updatePaginationLayout();
				listLayout.addComponent(noSurveyLabel);
			}
		}

		@Override
		protected void drawDisplayedEntries() {
			List<SurveyTokenIndexDto> displayedEntries = getDisplayedEntries();
			for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
				SurveyTokenIndexDto token = displayedEntries.get(i);
				SurveyListEntry listEntry = new SurveyListEntry(token);

				Button downloadButton = buildDownloadButton(token);
				listEntry.addComponent(downloadButton);
				listEntry.setComponentAlignment(downloadButton, Alignment.TOP_RIGHT);
				listEntry.setExpandRatio(downloadButton, 0);

				listEntry.addActionButton(String.valueOf(i), (Button.ClickListener) clickEvent -> {
					ControllerProvider.getSurveyTokenController().showCaseSurveyDetails(listEntry.getToken().toReference(), this::reload);
				}, UiUtil.permitted(isEditAllowed, UserRight.SURVEY_EDIT));
				listEntry.setEnabled(isEditAllowed);
				listLayout.addComponent(listEntry);
			}
		}

		private Button buildDownloadButton(SurveyTokenIndexDto token) {
			Button viewButton = ButtonHelper.createIconButton(null, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);

			StreamResource streamResource = new StreamResource((StreamResource.StreamSource) () -> {
				DocumentFacade documentFacade = FacadeProvider.getDocumentFacade();
				try {
					return new ByteArrayInputStream(documentFacade.getContent(token.getGeneratedDocumentUuid()));
				} catch (IOException | IllegalArgumentException e) {
					new Notification(
							String.format(I18nProperties.getString(Strings.errorReadingDocument), token.getGeneratedDocumentName()),
							e.getMessage(),
							Notification.Type.ERROR_MESSAGE,
							false).show(Page.getCurrent());
					return null;
				}
			}, token.getGeneratedDocumentName());
			streamResource.setMIMEType(token.getGeneratedDocumentMimeType());

			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(viewButton);
			fileDownloader.setFileDownloadResource(streamResource);

			return viewButton;
		}
	}

	private static class SurveyListEntry extends SideComponentField {

		private static final long serialVersionUID = 1876258478335703352L;

		private final SurveyTokenIndexDto token;

		public SurveyListEntry(SurveyTokenIndexDto token) {
			this.token = token;

			HorizontalLayout topLayout = new HorizontalLayout();
			topLayout.setWidth(100, Unit.PERCENTAGE);
			topLayout.setMargin(false);
			topLayout.setSpacing(false);
			addComponentToField(topLayout);

			VerticalLayout topLeftLayout = new VerticalLayout();
			{
				topLeftLayout.setMargin(false);
				topLeftLayout.setSpacing(false);

				Label surveyNameLabel = new Label(DataHelper.toStringNullable(token.getSurveyName()));
				CssStyles.style(surveyNameLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
				surveyNameLabel.setWidth(100, Unit.PERCENTAGE);
				topLeftLayout.addComponent(surveyNameLabel);

				Label assignmentDateLabel = new Label(I18nProperties.getCaption(Captions.creationDate));
				Label assignmentDate = new Label(DateFormatHelper.formatLocalDate(token.getAssignmentDate()));
				assignmentDate.setWidth(100, Unit.PERCENTAGE);
				topLeftLayout.addComponent(new HorizontalLayout(assignmentDateLabel, assignmentDate));

				if (StringUtils.isNotBlank(token.getRecipientEmail())) {
					Label sentToCaptionLabel = new Label(I18nProperties.getCaption(Captions.externalEmailSentTo));
					Label emailLabel = new Label(token.getRecipientEmail());
					if (token.isPseudonymized()) {
						emailLabel.addStyleName(CssStyles.INACCESSIBLE_LABEL);
					}
					topLeftLayout.addComponent(new HorizontalLayout(sentToCaptionLabel, emailLabel));
				}

				topLeftLayout.addComponent(
					new Label(
						token.getResponseReceived()
							? I18nProperties.getString(Strings.infoSurveyResponseReceived)
							: I18nProperties.getString(Strings.infoSurveyResponseNotReceived)));
			}
			topLayout.addComponent(topLeftLayout);
		}

		public void addDeleteButton(String id, Button.ClickListener actionClickListener) {
			Button actionButton = ButtonHelper.createIconButtonWithCaption(
				"delete" + id,
				null,
				VaadinIcons.TRASH,
				actionClickListener,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

		}

		public SurveyTokenIndexDto getToken() {
			return token;
		}
	}
}
