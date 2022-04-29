package de.symeda.sormas.backend.dashboard;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.sample.PathogenTestResultType;

class PathogenTestResultDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long caseId;
	private PathogenTestResultType pathogenTestResultType;
	private Date sampleDateTime;

	public PathogenTestResultDto(Long caseId, PathogenTestResultType pathogenTestResultType, Date sampleDateTime) {
		this.caseId = caseId;
		this.pathogenTestResultType = pathogenTestResultType;
		this.sampleDateTime = sampleDateTime;
	}

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public PathogenTestResultType getPathogenTestResultType() {
		return pathogenTestResultType;
	}

	public void setPathogenTestResultType(PathogenTestResultType pathogenTestResultType) {
		this.pathogenTestResultType = pathogenTestResultType;
	}

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}
}
