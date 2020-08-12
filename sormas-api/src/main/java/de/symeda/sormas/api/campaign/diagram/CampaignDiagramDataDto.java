package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;

public class CampaignDiagramDataDto implements Serializable {

	private static final long serialVersionUID = -8813972727008846360L;

	private String formId;
	private String fieldId;
	private Object valueSum;
	private Object groupingKey;
	private String groupingCaption;

	public CampaignDiagramDataDto(String formId, String fieldId, Object valueSum, Object groupingKey, String groupingCaption) {
		this.formId = formId;
		this.fieldId = fieldId;
		this.valueSum = valueSum;
		this.groupingKey = groupingKey;
		this.groupingCaption = groupingCaption;

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
