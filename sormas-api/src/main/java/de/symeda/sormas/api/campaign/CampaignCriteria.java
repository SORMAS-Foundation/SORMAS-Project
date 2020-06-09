package de.symeda.sormas.api.campaign;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.utils.IgnoreForUrl;

import java.io.Serializable;
import java.util.Date;

public class CampaignCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = -1500588264749724525L;

	private Date startDateAfter;
	private Date startDateBefore;
	private Date endDateAfter;
	private Date endDateBefore;
	private Boolean deleted = Boolean.FALSE;
	private String freeText;
	private EntityRelevanceStatus relevanceStatus;

	public CampaignCriteria startDateAfter(Date startDateAfter) {
		this.startDateAfter = startDateAfter;
		return this;
	}

	public Date getStartDateAfter() {
		return startDateAfter;
	}

	public CampaignCriteria startDateBefore(Date startDateBefore) {
		this.startDateBefore = startDateBefore;
		return this;
	}

	public Date getStartDateBefore() {
		return startDateBefore;
	}

	public CampaignCriteria endDateAfter(Date endDateAfter) {
		this.endDateAfter = endDateAfter;
		return this;
	}

	public Date getEndDateAfter() {
		return endDateAfter;
	}

	public CampaignCriteria endDateBefore(Date endDateBefore) {
		this.endDateBefore = endDateBefore;
		return this;
	}

	public Date getEndDateBefore() {
		return endDateBefore;
	}

	public CampaignCriteria deleted(Boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	@IgnoreForUrl
	public Boolean getDeleted() {
		return deleted;
	}

	public CampaignCriteria freeText(String freeText) {
		this.freeText = freeText;
		return this;
	}

	@IgnoreForUrl
	public String getFreeText() {
		return freeText;
	}

	public CampaignCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}
}
