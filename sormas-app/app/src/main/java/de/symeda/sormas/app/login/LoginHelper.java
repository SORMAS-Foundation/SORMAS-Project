package de.symeda.sormas.app.login;

import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Created by Orson on 19/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class LoginHelper {

    public static void processLogout() {
        ConfigProvider.clearUsernameAndPassword();
        ConfigProvider.clearPin();
        ConfigProvider.setAccessGranted(false);
    }
}
