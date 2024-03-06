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
package de.symeda.sormas.ui.document;

import static java.util.Objects.nonNull;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.importer.DocumentMultiFileUpload;
import de.symeda.sormas.ui.importer.DocumentUploadFinishedHandler;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class DocumentListComponent extends SideComponent {

	private final DocumentList documentList;
	private PopupButton mainButton;

	public DocumentListComponent(
		DocumentRelatedEntityType relatedEntityType,
		ReferenceDto entityRef,
		UserRight editRight,
		boolean pseudonymized,
		boolean isEditAllowed,
		boolean isDeleteAllowed) {
		super(I18nProperties.getString(Strings.entityDocuments));

		documentList = new DocumentList(relatedEntityType, entityRef, editRight, pseudonymized, isEditAllowed, isDeleteAllowed);
		addComponent(documentList);
		documentList.reload();

		if (UiUtil.permitted(isEditAllowed, editRight, UserRight.DOCUMENT_UPLOAD)) {
			Button uploadButton = buildUploadButton(relatedEntityType, entityRef);
			addCreateButton(uploadButton);
		}
	}

	private Button buildUploadButton(DocumentRelatedEntityType relatedEntityType, ReferenceDto entityRef) {
		VerticalLayout uploadLayout = new VerticalLayout();
		uploadLayout.setSpacing(true);
		uploadLayout.setMargin(true);
		uploadLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);

		mainButton =
			ButtonHelper.createIconPopupButton(Captions.documentUploadDocument, VaadinIcons.PLUS_CIRCLE, uploadLayout, ValoTheme.BUTTON_PRIMARY);

		boolean multipleUpload = UiUtil.enabled(FeatureType.DOCUMENTS_MULTI_UPLOAD);

		UploadStateWindow uploadStateWindow = new UploadStateWindow();
		MultiFileUpload multiFileUpload = new DocumentMultiFileUpload(() -> {
			mainButton.setButtonClickTogglesPopupVisibility(false);
			mainButton.setClosePopupOnOutsideClick(false);
		}, new DocumentUploadFinishedHandler(relatedEntityType, entityRef.getUuid(), documentList::reload), uploadStateWindow, multipleUpload);
		multiFileUpload
			.setUploadButtonCaptions(I18nProperties.getCaption(Captions.importImportData), I18nProperties.getCaption(Captions.importImportData));
		multiFileUpload.setAllUploadFinishedHandler(() -> {
			mainButton.setButtonClickTogglesPopupVisibility(true);
			mainButton.setClosePopupOnOutsideClick(true);
			mainButton.setPopupVisible(false);
		});

		uploadLayout.addComponentsAndExpand(multiFileUpload);

		return mainButton;
	}

	public void reload() {
		if (nonNull(documentList)) {
			documentList.reload();
		}
	}

	public PopupButton getMainButton() {
		return mainButton;
	}
}
