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

import java.io.Serializable;
import java.util.Optional;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;

/**
 * A CDI session-scoped bean that manages user-specific session data for the SORMAS UI.
 * This class maintains user preferences and settings that persist throughout the user's
 * session, particularly language preferences.
 * 
 */
@Named("userSession")
@SessionScoped
public class UserSession implements Serializable {

    private Language language;

    /**
     * Initializes the user's preferred language by retrieving it from the current user's profile.
     * If a language is found, it sets the language for both the I18nProperties and the I18nFacade.
     * 
     * <p>
     * This method is typically called during user login or session initialization
     * to configure the UI language based on the user's preferences.
     * </p>
     * 
     * @return the initialized Language, or null if no language preference is set for the current user
     */
    public Language initUserLanguage() {
        language = Optional.of(FacadeProvider.getUserFacade()).map(UserFacade::getCurrentUser).map(UserDto::getLanguage).orElse(null);
        if (language != null) {
            I18nProperties.setUserLanguage(language);
            FacadeProvider.getI18nFacade().setUserLanguage(language);
        }
        return language;
    }

    /**
     * Returns the current language setting for this user session.
     * 
     * @return the current Language, or null if no language has been set
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the language for this user session.
     * 
     * <p>
     * Note: This method only updates the session variable. To apply the language
     * change to the internationalization system, use {@link #initUserLanguage()} or
     * manually update I18nProperties and I18nFacade.
     * </p>
     * 
     * @param language
     *            the Language to set for this session
     */
    public void setLanguage(Language language) {
        this.language = language;
    }
}
