package de.symeda.sormas.api.utils.jurisdiction;

import java.util.Collections;

import de.symeda.sormas.api.task.TaskJurisdictionDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;

public class TaskJurisdictionHelper {

    public static boolean isInJurisdiction(RoleCheck roleCheck, UserJurisdiction userJurisdiction, TaskJurisdictionDto taskJurisdiction) {

        if (taskJurisdiction.getCreatorUserUuid() != null
                && DataHelper.equal(userJurisdiction.getUuid(), taskJurisdiction.getCreatorUserUuid())) {
            return true;
        }

        if (taskJurisdiction.getAssigneeUserUuid() != null
                && DataHelper.equal(userJurisdiction.getUuid(), taskJurisdiction.getAssigneeUserUuid())) {
            return true;
        }

        if (taskJurisdiction.getCaseJurisdiction() != null) {
            return CaseJurisdictionHelper.isInJurisdiction(roleCheck, userJurisdiction, taskJurisdiction.getCaseJurisdiction());
        }

        if (taskJurisdiction.getContactJurisdiction() != null) {
            return ContactJurisdictionHelper.isInJurisdiction(roleCheck, userJurisdiction, taskJurisdiction.getContactJurisdiction());
        }

        if (taskJurisdiction.getEventJurisdiction() != null) {
            return EventJurisdictionHelper.isInJurisdiction(roleCheck, userJurisdiction, taskJurisdiction.getEventJurisdiction());
        }

        return roleCheck.hasAnyRole(Collections.singleton(UserRole.NATIONAL_USER));
    }
}
