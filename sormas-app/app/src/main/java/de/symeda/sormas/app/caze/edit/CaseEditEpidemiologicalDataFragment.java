package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epidata.AnimalCondition;
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
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseEditEpidLayoutBinding;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.DataUtils;

public class CaseEditEpidemiologicalDataFragment extends BaseEditFragment<FragmentCaseEditEpidLayoutBinding, EpiData, Case> {

    public static final String TAG = CaseEditEpidemiologicalDataFragment.class.getSimpleName();

    private EpiData record;
    private Disease disease;

    private IEntryItemOnClickListener onGatheringItemClickListener;
    private IEntryItemOnClickListener onTravelItemClickListener;
    private IEntryItemOnClickListener onBurialItemClickListener;

    private List<Item> drinkingWaterSourceList;
    private List<Item> animalConditionList;

    // Static methods

    public static CaseEditEpidemiologicalDataFragment newInstance(Case activityRootData) {
        return newInstance(CaseEditEpidemiologicalDataFragment.class, null, activityRootData);
    }

    // Instance methods

    private void setUpControlListeners(final FragmentCaseEditEpidLayoutBinding contentBinding) {
        onGatheringItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataGathering gathering = (EpiDataGathering) item;
                final EpiDataGathering gatheringClone = (EpiDataGathering) gathering.clone();
                final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gatheringClone);

                dialog.setPositiveCallback(new Callback() {
                    @Override
                    public void call() {
                        record.getGatherings().set(record.getGatherings().indexOf(gathering), gatheringClone);
                        updateGatherings();
                        dialog.dismiss();
                    }
                });

