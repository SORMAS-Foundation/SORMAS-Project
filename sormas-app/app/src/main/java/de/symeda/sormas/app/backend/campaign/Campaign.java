/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.campaign;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.user.User;

@Entity(name = Campaign.TABLE_NAME)
@DatabaseTable(tableName = Campaign.TABLE_NAME)
public class Campaign extends PseudonymizableAdo {

	public static final String TABLE_NAME = "campaigns";
	public static final String I18N_PREFIX = "Campaign";

	public static final String CAMPAIGN_CAMPAIGNFORMMETA_TABLE_NAME = "campaign_campaignformmeta";

	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String CREATING_USER = "creatingUser";
	public static final String CAMPAIGN_FORM_METAS = "campaignFormMetas";
	public static final String CAMPAIGN_DASHBOARD_ELEMENTS = "dashboardElements";
	public static final String ARCHIVED = "archived";

	@Column(length = COLUMN_LENGTH_BIG)
	private String name;

	@Column(length = COLUMN_LENGTH_BIG)
	private String description;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date startDate;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date endDate;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User creatingUser;

	@DatabaseField
	private boolean archived;

	private List<CampaignFormMeta> campaignFormMetas = new ArrayList<>();

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

	public User getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(User creatingUser) {
		this.creatingUser = creatingUser;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public List<CampaignFormMeta> getCampaignFormMetas() {
		return campaignFormMetas;
	}

	public void setCampaignFormMetas(List<CampaignFormMeta> campaignFormMetas) {
		this.campaignFormMetas = campaignFormMetas;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
