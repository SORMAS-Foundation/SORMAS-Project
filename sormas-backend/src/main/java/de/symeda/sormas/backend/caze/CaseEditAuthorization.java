package de.symeda.sormas.backend.caze;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

@Stateless (name = "CaseEditAuthorization")
@LocalBean
public class CaseEditAuthorization {
	
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;

	public Boolean caseEditAllowedCheck(Case caze) {

		User user = userService.getCurrentUser();
       
        if (caze.getReportingUser()!=null && DataHelper.equal(user.getUuid(), (caze.getReportingUser().getUuid()))) {
            return true;
        }

        if (userService.hasAnyRole(UserRole.getSupervisorRoles())) {
            return DataHelper.equal(caze.getRegion(), (user.getRegion()));
        }

        if (userService.hasAnyRole(UserRole.getOfficerRoles())) {
            return DataHelper.equal(caze.getDistrict(), (user.getDistrict()));
        }

        if ((userService.hasRole(UserRole.HOSPITAL_INFORMANT))) {
            return DataHelper.equal(caze.getHealthFacility(), (user.getHealthFacility()));
        }

        if ((userService.hasRole(UserRole.COMMUNITY_INFORMANT))) {
            return DataHelper.equal(caze.getCommunity(), (user.getCommunity()));
        }

        if ((userService.hasRole(UserRole.POE_INFORMANT))) {
            return DataHelper.equal(caze.getPointOfEntry(), (user.getPointOfEntry()));
        }

        if (userService.hasRole(UserRole.NATIONAL_USER)) {
            return true;
        }
		
        return false;
    }
}
