package de.symeda.sormas.backend.caze;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


import de.symeda.sormas.api.user.UserRole;
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

	public Boolean caseEditAllowedCheck(String caseUuid) {

		Case caze = caseService.getByUuid(caseUuid);	
		User user = userService.getCurrentUser();
       
        if (user.getUuid().equals(caseUuid)) {
            return true;
        }

        if (hasRole(UserRole.getSupervisorRoles())) {
            return caze.getRegion().equals(user.getRegion());
        }

        if (hasRole(UserRole.getOfficerRoles())) {
            return caze.getDistrict().equals(user.getDistrict());
        }

        if ((hasRole(UserRole.HOSPITAL_INFORMANT))) {
            return caze.getHealthFacility().equals(user.getHealthFacility());
        }

        if ((hasRole(UserRole.COMMUNITY_INFORMANT))) {
            return caze.getCommunity().equals(user.getCommunity());
        }

        if ((hasRole(UserRole.POE_INFORMANT))) {
            return caze.getPointOfEntry().equals(user.getPointOfEntry());
        }

        if (hasRole(UserRole.NATIONAL_USER)) {
            return true;
        }
		
        return false;
    }

	public boolean hasRole (UserRole userRoleName){
		User user = userService.getCurrentUser();
        Set<UserRole> userRoles = user.getUserRoles();
        return !userRoles.stream().filter(userRole -> userRole.name().equals(userRoleName.name())).collect(Collectors.toList()).isEmpty();
    }

    public boolean hasRole(Set<UserRole> typeRoles) {
    	User user = userService.getCurrentUser();
        Set<UserRole> userRoles = user.getUserRoles();
        return !userRoles.stream().filter(userRole -> typeRoles.contains(userRole)).collect(Collectors.toList()).isEmpty();
    }
}
