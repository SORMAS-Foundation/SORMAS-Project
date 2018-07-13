package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.CommunityLoader;
import de.symeda.sormas.app.component.dialog.DistrictLoader;
import de.symeda.sormas.app.component.dialog.FacilityLoader;
import de.symeda.sormas.app.component.dialog.RegionLoader;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.databinding.FragmentCaseNewLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;


public class CaseNewFragment extends BaseEditFragment<FragmentCaseNewLayoutBinding, Case, Case> {

    public static final String TAG = CaseNewFragment.class.getSimpleName();

    private Case record;

    private List<Item> diseaseList;
    private List<Item> plagueTypeList;
    private List<Item> dengueFeverTypeList;

    private HealthFacilityLayoutProcessor healthFacilityLayoutProcessor;

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_new_case);
    }

    @Override
    public Case getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();

        diseaseList = DataUtils.getEnumItems(Disease.class, false);
        plagueTypeList = DataUtils.getEnumItems(PlagueType.class, false);
        dengueFeverTypeList = DataUtils.getEnumItems(DengueFeverType.class, false);
    }

    @Override
    public void onLayoutBinding(FragmentCaseNewLayoutBinding contentBinding) {

        healthFacilityLayoutProcessor = new HealthFacilityLayoutProcessor(getContext(), contentBinding, record);
        healthFacilityLayoutProcessor.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
//                setRootNotificationBindingVariable(binding, layoutName);
                setLocalBindingVariable(binding, layoutName);
            }
        });

        healthFacilityLayoutProcessor.setHealthFacilityDetailFieldChecker(new IHealthFacilityDetailFieldChecker() {
            @Override
            public boolean hasDetailField(Facility facility) {
                //TODO: Orson Replace
                return new Random().nextBoolean();
            }
        });

        Disease disease = record.getDisease();

        contentBinding.setData(record);
        //contentBinding.setCheckedCallback(onEventTypeCheckedCallback);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentCaseNewLayoutBinding contentBinding) {
        contentBinding.caseDataDisease.initializeSpinner(diseaseList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                if (field.getValue() == null)
                    return;

                Disease disease = (Disease) field.getValue();

                if (disease == Disease.OTHER) {
                    getContentBinding().caseDataDiseaseDetails.setVisibility(View.VISIBLE);
                    getContentBinding().caseDataPlagueType.setVisibility(View.GONE);
                    getContentBinding().caseDataDengueFeverType.setVisibility(View.GONE);
                } else if (disease == Disease.PLAGUE) {
                    getContentBinding().caseDataDiseaseDetails.setVisibility(View.GONE);
                    getContentBinding().caseDataPlagueType.setVisibility(View.VISIBLE);
                    getContentBinding().caseDataDengueFeverType.setVisibility(View.GONE);
                } else if (disease == Disease.DENGUE) {
                    getContentBinding().caseDataDiseaseDetails.setVisibility(View.GONE);
                    getContentBinding().caseDataPlagueType.setVisibility(View.GONE);
                    getContentBinding().caseDataDengueFeverType.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().caseDataDiseaseDetails.setVisibility(View.GONE);
                    getContentBinding().caseDataPlagueType.setVisibility(View.GONE);
                    getContentBinding().caseDataDengueFeverType.setVisibility(View.GONE);
                }
            }
        });

        contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);

        contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);

        if (contentBinding.caseDataRegion != null) {
            contentBinding.caseDataRegion.initializeSpinner(RegionLoader.getInstance().load(), null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    Region selectedValue = (Region) field.getValue();
                    if (selectedValue != null) {
                        contentBinding.caseDataDistrict.setSpinnerData(DataUtils.toItems(DatabaseHelper.getDistrictDao().getByRegion(selectedValue)), contentBinding.caseDataDistrict.getValue());
                    } else {
                        contentBinding.caseDataDistrict.setSpinnerData(null);
                    }
                }
            });
        }

        if (contentBinding.caseDataDistrict != null) {
            contentBinding.caseDataDistrict.initializeSpinner(DistrictLoader.getInstance().load((Region) contentBinding.caseDataRegion.getValue()), null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    District selectedValue = (District) field.getValue();
                    if (selectedValue != null) {
                        contentBinding.caseDataCommunity.setSpinnerData(DataUtils.toItems(DatabaseHelper.getCommunityDao().getByDistrict(selectedValue)), contentBinding.caseDataCommunity.getValue());
                        contentBinding.caseDataHealthFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict(selectedValue, true, true)), contentBinding.caseDataHealthFacility.getValue());
                    } else {
                        contentBinding.caseDataCommunity.setSpinnerData(null);
                        contentBinding.caseDataHealthFacility.setSpinnerData(null);
                    }
                }
            });
        }

        if (contentBinding.caseDataCommunity != null) {
            contentBinding.caseDataCommunity.initializeSpinner(CommunityLoader.getInstance().load((District) contentBinding.caseDataDistrict.getValue()), null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    Community selectedValue = (Community) field.getValue();
                    if (selectedValue != null) {
                        contentBinding.caseDataHealthFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(selectedValue, true, true)));
                    } else if (contentBinding.caseDataDistrict.getValue() != null) {
                        contentBinding.caseDataHealthFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict((District) contentBinding.caseDataDistrict.getValue(), true, true)));
                    } else {
                        contentBinding.caseDataHealthFacility.setSpinnerData(null);
                    }
                }
            });
        }

        List<Item> facilities = contentBinding.caseDataCommunity.getValue() != null ? FacilityLoader.getInstance().load((Community) contentBinding.caseDataCommunity.getValue(), true)
                : FacilityLoader.getInstance().load((District) contentBinding.caseDataDistrict.getValue(), true);
        if (contentBinding.caseDataHealthFacility != null) {
            contentBinding.caseDataHealthFacility.initializeSpinner(facilities, null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    if (field.getValue() == null)
                        return;

                    if (!healthFacilityLayoutProcessor.processLayout((Facility) field.getValue()))
                        return;
                }
            });
        }

        // TODO lock fields again
//        if ((recordUuid != null && !recordUuid.isEmpty()) || (contactUuid != null && !contactUuid.isEmpty()) || (personUuid != null && !personUuid.isEmpty()))
//        {
//            contentBinding.txtFirstName.changeVisualState(VisualState.DISABLED);
//            contentBinding.txtLastName.changeVisualState(VisualState.DISABLED);
//            contentBinding.txtDiseaseDetail.changeVisualState(VisualState.DISABLED);
//        }

        //contentBinding.caseDataRegion.changeVisualState(VisualState.DISABLED);
        //contentBinding.caseDataDistrict.changeVisualState(VisualState.DISABLED);

        User user = ConfigProvider.getUser();
        if (user.hasUserRole(UserRole.INFORMANT) && user.getHealthFacility() != null) {
            // this is ok, because informants are required to have a community and health facility
            contentBinding.caseDataCommunity.changeVisualState(VisualState.DISABLED);
            contentBinding.caseDataHealthFacility.changeVisualState(VisualState.DISABLED);
        } else {
            contentBinding.caseDataCommunity.changeVisualState(VisualState.NORMAL);
            contentBinding.caseDataHealthFacility.changeVisualState(VisualState.NORMAL);
        }
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_new_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }

    private void setLocalBindingVariable(final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, record)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    public static CaseNewFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseNewFragment.class, capsule, activityRootData);
    }
}
