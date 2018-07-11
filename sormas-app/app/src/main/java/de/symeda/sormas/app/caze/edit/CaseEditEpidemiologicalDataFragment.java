package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.databinding.FragmentCaseEditEpidLayoutBinding;
import de.symeda.sormas.app.epid.AnimalContact;
import de.symeda.sormas.app.epid.AnimalContactCategory;
import de.symeda.sormas.app.epid.AnimalContactFormListAdapter;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;

public class CaseEditEpidemiologicalDataFragment extends BaseEditFragment<FragmentCaseEditEpidLayoutBinding, EpiData, Case> {

    public static final String TAG = CaseEditEpidemiologicalDataFragment.class.getSimpleName();

    private EpiData record;
    private Disease disease;

    private IEntryItemOnClickListener onGatheringItemClickListener;
    private IEntryItemOnClickListener onTravelItemClickListener;
    private IEntryItemOnClickListener onBurialItemClickListener;

    private IEntryItemOnClickListener onAddGatheringEntryClickListener;
    private IEntryItemOnClickListener onAddTravelEntryClickListener;
    private IEntryItemOnClickListener onAddBurialEntryClickListener;

    private AnimalContactFormListAdapter animalContactAdapter;
    private AnimalContactFormListAdapter environmentalExposureAdapter;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager linearLayoutManagerForEvnExpo;
    private List<AnimalContact> animalContactList;
    private List<AnimalContact> environmentalExposureList;
    private List<Item> drinkingWaterSourceList;

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_epidemiological_information);
    }

    @Override
    public EpiData getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Case caze = getActivityRootData();
        disease = caze.getDisease();
        record = caze.getEpiData();

        drinkingWaterSourceList = DataUtils.getEnumItems(WaterSource.class, false);
        animalContactList = AnimalContact.makeAnimalContacts(AnimalContactCategory.GENERAL).loadState(caze.getEpiData());
        environmentalExposureList = AnimalContact.makeAnimalContacts(AnimalContactCategory.ENVIRONMENTAL_EXPOSURE).loadState(caze.getEpiData());
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditEpidLayoutBinding contentBinding) {

        setupCallback();

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        linearLayoutManagerForEvnExpo = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        animalContactAdapter = new AnimalContactFormListAdapter(this.getActivity(), R.layout.row_edit_animal_contact_list_item_layout, getFragmentManager(), animalContactList);
        animalContactAdapter.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
//                setRootNotificationBindingVariable(binding, layoutName);
            }
        });

        contentBinding.recyclerViewForList.setAdapter(animalContactAdapter);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);

        animalContactAdapter.notifyDataSetChanged();

        environmentalExposureAdapter = new AnimalContactFormListAdapter(this.getActivity(), R.layout.row_edit_animal_contact_list_item_layout, getFragmentManager(), environmentalExposureList);
        environmentalExposureAdapter.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
