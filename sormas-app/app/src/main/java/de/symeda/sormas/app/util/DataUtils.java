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

    public static <E> List<Item> toItems(List<E> listIn) {
        return toItems(listIn, true);
    }

    public static <E> List<Item> toItems(List<E> listIn, boolean withNull) {
        List<Item> listOut = new ArrayList<>();
        if (withNull) {
            listOut.add(new Item<E>("", null));
        }
        for (E listInEntry : listIn) {
            listOut.add(new Item<E>(String.valueOf(listInEntry), listInEntry));
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

}


