package de.symeda.sormas.app.backend.contact;

import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.jurisdiction.ContactJurisdictionHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.JurisdictionHelper;

public class ContactEditAuthorization {

	public static boolean isContactEditAllowed(Contact contact) {
		User user = ConfigProvider.getUser();

		return ContactJurisdictionHelper.isInJurisdiction(
			UserRole.getJurisdictionLevel(user.getUserRoles()),
			JurisdictionHelper.createUserJurisdiction(user),
			createContactJurisdictionDto(contact));
	}

	private static ContactJurisdictionDto createContactJurisdictionDto(Contact contact) {
		if (contact == null) {
			return null;
		}
		ContactJurisdictionDto dto = new ContactJurisdictionDto();

		if (contact.getReportingUser() != null) {
			dto.setReportingUserUuid(contact.getReportingUser().getUuid());
		}
		if (contact.getRegion() != null) {
			dto.setRegionUuid(contact.getRegion().getUuid());
		}
		if (contact.getDistrict() != null) {
			dto.setDistrictUuid(contact.getDistrict().getUuid());
		}

		if (contact.getCaseUuid() != null) {
			Case caseOfContact = DatabaseHelper.getCaseDao().queryUuidBasic(contact.getCaseUuid());
			JurisdictionHelper.createCaseJurisdictionDto(caseOfContact);
		}

		return dto;
	}
}