//                setRootNotificationBindingVariable(binding, layoutName);
            }
        });

        contentBinding.recyclerViewForEnvExposureList.setAdapter(environmentalExposureAdapter);
        contentBinding.recyclerViewForEnvExposureList.setLayoutManager(linearLayoutManagerForEvnExpo);

        environmentalExposureAdapter.notifyDataSetChanged();

        contentBinding.setData(record);
        contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
        contentBinding.setGatheringList(getGatherings());
        contentBinding.setTravelList(getTravels());
        contentBinding.setBurialList(getBurials());
        contentBinding.setGatheringItemClickCallback(onGatheringItemClickListener);
        contentBinding.setTravelItemClickCallback(onTravelItemClickListener);
        contentBinding.setBurialItemClickCallback(onBurialItemClickListener);
        contentBinding.setAddGatheringEntryClickCallback(onAddGatheringEntryClickListener);
        contentBinding.setAddTravelEntryClickCallback(onAddTravelEntryClickListener);
        contentBinding.setAddBurialEntryClickCallback(onAddBurialEntryClickListener);

        setVisibilityByDisease(EpiDataDto.class, disease, contentBinding.mainContent);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditEpidLayoutBinding contentBinding) {
        contentBinding.epiDataWaterSource.initializeSpinner(drinkingWaterSourceList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                WaterSource waterSource = (WaterSource) field.getValue();

                if (waterSource == WaterSource.OTHER) {
                    getContentBinding().epiDataWaterSourceOther.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().epiDataWaterSourceOther.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_edit_epid_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }

    private void setupCallback() {

        onGatheringItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataGathering gathering = (EpiDataGathering) item;
                final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gathering);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        updateGatherings((EpiDataGathering) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeGatherings((EpiDataGathering) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };

        onTravelItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataTravel travel = (EpiDataTravel) item;
                final EpiDataTravelDialog dialog = new EpiDataTravelDialog(CaseEditActivity.getActiveActivity(), travel);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        updateTravels((EpiDataTravel) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeTravels((EpiDataTravel) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };

        onBurialItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataBurial burial = (EpiDataBurial) item;
                final EpiDataBurialDialog dialog = new EpiDataBurialDialog(CaseEditActivity.getActiveActivity(), burial);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        updateBurials((EpiDataBurial) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeBurials((EpiDataBurial) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };

        onAddGatheringEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataGathering gathering = DatabaseHelper.getEpiDataGatheringDao().build();
                final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gathering);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        addGatherings((EpiDataGathering) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeGatherings((EpiDataGathering) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };

        onAddTravelEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataTravel travel = DatabaseHelper.getEpiDataTravelDao().build();
                final EpiDataTravelDialog dialog = new EpiDataTravelDialog(CaseEditActivity.getActiveActivity(), travel);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        addTravels((EpiDataTravel) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeTravels((EpiDataTravel) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };

        onAddBurialEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataBurial burial = DatabaseHelper.getEpiDataBurialDao().build();
                final EpiDataBurialDialog dialog = new EpiDataBurialDialog(CaseEditActivity.getActiveActivity(), burial);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        addBurials((EpiDataBurial) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeBurials((EpiDataBurial) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };
    }

    private ObservableArrayList getGatherings() {
        ObservableArrayList newGatherings = new ObservableArrayList();
        if (record != null)
            newGatherings.addAll(record.getGatherings());

        return newGatherings;
    }

    private void removeGatherings(EpiDataGathering item) {
        if (record == null)
            return;

        if (record.getGatherings() == null)
            return;

        record.getGatherings().remove(item);

        getContentBinding().setGatheringList(getGatherings());
        verifyGatheringStatus();
    }

    private void updateGatherings(EpiDataGathering item) {
        if (record == null)
            return;

        if (record.getGatherings() == null)
            return;

        getContentBinding().setGatheringList(getGatherings());
        verifyGatheringStatus();
    }

    private void addGatherings(EpiDataGathering item) {
        if (record == null)
            return;

        if (record.getGatherings() == null)
            return;

        record.getGatherings().add(0, (EpiDataGathering) item);

        getContentBinding().setGatheringList(getGatherings());
        verifyGatheringStatus();
    }

    private void verifyGatheringStatus() {
        YesNoUnknown hospitalizedPreviously = record.getGatheringAttended();
        if (hospitalizedPreviously == YesNoUnknown.YES && getGatherings().size() <= 0) {
            getContentBinding().epiDataGatheringAttended.enableErrorState((NotificationContext) getActivity(), R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().epiDataGatheringAttended.disableErrorState();
        }
    }

    private ObservableArrayList getBurials() {
        ObservableArrayList newBurials = new ObservableArrayList();
        if (record != null)
            newBurials.addAll(record.getBurials());

        return newBurials;
    }

    private void removeBurials(EpiDataBurial item) {
        if (record == null)
            return;

        if (record.getBurials() == null)
            return;

        record.getBurials().remove(item);

        getContentBinding().setBurialList(getBurials());
        verifyBurialStatus();
    }

    private void updateBurials(EpiDataBurial item) {
        if (record == null)
            return;

        if (record.getBurials() == null)
            return;

        getContentBinding().setBurialList(getBurials());
        verifyBurialStatus();
    }

    private void addBurials(EpiDataBurial item) {
        if (record == null)
            return;

        if (record.getBurials() == null)
            return;

        record.getBurials().add(0, (EpiDataBurial) item);

        getContentBinding().setBurialList(getBurials());
        verifyBurialStatus();
    }

    private void verifyBurialStatus() {
        YesNoUnknown hospitalizedPreviously = record.getBurialAttended();
        if (hospitalizedPreviously == YesNoUnknown.YES && getBurials().size() <= 0) {
            getContentBinding().epiDataBurialAttended.enableErrorState((NotificationContext) getActivity(), R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().epiDataBurialAttended.disableErrorState();
        }
    }

    private ObservableArrayList getTravels() {
        ObservableArrayList newTravels = new ObservableArrayList();
        if (record != null)
            newTravels.addAll(record.getTravels());

        return newTravels;
    }

    private void removeTravels(EpiDataTravel item) {
        if (record == null)
            return;

        if (record.getTravels() == null)
            return;

        record.getTravels().remove(item);

        getContentBinding().setTravelList(getTravels());
        verifyTravelStatus();
    }

    private void updateTravels(EpiDataTravel item) {
        if (record == null)
            return;

        if (record.getTravels() == null)
            return;

        getContentBinding().setTravelList(getTravels());
        verifyTravelStatus();
    }

    private void addTravels(EpiDataTravel item) {
        if (record == null)
            return;

        if (record.getTravels() == null)
            return;

        record.getTravels().add(0, item);

        getContentBinding().setTravelList(getTravels());
        verifyTravelStatus();
    }

    private void verifyTravelStatus() {
        YesNoUnknown hospitalizedPreviously = record.getTraveled();
        if (hospitalizedPreviously == YesNoUnknown.YES && getTravels().size() <= 0) {
            getContentBinding().epiDataTraveled.enableErrorState((NotificationContext) getActivity(), R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().epiDataTraveled.disableErrorState();
        }
    }


    public static CaseEditEpidemiologicalDataFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseEditEpidemiologicalDataFragment.class, capsule, activityRootData);
    }
}
