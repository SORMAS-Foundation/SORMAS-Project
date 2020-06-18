package de.symeda.sormas.api.utils.jurisdiction;

import de.symeda.sormas.api.event.EventJurisdictionDto;
import de.symeda.sormas.api.sample.SampleJurisdictionDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SampleJurisdictionHelper {

    public static boolean isInJurisdiction (RoleCheck roleCheck, UserJurisdiction userJurisdiction, SampleJurisdictionDto sampleJurisdiction){

        if (sampleJurisdiction.getReportingUserUuid() != null
                && DataHelper.equal(userJurisdiction.getUuid(), sampleJurisdiction.getReportingUserUuid())) {
        return true;
    }

        if (sampleJurisdiction.getCaseJurisdiction() != null) {
        return CaseJurisdictionHelper.isInJurisdiction(roleCheck, userJurisdiction, sampleJurisdiction.getCaseJurisdiction());
    }

        if (sampleJurisdiction.getContactJurisdiction() != null) {
        return ContactJurisdictionHelper.isInJurisdiction(roleCheck, userJurisdiction, sampleJurisdiction.getContactJurisdiction());
    }

        Set<UserRole> labRoles = new HashSet<>();
        labRoles.add(UserRole.LAB_USER);
        labRoles.add(UserRole.EXTERNAL_LAB_USER);

        if (sampleJurisdiction.getLabUuid() != null && roleCheck.hasAnyRole(labRoles)) {
            return DataHelper.equal(sampleJurisdiction.getLabUuid(), userJurisdiction.getHealthFacilityUuid());
    }

        if (sampleJurisdiction.getOtherLabUuid() != null && roleCheck.hasAnyRole(labRoles)) {
            return DataHelper.equal(sampleJurisdiction.getOtherLabUuid(), userJurisdiction.getHealthFacilityUuid());
        }

        return roleCheck.hasAnyRole(Collections.singleton(UserRole.NATIONAL_USER));
}


}
