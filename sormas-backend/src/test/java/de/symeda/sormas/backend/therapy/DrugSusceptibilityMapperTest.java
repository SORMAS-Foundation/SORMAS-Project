package de.symeda.sormas.backend.therapy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import de.symeda.sormas.api.therapy.DrugSusceptibilityDto;

public class DrugSusceptibilityMapperTest {

	@Test
	public void hasDataIsFalseWithoutResults() {
		assertFalse(DrugSusceptibilityMapper.hasData(null));
		assertFalse(DrugSusceptibilityMapper.hasData(DrugSusceptibilityDto.build()));
	}

	@TestFactory
	public Stream<DynamicTest> hasDataDetectsEveryDrugResultField() {
		return Arrays.stream(DrugSusceptibilityDto.class.getDeclaredFields())
			.filter(field -> !field.isSynthetic() && !Modifier.isStatic(field.getModifiers()))
			.map(field -> DynamicTest.dynamicTest(field.getName(), () -> {
				DrugSusceptibilityDto dto = DrugSusceptibilityDto.build();
				field.setAccessible(true);
				field.set(dto, sampleValue(field));

				assertTrue(DrugSusceptibilityMapper.hasData(dto), field.getName() + " must count as data");
			}));
	}

	private static Object sampleValue(Field field) {
		Class<?> type = field.getType();
		if (type.isEnum()) {
			return type.getEnumConstants()[0];
		}
		if (type == String.class) {
			return "0.5";
		}
		throw new IllegalArgumentException("No sample value for " + field.getName() + " of type " + type);
	}
}
