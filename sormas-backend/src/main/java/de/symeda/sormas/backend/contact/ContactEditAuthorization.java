package de.symeda.sormas.backend.contact;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.contact.ContactCaseJurisdictionDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseEditAuthorization;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;


@Stateless(name = "ContactEditAuthorization")
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

	public boolean isContactEditAllowed(Contact contact) {
		ContactCaseJurisdictionDto contactCaseJurisdiction = null;
		Case caze = contact.getCaze();
		if (caze != null) {
			contactCaseJurisdiction = createCaseJurisdictionDto(caze);
		}
		return isInJurisdiction(contact.getReportingUser(), contactCaseJurisdiction, contact.getRegion(), contact.getDistrict());
	}

	public boolean isInJurisdiction(ContactIndexDto contact) {
		return isInJurisdiction(wrapUuid(contact.getReportingUserUuid()), contact.getCaseJurisdiction(),
				wrapUuid(contact.getRegionUuid()), wrapUuid(contact.getDistrictUuid()));
	}

	private boolean isInJurisdiction(HasUuid reportingUser, ContactCaseJurisdictionDto caseJurisdiction, HasUuid region, HasUuid district) {

		User user = userService.getCurrentUser();

		if (reportingUser != null && DataHelper.isSame(user, reportingUser)) {
			return true;
		}

		if (caseJurisdiction != null && caseEditAuthorization.isInJurisdiction(
				wrapUuid(caseJurisdiction.getReportingUserUuid()), wrapUuid(caseJurisdiction.getDistrictUud()), wrapUuid(caseJurisdiction.getRegionUui()),
				wrapUuid(caseJurisdiction.getCommunityUuid()), wrapUuid(caseJurisdiction.getHealthFacilityUuid()), wrapUuid(caseJurisdiction.getPointOfEntryUuid())
		)) {
			return true;
		}

		if (userService.hasAnyRole(UserRole.getSupervisorRoles())) {
			return DataHelper.isSame(region, user.getRegion());
		}

		if (userService.hasAnyRole(UserRole.getOfficerRoles())) {
			return DataHelper.isSame(district, user.getDistrict());
		}

		if (userService.hasRole(UserRole.NATIONAL_USER)) {
			return true;
		}

		return false;
	}

	private ContactCaseJurisdictionDto createCaseJurisdictionDto(Case caze) {
		if (caze == null) {
			return null;
		}
		ContactCaseJurisdictionDto dto = new ContactCaseJurisdictionDto();

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

	private HasUuid wrapUuid(final String uuId) {
		return () -> uuId;
	}
}
