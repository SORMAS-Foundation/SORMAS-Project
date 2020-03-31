/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.contact.edit;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.caze.edit.CaseNewFragment;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentContactNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureHelper;
import de.symeda.sormas.app.visit.edit.VisitValidator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ContactNewFragment extends BaseEditFragment<FragmentContactNewLayoutBinding, Contact, Contact> {

    public static final String TAG = CaseNewFragment.class.getSimpleName();

    private Contact record;
    private Case sourceCase;

    private List<Item> relationshipList;
    private List<Item> initialRegions;
    private List<Item> initialDistricts;
    private List<Item> diseaseList;
    private List<Item> categoryList;

    public static ContactNewFragment newInstance(Contact activityRootData) {
        return newInstance(ContactNewFragment.class, null, activityRootData);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_new_contact);
    }

    @Override
    public Contact getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();
        if (record.getCaseUuid() != null) {
            sourceCase = DatabaseHelper.getCaseDao().queryUuidBasic(record.getCaseUuid());
        }
        relationshipList = DataUtils.getEnumItems(ContactRelation.class, true);
        initialRegions = InfrastructureHelper.loadRegions();
        initialDistricts = InfrastructureHelper.loadDistricts(record.getRegion());
        diseaseList = DataUtils.toItems(DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true));
        categoryList = DataUtils.getEnumItems(ContactCategory.class,true);
    }

    @Override
    public void onLayoutBinding(FragmentContactNewLayoutBinding contentBinding) {
       contentBinding.setData(record);

        InfrastructureHelper.initializeRegionFields(
                contentBinding.contactRegion, initialRegions, record.getRegion(),
                contentBinding.contactDistrict, initialDistricts, record.getDistrict(),
                null, null, null
        );

        contentBinding.contactDisease.initializeSpinner(diseaseList, DiseaseConfigurationCache.getInstance().getDefaultDisease());
        contentBinding.contactDisease.addValueChangedListener(e -> {
            contentBinding.contactContactProximity.setVisibility(e.getValue() == null ? GONE : VISIBLE);
            contentBinding.contactContactProximity.clear();
            contentBinding.contactContactProximity.setItems(DataUtils.toItems(Arrays.asList(ContactProximity.getValues((Disease) e.getValue(), ConfigProvider.getServerLocale()))));
        });

        String germanyLocale = "de";
        if (germanyLocale.equals(ConfigProvider.getServerLocale())){
            contentBinding.contactContactProximity.addValueChangedListener(e -> trySetContactProximityDetails(contentBinding, (ContactProximity) contentBinding.contactContactProximity.getValue()));
        } else {
            contentBinding.contactContactProximityDetails.setVisibility(GONE);
            contentBinding.contactContactCategory.setVisibility(GONE);
        }

        if (record.getCaseUuid() != null) {
            contentBinding.contactDisease.setVisibility(GONE);
            contentBinding.contactCaseIdExternalSystem.setVisibility(GONE);
            contentBinding.contactCaseOrEventInformation.setVisibility(GONE);
            contentBinding.contactContactProximity.setItems(DataUtils.toItems(Arrays.asList(ContactProximity.getValues(sourceCase.getDisease(), ConfigProvider.getServerLocale()))));
        } else {
            contentBinding.contactDisease.setRequired(true);
            contentBinding.contactRegion.setRequired(true);
            contentBinding.contactDistrict.setRequired(true);
        }

        if (getPrimaryData().getDisease() == null) {
            contentBinding.contactContactProximity.setVisibility(GONE);
        }

        ContactValidator.initializeValidation(record, contentBinding);
    }

    /*
     * Only used for Systems in Germany. Follows specific rules for german systems.
     */
    private void trySetContactProximityDetails(FragmentContactNewLayoutBinding contentBinding, ContactProximity proximity) {
        if (proximity != null) {
            switch (proximity) {
                case FACE_TO_FACE_LONG:
                case TOUCHED_FLUID:
                case AEROSOL:
                case MEDICAL_UNSAVE:
                    contentBinding.contactContactCategory.setValue(ContactCategory.HIGH_RISK);
                    break;
                case SAME_ROOM:
                case FACE_TO_FACE_SHORT:
                case MEDICAL_SAME_ROOM:
                    contentBinding.contactContactCategory.setValue(ContactCategory.LOW_RISK);
                    break;
                case MEDICAL_DISTANT:
                case MEDICAL_SAVE:
                    contentBinding.contactContactCategory.setValue(ContactCategory.NO_RISK);
                    break;
                default:
                    throw new IllegalArgumentException(proximity.toString());
            }
        }
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactNewLayoutBinding contentBinding) {
        contentBinding.contactRelationToCase.initializeSpinner(relationshipList);
        contentBinding.contactContactCategory.initializeSpinner(categoryList);
        contentBinding.contactLastContactDate.initializeDateField(getFragmentManager());
        contentBinding.contactReportDateTime.initializeDateField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_new_layout;
    }
}
