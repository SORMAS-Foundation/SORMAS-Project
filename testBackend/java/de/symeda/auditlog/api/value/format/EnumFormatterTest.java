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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.symeda.auditlog.api.sample.CustomEnum;
import de.symeda.auditlog.api.value.ValueContainer;

/**
 * @see EnumFormatter
 * @author Stefan Kock
 */
public class EnumFormatterTest {

	@Test
	public void testFormat() {

		EnumFormatter formatter = new EnumFormatter();

		assertThat(formatter.format(null), equalTo(ValueContainer.DEFAULT_NULL_STRING));
		assertThat(formatter.format(CustomEnum.VALUE_1), equalTo("VALUE_1"));
	}
}
