/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.visit;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.importexport.ExportGroup;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.importexport.ExportTarget;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class VisitExportDto extends AbstractUuidDto implements IsVisit {

	private static final long serialVersionUID = 6432390815000039126L;

	public static final String I18N_PREFIX = "VisitExport";

	private Long id;

	private Long personId;
	private Long symptomsId;
	@PersonalData
	private String firstName;
	@PersonalData
	private String lastName;
	private String diseaseFormatted;
	private Date visitDateTime;
	private Long visitUserId;
	private VisitStatus visitStatus;
	@SensitiveData
	private String visitRemarks;
	@EmbeddedSensitiveData
	private SymptomsDto symptoms;

	@SensitiveData
	private Double reportLat;
	@SensitiveData
	private Double reportLon;

	private String personUuid;

	private VisitOrigin origin;

	private String visitUserName;

	private String visitUserRoles;

	private boolean isInJurisdiction;

	public VisitExportDto(
		Long id,
		String uuid,
		Long personId,
		String firstName,
		String lastName,
		Long symptomsId,
		Long visitUserId,
		Disease disease,
		Date visitDateTime,
		VisitStatus visitStatus,
		String visitRemarks,
		Double reportLat,
		Double reportLon,
		VisitOrigin origin,
		String personUuid,
		boolean isInJurisdiction) {
		super(uuid);
		this.id = id;
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.symptomsId = symptomsId;
		this.diseaseFormatted = DiseaseHelper.toString(disease, null);
		this.visitDateTime = visitDateTime;
		this.visitUserId = visitUserId;
		this.visitStatus = visitStatus;
		this.visitRemarks = visitRemarks;
		this.reportLat = reportLat;
		this.reportLon = reportLon;
		this.personUuid = personUuid;
		this.origin = origin;
		this.isInJurisdiction = isInJurisdiction;
	}

	public Long getId() {
		return id;
	}

	public Long getSymptomsId() {
		return symptomsId;
	}

	public Long getPersonId() {
		return personId;
	}

	public boolean getInJurisdiction() {
		return isInJurisdiction;
	}

	@Order(0)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(PersonDto.FIRST_NAME)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getFirstName() {
		return firstName;
	}

	@Order(1)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(PersonDto.LAST_NAME)
	@ExportGroup(ExportGroupType.SENSITIVE)
	public String getLastName() {
		return lastName;
	}

	@Order(2)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(VisitDto.DISEASE)
	@ExportGroup(ExportGroupType.CORE)
	public String getDiseaseFormatted() {
		return diseaseFormatted;
	}

	@Order(3)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(VisitDto.VISIT_DATE_TIME)
	@ExportGroup(ExportGroupType.CORE)
	public Date getVisitDateTime() {
		return visitDateTime;
	}

	public Long getVisitUserId() {
		return visitUserId;
	}

	@Order(4)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(VisitDto.VISIT_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public VisitStatus getVisitStatus() {
		return visitStatus;
	}

	@Order(5)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(VisitDto.VISIT_REMARKS)
	@ExportGroup(ExportGroupType.CORE)
	public String getVisitRemarks() {
		return visitRemarks;
	}

	@Order(6)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(VisitDto.REPORT_LAT)
	@ExportGroup(ExportGroupType.CORE)
	public Double getReportLat() {
		return reportLat;
	}

	@Order(7)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(VisitDto.REPORT_LON)
	@ExportGroup(ExportGroupType.CORE)
	public Double getReportLon() {
		return reportLon;
	}

	@Order(8)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(VisitDto.ORIGIN)
	@ExportGroup(ExportGroupType.CORE)
	public VisitOrigin getOrigin() {
		return origin;
	}

	@Order(9)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(value = VisitDto.VISIT_USER, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getVisitUserName() {
		return visitUserName;
	}

	@Order(10)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(value = VisitDto.VISIT_USER, combined = true)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public String getVisitUserRoles() {
		return visitUserRoles;
	}

	@Order(11)
	@ExportTarget(visitExportTypes = VisitExportType.CONTACT_VISITS)
	@ExportProperty(VisitDto.SYMPTOMS)
	@ExportGroup(ExportGroupType.ADDITIONAL)
	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setSymptomsId(Long symptomsId) {
		this.symptomsId = symptomsId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public void setDiseaseFormatted(String diseaseFormatted) {
		this.diseaseFormatted = diseaseFormatted;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public void setVisitUserId(Long visitUserId) {
		this.visitUserId = visitUserId;
	}

	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public void setOrigin(VisitOrigin origin) {
		this.origin = origin;
	}

	public void setVisitUserName(String visitUserName) {
		this.visitUserName = visitUserName;
	}

	public void setVisitUserRoles(Set<UserRoleReferenceDto> roles) {
		this.visitUserRoles = roles.stream().map(ReferenceDto::buildCaption).collect(Collectors.joining(", "));;
	}
}
