
package de.symeda.sormas.app.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.component.Item;

public class DataUtilsTest {

	@Test
	public void getEnumItems() {

		List<Item> enumItems = DataUtils.getEnumItems(YesNoUnknown.class, true);
		assertThat(enumItems.size(), is(YesNoUnknown.values().length + 1));

		enumItems = DataUtils.getEnumItems(YesNoUnknown.class, false);
		assertThat(enumItems.size(), is(YesNoUnknown.values().length));
	}

	@Test(expected = NullPointerException.class)
	public void getEnumItemsNullPointer() {
		DataUtils.getEnumItems(null, false);
	}
}
