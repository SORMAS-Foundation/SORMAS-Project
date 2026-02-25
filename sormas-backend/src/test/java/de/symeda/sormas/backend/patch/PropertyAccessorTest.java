package de.symeda.sormas.backend.patch;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.RabiesType;
import de.symeda.sormas.backend.AbstractUnitTest;

class PropertyAccessorTest extends AbstractUnitTest {

	private CaseDataDto caseDataDto;

	@BeforeEach
	void setUp() {
		caseDataDto = new CaseDataDto();
		caseDataDto.setDengueFeverType(DengueFeverType.DENGUE_FEVER);
		caseDataDto.setRabiesType(RabiesType.FURIOUS_RABIES);
		caseDataDto.setClassificationComment("test comment");
		caseDataDto.setDistrictLevelDate(new Date());
	}

	// getNestedPropertyType - simple field

//	@Test
//	void getNestedPropertyType_simpleField_dengueFeverType() {
//		// EXECUTE
//		Optional<Tuple<Class<?>, Set<Disease>>> result = PropertyAccessor.getNestedPropertyType(caseDataDto, "dengueFeverType");
//
//		// CHECK
//		assertTrue(result.isPresent());
//		assertEquals(DengueFeverType.class, result.get().getFirst());
//		assertEquals(Set.of(Disease.DENGUE), result.get().getSecond());
//	}
//
//	@Test
//	void getNestedPropertyType_simpleField_rabiesType() {
//		// EXECUTE
//		Optional<Tuple<Class<?>, Set<Disease>>> result = PropertyAccessor.getNestedPropertyType(caseDataDto, "rabiesType");
//
//		// CHECK
//		assertTrue(result.isPresent());
//		assertEquals(RabiesType.class, result.get().getFirst());
//		assertEquals(Set.of(Disease.RABIES), result.get().getSecond());
//	}
//
//	@Test
//	void getNestedPropertyType_simpleField_allDiseases() {
//		// EXECUTE
//		Optional<Tuple<Class<?>, Set<Disease>>> result = PropertyAccessor.getNestedPropertyType(caseDataDto, "districtLevelDate");
//
//		// CHECK
//		assertTrue(result.isPresent());
//		assertEquals(Date.class, result.get().getFirst());
//		assertTrue(result.get().getSecond().contains(Disease.DENGUE));
//	}
//
//	@Test
//	void getNestedPropertyType_simpleField_hideDiseases() {
//		// EXECUTE
//		Optional<Tuple<Class<?>, Set<Disease>>> result = PropertyAccessor.getNestedPropertyType(caseDataDto, "classificationComment");
//
//		// CHECK
//		assertTrue(result.isPresent());
//		assertEquals(String.class, result.get().getFirst());
//		assertFalse(result.get().getSecond().contains(Disease.RESPIRATORY_SYNCYTIAL_VIRUS));
//		assertFalse(result.get().getSecond().contains(Disease.PLAGUE));
//		assertTrue(result.get().getSecond().contains(Disease.DENGUE));
//	}
//
//	// getPropertyType
//
//	@Test
//	void getPropertyType_fieldNotFound_returnsEmpty() {
//		// EXECUTE
//		Optional<Tuple<Class<?>, Set<Disease>>> result = PropertyAccessor.getPropertyType(caseDataDto, "nonExistentField");
//
//		// CHECK
//		assertTrue(result.isEmpty());
//	}
//
//	@Test
//	void getNestedProperty_nullPath_returnsEmpty() {
//		// EXECUTE & CHECK
//		assertTrue(PropertyAccessor.getNestedProperty(caseDataDto, null).isEmpty());
//	}
//
//	// edge cases
//
//	@Test
//	void getNestedPropertyType_nullBean_returnsEmpty() {
//		// EXECUTE & CHECK
//		assertTrue(PropertyAccessor.getNestedPropertyType(null, "field").isEmpty());
//	}
//
//	@Test
//	void getNestedPropertyType_emptyFieldName_returnsEmpty() {
//		// EXECUTE & CHECK
//		assertTrue(PropertyAccessor.getNestedPropertyType(caseDataDto, "").isEmpty());
//	}
//
//	@Test
//	void getNestedPropertyType_nullFieldName_returnsEmpty() {
//		// EXECUTE & CHECK
//		assertTrue(PropertyAccessor.getNestedPropertyType(caseDataDto, null).isEmpty());
//	}
//
//	@Test
//	void getNestedProperty_emptyPath_returnsEmpty() {
//		// EXECUTE & CHECK
//		assertTrue(PropertyAccessor.getNestedProperty(caseDataDto, "").isEmpty());
//	}
}
