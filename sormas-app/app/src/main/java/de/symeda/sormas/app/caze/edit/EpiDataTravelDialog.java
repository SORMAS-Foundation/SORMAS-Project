package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.api.epidata.TravelType;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboButtonType;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.BaseTeboAlertDialog;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.DialogEpidTravelsLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 19/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class EpiDataTravelDialog extends BaseTeboAlertDialog {

    public static final String TAG = EpiDataTravelDialog.class.getSimpleName();

    private EpiDataTravel data;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;
    private DialogEpidTravelsLayoutBinding mContentBinding;

    private List<Item> travelTypeList;


    public EpiDataTravelDialog(final FragmentActivity activity, EpiDataTravel epiDataTravel) {
        this(activity, R.string.heading_sub_case_epid_travels, -1, epiDataTravel);
    }

    public EpiDataTravelDialog(final FragmentActivity activity, int headingResId, int subHeadingResId, EpiDataTravel epiDataTravel) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_epid_travels_layout,
                R.layout.dialog_root_three_button_panel_layout, headingResId, subHeadingResId);

        this.data = epiDataTravel;

        travelTypeList = DataUtils.getEnumItems(TravelType.class, false);
    }

    @Override
    protected void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {
        /*DialogEpiDataTravelLayoutBinding _contentBinding = (DialogEpiDataTravelLayoutBinding)contentBinding;

        _contentBinding.spnState.enableErrorState("Hello");*/
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
        this.mContentBinding = (DialogEpidTravelsLayoutBinding)binding;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.addressLinkCallback, onAddressLinkClickedCallback)) {
            Log.e(TAG, "There is no variable 'addressLinkCallback' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeData(TaskResultHolder resultHolder, boolean executionComplete) {

    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        //DialogEpidTravelsLayoutBinding _contentBinding = (DialogEpidTravelsLayoutBinding)contentBinding;

        mContentBinding.dtpTravelFromDate.initialize(getFragmentManager());
        mContentBinding.dtpTravelToDate.initialize(getFragmentManager());

        mContentBinding.spnTravelType.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {

            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (travelTypeList.size() > 0) ? DataUtils.addEmptyItem(travelTypeList)
                        : travelTypeList;
            }
            @Override
            public VisualState getInitVisualState() {
                return null;
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
    public boolean isDeleteButtonVisible() {
        return true;
    }

    @Override
    public boolean isRounded() {
        return true;
    }

    @Override
    public TeboButtonType dismissButtonType() {
        return TeboButtonType.BTN_LINE_SECONDARY;
    }

    @Override
    public TeboButtonType okButtonType() {
        return TeboButtonType.BTN_LINE_PRIMARY;
    }

    @Override
    public TeboButtonType deleteButtonType() {
        return TeboButtonType.BTN_LINE_DANGER;
    }

}