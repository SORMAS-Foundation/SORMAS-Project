/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.api.event.sevenoneseven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;

class Event717EventStatusesProviderTest {

	private final Event717EventStatusesProvider provider = new Event717EventStatusesProvider();

	@Test
	void testDroppedIsNotSelectable() {

		assertFalse(provider.getKeys().contains(EventStatus.DROPPED.name()));
		assertFalse(provider.getOptions().containsKey(EventStatus.DROPPED.name()));
		// more than one key: shown as a checkbox per status
		assertEquals(4, provider.getKeys().size());
	}

	@Test
	void testParse() {

		assertEquals(EnumSet.of(EventStatus.EVENT, EventStatus.CLUSTER), Event717EventStatusesProvider.parse(null));
		assertEquals(EnumSet.of(EventStatus.EVENT, EventStatus.CLUSTER), Event717EventStatusesProvider.parse(" "));
		assertEquals(EnumSet.of(EventStatus.SIGNAL), Event717EventStatusesProvider.parse("SIGNAL"));
		assertEquals(EnumSet.of(EventStatus.SIGNAL, EventStatus.SCREENING), Event717EventStatusesProvider.parse("SCREENING| SIGNAL"));
		// unknown and not selectable values are ignored; nothing left means the default
		assertEquals(EnumSet.of(EventStatus.CLUSTER), Event717EventStatusesProvider.parse("CLUSTER|DROPPED|UNKNOWN"));
		assertEquals(EnumSet.of(EventStatus.EVENT, EventStatus.CLUSTER), Event717EventStatusesProvider.parse("DROPPED"));
	}

	@Test
	void testApplyAndMapValues() {

		SystemConfigurationValueDto dto = SystemConfigurationValueDto.build();
		provider.applyValues(Map.of(EventStatus.CLUSTER.name(), "Cluster", EventStatus.SIGNAL.name(), "Signal"), dto);
		// stored in the order of the enum
		assertEquals("SIGNAL|CLUSTER", dto.getValue());

		Map<String, String> mapped = provider.getMappedValues(dto);
		assertEquals(2, mapped.size());
		assertTrue(mapped.containsKey(EventStatus.SIGNAL.name()));
		assertTrue(mapped.containsKey(EventStatus.CLUSTER.name()));

		provider.applyValues(Map.of(), dto);
		assertEquals("", dto.getValue());
		assertTrue(provider.getMappedValues(dto).isEmpty());
	}
}
