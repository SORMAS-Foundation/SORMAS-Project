/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.system;

import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling system configuration value operations.
 */
public class SystemConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigurationController.class);

    private static final String MESSAGE_SYSTEM_CONFIGURATION_VALUE_SAVED = I18nProperties.getString(Strings.messageSystemConfigurationValueSaved);
    private static final String ERROR_SAVING_SYSTEM_CONFIGURATION_VALUE = I18nProperties.getString(Strings.errorProblemOccurred);
    private static final String ERROR_EDITING_SYSTEM_CONFIGURATION_VALUE = I18nProperties.getString(Strings.errorProblemOccurred);
    private static final String EDIT = I18nProperties.getString(Strings.edit);

    /**
     * Opens a modal window to edit the system configuration value identified by the given UUID.
     *
     * @param uuid
     *            the UUID of the system configuration value to be edited
     */
    public void editSystemConfigurationValue(String uuid) {
        try {
            SystemConfigurationValueDto systemConfigurationValue = FacadeProvider.getSystemConfigurationValueFacade().getByUuid(uuid);
            SystemConfigurationValueEditForm editForm = new SystemConfigurationValueEditForm(systemConfigurationValue);

            final CommitDiscardWrapperComponent<SystemConfigurationValueEditForm> cdw =
                new CommitDiscardWrapperComponent<>(editForm, editForm.getFieldGroup());
            cdw.addCommitListener(() -> {
                try {
                    FacadeProvider.getSystemConfigurationValueFacade().save(editForm.getValue());
                    Notification.show(MESSAGE_SYSTEM_CONFIGURATION_VALUE_SAVED, Notification.Type.ASSISTIVE_NOTIFICATION);
                    SormasUI.get().getNavigator().navigateTo(SystemConfigurationView.VIEW_NAME);
                } catch (Exception e) {
                    logger.error("Error saving system configuration value", e);
                    Notification.show(ERROR_SAVING_SYSTEM_CONFIGURATION_VALUE, Notification.Type.ERROR_MESSAGE);
                }
            });

            VaadinUiUtil.showModalPopupWindow(cdw, EDIT + " " + systemConfigurationValue.getKey());
        } catch (Exception e) {
            logger.error("Error editing system configuration value", e);
            Notification.show(ERROR_EDITING_SYSTEM_CONFIGURATION_VALUE, Notification.Type.ERROR_MESSAGE);
        }
    }
}
