package de.symeda.sormas.api.campaign.form;

import java.util.List;

public class CampaignFormElementOptions {
	public static Integer max = null;
	public static Integer min = null;
	public static boolean expression;

	public static Integer getMax() {
		return max;
	}

	public static void setMax(Integer max) {
		CampaignFormElementOptions.max = max;
	}

	public static Integer getMin() {
		return min;
	}

	public static void setMin(Integer min) {
		CampaignFormElementOptions.min = min;
	}

	public static boolean isExpression() {
		return expression;
	}

	public static void setExpression(boolean expression) {
		CampaignFormElementOptions.expression = expression;
	}



	// List Methods constraints
	public static List<String> optionsListValues;

	public static List<String> getOptionsListValues() {
		return optionsListValues;
	}

	public void setOptionsListValues(List<String> optionsListValues) {
		this.optionsListValues = optionsListValues;
	}

	// List Methods constraints
	public static List<String> constraintsListValues;

	public static List<String> getConstraintsListValues() {
		return constraintsListValues;
	}

	public void setConstraintsListValues(List<String> constraintsListValues) {
		this.constraintsListValues = constraintsListValues;
	}

	@Override
	public String toString() {
		return "CampaignFormElementOptions [getOptionsListValues()=" + getOptionsListValues() + "]";
	}

	public List CampaignFormElementOptions() {
		return getOptionsListValues();
		// TODO Auto-generated constructor stub
	}

}
