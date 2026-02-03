package de.symeda.sormas.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;

class EnumSortUtilsTest {

	@Test
	void testComparingOnEnumField_sortsByEnumFieldName() {
		class DiseaseHolder {

			final Disease disease;

			DiseaseHolder(Disease d) {
				this.disease = d;
			}

			Disease getDisease() {
				return disease;
			}
		}

		List<DiseaseHolder> list =
			Arrays.asList(new DiseaseHolder(Disease.MEASLES), new DiseaseHolder(Disease.ANTHRAX), new DiseaseHolder(Disease.CORONAVIRUS));

		Comparator<DiseaseHolder> comparator = EnumSortUtils.comparingOnEnumField(DiseaseHolder::getDisease);

		List<DiseaseHolder> sorted = list.stream().sorted(comparator).collect(Collectors.toList());

		List<Disease> resultOrder = sorted.stream().map(DiseaseHolder::getDisease).collect(Collectors.toList());

		assertEquals(Arrays.asList(Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.MEASLES), resultOrder);
	}

	@Test
	void testComparatorHandlesNullsGracefully() {
		Comparator<Disease> comparator = EnumSortUtils.enumNameComparator();
		assertThrows(NullPointerException.class, () -> comparator.compare(null, Disease.MEASLES));
	}
}
