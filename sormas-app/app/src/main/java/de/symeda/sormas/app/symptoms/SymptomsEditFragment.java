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

package de.symeda.sormas.app.symptoms;

import android.content.res.Resources;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.symptoms.CongenitalHeartDiseaseType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.utils.DependantOn;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.clinicalcourse.edit.ClinicalVisitEditActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ControlSwitchField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentSymptomsEditLayoutBinding;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.DataUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SymptomsEditFragment extends BaseEditFragment<FragmentSymptomsEditLayoutBinding, Symptoms, AbstractDomainObject> {

    private Symptoms record;
    private Disease disease;
    private boolean isInfant;
    private AbstractDomainObject ado;
    private SymptomsContext symptomsContext;

    private List<Item> bodyTempList;
    private List<Item> tempSourceList;
    private List<Item> congenitalHeartDiseaseList;

    private IEntryItemOnClickListener clearAllCallback;
    private IEntryItemOnClickListener setClearedToNoCallback;

    private List<ControlSwitchField> symptomFields;

    public static SymptomsEditFragment newInstance(Case activityRootData) {
        return newInstanceWithFieldCheckers(SymptomsEditFragment.class, null, activityRootData,
                FieldVisibilityCheckers.withDisease(activityRootData.getDisease()), null);
    }

    public static SymptomsEditFragment newInstance(Visit activityRootData) {
        return newInstanceWithFieldCheckers(SymptomsEditFragment.class, null, activityRootData,
                FieldVisibilityCheckers.withDisease(activityRootData.getDisease()), null);
    }

    public static SymptomsEditFragment newInstance(ClinicalVisit activityRootData, String caseUuid) {
        return newInstanceWithFieldCheckers(SymptomsEditFragment.class, ClinicalVisitEditActivity.buildBundleWithCase(caseUuid).get(), activityRootData,
                FieldVisibilityCheckers.withDisease(activityRootData.getDisease()), null);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_symptoms);
    }

    @Override
    public Symptoms getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        ado = getActivityRootData();
        Person person;
        if (ado instanceof Case) {
            symptomsContext = SymptomsContext.CASE;
            record = ((Case) ado).getSymptoms();
            disease = ((Case) ado).getDisease();
            person = ((Case) ado).getPerson();
        } else if (ado instanceof Visit) {
            symptomsContext = SymptomsContext.VISIT;
            record = ((Visit) ado).getSymptoms();
            disease = ((Visit) ado).getDisease();
            person = ((Visit) ado).getPerson();
        } else if (ado instanceof ClinicalVisit) {
            symptomsContext = SymptomsContext.CLINICAL_VISIT;
            record = ((ClinicalVisit) ado).getSymptoms();
            disease = ((ClinicalVisit) ado).getDisease();
            person = DatabaseHelper.getCaseDao().queryUuidBasic(new Bundler(getArguments()).getCaseUuid()).getPerson();
        } else {
            throw new UnsupportedOperationException("ActivityRootData of class " + ado.getClass().getSimpleName()
                    + " does not support PersonReadFragment");
        }
        isInfant = person.getApproximateAge() != null
                && ((person.getApproximateAge() <= 12 && person.getApproximateAgeType() == ApproximateAgeType.MONTHS)
                || person.getApproximateAge() <= 1);
        bodyTempList = getTemperatures(true);
        tempSourceList = DataUtils.getEnumItems(TemperatureSource.class, true);
        congenitalHeartDiseaseList = DataUtils.getEnumItems(CongenitalHeartDiseaseType.class, true);
    }

    @Override
    public void onLayoutBinding(final FragmentSymptomsEditLayoutBinding contentBinding) {
        setupCallback();

        contentBinding.setData(record);
        contentBinding.setSymptomsContext(symptomsContext);
        contentBinding.setSymptomStateClass(SymptomState.class);
        contentBinding.setClearAllCallback(clearAllCallback);
        contentBinding.setSetClearedToNoCallback(setClearedToNoCallback);

        SymptomsValidator.initializeSymptomsValidation(contentBinding, ado);
    }

    @Override
    public void onAfterLayoutBinding(FragmentSymptomsEditLayoutBinding contentBinding) {

        if (SymptomsContext.VISIT.equals(symptomsContext)) {
            Visit visit = (Visit) getActivityRootData();
            boolean enabled = VisitStatus.COOPERATIVE.equals(visit.getVisitStatus());
            for (int i = 0; i < contentBinding.mainContent.getChildCount(); i++) {
                View child = contentBinding.mainContent.getChildAt(i);
                child.setEnabled(enabled);
            }
            contentBinding.symptomsTemperature.setEnabled(enabled);
            contentBinding.symptomsTemperatureSource.setEnabled(enabled);
            contentBinding.btnClearAll.setEnabled(enabled);
            contentBinding.btnClearedToNo.setEnabled(enabled);
        }

        setFieldVisibilitiesAndAccesses(SymptomsDto.class, contentBinding.mainContent);

        if (contentBinding.symptomsBulgingFontanelle.getVisibility() == VISIBLE
                && !isInfant) {
            contentBinding.symptomsBulgingFontanelle.setVisibility(GONE);
        }

        contentBinding.symptomsOnsetDate.initializeDateField(getFragmentManager());

        contentBinding.symptomsTemperature.initializeSpinner(DataUtils.addEmptyItem(bodyTempList));
        contentBinding.symptomsTemperatureSource.initializeSpinner(DataUtils.addEmptyItem(tempSourceList));
        contentBinding.symptomsCongenitalHeartDiseaseType.initializeSpinner(congenitalHeartDiseaseList);
        contentBinding.symptomsOnsetSymptom.initializeSpinner(DataUtils.toItems(null, true));

        contentBinding.symptomsTemperature.setSelectionOnOpen(37.0f);

        initSymptomFields(contentBinding);
        initOnsetSymptomField(contentBinding);

        // Remove the Complications heading for CRS; should be done automatically later
        if (disease == Disease.CONGENITAL_RUBELLA) {
            contentBinding.complicationsHeading.setVisibility(GONE);
        }

        contentBinding.symptomsCongenitalHeartDisease.addValueChangedListener(e -> {
            if (e.getValue() != SymptomState.YES) {
                contentBinding.symptomsCongenitalHeartDiseaseDetails.setVisibility(GONE);
            }
        });
    }

    private void initSymptomFields(FragmentSymptomsEditLayoutBinding contentBinding) {

        symptomFields = new ArrayList<>();
        for (String symptomPropertyId : SymptomsHelper.getSymptomPropertyIds()) {

            ControlSwitchField symptomField = (ControlSwitchField) findFieldByPropertyId(symptomPropertyId, contentBinding.mainContent);
            if (symptomField == null)
                continue;

            symptomFields.add(symptomField);

            // set this field as parent of it's children
            for (String childSymptomPropertyId : DependantOn.DependencyConfiguration.getChildren(SymptomsDto.class, symptomPropertyId)) {

                ControlPropertyField childField = findFieldByPropertyId(childSymptomPropertyId, contentBinding.mainContent);
                if (symptomField != null && childField != null
                        && childField.getVisibility() == VISIBLE) {
                    // only do this for fields that are visible (based on visibility by disease)
                    ControlPropertyField.setDependencyParentField(childField, symptomField, SymptomState.YES, null,null, null);
                }
            }
        }
    }

    private void initOnsetSymptomField(FragmentSymptomsEditLayoutBinding contentBinding) {

        final ControlSpinnerField onsetSymptomField = contentBinding.symptomsOnsetSymptom;
        final ControlDateField onsetDateField = contentBinding.symptomsOnsetDate;
        List<Item> initialSpinnerItems = new ArrayList<>();
        for (ControlSwitchField symptomField : symptomFields) {

            symptomField.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    Item item = new Item(field.getCaption(), field.getCaption());
                    int position = onsetSymptomField.getPositionOf(item);
                    if (SymptomState.YES.equals(field.getValue())) {
                        if (position == -1) {
                            onsetSymptomField.getAdapter().add(item);
                        }

                        onsetDateField.setEnabled(true);
                    } else {
                        if (position != -1) {
                            onsetSymptomField.getAdapter().remove(onsetSymptomField.getAdapter().getItem(position));
                        }

                        onsetDateField.setEnabled(isAnySymptomSetToYes());
                    }
                    onsetSymptomField.setEnabled(onsetSymptomField.getAdapter().getCount() > 1); // first is "empty item"
                }
            });

            if (SymptomState.YES.equals(symptomField.getValue())) {
                initialSpinnerItems.add(new Item(symptomField.getCaption(), symptomField.getCaption()));
            }
        }

        onsetSymptomField.initializeSpinner(DataUtils.addEmptyItem(initialSpinnerItems));
        onsetSymptomField.setEnabled(onsetSymptomField.getAdapter().getCount() > 1); // first is "empty item"
        onsetDateField.setEnabled(isAnySymptomSetToYes());
    }

    private boolean isAnySymptomSetToYes() {
        boolean anySymptomSetToYes = false;
        for (ControlSwitchField symptomField : symptomFields) {
            if (symptomField.getValue() == SymptomState.YES) {
                anySymptomSetToYes = true;
                break;
            }
        }

        return anySymptomSetToYes;
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_symptoms_edit_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowNewAction() {
        return false;
    }

    private void setupCallback() {

        clearAllCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                for (ControlSwitchField symptomField : symptomFields) {
                    symptomField.setValue(null);
                }
            }
        };

        setClearedToNoCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                for (ControlSwitchField symptomField : symptomFields) {
                    if (symptomField.getVisibility() == VISIBLE
                            && symptomField.getValue() == null) {
                        symptomField.setValue(SymptomState.NO);
                    }
                }
            }
        };

    }

    private List<Item> getTemperatures(boolean withNull) {
        List<Item> temperature = new ArrayList<>();

        if (withNull)
            temperature.add(new Item("", null));

        for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
            temperature.add(new Item(SymptomsHelper.getTemperatureString(temperatureValue), temperatureValue));
        }

        return temperature;
    }

}
