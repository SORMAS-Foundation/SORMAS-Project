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

package de.symeda.sormas.api.campaign.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CampaignFormDataIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "CampaignFormData";

	public static final String UUID = "uuid";
	public static final String CAMPAIGN = "campaign";
	public static final String FORM = "form";
	public static final String AREA = "area";
	public static final String RCODE = "rcode";
	public static final String REGION = "region";
	public static final String PCODE = "pcode";
	public static final String DISTRICT = "district";
	public static final String DCODE = "dcode";
	public static final String COMMUNITY = "community";
	public static final String CCODE = "ccode";
	public static final String FORM_DATE = "formDate";
	public static final String FORM_TYPE = "formType";

	private static final long serialVersionUID = -6672198324526771162L;

	private String uuid;
	private String campaign;
	private String form;
	private List<CampaignFormDataEntry> formValues;
	private String area;
	private Long rcode;
	private String region;
	private Long pcode;
	private String district;
	private Long dcode;
	private String community;
	private Long ccode;
	private Date formDate;
	private String formType;

	public CampaignFormDataIndexDto(
		String uuid,
		String campaign,
		String form,
		Object formValues,
		String area,
		Long rcode,
		String region,
		Long pcode,
		String district,
		Long dcode,
		String community,
		Long ccode,
		Date formDate,
		String formType) {
		this.uuid = uuid;
		this.campaign = campaign;
		this.form = form;
		this.formValues = (List<CampaignFormDataEntry>) formValues;
		this.area = area;
		this.rcode = rcode;
		this.region = region;
		this.pcode = pcode;
		this.district = district;
		this.dcode = dcode;
		this.community = community;
		this.ccode = ccode;
		this.formDate = formDate;
		this.formType = formType;
	}
	
	public CampaignFormDataIndexDto(
			String uuid,
			String campaign,
			String form,
			Object formValues,
			String area,
			String region,
			String district,
			String community,
			Date formDate
			) {
			this.uuid = uuid;
			this.campaign = campaign;
			this.form = form;
			this.formValues = (List<CampaignFormDataEntry>) formValues;
			this.area = area;
			this.region = region;
			this.district = district;
			this.community = community;
			this.formDate = formDate;
		}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public List<CampaignFormDataEntry> getFormValues() {
		return formValues;
	}

	public void setFormValues(List<CampaignFormDataEntry> formValues) {
		this.formValues = formValues;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public Date getFormDate() {
		return formDate;
	}

	public void setFormDate(Date formDate) {
		this.formDate = formDate;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public Long getRcode() {
		return rcode;
	}

	public void setRcode(Long rcode) {
		this.rcode = rcode;
	}

	public int getPcode() {
		return pcode.intValue();
	}

	public void setPcode(Long pcode) {
		this.pcode = pcode;
	}

	public int getDcode() {
		return dcode.intValue();
	}

	public void setDcode(Long dcode) {
		this.dcode = dcode;
	}

	public Long getCcode() {
		return ccode;//Integer.parseInt(ccode+"");
	}

	public void setCcode(Long ccode) {
		this.ccode = ccode;
	}
	
	
	
	
}
