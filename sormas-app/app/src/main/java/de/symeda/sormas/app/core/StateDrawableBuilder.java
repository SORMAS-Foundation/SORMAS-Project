package de.symeda.sormas.app.core;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by Orson on 03/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class StateDrawableBuilder {
    private static final int[] STATE_SELECTED = new int[]{android.R.attr.state_selected};
    private static final int[] STATE_FOCUSED = new int[]{android.R.attr.state_focused};
    private static final int[] STATE_PRESSED = new int[]{android.R.attr.state_pressed};
    private static final int[] STATE_ENABLED = new int[]{android.R.attr.state_enabled};
    private static final int[] STATE_DISABED = new int[]{-android.R.attr.state_enabled};
    private static final int[] STATE_CHECKED = new int[]{android.R.attr.state_checked};
    private static final int[] STATE_CHECKED_AND_DISABLED = new int[]{android.R.attr.state_checked, -android.R.attr.state_enabled};



    StateListDrawable stateListDrawable = new StateListDrawable();

    private Drawable normalDrawable;
    private Drawable selectedDrawable;
    private Drawable focusedDrawable;
    private Drawable pressedDrawable;
    private Drawable disabledDrawable;
    private Drawable checkedDrawable;
    private Drawable checkedAndDisabledDrawable;

    public StateDrawableBuilder setNormalDrawable(Drawable normalDrawable) {
        this.normalDrawable = normalDrawable;

        if (this.normalDrawable != null) {
            stateListDrawable.addState(STATE_ENABLED, this.normalDrawable);
        }

        return this;
    }

    public StateDrawableBuilder setPressedDrawable(Drawable pressedDrawable) {
        this.pressedDrawable = pressedDrawable;

        if (this.pressedDrawable != null) {
            stateListDrawable.addState(STATE_PRESSED, this.pressedDrawable);
        }

        return this;
    }

    public StateDrawableBuilder setFocusedDrawable(Drawable focusedDrawable) {
        this.focusedDrawable = focusedDrawable;

        if (this.focusedDrawable != null) {
            stateListDrawable.addState(STATE_FOCUSED, this.focusedDrawable);
        }

        return this;
    }

    public StateDrawableBuilder setCheckedDrawable(Drawable checkedDrawable) {
        this.checkedDrawable = checkedDrawable;

        if (this.checkedDrawable != null) {
            stateListDrawable.addState(STATE_CHECKED, this.checkedDrawable);
        }

        return this;
    }

    public StateDrawableBuilder setCheckedAndDisabledDrawable(Drawable checkedAndDisabledDrawable) {
        this.checkedAndDisabledDrawable = checkedAndDisabledDrawable;

        if (this.checkedAndDisabledDrawable != null) {
            stateListDrawable.addState(STATE_CHECKED_AND_DISABLED, this.checkedAndDisabledDrawable);
        }

        return this;
    }

    public StateDrawableBuilder setSelectedDrawable(Drawable selectedDrawable) {
        this.selectedDrawable = selectedDrawable;

        if (this.selectedDrawable != null) {
            stateListDrawable.addState(STATE_SELECTED, this.selectedDrawable);
        }

        return this;
    }

    public StateDrawableBuilder setDisabledDrawable(Drawable disabledDrawable) {
        this.disabledDrawable = disabledDrawable;

        if (this.disabledDrawable != null) {
            stateListDrawable.addState(STATE_DISABED, this.disabledDrawable);
        }

        return this;
    }

    public StateListDrawable build() {
        return stateListDrawable;
    }
}
