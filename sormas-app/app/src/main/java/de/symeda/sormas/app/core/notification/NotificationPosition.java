package de.symeda.sormas.app.core.notification;

/**
 * Created by Orson on 02/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class NotificationPosition {
    private final int value;
    private final String displayName;


    public static final NotificationPosition TOP = new NotificationTop();
    public static final NotificationPosition BOTTOM = new NotificationBottom();

    protected NotificationPosition(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    private static class NotificationTop extends NotificationPosition
    {
        public NotificationTop() {
            super(0, "Top");
        }
    }

    private static class NotificationBottom extends NotificationPosition
    {
        public NotificationBottom() {
            super(1, "Bottom");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Overrides">

    @Override
    public int hashCode() {
        return value + 37 * value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NotificationPosition)) {
            return false;
        }
        NotificationPosition other = (NotificationPosition) obj;
        return value == other.value;
    }

    @Override
    public String toString() {
        return displayName;
    }

    // </editor-fold>
}
