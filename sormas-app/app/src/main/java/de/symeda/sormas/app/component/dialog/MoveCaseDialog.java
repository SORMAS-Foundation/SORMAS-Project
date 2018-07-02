package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.databinding.library.baseAdapters.BR;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogMoveCaseLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;

/**
 * Created by Orson on 05/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class MoveCaseDialog extends BaseTeboAlertDialog {

    public static final String TAG = MoveCaseDialog.class.getSimpleName();

    private Case data;

    private DialogMoveCaseLayoutBinding mContentBinding;
    private final Tracker tracker;
    private AsyncTask moveCaseTask;

    public MoveCaseDialog(final FragmentActivity activity, Case caze) {
        this(activity, R.string.heading_move_case_dialog, -1, caze);
    }

    public MoveCaseDialog(final FragmentActivity activity, int headingResId, int subHeadingResId, Case caze) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_move_case_layout,
                R.layout.dialog_root_two_button_panel_layout, headingResId, subHeadingResId);

        this.data = caze;
        this.tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, final Callback.IAction callback) {
        //DialogMoveCaseLayoutBinding _contentBinding = (DialogMoveCaseLayoutBinding)contentBinding;
        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                private String saveUnsuccessful;

                @Override
                public void onPreExecute() {
                    /*CaseValidator.clearErrorsForMoveCaseData(binding);
                    if (!CaseValidator.validateMoveCaseData(binding)) {
                        resultHolder.setResultStatus(new BoolResult(false, getResources().getString(R.string.notification_case_move_validation_error)));
                    }*/

                    Resources r = getActivity().getResources();

                    if (r != null)
                        saveUnsuccessful = String.format(r.getString(R.string.notification_case_move_error), r.getString(R.string.entity_case));
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
                    try {
                        DatabaseHelper.getCaseDao().transferCase(data);
                    } catch (NullPointerException | DaoException e) {
                        // TODO Remove the NullPointerException here as soon as bug #381 has been fixed!
                        resultHolder.setResultStatus(new BoolResult(false, this.saveUnsuccessful));
                        Log.e(getClass().getName(), "Error while trying to move case", e);
                        ErrorReportingHelper.sendCaughtException(tracker, e, data, true);
                    }
                }
            };
            moveCaseTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        if (callback != null)
                            callback.call(null);

                        return;
                    }

//                    if (!resultStatus.isSuccess()) {
//                        NotificationHelper.showNotification((INotificationContext) getActivity(), NotificationType.ERROR, R.string.snackbar_case_moved_error);
//                    } else {
//                        NotificationHelper.showNotification((INotificationContext) getActivity(), NotificationType.SUCCESS, R.string.snackbar_case_moved);
//                    }

                    if (callback != null)
                        callback.call(null);
                }

            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        if (moveCaseTask != null && !moveCaseTask.isCancelled())
            moveCaseTask.cancel(true);

        callback.call(null);
    }

    @Override
    protected void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        callback.call(null);
    }

    @Override
    protected void recieveViewDataBinding(Context context, ViewDataBinding binding) {
        this.mContentBinding = (DialogMoveCaseLayoutBinding)binding;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeData(TaskResultHolder resultHolder, boolean executionComplete) {

    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, final ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        final DialogMoveCaseLayoutBinding _contentBinding = (DialogMoveCaseLayoutBinding)contentBinding;


        _contentBinding.spnState.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return data.getRegion();
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                /*Community comm = DatabaseHelper.getCommunityDao().queryForAll().get(0);
                return DataUtils.toItems(DatabaseHelper.getFacilityDao()
                        .getHealthFacilitiesByCommunity(comm, false));*/


                //return DataUtils.toItems(communityList);

                List<Item> regions = RegionLoader.getInstance().load();

                return (regions.size() > 0) ? DataUtils.addEmptyItem(regions) : regions;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        _contentBinding.spnLga.initialize(_contentBinding.spnState, new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return data.getDistrict();
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                List<Item> districts = DistrictLoader.getInstance().load((Region)parentValue);

                return (districts.size() > 0) ? DataUtils.addEmptyItem(districts) : districts;

            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        _contentBinding.spnWard.initialize(_contentBinding.spnLga, new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return data.getCommunity();
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                List<Item> communities = CommunityLoader.getInstance().load((District)parentValue);

                return (communities.size() > 0) ? DataUtils.addEmptyItem(communities) : communities;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        _contentBinding.spnFacility.initialize(_contentBinding.spnWard, new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                List<Item> facilities = FacilityLoader.getInstance().load((Community)parentValue, false);

                return (facilities.size() > 0) ? DataUtils.addEmptyItem(facilities) : facilities;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                Facility facility = (Facility)value;

                boolean otherHealthFacility = facility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
                boolean noneHealthFacility = facility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

                if (otherHealthFacility || noneHealthFacility) {
                    _contentBinding.txtFacilityDetails.setVisibility(View.VISIBLE);
                } else {
                    _contentBinding.txtFacilityDetails.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean isOkButtonVisible() {
        return true;
    }

    @Override
    public boolean isDismissButtonVisible() {
        return true;
    }

    @Override
    public boolean isRounded() {
        return true;
    }

    @Override
    public ControlButtonType dismissButtonType() {
        return ControlButtonType.LINE_DANGER;
    }

    @Override
    public ControlButtonType okButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

    @Override
    public int getPositiveButtonText() {
        return R.string.action_move;
    }




    /*public MoveCaseDialog(final FragmentActivity activity, List<Region> regionList,
                          List<District> districtList, List<Community> communityList,
                          List<Facility> facilityList, Case caze) {
        this(activity, R.string.heading_move_case_dialog, -1, regionList, districtList,
                communityList, facilityList, caze);
    }

    public MoveCaseDialog(final FragmentActivity activity, int headingResId, int subHeadingResId,
                          List<Region> regionList, List<District> districtList, List<Community> communityList,
                          List<Facility> facilityList, Case caze) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_move_case_layout,
                R.layout.dialog_root_two_button_panel_layout, headingResId, subHeadingResId);

        this.data = caze;
    }*/


}