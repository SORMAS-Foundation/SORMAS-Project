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

package de.symeda.sormas.ui;

import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * A stateless EJB service that provides functionality for updating and retrieving
 * user language preferences.
 * 
 * <p>
 * This service updates the user language in session, internationalization properties,
 * and facade layers.
 * </p>
 * 
 */
@Stateless
public class UserLanguageUpdater {

    @Inject
    private UserSession userSession;

    /**
     * Updates the user's language preference.
     * This method synchronizes the language setting in the user session, internationalization
     * properties, and the I18n facade.
     * 
     * <p>
     * This is the recommended way to change a user's language preference as it
     * ensures all components are properly updated with the new language setting.
     * </p>
     * 
     * @param language
     *            the new Language to set for the current user
     */
    public void updateUserLanguage(Language language) {
        userSession.setLanguage(language);
        I18nProperties.setUserLanguage(language);
        FacadeProvider.getI18nFacade().setUserLanguage(language);
    }

    /**
     * Retrieves the current user's language preference from the user session.
     * 
     * @return the current Language setting for the user, or null if no language has been set
     */
    public Language getUserLanguage() {
        return userSession.getLanguage();
    }

}
