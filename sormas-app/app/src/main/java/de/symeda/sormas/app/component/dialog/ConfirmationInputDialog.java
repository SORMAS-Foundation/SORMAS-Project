package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;

public class ConfirmationInputDialog extends AbstractDialog {

    public static final String TAG = ConfirmationInputDialog.class.getSimpleName();

    private final String wordToType;

    // Constructors

    public ConfirmationInputDialog(final FragmentActivity activity, int headingResId, int subHeadingResId, String wordToType) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_confirmation_input_layout,
                R.layout.dialog_root_two_button_panel_layout, headingResId, subHeadingResId);

        this.wordToType = wordToType;
        getConfig().setHideHeadlineSeparator(true);
    }

    public ConfirmationInputDialog(final FragmentActivity activity, int headingResId, int subHeadingResId,
                                   int positiveButtonTextResId, int negativeButtonTextResId, String wordToType) {
        this(activity, headingResId, subHeadingResId, wordToType);

        Resources resources = getContext().getResources();
        if (positiveButtonTextResId >= 0) {
            getConfig().setPositiveButtonText(resources.getString(positiveButtonTextResId));
        } else {
            getConfig().setPositiveButtonText(resources.getString(R.string.action_confirm));
        }
        if (negativeButtonTextResId >= 0) {
            getConfig().setNegativeButtonText(resources.getString(negativeButtonTextResId));
        } else {
            getConfig().setNegativeButtonText(resources.getString(R.string.action_dismiss));
        }
    }

    // Overrides

    @Override
    protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
        // Data variable is not needed in this dialog
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
        getPositiveButton().setEnabled(false);

        ((ControlTextEditField) getRoot().findViewById(R.id.confirmation_input)).addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                getPositiveButton().setEnabled(field.getValue().equals(wordToType));
            }
        });
    }

    @Override
    public boolean isHeadingCentered() {
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

}
