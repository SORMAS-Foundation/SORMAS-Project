package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.symeda.sormas.api.AgeGroup;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CampaignDiagramSeries implements Serializable {

	private static final long serialVersionUID = 1420672609912364060L;

	private String fieldId;
	private String formId;
	private String referenceValue;
	private String stack;
	private AgeGroup populationGroup;
	private String caption;
	private String color;

	public CampaignDiagramSeries() {
	}

	public CampaignDiagramSeries(String fieldId, String formId, String referenceValue, String stack) {
		this.fieldId = fieldId;
		this.formId = formId;
		this.referenceValue = referenceValue;
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

	public String getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(String referenceValue) {
		this.referenceValue = referenceValue;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * Needed. Otherwise hibernate will persist whenever loading,
	 * because hibernate types creates new instances that aren't equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CampaignDiagramSeries that = (CampaignDiagramSeries) o;
		return Objects.equals(fieldId, that.fieldId)
			&& Objects.equals(formId, that.formId)
			&& Objects.equals(referenceValue, that.referenceValue)
			&& Objects.equals(stack, that.stack)
			&& Objects.equals(caption, that.caption)
			&& Objects.equals(color, that.color)
			&& Objects.equals(populationGroup, that.populationGroup);
	}

	public AgeGroup getPopulationGroup() {
		return populationGroup;
	}

	public void setPopulationGroup(AgeGroup populationGroup) {
		this.populationGroup = populationGroup;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldId, formId, referenceValue, stack, caption, color, populationGroup);
	}
}
