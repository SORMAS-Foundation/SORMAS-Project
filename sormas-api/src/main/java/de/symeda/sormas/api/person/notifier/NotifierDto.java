/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.person.notifier;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.IsPerson;
import de.symeda.sormas.api.utils.FeatureIndependent;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@FeatureIndependent
public class NotifierDto extends PseudonymizableDto implements IsPerson {

    private static final long serialVersionUID = 1L;

    public static final String REGISTRATION_NUMBER = "registrationNumber";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String ADDRESS = "address";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String AGENT_FIRST_NAME = "agentFirstName";
    public static final String AGENT_LAST_NAME = "agentLastName";

    @Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
    @PersonalData
    private String registrationNumber;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
    @PersonalData
    private String firstName;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
    @PersonalData
    private String lastName;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
    @PersonalData
    private String address;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
    @PersonalData
    private String phone;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
    @PersonalData
    private String email;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
    @PersonalData
    private String agentFirstName;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
    @PersonalData
    private String agentLastName;

    // Getters and setters
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgentFirstName() {
        return agentFirstName;
    }

    public void setAgentFirstName(String agentFirstName) {
        this.agentFirstName = agentFirstName;
    }

    public String getAgentLastName() {
        return agentLastName;
    }

    public void setAgentLastName(String agentLastName) {
        this.agentLastName = agentLastName;
    }
}
