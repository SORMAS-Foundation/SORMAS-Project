package de.symeda.sormas.app.caze.read;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.caze.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.dialog.SimpleDialog;
import de.symeda.sormas.app.component.tagview.Tag;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseReadEpidLayoutBinding;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.event.Event;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadEpidemiologicalDataFragment extends BaseReadActivityFragment<FragmentCaseReadEpidLayoutBinding> {

    public static final String TAG = CaseReadEpidemiologicalDataFragment.class.getSimpleName();

    private String caseUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private EpiData record;
    private ObservableArrayList burials = new ObservableArrayList();
    private ObservableArrayList gatherings = new ObservableArrayList();
    private ObservableArrayList travels = new ObservableArrayList();
    private FragmentCaseReadEpidLayoutBinding binding;

    private IEntryItemOnClickListener onBurialItemClickListener;
    private IEntryItemOnClickListener onSocialEventItemClickListener;
    private IEntryItemOnClickListener onTravelItemClickListener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, caseUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        caseUuid = getRecordUuidArg(arguments);
        filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setupCallback();

        binding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);
        record = MemoryDatabaseHelper.EPID_DATA.getEpidData(1).get(0);

        binding.setData(record);
        binding.setBurials(getBurialVisits());
        binding.setSocialEvents(getGatherings());
        binding.setTravels(getTravels());
        binding.setAnimalContactYes(getAnimalContactYes());
        binding.setAnimalContactUnknown(getAnimalContactUnknown());
        binding.setAnimalContactNo(getAnimalContactNo());

        binding.setBurialItemClickCallback(onBurialItemClickListener);
        binding.setSocialEventItemClickCallback(onSocialEventItemClickListener);
        binding.setTravelItemClickCallback(onTravelItemClickListener);

        return binding.getRoot();
    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getData();
    }

    @Override
    public FragmentCaseReadEpidLayoutBinding getBinding() {
        return binding;
    }

    @Override
    public Object getRecord() {
        return record;
    }

    public void showRecordEditView(Event event) {
        /*Intent intent = new Intent(getActivity(), TaskEditActivity.class);
        intent.putExtra(Task.UUID, task.getUuid());
        if(parentCaseUuid != null) {
            intent.putExtra(KEY_CASE_UUID, parentCaseUuid);
        }
        if(parentContactUuid != null) {
            intent.putExtra(KEY_CONTACT_UUID, parentContactUuid);
        }
        if(parentEventUuid != null) {
            intent.putExtra(KEY_EVENT_UUID, parentEventUuid);
        }
        startActivity(intent);*/
    }

    @Override
    public int getRootReadLayout() {
        return R.layout.fragment_case_read_epid_layout;
    }

    public static CaseReadEpidemiologicalDataFragment newInstance(CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(CaseReadEpidemiologicalDataFragment.class, capsule);
    }

    private ObservableArrayList getBurialVisits() {
        burials.add(MemoryDatabaseHelper.EPID_DATA_BURIAL.getBurials(1).get(0));
        return burials;
    }

    private ObservableArrayList getGatherings() {
        gatherings.add(MemoryDatabaseHelper.EPID_DATA_GATHERING.getGatherings(1).get(0));
        return gatherings;
    }

    private ObservableArrayList getTravels() {
        travels.add(MemoryDatabaseHelper.EPID_DATA_TRAVEL.getTravels(1).get(0));
        return travels;
    }

    private List<Tag> getAnimalContactYes() {
        List<Tag> results = new ArrayList();
        results.add(new Tag("Rodents or their excreta"));
        results.add(new Tag("Bats or their excreta"));
        return results;
    }

    private List<Tag> getAnimalContactUnknown() {
        List<Tag> results = new ArrayList();
        results.add(new Tag("Primates (monkeys)"));
        results.add(new Tag("Swine"));
        return results;
    }

    private List<Tag> getAnimalContactNo() {
        List<Tag> results = new ArrayList();
        results.add(new Tag("Poultry or wild birds"));
        return results;
    }

    private void setupCallback() {

        onBurialItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_case_epid_burial_layout, item);
                AlertDialog dialog = simpleDialog.show();
            }
        };

        onSocialEventItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_case_epid_social_event_layout, item);
                AlertDialog dialog = simpleDialog.show();
            }
        };

        onTravelItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_case_epid_travel_layout, item);
                AlertDialog dialog = simpleDialog.show();
            }
        };
    }
}
