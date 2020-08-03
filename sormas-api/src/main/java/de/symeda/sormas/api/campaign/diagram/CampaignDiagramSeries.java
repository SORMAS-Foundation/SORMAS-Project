package de.symeda.sormas.api.campaign.diagram;

public class CampaignDiagramSeries {

	private String fieldId;
	private String formId;
	private String fieldValue;
	private String stack;

	public CampaignDiagramSeries() {
	}

	public CampaignDiagramSeries(String fieldId, String formId, String fieldValue, String stack) {
		this.fieldId = fieldId;
		this.formId = formId;
		this.fieldValue = fieldValue;
		this.stack = stack;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}
}
