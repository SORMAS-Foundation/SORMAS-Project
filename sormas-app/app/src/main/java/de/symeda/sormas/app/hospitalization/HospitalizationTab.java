package de.symeda.sormas.app.hospitalization;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.AddEditDialog;
import de.symeda.sormas.app.component.Argumentable;
import de.symeda.sormas.app.component.LabelField;
import de.symeda.sormas.app.databinding.CaseHospitalizationFragmentLayoutBinding;
import de.symeda.sormas.app.util.Consumer;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


//        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(
//                    AdapterView<?> parent,
//                    View viewClicked,
//                    int position, long id) {
//                PreviousHospitalization previousHospitalization = (PreviousHospitalization)getListAdapter().getItem(position);
//                //showCaseEditView(caze);
//            }
//        });
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
                        public void accept(Object parameter) {
                            if(parameter instanceof PreviousHospitalization) {
//                                PreviousHospitalizationTab editTab = new PreviousHospitalizationTab();
//                                editTab.setData((PreviousHospitalization) parameter);
//                                AddEditDialog dialogBuilder = new AddEditDialog(getActivity(), null, null, null, editTab);
//                                AlertDialog newPersonDialog = dialogBuilder.create();
//                                newPersonDialog.show();

                                FragmentManager fm = getFragmentManager();
                                PreviousHospitalizationTab previousHospitalizationTab = new PreviousHospitalizationTab();
                                previousHospitalizationTab.setData((PreviousHospitalization) parameter);
                                previousHospitalizationTab.show(fm, "previous_hospitalization_edit_fragment");
                            }
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
