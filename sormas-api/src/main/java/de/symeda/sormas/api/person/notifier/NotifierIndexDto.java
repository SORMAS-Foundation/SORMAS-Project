/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.io.Serializable;

import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class NotifierIndexDto extends PseudonymizableIndexDto implements Serializable {

    public static final String REGISTRATION_NUMBER = "registrationNumber";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String ADDRESS = "address";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";

    @PersonalData
    private String registrationNumber;

    @PersonalData
    private String firstName;

    @PersonalData
    private String lastName;

    @PersonalData
    private String address;

    @PersonalData
    private String phone;

    @PersonalData
    private String email;

    public NotifierIndexDto(String uuid) {
        super(uuid);
    }

    public NotifierIndexDto(
        String uuid,
        String registrationNumber,
        String firstName,
        String lastName,
        String address,
        String phone,
        String email) {
        super(uuid);
        this.registrationNumber = registrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

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

    @Override
    public String getCaption() {
        return String.format("%s %s", firstName, lastName);
    }
}
