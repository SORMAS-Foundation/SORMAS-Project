package de.symeda.sormas.app.component;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 03/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class TeboButtonType {

    private final int value;
    private final String displayName;

    public static final TeboButtonType BTN_PRIMARY = new PrimaryButton();
    public static final TeboButtonType BTN_SECONDARY = new SecondaryButton();
    public static final TeboButtonType BTN_SUCCESS = new SuccessButton();
    public static final TeboButtonType BTN_WARNING = new WarningButton();
    public static final TeboButtonType BTN_DANGER = new DangerButton();
    public static final TeboButtonType BTN_INVERSE_PRIMARY = new PrimaryInverseButton();
    public static final TeboButtonType BTN_INVERSE_SECONDARY = new SecondaryInverseButton();
    public static final TeboButtonType BTN_INVERSE_SUCCESS = new SuccessInverseButton();
    public static final TeboButtonType BTN_INVERSE_WARNING = new WarningInverseButton();
    public static final TeboButtonType BTN_INVERSE_DANGER = new DangerInverseButton();
    public static final TeboButtonType BTN_LINE_PRIMARY = new PrimaryLineButton();
    public static final TeboButtonType BTN_LINE_SECONDARY = new SecondaryLineButton();
    public static final TeboButtonType BTN_LINE_SUCCESS = new SuccessLineButton();
    public static final TeboButtonType BTN_LINE_WARNING = new WarningLineButton();
    public static final TeboButtonType BTN_LINE_DANGER = new DangerLineButton();

    protected TeboButtonType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public static TeboButtonType getButtonType(int buttonTypeValue) {
        if (buttonTypeValue == TeboButtonType.BTN_PRIMARY.value) {
            return BTN_PRIMARY;
        } else if (buttonTypeValue == TeboButtonType.BTN_SECONDARY.value) {
            return BTN_SECONDARY;
        } else if (buttonTypeValue == TeboButtonType.BTN_WARNING.value) {
            return BTN_WARNING;
        } else if (buttonTypeValue == TeboButtonType.BTN_DANGER.value) {
            return BTN_DANGER;
        } else if (buttonTypeValue == TeboButtonType.BTN_SUCCESS.value) {
            return BTN_SUCCESS;
        } else if (buttonTypeValue == TeboButtonType.BTN_INVERSE_PRIMARY.value) {
            return BTN_INVERSE_PRIMARY;
        } else if (buttonTypeValue == TeboButtonType.BTN_INVERSE_SECONDARY.value) {
            return BTN_INVERSE_SECONDARY;
        } else if (buttonTypeValue == TeboButtonType.BTN_INVERSE_WARNING.value) {
            return BTN_INVERSE_WARNING;
        } else if (buttonTypeValue == TeboButtonType.BTN_INVERSE_DANGER.value) {
            return BTN_INVERSE_DANGER;
        } else if (buttonTypeValue == TeboButtonType.BTN_INVERSE_SUCCESS.value) {
            return BTN_INVERSE_SUCCESS;
        } else if (buttonTypeValue == TeboButtonType.BTN_LINE_PRIMARY.value) {
            return BTN_LINE_PRIMARY;
        } else if (buttonTypeValue == TeboButtonType.BTN_LINE_SECONDARY.value) {
            return BTN_LINE_SECONDARY;
        } else if (buttonTypeValue == TeboButtonType.BTN_LINE_WARNING.value) {
            return BTN_LINE_WARNING;
        } else if (buttonTypeValue == TeboButtonType.BTN_LINE_DANGER.value) {
            return BTN_LINE_DANGER;
        } else if (buttonTypeValue == TeboButtonType.BTN_LINE_SUCCESS.value) {
            return BTN_LINE_SUCCESS;
        }else {
            return null;
        }
    }

    public static float getHeight(Resources resources, boolean slim) {
        if (slim) {
            return resources.getDimension(R.dimen.slimButtonHeight);
        } else {
            return resources.getDimension(R.dimen.primaryButtonHeight);
        }
    }

    public static float getTextSize(Resources resources, boolean slim) {
        if (slim) {
            return resources.getDimension(R.dimen.slimControlTextSize);
        } else {
            return resources.getDimension(R.dimen.buttonTextSize);
        }

    }

    public static int getHorizontalPadding(Resources resources, boolean slim, boolean iconOnly) {
        if (iconOnly) {
            return resources.getDimensionPixelSize(R.dimen.iconOnlyButtonHorizontalPadding);
        } else if (slim) {
            return resources.getDimensionPixelSize(R.dimen.slimButtonHorizontalPadding);
        } else {
            return resources.getDimensionPixelSize(R.dimen.buttonHorizontalPadding);
        }
        //buttonHorizontalPadding
    }

    public static int getVerticalPadding(Resources resources, boolean slim) {
        if (slim) {
            return resources.getDimensionPixelSize(R.dimen.slimButtonVerticalPadding);
        } else {
            return resources.getDimensionPixelSize(R.dimen.buttonVerticalPadding);
        }
    }

    public static int getDrawablePadding(Resources resources, boolean slim, boolean iconOnly) {
        if (iconOnly) {
            return 0;
        } else if (slim) {
            return resources.getDimensionPixelSize(R.dimen.contentHorizontalSpacing);
        } else {
            return resources.getDimensionPixelSize(R.dimen.contentHorizontalSpacing);
        }
    }

    public abstract int getTextColor();

    public abstract int getBackgroundResource();

    public abstract int getDrawableTint();

    public abstract IMakeButtonRounded getNormalStateDrawable(Resources resources);

    public abstract IMakeButtonRounded getFocusedStateDrawable(Resources resources);

    public abstract IMakeButtonRounded getPressedStateDrawable(Resources resources);

    private static class PrimaryButton extends TeboButtonType
    {
        public PrimaryButton() {
            super(0, "Primary");
        }

        @Override
        public int getTextColor() {
            return R.color.primaryButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_primary;
        }

        @Override
        public int getDrawableTint() {
            return R.color.primaryButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.primaryButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.primaryButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.primaryButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class SecondaryButton extends TeboButtonType
    {
        public SecondaryButton() {
            super(1, "Secondary");
        }

        @Override
        public int getTextColor() {
            return R.color.secondaryButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_secondary;
        }

        @Override
        public int getDrawableTint() {
            return R.color.secondaryButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.secondaryButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.secondaryButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.secondaryButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class SuccessButton extends TeboButtonType
    {
        public SuccessButton() {
            super(2, "Success");
        }

        @Override
        public int getTextColor() {
            return R.color.successButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_success;
        }

        @Override
        public int getDrawableTint() {
            return R.color.successButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.successButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.successButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.successButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class WarningButton extends TeboButtonType
    {
        public WarningButton() {
            super(3, "Warning");
        }

        @Override
        public int getTextColor() {
            return R.color.warningButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_warning;
        }

        @Override
        public int getDrawableTint() {
            return R.color.warningButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.warningButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.warningButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.warningButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class DangerButton extends TeboButtonType
    {
        public DangerButton() {
            super(4, "Danger");
        }

        @Override
        public int getTextColor() {
            return R.color.dangerButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_danger;
        }

        @Override
        public int getDrawableTint() {
            return R.color.dangerButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.dangerButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.dangerButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.dangerButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class PrimaryInverseButton extends TeboButtonType
    {
        public PrimaryInverseButton() {
            super(5, "Primary Inverse");
        }

        @Override
        public int getTextColor() {
            return R.color.primaryInverseButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_inverse_primary;
        }

        @Override
        public int getDrawableTint() {
            return R.color.primaryInverseButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.primaryInverseButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.primaryInverseButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.primaryInverseButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class SecondaryInverseButton extends TeboButtonType
    {
        public SecondaryInverseButton() {
            super(6, "Secondary Inverse");
        }

        @Override
        public int getTextColor() {
            return R.color.secondaryInverseButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_inverse_secondary;
        }

        @Override
        public int getDrawableTint() {
            return R.color.secondaryInverseButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.secondaryInverseButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.secondaryInverseButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.secondaryInverseButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class SuccessInverseButton extends TeboButtonType
    {
        public SuccessInverseButton() {
            super(7, "Success Inverse");
        }

        @Override
        public int getTextColor() {
            return R.color.successInverseButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_inverse_success;
        }

        @Override
        public int getDrawableTint() {
            return R.color.successInverseButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.successInverseButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.successInverseButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.successInverseButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class WarningInverseButton extends TeboButtonType
    {
        public WarningInverseButton() {
            super(8, "Warning Inverse");
        }

        @Override
        public int getTextColor() {
            return R.color.warningInverseButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_inverse_warning;
        }

        @Override
        public int getDrawableTint() {
            return R.color.warningInverseButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.warningInverseButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.warningInverseButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.warningInverseButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class DangerInverseButton extends TeboButtonType
    {
        public DangerInverseButton() {
            super(9, "Danger Inverse");
        }

        @Override
        public int getTextColor() {
            return R.color.dangerInverseButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_inverse_danger;
        }

        @Override
        public int getDrawableTint() {
            return R.color.dangerInverseButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.dangerInverseButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.dangerInverseButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.dangerInverseButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            //drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class PrimaryLineButton extends TeboButtonType
    {
        public PrimaryLineButton() {
            super(10, "Primary Line");
        }

        @Override
        public int getTextColor() {
            return R.color.primaryLineButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_line_primary;
        }

        @Override
        public int getDrawableTint() {
            return R.color.primaryLineButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.primaryButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.primaryButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.primaryButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class SecondaryLineButton extends TeboButtonType
    {
        public SecondaryLineButton() {
            super(11, "Secondary Line");
        }

        @Override
        public int getTextColor() {
            return R.color.secondaryLineButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_line_secondary;
        }

        @Override
        public int getDrawableTint() {
            return R.color.secondaryLineButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.secondaryButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.secondaryButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.secondaryButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class SuccessLineButton extends TeboButtonType
    {
        public SuccessLineButton() {
            super(12, "Success Line");
        }

        @Override
        public int getTextColor() {
            return R.color.successLineButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_line_success;
        }

        @Override
        public int getDrawableTint() {
            return R.color.successLineButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.successButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.successButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.successButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class WarningLineButton extends TeboButtonType
    {
        public WarningLineButton() {
            super(13, "Warning Line");
        }

        @Override
        public int getTextColor() {
            return R.color.warningLineButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_line_warning;
        }

        @Override
        public int getDrawableTint() {
            return R.color.warningLineButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.warningButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.warningButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.warningButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }

    private static class DangerLineButton extends TeboButtonType
    {
        public DangerLineButton() {
            super(14, "Danger Line");
        }

        @Override
        public int getTextColor() {
            return R.color.dangerLineButtonText;
        }

        @Override
        public int getBackgroundResource() {
            return R.drawable.selector_button_line_danger;
        }

        @Override
        public int getDrawableTint() {
            return R.color.dangerLineButtonText;
        }

        @Override
        public IMakeButtonRounded getNormalStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.dangerButton);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getFocusedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.dangerButtonFocused);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }

        @Override
        public IMakeButtonRounded getPressedStateDrawable(Resources resources) {
            int color = resources.getColor(R.color.dangerButtonPressed);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke((int)resources.getDimension(R.dimen.defaultButtonStroke), color);
            //drawable.setColor(color);
            drawable.setCornerRadius(resources.getDimension(R.dimen.defaultButtonRadius));

            return new MakeButtonRounded(resources, drawable);
        }
    }


    public interface IMakeButtonRounded {
        GradientDrawable makeRounded(boolean rounded);
    }

    private class MakeButtonRounded implements IMakeButtonRounded {

        private GradientDrawable drawable;
        private Resources resources;

        public MakeButtonRounded(Resources resources, GradientDrawable drawable) {
            this.drawable = drawable;
            this.resources = resources;
        }

        @Override
        public GradientDrawable makeRounded(boolean rounded) {
            if (rounded) {
                this.drawable.setCornerRadius(this.resources.getDimension(R.dimen.roundButtonRadius));
                this.drawable.setCornerRadius(this.resources.getDimension(R.dimen.roundButtonRadius));
                this.drawable.setCornerRadius(this.resources.getDimension(R.dimen.roundButtonRadius));
            }

            return this.drawable;
        }
    }

    @Override
    public int hashCode() {
        return value + 37 * value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TeboButtonType)) {
            return false;
        }
        TeboButtonType other = (TeboButtonType)obj;
        return value==other.value;
    }

    @Override
    public String toString() {
        return displayName;
    }
}


