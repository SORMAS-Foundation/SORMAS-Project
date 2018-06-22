package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.controls.TeboSwitch;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.OnSetBindingVariableListener;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseEditEpidLayoutBinding;
import de.symeda.sormas.app.epid.AnimalContact;
import de.symeda.sormas.app.epid.AnimalContactCategory;
import de.symeda.sormas.app.epid.AnimalContactFormListAdapter;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditEpidemiologicalDataFragment extends BaseEditActivityFragment<FragmentCaseEditEpidLayoutBinding, EpiData, Case> {

    public static final String TAG = CaseEditEpidemiologicalDataFragment.class.getSimpleName();

    private AsyncTask onResumeTask;
    private String recordUuid = null;
    private InvestigationStatus pageStatus = null;
    private EpiData record;
    private int mGatheringLastCheckedId = -1;
    private int mTravelLastCheckedId = -1;
    private int mBurialLastCheckedId = -1;

    private OnTeboSwitchCheckedChangeListener onSocialGatheringCheckedCallback;
    private OnTeboSwitchCheckedChangeListener onTravelCheckedCallback;
    private OnTeboSwitchCheckedChangeListener onBurialCheckedCallback;

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (InvestigationStatus)getPageStatusArg(arguments);
    }

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
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case caze = getActivityRootData();

            if (caze != null) {
                if (caze.isUnreadOrChildUnread())
                    DatabaseHelper.getCaseDao().markAsRead(caze);

                if (caze.getPerson() == null) {
                    caze.setPerson(DatabaseHelper.getPersonDao().build());
                }

                //TODO: Do we really need to do this
                if (caze.getEpiData() != null)
                    caze.setEpiData(DatabaseHelper.getEpiDataDao().queryUuid(caze.getEpiData().getUuid()));
            }

            resultHolder.forItem().add(caze.getEpiData());

            resultHolder.forOther().add(DataUtils.getEnumItems(WaterSource.class, false));

            //TODO: Talk to Martin, we need to categorize the type of Animal Contacts
            resultHolder.forOther().add(AnimalContact.makeAnimalContacts(AnimalContactCategory.GENERAL).loadState(caze.getEpiData()));
            resultHolder.forOther().add(AnimalContact.makeAnimalContacts(AnimalContactCategory.ENVIRONMENTAL_EXPOSURE).loadState(caze.getEpiData()));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (otherIterator.hasNext())
                drinkingWaterSourceList = otherIterator.next();

            if (otherIterator.hasNext())
                animalContactList = otherIterator.next();

            if (otherIterator.hasNext())
                environmentalExposureList = otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditEpidLayoutBinding contentBinding) {

        Case caze = getActivityRootData();

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
        contentBinding.setGatheringCallback(onSocialGatheringCheckedCallback);
        contentBinding.setTravelCallback(onTravelCheckedCallback);
        contentBinding.setBurialCallback(onBurialCheckedCallback);
        contentBinding.setGatheringList(getGatherings());
        contentBinding.setTravelList(getTravels());
        contentBinding.setBurialList(getBurials());
        contentBinding.setGatheringItemClickCallback(onGatheringItemClickListener);
        contentBinding.setTravelItemClickCallback(onTravelItemClickListener);
        contentBinding.setBurialItemClickCallback(onBurialItemClickListener);
        contentBinding.setAddGatheringEntryClickCallback(onAddGatheringEntryClickListener);
        contentBinding.setAddTravelEntryClickCallback(onAddTravelEntryClickListener);
        contentBinding.setAddBurialEntryClickCallback(onAddBurialEntryClickListener);

        setVisibilityByDisease(EpiDataDto.class, caze.getDisease(), contentBinding.mainContent);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseEditEpidLayoutBinding contentBinding) {
        contentBinding.spnSourceOfDrinkingWater.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (drinkingWaterSourceList.size() > 0) ? DataUtils.toItems(drinkingWaterSourceList)
                        : DataUtils.toItems(drinkingWaterSourceList, false);
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                WaterSource waterSource = (WaterSource)value;

                if (waterSource == WaterSource.OTHER) {
                    getContentBinding().txtSourceOfDrinkingWaterOther.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().txtSourceOfDrinkingWaterOther.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void updateUI(FragmentCaseEditEpidLayoutBinding contentBinding, EpiData epiData) {

    }

    @Override
    public void onPageResume(FragmentCaseEditEpidLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
            @Override
            public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                //getActivityCommunicator().showPreloader();
                //getActivityCommunicator().hideFragmentView();
            }

            @Override
            public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                Case caze = getActivityRootData();

                if (caze != null) {
                    if (caze.isUnreadOrChildUnread())
                        DatabaseHelper.getCaseDao().markAsRead(caze);

                    if (caze.getPerson() == null) {
                        caze.setPerson(DatabaseHelper.getPersonDao().build());
                    }

                    //TODO: Do we really need to do this
                    if (caze.getEpiData() != null)
                        caze.setEpiData(DatabaseHelper.getEpiDataDao().queryUuid(caze.getEpiData().getUuid()));
                }

                resultHolder.forItem().add(caze.getEpiData());
            }
        });
        onResumeTask = executor.execute(new ITaskResultCallback() {
            @Override
            public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                //getActivityCommunicator().hidePreloader();
                //getActivityCommunicator().showFragmentView();

                if (resultHolder == null){
                    return;
                }

                ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                if (itemIterator.hasNext())
                    record = itemIterator.next();

                if (record != null)
                    requestLayoutRebind();
                else {
                    getActivity().finish();
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
    public boolean showSaveAction() {
        return true;
    }

    @Override
    public boolean showAddAction() {
        return false;
    }

    private void setupCallback() {
        onSocialGatheringCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (checkedId < 0)
                    return;

                if (mGatheringLastCheckedId == checkedId) {
                    return;
                }

                mGatheringLastCheckedId = checkedId;

                YesNoUnknown answer = (YesNoUnknown)checkedItem;

                if (answer == YesNoUnknown.YES) {
                    getContentBinding().ctrlGatherings.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().ctrlGatherings.setVisibility(View.GONE);
                }
            }
        };


        onTravelCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (checkedId < 0)
                    return;

                if (mTravelLastCheckedId == checkedId) {
                    return;
                }

                mTravelLastCheckedId = checkedId;

                YesNoUnknown answer = (YesNoUnknown)checkedItem;

                if (answer == YesNoUnknown.YES) {
                    getContentBinding().ctrlTravels.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().ctrlTravels.setVisibility(View.GONE);
                }

            }
        };


        onBurialCheckedCallback = new OnTeboSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
                if (checkedId < 0)
                    return;

                if (mBurialLastCheckedId == checkedId) {
                    return;
                }

                mBurialLastCheckedId = checkedId;

                YesNoUnknown answer = (YesNoUnknown)checkedItem;

                if (answer == YesNoUnknown.YES) {
                    getContentBinding().ctrlBurials.setVisibility(View.VISIBLE);
                } else {
                    getContentBinding().ctrlBurials.setVisibility(View.GONE);
                }

            }
        };


        onGatheringItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataGathering gathering = (EpiDataGathering)item;
                final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gathering);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        updateGatherings((EpiDataGathering)item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeGatherings((EpiDataGathering)item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };

        onTravelItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataTravel travel = (EpiDataTravel)item;
                final EpiDataTravelDialog dialog = new EpiDataTravelDialog(CaseEditActivity.getActiveActivity(), travel);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        updateTravels((EpiDataTravel)item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeTravels((EpiDataTravel)item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };

        onBurialItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataBurial burial = (EpiDataBurial)item;
                final EpiDataBurialDialog dialog = new EpiDataBurialDialog(CaseEditActivity.getActiveActivity(), burial);

                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        updateBurials((EpiDataBurial)item);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDeleteClickListener(new TeboAlertDialogInterface.DeleteOnClickListener() {
                    @Override
                    public void onDeleteClick(View v, Object item, View viewRoot) {
                        removeBurials((EpiDataBurial)item);
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
                        removeGatherings((EpiDataGathering)item);
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
                        removeTravels((EpiDataTravel)item);
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
                        removeBurials((EpiDataBurial)item);
                        dialog.dismiss();
                    }
                });

                dialog.show(null);
            }
        };
    }


    // <editor-fold defaultstate="collapsed" desc="Gathering Methods">

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
            getContentBinding().swhGathering.enableErrorState((NotificationContext)getActivity(), R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().swhGathering.disableErrorState();
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Burial Methods">

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
            getContentBinding().swhBurial.enableErrorState((NotificationContext)getActivity(), R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().swhBurial.disableErrorState();
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Travels Methods">

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
            getContentBinding().swhTraveled.enableErrorState((NotificationContext)getActivity(), R.string.validation_soft_add_list_entry);
        } else {
            getContentBinding().swhTraveled.disableErrorState();
        }
    }

    // </editor-fold>

    public static CaseEditEpidemiologicalDataFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule, Case activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseEditEpidemiologicalDataFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
