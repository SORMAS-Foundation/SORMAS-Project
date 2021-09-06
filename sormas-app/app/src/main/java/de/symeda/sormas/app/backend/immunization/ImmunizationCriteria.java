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

package de.symeda.sormas.app.backend.immunization;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Region;

public class ImmunizationCriteria implements Serializable {

    private Person person;
    private Disease disease;
    private Region responsibleRegion;
    private ImmunizationStatus immunizationStatus;
    private ImmunizationManagementStatus immunizationManagementStatus;
    private MeansOfImmunization meansOfImmunization;

    private Date reportDateFrom;
    private Date reportDateTo;

    private Date validFrom;
    private Date validUntil;

    private Date startDateFrom;
    private Date endDateTo;

    private Date positiveTestResultDateFrom;
    private Date positiveTestResultDateTo;

    private Date recoveryDateFrom;
    private Date recoveryDateTo;

    private Boolean overdueImmunization;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public Region getResponsibleRegion() {
        return responsibleRegion;
    }

    public void setResponsibleRegion(Region responsibleRegion) {
        this.responsibleRegion = responsibleRegion;
    }

    public ImmunizationStatus getImmunizationStatus() {
        return immunizationStatus;
    }

    public void setImmunizationStatus(ImmunizationStatus immunizationStatus) {
        this.immunizationStatus = immunizationStatus;
    }

    public ImmunizationManagementStatus getImmunizationManagementStatus() {
        return immunizationManagementStatus;
    }

    public void setImmunizationManagementStatus(ImmunizationManagementStatus immunizationManagementStatus) {
        this.immunizationManagementStatus = immunizationManagementStatus;
    }

    public MeansOfImmunization getMeansOfImmunization() {
        return meansOfImmunization;
    }

    public void setMeansOfImmunization(MeansOfImmunization meansOfImmunization) {
        this.meansOfImmunization = meansOfImmunization;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public Date getReportDateFrom() {
        return reportDateFrom;
    }

    public void setReportDateFrom(Date reportDateFrom) {
        this.reportDateFrom = reportDateFrom;
    }

    public Date getReportDateTo() {
        return reportDateTo;
    }

    public void setReportDateTo(Date reportDateTo) {
        this.reportDateTo = reportDateTo;
    }

    public Date getStartDateFrom() {
        return startDateFrom;
    }

    public void setStartDateFrom(Date startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public Date getEndDateTo() {
        return endDateTo;
    }

    public void setEndDateTo(Date endDateTo) {
        this.endDateTo = endDateTo;
    }

    public Boolean getOverdueImmunization() {
        return overdueImmunization;
    }

    public void setOverdueImmunization(Boolean overdueImmunization) {
        this.overdueImmunization = overdueImmunization;
    }

    public Date getRecoveryDateFrom() {
        return recoveryDateFrom;
    }

    public void setRecoveryDateFrom(Date recoveryDateFrom) {
        this.recoveryDateFrom = recoveryDateFrom;
    }

    public Date getRecoveryDateTo() {
        return recoveryDateTo;
    }

    public void setRecoveryDateTo(Date recoveryDateTo) {
        this.recoveryDateTo = recoveryDateTo;
    }

    public Date getPositiveTestResultDateFrom() {
        return positiveTestResultDateFrom;
    }

    public void setPositiveTestResultDateFrom(Date positiveTestResultDateFrom) {
        this.positiveTestResultDateFrom = positiveTestResultDateFrom;
    }

    public Date getPositiveTestResultDateTo() {
        return positiveTestResultDateTo;
    }

    public void setPositiveTestResultDateTo(Date positiveTestResultDateTo) {
        this.positiveTestResultDateTo = positiveTestResultDateTo;
    }
}
