package de.symeda.sormas.backend.campaign;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.backend.common.CoreAdo;
import org.hibernate.annotations.Type;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

@Entity(name = "campaigns")
@Audited
public class Campaign extends CoreAdo {

	private static final long serialVersionUID = -2744033662114826543L;

	public static final String TABLE_NAME = "campaigns";
	public static final String CAMPAIGN_CAMPAIGNFORMMETA_TABLE_NAME = "campaign_campaignformmeta";

	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String CREATING_USER = "creatingUser";
	public static final String CAMPAIGN_FORM_METAS = "campaignFormMetas";
	public static final String CAMPAIGN_DASHBOARD_ELEMENTS = "dashboardElements";

	private String name;
	private String description;
	private Date startDate;
	private Date endDate;
	private User creatingUser;
	private List<CampaignDashboardElement> dashboardElements;
	private Set<CampaignFormMeta> campaignFormMetas = new HashSet<>();

	@Column(length = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 512)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public User getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(User creatingUser) {
		this.creatingUser = creatingUser;
	}

	@Override
	public String toString() {
		return name;
	}

	@AuditedIgnore
	@Type(type = ModelConstants.HIBERNATE_TYPE_JSON)
	@Column(columnDefinition = ModelConstants.COLUMN_DEFINITION_JSON)
	public List<CampaignDashboardElement> getDashboardElements() {
		return dashboardElements;
	}

	public void setDashboardElements(List<CampaignDashboardElement> dashboardElements) {
		this.dashboardElements = dashboardElements;
	}

	@AuditedIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = CAMPAIGN_CAMPAIGNFORMMETA_TABLE_NAME,
		joinColumns = @JoinColumn(name = "campaign_id"),
		inverseJoinColumns = @JoinColumn(name = "campaignformmeta_id"))
	public Set<CampaignFormMeta> getCampaignFormMetas() {
		return campaignFormMetas;
	}

	public void setCampaignFormMetas(Set<CampaignFormMeta> campaignFormMetas) {
		this.campaignFormMetas = campaignFormMetas;
	}
}
