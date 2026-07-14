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

package de.symeda.sormas.ui.configuration.customizablefield;

import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DeletableUtils;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Controller for customizable field metadata CRUD operations.
 */
public class CustomizableFieldsController {

    /**
     * Open the create dialog for a new customizable field.
     */
    public void createField() {

        CustomizableFieldMetadataDto dto = new CustomizableFieldMetadataDto();
        dto.setActive(true);

        CustomizableFieldEditForm form = new CustomizableFieldEditForm(false);
        form.setValue(dto);

        final CommitDiscardWrapperComponent<CustomizableFieldEditForm> cdw = new CommitDiscardWrapperComponent<>(form);

        // Validate via Vaadin 8 Binder before allowing commit to proceed
        cdw.setPreCommitListener(successCallback -> {
            if (form.validate().isOk()) {
                successCallback.run();
            }
        });

        cdw.addCommitListener(() -> {
            FacadeProvider.getCustomizableFieldMetadataFacade().save(form.getValue());
            Notification.show(I18nProperties.getString(Strings.messageCustomizableFieldCreated), Notification.Type.ASSISTIVE_NOTIFICATION);
            SormasUI.get().getNavigator().navigateTo(CustomizableFieldsView.VIEW_NAME);
        });

        VaadinUiUtil.showModalPopupWindow(cdw, I18nProperties.getString(Strings.headingCreateCustomizableField));
    }

    /**
     * Open the edit dialog for an existing customizable field.
     */
    public void editField(String uuid) {

        CustomizableFieldMetadataDto dto = FacadeProvider.getCustomizableFieldMetadataFacade().getByUuid(uuid);

        CustomizableFieldEditForm form = new CustomizableFieldEditForm(true);
        form.setValue(dto);

        final CommitDiscardWrapperComponent<CustomizableFieldEditForm> cdw = new CommitDiscardWrapperComponent<>(form);

        // Validate via Vaadin 8 Binder before allowing commit to proceed
        cdw.setPreCommitListener(successCallback -> {
            if (form.validate().isOk()) {
                successCallback.run();
            }
        });

        cdw.addCommitListener(() -> {
            FacadeProvider.getCustomizableFieldMetadataFacade().save(form.getValue());
            Notification.show(I18nProperties.getString(Strings.messageCustomizableFieldSaved), Notification.Type.ASSISTIVE_NOTIFICATION);
            SormasUI.get().getNavigator().navigateTo(CustomizableFieldsView.VIEW_NAME);
        });

        VaadinUiUtil.showModalPopupWindow(cdw, I18nProperties.getString(Strings.headingEditCustomizableField) + " - " + dto.getName());
    }

    /**
     * Show a dialog to clone the given field with a new name.
     */
    public void cloneField(CustomizableFieldMetadataDto sourceDto) {

        TextField nameField =
            new TextField(I18nProperties.getPrefixCaption(CustomizableFieldMetadataDto.I18N_PREFIX, CustomizableFieldMetadataDto.NAME));
        nameField.setWidth(100, com.vaadin.server.Sizeable.Unit.PERCENTAGE);
        nameField.setRequiredIndicatorVisible(true);

        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);
        content.addComponent(nameField);

        VaadinUiUtil.showConfirmationPopup(
            I18nProperties.getString(Strings.headingCloneCustomizableField),
            content,
            I18nProperties.getCaption(Captions.actionClone),
            I18nProperties.getCaption(Captions.actionCancel),
            480,
            result -> {
                if (Boolean.TRUE.equals(result)) {
                    String newName = nameField.getValue();
                    if (newName == null || newName.trim().isEmpty()) {
                        Notification.show(I18nProperties.getCaption(Captions.actionClone) + " - name required", Notification.Type.WARNING_MESSAGE);
                        return;
                    }
                    FacadeProvider.getCustomizableFieldMetadataFacade().cloneField(sourceDto.getUuid(), newName.trim());
                    Notification.show(I18nProperties.getString(Strings.messageCustomizableFieldCloned), Notification.Type.ASSISTIVE_NOTIFICATION);
                    SormasUI.get().getNavigator().navigateTo(CustomizableFieldsView.VIEW_NAME);
                }
            });
    }

    /**
     * Toggle the active state of a customizable field.
     */
    public void toggleActive(CustomizableFieldMetadataDto dto) {

        FacadeProvider.getCustomizableFieldMetadataFacade().setFieldActive(dto.getUuid(), !dto.isActive());
        SormasUI.get().getNavigator().navigateTo(CustomizableFieldsView.VIEW_NAME);
    }

    /**
     * Show a delete confirmation and delete if confirmed.
     */
    public void deleteField(String uuid, CustomizableFieldsGrid grid) {

        DeletableUtils.showDeleteWithReasonPopup(I18nProperties.getString(Strings.promptConfirmDeleteCustomizableField), deletionDetails -> {
            FacadeProvider.getCustomizableFieldMetadataFacade().delete(uuid, deletionDetails);
            Notification.show(I18nProperties.getString(Strings.messageCustomizableFieldDeleted), Notification.Type.ASSISTIVE_NOTIFICATION);
            grid.reload();
        });
    }
}
