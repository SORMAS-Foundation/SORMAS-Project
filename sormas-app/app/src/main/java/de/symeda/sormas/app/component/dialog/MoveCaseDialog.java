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
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
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

        if (_contentBinding.caseDataRegion != null) {
            _contentBinding.caseDataRegion.initializeSpinner(RegionLoader.getInstance().load(), null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    Region selectedValue = (Region) field.getValue();
                    if (selectedValue != null) {
                        _contentBinding.caseDataDistrict.setSpinnerData(DataUtils.toItems(DatabaseHelper.getDistrictDao().getByRegion(selectedValue)), _contentBinding.caseDataDistrict.getValue());
                    } else {
                        _contentBinding.caseDataDistrict.setSpinnerData(null);
                    }
                }
            });
        }

        if (_contentBinding.caseDataDistrict != null) {
            _contentBinding.caseDataDistrict.initializeSpinner(DistrictLoader.getInstance().load((Region) _contentBinding.caseDataRegion.getValue()), null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    District selectedValue = (District) field.getValue();
                    if (selectedValue != null) {
                        _contentBinding.caseDataCommunity.setSpinnerData(DataUtils.toItems(DatabaseHelper.getCommunityDao().getByDistrict(selectedValue)), _contentBinding.caseDataCommunity.getValue());
                        _contentBinding.caseDataHealthFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict(selectedValue, true, true)), _contentBinding.caseDataHealthFacility.getValue());
                    } else {
                        _contentBinding.caseDataCommunity.setSpinnerData(null);
                        _contentBinding.caseDataHealthFacility.setSpinnerData(null);
                    }
                }
            });
        }

        if (_contentBinding.caseDataCommunity != null) {
            _contentBinding.caseDataCommunity.initializeSpinner(CommunityLoader.getInstance().load((District) _contentBinding.caseDataDistrict.getValue()), null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    Community selectedValue = (Community) field.getValue();
                    if (selectedValue != null) {
                        _contentBinding.caseDataHealthFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(selectedValue, true, true)));
                    } else if (_contentBinding.caseDataDistrict.getValue() != null) {
                        _contentBinding.caseDataHealthFacility.setSpinnerData(DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict((District) _contentBinding.caseDataDistrict.getValue(), true, true)));
                    } else {
                        _contentBinding.caseDataHealthFacility.setSpinnerData(null);
                    }
                }
            });
        }

        List<Item> facilities = _contentBinding.caseDataCommunity.getValue() != null ? FacilityLoader.getInstance().load((Community) _contentBinding.caseDataCommunity.getValue(), true)
                : FacilityLoader.getInstance().load((District) _contentBinding.caseDataDistrict.getValue(), true);
        if (_contentBinding.caseDataHealthFacility != null) {
            _contentBinding.caseDataHealthFacility.initializeSpinner(facilities, null, new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    if (field.getValue() == null)
                        return;

                    // TODO Re-add facility details field
                }
            });
        }
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