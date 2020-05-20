package de.symeda.sormas.backend.caze;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

@Stateless(name = "CaseEditAuthorization")
@LocalBean
public class CaseEditAuthorization {

	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;

	public Boolean caseEditAllowedCheck(Case caze) {

		return isInJurisdiction(caze.getReportingUser(), caze.getRegion(), caze.getDistrict(),
				caze.getCommunity(), caze.getHealthFacility(), caze.getPointOfEntry());
	}

	public boolean isInJurisdiction(CaseIndexDto dto) {
		return isInJurisdiction(wrapUuid(dto.getReportingUserUuid()), wrapUuid(dto.getRegionUuid()), wrapUuid(dto.getDistrictUuid()),
				wrapUuid(dto.getCommunityUuid()), wrapUuid(dto.getHealthFacilityUuid()), wrapUuid(dto.getPointOfEntryUuid()));
	}

	public boolean isInJurisdiction(CaseExportDto dto) {
		return isInJurisdiction(wrapUuid(dto.getReportingUserUuid()), wrapUuid(dto.getRegionUuid()), wrapUuid(dto.getDistrictUuid()),
				wrapUuid(dto.getCommunityUuid()), wrapUuid(dto.getHealthFacilityUuid()), wrapUuid(dto.getPointOfEntryUuid()));
	}

	public boolean isInJurisdiction(CaseDataDto dto) {
		return isInJurisdiction(dto.getReportingUser(), dto.getRegion(), dto.getDistrict(),
				dto.getCommunity(), dto.getHealthFacility(), dto.getPointOfEntry());
	}

	public boolean isInJurisdiction(HasUuid reportingUser, HasUuid region, HasUuid district,
									 HasUuid community, HasUuid healthFacility, HasUuid pointOfEntry) {
		User user = userService.getCurrentUser();

		if (userService.hasRole(UserRole.NATIONAL_USER) ||
				reportingUser != null && DataHelper.isSame(user, reportingUser)) {
			return true;
		}

		if (userService.hasAnyRole(UserRole.getSupervisorRoles())) {
			return DataHelper.isSame(region, user.getRegion());
		}

		if (userService.hasAnyRole(UserRole.getOfficerRoles())) {
			return DataHelper.isSame(district, user.getDistrict());
		}

		if ((userService.hasRole(UserRole.COMMUNITY_INFORMANT))) {
			return DataHelper.isSame(community, user.getCommunity());
		}

		if ((userService.hasRole(UserRole.HOSPITAL_INFORMANT))) {
			return DataHelper.isSame(healthFacility, user.getHealthFacility());
		}

		if ((userService.hasRole(UserRole.POE_INFORMANT))) {
			return DataHelper.isSame(pointOfEntry, user.getPointOfEntry());
		}

		return false;
	}

	private HasUuid wrapUuid(final String uuId) {
		return () -> uuId;
	}
}
