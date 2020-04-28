package de.symeda.sormas.app.backend.caze;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;

public class CaseEditAuthorization {

    public static Boolean isCaseEditAllowed(Case caze) {

        User user = ConfigProvider.getUser();
//        Set<UserRole> userRoles = user.getUserRoles();


        if (user.getUuid().equals(caze.getReportingUser().getUuid())) {
            return true;
        }

        if (ConfigProvider.hasRole(UserRole.getSupervisorRoles())){
            return caze.getRegion().equals(user.getRegion());
        }

        if (ConfigProvider.hasRole(UserRole.getOfficerRoles())) {
                return caze.getDistrict().equals(user.getDistrict());
        }

        if ((ConfigProvider.hasRole(UserRole.HOSPITAL_INFORMANT))) {
                return caze.getHealthFacility().equals(user.getHealthFacility());
        }

        if ((ConfigProvider.hasRole(UserRole.COMMUNITY_INFORMANT))) {
            return caze.getCommunity().equals(user.getCommunity());
        }

        if ((ConfigProvider.hasRole(UserRole.POE_INFORMANT))) {
            return caze.getPointOfEntry().equals(user.getPointOfEntry());
        }

        if (ConfigProvider.hasRole(UserRole.NATIONAL_USER)) {
            return true;
        }

        return false;
    }
}
