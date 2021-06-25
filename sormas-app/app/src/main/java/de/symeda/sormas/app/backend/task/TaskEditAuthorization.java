package de.symeda.sormas.app.backend.task;

import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class TaskEditAuthorization {

    public static boolean isTaskEditAllowed(Task task) {
        final User user = ConfigProvider.getUser();
        final TaskJurisdictionBooleanValidator validator =
                TaskJurisdictionBooleanValidator.of(JurisdictionHelper.createTaskJurisdictionDto(task), JurisdictionHelper.createUserJurisdiction(user));
        return validator.inJurisdictionOrOwned();
    }
}