                dialog.setDeleteCallback(new Callback() {
                    @Override
                    public void call() {
                        removeGathering(gathering);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        };

        onTravelItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataTravel travel = (EpiDataTravel) item;
                final EpiDataTravel travelClone = (EpiDataTravel) travel.clone();
                final EpiDataTravelDialog dialog = new EpiDataTravelDialog(CaseEditActivity.getActiveActivity(), travelClone);

                dialog.setPositiveCallback(new Callback() {
                    @Override
                    public void call() {
                        record.getTravels().set(record.getTravels().indexOf(travel), travelClone);
                        updateTravels();
                        dialog.dismiss();
                    }
                });

                dialog.setDeleteCallback(new Callback() {
                    @Override
                    public void call() {
                        removeTravel(travel);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        };

        onBurialItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataBurial burial = (EpiDataBurial) item;
                final EpiDataBurial burialClone = (EpiDataBurial) burial.clone();
                final EpiDataBurialDialog dialog = new EpiDataBurialDialog(CaseEditActivity.getActiveActivity(), burialClone);

                dialog.setPositiveCallback(new Callback() {
                    @Override
                    public void call() {
                        record.getBurials().set(record.getBurials().indexOf(burial), burialClone);
                        updateBurials();
                        dialog.dismiss();
                    }
                });

                dialog.setDeleteCallback(new Callback() {
                    @Override
                    public void call() {
                        removeBurial(burial);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        };

        contentBinding.btnAddGathering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EpiDataGathering gathering = DatabaseHelper.getEpiDataGatheringDao().build();
                final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gathering);

                dialog.setPositiveCallback(new Callback() {
                    @Override
                    public void call() {
                        addGathering(gathering);
                        dialog.dismiss();
                    }
                });

                dialog.setDeleteCallback(new Callback() {
                    @Override
                    public void call() {
                        removeGathering(gathering);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        contentBinding.btnAddTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EpiDataTravel travel = DatabaseHelper.getEpiDataTravelDao().build();
                final EpiDataTravelDialog dialog = new EpiDataTravelDialog(CaseEditActivity.getActiveActivity(), travel);

                dialog.setPositiveCallback(new Callback() {
                    @Override
                    public void call() {
                        addTravel(travel);
                        dialog.dismiss();
                    }
                });

                dialog.setDeleteCallback(new Callback() {
                    @Override
                    public void call() {
                        removeTravel(travel);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        contentBinding.btnAddBurial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EpiDataBurial burial = DatabaseHelper.getEpiDataBurialDao().build();
                final EpiDataBurialDialog dialog = new EpiDataBurialDialog(CaseEditActivity.getActiveActivity(), burial);

                dialog.setPositiveCallback(new Callback() {
                    @Override
                    public void call() {
                        addBurial(burial);
                        dialog.dismiss();
                    }
                });

                dialog.setDeleteCallback(new Callback() {
                    @Override
                    public void call() {
                        removeBurial(burial);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    private ObservableArrayList<EpiDataGathering> getGatherings() {
        ObservableArrayList<EpiDataGathering> newGatherings = new ObservableArrayList<>();
        newGatherings.addAll(record.getGatherings());
        return newGatherings;
    }

    private void clearGatherings() {
        record.getGatherings().clear();
        updateGatherings();
    }

    private void removeGathering(EpiDataGathering item) {
        record.getGatherings().remove(item);
        updateGatherings();
    }

    private void updateGatherings() {
        getContentBinding().setGatheringList(getGatherings());
        verifyGatheringStatus();
    }

    private void addGathering(EpiDataGathering item) {
        record.getGatherings().add(0, item);
        updateGatherings();
    }

    private void verifyGatheringStatus() {
        YesNoUnknown gatheringAttended = record.getGatheringAttended();
        if (gatheringAttended == YesNoUnknown.YES && getGatherings().size() <= 0) {
            getContentBinding().epiDataGatheringAttended.enableWarningState(R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().epiDataGatheringAttended.disableWarningState();
        }
    }

    private ObservableArrayList<EpiDataBurial> getBurials() {
        ObservableArrayList<EpiDataBurial> newBurials = new ObservableArrayList<>();
        newBurials.addAll(record.getBurials());
        return newBurials;
    }

    private void clearBurials() {
        record.getBurials().clear();
        updateBurials();
    }

    private void removeBurial(EpiDataBurial item) {
        record.getBurials().remove(item);
        updateBurials();
    }

    private void updateBurials() {
        getContentBinding().setBurialList(getBurials());
        verifyBurialStatus();
    }

    private void addBurial(EpiDataBurial item) {
        record.getBurials().add(0, item);
        updateBurials();
    }

    private void verifyBurialStatus() {
        YesNoUnknown burialAttended = record.getBurialAttended();
        if (burialAttended == YesNoUnknown.YES && getBurials().size() <= 0) {
            getContentBinding().epiDataBurialAttended.enableWarningState(R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().epiDataBurialAttended.disableWarningState();
        }
    }

    private ObservableArrayList<EpiDataTravel> getTravels() {
        ObservableArrayList<EpiDataTravel> newTravels = new ObservableArrayList<>();
        newTravels.addAll(record.getTravels());
        return newTravels;
    }

    private void clearTravels() {
        record.getTravels().clear();
        updateTravels();
    }

    private void removeTravel(EpiDataTravel item) {
        record.getTravels().remove(item);
        updateTravels();
    }

    private void updateTravels() {
        getContentBinding().setTravelList(getTravels());
        verifyTravelStatus();
    }

    private void addTravel(EpiDataTravel item) {
        record.getTravels().add(0, item);
        updateTravels();
    }

    private void verifyTravelStatus() {
        YesNoUnknown traveled = record.getTraveled();
        if (traveled == YesNoUnknown.YES && getTravels().size() <= 0) {
            getContentBinding().epiDataTraveled.enableWarningState(R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().epiDataTraveled.disableWarningState();
        }
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_case_epidemiological_data);
    }

    @Override
    public EpiData getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        Case caze = getActivityRootData();
        disease = caze.getDisease();
        record = caze.getEpiData();

        drinkingWaterSourceList = DataUtils.getEnumItems(WaterSource.class, true);
        animalConditionList = DataUtils.getEnumItems(AnimalCondition.class, true);
    }

    @Override
    public void onLayoutBinding(final FragmentCaseEditEpidLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        contentBinding.setData(record);
        contentBinding.setWaterSourceClass(WaterSource.class);
        contentBinding.setGatheringList(getGatherings());
        contentBinding.setTravelList(getTravels());
        contentBinding.setBurialList(getBurials());
        contentBinding.setGatheringItemClickCallback(onGatheringItemClickListener);
        contentBinding.setTravelItemClickCallback(onTravelItemClickListener);
        contentBinding.setBurialItemClickCallback(onBurialItemClickListener);

        contentBinding.epiDataBurialAttended.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                YesNoUnknown value = (YesNoUnknown) field.getValue();
                contentBinding.burialsLayout.setVisibility(value == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                if (value != YesNoUnknown.YES) {
                    clearBurials();
                }

                verifyBurialStatus();
            }
        });

        contentBinding.epiDataGatheringAttended.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                YesNoUnknown value = (YesNoUnknown) field.getValue();
                contentBinding.gatheringsLayout.setVisibility(value == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                if (value != YesNoUnknown.YES) {
                    clearGatherings();
                }

                verifyGatheringStatus();
            }
        });

        contentBinding.epiDataTraveled.addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                YesNoUnknown value = (YesNoUnknown) field.getValue();
                contentBinding.travelsLayout.setVisibility(value == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
                if (value != YesNoUnknown.YES) {
                    clearTravels();
                }

                verifyTravelStatus();
            }
        });
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditEpidLayoutBinding contentBinding) {
        setVisibilityByDisease(EpiDataDto.class, disease, contentBinding.mainContent);

        // Initialize ControlSpinnerFields
        contentBinding.epiDataWaterSource.initializeSpinner(drinkingWaterSourceList);
        contentBinding.epiDataAnimalCondition.initializeSpinner(animalConditionList);

        // Initialize ControlDateFields
        contentBinding.epiDataDateOfLastExposure.initializeDateField(getFragmentManager());
        contentBinding.epiDataSickDeadAnimalsDate.initializeDateField(getFragmentManager());

        verifyBurialStatus();
        verifyGatheringStatus();
        verifyTravelStatus();
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_edit_epid_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowNewAction() {
        return false;
    }

}
