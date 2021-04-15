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

package de.symeda.sormas.app.campaign.edit;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.controls.ControlCheckBoxField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.util.TextViewBindingAdapters;

public class CampaignFormDataEditUtils {

    private CampaignFormDataEditUtils(){
    }

    // todo : use this method for both edit and new - for new the campaign form meta needs to be chosen with a popup
    public static void createDynamicFields(View view, CampaignFormData record, Context context) {
        final LinearLayout dynamicLayout = view.findViewById(R.id.dynamicLayout);

        final CampaignFormMeta campaignFormMeta = DatabaseHelper.getCampaignFormMetaDao().queryForId(record.getCampaignFormMeta().getId());
        final List<CampaignFormDataEntry> formValues = record.getFormValues();
        final Map<String, String> formValuesMap = new HashMap<>();
        formValues.forEach(campaignFormDataEntry -> formValuesMap.put(campaignFormDataEntry.getId(), campaignFormDataEntry.getValue().toString()));

        for (CampaignFormElement campaignFormElement : campaignFormMeta.getCampaignFormElements()) {
            CampaignFormElementType type = CampaignFormElementType.fromString(campaignFormElement.getType());

            if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                String value = formValuesMap.get(campaignFormElement.getId());
                if (value != null) {
                    ControlPropertyField dynamicField = null;
                    if (type == CampaignFormElementType.YES_NO) {
                        dynamicField = createControlCheckBoxField(campaignFormElement, context);
                        ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                    } else {
                        dynamicField = createControlTextEditField(campaignFormElement, context);
                        ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                    }
                    dynamicField.setShowCaption(true);
                    dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    dynamicField.addValueChangedListener(field -> {
                        final CampaignFormDataEntry campaignFormDataEntry = getCampaignFormDataEntry(formValues, campaignFormElement);
                        campaignFormDataEntry.setValue(field.getValue());
                    });
                } else {
                    Log.e(CampaignFormDataEditUtils.class.getName(), "No form value for element id : " + campaignFormElement.getId());
                }
            } else if (type == CampaignFormElementType.SECTION) {
                dynamicLayout.addView(new ImageView(context, null, R.style.FullHorizontalDividerStyle));
            } else if (type == CampaignFormElementType.LABEL) {
                TextView textView = new TextView(context);
                TextViewBindingAdapters.setHtmlValue(textView, campaignFormElement.getCaption());
                dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    public static CampaignFormDataEntry getCampaignFormDataEntry(List<CampaignFormDataEntry> formValues, CampaignFormElement campaignFormElement) {
        for (CampaignFormDataEntry campaignFormDataEntry : formValues) {
            if (campaignFormDataEntry.getId().equals(campaignFormElement.getId())) {
                return campaignFormDataEntry;
            }
        }
        Log.e(CampaignFormDataEditUtils.class.getName(), "No form value for element id : " + campaignFormElement.getId());
        throw new RuntimeException("No form value for element id : " + campaignFormElement.getId());
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

}
