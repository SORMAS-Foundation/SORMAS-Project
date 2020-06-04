
package de.symeda.sormas.app.util;

import org.junit.Test;

import java.util.List;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.component.Item;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DataUtilsTest {

    @Test
    public void getEnumItems() {

        List<Item> enumItems = DataUtils.getEnumItems(YesNoUnknown.class, true);
        assertThat(enumItems.size(), is(YesNoUnknown.values().length+1));

        enumItems = DataUtils.getEnumItems(YesNoUnknown.class, false);
        assertThat(enumItems.size(), is(YesNoUnknown.values().length));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEnumItemsIllegalArgument() {
        DataUtils.getEnumItems(Object.class, false);
    }
    @Test(expected = NullPointerException.class)
    public void getEnumItemsNullPointer() {
        DataUtils.getEnumItems(null, false);
    }
}