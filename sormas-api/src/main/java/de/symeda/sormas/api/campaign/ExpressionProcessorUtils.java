package de.symeda.sormas.api.campaign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;

public class ExpressionProcessorUtils {

	private ExpressionProcessorUtils() {
	}

	public static EvaluationContext refreshEvaluationContext(List<CampaignFormDataEntry> formValues) {
		EvaluationContext context = new StandardEvaluationContext(transformFormValueListToMap(formValues));
		context.getPropertyAccessors().add(new MapAccessor()); 
		return context;
	}

	private static Map<String, Object> transformFormValueListToMap(List<CampaignFormDataEntry> formValues) {

		final Map<String, Object> formValuesMap = new HashMap<>();
		for (CampaignFormDataEntry campaignFormDataEntry : formValues) {
			formValuesMap.put(campaignFormDataEntry.getId(), parseValue(campaignFormDataEntry.getValue()));
		}

		return formValuesMap;
	}

	private static Object parseValue(Object value) {
		if (value instanceof String) {
			try {
				return Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				try {
					return Double.parseDouble(value.toString());
				} catch (NumberFormatException e1) {
					return value;
				}
			}
		}
		return value;
	}
}
