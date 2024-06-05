/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.selfreport;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class SelfReportCriteria extends BaseCriteria implements Serializable {

	public static final String FREE_TEXT = "freeText";
	public static final String TYPE = "type";
	public static final String DISEASE = "disease";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String REPORT_DATE = "reportDate";
	private static final long serialVersionUID = 7245463026500908524L;

	private String freeText;
	private SelfReportType type;
	private Disease disease;
	private DiseaseVariant diseaseVariant;
	private SelfReportInvestigationStatus investigationStatus;
	private EntityRelevanceStatus relevanceStatus;
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private Date reportDateFrom;
	private Date reportDateTo;

	private CaseReferenceDto caze;
	private ContactReferenceDto contact;

	@IgnoreForUrl
	public String getFreeText() {
		return freeText;
	}

	public SelfReportCriteria freeText(String freeText) {
		this.freeText = freeText;
		return this;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public SelfReportType getType() {
		return type;
	}

	public void setType(SelfReportType type) {
		this.type = type;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	public SelfReportInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(SelfReportInvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public void setRelevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
	}

	public SelfReportCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		setRelevanceStatus(relevanceStatus);
		return this;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public void setDateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
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

	public void reportDateBetween(Date reportDateFrom, Date reportDateTo, DateFilterOption dateFilterOption) {
		this.reportDateFrom = reportDateFrom;
		this.reportDateTo = reportDateTo;
		this.dateFilterOption = dateFilterOption;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public SelfReportCriteria setCaze(CaseReferenceDto caze) {
		this.caze = caze;
		return this;
	}

	public ContactReferenceDto getContact() {
		return contact;
	}

	public SelfReportCriteria setContact(ContactReferenceDto contact) {
		this.contact = contact;
		return this;
	}
}
