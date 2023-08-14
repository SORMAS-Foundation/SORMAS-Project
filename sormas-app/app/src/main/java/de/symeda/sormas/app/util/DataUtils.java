/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;

/**
 * Created by Stefan Szczesny on 02.08.2016.
 */

public class DataUtils {

	/**
	 * @deprecated use buildEnumItems instead
	 */
	@Deprecated
	public static <E extends Enum<?>> List<Item> getEnumItems(Class<E> clazz) {
		return getEnumItems(clazz, true);
	}

	/**
	 * @deprecated use buildEnumItems instead
	 */
	@Deprecated
	public static <E extends Enum<?>> List<Item> getEnumItems(Class<E> clazz, boolean withNull) {
		return getEnumItems(clazz, withNull, null);
	}

	/**
	 * @deprecated use buildEnumItems instead
	 */
	@Deprecated
	public static <E extends Enum<?>> List<Item> getEnumItems(Class<E> clazz, boolean withNull, FieldVisibilityCheckers checkers) {
		E[] enumConstants = clazz.getEnumConstants();
		if (!clazz.isEnum()) {
			throw new IllegalArgumentException(clazz.toString() + " is not an enum");
		}
		List<Item> list = new ArrayList<>();

		if (withNull) {
			list.add(new Item<E>("", null));
		}

		for (E enumConstant : enumConstants) {
			boolean visible = true;
			if (checkers != null) {
				visible = checkers.isVisible(clazz, enumConstant.name());
			}
			if (visible) {
				list.add(new Item<>(enumConstant.toString(), enumConstant));
			}
		}
		return list;
	}

	public static <E extends Enum<?>> List<Item<E>> buildEnumItems(Class<E> clazz, boolean withNull, FieldVisibilityCheckers checkers) {
		E[] enumConstants = clazz.getEnumConstants();
		if (!clazz.isEnum()) {
			throw new IllegalArgumentException(clazz.toString() + " is not an enum");
		}
		List<Item<E>> list = new ArrayList<>();

		if (withNull) {
			list.add(new Item<E>("", null));
		}

		for (E enumConstant : enumConstants) {
			boolean visible = true;
			if (checkers != null) {
				visible = checkers.isVisible(clazz, enumConstant.name());
			}
			if (visible) {
				list.add(new Item<>(enumConstant.toString(), enumConstant));
			}
		}
		return list;
	}

	public static List<Item> getBooleanItems() {
		List<Item> list = new ArrayList<>();
		list.add(new Item<>(DatabaseHelper.getString(R.string.yes), Boolean.TRUE));
		list.add(new Item<>(DatabaseHelper.getString(R.string.no), Boolean.FALSE));
		return list;
	}

	public static boolean emptyOrWithOneNullItem(List<Item> listIn) {
		return listIn.isEmpty() || (listIn.size() == 1 && (listIn.get(0) == null || listIn.get(0).getValue() == null));
	}

	public static <E> List<Item> toItems(List<E> listIn) {
		return toItems(listIn, true);
	}

	public static <E> Item toItem(E item) {
		return new Item<>(item instanceof AbstractDomainObject ? ((AbstractDomainObject) item).buildCaption() : item.toString(), item);
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
				listOut.add(
					new Item<E>(
						listInEntry instanceof AbstractDomainObject
							? ((AbstractDomainObject) listInEntry).buildCaption()
							: String.valueOf(listInEntry),
						listInEntry));
			}
		}

		return listOut;
	}

	public static <E> List<Item> toItems(
		List<E> listIn,
		boolean withNull,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		Class<? extends Enum> enumClass) {

		List<Item> listOut = new ArrayList<>();
		if (withNull) {
			listOut.add(new Item<E>("", null));
		}

		if (listIn != null) {
			if (fieldVisibilityCheckers != null) {
				listOut.addAll(
					listIn.stream()
						.filter(i -> fieldVisibilityCheckers.isVisible(enumClass, ((Enum<?>) i).name()))
						.map(i -> new Item<>(String.valueOf(i), i))
						.collect(Collectors.toList()));
			} else {
				listOut.addAll(listOut.stream().map(i -> new Item<>(String.valueOf(i), i)).collect(Collectors.toList()));
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

	public static void updateListOfDays(ControlSpinnerField birthdateDD, Integer selectedYear, Integer selectedMonth) {
		Integer currentlySelected = (Integer) birthdateDD.getValue();
		List<Item> days = DataUtils.toItems(DateHelper.getDaysInMonth(selectedMonth, selectedYear));
		birthdateDD.setSpinnerData(days);
		if (currentlySelected != null) {
			birthdateDD.setValue(currentlySelected);
		}
	}

	public static <T> T getRandomCandidate(List<T> candidates) {
		if (CollectionUtils.isEmpty(candidates)) {
			return null;
		}

		return candidates.get(new Random().nextInt(candidates.size()));
	}
}
