package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.api.epidata.TravelType;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.BaseTeboAlertDialog;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogCaseEpidTravelEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.component.validation.FragmentValidator;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class EpiDataTravelDialog extends BaseTeboAlertDialog {

    public static final String TAG = EpiDataTravelDialog.class.getSimpleName();

    private EpiDataTravel data;
    private DialogCaseEpidTravelEditLayoutBinding mContentBinding;

    private List<Item> travelTypeList;


    public EpiDataTravelDialog(final FragmentActivity activity, EpiDataTravel epiDataTravel) {
        this(activity, R.string.heading_sub_case_epid_travels, -1, epiDataTravel);
    }

    public EpiDataTravelDialog(final FragmentActivity activity, int headingResId, int subHeadingResId, EpiDataTravel epiDataTravel) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_case_epid_travel_edit_layout,
                R.layout.dialog_root_three_button_panel_layout, headingResId, subHeadingResId);

        this.data = epiDataTravel;

        travelTypeList = DataUtils.getEnumItems(TravelType.class, true);
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {

        setLiveValidationDisabled(false);

        try {
            FragmentValidator.validate(getContext(), contentBinding);
        } catch (ValidationException e) {
            NotificationHelper.showDialogNotification(this, ERROR, e.getMessage());
            return;
        }

        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        if (callback != null)
            callback.call(null);
    }

    @Override
    protected void recieveViewDataBinding(Context context, ViewDataBinding binding) {
        this.mContentBinding = (DialogCaseEpidTravelEditLayoutBinding) binding;

        if (data.getId() == null) {
            setLiveValidationDisabled(true);
        }
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void prepareDialogData() {

    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        mContentBinding.epiDataTravelTravelDateFrom.initializeDateField(getFragmentManager());
        mContentBinding.epiDataTravelTravelDateTo.initializeDateField(getFragmentManager());
        mContentBinding.epiDataTravelTravelType.initializeSpinner(travelTypeList);

        CaseValidator.initializeEpiDataTravelValidation(getContext(), (DialogCaseEpidTravelEditLayoutBinding) contentBinding);
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
    public boolean isDeleteButtonVisible() {
        return true;
    }

    @Override
    public boolean isRounded() {
        return true;
    }

    @Override
    public ControlButtonType dismissButtonType() {
        return ControlButtonType.LINE_SECONDARY;
    }

    @Override
    public ControlButtonType okButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

    @Override
    public ControlButtonType deleteButtonType() {
        return ControlButtonType.LINE_DANGER;
    }

}