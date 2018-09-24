package de.symeda.sormas.app.component.controls;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;

import de.symeda.sormas.app.R;

public enum ControlButtonType {

    PRIMARY(ControlButtonSubType.NORMAL, R.color.primaryButton, R.color.dangerButtonFocused,
            R.color.dangerButtonPressed, R.color.primaryButtonText),
    SECONDARY(ControlButtonSubType.NORMAL, R.color.secondaryButton, R.color.secondaryButtonFocused,
            R.color.secondaryButtonPressed, R.color.secondaryButtonText),
    SUCCESS(ControlButtonSubType.NORMAL, R.color.successButton, R.color.successButtonFocused,
            R.color.successButtonPressed, R.color.successButtonText),
    WARNING(ControlButtonSubType.NORMAL, R.color.warningButton, R.color.warningButtonFocused,
            R.color.warningButtonPressed, R.color.warningButtonText),
    DANGER(ControlButtonSubType.NORMAL, R.color.dangerButton, R.color.dangerButtonFocused,
            R.color.dangerButtonPressed, R.color.dangerButtonText),
    INVERSE_PRIMARY(ControlButtonSubType.INVERSE, R.color.primaryInverseButton, R.color.primaryInverseButtonFocused,
            R.color.primaryInverseButtonPressed, R.color.primaryInverseButtonText),
    INVERSE_SECONDARY(ControlButtonSubType.INVERSE, R.color.secondaryInverseButton, R.color.secondaryInverseButtonFocused,
            R.color.secondaryInverseButtonPressed, R.color.secondaryInverseButtonText),
    INVERSE_SUCCESS(ControlButtonSubType.INVERSE, R.color.successInverseButton, R.color.successInverseButtonFocused,
            R.color.successInverseButtonPressed, R.color.successInverseButtonText),
    INVERSE_WARNING(ControlButtonSubType.INVERSE, R.color.warningInverseButton, R.color.warningInverseButtonFocused,
            R.color.warningInverseButtonPressed, R.color.warningInverseButtonText),
    INVERSE_DANGER(ControlButtonSubType.INVERSE, R.color.dangerInverseButton, R.color.dangerInverseButtonFocused,
            R.color.dangerInverseButtonPressed, R.color.dangerInverseButtonText),
    LINE_PRIMARY(ControlButtonSubType.LINE, R.color.primaryButton, R.color.primaryButtonFocused,
            R.color.primaryButtonPressed, R.color.primaryLineButtonText),
    LINE_SECONDARY(ControlButtonSubType.LINE, R.color.secondaryButton, R.color.secondaryButtonFocused,
            R.color.secondaryButtonPressed, R.color.secondaryLineButtonText),
    LINE_SUCCESS(ControlButtonSubType.LINE, R.color.successButton, R.color.successButtonFocused,
            R.color.successButtonPressed, R.color.successLineButtonText),
    LINE_WARNING(ControlButtonSubType.LINE, R.color.warningButton, R.color.warningButtonFocused,
            R.color.warningButtonPressed, R.color.warningLineButtonText),
    LINE_DANGER(ControlButtonSubType.LINE, R.color.dangerButton, R.color.dangerButtonFocused,
            R.color.dangerButtonPressed, R.color.dangerLineButtonText);

    private ControlButtonSubType subType;
    private int buttonColorNormal;
    private int buttonColorFocused;
    private int buttonColorPressed;
    private int textColor;

    ControlButtonType(ControlButtonSubType subType, int buttonColorNormal, int buttonColorFocused, int buttonColorPressed, int textColor) {
        this.subType = subType;
        this.buttonColorNormal = buttonColorNormal;
        this.buttonColorFocused = buttonColorFocused;
        this.buttonColorPressed = buttonColorPressed;
        this.textColor = textColor;
    }

    public int getTextColor() {
        return textColor;
    }

    private int getButtonColor(ControlButtonState buttonState) {
        switch (buttonState) {
            case NORMAL:
                return buttonColorNormal;
            case FOCUSED:
                return buttonColorFocused;
            case PRESSED:
                return buttonColorPressed;
            default:
                throw new IllegalArgumentException(buttonState.toString());
        }
    }

    public GradientDrawable getDrawable(ControlButtonState buttonState, boolean rounded, Resources resources) {
        int color = resources.getColor(getButtonColor(buttonState));

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);

        if (this.subType != ControlButtonSubType.INVERSE) {
            drawable.setStroke((int) resources.getDimension(R.dimen.defaultButtonStroke), color);
        }

        if (this.subType != ControlButtonSubType.LINE) {
            drawable.setColor(color);
        }

        drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

        if (rounded) {
            drawable.setCornerRadius(resources.getDimension(R.dimen.roundButtonRadius));
        }

        return drawable;
    }

    private enum ControlButtonSubType {
        NORMAL,
        INVERSE,
        LINE;
    }

}
