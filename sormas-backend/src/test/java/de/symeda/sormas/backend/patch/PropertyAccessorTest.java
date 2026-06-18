package de.symeda.sormas.backend.patch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.backend.AbstractUnitTest;

public class PropertyAccessorTest extends AbstractUnitTest {

	// Both classes must be public static for Apache Commons PropertyUtils to introspect them via reflection.
	// The self-referential "address" property on AddressBean is required so that for 3-segment path tests
	// getPropertyType(leafValue, "address", ...) can succeed: the navigated leaf value must itself expose
	// the leaf segment name as a bean property (see getNestedPropertyType implementation).
	public static class PersonBean {

		private AddressBean address;

		public AddressBean getAddress() {
			return address;
		}

		public void setAddress(AddressBean address) {
			this.address = address;
		}
	}

	public static class AddressBean {

		private AddressBean address;
		private String street;

		public AddressBean getAddress() {
			return address;
		}

		public void setAddress(AddressBean address) {
			this.address = address;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}
	}

	// ---- getNestedPropertyType ----

	@Test
	void getNestedPropertyType_threeSegmentPath_returnsType() {
		// PREPARE
		// Build a 3-deep address chain so that navigating "address.address.address"
		// reaches a non-null AddressBean leaf.
		AddressBean level3 = new AddressBean();
		AddressBean level2 = new AddressBean();
		level2.setAddress(level3);
		AddressBean level1 = new AddressBean();
		level1.setAddress(level2);
		PersonBean person = new PersonBean();
		person.setAddress(level1);

		// EXECUTE
		// "address.address.address" has 2 dots → 3 segments → triggers the nested-path branch.
		// Regression: Optional.ofNullable(getNestedProperty(...)) wrapped Optional<AddressBean>
		// in a second Optional, so leafParent became Optional<AddressBean> instead of AddressBean.
		// PropertyUtils.getPropertyType(Optional, "address") then threw NoSuchMethodException,
		// which was silently caught and returned FIELD_DOES_NOT_EXIST for any existing 3-segment path.
		Tuple<Class<?>, PropertyAccessFailure> result =
			PropertyAccessor.getNestedPropertyType(person, "address.address.address", FieldVisibilityCheckers.getNoop());

		// CHECK
		assertNull(result.getSecond(), "Expected no failure for existing 3-segment path, but got: " + result.getSecond());
		assertEquals(AddressBean.class, result.getFirst());
	}

	@Test
	void getNestedPropertyType_threeSegmentPath_nonExistentLeaf_returnsFieldDoesNotExist() {
		// PREPARE
		AddressBean level3 = new AddressBean();
		AddressBean level2 = new AddressBean();
		level2.setAddress(level3);
		AddressBean level1 = new AddressBean();
		level1.setAddress(level2);
		PersonBean person = new PersonBean();
		person.setAddress(level1);

		// EXECUTE
		Tuple<Class<?>, PropertyAccessFailure> result =
			PropertyAccessor.getNestedPropertyType(person, "address.address.nonExistent", FieldVisibilityCheckers.getNoop());

		// CHECK
		assertEquals(PropertyAccessFailure.FIELD_DOES_NOT_EXIST, result.getSecond());
	}

	@Test
	void getNestedPropertyType_twoSegmentPath_returnsType() {
		// PREPARE
		AddressBean address = new AddressBean();
		address.setStreet("Main St");
		PersonBean person = new PersonBean();
		person.setAddress(address);

		// EXECUTE — 1 dot → notNestedPath = true → takes the early-return branch
		Tuple<Class<?>, PropertyAccessFailure> result =
			PropertyAccessor.getNestedPropertyType(person, "address.street", FieldVisibilityCheckers.getNoop());

		// CHECK
		assertNull(result.getSecond());
		assertEquals(String.class, result.getFirst());
	}

	@Test
	void getNestedPropertyType_nullBean_returnsInvalidInput() {
		Tuple<Class<?>, PropertyAccessFailure> result =
			PropertyAccessor.getNestedPropertyType(null, "address.street", FieldVisibilityCheckers.getNoop());

		assertEquals(PropertyAccessFailure.INVALID_INPUT, result.getSecond());
	}

	@Test
	void getNestedPropertyType_nullFieldName_returnsInvalidInput() {
		Tuple<Class<?>, PropertyAccessFailure> result =
			PropertyAccessor.getNestedPropertyType(new PersonBean(), null, FieldVisibilityCheckers.getNoop());

		assertEquals(PropertyAccessFailure.INVALID_INPUT, result.getSecond());
	}

	@Test
	void getNestedPropertyType_invisibleField_returnsUnsupportedField() {
		// PREPARE
		AddressBean address = new AddressBean();
		PersonBean person = new PersonBean();
		person.setAddress(address);
		FieldVisibilityCheckers hideAll =
			FieldVisibilityCheckers.withCheckers((FieldVisibilityCheckers.FieldNameBaseChecker) (type, id) -> false);

		// EXECUTE
		Tuple<Class<?>, PropertyAccessFailure> result =
			PropertyAccessor.getNestedPropertyType(person, "address.street", hideAll);

		// CHECK
		assertEquals(PropertyAccessFailure.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE, result.getSecond());
	}

	// ---- getNestedPropertyAndType ----

	@Test
	void getNestedPropertyAndType_nullBean_returnsInvalidInput() {
		Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> result =
			PropertyAccessor.getNestedPropertyAndType(null, "address.street", FieldVisibilityCheckers.getNoop());

		assertEquals(PropertyAccessFailure.INVALID_INPUT, result.getSecond());
	}

