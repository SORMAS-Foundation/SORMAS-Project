package de.symeda.sormas.app.hospitalization;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.LabelField;
import de.symeda.sormas.app.component.ListField;
import de.symeda.sormas.app.databinding.CaseHospitalizationFragmentLayoutBinding;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;

public class HospitalizationTab extends FormTab {

    public static final String KEY_CASE_UUID = "caseUuid";

    private CaseHospitalizationFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_hospitalization_fragment_layout, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {

            final String caseUuid = getArguments().getString(HospitalizationTab.KEY_CASE_UUID);
            final Case caze = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
            if (caze.getHealthFacility() != null) {
                ((LabelField) getView().findViewById(R.id.hospitalization_healthFacility)).setValue(caze.getHealthFacility().toString());
            }

            // lazy loading hospitalization and inner previousHospitalization
            final String hospitalizationUuid = getArguments().getString(Hospitalization.UUID);
            if (hospitalizationUuid != null) {
                final Hospitalization hospitalization = DatabaseHelper.getHospitalizationDao().queryUuid(hospitalizationUuid);
                // TODO: this should be done by lazy loading the parent hospitalization
                hospitalization.setPreviousHospitalizations(DatabaseHelper.getPreviousHospitalizationDao().getByHospitalization(hospitalization));
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
                            if (prevHosp == null) {
                                try {
                                    prevHosp = DataUtils.createNew(PreviousHospitalization.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            PreviousHospitalizationTab previousHospitalizationTab = new PreviousHospitalizationTab();
                            previousHospitalizationTab.initialize(
                                    (PreviousHospitalization) prevHosp,
                                    new Consumer() {
                                        @Override
                                        public void accept(Object prevHospDialog) {
                                            binding.hospitalizationPreviousHospitalizations.setValue(
                                                    ListField.updateList(
                                                            binding.hospitalizationPreviousHospitalizations.getValue(),
                                                            (PreviousHospitalization) prevHospDialog
                                                    )
                                            );

                                        }
                                    }, new Consumer() {
                                        @Override
                                        public void accept(Object prevHospDialog) {
                                            binding.hospitalizationPreviousHospitalizations.setValue(
                                                    ListField.removeFromList(
                                                            binding.hospitalizationPreviousHospitalizations.getValue(),
                                                            (PreviousHospitalization) prevHospDialog
                                                    )
                                            );

                                        }
                                    },
                                    getActivity().getResources().getString(R.string.headline_previousHospitalization)
                            );
                            previousHospitalizationTab.show(getFragmentManager(), "previous_hospitalization_edit_fragment");
                        }
                    }
            );

            binding.hospitalizationAdmissionDate.initialize(this);
            binding.hospitalizationDischargeDate.initialize(this);
            binding.hospitalization1isolationDate.initialize(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public AbstractDomainObject getData() {
        return binding.getHospitalization();
    }

}
