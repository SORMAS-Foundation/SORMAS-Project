package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.event.edit.OnSetBindingVariableListener;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.facility.Facility;

/**
 * Created by Orson on 15/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class HealthFacilityLayoutProcessor {

    private Context context;
    private ViewDataBinding contentBinding;
    private LinearLayout rootChildLayout;
    private OnSetBindingVariableListener mOnSetBindingVariableListener;
    private ViewDataBinding binding;

    private Case record;
    private Facility initialHealthFacility;
    private String initialHealthFacilityDetails;

    private IHealthFacilityDetailFieldChecker mHealthFacilityDetailFieldChecker;

    public HealthFacilityLayoutProcessor(Context context, ViewDataBinding contentBinding, Case record) {
        this.context = context;
        this.contentBinding = contentBinding;
        this.record = record;

        this.initialHealthFacility = record.getHealthFacility();
        this.initialHealthFacilityDetails = record.getHealthFacilityDetails();

        hideRootChildLayout();
    }

    public boolean processLayout(Facility facility) {
        int layoutResId = getLayoutResId(facility);
        String layoutName = getLayoutName(layoutResId);

        if (layoutResId <= 0) {
            hideRootChildLayout();
            return false;
        }

        ensureCauseOfDeathDataIntegrity(facility);
        binding = inflateChildLayout(layoutResId);

        if (binding == null)
            return false;

        performSetBindingVariable(binding, layoutName);

        return initializeChildLayout(binding);
    }

    private ViewDataBinding inflateChildLayout(int layoutResId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutResId, null);

        ViewDataBinding binding = DataBindingUtil.bind(layout);

        return binding;
    }

    private boolean initializeChildLayout(ViewDataBinding binding) {
        View innerRootLayout = binding.getRoot();
        LinearLayout rootLayout = getRootChildLayout();

        if (getRootChildLayout() != null) {
            getRootChildLayout().removeAllViews();
            getRootChildLayout().addView(innerRootLayout);

            getRootChildLayout().setVisibility(View.VISIBLE);
        }

        return true;
    }

    private LinearLayout getRootChildLayout() {
        if (rootChildLayout == null)
            rootChildLayout = (LinearLayout)contentBinding.getRoot().findViewById(R.id.healthFacilityInclude);

        return rootChildLayout;
    }

    private void hideRootChildLayout() {
        if (getRootChildLayout() == null)
            return;

        getRootChildLayout().setVisibility(View.GONE);
        getRootChildLayout().removeAllViews();
    }

    private int getLayoutResId(Facility facility) {
        if (doesHealthFacilityHaveDetails(facility)) {
            return R.layout.fragment_edit_health_facility_details_layout;
        }

        return -1;
    }

    private String getLayoutName(int layoutResId) {
        if (layoutResId <= 0)
            return null;

        return context.getResources().getResourceEntryName(layoutResId);
    }

    private void ensureCauseOfDeathDataIntegrity(Facility facility) {
        if (initialHealthFacility.getUuid() == facility.getUuid()) {
            record.setHealthFacilityDetails(initialHealthFacilityDetails);
        } else {
            record.setHealthFacilityDetails(null);
        }
    }

    private void performSetBindingVariable(ViewDataBinding binding, String layoutName) {
        if (this.mOnSetBindingVariableListener != null) {
            this.mOnSetBindingVariableListener.onSetBindingVariable(binding, layoutName);
        }
    }

    public void setOnSetBindingVariable(OnSetBindingVariableListener listener) {
        this.mOnSetBindingVariableListener = listener;
    }

    private boolean doesHealthFacilityHaveDetails(Facility facility) {
        if (mHealthFacilityDetailFieldChecker == null)
            return false;

        return mHealthFacilityDetailFieldChecker.hasDetailField(facility);
    }

    public void setHealthFacilityDetailFieldChecker(IHealthFacilityDetailFieldChecker callback) {
        this.mHealthFacilityDetailFieldChecker = callback;
    }
}
