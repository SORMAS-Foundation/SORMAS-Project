package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;

public class CampaignDiagramDataDto implements Serializable {

	private static final long serialVersionUID = -8813972727008846360L;

	private String formMetaUuid;
	private String formId;
	private String fieldId;
	private String fieldCaption;
	private Object valueSum;
	private Object groupingKey;
	private String groupingCaption;

	public CampaignDiagramDataDto(String formMetaUuid, String formId, String fieldId, Object valueSum, Object groupingKey, String groupingCaption) {
		this.formMetaUuid = formMetaUuid;
		this.formId = formId;
		this.fieldId = fieldId;
		this.valueSum = valueSum;
		this.groupingKey = groupingKey;
		this.groupingCaption = groupingCaption;

	}

	public String getFormMetaUuid() {
		return formMetaUuid;
	}

	public void setFormMetaUuid(String formMetaUuid) {
		this.formMetaUuid = formMetaUuid;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getFieldCaption() {
		return fieldCaption;
	}

	public void setFieldCaption(String fieldCaption) {
		this.fieldCaption = fieldCaption;
	}

	public Object getValueSum() {
		return valueSum;
	}

	public void setValueSum(Object valueSum) {
		this.valueSum = valueSum;
	}

	public Object getGroupingKey() {
		return groupingKey;
	}

	public void setGroupingKey(Object groupingKey) {
		this.groupingKey = groupingKey;
	}

	public String getGroupingCaption() {
		return groupingCaption;
	}

	public void setGroupingCaption(String groupingCaption) {
		this.groupingCaption = groupingCaption;
	}
}
