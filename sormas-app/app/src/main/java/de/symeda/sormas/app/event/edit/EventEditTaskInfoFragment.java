package de.symeda.sormas.app.event.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.databinding.FragmentTaskEditLayoutBinding;
import de.symeda.sormas.app.task.TaskFormNavigationCapsule;
import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;

/**
 * Created by Orson on 12/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EventEditTaskInfoFragment extends BaseEditActivityFragment<FragmentTaskEditLayoutBinding> implements OnTeboSwitchCheckedChangeListener {

    private String recordUuid = null;
    private TaskStatus pageStatus = null;
    private Task record;

    private View.OnClickListener doneCallback;
    private View.OnClickListener notExecCallback;

    private List<Region> regionList;
    private List<District> districtList;
    private List<Community> communityList;
    private List<Facility> facilityList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        //filterStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (TaskStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.heading_level4_1_event_edit_task_info);
    }

    @Override
    public AbstractDomainObject getData() {
        return record;
    }

    @Override
    public void onBeforeLayoutBinding(Bundle savedInstanceState) {

        regionList = MemoryDatabaseHelper.REGION.getRegions(5);
        districtList = MemoryDatabaseHelper.DISTRICT.getDistricts(5);
        communityList = MemoryDatabaseHelper.COMMUNITY.getCommunities(5);
        facilityList = MemoryDatabaseHelper.FACILITY.getFacilities(5);

        for(District d: districtList) {
            d.setRegion(regionList.get(0));
        }

        for(Community c: communityList) {
            c.setDistrict(districtList.get(0));
        }

        for(Facility f: facilityList) {
            f.setCommunity(communityList.get(0));
        }

        setupCallback();

    }

    @Override
    public void onLayoutBinding(ViewStub stub, View inflated, FragmentTaskEditLayoutBinding contentBinding) {

        //binding = DataBindingUtil.inflate(inflater, getEditLayout(), container, true);
        record = MemoryDatabaseHelper.TASK.getTasks(1).get(0);
        Symptoms symptom = MemoryDatabaseHelper.SYMPTOM.getSymptoms(1).get(0);
        Person person = MemoryDatabaseHelper.PERSON.getPersons(1).get(0);
        Event event = MemoryDatabaseHelper.EVENT.getEvents(1).get(0);

        contentBinding.setData(record);
        contentBinding.setTest1(event);
        contentBinding.setTest(symptom);
        contentBinding.setSymptomState(SymptomState.class);
        contentBinding.setDoneCallback(doneCallback);
        contentBinding.setNotExecCallback(notExecCallback);
        contentBinding.setCheckedCallback(this);

        //return binding;
    }

    @Override
    public void onAfterLayoutBinding(FragmentTaskEditLayoutBinding binding) {

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_task_edit_layout;
    }

    private void setupCallback() {

        doneCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*UserReportDialog userReportDialog = new UserReportDialog(TaskEditActivity.getActiveActivity(),
                        this.getClass().getSimpleName(), "9839393949");

                userReportDialog.show();*/

                /*Location location = MemoryDatabaseHelper.LOCATION.getLocations(1).get(0);
                LocationDialog locationDialog = new LocationDialog(TaskEditActivity.getActiveActivity(),
                        regionList, districtList, communityList, location);
                locationDialog.show();*/




                /*Case caze = MemoryDatabaseHelper.CASE.getCases(1).get(0);
                MoveCaseDialog moveCaseDialog = new MoveCaseDialog(TaskEditActivity.getActiveActivity(),
                        regionList, districtList, communityList, facilityList, caze);
                moveCaseDialog.show();*/



                /*userReportDialog.setOnDismissListener()
                userReportDialog.setOnDialogOkClickListener(null);
                userReportDialog.show();*/





                /*AlertDialog dialog2 = new ErrorDialog(TaskEditActivity.getActiveActivity(),
                        this.getClass().getSimpleName(), "9839393949", "").show();*/

                /*AlertDialog dialog3 = new SuccessDialog(TaskEditActivity.getActiveActivity(),
                        this.getClass().getSimpleName(), "9839393949", "").show();*/


                /*binding.txtCommentOnExec.disableErrorState();
                binding.checkbox1.disableErrorState();

                Date d1 = binding.tdpBurialDate.getValue();*/

                //SymptomState kkk = (SymptomState)binding.radio1.getValue();
                //Toast.makeText(getContext(), (kkk != null)? kkk.toString() : "", Toast.LENGTH_SHORT).show();
            }
        };

        notExecCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentBinding().txtCommentOnExec.enableErrorState("There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable.");
                //binding.checkbox1.enableErrorState("Hello");
                Toast.makeText(getContext(), "Not Executable", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {

    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static EventEditTaskInfoFragment newInstance(TaskFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(EventEditTaskInfoFragment.class, capsule);
    }
}

