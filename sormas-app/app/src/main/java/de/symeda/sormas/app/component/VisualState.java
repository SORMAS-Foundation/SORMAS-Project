package de.symeda.sormas.app.component;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 14/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

//TODO: Redesign this class
public abstract class VisualState {
    private final int value;
    private final String displayName;

    public static final VisualState NORMAL = new NormalState();
    public static final VisualState FOCUSED = new FocusedState();
    public static final VisualState ENABLED = new EnabledState();
    public static final VisualState DISABLED = new DisabledState();
    public static final VisualState ERROR = new ErrorState();

    protected VisualState(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public abstract int getBackground(VisualStateControl control);

    public abstract int getLabelColor(VisualStateControl control);

    public abstract int getTextColor(VisualStateControl control);

    public abstract int getHintColor(VisualStateControl control);

    private static class NormalState extends VisualState
    {
        public NormalState() {
            super(0, "Normal");
        }

        @Override
        public int getBackground(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.drawable.selector_text_control_edit;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.drawable.selector_text_control_edit;
            } else if (control == VisualStateControl.SPINNER) {
                return R.drawable.selector_spinner;
            } else if (control == VisualStateControl.SWITCH) {
                return R.drawable.control_switch_background_border;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getLabelColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.controlLabelColor;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.controlLabelColor;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.controlLabelColor;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.controlLabelColor;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getTextColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.controlTextColor;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getHintColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.controlTextViewHint;
            }

            throw new UnsupportedOperationException();
        }
    }

    private static class FocusedState extends VisualState
    {
        public FocusedState() {
            super(1, "Focused");
        }

        @Override
        public int getBackground(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.drawable.selector_text_control_edit;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.drawable.selector_text_control_edit;
            } else if (control == VisualStateControl.SPINNER) {
                return R.drawable.selector_spinner;
            } else if (control == VisualStateControl.SWITCH) {
                return R.drawable.selector_text_control_edit;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getLabelColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.colorControlActivated;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.colorControlActivated;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.colorControlActivated;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.colorControlActivated;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getTextColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.controlTextColor;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getHintColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.controlTextViewHint;
            }

            throw new UnsupportedOperationException();
        }
    }

    private static class EnabledState extends VisualState
    {
        public EnabledState() {
            super(2, "Enabled");
        }

        @Override
        public int getBackground(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.drawable.selector_text_control_edit;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.drawable.selector_text_control_edit;
            } else if (control == VisualStateControl.SPINNER) {
                return R.drawable.selector_spinner;
            } else if (control == VisualStateControl.SWITCH) {
                return R.drawable.selector_text_control_edit;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getLabelColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.controlLabelColor;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.controlLabelColor;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.controlLabelColor;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.controlLabelColor;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getTextColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.controlTextColor;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getHintColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.controlTextViewHint;
            }

            throw new UnsupportedOperationException();
        }
    }

    private static class DisabledState extends VisualState
    {
        public DisabledState() {
            super(3, "Disabled");
        }

        @Override
        public int getBackground(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.drawable.selector_text_control_edit;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.drawable.selector_text_control_edit;
            } else if (control == VisualStateControl.SPINNER) {
                return R.drawable.selector_spinner;
            } else if (control == VisualStateControl.SWITCH) {
                return R.drawable.selector_text_control_edit;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getLabelColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.colorControlDisabled;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.colorControlDisabled;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.colorControlDisabled;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.colorControlDisabled;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getTextColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.colorControlDisabled;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.colorControlDisabled;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.colorControlDisabled;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.colorControlDisabled;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getHintColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.colorControlDisabledHint;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.colorControlDisabledHint;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.colorControlDisabledHint;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.colorControlDisabledHint;
            }

            throw new UnsupportedOperationException();
        }
    }

    private static class ErrorState extends VisualState
    {
        public ErrorState() {
            super(4, "Error");
        }

        @Override
        public int getBackground(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.drawable.selector_text_control_edit_error;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.drawable.selector_text_control_edit_error;
            } else if (control == VisualStateControl.SPINNER) {
                return R.drawable.selector_spinner_error;
            } else if (control == VisualStateControl.SWITCH) {
                return R.drawable.control_switch_background_border_error;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getLabelColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.colorControlError;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.colorControlError;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.colorControlError;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.colorControlError;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getTextColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.controlTextColor;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.controlTextColor;
            }

            throw new UnsupportedOperationException();
        }

        @Override
        public int getHintColor(VisualStateControl control) {
            if (control == VisualStateControl.EDIT_TEXT) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.CHECKBOX) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.SPINNER) {
                return R.color.controlTextViewHint;
            } else if (control == VisualStateControl.SWITCH) {
                return R.color.controlTextViewHint;
            }

            throw new UnsupportedOperationException();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Overrides">

    @Override
    public int hashCode() {
        return value + 37 * value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VisualState)) {
            return false;
        }
        VisualState other = (VisualState) obj;
        return value == other.value;
    }

    @Override
    public String toString() {
        return displayName;
    }

    // </editor-fold>
}
