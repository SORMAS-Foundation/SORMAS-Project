package de.symeda.sormas.app.hospitalization;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.annotations.Nullable;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.databinding.PreviousHospitalizationEditFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormFragment;


public class PreviousHospitalizationTab extends DialogFragment implements FormFragment {

//    public static final String PREV_HOSP_UUID = "prevHospUuid";

    private PreviousHospitalizationEditFragmentLayoutBinding binding;
    private PreviousHospitalization prevHosp;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.previous_hospitalization_edit_fragment_layout, container, false);
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

//            final String prevHospUuid = getArguments().getString(PREV_HOSP_UUID);
//            final PreviousHospitalization prevHosp = DatabaseHelper.getPreviousHospitalizationDao().queryUuid(prevHospUuid);
//            binding.setPrevHosp(prevHosp);
            binding.setPrevHosp(prevHosp);

            binding.prevHospAdmissionDate.initialize(this);
            binding.prevHospDischargeDate.initialize(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        getDialog().setTitle(getActivity().getResources().getString(R.string.headline_location));

//        getDialog().setPositiveButton(getActivity().getResources().getString(R.string.action_done), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                if (positiveCallback != null) {
//                    positiveCallback.accept(ado);
//                }
//            }
//        });
//        getDialog().setNegativeButton(getActivity().getResources().getString(R.string.action_dimiss), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                if (negativeCallback != null) {
//                    negativeCallback.call();
//                }
//            }
//        });
    }


    @Override
    public AbstractDomainObject getData() {
        return binding.getPrevHosp();
    }

    public void setData(PreviousHospitalization prevHosp) {
        this.prevHosp = prevHosp;
        onResume();
    }
}
