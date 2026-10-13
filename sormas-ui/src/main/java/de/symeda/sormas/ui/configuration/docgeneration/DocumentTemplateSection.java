/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.docgeneration;

import static de.symeda.sormas.ui.utils.CssStyles.H3;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateCriteria;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.importer.DocumentTemplateReceiver;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocumentTemplateSection extends VerticalLayout {

	private static final long serialVersionUID = 379271838736314055L;

	private DocumentTemplateReceiver documentTemplateReceiver;

	public DocumentTemplateSection(
		DocumentTemplateCriteria documentTemplateCriteria,
		boolean hasDisease,
		DocumentTemplateReceiver documentTemplateReceiver) {
		this.documentTemplateReceiver = documentTemplateReceiver;

		HorizontalLayout sectionHeader = new HorizontalLayout();

		DocumentTemplatesGrid documentTemplatesGrid = new DocumentTemplatesGrid(documentTemplateCriteria, hasDisease);
		documentTemplatesGrid.setWidth(700, Unit.PIXELS);

		Label quarantineTemplatesLabel = new Label(documentTemplateCriteria.getDocumentWorkflow().toString());
		quarantineTemplatesLabel.addStyleName(H3);
		Button uploadButton = buildUploadButton(documentTemplateCriteria.getDocumentWorkflow(), documentTemplatesGrid, hasDisease);
		sectionHeader.addComponents(quarantineTemplatesLabel, uploadButton);
		sectionHeader.setComponentAlignment(uploadButton, Alignment.MIDDLE_RIGHT);
		sectionHeader.setWidth(700, Unit.PIXELS);

		addComponent(sectionHeader);
		addComponent(documentTemplatesGrid);
		setExpandRatio(documentTemplatesGrid, 1F);
	}

	private Button buildUploadButton(DocumentWorkflow documentWorkflow, DocumentTemplatesGrid documentTemplatesGrid, boolean hasDisease) {
		return ButtonHelper.createIconButton(I18nProperties.getCaption(Captions.DocumentTemplate_uploadTemplate), VaadinIcons.UPLOAD, e -> {
			Window window = VaadinUiUtil.showPopupWindow(new DocumentTemplateUploadLayout(documentWorkflow, hasDisease, documentTemplateReceiver));
			window.setCaption(String.format(I18nProperties.getCaption(Captions.DocumentTemplate_uploadWorkflowTemplate), documentWorkflow));
			window.addCloseListener(c -> documentTemplatesGrid.reload());
		}, ValoTheme.BUTTON_PRIMARY);
	}
}
