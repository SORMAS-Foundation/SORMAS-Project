package de.symeda.sormas.app.util;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.UserJurisdiction;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.user.User;

public class JurisdictionHelper {
    public static UserJurisdiction createUserJurisdiction(User user){
        UserJurisdiction jurisdiction = new UserJurisdiction();

        jurisdiction.setUuid(user.getUuid());

        if (user.getRegion() != null) {
            jurisdiction.setRegionUuid(user.getRegion().getUuid());
        }
        if (user.getDistrict() != null) {
            jurisdiction.setDistrictUuid(user.getDistrict().getUuid());
        }
        if (user.getCommunity() != null) {
            jurisdiction.setCommunityUuid(user.getCommunity().getUuid());
        }
        if (user.getHealthFacility() != null) {
            jurisdiction.setHealthFacilityUuid(user.getHealthFacility().getUuid());
        }
        if (user.getPointOfEntry() != null) {
            jurisdiction.setPointOfEntryUuid(user.getPointOfEntry().getUuid());
        }

        return jurisdiction;
    }

    public static CaseJurisdictionDto createCaseJurisdictionDto(Case caze) {
        if (caze == null) {
            return null;
        }
        CaseJurisdictionDto dto = new CaseJurisdictionDto();

        if (caze.getReportingUser() != null) {
            dto.setReportingUserUuid(caze.getReportingUser().getUuid());
        }
        if (caze.getRegion() != null) {
            dto.setRegionUuid(caze.getRegion().getUuid());
        }
        if (caze.getDistrict() != null) {
            dto.setDistrictUuid(caze.getDistrict().getUuid());
        }
        if (caze.getCommunity() != null) {
            dto.setCommunityUuid(caze.getCommunity().getUuid());
        }
        if (caze.getHealthFacility() != null) {
            dto.setHealthFacilityUuid(caze.getHealthFacility().getUuid());
        }
        if (caze.getPointOfEntry() != null) {
            dto.setPointOfEntryUuid(caze.getPointOfEntry().getUuid());
        }

        return dto;
    }
}
