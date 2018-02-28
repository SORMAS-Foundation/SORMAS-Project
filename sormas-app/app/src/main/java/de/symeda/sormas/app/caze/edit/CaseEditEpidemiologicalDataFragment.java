package de.symeda.sormas.app.caze.edit;

import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;

import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseEditEpidLayoutBinding;
import de.symeda.sormas.app.epid.AnimalContact;
import de.symeda.sormas.app.epid.AnimalContactCategory;
import de.symeda.sormas.app.epid.AnimalContactFormListAdapter;
import de.symeda.sormas.app.event.edit.OnSetBindingVariableListener;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditEpidemiologicalDataFragment extends BaseEditActivityFragment<FragmentCaseEditEpidLayoutBinding> {

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
    private List<WaterSource> drinkingWaterSourceList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, followUpStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        //followUpStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (InvestigationStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public AbstractDomainObject getData() {
        return record;
    }

    @Override
    public void onBeforeLayoutBinding(Bundle savedInstanceState) {

        drinkingWaterSourceList = MemoryDatabaseHelper.WATER_SOURCE.getWaterSources();

        record = MemoryDatabaseHelper.EPID_DATA.getEpidData(1).get(0);

        animalContactList = AnimalContact.makeAnimalContacts(AnimalContactCategory.GENERAL).loadState(record);
        environmentalExposureList = AnimalContact.makeAnimalContacts(AnimalContactCategory.ENVIRONMENTAL_EXPOSURE).loadState(record);

        for(AnimalContact ac: animalContactList) {
            if (ac.equals(AnimalContact.OTHER_ANIMAL)) {
                ac.getLayout().setDetailOrSpecify("abc");
            }
        }

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

        loadGatherings();
        loadTravels();
        loadBurials();
        setupCallback();
    }

    @Override
    public void onLayoutBinding(ViewStub stub, View inflated, FragmentCaseEditEpidLayoutBinding contentBinding) {
        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);

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
    public void onAfterLayoutBinding(FragmentCaseEditEpidLayoutBinding binding) {
        binding.spnSourceOfDrinkingWater.initialize(new TeboSpinner.ISpinnerInitConfig() {
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
                final EpiDataGathering gathering = MemoryDatabaseHelper.EPID_DATA_GATHERING.getGatherings(1).get(0);
                final EpiDataGatheringDialog dialog = new EpiDataGatheringDialog(CaseEditActivity.getActiveActivity(), gathering);
                dialog.show();


                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        gatherings.add(0, gathering);
                    }
                });

                //gatherings.add(0, MemoryDatabaseHelper.EPID_DATA_GATHERING.getGatherings(20).get(new Random().nextInt(10)));
            }
        };

        onAddTravelEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataTravel travel = MemoryDatabaseHelper.EPID_DATA_TRAVEL.getTravels(1).get(0);
                final EpiDataTravelDialog dialog = new EpiDataTravelDialog(CaseEditActivity.getActiveActivity(), travel);
                dialog.show();


                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        travels.add(0, travel);
                    }
                });

                //travels.add(0, MemoryDatabaseHelper.EPID_DATA_TRAVEL.getTravels(20).get(new Random().nextInt(10)));
            }
        };

        onAddBurialEntryClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final EpiDataBurial burial = MemoryDatabaseHelper.EPID_DATA_BURIAL.getBurials(1).get(0);
                final EpiDataBurialDialog dialog = new EpiDataBurialDialog(CaseEditActivity.getActiveActivity(), burial);
                dialog.show();


                dialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        burials.add(0, burial);
                    }
                });

                //travels.add(0, MemoryDatabaseHelper.EPID_DATA_TRAVEL.getTravels(20).get(new Random().nextInt(10)));
            }
        };
    }

    private void loadGatherings() {
        EpiDataGathering item = MemoryDatabaseHelper.EPID_DATA_GATHERING.getGatherings(1).get(0);
        gatherings.add(item);
    }

    private void loadTravels() {
        EpiDataTravel item = MemoryDatabaseHelper.EPID_DATA_TRAVEL.getTravels(1).get(0);
        travels.add(item);
    }

    private void loadBurials() {
        EpiDataBurial item = MemoryDatabaseHelper.EPID_DATA_BURIAL.getBurials(1).get(0);
        burials.add(item);
    }

    public static CaseEditEpidemiologicalDataFragment newInstance(CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(CaseEditEpidemiologicalDataFragment.class, capsule);
    }
}
