package de.symeda.sormas.app.backend.contact;

import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseEditAuthorization;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;

public class ContactEditAuthorization {

    public static boolean isContactEditAllowed(Contact contact){

        User user = ConfigProvider.getUser();
        Set<UserRole> userRole = user.getUserRoles();
        Case caseofContact = DatabaseHelper.getCaseDao().queryUuidBasic(contact.getCaseUuid());

        if (user.getUuid().equals(contact.getReportingUser().getUuid())){
            return true;
        }

        if (CaseEditAuthorization.isCaseEditAllowed(caseofContact)){
            return true;
        }

        if ((!userRole.stream().filter(UserRole::isSupervisor).collect(Collectors.toList()).isEmpty())) {
            Boolean contactRegion = checkMatchRegionForUserAndContact(contact, user);
            if (contactRegion != null) return contactRegion;
        }

        if ((!userRole.stream().filter(UserRole::isOfficer).collect(Collectors.toList()).isEmpty())) {
            District contactDistrict = contact.getDistrict();
            if (contactDistrict != null) {
                return  contactDistrict.equals(user.getDistrict());
            }
        }

        if ((!userRole.stream().filter(UserRole::isPortHealthUser).collect(Collectors.toList()).isEmpty())) {
            Boolean contactRegion = checkMatchRegionForUserAndContact(contact, user);
            if (contactRegion != null) return contactRegion;
        }

        if ((!userRole.stream().filter(UserRole::isNational).collect(Collectors.toList()).isEmpty())) {
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
