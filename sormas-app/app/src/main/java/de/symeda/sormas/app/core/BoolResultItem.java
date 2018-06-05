package de.symeda.sormas.app.core;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class BoolResultItem extends BoolResult {

    private Object mItem;

    public static final BoolResultItem TRUE = new BoolResultItem(null, true, "");
    public static final BoolResultItem FALSE = new BoolResultItem(null, false, "");

    public BoolResultItem(Object item, boolean success, String message) {
        super(success, message);
        mItem = item;
    }

    public Object getItem() {
        return mItem;
    }
}
