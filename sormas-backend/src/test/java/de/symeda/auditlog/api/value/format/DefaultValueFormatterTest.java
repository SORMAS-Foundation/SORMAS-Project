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
package de.symeda.auditlog.api.value.format;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.Month;

import org.junit.Test;

import de.symeda.auditlog.api.sample.SimpleBooleanFlagEntity;
import de.symeda.auditlog.api.value.SimpleValueContainer;

public class DefaultValueFormatterTest {

	@Test
	public void shouldFormatEnums() {

		DefaultValueFormatter cut = new DefaultValueFormatter();
		assertThat(cut.format(Month.MAY), is(equalTo("MAY")));

	}

	@Test
	public void shouldFormatHasUuid() {

		DefaultValueFormatter cut = new DefaultValueFormatter();

		final String theUuid = "the-uuid";
		SimpleBooleanFlagEntity e = new SimpleBooleanFlagEntity(theUuid, false);

		assertThat(cut.format(e), is(equalTo(theUuid)));
	}

	@Test
	public void shouldFormatObjects() {

		DefaultValueFormatter cut = new DefaultValueFormatter();
		assertThat(cut.format(Boolean.FALSE), is(equalTo("false")));
	}

	@Test
	public void shouldFormatNull() {

		DefaultValueFormatter cut = new DefaultValueFormatter();
		assertThat(cut.format(null), is(equalTo(SimpleValueContainer.DEFAULT_NULL_STRING)));
	}
}
