package de.symeda.sormas.api.campaign.form;

import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.MapperUtil;

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
	public static Map<String, String> optionsListValues;

	

	public static Map<String, String> getOptionsListValues() {
		return optionsListValues;
	}

	public static void setOptionsListValues(Map<String, String> optionsListValues) {
		CampaignFormElementOptions.optionsListValues = optionsListValues;
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


}
