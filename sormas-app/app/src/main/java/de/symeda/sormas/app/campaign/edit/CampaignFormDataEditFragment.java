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

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlCheckBoxField;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.databinding.FragmentCampaignDataEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.TextViewBindingAdapters;

import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.createControlCheckBoxField;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.createControlSpinnerFieldEditField;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.createControlDateEditField;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.createControlTextEditField;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.getOrCreateCampaignFormDataEntry;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.getUserLanguageCaption;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.getUserTranslations;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.handleDependingOn;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.handleExpression;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CampaignFormDataEditFragment extends BaseEditFragment<FragmentCampaignDataEditLayoutBinding, CampaignFormData, CampaignFormData> {

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private CampaignFormData record;
    private List<Item> initialCampaigns;
    private List<Item> initialAreas;
    private List<Item> initialRegions;
    private List<Item> initialDistricts;
    private List<Item> initialCommunities;
    private Map<String, String> optionsValues = null;

    private String caption_1 = "";
    private String caption_2 = "";
    private String caption_3 = "";
    private String caption_4 = "";
    private String caption_5 = "";
    private String caption_6 = "";
    private String caption_7 = "";
    private String caption_8 = "";

    //private List<CampaignFormTranslations> translationsOpt;
    private Map<String, String> userOptTranslations = null;


    private SimpleDateFormat dateFormat;

    public static BaseEditFragment newInstance(CampaignFormData activityRootData) {
        return newInstance(CampaignFormDataEditFragment.class, null, activityRootData);
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
        final Map<CampaignFormElement, ControlPropertyField> expressionMap = new HashMap<>();

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
      TabHost mTabHost = (TabHost) view.findViewById(R.id.tabhostxxxxXEd);
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

                System.out.println("_______________________ "+userOptTranslations);
                if(userOptTranslations == null) {
                    campaignFormElementOptions.setOptionsListValues(optionsValues);
                    //get18nOptCaption(formElement.getId(), optionsValues));
                }else {
                    campaignFormElementOptions.setOptionsListValues(userOptTranslations);

                }





              //  CampaignFormElementOptions campaignFormElementOptions = new CampaignFormElementOptions();
                //optionsValues = (List) Arrays.stream(campaignFormElement.getOptions()).collect(Collectors.toList());
              //  optionsValues = campaignFormElement.getOptions().stream().collect(Collectors.toMap(MapperUtil::getKey, MapperUtil::getCaption));  // .collect(Collectors.toList());

               // ListIterator<String> lstItems = optionsValues.listIterator();
            //    int i = 1;
          //      campaignFormElementOptions.setOptionsListValues(optionsValues);
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

                      ControlPropertyField dynamicField;
                      if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
                          dynamicField = createControlCheckBoxField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                          ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                      } else if (type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE) {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DROPDOWN) {
                          dynamicField = createControlSpinnerFieldEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), optionsValues);
                          ControlSpinnerField.setValue((ControlSpinnerField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DATE) {
                          dynamicField = createControlDateEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, this.getFragmentManager(), campaignFormElement.isImportant());
                          ControlDateField.setValue((ControlDateField) dynamicField, getDateValue(value));
                      } else {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), false, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      }

                      fieldMap.put(campaignFormElement.getId(), dynamicField);
                      dynamicField.setShowCaption(true);
                      dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                      dynamicField.addValueChangedListener(field -> {
                          final CampaignFormDataEntry campaignFormDataEntry = getOrCreateCampaignFormDataEntry(formValues, campaignFormElement);
                          campaignFormDataEntry.setValue(field.getValue());
                          if (campaignFormElement.getExpression() == null && fieldMap.get(campaignFormElement.getId()) != null) {
                              expressionMap.forEach((formElement, controlPropertyField) ->
                                      handleExpression(expressionParser, formValues, CampaignFormElementType.fromString(formElement.getType()), controlPropertyField, formElement.getExpression()));
                          }
                      });

                      handleDependingOn(fieldMap, campaignFormElement, dynamicField);

                      final String expressionString = campaignFormElement.getExpression();
                      if (expressionString != null) {
                          handleExpression(expressionParser, formValues, type, dynamicField, expressionString);
                          expressionMap.put(campaignFormElement, dynamicField);
                      }
                  } else if (type == CampaignFormElementType.SECTION) {

                      dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
                  } else if (type == CampaignFormElementType.LABEL) {
                      TextView textView = new TextView(requireContext());
                      TextViewBindingAdapters.setHtmlValue(textView, getUserLanguageCaption(getUserTranslations(campaignFormMeta), campaignFormElement));
                      dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                  }
              }  else if (countr == 2) {
                  final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet2);
                  if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                      String value = formValuesMap.get(campaignFormElement.getId());

                      ControlPropertyField dynamicField;
                      if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
                          dynamicField = createControlCheckBoxField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                          ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                      } else if (type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE) {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DROPDOWN) {
                          dynamicField = createControlSpinnerFieldEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), optionsValues);
                          ControlSpinnerField.setValue((ControlSpinnerField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DATE) {
                          dynamicField = createControlDateEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, this.getFragmentManager(), campaignFormElement.isImportant());
                          ControlDateField.setValue((ControlDateField) dynamicField, getDateValue(value));
                      } else {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), false, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      }

                      fieldMap.put(campaignFormElement.getId(), dynamicField);
                      dynamicField.setShowCaption(true);
                      dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                      dynamicField.addValueChangedListener(field -> {
                          final CampaignFormDataEntry campaignFormDataEntry = getOrCreateCampaignFormDataEntry(formValues, campaignFormElement);
                          campaignFormDataEntry.setValue(field.getValue());
                          if (campaignFormElement.getExpression() == null && fieldMap.get(campaignFormElement.getId()) != null) {
                              expressionMap.forEach((formElement, controlPropertyField) ->
                                      handleExpression(expressionParser, formValues, CampaignFormElementType.fromString(formElement.getType()), controlPropertyField, formElement.getExpression()));
                          }
                      });

                      handleDependingOn(fieldMap, campaignFormElement, dynamicField);

                      final String expressionString = campaignFormElement.getExpression();
                      if (expressionString != null) {
                          handleExpression(expressionParser, formValues, type, dynamicField, expressionString);
                          expressionMap.put(campaignFormElement, dynamicField);
                      }
                  } else if (type == CampaignFormElementType.SECTION) {

                      dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
                  } else if (type == CampaignFormElementType.LABEL) {
                      TextView textView = new TextView(requireContext());
                      TextViewBindingAdapters.setHtmlValue(textView, getUserLanguageCaption(getUserTranslations(campaignFormMeta), campaignFormElement));
                      dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                  }
              }  else if (countr == 3) {
              final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet3);
                  if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                      String value = formValuesMap.get(campaignFormElement.getId());

                      ControlPropertyField dynamicField;
                      if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
                          dynamicField = createControlCheckBoxField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                          ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                      } else if (type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE) {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DROPDOWN) {
                          dynamicField = createControlSpinnerFieldEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), optionsValues);
                          ControlSpinnerField.setValue((ControlSpinnerField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DATE) {
                          dynamicField = createControlDateEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, this.getFragmentManager(), campaignFormElement.isImportant());
                          ControlDateField.setValue((ControlDateField) dynamicField, getDateValue(value));
                      } else {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), false, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      }

                      fieldMap.put(campaignFormElement.getId(), dynamicField);
                      dynamicField.setShowCaption(true);
                      dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                      dynamicField.addValueChangedListener(field -> {
                          final CampaignFormDataEntry campaignFormDataEntry = getOrCreateCampaignFormDataEntry(formValues, campaignFormElement);
                          campaignFormDataEntry.setValue(field.getValue());
                          if (campaignFormElement.getExpression() == null && fieldMap.get(campaignFormElement.getId()) != null) {
                              expressionMap.forEach((formElement, controlPropertyField) ->
                                      handleExpression(expressionParser, formValues, CampaignFormElementType.fromString(formElement.getType()), controlPropertyField, formElement.getExpression()));
                          }
                      });

                      handleDependingOn(fieldMap, campaignFormElement, dynamicField);

                      final String expressionString = campaignFormElement.getExpression();
                      if (expressionString != null) {
                          handleExpression(expressionParser, formValues, type, dynamicField, expressionString);
                          expressionMap.put(campaignFormElement, dynamicField);
                      }
                  } else if (type == CampaignFormElementType.SECTION) {

                      dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
                  } else if (type == CampaignFormElementType.LABEL) {
                      TextView textView = new TextView(requireContext());
                      TextViewBindingAdapters.setHtmlValue(textView, getUserLanguageCaption(getUserTranslations(campaignFormMeta), campaignFormElement));
                      dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                  }
          }  else if (countr == 4) {
              final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet4);
                  if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                      String value = formValuesMap.get(campaignFormElement.getId());

                      ControlPropertyField dynamicField;
                      if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
                          dynamicField = createControlCheckBoxField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                          ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                      } else if (type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE) {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DROPDOWN) {
                          dynamicField = createControlSpinnerFieldEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), optionsValues);
                          ControlSpinnerField.setValue((ControlSpinnerField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DATE) {
                          dynamicField = createControlDateEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, this.getFragmentManager(), campaignFormElement.isImportant());
                          ControlDateField.setValue((ControlDateField) dynamicField, getDateValue(value));
                      } else {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), false, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      }

                      fieldMap.put(campaignFormElement.getId(), dynamicField);
                      dynamicField.setShowCaption(true);
                      dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                      dynamicField.addValueChangedListener(field -> {
                          final CampaignFormDataEntry campaignFormDataEntry = getOrCreateCampaignFormDataEntry(formValues, campaignFormElement);
                          campaignFormDataEntry.setValue(field.getValue());
                          if (campaignFormElement.getExpression() == null && fieldMap.get(campaignFormElement.getId()) != null) {
                              expressionMap.forEach((formElement, controlPropertyField) ->
                                      handleExpression(expressionParser, formValues, CampaignFormElementType.fromString(formElement.getType()), controlPropertyField, formElement.getExpression()));
                          }
                      });

                      handleDependingOn(fieldMap, campaignFormElement, dynamicField);

                      final String expressionString = campaignFormElement.getExpression();
                      if (expressionString != null) {
                          handleExpression(expressionParser, formValues, type, dynamicField, expressionString);
                          expressionMap.put(campaignFormElement, dynamicField);
                      }
                  } else if (type == CampaignFormElementType.SECTION) {

                      dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
                  } else if (type == CampaignFormElementType.LABEL) {
                      TextView textView = new TextView(requireContext());
                      TextViewBindingAdapters.setHtmlValue(textView, getUserLanguageCaption(getUserTranslations(campaignFormMeta), campaignFormElement));
                      dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                  }
          }  else if (countr == 5) {
              final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet5);
                  if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                      String value = formValuesMap.get(campaignFormElement.getId());

                      ControlPropertyField dynamicField;
                      if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
                          dynamicField = createControlCheckBoxField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                          ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                      } else if (type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE) {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DROPDOWN) {
                          dynamicField = createControlSpinnerFieldEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), optionsValues);
                          ControlSpinnerField.setValue((ControlSpinnerField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DATE) {
                          dynamicField = createControlDateEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, this.getFragmentManager(), campaignFormElement.isImportant());
                          ControlDateField.setValue((ControlDateField) dynamicField, getDateValue(value));
                      } else {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), false, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      }

                      fieldMap.put(campaignFormElement.getId(), dynamicField);
                      dynamicField.setShowCaption(true);
                      dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                      dynamicField.addValueChangedListener(field -> {
                          final CampaignFormDataEntry campaignFormDataEntry = getOrCreateCampaignFormDataEntry(formValues, campaignFormElement);
                          campaignFormDataEntry.setValue(field.getValue());
                          if (campaignFormElement.getExpression() == null && fieldMap.get(campaignFormElement.getId()) != null) {
                              expressionMap.forEach((formElement, controlPropertyField) ->
                                      handleExpression(expressionParser, formValues, CampaignFormElementType.fromString(formElement.getType()), controlPropertyField, formElement.getExpression()));
                          }
                      });

                      handleDependingOn(fieldMap, campaignFormElement, dynamicField);

                      final String expressionString = campaignFormElement.getExpression();
                      if (expressionString != null) {
                          handleExpression(expressionParser, formValues, type, dynamicField, expressionString);
                          expressionMap.put(campaignFormElement, dynamicField);
                      }
                  } else if (type == CampaignFormElementType.SECTION) {

                      dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
                  } else if (type == CampaignFormElementType.LABEL) {
                      TextView textView = new TextView(requireContext());
                      TextViewBindingAdapters.setHtmlValue(textView, getUserLanguageCaption(getUserTranslations(campaignFormMeta), campaignFormElement));
                      dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                  }
          }  else if (countr == 6) {
              final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet6);
                  if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                      String value = formValuesMap.get(campaignFormElement.getId());

                      ControlPropertyField dynamicField;
                      if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
                          dynamicField = createControlCheckBoxField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                          ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                      } else if (type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE) {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DROPDOWN) {
                          dynamicField = createControlSpinnerFieldEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), optionsValues);
                          ControlSpinnerField.setValue((ControlSpinnerField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DATE) {
                          dynamicField = createControlDateEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, this.getFragmentManager(), campaignFormElement.isImportant());
                          ControlDateField.setValue((ControlDateField) dynamicField, getDateValue(value));
                      } else {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), false, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      }

                      fieldMap.put(campaignFormElement.getId(), dynamicField);
                      dynamicField.setShowCaption(true);
                      dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                      dynamicField.addValueChangedListener(field -> {
                          final CampaignFormDataEntry campaignFormDataEntry = getOrCreateCampaignFormDataEntry(formValues, campaignFormElement);
                          campaignFormDataEntry.setValue(field.getValue());
                          if (campaignFormElement.getExpression() == null && fieldMap.get(campaignFormElement.getId()) != null) {
                              expressionMap.forEach((formElement, controlPropertyField) ->
                                      handleExpression(expressionParser, formValues, CampaignFormElementType.fromString(formElement.getType()), controlPropertyField, formElement.getExpression()));
                          }
                      });

                      handleDependingOn(fieldMap, campaignFormElement, dynamicField);

                      final String expressionString = campaignFormElement.getExpression();
                      if (expressionString != null) {
                          handleExpression(expressionParser, formValues, type, dynamicField, expressionString);
                          expressionMap.put(campaignFormElement, dynamicField);
                      }
                  } else if (type == CampaignFormElementType.SECTION) {

                      dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
                  } else if (type == CampaignFormElementType.LABEL) {
                      TextView textView = new TextView(requireContext());
                      TextViewBindingAdapters.setHtmlValue(textView, getUserLanguageCaption(getUserTranslations(campaignFormMeta), campaignFormElement));
                      dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                  }
          }  else if (countr == 7) {
              final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet7);
                  if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                      String value = formValuesMap.get(campaignFormElement.getId());

                      ControlPropertyField dynamicField;
                      if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
                          dynamicField = createControlCheckBoxField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                          ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                      } else if (type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE) {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DROPDOWN) {
                          dynamicField = createControlSpinnerFieldEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), optionsValues);
                          ControlSpinnerField.setValue((ControlSpinnerField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DATE) {
                          dynamicField = createControlDateEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, this.getFragmentManager(), campaignFormElement.isImportant());
                          ControlDateField.setValue((ControlDateField) dynamicField, getDateValue(value));
                     } else {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), false, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      }

                      fieldMap.put(campaignFormElement.getId(), dynamicField);
                      dynamicField.setShowCaption(true);
                      dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                      dynamicField.addValueChangedListener(field -> {
                          final CampaignFormDataEntry campaignFormDataEntry = getOrCreateCampaignFormDataEntry(formValues, campaignFormElement);
                          campaignFormDataEntry.setValue(field.getValue());
                          if (campaignFormElement.getExpression() == null && fieldMap.get(campaignFormElement.getId()) != null) {
                              expressionMap.forEach((formElement, controlPropertyField) ->
                                      handleExpression(expressionParser, formValues, CampaignFormElementType.fromString(formElement.getType()), controlPropertyField, formElement.getExpression()));
                          }
                      });

                      handleDependingOn(fieldMap, campaignFormElement, dynamicField);

                      final String expressionString = campaignFormElement.getExpression();
                      if (expressionString != null) {
                          handleExpression(expressionParser, formValues, type, dynamicField, expressionString);
                          expressionMap.put(campaignFormElement, dynamicField);
                      }
                  } else if (type == CampaignFormElementType.SECTION) {

                      dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
                  } else if (type == CampaignFormElementType.LABEL) {
                      TextView textView = new TextView(requireContext());
                      TextViewBindingAdapters.setHtmlValue(textView, getUserLanguageCaption(getUserTranslations(campaignFormMeta), campaignFormElement));
                      dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                  }
          }  else if (countr == 8) {
              final LinearLayout dynamicLayout = mTabHost.findViewById(R.id.tabSheet8);
                  if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                      String value = formValuesMap.get(campaignFormElement.getId());

                      ControlPropertyField dynamicField;
                      if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
                          dynamicField = createControlCheckBoxField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                          ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                      } else if (type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE) {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DROPDOWN) {
                          dynamicField = createControlSpinnerFieldEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), optionsValues);
                          ControlSpinnerField.setValue((ControlSpinnerField) dynamicField, value);
                      } else if (type == CampaignFormElementType.DATE) {
                          dynamicField = createControlDateEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, this.getFragmentManager(), campaignFormElement.isImportant());
                          ControlDateField.setValue((ControlDateField) dynamicField, getDateValue(value));
                      } else {
                          dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), false, campaignFormElement.isImportant());
                          ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                      }

                      fieldMap.put(campaignFormElement.getId(), dynamicField);
                      dynamicField.setShowCaption(true);
                      dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                      dynamicField.addValueChangedListener(field -> {
                          final CampaignFormDataEntry campaignFormDataEntry = getOrCreateCampaignFormDataEntry(formValues, campaignFormElement);
                          campaignFormDataEntry.setValue(field.getValue());
                          if (campaignFormElement.getExpression() == null && fieldMap.get(campaignFormElement.getId()) != null) {
                              expressionMap.forEach((formElement, controlPropertyField) ->
                                      handleExpression(expressionParser, formValues, CampaignFormElementType.fromString(formElement.getType()), controlPropertyField, formElement.getExpression()));
                          }
                      });

                      handleDependingOn(fieldMap, campaignFormElement, dynamicField);

                      final String expressionString = campaignFormElement.getExpression();
                      if (expressionString != null) {
                          handleExpression(expressionParser, formValues, type, dynamicField, expressionString);
                          expressionMap.put(campaignFormElement, dynamicField);
                      }
                  } else if (type == CampaignFormElementType.SECTION) {

                      dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
                  } else if (type == CampaignFormElementType.LABEL) {
                      TextView textView = new TextView(requireContext());
                      TextViewBindingAdapters.setHtmlValue(textView, getUserLanguageCaption(getUserTranslations(campaignFormMeta), campaignFormElement));
                      dynamicLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                  }
          }
          }else{
              final LinearLayout dynamicLayout = view.findViewById(R.id.dynamicLayoutxXEd);
              if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                  String value = formValuesMap.get(campaignFormElement.getId());

                  ControlPropertyField dynamicField;
                  if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX || type == CampaignFormElementType.RADIO || type == CampaignFormElementType.CHECKBOXBASIC || type == CampaignFormElementType.RADIOBASIC) {
                      dynamicField = createControlCheckBoxField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                      ControlCheckBoxField.setValue((ControlCheckBoxField) dynamicField, Boolean.valueOf(value));
                  } else if (type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE) {
                      dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, campaignFormElement.isImportant());
                      ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                  } else if (type == CampaignFormElementType.DROPDOWN) {
                      dynamicField = createControlSpinnerFieldEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), optionsValues);
                      ControlSpinnerField.setValue((ControlSpinnerField) dynamicField, value);
                  } else if (type == CampaignFormElementType.DATE) {
                      dynamicField = createControlDateEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), true, this.getFragmentManager(), campaignFormElement.isImportant());
                      ControlDateField.setValue((ControlDateField) dynamicField, getDateValue(value));
                  } else {
                      dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta), false, campaignFormElement.isImportant());
                      ControlTextEditField.setValue((ControlTextEditField) dynamicField, value);
                  }

                  fieldMap.put(campaignFormElement.getId(), dynamicField);
                  dynamicField.setShowCaption(true);
                  dynamicLayout.addView(dynamicField, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                  dynamicField.addValueChangedListener(field -> {
                      final CampaignFormDataEntry campaignFormDataEntry = getOrCreateCampaignFormDataEntry(formValues, campaignFormElement);
                      campaignFormDataEntry.setValue(field.getValue());
                      if (campaignFormElement.getExpression() == null && fieldMap.get(campaignFormElement.getId()) != null) {
                          expressionMap.forEach((formElement, controlPropertyField) ->
                                  handleExpression(expressionParser, formValues, CampaignFormElementType.fromString(formElement.getType()), controlPropertyField, formElement.getExpression()));
                      }
                  });

                  handleDependingOn(fieldMap, campaignFormElement, dynamicField);

                  final String expressionString = campaignFormElement.getExpression();
                  if (expressionString != null) {
                      handleExpression(expressionParser, formValues, type, dynamicField, expressionString);
                      expressionMap.put(campaignFormElement, dynamicField);
                  }
              } else if (type == CampaignFormElementType.SECTION) {

                  dynamicLayout.addView(new ImageView(requireContext(), null, R.style.FullHorizontalDividerStyle));
              } else if (type == CampaignFormElementType.LABEL) {
                  TextView textView = new TextView(requireContext());
                  TextViewBindingAdapters.setHtmlValue(textView, getUserLanguageCaption(getUserTranslations(campaignFormMeta), campaignFormElement));
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
      /*    if (dayy > 5) {
              spec = mTabHost.newTabSpec("tab5").setIndicator("D5",//caption_1,
                      res.getDrawable(R.drawable.ic_clear_black_24dp))
                      .setContent(R.id.tabSheet5);
              mTabHost.addTab(spec);
              mTabHost.getTabWidget().getChildAt(4).getLayoutParams().width = 140;
          }
          if (dayy > 6) {
              spec = mTabHost.newTabSpec("tab6").setIndicator("D6",//caption_1,
                      res.getDrawable(R.drawable.ic_clear_black_24dp))
                      .setContent(R.id.tabSheet6);
              mTabHost.addTab(spec);
              mTabHost.getTabWidget().getChildAt(5).getLayoutParams().width = 140;
          }
          if (dayy > 7) {
              spec = mTabHost.newTabSpec("tab7").setIndicator("D7",//caption_1,
                      res.getDrawable(R.drawable.ic_clear_black_24dp))
                      .setContent(R.id.tabSheet7);
              mTabHost.addTab(spec);
              mTabHost.getTabWidget().getChildAt(6).getLayoutParams().width = 140;
          }
          if (dayy > 8) {
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

    public long getMilliFromDate(String dateFormat) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a");

        try {
            System.out.println("'@@@@@@@@@@@@@"+formatter.parse(dateFormat));
            date = formatter.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Today is " + date);
        return date.getTime();
    }

    private Date getDateValue(String input) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }

       // getMilliFromDate(input);

        System.out.println("'@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@s"+getMilliFromDate(input));
        dateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a");

System.out.println(input);
        System.out.println(DateHelper.parseDate(input, dateFormat));
     //   Date date = DateHelper.parseDate(input, dateFormat);
        Calendar dateCalendar = Calendar.getInstance();
        Calendar cachedCalendar = Calendar.getInstance();

    //    System.err.println(date);
        Date date = new Date(getMilliFromDate(input));
        dateCalendar.setTime(date);

        cachedCalendar.set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR));
        cachedCalendar.set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH));
        cachedCalendar.set(Calendar.DAY_OF_MONTH, dateCalendar.get(Calendar.DAY_OF_MONTH));
        date = cachedCalendar.getTime();

        System.out.println(date);

        return date;
    }



    @Override
    public int getEditLayout() {
        return R.layout.fragment_campaign_data_edit_layout;
    }

    @Override
    public CampaignFormData getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();

        initialCampaigns = DataUtils.toItems(DatabaseHelper.getCampaignDao().queryActiveForAll());

        initialAreas = InfrastructureDaoHelper.loadAreas();
        initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
        initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getRegion());
        initialCommunities = InfrastructureDaoHelper.loadCommunities(record.getDistrict());
    }

    @Override
    protected void onLayoutBinding(FragmentCampaignDataEditLayoutBinding contentBinding) {
        record.setArea(record.getRegion().getArea());
        contentBinding.setData(record);

        Item campaignItem = record.getCampaign() != null ? DataUtils.toItem(record.getCampaign()) : null;

        if (campaignItem != null && !initialCampaigns.contains(campaignItem)) {
            initialCampaigns.add(campaignItem);
        }

        contentBinding.campaignFormDataCampaign.initializeSpinner(initialCampaigns, record.getCampaign());

        InfrastructureDaoHelper.initializeRegionAreaFields(
                contentBinding.campaignFormDataArea,
                initialAreas,
                record.getArea(),
                contentBinding.campaignFormDataRegion,
                initialRegions,
                record.getRegion(),
                contentBinding.campaignFormDataDistrict,
                initialDistricts,
                record.getDistrict(),
                contentBinding.campaignFormDataCommunity,
                initialCommunities,
                record.getCommunity());
    }

    @Override
    protected void onAfterLayoutBinding(FragmentCampaignDataEditLayoutBinding contentBinding) {
        super.onAfterLayoutBinding(contentBinding);

        contentBinding.campaignFormDataFormDate.initializeDateField(getFragmentManager());

        User user = ConfigProvider.getUser();

        if (user.getRegion() != null) {
            contentBinding.campaignFormDataArea.setEnabled(false);
            contentBinding.campaignFormDataRegion.setEnabled(false);
        }
        if (user.getDistrict() != null) {
            contentBinding.campaignFormDataDistrict.setEnabled(false);
        }
        if (user.getCommunity() != null) {
            contentBinding.campaignFormDataCommunity.setEnabled(false);
        }
    }
}
