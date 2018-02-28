package de.symeda.sormas.app.event.edit;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.databinding.FragmentEventEditPersonInfoLayoutBinding;

import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.app.backend.person.Person;

/**
 * Created by Orson on 11/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class OccupationTypeLayoutProcessor {

    private Context context;
    private FragmentEventEditPersonInfoLayoutBinding contentBinding;
    private LinearLayout rootChildLayout;
    private ViewDataBinding binding;

    private int mLastLayoutResId;
    private Person record;
    private OccupationType initialOccupationType;
    private String initialOccupationDetail;

    private OnSetBindingVariableListener mOnSetBindingVariableListener;


    public OccupationTypeLayoutProcessor(Context context, FragmentEventEditPersonInfoLayoutBinding contentBinding, Person record) {
        this.mLastLayoutResId = -1;
        this.context = context;
        this.contentBinding = contentBinding;
        this.record = record;

        this.initialOccupationType = record.getOccupationType();
        this.initialOccupationDetail = record.getOccupationDetails();

        hideRootChildLayout();
    }



    public boolean processLayout(OccupationType occupationType) {
        if (getRootChildLayout() == null)
            return false;

        int layoutResId = getLayoutResId(occupationType);
        String layoutName = getLayoutName(layoutResId);

        if (mLastLayoutResId == layoutResId) {
            if (binding == null)
                return false;

            ensureOccupationDetailIntegrity(occupationType);
            performSetBindingVariable(binding, layoutName);

            return false;
        }

        mLastLayoutResId = layoutResId;

        if (layoutResId <= 0) {
            hideRootChildLayout();
            //getRootChildLayout().setVisibility(View.GONE);
            return false;
        }

        ensureOccupationDetailIntegrity(occupationType);
        binding = inflateChildLayout(layoutResId);

        if (binding == null)
            return false;

        performSetBindingVariable(binding, layoutName);

        return initializeChildLayout(binding);

    }

    private ViewDataBinding inflateChildLayout(int layoutResId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutResId, null);

        ViewDataBinding b = DataBindingUtil.bind(layout);
        String layoutName = context.getResources().getResourceEntryName(layoutResId);
        performSetBindingVariable(b, layoutName);
        //setRootNotificationBindingVariable(b, layoutName);
        //setLocalBindingVariable(b, layoutName);

        return b;
    }


    private boolean initializeChildLayout(ViewDataBinding binding) {
        View innerRootLayout = binding.getRoot();

        if (getRootChildLayout() != null) {
            getRootChildLayout().removeAllViews();
            getRootChildLayout().addView(innerRootLayout);

            getRootChildLayout().setVisibility(View.VISIBLE);
        }

        return true;
    }


    private LinearLayout getRootChildLayout() {
        if (rootChildLayout == null)
            rootChildLayout = (LinearLayout)contentBinding.occupationDetailsInclude.getRoot();

        return rootChildLayout;
    }

    private int getLayoutResId(OccupationType type) {
        if (type == OccupationType.OTHER) {
            return R.layout.fragment_edit_person_info_occupation_other_layout;
        } else if (type == OccupationType.BUSINESSMAN_WOMAN) {
            return R.layout.fragment_edit_person_info_occupation_biz_type_layout;
        } else if (type == OccupationType.TRANSPORTER) {
            return R.layout.fragment_edit_person_info_occupation_trans_type_layout;
        } else if (type == OccupationType.HEALTHCARE_WORKER) {
            return R.layout.fragment_edit_person_info_occupation_hc_worker_layout;
        }

        return -1;
    }

    private String getLayoutName(int layoutResId) {
        if (layoutResId <= 0)
            return null;

        return context.getResources().getResourceEntryName(layoutResId);
    }

    private void ensureOccupationDetailIntegrity(OccupationType type) {
        if (initialOccupationType == type) {
            record.setOccupationDetails(initialOccupationDetail);
        } else {
            record.setOccupationDetails(null);
        }
    }

    private void performSetBindingVariable(ViewDataBinding binding, String layoutName) {
        if (this.mOnSetBindingVariableListener != null)
            this.mOnSetBindingVariableListener.onSetBindingVariable(binding, layoutName);
    }

    private void hideRootChildLayout() {
        if (getRootChildLayout() == null)
            return;

        getRootChildLayout().setVisibility(View.GONE);
        getRootChildLayout().removeAllViews();
    }



    public void setOnSetBindingVariable(OnSetBindingVariableListener listener) {
        this.mOnSetBindingVariableListener = listener;
    }


}
