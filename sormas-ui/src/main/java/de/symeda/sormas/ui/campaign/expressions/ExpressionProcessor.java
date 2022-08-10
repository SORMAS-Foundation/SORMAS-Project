package de.symeda.sormas.ui.campaign.expressions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.validator.RegexpValidator;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignFormBuilder;

import static de.symeda.sormas.api.campaign.ExpressionProcessorUtils.refreshEvaluationContext;

public class ExpressionProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(ExpressionProcessor.class);

	private final CampaignFormBuilder campaignFormBuilder;
	private final ExpressionParser expressionParser = new SpelExpressionParser();

	public ExpressionProcessor(CampaignFormBuilder campaignFormBuilder) {
		this.campaignFormBuilder = campaignFormBuilder;
		checkExpression();
	}

	public void disableExpressionFieldsForEditing() {
		final Map<String, Field<?>> fields = campaignFormBuilder.getFields();
		campaignFormBuilder.getFormElements()
			.stream()
			.filter(formElement -> formElement.getExpression() != null)
			.filter(formElement -> fields.get(formElement.getId()) != null)
			.filter(formElement -> !formElement.getType().equals("range"))
			.forEach(formElement -> fields.get(formElement.getId()).setEnabled(false));
		
	}

	public void addExpressionListener() {
		final Map<String, Field<?>> fields = campaignFormBuilder.getFields();
		final List<CampaignFormElement> formElements = campaignFormBuilder.getFormElements();
		formElements.stream()
			//.filter(formElement -> formElement.getExpression() == null)
			.filter(formElement -> fields.get(formElement.getId()) != null)
			.forEach(formElement -> {
				fields.get(formElement.getId()).addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> checkExpression());
			});
	}

	public void configureExpressionFieldsWithTooltip() {
		final Map<String, Field<?>> fields = campaignFormBuilder.getFields();
		campaignFormBuilder.getFormElements()
			.stream()
			.filter(formElement -> formElement.getExpression() != null)
			.filter(formElement -> fields.get(formElement.getId()) != null)
			.filter(formElement -> fields.get(formElement.getId()) instanceof AbstractComponent)
			.forEach(this::buildTooltipDescription);
	}

	private void buildTooltipDescription(CampaignFormElement formElement) {
		final Set<String> fieldNamesInExpression = new HashSet<>();
		final String tooltip = formElement.getExpression();
		final Map<String, Field<?>> fields = campaignFormBuilder.getFields();
		final AbstractComponent field = (AbstractComponent) fields.get(formElement.getId());
		campaignFormBuilder.getFormElements().forEach(element -> {
			if (tooltip.contains(element.getId())) {
				fieldNamesInExpression.add(campaignFormBuilder.get18nCaption(element.getId(), element.getCaption()));
			}
		});
		field.setDescription(String.format("%s: %s", I18nProperties.getDescription(Descriptions.Campaign_calculatedBasedOn), StringUtils.join(fieldNamesInExpression, ", ")));
	}

	private void checkExpression() {
		System.out.println("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj" );
		EvaluationContext context = refreshEvaluationContext(campaignFormBuilder.getFormValues());
		final List<CampaignFormElement> formElements = campaignFormBuilder.getFormElements();
		formElements.stream().filter(element -> element.getExpression() != null).forEach(e -> {
			try {
				final Expression expression = expressionParser.parseExpression(e.getExpression());
				final Class<?> valueType = expression.getValueType(context);
				final Object value = expression.getValue(context, valueType); 
				//final Object valx = Precision.round((double) value, 3);
				final List <String> opt = null;
				
				System.out.println(value + "  +++ range? "+e.getType().toString().equals("range")+"++   "+ expression.getExpressionString() +"  +++++  "+expression.getValue(context));
				
				if(e.getType().toString().equals("range")) {
					
					if(value.toString().equals("0")) {

						campaignFormBuilder
						.setFieldValue(campaignFormBuilder.getFields().get(e.getId()), 
								CampaignFormElementType.fromString(e.getType()),
								"0.0",
								null);
						//return;
					} else {

						campaignFormBuilder
						.setFieldValue(campaignFormBuilder.getFields().get(e.getId()), 
								CampaignFormElementType.fromString(e.getType()),
								value.toString().endsWith(".0") ? value.toString().replace(".0", "") : value,
								null);
						//return;
					}
					
					
				
						
					} else if(valueType.isAssignableFrom(Double.class)) {
					System.out.println("yes double detected "+Double.isFinite((double) value) +" = "+ value);
				campaignFormBuilder
					.setFieldValue(campaignFormBuilder.getFields().get(e.getId()), 
							CampaignFormElementType.fromString(e.getType()),
							!Double.isFinite((double) value) ? 0 : value.toString().endsWith(".0") ? value.toString().replace(".0", "") : Precision.round((double) value, 2),
									null);
			//	return;
				} else if(valueType.isAssignableFrom(Boolean.class)) {
					campaignFormBuilder
					.setFieldValue(campaignFormBuilder.getFields().get(e.getId()), 
							CampaignFormElementType.fromString(e.getType()), value,
									null);
				//	return;
				//	
				} else {
					
					campaignFormBuilder
					.setFieldValue(campaignFormBuilder.getFields().get(e.getId()), 
							CampaignFormElementType.fromString(e.getType()), value,
									null);
					
				}
			} catch (SpelEvaluationException evaluationException) {
				//LOG.error("Error evaluating expression: {} / {}", evaluationException.getMessageCode(), evaluationException.getMessage());
			}
		});
	}

}

