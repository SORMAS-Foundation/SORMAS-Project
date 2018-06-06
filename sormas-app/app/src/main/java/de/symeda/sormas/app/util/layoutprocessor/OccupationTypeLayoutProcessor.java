package de.symeda.sormas.app.util.layoutprocessor;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.CommunityLoader;
import de.symeda.sormas.app.component.dialog.DistrictLoader;
import de.symeda.sormas.app.component.dialog.FacilityLoader;
import de.symeda.sormas.app.component.dialog.RegionLoader;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 13/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class OccupationTypeLayoutProcessor {

    private Context context;
    private ViewDataBinding contentBinding;
    private LinearLayout rootChildLayout;
    private ViewDataBinding binding;

    private int mLastLayoutResId;
    private Person record;
    private OccupationType initialOccupationType;
    private String initialOccupationDetail;

    private TeboSpinner spnFacilityState;
    private TeboSpinner spnFacilityLga;
    private TeboSpinner spnFacilityWard;
    private TeboSpinner spnHealthCareFacility;

    private OnSetBindingVariableListener mOnSetBindingVariableListener;


    public OccupationTypeLayoutProcessor(Context context, ViewDataBinding contentBinding, Person record) {
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

        spnFacilityState = (TeboSpinner)innerRootLayout.findViewById(R.id.spnFacilityState);
        spnFacilityLga = (TeboSpinner)innerRootLayout.findViewById(R.id.spnFacilityLga);
        spnFacilityWard = (TeboSpinner)innerRootLayout.findViewById(R.id.spnFacilityWard);
        spnHealthCareFacility = (TeboSpinner)innerRootLayout.findViewById(R.id.spnHealthCareFacility);

        binding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public void onBound(ViewDataBinding binding) {
                super.onBound(binding);

                if (spnFacilityState != null) {
                    spnFacilityState.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
                        @Override
                        public Object getSelectedValue() {
                            return null;
                        }

                        @Override
                        public List<Item> getDataSource(Object parentValue) {
                            List<Item> regionList = RegionLoader.getInstance().load();

                            return (regionList.size() > 0) ? DataUtils.addEmptyItem(regionList)
                                    : regionList;
                        }

                        @Override
                        public VisualState getInitVisualState() {
                            return null;
                        }
                    });
                }

                if (spnFacilityLga != null) {
                    spnFacilityLga.initialize(spnFacilityState, new TeboSpinner.ISpinnerInitSimpleConfig() {
                        @Override
                        public Object getSelectedValue() {
                            return null;
                        }

                        @Override
                        public List<Item> getDataSource(Object parentValue) {
                            List<Item> districts = DistrictLoader.getInstance().load((Region)parentValue);
                            return (districts.size() > 0) ? DataUtils.addEmptyItem(districts) : districts;
                        }

                        @Override
                        public VisualState getInitVisualState() {
                            return null;
                        }
                    });
                }

                if (spnFacilityWard != null) {
                    spnFacilityWard.initialize(spnFacilityLga, new TeboSpinner.ISpinnerInitSimpleConfig() {
                        @Override
                        public Object getSelectedValue() {
                            return null;
                        }

                        @Override
                        public List<Item> getDataSource(Object parentValue) {
                            List<Item> communities = CommunityLoader.getInstance().load((District)parentValue);

                            return (communities.size() > 0) ? DataUtils.addEmptyItem(communities) : communities;
                        }

                        @Override
                        public VisualState getInitVisualState() {
                            return null;
                        }
                    });
                }


                if (spnHealthCareFacility != null) {
                    spnHealthCareFacility.initialize(spnFacilityWard, new TeboSpinner.ISpinnerInitSimpleConfig() {
                        @Override
                        public Object getSelectedValue() {
                            return null;
                        }

                        @Override
                        public List<Item> getDataSource(Object parentValue) {
                            List<Item> facilities = FacilityLoader.getInstance().load((Community)parentValue, false);
                            return (facilities.size() > 0) ? DataUtils.addEmptyItem(facilities) : facilities;
                        }

                        @Override
                        public VisualState getInitVisualState() {
                            return null;
                        }
                    });
                }


            }
        });

        if (getRootChildLayout() != null) {
            getRootChildLayout().removeAllViews();
            getRootChildLayout().addView(innerRootLayout);

            getRootChildLayout().setVisibility(View.VISIBLE);
        }

        return true;
    }


    private LinearLayout getRootChildLayout() {
        if (rootChildLayout == null)
            rootChildLayout = (LinearLayout)contentBinding.getRoot().findViewById(R.id.occupationDetailsInclude);

        //rootChildLayout = (LinearLayout)contentBinding.occupationDetailsInclude.getRoot();

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
