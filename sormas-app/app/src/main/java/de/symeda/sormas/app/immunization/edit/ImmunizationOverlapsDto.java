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

package de.symeda.sormas.app.immunization.edit;

import java.util.Date;

import de.symeda.sormas.api.Disease;

public class ImmunizationOverlapsDto {

    private Date startDateExisting;
    private Date endDateExisting;
    private Date startDate;
    private Date endDate;
    private Disease disease;

    public Date getStartDateExisting() {
        return startDateExisting;
    }

    public void setStartDateExisting(Date startDateExisting) {
        this.startDateExisting = startDateExisting;
    }

    public Date getEndDateExisting() {
        return endDateExisting;
    }

    public void setEndDateExisting(Date endDateExisting) {
        this.endDateExisting = endDateExisting;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }
}