	@Test
	void getNestedPropertyAndType_twoSegmentPath_returnsTypeAndValue() {
		// PREPARE
		AddressBean address = new AddressBean();
		address.setStreet("Baker St");
		PersonBean person = new PersonBean();
		person.setAddress(address);

		// EXECUTE
		Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> result =
			PropertyAccessor.getNestedPropertyAndType(person, "address.street", FieldVisibilityCheckers.getNoop());

		// CHECK
		assertNull(result.getSecond());
		assertEquals(String.class, result.getFirst().getFirst());
		assertEquals("Baker St", result.getFirst().getSecond());
	}

	@Test
	void getNestedPropertyAndType_threeSegmentPath_returnsTypeAndValue() {
		// PREPARE
		AddressBean level4 = new AddressBean();
		AddressBean level3 = new AddressBean();
		level3.setAddress(level4);
		AddressBean level2 = new AddressBean();
		level2.setAddress(level3);
		AddressBean level1 = new AddressBean();
		level1.setAddress(level2);
		PersonBean person = new PersonBean();
		person.setAddress(level1);

		// EXECUTE
		Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> result =
			PropertyAccessor.getNestedPropertyAndType(person, "address.address.address", FieldVisibilityCheckers.getNoop());

		// CHECK
		assertNull(result.getSecond());
		assertEquals(AddressBean.class, result.getFirst().getFirst());
		assertEquals(level4, result.getFirst().getSecond());
	}

	@Test
	void getNestedPropertyAndType_threeSegmentPath_nonExistentLeaf_returnsFieldDoesNotExist() {
		// PREPARE
		AddressBean level3 = new AddressBean();
		AddressBean level2 = new AddressBean();
		level2.setAddress(level3);
		AddressBean level1 = new AddressBean();
		level1.setAddress(level2);
		PersonBean person = new PersonBean();
		person.setAddress(level1);

		// EXECUTE
		Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> result =
			PropertyAccessor.getNestedPropertyAndType(person, "address.address.nonExistent", FieldVisibilityCheckers.getNoop());

		// CHECK
		assertEquals(PropertyAccessFailure.FIELD_DOES_NOT_EXIST, result.getSecond());
	}

	// ---- getPropertyTypeAndValue ----

	@Test
	void getPropertyTypeAndValue_existingField_returnsTypeAndValue() {
		// PREPARE
		AddressBean address = new AddressBean();
		address.setStreet("Elm Ave");

		// EXECUTE
		Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> result =
			PropertyAccessor.getPropertyTypeAndValue(address, "street", FieldVisibilityCheckers.getNoop());

		// CHECK
		assertNull(result.getSecond());
		assertEquals(String.class, result.getFirst().getFirst());
		assertEquals("Elm Ave", result.getFirst().getSecond());
	}

	@Test
	void getPropertyTypeAndValue_nonExistentField_returnsFieldDoesNotExist() {
		AddressBean address = new AddressBean();

		Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> result =
			PropertyAccessor.getPropertyTypeAndValue(address, "nonExistent", FieldVisibilityCheckers.getNoop());

		assertEquals(PropertyAccessFailure.FIELD_DOES_NOT_EXIST, result.getSecond());
	}

	@Test
	void getPropertyTypeAndValue_invisibleField_returnsUnsupportedField() {
		// PREPARE
		AddressBean address = new AddressBean();
		FieldVisibilityCheckers hideAll =
			FieldVisibilityCheckers.withCheckers((FieldVisibilityCheckers.FieldNameBaseChecker) (type, id) -> false);

		// EXECUTE
		Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> result =
			PropertyAccessor.getPropertyTypeAndValue(address, "street", hideAll);

		// CHECK
		assertEquals(PropertyAccessFailure.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE, result.getSecond());
	}

	// ---- getNestedProperty ----

	@Test
	void getNestedProperty_simpleField_returnsValue() {
		// PREPARE
		AddressBean address = new AddressBean();
		address.setStreet("Oak Lane");

		// EXECUTE
		Optional<Object> result = PropertyAccessor.getNestedProperty(address, "street");

		// CHECK
		assertTrue(result.isPresent());
		assertEquals("Oak Lane", result.get());
	}

	@Test
	void getNestedProperty_nestedField_returnsValue() {
		// PREPARE
		AddressBean inner = new AddressBean();
		inner.setStreet("Nested St");
		AddressBean outer = new AddressBean();
		outer.setAddress(inner);

		// EXECUTE
		Optional<Object> result = PropertyAccessor.getNestedProperty(outer, "address.street");

		// CHECK
		assertTrue(result.isPresent());
		assertEquals("Nested St", result.get());
	}

	@Test
	void getNestedProperty_nonExistentField_returnsEmpty() {
		// NoSuchMethodException is caught internally and converted to Optional.empty()
		Optional<Object> result = PropertyAccessor.getNestedProperty(new AddressBean(), "nonExistent");

		assertFalse(result.isPresent());
	}

	// ---- setNestedProperty ----

	@Test
	void setNestedProperty_success_returnsEmpty() {
		// PREPARE
		AddressBean address = new AddressBean();

		// EXECUTE
		Optional<Exception> result = PropertyAccessor.setNestedProperty(address, "street", "New Street");

		// CHECK
		assertFalse(result.isPresent());
		assertEquals("New Street", address.getStreet());
	}

	@Test
	void setNestedProperty_nonExistentField_returnsException() {
		// NoSuchMethodException is caught and returned as Optional<Exception>
		Optional<Exception> result = PropertyAccessor.setNestedProperty(new AddressBean(), "nonExistent", "value");

		assertTrue(result.isPresent());
	}
}
