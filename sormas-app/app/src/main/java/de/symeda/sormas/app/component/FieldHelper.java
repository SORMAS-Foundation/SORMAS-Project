package de.symeda.sormas.app.component;

import android.widget.AdapterView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.FacilityDao;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.region.CommunityDao;
import de.symeda.sormas.app.backend.region.DistrictDao;
import de.symeda.sormas.app.backend.region.RegionDao;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.Item;

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

    public static SpinnerField initOnsetSymptomSpinnerField(SpinnerField spinnerField, List<Item> items) {
        spinnerField.initializeForOnsetSymptom(items);
        return spinnerField;
    }

    public static SpinnerField initMonthSpinnerField(SpinnerField spinnerField, List<Item> items, final AdapterView.OnItemSelectedListener ...moreListeners) {
        spinnerField.initializeForMonth(items, moreListeners);
        return spinnerField;
    }

    public static SpinnerField initRegionSpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        RegionDao regionDao = DatabaseHelper.getRegionDao();
        List<Item> items = DataUtils.toItems(regionDao.queryForAll());
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }
}
