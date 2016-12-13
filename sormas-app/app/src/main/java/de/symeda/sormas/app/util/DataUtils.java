package de.symeda.sormas.app.util;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.FacilityDao;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.region.CommunityDao;
import de.symeda.sormas.app.backend.region.DistrictDao;
import de.symeda.sormas.app.backend.region.RegionDao;
import de.symeda.sormas.app.component.DateField;
import de.symeda.sormas.app.component.SpinnerField;

/**
 * Created by Stefan Szczesny on 02.08.2016.
 */
public class DataUtils {

    /**
     * @return null or object.toString()
     */
    public static String toString(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }

    public static <E>  List<Item> getEnumItems(Class<E> clazz) {

        E[] enumConstants = clazz.getEnumConstants();
        List<Item> list = new ArrayList<Item>();
        list.add(new Item<E>("",null));
        for (int i = 0; i < enumConstants.length; i++) {
            list.add(new Item<E>(enumConstants[i].toString(),enumConstants[i]));
        }
        return list;
    }

    public static <E>  List<Item> getItems(List<E> listIn) {
        return getItems(listIn, true);
    }

    public static <E>  List<Item> getItems(List<E> listIn, boolean withNull) {
        List<Item> listOut = new ArrayList<Item>();
        if(withNull) {
            listOut.add(new Item<E>("",null));
        }
        for (E listInEntry: listIn) {
            listOut.add(new Item<E>(listInEntry.toString(),listInEntry));
        }
        return listOut;
    }

    public static <E>  List<Item> addItems(List<Item> items, List<E> listIn) {
        for (E listInEntry: listIn) {
            items.add(new Item<E>(listInEntry.toString(),listInEntry));
        }
        return items;
    }

    public static <E extends AbstractDomainObject> E createNew(Class<E> clazz) throws IllegalAccessException, InstantiationException {
        E e = clazz.newInstance();
        e.setUuid(DataHelper.createUuid());
        Date now = new Date();
        e.setCreationDate(now);
        e.setChangeDate(now);
        return e;
    }

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
     * TODO #9 remove
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
        List<Item> items = DataUtils.getItems(facilityDao.queryForAll());
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

    public static SpinnerField initRegionSpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        RegionDao regionDao = DatabaseHelper.getRegionDao();
        List<Item> items = DataUtils.getItems(regionDao.queryForAll());
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

    public static SpinnerField initDistrictSpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        DistrictDao districtDao = DatabaseHelper.getDistrictDao();
        List<Item> items = DataUtils.getItems(districtDao.queryForAll());
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

    public static SpinnerField initCommunitySpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        CommunityDao communityDao = DatabaseHelper.getCommunityDao();
        List<Item> items = DataUtils.getItems(communityDao.queryForAll());
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

    public static SpinnerField initPersonSpinnerField(SpinnerField spinnerField, final AdapterView.OnItemSelectedListener ...moreListeners) {
        PersonDao personDao = DatabaseHelper.getPersonDao();
        List<Item> items = null;
        try {
            items = DataUtils.getItems(personDao.getAllPersonsWithoutCase());
        } catch (SQLException e) {
            Toast.makeText(spinnerField.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        spinnerField.initialize(items, moreListeners);
        return spinnerField;
    }

}


