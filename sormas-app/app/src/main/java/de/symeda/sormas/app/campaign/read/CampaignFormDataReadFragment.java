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

package de.symeda.sormas.app.campaign.read;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlTextReadField;
import de.symeda.sormas.app.databinding.FragmentCampaignDataReadLayoutBinding;
import de.symeda.sormas.app.util.TextViewBindingAdapters;

import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.createControlTextReadField;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.getExpressionValue;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.getUserTranslations;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.setVisibilityDependency;

public class CampaignFormDataReadFragment extends BaseReadFragment<FragmentCampaignDataReadLayoutBinding, CampaignFormData, CampaignFormData> {

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private CampaignFormData record;

    public static CampaignFormDataReadFragment newInstance(CampaignFormData activityRootData) {
        return newInstance(
                CampaignFormDataReadFragment.class, null, activityRootData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        final LinearLayout dynamicLayout = view.findViewById(R.id.dynamicLayout);

        final CampaignFormMeta campaignFormMeta = DatabaseHelper.getCampaignFormMetaDao().queryForId(record.getCampaignFormMeta().getId());
        final List<CampaignFormDataEntry> formValues = record.getFormValues();
        final Map<String, String> formValuesMap = new HashMap<>();
        formValues.forEach(campaignFormDataEntry -> formValuesMap.put(campaignFormDataEntry.getId(), DataHelper.toStringNullable(campaignFormDataEntry.getValue())));
        final Map<String, ControlPropertyField> fieldMap = new HashMap<>();

        for (CampaignFormElement campaignFormElement : campaignFormMeta.getCampaignFormElements()) {
            CampaignFormElementType type = CampaignFormElementType.fromString(campaignFormElement.getType());

            if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                String value = formValuesMap.get(campaignFormElement.getId());
                ControlPropertyField dynamicField = createControlTextReadField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                dynamicField.setShowCaption(true);
                if (value != null) {
                    if (type == CampaignFormElementType.YES_NO) {
                        ControlTextReadField.setValue((ControlTextReadField) dynamicField, Boolean.valueOf(value), null, null);
                    } else {
                        ControlTextReadField.setValue((ControlTextReadField) dynamicField, value, null, null, null);
                    }
                }
                dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                fieldMap.put(campaignFormElement.getId(), dynamicField);
                final String dependingOn = campaignFormElement.getDependingOn();
                final String[] dependingOnValues = campaignFormElement.getDependingOnValues();
                if (dependingOn != null && dependingOnValues != null) {
                    ControlPropertyField controlPropertyField = fieldMap.get(dependingOn);
                    setVisibilityDependency(dynamicField, dependingOnValues, controlPropertyField.getValue());
                }
                final String expressionString = campaignFormElement.getExpression();
                if (expressionString != null) {
                    try {
                        final Object expressionValue = getExpressionValue(expressionParser, formValues, expressionString);
                        if (type == CampaignFormElementType.YES_NO) {
                            ControlTextReadField.setValue((ControlTextReadField) dynamicField, (Boolean) expressionValue, null, null);
                        } else {
                            ControlTextReadField.setValue((ControlTextReadField) dynamicField, expressionValue.toString(), null, null, null);
                        }
                    } catch (SpelEvaluationException e) {
                        Log.e("Error evaluating expression: " + expressionString, e.getMessage());
                    }
                }
            } else if (type == CampaignFormElementType.SECTION) {
                dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
            } else if (type == CampaignFormElementType.LABEL) {
                TextView textView = new TextView(requireContext());
                TextViewBindingAdapters.setHtmlValue(textView, campaignFormElement.getCaption());
                dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }
        return view;
    }


    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
    }

    @Override
    protected void onLayoutBinding(FragmentCampaignDataReadLayoutBinding contentBinding) {
        record.setArea(record.getRegion().getArea());
        contentBinding.setData(record);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_campaign_data_read_layout;
    }

    @Override
    public CampaignFormData getPrimaryData() {
        return record;
    }
}
