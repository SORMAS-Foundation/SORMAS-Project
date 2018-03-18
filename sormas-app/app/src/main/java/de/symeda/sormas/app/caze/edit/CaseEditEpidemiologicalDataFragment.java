package de.symeda.sormas.app.caze.edit;

import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
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
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseEditEpidLayoutBinding;
import de.symeda.sormas.app.epid.AnimalContact;
import de.symeda.sormas.app.epid.AnimalContactCategory;
import de.symeda.sormas.app.epid.AnimalContactFormListAdapter;
import de.symeda.sormas.app.event.edit.OnSetBindingVariableListener;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditEpidemiologicalDataFragment extends BaseEditActivityFragment<FragmentCaseEditEpidLayoutBinding, EpiData> {

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

    private ObservableArrayList gatherings = new ObservableArrayList();
    private ObservableArrayList travels = new ObservableArrayList();
    private ObservableArrayList burials = new ObservableArrayList();

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

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (InvestigationStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public EpiData getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            EpiData epiData = null;
            Case caze = DatabaseHelper.getCaseDao().queryUuid(recordUuid);
            if (caze != null)
                epiData = DatabaseHelper.getEpiDataDao().queryUuid(caze.getEpiData().getUuid());

            resultHolder.forItem().add(epiData);

            resultHolder.forList().add(epiData.getBurials());
            resultHolder.forList().add(epiData.getGatherings());
            resultHolder.forList().add(epiData.getTravels());

            resultHolder.forOther().add(DataUtils.getEnumItems(WaterSource.class, false));

            //TODO: Talk to Martin, we need to categorize the type of Animal Contacts
            resultHolder.forOther().add(AnimalContact.makeAnimalContacts(AnimalContactCategory.GENERAL).loadState(epiData));
            resultHolder.forOther().add(AnimalContact.makeAnimalContacts(AnimalContactCategory.ENVIRONMENTAL_EXPOSURE).loadState(epiData));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (listIterator.hasNext()) {
                burials.addAll((List<EpiDataBurial>)listIterator.next());
            }

            if (listIterator.hasNext()) {
                gatherings.addAll((List<EpiDataGathering>)listIterator.next());
            }

            if (listIterator.hasNext()) {
                travels.addAll((List<EpiDataTravel>)listIterator.next());
            }

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
                setRootNotificationBindingVariable(binding, layoutName);
            }
        });

        contentBinding.recyclerViewForList.setAdapter(animalContactAdapter);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);

        animalContactAdapter.notifyDataSetChanged();

        environmentalExposureAdapter = new AnimalContactFormListAdapter(this.getActivity(), R.layout.row_edit_animal_contact_list_item_layout, getFragmentManager(), environmentalExposureList);
        environmentalExposureAdapter.setOnSetBindingVariable(new OnSetBindingVariableListener() {
            @Override
            public void onSetBindingVariable(ViewDataBinding binding, String layoutName) {
                setRootNotificationBindingVariable(binding, layoutName);
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
        contentBinding.setGatheringList(gatherings);
        contentBinding.setTravelList(travels);
        contentBinding.setBurialList(burials);
        contentBinding.setGatheringItemClickCallback(onGatheringItemClickListener);
        contentBinding.setTravelItemClickCallback(onTravelItemClickListener);
        contentBinding.setBurialItemClickCallback(onBurialItemClickListener);
        contentBinding.setAddGatheringEntryClickCallback(onAddGatheringEntryClickListener);
        contentBinding.setAddTravelEntryClickCallback(onAddTravelEntryClickListener);
        contentBinding.setAddBurialEntryClickCallback(onAddBurialEntryClickListener);
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
    public int getEditLayout() {
        return R.layout.fragment_case_edit_epid_layout;
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

            }
        };

        onTravelItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {

            }
        };

        onBurialItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {

            }
        };

        onAddGatheringEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataGathering gathering = DatabaseHelper.getEpiDataGatheringDao().build();
                final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gathering);
                dialog.show();


                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        ObservableArrayList newGatherings = new ObservableArrayList();
                        newGatherings.addAll(gatherings);
                        newGatherings.add(0, gathering);
                        getContentBinding().setGatheringList(newGatherings);
                    }
                });
            }
        };

        onAddTravelEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataTravel travel = DatabaseHelper.getEpiDataTravelDao().build();
                final EpiDataTravelDialog dialog = new EpiDataTravelDialog(CaseEditActivity.getActiveActivity(), travel);
                dialog.show();


                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        ObservableArrayList newTravels = new ObservableArrayList();
                        newTravels.addAll(travels);
                        newTravels.add(0, travel);
                        getContentBinding().setTravelList(newTravels);
                    }
                });
            }
        };

        onAddBurialEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataBurial burial = DatabaseHelper.getEpiDataBurialDao().build();
                final EpiDataBurialDialog dialog = new EpiDataBurialDialog(CaseEditActivity.getActiveActivity(), burial);
                dialog.show();


                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        ObservableArrayList newBurials = new ObservableArrayList();
                        newBurials.addAll(burials);
                        newBurials.add(0, burial);
                        getContentBinding().setBurialList(newBurials);
                    }
                });
            }
        };
    }

    public static CaseEditEpidemiologicalDataFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseEditEpidemiologicalDataFragment.class, capsule);
    }
}
