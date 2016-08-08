package de.symeda.sormas.app.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Stefan Szczesny on 02.08.2016.
 */
public class DataUtils {

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
        List<Item> listOut = new ArrayList<Item>();
        listOut.add(new Item<E>("",null));
        for (E listInEntry: listIn) {
            listOut.add(new Item<E>(listInEntry.toString(),listInEntry));
        }
        return listOut;
    }

    public static <E extends AbstractDomainObject> E createNew(Class<E> clazz) throws IllegalAccessException, InstantiationException {
        E e = clazz.newInstance();
        e.setUuid(DataHelper.createUuid());
        Date now = new Date();
        e.setCreationDate(now);
        e.setChangeDate(now);
        e.setLocalChangeDate(now);
        return e;
    }



}


