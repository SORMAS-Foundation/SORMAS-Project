package de.symeda.sormas.backend.contact;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
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

	public boolean isInJurisdiction(Contact contact) {
		return isInJurisdiction(createContactJurisdictionDto(contact));
	}

	public boolean isInJurisdiction(ContactJurisdictionDto contactJurisdiction) {
		User user = userService.getCurrentUser();

		if (contactJurisdiction.getReportingUserUuid() != null && DataHelper.isSame(user, wrapUuid(contactJurisdiction.getReportingUserUuid()))) {
			return true;
		}

		if (contactJurisdiction.getCaseJurisdiction() != null && caseEditAuthorization.isInJurisdiction(contactJurisdiction.getCaseJurisdiction())) {
			return true;
		}

		if (userService.hasAnyRole(UserRole.getSupervisorRoles())) {
			return DataHelper.isSame(wrapUuid(contactJurisdiction.getRegionUuId()), user.getRegion());
		}

		if (userService.hasAnyRole(UserRole.getOfficerRoles())) {
			return DataHelper.isSame(wrapUuid(contactJurisdiction.getDistrictUuid()), user.getDistrict());
		}

		if (userService.hasRole(UserRole.NATIONAL_USER)) {
			return true;
		}

		return false;
	}

	private ContactJurisdictionDto createContactJurisdictionDto(Contact contact) {
		if (contact == null) {
			return null;
		}
		ContactJurisdictionDto dto = new ContactJurisdictionDto();

		if (contact.getReportingUser() != null) {
			dto.setReportingUserUuid(contact.getReportingUser().getUuid());
		}
		if (contact.getRegion() != null) {
			dto.setRegionUuId(contact.getRegion().getUuid());
		}
		if (contact.getDistrict() != null) {
			dto.setDistrictUuid(contact.getDistrict().getUuid());
		}

		if (contact.getCaze() != null) {
			CaseEditAuthorization.createCaseJurisdictionDto(contact.getCaze());
		}

		return dto;
	}

	private HasUuid wrapUuid(final String uuId) {
		return () -> uuId;
	}
}
