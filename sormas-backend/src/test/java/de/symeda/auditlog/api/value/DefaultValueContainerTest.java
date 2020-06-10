/*******************************************************************************
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.auditlog.api.value;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.Month;
import java.util.Date;
import java.util.SortedMap;

import org.junit.Test;

import de.symeda.auditlog.api.sample.CustomEnum;
import de.symeda.auditlog.api.sample.Entity;
import de.symeda.sormas.backend.auditlog.AuditLogDateHelper;

public class DefaultValueContainerTest {

	private static final String CUSTOM_NULL_STRING = "{I_AM_NULL}";
	private static final String ID = "Id";
	private static final String NAME = "name";
	private static final String TITLE = "title";
	private static final String NULL_KEY = "nullKey";
	private static final String PRESENT_KEY = "presentKey";

	@Test
	public void compareShouldYieldEmptyMap() {

		final String unchangedValue = "1";

		DefaultValueContainer original = new DefaultValueContainer();
		original.put(ID, unchangedValue);

		DefaultValueContainer later = new DefaultValueContainer();
		later.put(ID, unchangedValue);

		SortedMap<String, String> changes = later.compare(original);

		assertThat(changes.entrySet().size(), is(0));
	}

	@Test
	public void compareShouldDetectSingleChange() {

		DefaultValueContainer original = new DefaultValueContainer();
		original.put(ID, "1");

		DefaultValueContainer later = new DefaultValueContainer();
		later.put(ID, "2");

		SortedMap<String, String> changes = later.compare(original);

		assertThat(changes.entrySet().size(), is(1));
		assertThat(changes.get(ID), is(equalTo("2")));

	}

	@Test
	public void compareShouldDetectComplexChanges() {

		DefaultValueContainer original = new DefaultValueContainer();
		original.put(ID, "1");
		original.put("changeDate", LocalTime.of(12, 0).toString());
		original.put("counter", "0");
		original.put("literal", "unchanged");

		DefaultValueContainer later = new DefaultValueContainer();
		later.put(ID, "1");
		later.put("changeDate", LocalTime.of(12, 15).toString());
		later.put("counter", "1");
		later.put("literal", "unchanged");

		SortedMap<String, String> changes = later.compare(original);

		assertThat(changes.entrySet().size(), is(2));
		assertThat(changes.get("counter"), is(equalTo("1")));
		assertThat(changes.get("changeDate"), is(equalTo(LocalTime.of(12, 15).toString())));

	}

	@Test
	public void compareShouldDetectNewAttributes() {

		DefaultValueContainer original = new DefaultValueContainer();
		original.put(ID, "1");
		original.put("changeDate", LocalTime.of(12, 0).toString());
		original.put("counter", "0");

		DefaultValueContainer later = new DefaultValueContainer();
		later.put(ID, "1");
		later.put("changeDate", LocalTime.of(12, 15).toString());
		later.put("counter", "1");
		later.put("literal", "unchanged");

		SortedMap<String, String> changes = later.compare(original);

		assertThat(changes.entrySet().size(), is(3));
		assertThat(changes.get("counter"), is(equalTo("1")));
		assertThat(changes.get("changeDate"), is(equalTo(LocalTime.of(12, 15).toString())));
		assertThat(changes.get("literal"), is(equalTo("unchanged")));

	}

	@Test
	public void testPutToMapDefaultNullString() {

		DefaultValueContainer cut = new DefaultValueContainer();
		assertPutToMapEscapesNullValue(cut, DefaultValueContainer.DEFAULT_NULL_STRING);
	}

	@Test
	public void testPutToMapCustomNullString() {

		DefaultValueContainer cut = new DefaultValueContainer(CUSTOM_NULL_STRING);
		assertPutToMapEscapesNullValue(cut, CUSTOM_NULL_STRING);
	}

	private static void assertPutToMapEscapesNullValue(DefaultValueContainer cut, String expectedNullString) {

		assertEquals(expectedNullString, cut.getNullString());

		cut.putToMap(NULL_KEY, null);
		cut.putToMap(PRESENT_KEY, "presentValue");
		assertEquals(2, cut.getAttributes().size());
		assertThat(cut.getAttributes().get(NULL_KEY), is(expectedNullString));
		assertThat(cut.getAttributes().get(PRESENT_KEY), is("presentValue"));
	}

	@Test
	public void testPutString() {

		DefaultValueContainer cut = new DefaultValueContainer();

		cut.put(TITLE, (String) null);
		cut.put(NAME, "Max Mustermann");
		assertThat(cut.getAttributes().get(TITLE), is(cut.getNullString()));
		assertThat(cut.getAttributes().get(NAME), is("Max Mustermann"));
	}

	@Test
	public void testPutValueWithFormatter() {

		DefaultValueContainer cut = new DefaultValueContainer();

		cut.put(NULL_KEY, (Entity) null, value -> value.getUuid());
		cut.put("presentReference", new Entity("affe-cafe", false, "test", 2), value -> value.getUuid());
		assertThat(cut.getAttributes().get(NULL_KEY), is(cut.getNullString()));
		assertThat(cut.getAttributes().get("presentReference"), is("affe-cafe"));
	}

	@Test
	public void testPutBoolean() {

		DefaultValueContainer cut = new DefaultValueContainer();

		cut.put(NULL_KEY, (Boolean) null);
		cut.put("boxedFalse", Boolean.FALSE);
		cut.put("boxedTrue", Boolean.TRUE);
		cut.put("primitivefalse", false);
		cut.put("primitiveTrue", true);
		assertThat(cut.getAttributes().get(NULL_KEY), is(cut.getNullString()));
		assertThat(cut.getAttributes().get("boxedFalse"), is("false"));
		assertThat(cut.getAttributes().get("boxedTrue"), is("true"));
		assertThat(cut.getAttributes().get("primitivefalse"), is("false"));
		assertThat(cut.getAttributes().get("primitiveTrue"), is("true"));
	}

	@Test
	public void testPutNumber() {

		DefaultValueContainer cut = new DefaultValueContainer();

		cut.put(NULL_KEY, (Integer) null);
		cut.put("boxedInteger", Integer.valueOf(4711));
		cut.put("primitiveInteger", 4711);
		cut.put("boxedDouble", Double.valueOf("47.11"));
		cut.put("primitiveDouble", 47.11D);
		cut.put("bigDecimal", new BigDecimal("47.11"));
		assertThat(cut.getAttributes().get(NULL_KEY), is(cut.getNullString()));
		assertThat(cut.getAttributes().get("boxedInteger"), is("4711"));
		assertThat(cut.getAttributes().get("primitiveInteger"), is("4711"));
		assertThat(cut.getAttributes().get("boxedDouble"), is("47.11"));
		assertThat(cut.getAttributes().get("primitiveDouble"), is("47.11"));
		assertThat(cut.getAttributes().get("bigDecimal"), is("47.11"));
	}

	@Test
	public void testPutEnum() {

		DefaultValueContainer cut = new DefaultValueContainer();
		cut.put(NULL_KEY, (Month) null);
		cut.put(PRESENT_KEY, Month.MARCH);
		cut.put("customToStringNotUsed", CustomEnum.VALUE);
		assertThat(cut.getAttributes().get(NULL_KEY), is(cut.getNullString()));
		assertThat(cut.getAttributes().get(PRESENT_KEY), is("MARCH"));
		assertThat(cut.getAttributes().get("customToStringNotUsed"), is("VALUE"));
	}

	@Test
	public void testPutDate() {

		DefaultValueContainer cut = new DefaultValueContainer();
		String dayPattern = "yyyy-MM-dd";
		String timePattern = "HH:mm:ss";
		Date date = AuditLogDateHelper.of(2016, Month.JANUARY, 28);
		Date now = new Date();

		cut.put(NULL_KEY, (Date) null, dayPattern);
		cut.put("date", date, dayPattern);
		cut.put("nowAsDate", now, dayPattern);
		cut.put("nowAsTime", now, timePattern);
		assertThat(cut.getAttributes().get(NULL_KEY), is(cut.getNullString()));
		assertThat(cut.getAttributes().get("date"), is("2016-01-28"));
		assertThat(cut.getAttributes().get("nowAsDate"), is(new SimpleDateFormat(dayPattern).format(now)));
		assertThat(cut.getAttributes().get("nowAsTime"), is(new SimpleDateFormat(timePattern).format(now)));
	}

	@Test
	public void testConfigureAnonymizeValue() {

		DefaultValueContainer cut = new DefaultValueContainer();

		// 1. configured anonymizeValue
		String criticalKey = "critical";
		String anonymizeValue = "----";
		cut.configureAnonymizeValue(criticalKey, anonymizeValue);
		assertThat(cut.getAnonymizeConfig().get(criticalKey), is(anonymizeValue));

		// 2. CRITICAL has not changed -> is not returned in compare
		assertTrue(cut.compare(new DefaultValueContainer()).isEmpty());
		assertThat(cut.compare(new DefaultValueContainer()).get(criticalKey), is(nullValue()));

		// 3. CRITICAL has changed -> anonymizeValue will be returned in compare
		cut.put(criticalKey, "dontReturnMeInCompare");
		assertThat(cut.compare(new DefaultValueContainer()).get(criticalKey), is(anonymizeValue));
	}
}
