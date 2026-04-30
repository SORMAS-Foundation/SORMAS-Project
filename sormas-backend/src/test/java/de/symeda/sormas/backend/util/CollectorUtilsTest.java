package de.symeda.sormas.backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.backend.AbstractUnitTest;

class CollectorUtilsTest extends AbstractUnitTest {

	private final Item item1 = new Item("key1", "value1");
	private final Item item2 = new Item("key2", null);
	private final Item item3 = new Item(null, "value3");
	private final Item item4 = new Item(null, null);

	@Test
	void toNullSafeMap_normalKeysAndValues_createsCorrectMap() {
		// PREPARE
		List<Item> items = List.of(item1);

		// EXECUTE
		Map<String, String> result = items.stream().collect(CollectorUtils.toNullSafeMap(Item::getKey, Item::getValue));

		// CHECK
		assertEquals(mapOf("key1", "value1"), result);
	}

	@Test
	void toNullSafeMap_nullValues_preservesNulls() {
		// PREPARE
		List<Item> items = List.of(item2);

		// EXECUTE
		Map<String, String> result = items.stream().collect(CollectorUtils.toNullSafeMap(Item::getKey, Item::getValue));

		// CHECK
		assertEquals(mapOf("key2", null), result);
	}

	@Test
	void toNullSafeMap_nullKeys_preservesNulls() {
		// PREPARE
		List<Item> items = List.of(item3);

		// EXECUTE
		Map<String, String> result = items.stream().collect(CollectorUtils.toNullSafeMap(Item::getKey, Item::getValue));

		// CHECK
		assertEquals(mapOf(null, "value3"), result);
	}

	@Test
	void toNullSafeMap_nullKeyAndValue_preservesNulls() {
		// PREPARE
		List<Item> items = List.of(item4);

		// EXECUTE
		Map<String, String> result = items.stream().collect(CollectorUtils.toNullSafeMap(Item::getKey, Item::getValue));

		// CHECK
		Object v1 = null;
		Object k1 = null;
		assertEquals(mapOf(k1, v1), result);
	}

	private static @NotNull Map<Object, Object> mapOf(Object key, Object value) {
		HashMap<Object, Object> hashMap = new HashMap<>();
		hashMap.put(key, value);
		return hashMap;
	}

	@Test
	void toNullSafeMap_multipleItems_handlesCollisions() {
		// PREPARE
		List<Item> items = List.of(
			new Item("key1", "value1"),
			new Item("key1", "value2"),  // collision
			new Item("key2", null),
			new Item(null, "value3"));

		// EXECUTE
		Map<String, String> result = items.stream().collect(CollectorUtils.toNullSafeMap(Item::getKey, Item::getValue));

		// CHECK
		assertEquals("value2", result.get("key1")); // last write wins
		assertNull(result.get("key2"));
		assertEquals("value3", result.get(null));
	}

	@Test
	void toNullSafeMap_parallelStream_preservesNulls() {
		// PREPARE
		List<Item> items = List.of(item1, item2, item3);

		// EXECUTE
		Map<String, String> result = items.parallelStream().collect(CollectorUtils.toNullSafeMap(Item::getKey, Item::getValue));

		// CHECK
		assertEquals("value1", result.get("key1"));
		assertNull(result.get("key2"));
		assertEquals("value3", result.get(null));
	}

	@Test
	void toNullSafeMap_emptyStream_returnsEmptyMap() {
		// PREPARE
		List<Item> items = List.of();

		// EXECUTE
		Map<String, String> result = items.stream().collect(CollectorUtils.toNullSafeMap(Item::getKey, Item::getValue));

		// CHECK
		assertTrue(result.isEmpty());
	}

	@Test
	void toNullSafeMap_handlesNullsWhereStandardFails() {
		// PREPARE
		List<Item> items = List.of(item2); // contains null value

		// EXECUTE
		Map<String, String> nullSafeResult = items.stream().collect(CollectorUtils.toNullSafeMap(Item::getKey, Item::getValue));

		// CHECK
		assertEquals(mapOf("key2", null), nullSafeResult);

		assertThrows(NullPointerException.class, () -> items.stream().collect(Collectors.toMap(Item::getKey, Item::getValue)));
	}

	// Helper class for tests
	static class Item {

		private final String key;
		private final String value;

		Item(String key, String value) {
			this.key = key;
			this.value = value;
		}

		String getKey() {
			return key;
		}

		String getValue() {
			return value;
		}
	}
}
