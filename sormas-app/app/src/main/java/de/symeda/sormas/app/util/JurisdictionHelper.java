package de.symeda.sormas.app.util;

import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.jurisdiction.UserJurisdiction;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.user.User;

public class JurisdictionHelper {

	public static UserJurisdiction createUserJurisdiction(User user) {
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

	public static ContactJurisdictionDto createContactJurisdictionDto(Contact contact) {
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
