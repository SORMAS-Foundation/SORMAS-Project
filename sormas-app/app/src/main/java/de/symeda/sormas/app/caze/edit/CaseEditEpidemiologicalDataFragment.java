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
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseEditEpidLayoutBinding;
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
                final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gathering);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        updateGatherings();
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeGathering((EpiDataGathering) item);
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
                        updateTravels();
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeTravel((EpiDataTravel) item);
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
                        updateBurials();
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeBurial((EpiDataBurial) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };

        contentBinding.btnAddGathering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EpiDataGathering gathering = DatabaseHelper.getEpiDataGatheringDao().build();
                final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gathering);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        addGathering((EpiDataGathering) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeGathering((EpiDataGathering) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        });

        contentBinding.btnAddTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EpiDataTravel travel = DatabaseHelper.getEpiDataTravelDao().build();
                final EpiDataTravelDialog dialog = new EpiDataTravelDialog(CaseEditActivity.getActiveActivity(), travel);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        addTravel((EpiDataTravel) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeTravel((EpiDataTravel) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        });

        contentBinding.btnAddBurial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EpiDataBurial burial = DatabaseHelper.getEpiDataBurialDao().build();
                final EpiDataBurialDialog dialog = new EpiDataBurialDialog(CaseEditActivity.getActiveActivity(), burial);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        addBurial((EpiDataBurial) item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeBurial((EpiDataBurial) item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
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
        // TODO: check sorting
        record.getGatherings().add(0, item);
        updateGatherings();
    }

    private void verifyGatheringStatus() {
        YesNoUnknown hospitalizedPreviously = record.getGatheringAttended();
        if (hospitalizedPreviously == YesNoUnknown.YES && getGatherings().size() <= 0) {
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
        YesNoUnknown hospitalizedPreviously = record.getBurialAttended();
        if (hospitalizedPreviously == YesNoUnknown.YES && getBurials().size() <= 0) {
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
        YesNoUnknown hospitalizedPreviously = record.getTraveled();
        if (hospitalizedPreviously == YesNoUnknown.YES && getTravels().size() <= 0) {
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
