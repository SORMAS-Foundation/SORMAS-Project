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
import static org.junit.Assert.assertThat;

import java.time.Month;

import javax.persistence.TemporalType;

import org.junit.Test;

import de.symeda.sormas.backend.auditlog.AuditLogDateHelper;

/**
 * @see UtilDateFormatter
 * @author Stefan Kock
 */
public class UtilDateFormatterTest {

	@Test
	public void testUtilDateFormatterTemporalType() {

		assertThat(new UtilDateFormatter(TemporalType.TIMESTAMP).getPattern(), equalTo(UtilDateFormatter.TIMESTAMP_PATTERN));
		assertThat(new UtilDateFormatter(TemporalType.DATE).getPattern(), equalTo(UtilDateFormatter.DAY_PATTERN));
		assertThat(new UtilDateFormatter(TemporalType.TIME).getPattern(), equalTo(UtilDateFormatter.HOUR_MIN_PATTERN));

		assertThat(new UtilDateFormatter((TemporalType) null).getPattern(), equalTo(UtilDateFormatter.TIMESTAMP_PATTERN));
	}

	@Test
	public void testUtilDateFormatterString() {

		String pattern = "YYYY MMM";
		assertThat(new UtilDateFormatter(pattern).getPattern(), equalTo(pattern));
	}

	@Test
	public void testFormat() {

		UtilDateFormatter formatter = new UtilDateFormatter(TemporalType.DATE);
		assertThat(formatter.format(AuditLogDateHelper.of(2016, Month.APRIL, 1)), equalTo("2016-04-01"));
	}
}
