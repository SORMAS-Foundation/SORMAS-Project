package de.symeda.sormas.api.campaign.data;

import de.symeda.sormas.api.EntityDto;

public class CampaignAggregateDataDto extends EntityDto { 

	private static final long serialVersionUID = -8087195060395038093L;

	private String formUuid;
	private String formId;
	private String formField;
	private String formCaption;
	private String area;
	private String region;
	private String district;
	private Long sumValue;
	
	
	
	
	public CampaignAggregateDataDto(String formUuid, String formId, String formField, String formCaption, String area,
			String region, String district, Long sumValue) {
		super();
		this.formUuid = formUuid;
		this.formId = formId;
		this.formField = formField;
		this.formCaption = formCaption;
		this.area = area;
		this.region = region;
		this.district = district;
		this.sumValue = sumValue;
	}
	
	public String getFormUuid() {
		return formUuid;
	}
	public void setFormUuid(String formUuid) {
		this.formUuid = formUuid;
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public String getFormField() {
		return formField;
	}
	public void setFormField(String formField) {
		this.formField = formField;
	}
	public String getFormCaption() {
		return formCaption;
	}
	public void setFormCaption(String formCaption) {
		this.formCaption = formCaption;
	}
	public Long getSumValue() {
		return sumValue;
	}
	public void setSumValue(Long sumValue) {
		this.sumValue = sumValue;
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

	

}
