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
        Set<UserRole> userRoles = user.getUserRoles();

        if (user.getUuid().equals(caze.getReportingUser().getUuid())){
            return true;
        }

        if (hasRole(userRoles.stream().filter(UserRole::isSupervisor))) {
            Region caseRegion = getCaseRegion(caze);
            if (caseRegion != null) {
                return caseRegion.equals(user.getRegion());
            }
        }

        if ((hasRole(userRoles.stream().filter(UserRole::isOfficer)))) {
            District caseDistrict = getCaseDistrict(caze);
            if (caseDistrict != null) {
                return caseDistrict.equals(user.getDistrict());
            }
        }

        if ((hasRole(userRoles.stream().filter(UserRole::isInformant )))) {
            Facility caseHealthFacility = caze.getHealthFacility();
            if (caseHealthFacility != null) {
                return caseHealthFacility.equals(user.getHealthFacility());
            }
        }

        if ((hasRole(userRoles.stream().filter(UserRole::isPortHealthUser)))) {
            Region caseRegion = getCaseRegion(caze);
            if (caseRegion != null) {
                return caseRegion.equals(user.getRegion());
            }
        }

        if ((hasRole(userRoles.stream().filter(UserRole::isNational)))) {
            return true;
        }

        return false;
    }

    private static boolean hasRole(Stream<UserRole> userRoleStream) {
        return !userRoleStream.collect(Collectors.toList()).isEmpty();
    }

    public static Region getCaseRegion(Case caze) {

        if (caze.getRegion() != null) {
            return caze.getRegion();
        }

        final District caseDistrict = caze.getDistrict();
        if (caseDistrict != null && caseDistrict.getRegion() != null) {
            return caseDistrict.getRegion();
        }

        final Community caseCommunity = caze.getCommunity();
        if (caseCommunity != null && caseCommunity.getDistrict() != null && caseCommunity.getDistrict().getRegion() != null) {
            return caseCommunity.getDistrict().getRegion();
        }

        final Facility caseHealthFacility = caze.getHealthFacility();
        if (caseHealthFacility != null) {
            if (caseHealthFacility.getRegion() != null) {
                return caseHealthFacility.getRegion();
            }

            final District district = caseHealthFacility.getDistrict();
            if (district != null && district.getRegion() != null) {
                return district.getRegion();
            }
            final Community community = caseHealthFacility.getCommunity();
            if (community != null && community.getDistrict() != null && community.getDistrict().getRegion() != null) {
                return community.getDistrict().getRegion();
            }
        }

        final PointOfEntry casePointOfEntry = caze.getPointOfEntry();
        if (casePointOfEntry != null) {
            if (casePointOfEntry.getRegion() != null) {
                return casePointOfEntry.getRegion();
            }

            if (casePointOfEntry.getDistrict() != null && casePointOfEntry.getDistrict().getRegion() != null) {
                return casePointOfEntry.getDistrict().getRegion();
            }
        }

        return null;
    }

    public static District getCaseDistrict(Case caze) {

        if (caze.getDistrict() != null) {
            return caze.getDistrict();
        }

        final Community caseCommunity = caze.getCommunity();
        if (caseCommunity != null && caseCommunity.getDistrict() != null) {
            return caseCommunity.getDistrict();
        }

        final Facility caseHealthFacility = caze.getHealthFacility();
        if (caseHealthFacility != null) {
            if (caseHealthFacility.getDistrict() != null) {
                return caseHealthFacility.getDistrict();
            }
            if (caseHealthFacility.getCommunity() != null && caseHealthFacility.getCommunity().getDistrict() != null) {
                return caseHealthFacility.getCommunity().getDistrict();
            }
        }

        if (caze.getPointOfEntry() != null) {
            return caze.getPointOfEntry().getDistrict();
        }
        return null;
    }
}
