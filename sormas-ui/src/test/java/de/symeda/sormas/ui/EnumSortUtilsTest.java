package de.symeda.sormas.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

		List<DiseaseHolder> list = Arrays.asList(
			null,
			new DiseaseHolder(Disease.MEASLES),
			new DiseaseHolder(Disease.ANTHRAX),
			new DiseaseHolder(Disease.CORONAVIRUS),
			new DiseaseHolder(null));

		Comparator<DiseaseHolder> comparator = EnumSortUtils.comparingOnEnumField(DiseaseHolder::getDisease);

		List<DiseaseHolder> sorted = list.stream().sorted(comparator).collect(Collectors.toList());

		List<Disease> resultOrder =
			sorted.stream().map(diseaseHolder -> diseaseHolder != null ? diseaseHolder.getDisease() : null).collect(Collectors.toList());

		assertEquals(Arrays.asList(Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.MEASLES, null, null), resultOrder);
	}
}
