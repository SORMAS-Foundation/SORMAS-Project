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

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.FeatureIndependent;
import de.symeda.sormas.api.utils.PersonalData;
import java.util.Date;

@FeatureIndependent
public class NotifierReferenceDto extends ReferenceDto {

    private static final long serialVersionUID = 1L;

    @PersonalData
    private String firstName;

    @PersonalData
    private String lastName;

    @PersonalData
    private Date versionDate;

    public NotifierReferenceDto() {
        // Default constructor
    }

    public NotifierReferenceDto(String uuid) {
        setUuid(uuid);
    }

    public NotifierReferenceDto(String uuid, String firstName, String lastName) {
        setUuid(uuid);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String getCaption() {
        return String.format("%s %s", firstName, lastName);
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

    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }
}
