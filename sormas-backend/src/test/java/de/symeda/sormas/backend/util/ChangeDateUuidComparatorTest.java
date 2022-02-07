/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.util;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import de.symeda.sormas.backend.common.AbstractDomainObject;

public class ChangeDateUuidComparatorTest {

	long TEST_TIME = 1642006703753L;

	@Test
	public void testSortChangeDateUuid() {

		Timestamp testTimestamp1 = new Timestamp(TEST_TIME);
		Timestamp testTimestamp2 = new Timestamp(TEST_TIME + 1);
		Timestamp testTimestamp3 = new Timestamp(TEST_TIME + 2);
		Timestamp testTimestamp4 = new Timestamp(TEST_TIME + 2);
		testTimestamp4.setNanos(testTimestamp4.getNanos() + 15);
		Timestamp testTimestamp5 = new Timestamp(TEST_TIME + 2);
		testTimestamp5.setNanos(testTimestamp5.getNanos() + 1500);
		Timestamp testTimestamp6 = new Timestamp(TEST_TIME + 2);
		testTimestamp5.setNanos(testTimestamp6.getNanos() + 999999);
		Timestamp testTimestamp7 = new Timestamp(TEST_TIME + 3);

		String uuid1 = "AAAAAA-AAAAAA-AAAAAA-AAAAAG";
		String uuid2 = "AAAAAA-AAAAAA-AAAAAA-AAAAAF";
		String uuid3 = "AAAAAA-ACAAAA-AAAAAA-AAAAAE";
		String uuid4 = "AAAAAA-ABAAAA-AAAAAA-AAAAAD";
		String uuid5 = "AAAAAA-AZAAAA-AAAAAA-AAAAAC";
		String uuid6 = "AAAAAA-AAAAAA-AAAAAA-AAAAAB";
		String uuid7 = "AAAAAA-AAAAAA-AAAAAA-AAAAAA";

		ArrayList<AbstractDomainObject> ados = new ArrayList<>();
		ados.add(createAbstractDomainObject(testTimestamp7, uuid7));
		ados.add(createAbstractDomainObject(testTimestamp6, uuid6));
		ados.add(createAbstractDomainObject(testTimestamp5, uuid5));
		ados.add(createAbstractDomainObject(testTimestamp4, uuid4));
		ados.add(createAbstractDomainObject(testTimestamp3, uuid3));
		ados.add(createAbstractDomainObject(testTimestamp2, uuid2));
		ados.add(createAbstractDomainObject(testTimestamp1, uuid1));

		List<AbstractDomainObject> sortedAdos = ados.stream().sorted(new ChangeDateUuidComparator<>()).collect(Collectors.toList());

		assertEquals(uuid1, sortedAdos.get(0).getUuid());
		assertEquals(uuid2, sortedAdos.get(1).getUuid());
		assertEquals(uuid6, sortedAdos.get(2).getUuid());
		assertEquals(uuid4, sortedAdos.get(3).getUuid());
		assertEquals(uuid3, sortedAdos.get(4).getUuid());
		assertEquals(uuid5, sortedAdos.get(5).getUuid());
		assertEquals(uuid7, sortedAdos.get(6).getUuid());
	}

	@NotNull
	private AbstractDomainObject createAbstractDomainObject(Timestamp testTimestamp, String uuid) {
		AbstractDomainObject testObject = new AbstractDomainObject() {
		};
		testObject.setChangeDate(testTimestamp);
		testObject.setUuid(uuid);
		return testObject;
	}
}
