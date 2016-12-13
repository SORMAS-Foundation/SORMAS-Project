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

    /**
     * Fill the spinner for facility selection.
     * See {@see addSpinnerField()}
     * @param moreListeners
     */
    public static SpinnerField initFacilitySpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        FacilityDao facilityDao = DatabaseHelper.getFacilityDao();
        List<Item> items = DataUtils.toItems(facilityDao.queryForAll());
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

    public static SpinnerField initRegionSpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        RegionDao regionDao = DatabaseHelper.getRegionDao();
        List<Item> items = DataUtils.toItems(regionDao.queryForAll());
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

    public static SpinnerField initDistrictSpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        DistrictDao districtDao = DatabaseHelper.getDistrictDao();
        List<Item> items = DataUtils.toItems(districtDao.queryForAll());
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

    public static SpinnerField initCommunitySpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        CommunityDao communityDao = DatabaseHelper.getCommunityDao();
        List<Item> items = DataUtils.toItems(communityDao.queryForAll());
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

    public static SpinnerField initPersonSpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        PersonDao personDao = DatabaseHelper.getPersonDao();
        List<Item> items = null;
        try {
            items = DataUtils.toItems(personDao.getAllPersonsWithoutCase());
        } catch (SQLException e) {
            Toast.makeText(spinnerField.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

}
