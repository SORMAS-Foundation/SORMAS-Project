/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.person;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;

@Entity(name = PersonContactDetail.TABLE_NAME)
@DatabaseTable(tableName = PersonContactDetail.TABLE_NAME)
@EmbeddedAdo
public class PersonContactDetail extends PseudonymizableAdo {

    public static final String TABLE_NAME = "personContactDetail";
    public static final String I18N_PREFIX = "PersonContactDetail";

    public static final String PERSON = "person";
    public static final String PRIMARY_CONTACT = "primaryContact";
    public static final String PERSON_CONTACT_DETAIL_TYPE = "personContactDetailType";
    public static final String PHONE_NUMBER_TYPE = "phoneNumberType";
    public static final String DETAILS = "details";
    public static final String CONTACT_INFORMATION = "contactInformation";
    public static final String ADDITIONAL_INFORMATION = "additionalInformation";
    public static final String THIRD_PARTY = "thirdParty";
    public static final String THIRD_PARTY_ROLE = "thirdPartyRole";
    public static final String THIRD_PARTY_NAME = "thirdPartyName";

    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
    private Person person;

    @DatabaseField
    private boolean primaryContact;

    @Enumerated(EnumType.STRING)
    private PersonContactDetailType personContactDetailType;
    @Enumerated(EnumType.STRING)
    private PhoneNumberType phoneNumberType;
    @Column(columnDefinition = "text")
    private String details;

    @Column(columnDefinition = "text")
    private String contactInformation;
    @Column(columnDefinition = "text")
    private String additionalInformation;

    @DatabaseField
    private boolean thirdParty;
    @Column(columnDefinition = "text")
    private String thirdPartyRole;
    @Column(columnDefinition = "text")
    private String thirdPartyName;

	public String getOwner() {
		return isThirdParty() ? getThirdPartyName() : String.valueOf((R.string.caption_this_person));
	}

	public String getOwnerName() {
		return isThirdParty() ? getThirdPartyName() : person.toString();
	}

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public boolean isPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(boolean primaryContact) {
        this.primaryContact = primaryContact;
    }

    public PersonContactDetailType getPersonContactDetailType() {
        return personContactDetailType;
    }

    public void setPersonContactDetailType(PersonContactDetailType personContactDetailType) {
        this.personContactDetailType = personContactDetailType;
    }

    public PhoneNumberType getPhoneNumberType() {
        return phoneNumberType;
    }

    public void setPhoneNumberType(PhoneNumberType phoneNumberType) {
        this.phoneNumberType = phoneNumberType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public boolean isThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(boolean thirdParty) {
        this.thirdParty = thirdParty;
    }

    public String getThirdPartyRole() {
        return thirdPartyRole;
    }

    public void setThirdPartyRole(String thirdPartyRole) {
        this.thirdPartyRole = thirdPartyRole;
    }

    public String getThirdPartyName() {
        return thirdPartyName;
    }

    public void setThirdPartyName(String thirdPartyName) {
        this.thirdPartyName = thirdPartyName;
    }

    @Override
    public String getI18nPrefix() {
        return I18N_PREFIX;
    }
}
