package de.symeda.sormas.backend.contact;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.ContactJurisdictionHelper;
import de.symeda.sormas.backend.caze.CaseJurisdictionChecker;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


@Stateless(name = "ContactJurisdictionChecker")
@LocalBean
public class ContactJurisdictionChecker {

	@EJB
	private UserService userService;
	@EJB
	private CaseJurisdictionChecker caseJurisdictionChecker;

	public boolean isInJurisdiction(Contact contact) {
		return isInJurisdiction(createContactJurisdictionDto(contact));
	}

	public boolean isInJurisdiction(ContactJurisdictionDto contactJurisdiction) {
		User user = userService.getCurrentUser();

		return de.symeda.sormas.api.utils.jurisdiction.ContactJurisdictionHelper.isInJurisdiction(userService::hasAnyRole, JurisdictionHelper.createUserJurisdiction(user), contactJurisdiction);
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
			dto.setCaseJurisdiction(JurisdictionHelper.createCaseJurisdictionDto(contact.getCaze()));
		}

		return dto;
	}
}
