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

package de.symeda.sormas.backend.info;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases;

public class EntityColumnsTest {

	private static class TestDto {

		@Diseases(value = {
			Disease.GIARDIASIS,
			Disease.CRYPTOSPORIDIOSIS }, hide = true)
		private String fieldWithHide;

		@Diseases({
			Disease.EVD,
			Disease.DENGUE })
		private String fieldWithoutHide;

		private String fieldWithoutAnnotation;
	}

	private String invokeGetDiseases(String fieldName) throws Exception {
		EntityColumns entityColumns = new EntityColumns(null, false);
		Field field = TestDto.class.getDeclaredField(fieldName);
		FieldData fieldData = new FieldData(field, TestDto.class, "Test");
		Method getDiseases = EntityColumns.class.getDeclaredMethod("getDiseases", FieldData.class);
		getDiseases.setAccessible(true);
		return (String) getDiseases.invoke(entityColumns, fieldData);
	}

	@Test
	public void testGetDiseases_noAnnotation_returnsAll() throws Exception {
		assertThat(invokeGetDiseases("fieldWithoutAnnotation"), is("All"));
	}

	@Test
	public void testGetDiseases_withHideFalse_returnsListedDiseases() throws Exception {
		String result = invokeGetDiseases("fieldWithoutHide");
		assertThat(result, containsString(Disease.EVD.toShortString()));
		assertThat(result, containsString(Disease.DENGUE.toShortString()));
		assertThat(result, not(containsString(Disease.GIARDIASIS.toShortString())));
	}

	@Test
	public void testGetDiseases_withHideTrue_returnsAllDiseasesExceptListed() throws Exception {
		String result = invokeGetDiseases("fieldWithHide");
		assertThat(result, not(containsString(Disease.GIARDIASIS.toShortString())));
		assertThat(result, not(containsString(Disease.CRYPTOSPORIDIOSIS.toShortString())));
		assertThat(result, containsString(Disease.EVD.toShortString()));
		assertThat(result, containsString(Disease.DENGUE.toShortString()));
	}
}
