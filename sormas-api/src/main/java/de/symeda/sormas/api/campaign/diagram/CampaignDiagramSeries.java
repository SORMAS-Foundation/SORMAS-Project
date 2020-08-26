package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;
import java.util.Objects;

public class CampaignDiagramSeries implements Serializable {

	private static final long serialVersionUID = 1420672609912364060L;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CampaignDiagramSeries that = (CampaignDiagramSeries) o;
		return Objects.equals(fieldId, that.fieldId) &&
				Objects.equals(formId, that.formId) &&
				Objects.equals(fieldValue, that.fieldValue) &&
				Objects.equals(stack, that.stack);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldId, formId, fieldValue, stack);
	}
}
