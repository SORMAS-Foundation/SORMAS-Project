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

package de.symeda.sormas.backend.person.notifier;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.backend.common.AbstractDomainObject;

/**
 * Represents a notifier entity in the system.
 * A notifier is an individual responsible for reporting.
 */
@Entity(name = Notifier.TABLE_NAME)
public class Notifier extends AbstractDomainObject {

    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "notifier";

    public static final String QUERY_FIND_AT_DATE = "Notifier.findAtDate";

    public static final String REGISTRATION_NUMBER_FIELD_NAME = "registrationNumber";
    public static final String FIRST_NAME_FIELD_NAME = "firstName";
    public static final String LAST_NAME_FIELD_NAME = "lastName";
    public static final String ADDRESS_FIELD_NAME = "address";
    public static final String PHONE_FIELD_NAME = "phone";
    public static final String EMAIL_FIELD_NAME = "email";

    private String registrationNumber;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String email;
    private String agentFirstName;
    private String agentLastName;

    @Column(nullable = false)
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    @Column
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column
    public String getAgentFirstName() {
        return agentFirstName;
    }

    public void setAgentFirstName(String agentFirstName) {
        this.agentFirstName = agentFirstName;
    }

    @Column
    public String getAgentLastName() {
        return agentLastName;
    }

    public void setAgentLastName(String agentLastName) {
        this.agentLastName = agentLastName;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
