package de.symeda.sormas.app.symptoms;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.utils.DependantOn;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ControlSwitchField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentSymptomsEditLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SymptomsEditFragment extends BaseEditFragment<FragmentSymptomsEditLayoutBinding, Symptoms, AbstractDomainObject> {

    private Symptoms record;
    private Disease disease;
    private boolean isInfant;
    private AbstractDomainObject ado;

    private List<Item> bodyTempList;
    private List<Item> tempSourceList;

    private IEntryItemOnClickListener clearAllCallback;
    private IEntryItemOnClickListener setClearedToNoCallback;

    private List<ControlSwitchField> symptomFields;

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_symptom_information);
    }

    @Override
    public Symptoms getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        ado = getActivityRootData();
        Person person;
        if (ado instanceof Case) {
            record = ((Case) ado).getSymptoms();
            disease = ((Case) ado).getDisease();
            person = ((Case) ado).getPerson();
        } else if (ado instanceof Visit) {
            record = ((Visit) ado).getSymptoms();
            disease = ((Visit) ado).getDisease();
            person = ((Visit) ado).getPerson();
        } else {
            throw new UnsupportedOperationException("ActivityRootData of class " + ado.getClass().getSimpleName()
                    + " does not support PersonReadFragment");
        }
        isInfant = person.getApproximateAge() != null
                && ((person.getApproximateAge() <= 12 && person.getApproximateAgeType() == ApproximateAgeType.MONTHS)
                || person.getApproximateAge() <= 1);
        bodyTempList = getTemperatures(true);
        tempSourceList = DataUtils.getEnumItems(TemperatureSource.class, true);
    }

    @Override
    public void onLayoutBinding(FragmentSymptomsEditLayoutBinding contentBinding) {
        setupCallback();

        contentBinding.setData(record);
        contentBinding.setSymptomStateClass(SymptomState.class);
        contentBinding.setClearAllCallback(clearAllCallback);
        contentBinding.setSetClearedToNoCallback(setClearedToNoCallback);

        SymptomsValidator.initializeSymptomsValidation(contentBinding);

        if (ado instanceof Visit) {
            makeAllSymptomsRequired();
        }
    }

    @Override
    public void onAfterLayoutBinding(FragmentSymptomsEditLayoutBinding contentBinding) {

        setVisibilityByDisease(SymptomsDto.class, disease, contentBinding.mainContent);

        if (contentBinding.symptomsBulgingFontanelle.getVisibility() == VISIBLE
                && !isInfant) {
            contentBinding.symptomsBulgingFontanelle.setVisibility(GONE);
        }

        contentBinding.symptomsOnsetDate.initializeDateField(getFragmentManager());

        contentBinding.symptomsTemperature.initializeSpinner(DataUtils.addEmptyItem(bodyTempList));
        contentBinding.symptomsTemperatureSource.initializeSpinner(DataUtils.addEmptyItem(tempSourceList));
        contentBinding.symptomsOnsetSymptom.initializeSpinner(DataUtils.toItems(null, true));

        contentBinding.symptomsTemperature.setSelectionOnOpen(37.0f);

        initSymptomFields(contentBinding);
        initOnsetSymptomField(contentBinding);
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
                    ControlPropertyField.setDependencyParentField(childField, symptomField, SymptomState.YES, null);
                }
            }
        }
    }

    private void initOnsetSymptomField(FragmentSymptomsEditLayoutBinding contentBinding) {

        final ControlSpinnerField onsetSymptomField = contentBinding.symptomsOnsetSymptom;
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
                    } else {
                        if (position != -1) {
                            onsetSymptomField.getAdapter().remove(onsetSymptomField.getAdapter().getItem(position));
                        }
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
    public boolean isShowAddAction() {
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

    public static SymptomsEditFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(SymptomsEditFragment.class, capsule, activityRootData);
    }

    public static SymptomsEditFragment newInstance(VisitFormNavigationCapsule capsule, Visit activityRootData) {
        return newInstance(SymptomsEditFragment.class, capsule, activityRootData);
    }

    private void makeAllSymptomsRequired() {
        ViewGroup root = (ViewGroup) getContentBinding().getRoot();
        makeAllChildrenRequired(root);
    }

    private static void makeAllChildrenRequired(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlSwitchField) {
                ((ControlSwitchField) child).setRequired(true);
            } else if (child instanceof ViewGroup) {
                makeAllChildrenRequired((ViewGroup) child);
            }
        }
    }

}
