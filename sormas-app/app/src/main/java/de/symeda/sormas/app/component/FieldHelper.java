package de.symeda.sormas.app.component;

import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDao;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Mate Strysewske on 13.12.2016.
 */

public class FieldHelper {

    /**
     * Fill the spinner for the given enum, set the selected entry, register the base listeners and the given ones.
     * @param enumClass
     */
    public static SpinnerField initSpinnerField(SpinnerField spinnerField, Class enumClass, final AdapterView.OnItemSelectedListener ...moreListeners) {
        List<Item> items = DataUtils.getEnumItems(enumClass);
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

    /**
     * Fill the spinner for the given list, set the selected entry, register the base listeners and the given ones.
     */
    public static SpinnerField initSpinnerField(SpinnerField spinnerField, List<Item> items, final AdapterView.OnItemSelectedListener ...moreListeners) {
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

    public static SpinnerField initRegionSpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        RegionDao regionDao = DatabaseHelper.getRegionDao();
        List<Item> items = DataUtils.toItems(regionDao.queryForAll(Region.NAME, true), false);
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }
}
