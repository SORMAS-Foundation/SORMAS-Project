package de.symeda.sormas.backend.campaign.statistics;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.data.CampaignFormDataReferenceDto;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Entity(name = "campaignFormData")
@Audited
public class CampaignStatistics extends AbstractDomainObject {

	public static final String TABLE_NAME = "campaignFormData";

	public static final String FORM_VALUES = "formValues";
	public static final String CAMPAIGN = "campaign";
	public static final String CAMPAIGN_FORM_META = "campaignFormMeta";
	public static final String FORM_DATE = "formDate";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String ARCHIVED = "archived";

	private static final long serialVersionUID = -8021065433714419288L;

	private List<CampaignFormDataEntry> formValues;
	private Campaign campaign;
	private CampaignFormMeta campaignFormMeta;
	private Date formDate;
	private Region region;
	private District district;
	private Community community;
	private User creatingUser;
	private boolean archived;

	@AuditedIgnore
	@Type(type = "json")
	@Column(columnDefinition = "json")
	public List<CampaignFormDataEntry> getFormValues() {
		return formValues;
	}

	public void setFormValues(List<CampaignFormDataEntry> formValues) {
		this.formValues = formValues;
	}

	@ManyToOne()
	@JoinColumn(nullable = false)
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@ManyToOne()
	@JoinColumn(nullable = false)
	public CampaignFormMeta getCampaignFormMeta() {
		return campaignFormMeta;
	}

	public void setCampaignFormMeta(CampaignFormMeta campaignFormMeta) {
		this.campaignFormMeta = campaignFormMeta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getFormDate() {
		return formDate;
	}

	public void setFormDate(Date formDate) {
		this.formDate = formDate;
	}

	@ManyToOne()
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne()
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne()
	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	@ManyToOne
	@JoinColumn
	public User getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(User creatingUser) {
		this.creatingUser = creatingUser;
	}

	@Column
	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public CampaignFormDataReferenceDto toReference() {
		return new CampaignFormDataReferenceDto(getUuid());
	}
}
