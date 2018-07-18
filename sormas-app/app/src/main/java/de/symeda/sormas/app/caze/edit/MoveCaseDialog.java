package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.BaseTeboAlertDialog;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogMoveCaseLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;

public class MoveCaseDialog extends BaseTeboAlertDialog {

    public static final String TAG = MoveCaseDialog.class.getSimpleName();

    private Case data;

    private AsyncTask moveCaseTask;

    private List<Item> initialRegions;
    private List<Item> initialDistricts;
    private List<Item> initialCommunities;
    private List<Item> initialFacilities;


    public MoveCaseDialog(final FragmentActivity activity, Case caze) {
        this(activity, R.string.heading_move_case_dialog, -1, caze);
    }

    public MoveCaseDialog(final FragmentActivity activity, int headingResId, int subHeadingResId, Case caze) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_move_case_layout,
                R.layout.dialog_root_two_button_panel_layout, headingResId, subHeadingResId);

        this.data = caze;
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, final Callback.IAction callback) {

        moveCaseTask = new SavingAsyncTask(getActivity().findViewById(android.R.id.content), data) {

            @Override
            protected void doInBackground(TaskResultHolder resultHolder) throws Exception {
                DatabaseHelper.getCaseDao().transferCase(data);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                super.onPostExecute(taskResult);
                if (callback != null)
                    callback.call(taskResult);
            }
        }.executeOnThreadPool();
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

    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void prepareDialogData() {
        initialRegions = InfrastructureHelper.loadRegions();
        initialDistricts = InfrastructureHelper.loadDistricts(data.getRegion());
        initialCommunities = InfrastructureHelper.loadCommunities(data.getDistrict());
        initialFacilities = InfrastructureHelper.loadFacilities(data.getDistrict(), data.getCommunity());
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, final ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        final DialogMoveCaseLayoutBinding _contentBinding = (DialogMoveCaseLayoutBinding) contentBinding;

        InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(_contentBinding.caseDataHealthFacility, _contentBinding.caseDataHealthFacilityDetails);

        InfrastructureHelper.initializeFacilityFields(_contentBinding.caseDataRegion, initialRegions,
                _contentBinding.caseDataDistrict, initialDistricts,
                _contentBinding.caseDataCommunity, initialCommunities,
                _contentBinding.caseDataHealthFacility, initialFacilities);
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
}