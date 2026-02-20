package de.symeda.sormas.patch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyAccessorTest {

	private static class TestBean {

		private String name;
		private Address address;
		private List<Item> items;
		private String[] array;
		private Map<String, String> map;

		// Getters/setters
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		public List<Item> getItems() {
			return items;
		}

		public void setItems(List<Item> items) {
			this.items = items;
		}

		public String[] getArray() {
			return array;
		}

		public void setArray(String[] array) {
			this.array = array;
		}

		public Map<String, String> getMap() {
			return map;
		}

		public void setMap(Map<String, String> map) {
			this.map = map;
		}
	}

	private static class Address {

		private String city;

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}
	}

	private static class Item {

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private TestBean bean;

	@BeforeEach
	void setUp() {
		bean = new TestBean();
		bean.setName("test");
		bean.address = new Address();
		bean.address.setCity("Paris");
		bean.items = List.of(new Item()); // single item for [0]
		bean.items.get(0).setName("item1");
		bean.array = new String[] {
			"a",
			"b" };
		bean.map = Map.of("key", "value");
	}

	@Test
	void getNestedPropertyType_simpleProperty_returnsCorrectType() {
		Optional<Class<?>> type = PropertyAccessor.getNestedPropertyType(bean, "name");
		assertTrue(type.isPresent());
		assertEquals(String.class, type.get());
	}

	@Test
	void getNestedPropertyType_nestedProperty_returnsCorrectType() {
		Optional<Class<?>> type = PropertyAccessor.getNestedPropertyType(bean, "address.city");
		assertTrue(type.isPresent());
		assertEquals(String.class, type.get());
	}

	@Test
	void getNestedPropertyType_indexedProperty_returnsElementType() {
		Optional<Class<?>> type = PropertyAccessor.getNestedPropertyType(bean, "items[0].name");
		assertTrue(type.isPresent());
		assertEquals(String.class, type.get());
	}

	@Test
	void getNestedPropertyType_array_returnsComponentType() {
		Optional<Class<?>> type = PropertyAccessor.getNestedPropertyType(bean, "array[0]");
		assertTrue(type.isPresent());
		assertEquals(String.class, type.get());
	}

	@Test
	void getNestedPropertyType_iterable_returnsObjectClass() {
		Optional<Class<?>> type = PropertyAccessor.getNestedPropertyType(bean, "items[0]");
		assertTrue(type.isPresent());
		assertEquals(Object.class, type.get());
	}

	@Test
	void getNestedPropertyType_invalidProperty_returnsEmpty() {
		Optional<Class<?>> type = PropertyAccessor.getNestedPropertyType(bean, "nonexistent");
		assertFalse(type.isPresent());
	}

	@Test
	void getNestedPropertyType_nullInputs_returnsEmpty() {
		assertFalse(PropertyAccessor.getNestedPropertyType(null, "name").isPresent());
		assertFalse(PropertyAccessor.getNestedPropertyType(bean, null).isPresent());
		assertFalse(PropertyAccessor.getNestedPropertyType(bean, "").isPresent());
	}

	@Test
	void getNestedProperty_readsSimpleProperty() {
		Optional<Object> value = PropertyAccessor.getNestedProperty(bean, "name");
		assertTrue(value.isPresent());
		assertEquals("test", value.get());
	}

	@Test
	void getNestedProperty_readsNestedProperty() {
		Optional<Object> value = PropertyAccessor.getNestedProperty(bean, "address.city");
		assertTrue(value.isPresent());
		assertEquals("Paris", value.get());
	}

	@Test
	void getNestedProperty_readsIndexedProperty() {
		Optional<Object> value = PropertyAccessor.getNestedProperty(bean, "items[0].name");
		assertTrue(value.isPresent());
		assertEquals("item1", value.get());
	}

	@Test
	void getNestedProperty_readsArrayElement() {
		Optional<Object> value = PropertyAccessor.getNestedProperty(bean, "array[0]");
		assertTrue(value.isPresent());
		assertEquals("a", value.get());
	}

	@Test
	void getNestedProperty_invalidPath_returnsEmpty() {
		Optional<Object> value = PropertyAccessor.getNestedProperty(bean, "nonexistent");
		assertFalse(value.isPresent());
	}

	@Test
	void setNestedProperty_success_returnsEmpty() {
		Optional<Exception> result = PropertyAccessor.setNestedProperty(bean, "name", "newName");
		assertFalse(result.isPresent());
		assertEquals("newName", bean.getName());
	}

	@Test
	void setNestedProperty_nested_success_returnsEmpty() {
		Optional<Exception> result = PropertyAccessor.setNestedProperty(bean, "address.city", "London");
		assertFalse(result.isPresent());
		assertEquals("London", bean.address.getCity());
	}

	@Test
	void setNestedProperty_indexed_success_returnsEmpty() {
		Optional<Exception> result = PropertyAccessor.setNestedProperty(bean, "items[0].name", "newItem");
		assertFalse(result.isPresent());
		assertEquals("newItem", bean.items.get(0).getName());
	}

	@Test
	void setNestedProperty_invalidPath_returnsException() {
		Optional<Exception> result = PropertyAccessor.setNestedProperty(bean, "nonexistent", "value");
		assertTrue(result.isPresent());
		assertInstanceOf(InvocationTargetException.class, result.get()); // or other expected types
	}

	@Test
	void getNestedPropertyType_mappedProperty_throwsUnsupported() {
		assertThrows(UnsupportedOperationException.class, () -> PropertyAccessor.getNestedPropertyType(bean, "map[key]"));
	}

	// Integration test with real reflection errors
	@Test
	void getNestedPropertyType_exception_returnsEmpty() {
		class PrivateBean {

			private String secret; // private field
		}
		PrivateBean privateBean = new PrivateBean();

		Optional<Class<?>> type = PropertyAccessor.getNestedPropertyType(privateBean, "secret");
		assertFalse(type.isPresent());
	}
}
