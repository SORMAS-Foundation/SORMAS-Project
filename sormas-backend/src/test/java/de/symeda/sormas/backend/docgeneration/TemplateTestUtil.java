/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.docgeneration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.person.PersonDto;

public class TemplateTestUtil {

	@Disabled("Only for creation of new test cases")
	@Test
	public void serializeObjects() throws JsonProcessingException {
		// To create test cases with Object properties, use the following:
		PersonDto personDto = new PersonDto();
		personDto.setFirstName("Guy");
		personDto.setLastName("Debord");
		writeJsonProperty(personDto);

		// For collections, use arrays:
		String[] stringArray = {
			"a",
			"b",
			"c" };
		writeJsonProperty(stringArray);
	}

	public static void writeJsonProperty(Object object) throws JsonProcessingException {
		String canonicalClassName = object.getClass().getCanonicalName().replaceFirst("(.*)\\[]", "[L$1;");
		String json = new ObjectMapper().writeValueAsString(object);
		System.out.println("(" + canonicalClassName + ") " + json);
	}

	public static String cleanLineSeparators(String text) {
		return text.replaceAll("\\r\\n?", "\n");
	}

	public static String updateLineSeparatorsBasedOnOS(String text) {
		if (System.lineSeparator().equals("\r\n")) {
			return text;
		} else {
			return text.replaceAll("\\r\\n?", "\n");
		}
	}

}
