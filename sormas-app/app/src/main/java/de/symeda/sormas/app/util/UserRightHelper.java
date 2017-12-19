package de.symeda.sormas.app.util;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Created by Mate Strysewske on 18.12.2017.
 */

public class UserRightHelper {

    public static boolean hasUserRight(UserRight userRight) {
        for (UserRole userRole : userRight.getUserRoles()) {
            if (ConfigProvider.getUser().getUserRoles().contains(userRole)) {
                return true;
            }
        }

        return false;
    }

}
