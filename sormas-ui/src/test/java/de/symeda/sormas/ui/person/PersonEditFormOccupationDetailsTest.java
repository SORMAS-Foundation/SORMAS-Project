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

package de.symeda.sormas.ui.person;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.vaadin.ui.UI;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.PseudonymizableDataAccessLevel;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValue;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.UserProvider;

/**
 * Tests the value change listener of the occupation type field in {@link PersonEditForm}: the occupation details have to be cleared
 * whenever the user selects a different occupation type, but stored details must survive opening and discarding the form.
 */
class PersonEditFormOccupationDetailsTest extends AbstractUiBeanTest {

	private static final String OCCUPATION_A = "TEST_OCCUPATION_A";
	private static final String OCCUPATION_B = "TEST_OCCUPATION_B";
	private static final String OCCUPATION_WITHOUT_DETAILS = "TEST_OCCUPATION_WITHOUT_DETAILS";

	@Override
	public void init() {
		super.init();

		createOccupationType(OCCUPATION_A, "Occupation A", true);
		createOccupationType(OCCUPATION_B, "Occupation B", true);
		createOccupationType(OCCUPATION_WITHOUT_DETAILS, "Occupation without details", false);
		getCustomizableEnumFacade().loadData();

		// The form reads the data access level of the current user via UiUtil, which needs a current UI with a user provider
		UserProvider userProvider = mock(UserProvider.class);
		when(userProvider.getPseudonymizableDataAccessLevel(anyBoolean())).thenReturn(PseudonymizableDataAccessLevel.ALL);
		UI ui = mock(UI.class, withSettings().extraInterfaces(UserProvider.HasUserProvider.class));
		when(((UserProvider.HasUserProvider) ui).getUserProvider()).thenReturn(userProvider);
		UI.setCurrent(ui);
	}

	@AfterEach
	void resetCurrentUi() {
		// UI.setCurrent is thread-local, so it would otherwise leak into other tests running in the same thread
		UI.setCurrent(null);
	}

	@Test
	void shouldKeepStoredDetailsWhenFormIsOpened() {
		PersonEditForm form = createForm(OCCUPATION_A, "TA");

		assertEquals(OCCUPATION_A, getSelectedValue(form));
		assertTrue(getDetailsField(form).isVisible());
		assertEquals("TA", getDetailsField(form).getValue());
	}

	@Test
	void shouldClearDetailsWhenSwitchingBetweenTypesWithDetails() {
		PersonEditForm form = createForm(OCCUPATION_A, "TA");

		selectOccupationType(form, OCCUPATION_B);

		assertTrue(getDetailsField(form).isVisible());
		assertDetailsEmpty(form);
	}

	@Test
	void shouldClearDetailsWhenSwitchingBackToStoredType() {
		// A (stored, "TA") -> B, enter "TB" -> back to A: "TB" belongs to B and must not stay for A
		PersonEditForm form = createForm(OCCUPATION_A, "TA");

		selectOccupationType(form, OCCUPATION_B);
		getDetailsField(form).setValue("TB");
		selectOccupationType(form, OCCUPATION_A);

		assertTrue(getDetailsField(form).isVisible());
		assertDetailsEmpty(form);
	}

	@Test
	void shouldClearDetailsOnEverySwitchForNewPerson() {
		// nothing stored: A, enter "TA" -> B, enter "TB" -> A
		PersonEditForm form = createForm(null, null);

		selectOccupationType(form, OCCUPATION_A);
		getDetailsField(form).setValue("TA");
		selectOccupationType(form, OCCUPATION_B);
		assertDetailsEmpty(form);

		getDetailsField(form).setValue("TB");
		selectOccupationType(form, OCCUPATION_A);
		assertDetailsEmpty(form);
	}

	@Test
	void shouldClearAndHideDetailsWhenSwitchingToTypeWithoutDetails() {
		PersonEditForm form = createForm(OCCUPATION_A, "TA");

		selectOccupationType(form, OCCUPATION_WITHOUT_DETAILS);

		assertFalse(getDetailsField(form).isVisible());
		assertDetailsEmpty(form);
	}

	@Test
	void shouldClearDetailsWhenOccupationTypeIsRemoved() {
		PersonEditForm form = createForm(OCCUPATION_A, "TA");

		selectOccupationType(form, null);

		assertFalse(getDetailsField(form).isVisible());
		assertDetailsEmpty(form);
	}

	@Test
	void shouldRestoreStoredDetailsWhenFormIsDiscarded() {
		PersonEditForm form = createForm(OCCUPATION_A, "TA");

		selectOccupationType(form, OCCUPATION_B);
		getDetailsField(form).setValue("TB");
		assertDoesNotThrow(form::discard);

		assertEquals(OCCUPATION_A, getSelectedValue(form));
		assertTrue(getDetailsField(form).isVisible());
		assertEquals("TA", getDetailsField(form).getValue());
	}

	private void createOccupationType(String value, String caption, boolean hasDetails) {
		CustomizableEnumValue enumValue = new CustomizableEnumValue();
		enumValue.setDataType(CustomizableEnumType.OCCUPATION_TYPE);
		enumValue.setValue(value);
		enumValue.setCaption(caption);
		enumValue.setActive(true);
		enumValue.setProperties(Map.of(OccupationType.HAS_DETAILS, hasDetails));
		getCustomizableEnumValueService().ensurePersisted(enumValue);
	}

	private PersonEditForm createForm(String occupationTypeValue, String occupationDetails) {
		PersonEditForm form = new PersonEditForm(true, false, true);
		PersonDto person = createPerson(occupationTypeValue, occupationDetails);
		assertDoesNotThrow(() -> form.setValue(person));
		return form;
	}

	private PersonDto createPerson(String occupationTypeValue, String occupationDetails) {
		PersonDto person = PersonDto.build();
		person.setFirstName("John");
		person.setLastName("Doe");
		person.setOccupationType(getOccupationType(occupationTypeValue));
		person.setOccupationDetails(occupationDetails);
		return person;
	}

	private OccupationType getOccupationType(String value) {
		if (value == null) {
			return null;
		}

		return getCustomizableEnumFacade().<OccupationType> getEnumValues(CustomizableEnumType.OCCUPATION_TYPE, null)
			.stream()
			.filter(occupationType -> value.equals(occupationType.getValue()))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Occupation type " + value + " was not created"));
	}

	private void selectOccupationType(PersonEditForm form, String value) {
		// the same call a selection in the combo box ends in: ComboBox.setValue marks the bound field as modified
		getOccupationTypeField(form).setValue(getOccupationType(value));
	}

	private static String getSelectedValue(PersonEditForm form) {
		Object selected = getOccupationTypeField(form).getValue();
		return selected instanceof OccupationType ? ((OccupationType) selected).getValue() : null;
	}

	private static void assertDetailsEmpty(PersonEditForm form) {
		String details = getDetailsField(form).getValue();
		assertTrue(details == null || details.isEmpty(), "Expected empty occupation details, but was: " + details);
	}

	private static ComboBox getOccupationTypeField(PersonEditForm form) {
		return form.getField(PersonDto.OCCUPATION_TYPE);
	}

	private static TextField getDetailsField(PersonEditForm form) {
		return form.getField(PersonDto.OCCUPATION_DETAILS);
	}
}
