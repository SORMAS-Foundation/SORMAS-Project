/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.util;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;

/**
 * Created by Stefan Szczesny on 02.08.2016.
 */

public class DataUtils {

    public static <E> List<Item> getEnumItems(Class<E> clazz) {
        return getEnumItems(clazz, true);
    }

    public static <E> List<Item> getEnumItems(Class<E> clazz, boolean withNull) {
        E[] enumConstants = clazz.getEnumConstants();
        if (!clazz.isEnum()) {
            throw new IllegalArgumentException(clazz.toString() + " is not an enum");
        }
        List<Item> list = new ArrayList<Item>();

        if (withNull) {
            list.add(new Item<E>("", null));
        }

        for (int i = 0; i < enumConstants.length; i++) {
            list.add(new Item<E>(enumConstants[i].toString(),enumConstants[i]));
        }
        return list;
    }

    public static List<Item> getBooleanItems() {
        List<Item> list = new ArrayList<>();
        list.add(new Item<>(DatabaseHelper.getString(R.string.yes), Boolean.TRUE));
        list.add(new Item<>(DatabaseHelper.getString(R.string.no), Boolean.FALSE));
        return list;
    }

    public static <E> List<Item> toItems(List<E> listIn) {
        return toItems(listIn, true);
    }

    public static <E> Item toItem(E item) {
        return new Item<E>(item.toString(), item);
    }

    public static List<Item> getMonthItems() {
        List<Item> listOut = new ArrayList<>();
        listOut.add(new Item<Integer>("", null));
        for (Month month : Month.values()) {
            listOut.add(new Item<Integer>(I18nProperties.getEnumCaption(month), month.ordinal()));
        }
        return listOut;
    }

    public static List<Item> getMonthItems(boolean withNull) {
        List<Item> listOut = new ArrayList<>();
        if (withNull) {
            listOut.add(new Item<Integer>("", null));
        }
        for (Month month : Month.values()) {
            listOut.add(new Item<>(I18nProperties.getEnumCaption(month), month.ordinal() + 1));
        }
        return listOut;
    }

    public static <E> List<Item> toItems(List<E> listIn, boolean withNull) {
        List<Item> listOut = new ArrayList<>();
        if (withNull) {
            listOut.add(new Item<E>("", null));
        }
        if (listIn != null) {
            for (E listInEntry : listIn) {
                listOut.add(new Item<E>(String.valueOf(listInEntry), listInEntry));
            }
        }
        return listOut;
    }

    public static <E> List<Item> addEmptyItem(List<Item> items) {
        boolean hasEmptyItem = false;
        for (int i = 0; i < items.size(); i++) {
             if (items.get(i).getKey().equals("")) {
                 hasEmptyItem = true;
                 break;
             }
        }

        if (!hasEmptyItem)
            items.add(0, new Item<E>("", null));
        return items;
    }

    public static <E> List<Item> addItems(List<Item> items, List<E> listIn) {
        for (E listInEntry: listIn) {
            items.add(new Item<E>(listInEntry.toString(),listInEntry));
        }
        return items;
    }

    public static void updateListOfDays(ControlSpinnerField birthdateDD, Integer selectedYear, Integer selectedMonth) {
        Integer currentlySelected = (Integer) birthdateDD.getValue();
        List<Item> days = DataUtils.toItems(DateHelper.getDaysInMonth(selectedMonth, selectedYear));
        birthdateDD.setSpinnerData(days);
        if (currentlySelected != null) {
            birthdateDD.setValue(currentlySelected);
        }
    }

}
