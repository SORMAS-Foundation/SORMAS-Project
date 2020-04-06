
package de.symeda.sormas.app.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.component.Item;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class DataUtilsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void getEnumItems() {

        List<Item> enumItems = DataUtils.getEnumItems(YesNoUnknown.class, true);
        assertThat(enumItems.size(), is(YesNoUnknown.values().length+1));

        enumItems = DataUtils.getEnumItems(YesNoUnknown.class, false);
        assertThat(enumItems.size(), is(YesNoUnknown.values().length));

        exceptionRule.expect(IllegalArgumentException.class);
        DataUtils.getEnumItems(Object.class, false);

        exceptionRule.expect(NullPointerException.class);
        DataUtils.getEnumItems(null, false);
    }
}