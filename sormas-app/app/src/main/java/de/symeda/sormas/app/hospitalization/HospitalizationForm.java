package de.symeda.sormas.app.hospitalization;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.LabelField;
import de.symeda.sormas.app.component.ListField;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.CaseHospitalizationFragmentLayoutBinding;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.validation.PreviousHospitalizationValidator;

public class HospitalizationForm extends FormTab {

    public static final String KEY_CASE_UUID = "caseUuid";

    private CaseHospitalizationFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_hospitalization_fragment_layout, container, false);
        View view = binding.getRoot();
        editOrCreateUserRight = (UserRight) getArguments().get(EDIT_OR_CREATE_USER_RIGHT);

        final String caseUuid = getArguments().getString(HospitalizationForm.KEY_CASE_UUID);

        final Case caze = DatabaseHelper.getCaseDao().queryUuidBasic(caseUuid);
        if (caze.getHealthFacility() != null) {
            ((LabelField) view.findViewById(R.id.hospitalization_healthFacility)).setValue(caze.getHealthFacility().toString());
        }

        // lazy loading hospitalization and inner previousHospitalization
        final String hospitalizationUuid = getArguments().getString(Hospitalization.UUID);
        if (hospitalizationUuid != null) {
            final Hospitalization hospitalization = DatabaseHelper.getHospitalizationDao().queryUuid(hospitalizationUuid);
            binding.setHospitalization(hospitalization);

        } else {
            // TODO: check if it ok this way
            binding.setHospitalization(new Hospitalization());
        }

        binding.hospitalizationPreviousHospitalizations.initialize(
                new PreviousHospitalizationsListArrayAdapter(
                        this.getActivity(),
                        R.layout.previous_hospitalizations_list_item),
                new Consumer() {
                    @Override
                    public void accept(Object prevHosp) {
                        editPreviousHospitalization((PreviousHospitalization)prevHosp);
                    }
                }
        );

        binding.hospitalizationAdmissionDate.initialize(this);
        binding.hospitalizationDischargeDate.initialize(this);
        binding.hospitalizationIsolationDate.initialize(this);

        binding.hospitalizationHospitalizedPreviously.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.hospitalizationPreviousHospitalizations.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                updatePrevHospHint();
            }
        });

        binding.hospitalizationIsolated.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.hospitalizationIsolationDate.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
            }
        });

        binding.hospitalizationAdmittedToHealthFacility.addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                binding.hospitalizationIsolated.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                binding.hospitalizationDischargeDate.setVisibility(field.getValue() == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
            }
        });

        return view;
    }

    private void editPreviousHospitalization(PreviousHospitalization previousHospitalization) {
        if (previousHospitalization == null) {
            previousHospitalization = DatabaseHelper.getPreviousHospitalizationDao().build();
        }
        final PreviousHospitalizationForm previousHospitalizationDialog = new PreviousHospitalizationForm();
        previousHospitalizationDialog.initialize(
                previousHospitalization,
                new Consumer() {
                    @Override
                    public void accept(Object previousHospitalization) {
                        if (PreviousHospitalizationValidator.validatePreviousHospitalizationData(previousHospitalizationDialog.getBinding())) {
                            binding.hospitalizationPreviousHospitalizations.setValue(
                                    ListField.updateList(
                                            binding.hospitalizationPreviousHospitalizations.getValue(),
                                            (PreviousHospitalization) previousHospitalization
                                    )
                            );
                            previousHospitalizationDialog.dismiss();
                            updatePrevHospHint();
                        }
                    }
                }, new Consumer() {
                    @Override
                    public void accept(Object previousHospitalization) {
                        binding.hospitalizationPreviousHospitalizations.setValue(
                                ListField.removeFromList(
                                        binding.hospitalizationPreviousHospitalizations.getValue(),
                                        (PreviousHospitalization) previousHospitalization
                                )
                        );
                        updatePrevHospHint();
                    }
                },
                getActivity().getResources().getString(R.string.headline_previousHospitalization)
        );
        previousHospitalizationDialog.show(getFragmentManager(), "previous_hospitalization_edit_fragment");
    }

    private void updatePrevHospHint() {
        YesNoUnknown hospitalizedPreviously = binding.hospitalizationHospitalizedPreviously.getValue();
        if (hospitalizedPreviously == YesNoUnknown.YES && binding.hospitalizationPreviousHospitalizations.getValue().size() == 0) {
            binding.hospitalizationHospitalizedPreviously.setErrorWithoutFocus(DatabaseHelper.getContext().getResources().getString(R.string.validation_soft_add_list_entry));
        } else {
            binding.hospitalizationHospitalizedPreviously.clearError();
        }
    }

    @Override
    public AbstractDomainObject getData() {
        return binding == null ? null : binding.getHospitalization();
    }

}
