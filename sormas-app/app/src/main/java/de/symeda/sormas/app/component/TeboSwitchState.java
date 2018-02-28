package de.symeda.sormas.app.component;

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

    private static Drawable createDrawable(TeboSwitch.ButtonPosition last, int backgroundColor) {
        if (last == TeboSwitch.ButtonPosition.LAST) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(DRAWABLE_SHAPE);
            drawable.setColor(backgroundColor);

            return drawable;
        }

        GradientDrawable innerDrawable = new GradientDrawable();
        innerDrawable.setShape(DRAWABLE_SHAPE);
        innerDrawable.setColor(backgroundColor);
        innerDrawable.setStroke(BUTTON_DIVIDER_WIDTH, STROKE_COLOR);

        LayerDrawable drawable = new LayerDrawable(new Drawable[] {innerDrawable});
        drawable.setLayerInset(0, LAST_BG_INSET_LEFT, LAST_BG_INSET_TOP,
                LAST_BG_INSET_RIGHT, LAST_BG_INSET_BOTTOM);

        return drawable;

    }

    public Drawable getDefaultDrawable() {
        return getDrawable(TeboSwitch.ButtonPosition.NOT_LAST);
    }

    public Drawable getDrawableForLastPosition() {
        return getDrawable(TeboSwitch.ButtonPosition.LAST);
    }

    public abstract Drawable getDrawable(TeboSwitch.ButtonPosition last);

    private static class NormalState extends TeboSwitchState {

        public NormalState() {
            super(0, "Normal");
        }

        @Override
        public Drawable getDrawable(TeboSwitch.ButtonPosition last) {
            return createDrawable(last, SormasColor.SWITCH_UNCHECKED_BG);
        }
    }

    private static class PressedState extends TeboSwitchState {

        public PressedState() {
            super(1, "Pressed");
        }

        @Override
        public Drawable getDrawable(TeboSwitch.ButtonPosition last) {
            return createDrawable(last, SormasColor.SWITCH_UNCHECKED_BG);
        }
    }

    private static class CheckedState extends TeboSwitchState {

        public CheckedState() {
            super(2, "Checked");
        }

        @Override
        public Drawable getDrawable(TeboSwitch.ButtonPosition last) {
            return createDrawable(last, SormasColor.SWITCH_CHECKED_BG);
        }
    }


}
