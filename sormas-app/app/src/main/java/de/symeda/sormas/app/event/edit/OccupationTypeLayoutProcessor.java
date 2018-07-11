package de.symeda.sormas.app.event.edit;

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
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.CommunityLoader;
import de.symeda.sormas.app.component.dialog.DistrictLoader;
import de.symeda.sormas.app.component.dialog.FacilityLoader;
import de.symeda.sormas.app.component.dialog.RegionLoader;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.databinding.FragmentEventEditPersonInfoLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class OccupationTypeLayoutProcessor {

    private Context context;
    private FragmentEventEditPersonInfoLayoutBinding contentBinding;
    private LinearLayout rootChildLayout;
    private ViewDataBinding binding;

    private int mLastLayoutResId;
    private Person record;
    private OccupationType initialOccupationType;
    private String initialOccupationDetail;

    private ControlSpinnerField spnFacilityState;
    private ControlSpinnerField spnFacilityLga;
    private ControlSpinnerField spnFacilityWard;
    private ControlSpinnerField spnHealthCareFacility;

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

        spnFacilityState = (ControlSpinnerField)innerRootLayout.findViewById(R.id.person_occupationRegion);
        spnFacilityLga = (ControlSpinnerField)innerRootLayout.findViewById(R.id.person_occupationDistrict);
        spnFacilityWard = (ControlSpinnerField)innerRootLayout.findViewById(R.id.person_occupationCommunity);
        spnHealthCareFacility = (ControlSpinnerField)innerRootLayout.findViewById(R.id.person_occupationFacility);

        binding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public void onBound(ViewDataBinding binding) {
                super.onBound(binding);

                if (spnFacilityState != null) {
                    spnFacilityState.initializeSpinner(RegionLoader.getInstance().load(), null, new ValueChangeListener() {
                        @Override
                        public void onChange(ControlPropertyField field) {
                            Region selectedValue = (Region) field.getValue();
                            if (selectedValue != null) {
                                spnFacilityLga.setSpinnerData(DataUtils.toItems(DatabaseHelper.getDistrictDao().getByRegion(selectedValue)), spnFacilityLga.getValue());
                            } else {
                                spnFacilityLga.setSpinnerData(null);
                            }
                        }
                    });
                }

                if (spnFacilityLga != null) {
                    spnFacilityLga.initializeSpinner(DistrictLoader.getInstance().load((Region) spnFacilityState.getValue()), null, new ValueChangeListener() {
                        @Override
                        public void onChange(ControlPropertyField field) {
                            District selectedValue = (District) field.getValue();
                            if (selectedValue != null) {
                                spnFacilityWard.setSpinnerData(DataUtils.toItems(DatabaseHelper.getCommunityDao().getByDistrict(selectedValue)), spnFacilityWard.getValue());
                                spnHealthCareFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict(selectedValue, true)), spnHealthCareFacility.getValue());
                            } else {
                                spnFacilityWard.setSpinnerData(null);
                                spnHealthCareFacility.setSpinnerData(null);
                            }
                        }
                    });
                }

                if (spnFacilityWard != null) {
                    spnFacilityWard.initializeSpinner(CommunityLoader.getInstance().load((District) spnFacilityLga.getValue()), null, new ValueChangeListener() {
                        @Override
                        public void onChange(ControlPropertyField field) {
                            Community selectedValue = (Community) field.getValue();
                            if (selectedValue != null) {
                                spnHealthCareFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(selectedValue, true)));
                            } else if (spnFacilityLga.getValue() != null) {
                                spnHealthCareFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict((District) spnFacilityLga.getValue(), true)));
                            } else {
                                spnHealthCareFacility.setSpinnerData(null);
                            }
                        }
                    });
                }

                List<Item> facilities = spnFacilityWard.getValue() != null ? FacilityLoader.getInstance().load((Community) spnFacilityWard.getValue(), true)
                        : FacilityLoader.getInstance().load((District) spnFacilityLga.getValue(), true);
                if (spnHealthCareFacility != null) {
                    spnHealthCareFacility.initializeSpinner(facilities, null, new ValueChangeListener() {
                        @Override
                        public void onChange(ControlPropertyField field) {

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
