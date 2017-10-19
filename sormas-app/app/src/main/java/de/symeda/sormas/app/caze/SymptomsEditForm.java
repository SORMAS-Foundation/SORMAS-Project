package de.symeda.sormas.app.caze;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.SymptomStateField;
import de.symeda.sormas.app.databinding.CaseSymptomsFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;
import de.symeda.sormas.app.validation.SymptomsValidator;


/**
 * Use this tab with arguments:
 * symptomsUuid as string
 * disease as serialized enum
 */
public class SymptomsEditForm extends FormTab {

    public static final String NEW_SYMPTOMS = "newSymptoms";
    public static final String FOR_VISIT = "forVisit";
    public static final String VISIT_COOPERATIVE = "visitCooperative";

    private CaseSymptomsFragmentLayoutBinding binding;
    private List<SymptomStateField> nonConditionalSymptoms;
    private List<SymptomStateField> conditionalBleedingSymptoms;

    private boolean forVisit;
    private boolean visitCooperative;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_symptoms_fragment_layout, container, false);
        View view = binding.getRoot();

        Symptoms symptoms;

        final Disease disease = (Disease) getArguments().getSerializable(Case.DISEASE);

        // build a new visit from contact data
        if(getArguments().getBoolean(NEW_SYMPTOMS)) {
            symptoms = DatabaseHelper.getSymptomsDao().build();
        }
        // open the given visit
        else {
            String symptomsUuid = getArguments().getString(Symptoms.UUID);
            symptoms = DatabaseHelper.getSymptomsDao().queryUuid(symptomsUuid);
        }

        if (getArguments().getBoolean(FOR_VISIT)) {
            forVisit = true;
            if (getArguments().getBoolean(VISIT_COOPERATIVE)) {
                visitCooperative = true;
            }
        }

        binding.setSymptoms(symptoms);

        binding.symptomsOnsetDate.initialize(this);

        List<Item> temperature = new ArrayList<>();
        temperature.add(new Item("",null));
        for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
            temperature.add(new Item(SymptomsHelper.getTemperatureString(temperatureValue),temperatureValue));
        }

        FieldHelper.initSpinnerField(binding.symptomsTemperature, temperature);
        binding.symptomsTemperature.setSelectionOnOpen(37.0f);

        FieldHelper.initSpinnerField(binding.symptomsTemperatureSource, TemperatureSource.class);

        binding.symptomsUnexplainedBleeding.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                toggleUnexplainedBleedingFields();
                if (forVisit) {
                    SymptomsValidator.setRequiredHintsForVisitSymptoms(visitCooperative, binding);
                } else {
                    SymptomsValidator.setRequiredHintsForCaseSymptoms(binding);
                }
            }
        });
        binding.symptomsOtherHemorrhagicSymptoms.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                visibilityOtherHemorrhagicSymptoms();
                if (forVisit) {
                    SymptomsValidator.setRequiredHintsForVisitSymptoms(visitCooperative, binding);
                } else {
                    SymptomsValidator.setRequiredHintsForCaseSymptoms(binding);
                }
            }
        });
        binding.symptomsOtherNonHemorrhagicSymptoms.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                visibilityOtherNonHemorrhagicSymptoms();
                if (forVisit) {
                    SymptomsValidator.setRequiredHintsForVisitSymptoms(visitCooperative, binding);
                } else {
                    SymptomsValidator.setRequiredHintsForCaseSymptoms(binding);
                }
            }
        });

        // set initial UI
        toggleUnexplainedBleedingFields();
        visibilityOtherHemorrhagicSymptoms();
        visibilityOtherNonHemorrhagicSymptoms();
        setVisibilityByDisease(SymptomsDto.class, disease, (ViewGroup)binding.getRoot());

        nonConditionalSymptoms = Arrays.asList(binding.symptomsFever, binding.symptomsVomiting,
                binding.symptomsDiarrhea, binding.symptomsBloodInStool1, binding.symptomsNausea, binding.symptomsAbdominalPain,
                binding.symptomsHeadache, binding.symptomsMusclePain, binding.symptomsFatigueWeakness, binding.symptomsUnexplainedBleeding,
                binding.symptomsSkinRash, binding.symptomsNeckStiffness, binding.symptomsSoreThroat, binding.symptomsCough,
                binding.symptomsRunnyNose, binding.symptomsDifficultyBreathing, binding.symptomsChestPain, binding.symptomsConfusedDisoriented,
                binding.symptomsSeizures, binding.symptomsAlteredConsciousness, binding.symptomsConjunctivitis,
                binding.symptomsEyePainLightSensitive, binding.symptomsKopliksSpots1, binding.symptomsThrobocytopenia,
                binding.symptomsOtitisMedia, binding.symptomsHearingloss, binding.symptomsDehydration, binding.symptomsAnorexiaAppetiteLoss,
                binding.symptomsRefusalFeedorDrink, binding.symptomsJointPain, binding.symptomsShock,
                binding.symptomsHiccups, binding.symptomsBackache, binding.symptomsEyesBleeding, binding.symptomsJaundice,
                binding.symptomsDarkUrine, binding.symptomsStomachBleeding, binding.symptomsRapidBreathing, binding.symptomsSwollenGlands,
                binding.symptomsOtherNonHemorrhagicSymptoms);

        conditionalBleedingSymptoms = Arrays.asList(binding.symptomsGumsBleeding1, binding.symptomsInjectionSiteBleeding,
                binding.symptomsNoseBleeding1, binding.symptomsBloodyBlackStool, binding.symptomsRedBloodVomit,
                binding.symptomsDigestedBloodVomit, binding.symptomsCoughingBlood, binding.symptomsBleedingVagina,
                binding.symptomsSkinBruising1, binding.symptomsBloodUrine, binding.symptomsOtherHemorrhagicSymptoms);

        FieldHelper.initSpinnerField(binding.symptomsOnsetSymptom1, DataUtils.toItems(null, true));
        addListenerForOnsetSymptom();

        Button clearAllBtn = binding.symptomsClearAll;
        clearAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SymptomStateField symptom : nonConditionalSymptoms) {
                    symptom.setValue(null);
                }
                for (SymptomStateField symptom : conditionalBleedingSymptoms) {
                    symptom.setValue(null);
                }

                if (forVisit) {
                    SymptomsValidator.setRequiredHintsForVisitSymptoms(visitCooperative, binding);
                } else {
                    SymptomsValidator.setRequiredHintsForCaseSymptoms(binding);
                }
            }
        });

        Button setAllToNoBtn = binding.symptomsSetEmptyToNo;
        setAllToNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SymptomStateField symptom : nonConditionalSymptoms) {
                    if (symptom.getVisibility() == View.VISIBLE && symptom.getValue() == null) {
                        symptom.setValue(SymptomState.NO);
                    }
                }
                for (SymptomStateField symptom : conditionalBleedingSymptoms) {
                    if (symptom.getVisibility() == View.VISIBLE && symptom.getValue() == null) {
                        symptom.setValue(SymptomState.NO);
                    }
                }

                if (forVisit) {
                    SymptomsValidator.setRequiredHintsForVisitSymptoms(visitCooperative, binding);
                } else {
                    SymptomsValidator.setRequiredHintsForCaseSymptoms(binding);
                }
            }
        });

        if (!forVisit) {
            SymptomsValidator.setRequiredHintsForCaseSymptoms(binding);
        } else {
            SymptomsValidator.setRequiredHintsForVisitSymptoms(visitCooperative, binding);
        }

        // Add listeners to symptom state fields; OnWindowFocusChangeListener is used to make sure that these
        // listeners aren't called when the view is being built.
        binding.caseSymptomsForm.getViewTreeObserver().addOnWindowFocusChangeListener(
                new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(boolean b) {
                binding.caseSymptomsForm.getViewTreeObserver().removeOnWindowFocusChangeListener(this);
                for (SymptomStateField symptom : nonConditionalSymptoms) {
                    symptom.addValueChangedListener(new PropertyField.ValueChangeListener() {
                        @Override
                        public void onChange(PropertyField field) {
                            if (forVisit) {
                                SymptomsValidator.setRequiredHintsForVisitSymptoms(visitCooperative, binding);
                            } else {
                                SymptomsValidator.setRequiredHintsForCaseSymptoms(binding);
                            }
                        }
                    });
                }
            }
        });

        //view.requestFocus();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // @TODO: Workaround, find a better solution. Remove autofocus on first field.
        getView().requestFocus();
    }

    private void visibilityOtherHemorrhagicSymptoms() {
        SymptomState symptomState = binding.symptomsOtherHemorrhagicSymptoms.getValue();
        binding.symptomsOtherHemorrhagicSymptomsLayout.setVisibility(symptomState == SymptomState.YES?View.VISIBLE:View.GONE);
        if(symptomState != SymptomState.YES) {
            binding.symptomsOther1HemorrhagicSymptomsText.setValue("");
        }
    }

    private void visibilityOtherNonHemorrhagicSymptoms() {
        SymptomState symptomState = binding.symptomsOtherNonHemorrhagicSymptoms.getValue();
        binding.symptomsOtherNonHemorrhagicSymptomsLayout.setVisibility(symptomState == SymptomState.YES?View.VISIBLE:View.GONE);
        if(symptomState != SymptomState.YES) {
            binding.symptomsOther1NonHemorrhagicSymptomsText.setValue("");
        }
    }

    private void toggleUnexplainedBleedingFields() {
        int[] fieldIds = {
                R.id.symptoms_gumsBleeding1,
                R.id.symptoms_injectionSiteBleeding,
                R.id.symptoms_noseBleeding1,
                R.id.symptoms_bloodyBlackStool,
                R.id.symptoms_redBloodVomit,
                R.id.symptoms_digestedBloodVomit,
                R.id.symptoms_coughingBlood,
                R.id.symptoms_bleedingVagina,
                R.id.symptoms_skinBruising1,
                R.id.symptoms_bloodUrine,
                R.id.symptoms_otherHemorrhagicSymptoms
        };

        SymptomState symptomState = binding.symptomsUnexplainedBleeding.getValue();
        for (int fieldId:fieldIds) {
            if(symptomState == SymptomState.YES) {
                setFieldVisible(binding.getRoot().findViewById(fieldId), true);
            }
            else {
                View view = binding.getRoot().findViewById(fieldId);
                // reset value
                ((SymptomStateField)view).setValue(null);
                setFieldGone(view);
            }
        }
    }

    private void addListenerForOnsetSymptom() {
        final ArrayAdapter<Item> adapter = (ArrayAdapter<Item>) binding.symptomsOnsetSymptom1.getAdapter();

        for (SymptomStateField symptom : nonConditionalSymptoms) {
            symptom.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    Item item = new Item(field.getCaption(), field.getCaption());
                    int position = binding.symptomsOnsetSymptom1.getPositionOf(item);
                    if (field.getValue() == SymptomState.YES) {
                        if (position == -1) {
                            adapter.add(item);
                        }
                    } else {
                        if (position != -1) {
                            adapter.remove(adapter.getItem(position));
                        }
                    }
                }
            });
        }

        for (SymptomStateField symptom : conditionalBleedingSymptoms) {
            symptom.addValueChangedListener(new PropertyField.ValueChangeListener() {
                @Override
                public void onChange(PropertyField field) {
                    Item item = new Item(field.getCaption(), field.getCaption());
                    int position = binding.symptomsOnsetSymptom1.getPositionOf(item);
                    if (field.getValue() == SymptomState.YES) {
                        if (position == -1) {
                            adapter.add(item);
                        }
                    } else {
                        if (position != -1) {
                            adapter.remove(adapter.getItem(position));
                        }
                    }
                }
            });
        }
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getSymptoms();
    }

    public CaseSymptomsFragmentLayoutBinding getBinding() {
        return binding;
    }

    public void changeVisitCooperative(boolean cooperative) {
        visitCooperative = cooperative;
        SymptomsValidator.setRequiredHintsForVisitSymptoms(visitCooperative, binding);
    }

}