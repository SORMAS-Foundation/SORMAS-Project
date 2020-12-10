package de.symeda.sormas.app.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.Country;
import de.symeda.sormas.app.component.Item;

public class CountryHelper {

	public static List<Item> loadCountries() {
		List<Item> items = new ArrayList<>();

		items.add(new Item<>("", null));
		items.addAll(
			DatabaseHelper.getCountryDao()
				.queryActiveForAll(Country.ISO_CODE, true)
				.stream()
				.map(c -> new Item<>(I18nProperties.getCountryName(c.getIsoCode(), c.getName()), c))
				.sorted(Comparator.comparing(Item::getKey))
				.collect(Collectors.toList()));

		return items;
	}
}
