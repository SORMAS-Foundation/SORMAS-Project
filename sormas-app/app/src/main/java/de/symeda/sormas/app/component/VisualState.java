package de.symeda.sormas.app.component;

import de.symeda.sormas.app.R;

public enum VisualState {

    NORMAL(R.drawable.selector_text_control_edit, R.drawable.selector_text_control_edit,
            R.drawable.selector_spinner, R.drawable.control_switch_background_border,
            R.color.controlLabelColor, R.color.controlTextColor, R.color.controlTextViewHint),
    FOCUSED(R.drawable.selector_text_control_edit, R.drawable.selector_text_control_edit,
            R.drawable.selector_spinner, R.drawable.selector_text_control_edit,
            R.color.colorControlActivated, R.color.controlTextColor, R.color.controlTextViewHint),
    DISABLED(R.drawable.selector_text_control_edit, R.drawable.selector_text_control_edit,
            R.drawable.selector_spinner, R.drawable.selector_text_control_edit,
            R.color.colorControlDisabled, R.color.colorControlDisabled, R.color.colorControlDisabledHint),
    ERROR(R.drawable.selector_text_control_edit_error, R.drawable.selector_text_control_edit_error,
            R.drawable.selector_spinner_error, R.drawable.control_switch_background_border_error,
            R.color.colorControlError, R.color.controlTextColor, R.color.controlTextViewHint);

    private int backgroundTextField;
    private int backgroundCheckbox;
    private int backgroundSpinner;
    private int backgroundSwitch;
    private int labelColor;
    private int textColor;
    private int hintColor;

    VisualState(int backgroundTextField, int backgroundCheckbox, int backgroundSpinner,
                int backgroundSwitch, int labelColor, int textColor, int hintColor) {
        this.backgroundTextField = backgroundTextField;
        this.backgroundCheckbox = backgroundCheckbox;
        this.backgroundSpinner = backgroundSpinner;
        this.backgroundSwitch = backgroundSwitch;
        this.labelColor = labelColor;
        this.textColor = textColor;
        this.hintColor = hintColor;
    }

    public int getBackground(VisualStateControlType controlType) {
        switch (controlType) {
            case TEXT_FIELD:
                return backgroundTextField;
            case CHECKBOX:
                return backgroundCheckbox;
            case SPINNER:
                return backgroundSpinner;
            case SWITCH:
                return backgroundSwitch;
            default:
                throw new IllegalArgumentException(controlType.toString());
        }
    }

    public int getLabelColor() {
        return labelColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getHintColor() {
        return hintColor;
    }

}