package de.symeda.sormas.api.labmessage;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class LabMessageCriteria extends BaseCriteria implements Serializable {

	public static final String I18N_PREFIX = "LabMessageCriteria";

	public static final String SEARCH_FIELD_LIKE = "searchFieldLike";
	public static final String MESSAGE_DATE_FROM = "messageDateFrom";
	public static final String MESSAGE_DATE_TO = "messageDateTo";
	public static final String BIRTH_DATE_FROM = "birthDateFrom";
	public static final String BIRTH_DATE_TO = "birthDateTo";
	public static final String ASSIGNEE = "assignee";
	public static final String TYPE = "type";

	private String uuid;
	private ExternalMessageType type;
	private LabMessageStatus labMessageStatus;
	private SampleReferenceDto sample;
	private String searchFieldLike;
	private Date messageDateFrom;
	private Date messageDateTo;
	private Date birthDateFrom;
	private Date birthDateTo;
	private UserReferenceDto assignee;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ExternalMessageType getType() {
		return type;
	}

	public void setType(ExternalMessageType type) {
		this.type = type;
	}

	public LabMessageStatus getLabMessageStatus() {
		return labMessageStatus;
	}

	public LabMessageCriteria labMessageStatus(LabMessageStatus labMessageStatus) {
		this.labMessageStatus = labMessageStatus;
		return this;
	}

	public SampleReferenceDto getSample() {
		return sample;
	}

	public void setSample(SampleReferenceDto sample) {
		this.sample = sample;
	}

	public String getSearchFieldLike() {
		return searchFieldLike;
	}

	public void setSearchFieldLike(String searchFieldLike) {
		this.searchFieldLike = searchFieldLike;
	}

	public Date getMessageDateFrom() {
		return messageDateFrom;
	}

	public void setMessageDateFrom(Date messageDateFrom) {
		this.messageDateFrom = messageDateFrom;
	}

	public Date getMessageDateTo() {
		return messageDateTo;
	}

	public void setMessageDateTo(Date messageDateTo) {
		this.messageDateTo = messageDateTo;
	}

	public Date getBirthDateFrom() {
		return birthDateFrom;
	}

	public void setBirthDateFrom(Date birthDateFrom) {
		this.birthDateFrom = birthDateFrom;
	}

	public Date getBirthDateTo() {
		return birthDateTo;
	}

	public void setBirthDateTo(Date birthDateTo) {
		this.birthDateTo = birthDateTo;
	}

	public UserReferenceDto getAssignee() {
		return assignee;
	}

	public void setAssignee(UserReferenceDto assignee) {
		this.assignee = assignee;
	}
}
