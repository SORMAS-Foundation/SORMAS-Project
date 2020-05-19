package de.symeda.sormas.backend.contact;

import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseEditAuthorization;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;


@Stateless (name = "ContactEditAuthorization")
@LocalBean
public class ContactEditAuthorization {
	
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private CaseEditAuthorization caseEditAuthorization;
	@EJB
	private ContactService contactService;
	
    public boolean isContactEditAllowed(Contact contact){

    	User user = userService.getCurrentUser();

        if (contact.getReportingUser()!=null && DataHelper.equal(user.getUuid(), (contact.getReportingUser().getUuid()))){
            return true;
        }

        if (contact.getUuid() != null && contact.getCaze()!= null && contact.getCaze().getUuid() != null) {
            Case caseofContact = caseService.getByUuid(contact.getCaze().getUuid());
            if (caseofContact != null && caseEditAuthorization.caseEditAllowedCheck(caseofContact)) {
                return true;
            }
        }

        if (userService.hasAnyRole(UserRole.getSupervisorRoles())) {
            return  DataHelper.equal(contact.getRegion(), (user.getRegion()));
        }

        if (userService.hasAnyRole(UserRole.getOfficerRoles())) {
            return DataHelper.equal(contact.getDistrict(), (user.getDistrict()));
        }

        if (userService.hasRole(UserRole.NATIONAL_USER)) {
            return true;
        }

        return false;
    }
}
