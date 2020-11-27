package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;

public class CampaignDiagramDataDto implements Serializable {

	private static final long serialVersionUID = -8813972727008846360L;

	private String formMetaUuid;
	private String formId;
	private String fieldId;
	private String fieldCaption;
	private Number valueSum;
	private Object groupingKey;
	private String groupingCaption;
	private String stack;
	private boolean hasAgeGroupData;

	public CampaignDiagramDataDto(String fieldCaption, Number valueSum, Object groupingKey, String groupingCaption, String fieldId, String formId) {
		this.fieldCaption = fieldCaption;
		this.valueSum = valueSum;
		this.groupingKey = groupingKey;
		this.groupingCaption = groupingCaption;
		this.fieldId = fieldId;
		this.formId = formId;
	}

	public CampaignDiagramDataDto(
		String fieldCaption,
		Number valueSum,
		Object groupingKey,
		String groupingCaption,
		String fieldId,
		String formId,
		boolean hasAgeGroupData,
		String stack) {
		this.fieldCaption = fieldCaption;
		this.valueSum = valueSum;
		this.groupingKey = groupingKey;
		this.groupingCaption = groupingCaption;
		this.fieldId = fieldId;
		this.formId = formId;
		this.hasAgeGroupData = hasAgeGroupData;
		this.stack = stack;
	}

	public CampaignDiagramDataDto(
		String fieldCaption,
		Number valueSum,
		Object groupingKey,
		String groupingCaption,
		String fieldId,
		String formId,
		boolean hasAgeGroupData) {
		this.fieldCaption = fieldCaption;
		this.valueSum = valueSum;
		this.groupingKey = groupingKey;
		this.groupingCaption = groupingCaption;
		this.fieldId = fieldId;
		this.formId = formId;
		this.hasAgeGroupData = hasAgeGroupData;

	}

	public CampaignDiagramDataDto(
		String formMetaUuid,
		String formId,
		String fieldId,
		String fieldCaption,
		Number valueSum,
		Object groupingKey,
		String groupingCaption,
		String stack) {
		this.formMetaUuid = formMetaUuid;
		this.formId = formId;
		this.fieldId = fieldId;
		this.fieldCaption = fieldCaption;
		this.valueSum = valueSum;
		this.groupingKey = groupingKey;
		this.groupingCaption = groupingCaption;
		this.stack = stack;
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

	public Number getValueSum() {
		return valueSum;
	}

	public void setValueSum(Number valueSum) {
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

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

	public boolean getHasAgeGroupData() {
		return hasAgeGroupData;
	}

	public void setHasAgeGroupData(boolean hasAgeGroupData) {
		this.hasAgeGroupData = hasAgeGroupData;
	}
}
