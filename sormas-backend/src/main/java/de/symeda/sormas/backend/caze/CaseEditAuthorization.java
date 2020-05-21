package de.symeda.sormas.backend.caze;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

@Stateless(name = "CaseEditAuthorization")
@LocalBean
public class CaseEditAuthorization {

	@EJB
	private UserService userService;

	public Boolean isInJurisdiction(Case caze) {
		return isInJurisdiction(createCaseJurisdictionDto(caze));
	}

	public Boolean isInJurisdiction(CaseJurisdictionDto caseJurisdictionDto) {

		User user = userService.getCurrentUser();

		if (userService.hasRole(UserRole.NATIONAL_USER) ||
				caseJurisdictionDto.getDistrictUud() != null && DataHelper.isSame(user, wrapUuid(caseJurisdictionDto.getDistrictUud()))) {
			return true;
		}

		if (userService.hasAnyRole(UserRole.getSupervisorRoles())) {
			return DataHelper.isSame(wrapUuid(caseJurisdictionDto.getRegionUui()), user.getRegion());
		}

		if (userService.hasAnyRole(UserRole.getOfficerRoles())) {
			return DataHelper.isSame(wrapUuid(caseJurisdictionDto.getDistrictUud()), user.getDistrict());
		}

		if ((userService.hasRole(UserRole.COMMUNITY_INFORMANT))) {
			return DataHelper.isSame(wrapUuid(caseJurisdictionDto.getCommunityUuid()), user.getCommunity());
		}

		if ((userService.hasRole(UserRole.HOSPITAL_INFORMANT))) {
			return DataHelper.isSame(wrapUuid(caseJurisdictionDto.getHealthFacilityUuid()), user.getHealthFacility());
		}

		if ((userService.hasRole(UserRole.POE_INFORMANT))) {
			return DataHelper.isSame(wrapUuid(caseJurisdictionDto.getPointOfEntryUuid()), user.getPointOfEntry());
		}

		return false;
	}

	private HasUuid wrapUuid(final String uuId) {
		return () -> uuId;
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
			dto.setRegionUui(caze.getRegion().getUuid());
		}
		if (caze.getDistrict() != null) {
			dto.setDistrictUud(caze.getDistrict().getUuid());
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
