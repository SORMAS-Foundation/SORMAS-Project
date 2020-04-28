package de.symeda.sormas.backend.contact;

import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseEditAuthorization;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;


@Stateless
@LocalBean
public class ContactEditAuthorization {
	
	private UserService userService;
	private CaseService caseService;
	private CaseEditAuthorization caseEditAuthorization;
	
    public boolean isContactEditAllowed(Contact contact){

    	User user = userService.getCurrentUser();
        Set<UserRole> userRoles = user.getUserRoles();
    	
        Case caseofContact = caseService.getByUuid(contact.getUuid()); 
        		
        if (user.getUuid().equals(contact.getReportingUser().getUuid())){
            return true;
        }

        if (caseEditAuthorization.caseEditAllowedCheck(caseofContact.getUuid())){
            return true;
        }

        if ((!userRoles.stream().filter(UserRole::isSupervisor).collect(Collectors.toList()).isEmpty())) {
            Boolean contactRegion = checkMatchRegionForUserAndContact(contact, user);
            if (contactRegion != null) return contactRegion;
        }

        if ((!userRoles.stream().filter(UserRole::isOfficer).collect(Collectors.toList()).isEmpty())) {
            District contactDistrict = contact.getDistrict();
            if (contactDistrict != null) {
                return  contactDistrict.equals(user.getDistrict());
            }
        }

        if ((!userRoles.stream().filter(UserRole::isPortHealthUser).collect(Collectors.toList()).isEmpty())) {
            Boolean contactRegion = checkMatchRegionForUserAndContact(contact, user);
            if (contactRegion != null) return contactRegion;
        }

        if ((!userRoles.stream().filter(UserRole::isNational).collect(Collectors.toList()).isEmpty())) {
            return true;
        }

        return false;
    }

    private static Boolean checkMatchRegionForUserAndContact(Contact contact, User user) {
        Region contactRegion = contact.getRegion();
        if (contactRegion != null) {
            return contactRegion.equals(user.getRegion());
        }

        final District contactDistrict = contact.getDistrict();
        if (contactDistrict !=null && contactDistrict.getRegion()!=null){
            return contactDistrict.getRegion().equals(user.getRegion());
        }
        return null;
    }
}
