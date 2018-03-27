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
import de.symeda.sormas.app.component.TeboButtonType;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.ICallback;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogMoveCaseLayoutBinding;
import de.symeda.sormas.app.util.ConstantHelper;
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
    private IRegionLoader regionLoader;
    private IDistrictLoader districtLoader;
    private ICommunityLoader communityLoader;
    private IFacilityLoader facilityLoader;

    public MoveCaseDialog(final FragmentActivity activity, Case caze) {
        this(activity, R.string.heading_move_case_dialog, -1, null,
                null, null, null, caze);
    }

    public MoveCaseDialog(final FragmentActivity activity, IRegionLoader regionLoader,
                                         IDistrictLoader districtLoader, ICommunityLoader communityLoader, IFacilityLoader facilityLoader,
                          Case caze) {
        this(activity, R.string.heading_move_case_dialog, -1, regionLoader, districtLoader,
                communityLoader, facilityLoader, caze);
    }

    public MoveCaseDialog(final FragmentActivity activity, int headingResId, int subHeadingResId,
                                         IRegionLoader regionLoader, IDistrictLoader districtLoader,
                                         ICommunityLoader communityLoader, IFacilityLoader facilityLoader, Case caze) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_move_case_layout,
                R.layout.dialog_root_two_button_panel_layout, headingResId, subHeadingResId);

        this.regionLoader = regionLoader == null? RegionLoader.getInstance() : regionLoader;
        this.districtLoader = districtLoader == null? DistrictLoader.getInstance() : districtLoader;
        this.communityLoader = communityLoader == null? CommunityLoader.getInstance() : communityLoader;
        this.facilityLoader = facilityLoader == null? FacilityLoader.getInstance() : facilityLoader;

        this.data = caze;
        this.tracker = ((SormasApplication) activity.getApplication()).getDefaultTracker();
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, final ICallback callback) {
        //DialogMoveCaseLayoutBinding _contentBinding = (DialogMoveCaseLayoutBinding)contentBinding;
        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private String saveUnsuccessful;

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    /*CaseValidator.clearErrorsForMoveCaseData(binding);
                    if (!CaseValidator.validateMoveCaseData(binding)) {
                        resultHolder.setResultStatus(new BoolResult(false, getResources().getString(R.string.notification_case_move_validation_error)));
                    }*/

                    Resources r = getActivity().getResources();

                    if (r != null)
                        saveUnsuccessful = String.format(r.getString(R.string.notification_case_move_error), r.getString(R.string.entity_case));
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    try {
                        DatabaseHelper.getCaseDao().moveCase(data);
                    } catch (NullPointerException | DaoException e) {
                        // TODO Remove the NullPointerException here as soon as bug #381 has been fixed!
                        resultHolder.setResultStatus(new BoolResult(false, this.saveUnsuccessful));
                        Log.e(getClass().getName(), "Error while trying to move case", e);
                        ErrorReportingHelper.sendCaughtException(tracker, e, data, true);
                    }
                }
            });
            moveCaseTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification((INotificationContext) getActivity(), NotificationType.ERROR, R.string.snackbar_case_moved_error);
                        return;
                    } else {
                        NotificationHelper.showNotification((INotificationContext) getActivity(), NotificationType.SUCCESS, R.string.snackbar_case_moved);
                        callback.result(null);
                    }
                }

            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, ICallback callback) {
        if (moveCaseTask != null && !moveCaseTask.isCancelled())
            moveCaseTask.cancel(true);

        callback.result(null);
    }

    @Override
    protected void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, ICallback callback) {
        callback.result(null);
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

                List<Item> regions = regionLoader.load();

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
                List<Item> districts = districtLoader.load((Region)parentValue);

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
                List<Item> communities = communityLoader.load((District)parentValue);

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
                List<Item> facilities = facilityLoader.load((Community)parentValue, false);

                return (facilities.size() > 0) ? DataUtils.addEmptyItem(facilities) : facilities;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                Facility facility = (Facility)value;

                boolean otherHealthFacility = facility.getUuid().equals(ConstantHelper.OTHER_FACILITY_UUID);
                boolean noneHealthFacility = facility.getUuid().equals(ConstantHelper.NONE_FACILITY_UUID);

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
    public TeboButtonType dismissButtonType() {
        return TeboButtonType.BTN_LINE_DANGER;
    }

    @Override
    public TeboButtonType okButtonType() {
        return TeboButtonType.BTN_LINE_PRIMARY;
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