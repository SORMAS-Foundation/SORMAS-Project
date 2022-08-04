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

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import de.symeda.sormas.api.MapperUtil;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementOptions;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.i18n.I18nProperties;
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

    private Map<String, String> optionsValues;

    private String caption_1 = "";
    private String caption_2 = "";
    private String caption_3 = "";
    private String caption_4 = "";
    private String caption_5 = "";
    private String caption_6 = "";
    private String caption_7 = "";
    private String caption_8 = "";

   // private List<CampaignFormTranslations> translationsOpt;
    private Map<String, String> userOptTranslations = null;

    public static CampaignFormDataReadFragment newInstance(CampaignFormData activityRootData) {
        return newInstance(
                CampaignFormDataReadFragment.class, null, activityRootData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);



        final CampaignFormMeta campaignFormMeta = DatabaseHelper.getCampaignFormMetaDao().queryForId(record.getCampaignFormMeta().getId());
        final List<CampaignFormDataEntry> formValues = record.getFormValues();

        final List<CampaignFormTranslations> translationsOpt = record.getCampaignFormMeta().getCampaignFormTranslations();

        final Map<String, String> formValuesMap = new HashMap<>();
        formValues.forEach(campaignFormDataEntry -> formValuesMap.put(campaignFormDataEntry.getId(), DataHelper.toStringNullable(campaignFormDataEntry.getValue())));
        final Map<String, ControlPropertyField> fieldMap = new HashMap<>();

        boolean daywise = false;

        int dayy = 0;
        for (CampaignFormElement campaignFormElement : campaignFormMeta.getCampaignFormElements()) {
            CampaignFormElementType type = CampaignFormElementType.fromString(campaignFormElement.getType());

            if (type == CampaignFormElementType.DAYWISE) {
                dayy++;
                switch(dayy) {
                    case 1:
                        caption_1 = campaignFormElement.getCaption();
                        break;
                    case 2:
                        caption_2 = campaignFormElement.getCaption();
                        break;
                    case 3:
                        caption_3 = campaignFormElement.getCaption();
                        break;
                    case 4:
                        caption_4 = campaignFormElement.getCaption();
                        break;
                    case 5:
                        caption_5 = campaignFormElement.getCaption();
                        break;
                    case 6:
                        caption_6 = campaignFormElement.getCaption();
                        break;
                    case 7:
                        caption_7 = campaignFormElement.getCaption();
                        break;
                    case 8:
                        caption_8 = campaignFormElement.getCaption();
                        break;
                }
                daywise = true;
            }

        }

        int accrd_count = 0;

        Resources res = getResources();
        TabHost mTabHost = (TabHost) view.findViewById(R.id.tabhostxxxRd);
        mTabHost.setup();

        TabHost.TabSpec spec;

        int countr = 0;

        for (CampaignFormElement campaignFormElement : campaignFormMeta.getCampaignFormElements()) {
            CampaignFormElementType type = CampaignFormElementType.fromString(campaignFormElement.getType());

            if (campaignFormElement.getOptions() != null) {
                final Locale locale = I18nProperties.getUserLanguage().getLocale();

                if (locale != null) {
                    translationsOpt.stream().filter(t -> t.getLanguageCode().equals(locale.toString()))
                            .findFirst().ifPresent(filteredTranslations -> filteredTranslations.getTranslations().stream()
                            .filter(cd -> cd.getOptions() != null)
                            .findFirst().ifPresent(optionsList -> userOptTranslations = optionsList.getOptions().stream()
                                    .filter(c -> c.getCaption() != null).collect(Collectors.toMap(MapperUtil::getKey, MapperUtil::getCaption))));
                }


                CampaignFormElementOptions campaignFormElementOptions = new CampaignFormElementOptions();
                optionsValues = campaignFormElement.getOptions().stream().collect(Collectors.toMap(MapperUtil::getKey, MapperUtil::getCaption));  // .collect(Collectors.toList());

                System.out.println("_______________________ " + userOptTranslations);
                if (userOptTranslations == null) {
                    campaignFormElementOptions.setOptionsListValues(optionsValues);
                    //get18nOptCaption(formElement.getId(), optionsValues));
                } else {
                    campaignFormElementOptions.setOptionsListValues(userOptTranslations);

                }
            } else {
                optionsValues =  new HashMap<String, String>();
            }

            if (daywise) {
                if (type == CampaignFormElementType.DAYWISE) {
                    countr++;
                } else if (countr == 1) {
                    final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet1);
                    if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                        String value = formValuesMap.get(campaignFormElement.getId());
                        ControlPropertyField dynamicField = createControlTextReadField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                        dynamicField.setShowCaption(true);


                        if (value != null) {

                            if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
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
                } else if (countr == 2) {
                    final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet2);
                    if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                        String value = formValuesMap.get(campaignFormElement.getId());
                        ControlPropertyField dynamicField = createControlTextReadField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                        dynamicField.setShowCaption(true);


                        if (value != null) {

                            if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
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
                } else if (countr == 3) {
                    final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet3);
                    if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                        String value = formValuesMap.get(campaignFormElement.getId());
                        ControlPropertyField dynamicField = createControlTextReadField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                        dynamicField.setShowCaption(true);


                        if (value != null) {

                            if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
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
                } else if (countr == 4) {
                    final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet4);
                    if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                        String value = formValuesMap.get(campaignFormElement.getId());
                        ControlPropertyField dynamicField = createControlTextReadField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                        dynamicField.setShowCaption(true);


                        if (value != null) {

                            if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
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
                } else if (countr == 5) {
                    final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet5);
                    if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                        String value = formValuesMap.get(campaignFormElement.getId());
                        ControlPropertyField dynamicField = createControlTextReadField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                        dynamicField.setShowCaption(true);


                        if (value != null) {

                            if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
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
                } else if (countr == 6) {
                    final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet6);
                    if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                        String value = formValuesMap.get(campaignFormElement.getId());
                        ControlPropertyField dynamicField = createControlTextReadField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                        dynamicField.setShowCaption(true);


                        if (value != null) {

                            if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
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
                } else if (countr == 7) {
                    final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet7);
                    if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                        String value = formValuesMap.get(campaignFormElement.getId());
                        ControlPropertyField dynamicField = createControlTextReadField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                        dynamicField.setShowCaption(true);


                        if (value != null) {

                            if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
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
                } else if (countr == 8) {
                    final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet8);
                    if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                        String value = formValuesMap.get(campaignFormElement.getId());
                        ControlPropertyField dynamicField = createControlTextReadField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                        dynamicField.setShowCaption(true);


                        if (value != null) {

                            if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
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
            }else {
                final LinearLayout dynamicLayout = view.findViewById(R.id.dynamicLayoutxXRd);
                if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                    String value = formValuesMap.get(campaignFormElement.getId());
                    ControlPropertyField dynamicField = createControlTextReadField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                    dynamicField.setShowCaption(true);


                    if (value != null) {

                        if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
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
        }

        if (daywise) {

            if (dayy > 0) {
                spec = mTabHost.newTabSpec("tab1").setIndicator("D1",//caption_1,
                        res.getDrawable(R.drawable.ic_clear_black_24dp))
                        .setContent(R.id.tabSheet1);
                mTabHost.addTab(spec);
                mTabHost.getTabWidget().getChildAt(0).getLayoutParams().width = 140;
            }
            if (dayy > 1) {
                spec = mTabHost.newTabSpec("tab2").setIndicator("D2",//caption_1,
                        res.getDrawable(R.drawable.ic_clear_black_24dp))
                        .setContent(R.id.tabSheet2);
                mTabHost.addTab(spec);
                mTabHost.getTabWidget().getChildAt(1).getLayoutParams().width = 140;
            }
            if (dayy > 2) {
                spec = mTabHost.newTabSpec("tab3").setIndicator("D3",//caption_1,
                        res.getDrawable(R.drawable.ic_clear_black_24dp))
                        .setContent(R.id.tabSheet3);
                mTabHost.addTab(spec);
                mTabHost.getTabWidget().getChildAt(2).getLayoutParams().width = 140;
            }
            if (dayy > 3) {
                spec = mTabHost.newTabSpec("tab4").setIndicator("D4",//caption_1,
                        res.getDrawable(R.drawable.ic_clear_black_24dp))
                        .setContent(R.id.tabSheet4);
                mTabHost.addTab(spec);
                mTabHost.getTabWidget().getChildAt(3).getLayoutParams().width = 140;
            }
          /*  if (dayy > 4) {
                spec = mTabHost.newTabSpec("tab5").setIndicator("D5",//caption_1,
                        res.getDrawable(R.drawable.ic_clear_black_24dp))
                        .setContent(R.id.tabSheet5);
                mTabHost.addTab(spec);
                mTabHost.getTabWidget().getChildAt(4).getLayoutParams().width = 140;
            }
            if (dayy > 5) {
                spec = mTabHost.newTabSpec("tab6").setIndicator("D6",//caption_1,
                        res.getDrawable(R.drawable.ic_clear_black_24dp))
                        .setContent(R.id.tabSheet6);
                mTabHost.addTab(spec);
                mTabHost.getTabWidget().getChildAt(5).getLayoutParams().width = 140;
            }
            if (dayy > 6) {
                spec = mTabHost.newTabSpec("tab7").setIndicator("D7",//caption_1,
                        res.getDrawable(R.drawable.ic_clear_black_24dp))
                        .setContent(R.id.tabSheet7);
                mTabHost.addTab(spec);
                mTabHost.getTabWidget().getChildAt(6).getLayoutParams().width = 140;
            }
            if (dayy > 7) {
                spec = mTabHost.newTabSpec("tab8").setIndicator("D8",//caption_1,
                        res.getDrawable(R.drawable.ic_clear_black_24dp))
                        .setContent(R.id.tabSheet8);
                mTabHost.addTab(spec);
                mTabHost.getTabWidget().getChildAt(7).getLayoutParams().width = 140;
            }

*/

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
