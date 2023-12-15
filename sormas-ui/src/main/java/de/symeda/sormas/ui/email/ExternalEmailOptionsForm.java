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

package de.symeda.sormas.ui.email;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.document.DocumentReferenceDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.components.MultiSelect;
import de.symeda.sormas.ui.utils.components.MultilineLabel;

public class ExternalEmailOptionsForm extends AbstractEditForm<ExternalEmailOptionsDto> {

    private static final String ATTACHMENT_NOT_AVAILABLE_INFO_LOC = "attachmentNotAvailableInfoLoc";
    private static final String HTML_LAYOUT = fluidRowLocs(ExternalEmailOptionsDto.TEMPLATE_NAME)
            + fluidRowLocs(ExternalEmailOptionsDto.RECIPIENT_EMAIL)
            + fluidRowLocs(ExternalEmailOptionsDto.ATTACHED_DOCUMENTS)
            + fluidRowLocs(ATTACHMENT_NOT_AVAILABLE_INFO_LOC);

	private final DocumentWorkflow documentWorkflow;
    private final DocumentRelatedEntityType documentRelatedEntityType;
	private final PersonDto person;
    private final boolean isAttachmentAvailable;
    private MultiSelect<DocumentReferenceDto> attachedDocumentsField;

    protected ExternalEmailOptionsForm(
            DocumentWorkflow documentWorkflow,
            DocumentRelatedEntityType documentRelatedEntityType,
            PersonDto person,
            boolean isAttachmentAvailable) {
		super(ExternalEmailOptionsDto.class, ExternalEmailOptionsDto.I18N_PREFIX, false);
		this.documentWorkflow = documentWorkflow;
        this.documentRelatedEntityType = documentRelatedEntityType;
		this.person = person;
        this.isAttachmentAvailable = isAttachmentAvailable;

		addFields();
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		ComboBox templateCombo = addField(ExternalEmailOptionsDto.TEMPLATE_NAME, ComboBox.class);
		templateCombo.setRequired(true);
		List<String> templateNames = FacadeProvider.getExternalEmailFacade().getTemplateNames(documentWorkflow);
		FieldHelper.updateItems(templateCombo, templateNames);

		ComboBox recipientEmailCombo = addField(ExternalEmailOptionsDto.RECIPIENT_EMAIL, ComboBox.class);
		recipientEmailCombo.setRequired(true);
		List<String> recipientEmails = person.getAllEmailAddresses();
		FieldHelper.updateItems(recipientEmailCombo, recipientEmails);
		String primaryEmailAddress = person.getEmailAddress(true);
		if (StringUtils.isNotBlank(primaryEmailAddress)) {
			recipientEmailCombo
                    .setItemCaption(primaryEmailAddress, primaryEmailAddress + " (" + I18nProperties.getCaption(Captions.primarySuffix) + ")");
		}

        if (documentRelatedEntityType != null) {
            attachedDocumentsField = addField(ExternalEmailOptionsDto.ATTACHED_DOCUMENTS, MultiSelect.class);
            if (!isAttachmentAvailable) {
                attachedDocumentsField.setEnabled(false);

                MultilineLabel attachmentUnavailableInfo = new MultilineLabel(
                        VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.messageExternalEmailAttachmentNotAvailableInfo),
                        ContentMode.HTML);
                attachmentUnavailableInfo.addStyleNames(CssStyles.VSPACE_2, CssStyles.VSPACE_TOP_4);
                getContent().addComponent(attachmentUnavailableInfo, ATTACHMENT_NOT_AVAILABLE_INFO_LOC);
            }
        }
    }

    @Override
    public void setValue(ExternalEmailOptionsDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
        super.setValue(newFieldValue);

        if (attachedDocumentsField != null) {
            attachedDocumentsField.setItems(
                    FacadeProvider.getExternalEmailFacade().getAttachableDocuments(documentWorkflow, newFieldValue.getRootEntityReference().getUuid()));
        }
	}
}
