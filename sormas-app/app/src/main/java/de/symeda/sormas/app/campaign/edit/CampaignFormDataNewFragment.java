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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.databinding.FragmentCampaignDataNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.TextViewBindingAdapters;

import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.createControlCheckBoxField;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.createControlTextEditField;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.getOrCreateCampaignFormDataEntry;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.getUserLanguageCaption;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.getUserTranslations;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.handleDependingOn;
import static de.symeda.sormas.app.campaign.CampaignFormDataFragmentUtils.handleExpression;

public class CampaignFormDataNewFragment extends BaseEditFragment<FragmentCampaignDataNewLayoutBinding, CampaignFormData, CampaignFormData> {

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private CampaignFormData record;
    private List<Item> initialCampaigns;
    private List<Item> initialAreas;
    private List<Item> initialRegions;
    private List<Item> initialDistricts;
    private List<Item> initialCommunities;

    public static CampaignFormDataNewFragment newInstance(CampaignFormData activityRootData) {
        return newInstance(CampaignFormDataNewFragment.class, null, activityRootData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        final LinearLayout dynamicLayout = view.findViewById(R.id.dynamicLayout);

        final CampaignFormMeta campaignFormMeta = DatabaseHelper.getCampaignFormMetaDao().queryForId(record.getCampaignFormMeta().getId());
        record.setFormValues(new ArrayList<>());

        final List<CampaignFormDataEntry> formValues = record.getFormValues();
        final Map<String, ControlPropertyField> fieldMap = new HashMap<>();
        final Map<CampaignFormElement, ControlPropertyField> expressionMap = new HashMap<>();

        for (CampaignFormElement campaignFormElement : campaignFormMeta.getCampaignFormElements()) {
            CampaignFormElementType type = CampaignFormElementType.fromString(campaignFormElement.getType());

            if (type != CampaignFormElementType.SECTION && type != CampaignFormElementType.LABEL) {
                ControlPropertyField dynamicField;
                if (type == CampaignFormElementType.YES_NO) {
                    dynamicField = createControlCheckBoxField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
                } else {
                    dynamicField = createControlTextEditField(campaignFormElement, requireContext(), getUserTranslations(campaignFormMeta));
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
                formValues.add(new CampaignFormDataEntry(campaignFormElement.getId(), null));

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
        return view;
    }


    @Override
    public int getEditLayout() {
        return R.layout.fragment_campaign_data_new_layout;
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
        initialDistricts = InfrastructureDaoHelper.loadAllDistricts();
        initialCommunities = InfrastructureDaoHelper.loadAllCommunities();
    }

    @Override
    protected void onLayoutBinding(FragmentCampaignDataNewLayoutBinding contentBinding) {
        contentBinding.setData(record);

        Item campaignItem = record.getCampaign() != null ? DataUtils.toItem(record.getCampaign()) : null;

        if (campaignItem != null && !initialCampaigns.contains(campaignItem)) {
            initialCampaigns.add(campaignItem);
        }

        contentBinding.campaignFormDataCampaign.initializeSpinner(initialCampaigns, record.getCampaign());

        contentBinding.campaignFormDataFormDate.initializeDateField(getFragmentManager());

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
    protected void onAfterLayoutBinding(FragmentCampaignDataNewLayoutBinding contentBinding) {
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
