package de.symeda.sormas.app.backend.task;

import de.symeda.sormas.api.utils.jurisdiction.TaskJurisdictionHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class TaskEditAuthorization {

    public static boolean isEventEditAllowed(Task task) {
        User user = ConfigProvider.getUser();

        return TaskJurisdictionHelper
                .isInJurisdiction(ConfigProvider::hasRole, JurisdictionHelper.createUserJurisdiction(user), JurisdictionHelper.createTaskJurisdictionDto(task));
    }
}
