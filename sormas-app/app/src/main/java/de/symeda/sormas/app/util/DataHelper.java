package de.symeda.sormas.app.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan Szczesny on 02.08.2016.
 */
public class DataHelper {

    public static <E>  List<Item> getEnumItems(Class<E> clazz) {

        E[] enumConstants = clazz.getEnumConstants();
        List<Item> list = new ArrayList<Item>();
        list.add(new Item<E>("",null));
        for (int i = 0; i < enumConstants.length; i++) {
            list.add(new Item<E>(enumConstants[i].toString(),enumConstants[i]));
        }
        return list;
    }



}


