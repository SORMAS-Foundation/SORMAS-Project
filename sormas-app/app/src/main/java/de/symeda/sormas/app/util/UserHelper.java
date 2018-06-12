package de.symeda.sormas.app.util;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Orson on 28/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class UserHelper {

    public static String getUserRole(User user) {
        if (user == null)
            return "";

        StringBuilder result = new StringBuilder();

        int index = 0;
        for (UserRole userRole : user.getUserRoles()) {
            result.append(userRole.toShortString() + ((index < (user.getUserRoles().size() - 1))? ", " : ""));

            index = index + 1;
        }

        return result.toString();
    }
}
