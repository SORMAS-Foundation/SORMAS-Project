package de.symeda.sormas.app.component;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import de.symeda.sormas.app.util.SormasColor;

/**
 * Created by Orson on 06/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class TeboSwitchState {

    private final int value;
    private final String displayName;

    public static final TeboSwitchState NORMAL = new NormalState();
    public static final TeboSwitchState PRESSED = new PressedState();
    public static final TeboSwitchState CHECKED = new CheckedState();
    public static final TeboSwitchState DISABLED = new DisabledState();

    private static final int DRAWABLE_SHAPE = GradientDrawable.RECTANGLE;
    private static final int STROKE_COLOR = SormasColor.SWITCH_CHECKED_BG;
    private static final int LAST_BG_INSET_LEFT = -1;
    private static final int LAST_BG_INSET_TOP = -1;
    private static final int LAST_BG_INSET_BOTTOM = -1;
    private static final int LAST_BG_INSET_RIGHT = 0;

    private static final int BUTTON_DIVIDER_WIDTH = 1;


    protected TeboSwitchState(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    private static Drawable createDrawable(TeboSwitch.ButtonPosition last, int backgroundColor, boolean errorState, int backgroundColorError) {
        if (last == TeboSwitch.ButtonPosition.LAST) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(DRAWABLE_SHAPE);
            drawable.setColor((errorState)? backgroundColorError : backgroundColor);

            return drawable;
        }

        GradientDrawable innerDrawable = new GradientDrawable();
        innerDrawable.setShape(DRAWABLE_SHAPE);
        innerDrawable.setColor((errorState)? backgroundColorError : backgroundColor);
        //innerDrawable.setStroke(BUTTON_DIVIDER_WIDTH, STROKE_COLOR); //
        //(errorState)? backgroundColorError : backgroundColor
        innerDrawable.setStroke(BUTTON_DIVIDER_WIDTH, (errorState)? getStrokeButtonDividerError() : getStrokeButtonDivider());

        LayerDrawable drawable = new LayerDrawable(new Drawable[] {innerDrawable});
        drawable.setLayerInset(0, LAST_BG_INSET_LEFT, LAST_BG_INSET_TOP,
                LAST_BG_INSET_RIGHT, LAST_BG_INSET_BOTTOM);

        return drawable;

    }

    private static ColorStateList getStrokeButtonDividerError() {
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked, -android.R.attr.state_enabled, -android.R.attr.state_checkable, -android.R.attr.state_focused},
                new int[] {-android.R.attr.state_enabled},
                new int[] {android.R.attr.state_enabled},
        };

        int[] thumbColors = new int[] {
                SormasColor.SWITCH_CHECKED_DISABLED_BG,
                SormasColor.SWITCH_CHECKED_DISABLED_BG,
                SormasColor.WATCHOUT,
        };

        return new ColorStateList(states, thumbColors);
    }

    private static ColorStateList getStrokeButtonDivider() {
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked, -android.R.attr.state_enabled, -android.R.attr.state_checkable, -android.R.attr.state_focused},
                new int[] {-android.R.attr.state_enabled},
                new int[] {android.R.attr.state_enabled},
        };

        int[] thumbColors = new int[] {
                SormasColor.SWITCH_CHECKED_DISABLED_BG,
                SormasColor.SWITCH_CHECKED_DISABLED_BG,
                STROKE_COLOR,
        };

        return new ColorStateList(states, thumbColors);
    }

    public Drawable getDefaultDrawable(boolean errorState) {
        return getDrawable(TeboSwitch.ButtonPosition.NOT_LAST, errorState);
    }

    public Drawable getDrawableForLastPosition(boolean errorState) {
        return getDrawable(TeboSwitch.ButtonPosition.LAST, errorState);
    }

    public abstract Drawable getDrawable(TeboSwitch.ButtonPosition last, boolean errorState);

    private static class NormalState extends TeboSwitchState {

        public NormalState() {
            super(0, "Normal");
        }

        @Override
        public Drawable getDrawable(TeboSwitch.ButtonPosition last, boolean errorState) {
            return createDrawable(last, SormasColor.SWITCH_UNCHECKED_BG, errorState, SormasColor.SWITCH_UNCHECKED_BG);
        }
    }

    private static class PressedState extends TeboSwitchState {

        public PressedState() {
            super(1, "Pressed");
        }

        @Override
        public Drawable getDrawable(TeboSwitch.ButtonPosition last, boolean errorState) {
            return createDrawable(last, SormasColor.SWITCH_UNCHECKED_BG, errorState, SormasColor.SWITCH_UNCHECKED_BG);
        }
    }

    private static class CheckedState extends TeboSwitchState {

        public CheckedState() {
            super(2, "Checked");
        }

        @Override
        public Drawable getDrawable(TeboSwitch.ButtonPosition last, boolean errorState) {
            return createDrawable(last, SormasColor.SWITCH_CHECKED_BG, errorState, SormasColor.WATCHOUT);
        }
    }

    private static class DisabledState extends TeboSwitchState {

        public DisabledState() {
            super(3, "Disabled");
        }

        @Override
        public Drawable getDrawable(TeboSwitch.ButtonPosition last, boolean errorState) {
            return createDrawable(last, SormasColor.SWITCH_CHECKED_DISABLED_BG, false, SormasColor.SWITCH_CHECKED_DISABLED_BG);
        }
    }


}
