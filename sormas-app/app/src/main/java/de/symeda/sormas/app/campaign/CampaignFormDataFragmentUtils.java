/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.campaign;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.component.controls.ControlCheckBoxField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.component.controls.ControlTextReadField;

import static de.symeda.sormas.api.campaign.ExpressionProcessorUtils.refreshEvaluationContext;

public class CampaignFormDataFragmentUtils {

    private CampaignFormDataFragmentUtils() {
    }

    public static void handleExpression(ExpressionParser expressionParser, List<CampaignFormDataEntry> formValues, CampaignFormElementType type, ControlPropertyField dynamicField, String expressionString) {
        try {
            final Object expressionValue = getExpressionValue(expressionParser, formValues, expressionString);
            if (type == CampaignFormElementType.YES_NO) {
                ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, (Boolean) expressionValue);
            } else {
                ControlTextEditField.setValue((ControlTextEditField) dynamicField, expressionValue.toString());
            }
            dynamicField.setEnabled(false);
        } catch (SpelEvaluationException e) {
            Log.e("Error evaluating expression depending on field" + dynamicField.getCaption(), e.getMessage());
        }

    }

    public static Object getExpressionValue(ExpressionParser expressionParser, List<CampaignFormDataEntry> formValues, String expressionString) throws SpelEvaluationException {
        final EvaluationContext context = refreshEvaluationContext(formValues);
        final Expression expression = expressionParser.parseExpression(expressionString);
        final Class<?> valueType = expression.getValueType(context);
        return expression.getValue(context, valueType);
    }

    public static void handleDependingOn(Map<String, ControlPropertyField> fieldMap, CampaignFormElement campaignFormElement, ControlPropertyField dynamicField) {
        final String dependingOn = campaignFormElement.getDependingOn();
        final String[] dependingOnValues = campaignFormElement.getDependingOnValues();
        if (dependingOn != null && dependingOnValues != null) {
            ControlPropertyField controlPropertyField = fieldMap.get(dependingOn);
            setVisibilityDependency(dynamicField, dependingOnValues, controlPropertyField.getValue());
            final ControlPropertyField finalDynamicField = dynamicField;
            controlPropertyField.addValueChangedListener(field -> setVisibilityDependency(finalDynamicField, dependingOnValues, field.getValue()));
        }
    }


    public static void setVisibilityDependency(ControlPropertyField field, String[] dependingOnValues, Object dependingOnFieldValue) {
        String parsedDependingOnFieldValue = dependingOnFieldValue == null ? "" : dependingOnFieldValue instanceof Boolean ? YesNoUnknown.valueOf(((Boolean) dependingOnFieldValue).booleanValue()).name() : dependingOnFieldValue.toString();
        if (!containsIgnoreCase(Arrays.asList(dependingOnValues), parsedDependingOnFieldValue)) {
            field.setVisibility(View.INVISIBLE);
        } else {
            field.setVisibility(View.VISIBLE);
        }
    }

    private static boolean containsIgnoreCase(List<String> list, String soughtFor) {
        for (String current : list) {
            if (current.equalsIgnoreCase(soughtFor)) {
                return true;
            }
        }
        return false;
    }

    public static CampaignFormDataEntry getOrCreateCampaignFormDataEntry(List<CampaignFormDataEntry> formValues, CampaignFormElement campaignFormElement) {
        for (CampaignFormDataEntry campaignFormDataEntry : formValues) {
            if (campaignFormDataEntry.getId().equals(campaignFormElement.getId())) {
                return campaignFormDataEntry;
            }
        }
        final CampaignFormDataEntry newCampaignFomDataEntry = new CampaignFormDataEntry(campaignFormElement.getId(), null);
        formValues.add(newCampaignFomDataEntry);
        return newCampaignFomDataEntry;
    }

    public static ControlTextEditField createControlTextEditField(CampaignFormElement campaignFormElement, Context context) {
        return new ControlTextEditField(context) {
            @Override
            protected String getPrefixDescription() {
                return campaignFormElement.getCaption();
            }

            @Override
            protected String getPrefixCaption() {
                return campaignFormElement.getCaption();
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return EntityDto.COLUMN_LENGTH_DEFAULT;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                initInput();
            }
        };
    }

    public static ControlCheckBoxField createControlCheckBoxField(CampaignFormElement campaignFormElement, Context context) {
        return new ControlCheckBoxField(context) {
            @Override
            protected String getPrefixDescription() {
                return campaignFormElement.getCaption();
            }

            @Override
            protected String getPrefixCaption() {
                return campaignFormElement.getCaption();
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                initInput();
            }
        };
    }


    public static ControlTextReadField createControlTextReadField(CampaignFormElement campaignFormElement, Context context) {
        return new ControlTextReadField(context) {
            @Override
            protected String getPrefixDescription() {
                return campaignFormElement.getCaption();
            }

            @Override
            protected String getPrefixCaption() {
                return campaignFormElement.getCaption();
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initTextView();
            }
        };
    }

}
