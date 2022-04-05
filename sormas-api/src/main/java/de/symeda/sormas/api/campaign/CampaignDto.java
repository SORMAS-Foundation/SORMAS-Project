package de.symeda.sormas.api.campaign;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

public class CampaignDto extends EntityDto {

	private static final long serialVersionUID = 8301363182762462920L;

	public static final String I18N_PREFIX = "Campaign";

	public static final String NAME = "name";
	public static final String ROUND = "round";
	public static final String DESCRIPTION = "description";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String CAMPAIGN_YEAR = "campaignYear";
	public static final String CREATING_USER = "creatingUser";
	public static final String CAMPAIGN_FORM_METAS = "campaignFormMetas";
	public static final String CAMPAIGN_TYPES = "campaignTypes";

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String name;
	private String round;
	//private String campaignTypes;
	
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String description;
	private Date startDate;
	private Date endDate;
	private String campaignYear;
	private UserReferenceDto creatingUser;
	private Set<CampaignFormMetaReferenceDto> campaignFormMetas;
	
	@Valid
	private List<CampaignDashboardElement> campaignDashboardElements;

	public static CampaignDto build() {
		CampaignDto campaign = new CampaignDto();
		campaign.setUuid(DataHelper.createUuid());
		return campaign;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	
	
	/*public String getCampaignTypes() {
		return campaignTypes;
	}

	public void setCampaignTypes(String campaignTypes) {
		this.campaignTypes = campaignTypes;
	}
*/
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getCampaignYear() {
		return campaignYear;
	}

	public void setCampaignYear(String campaignYear) {
		this.campaignYear = campaignYear;
	}

	public UserReferenceDto getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(UserReferenceDto creatingUser) {
		this.creatingUser = creatingUser;
	}

	public Set<CampaignFormMetaReferenceDto> getCampaignFormMetas() {
		return campaignFormMetas;
	}

	public void setCampaignFormMetas(Set<CampaignFormMetaReferenceDto> campaignFormMetas) {
		this.campaignFormMetas = campaignFormMetas;
	}

	public List<CampaignDashboardElement> getCampaignDashboardElements() {
		return campaignDashboardElements;
	}

	public void setCampaignDashboardElements(List<CampaignDashboardElement> campaignDashboardElements) {
		this.campaignDashboardElements = campaignDashboardElements;
	}
}
