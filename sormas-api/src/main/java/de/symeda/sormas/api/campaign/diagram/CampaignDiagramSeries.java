package de.symeda.sormas.api.campaign.diagram;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.i18n.Validations;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CampaignDiagramSeries implements Serializable {

	private static final long serialVersionUID = 1420672609912364060L;

	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String fieldId;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String formId;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String referenceValue;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String stack;
	private AgeGroup populationGroup;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String caption;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
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
