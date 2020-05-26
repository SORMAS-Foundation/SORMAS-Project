package de.symeda.sormas.api.campaign;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class CampaignDto extends EntityDto {

	private static final long serialVersionUID = 8301363182762462920L;

	public static final String I18N_PREFIX = "Campaign";

	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String CREATING_USER = "creatingUser";

	private String name;
	private String description;
	private Date startDate;
	private Date endDate;
	private UserReferenceDto creatingUser;

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

	public UserReferenceDto getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(UserReferenceDto creatingUser) {
		this.creatingUser = creatingUser;
	}
}
