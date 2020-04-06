/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.backend.caze.Case;

public class ContactCriteria implements Serializable {

    private FollowUpStatus followUpStatus;
    private String textFilter;
    private ContactClassification contactClassification;
    private Disease disease;
    private Date reportDateFrom;
    private Date reportDateTo;
    private Case caze;

    public String getTextFilter() {
        return textFilter;
    }

    public ContactCriteria setTextFilter(String textFilter) {
        this.textFilter = textFilter;
        return this;
    }

    public FollowUpStatus getFollowUpStatus() {
        return followUpStatus;
    }

    public ContactCriteria followUpStatus(FollowUpStatus followUpStatus) {
        this.followUpStatus = followUpStatus;
        return this;
    }

    public ContactClassification getContactClassification() {
        return contactClassification;
    }

    public ContactCriteria setContactClassification(ContactClassification contactClassification) {
        this.contactClassification = contactClassification;
        return this;
    }

    public Disease getDisease() {
        return disease;
    }

    public ContactCriteria setDisease(Disease disease) {
        this.disease = disease;
        return this;
    }

    public Date getReportDateFrom() {
        return reportDateFrom;
    }

    public ContactCriteria setReportDateFrom(Date reportDateFrom) {
        this.reportDateFrom = reportDateFrom;
        return this;
    }

    public Date getReportDateTo() {
        return reportDateTo;
    }

    public ContactCriteria setReportDateTo(Date reportDateTo) {
        this.reportDateTo = reportDateTo;
        return this;
    }

    public Case getCaze() {
        return caze;
    }

    public ContactCriteria caze(Case caze) {
        this.caze = caze;
        return this;
    }

}

