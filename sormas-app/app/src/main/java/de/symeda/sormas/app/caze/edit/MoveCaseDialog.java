package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.DialogMoveCaseLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class MoveCaseDialog extends AbstractDialog {

    public static final String TAG = MoveCaseDialog.class.getSimpleName();

    private Case data;

    MoveCaseDialog(final FragmentActivity activity, Case caze) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_move_case_layout,
                R.layout.dialog_root_two_button_panel_layout, R.string.heading_move_case_dialog, -1);

        this.data = caze;
    }

    @Override
    protected void setBindingVariable(Context context, ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, final ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding) {
        final DialogMoveCaseLayoutBinding _contentBinding = (DialogMoveCaseLayoutBinding) contentBinding;

        InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(_contentBinding.caseDataHealthFacility,
                _contentBinding.caseDataHealthFacilityDetails);

        List<Item> initialRegions = InfrastructureHelper.loadRegions();
        List<Item> initialDistricts = InfrastructureHelper.loadDistricts(data.getRegion());
        List<Item> initialCommunities = InfrastructureHelper.loadCommunities(data.getDistrict());
        List<Item> initialFacilities = InfrastructureHelper.loadFacilities(data.getDistrict(), data.getCommunity());
        InfrastructureHelper.initializeFacilityFields(_contentBinding.caseDataRegion, initialRegions,
                _contentBinding.caseDataDistrict, initialDistricts,
                _contentBinding.caseDataCommunity, initialCommunities,
                _contentBinding.caseDataHealthFacility, initialFacilities);

        setPositiveCallback(new de.symeda.sormas.app.util.Callback() {
            @Override
            public void call() {
                try {
                    FragmentValidator.validate(getContext(), contentBinding);
                } catch (ValidationException e) {
                    NotificationHelper.showDialogNotification(MoveCaseDialog.this, ERROR, e.getMessage());
                    return;
                }

                try {
                    DatabaseHelper.getCaseDao().transferCase(data);
                } catch (DaoException e) {
                    NotificationHelper.showDialogNotification(MoveCaseDialog.this, ERROR, getContext().getResources().getString(R.string.error_case_transfer));
                }
            }
        });
    }

    @Override
    public boolean isPositiveButtonVisible() {
        return true;
    }

    @Override
    public boolean isNegativeButtonVisible() {
        return true;
    }

    @Override
    public boolean isRounded() {
        return true;
    }

    @Override
    public ControlButtonType getNegativeButtonType() {
        return ControlButtonType.LINE_DANGER;
    }

    @Override
    public ControlButtonType getPositiveButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

    @Override
    public int getPositiveButtonText() {
        return R.string.action_move;
    }

}