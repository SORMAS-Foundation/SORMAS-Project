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

package de.symeda.sormas.ui.epipulse;

import static com.vaadin.v7.data.fieldgroup.DefaultFieldGroupFieldFactory.CAPTION_PROPERTY_ID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.epipulse.EpipulseExportCriteria;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.utils.AbstractForm;
import de.symeda.sormas.ui.utils.SormasFieldGroupFieldFactory;

/**
 * Checks that the disease (subject code) dropdowns of the Epipulse export creation form and of the export filter show
 * their items in alphabetical order of the caption instead of the declaration order of {@link EpipulseSubjectCode}, and
 * tests the shared {@code AbstractForm.sortItemsByCaption(ComboBox)} they use: collator of the user's language, empty
 * captions last, selected value kept, and null, read-only and tiny dropdowns left alone without an exception.
 */
public class EpipulseSubjectCodeSortingTest extends AbstractUiBeanTest {

	private final TestForm form = new TestForm();

	@AfterEach
	void resetUserLanguage() {
		I18nProperties.removeUserLanguage();
	}

	@Test
	public void editFormShowsAllSubjectCodesSortedByCaption() {

		mockCurrentUiWithFullDataAccess();
		EpipulseEditForm editForm = new EpipulseEditForm(true);

		ComboBox field = subjectCodeField(editForm.getField(EpipulseExportDto.SUBJECT_CODE));

		assertEquals(EpipulseSubjectCode.values().length, field.getItemIds().size());
		assertSortedByCaption(field);
	}

	@Test
	public void filterFormShowsSubjectCodesSortedByCaption() {

		mockCurrentUiWithFullDataAccess();
		EpipulseExportGridFilterForm filterForm = new EpipulseExportGridFilterForm();

		ComboBox field = subjectCodeField(filterForm.getField(EpipulseExportCriteria.SUBJECT_CODE));

		assertSortedByCaption(field);
	}

	@Test
	public void sortsItemsByCaptionInsteadOfInsertionOrder() {

		ComboBox field = createComboBox("PERT", "Pertussis", "MEAS", "Measles", "DENG", "Dengue");

		form.sort(field);

		assertEquals(Arrays.asList("DENG", "MEAS", "PERT"), itemIds(field));
		assertEquals(Arrays.asList("Dengue", "Measles", "Pertussis"), captions(field));
	}

	@Test
	public void sortsWithTheCollatorOfTheUserLanguage() {

		I18nProperties.setUserLanguage(Language.DE);
		// plain String comparison would put "Äpfel" after "Zebra" because 'Ä' (U+00C4) > 'Z'
		ComboBox field = createComboBox("Z", "Zebra", "A", "Äpfel", "B", "Birne");

		form.sort(field);

		assertEquals(Arrays.asList("Äpfel", "Birne", "Zebra"), captions(field));
	}

	@Test
	public void sortingIgnoresCaseOfCaptions() {

		ComboBox field = createComboBox("B", "beta", "A", "Alpha", "C", "Gamma");

		form.sort(field);

		assertEquals(Arrays.asList("Alpha", "beta", "Gamma"), captions(field));
	}

	@Test
	public void placesItemsWithEmptyCaptionLast() {

		ComboBox field = createComboBox("EMPTY", "", "MEAS", "Measles", "DENG", "Dengue");

		form.sort(field);

		assertEquals(Arrays.asList("DENG", "MEAS", "EMPTY"), itemIds(field));
		assertEquals("", field.getItemCaption("EMPTY"));
	}

	@Test
	public void keepsTheSelectedValue() {

		ComboBox field = createComboBox("PERT", "Pertussis", "MEAS", "Measles", "DENG", "Dengue");
		field.setValue("MEAS");

		form.sort(field);

		assertEquals("MEAS", field.getValue());
		assertEquals(Arrays.asList("DENG", "MEAS", "PERT"), itemIds(field));
	}

	@Test
	public void leavesReadOnlyFieldUnchanged() {

		ComboBox field = createComboBox("PERT", "Pertussis", "MEAS", "Measles", "DENG", "Dengue");
		field.setValue("PERT");
		field.setReadOnly(true);

		assertDoesNotThrow(() -> form.sort(field));

		assertEquals(Arrays.asList("PERT", "MEAS", "DENG"), itemIds(field));
		assertEquals("PERT", field.getValue());
	}

	@Test
	public void sortsItemsWhoseCaptionComesFromTheItemId() {

		ComboBox field = new ComboBox();
		field.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID);
		field.addItem("Pertussis");
		field.addItem("Measles");
		field.addItem("Dengue");

		form.sort(field);

		assertEquals(Arrays.asList("Dengue", "Measles", "Pertussis"), itemIds(field));
	}

	@Test
	public void doesNothingForNullEmptyOrSingleItemField() {

		assertDoesNotThrow(() -> form.sort(null));

		ComboBox emptyField = createComboBox();
		assertDoesNotThrow(() -> form.sort(emptyField));
		assertTrue(emptyField.getItemIds().isEmpty());

		ComboBox singleItemField = createComboBox("MEAS", "Measles");
		assertDoesNotThrow(() -> form.sort(singleItemField));
		assertEquals(Arrays.asList("MEAS"), itemIds(singleItemField));
		assertEquals("Measles", singleItemField.getItemCaption("MEAS"));
	}

	private static ComboBox subjectCodeField(Field<?> field) {
		return assertInstanceOf(ComboBox.class, field);
	}

	/**
	 * Compares the captions in dropdown order with the same captions sorted by the English collator; the form tests run
	 * with the default user language (English).
	 */
	private static void assertSortedByCaption(ComboBox field) {

		List<String> captions = captions(field);
		List<String> expected = new ArrayList<>(captions);
		Comparator<Object> collator = Collator.getInstance(Locale.ENGLISH);
		expected.sort(collator);

		assertEquals(expected, captions);
	}

	/**
	 * Creates a dropdown whose captions are held in the {@code CAPTION_PROPERTY_ID} property, like the enum dropdowns
	 * created by {@link SormasFieldGroupFieldFactory}; arguments are pairs of item id and caption.
	 */
	@SuppressWarnings("unchecked")
	private static ComboBox createComboBox(String... idsAndCaptions) {

		ComboBox field = new ComboBox();
		field.addContainerProperty(CAPTION_PROPERTY_ID, String.class, "");
		field.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);
		for (int i = 0; i + 1 < idsAndCaptions.length; i += 2) {
			Item item = field.addItem(idsAndCaptions[i]);
			item.getItemProperty(CAPTION_PROPERTY_ID).setValue(idsAndCaptions[i + 1]);
		}
		return field;
	}

	private static List<Object> itemIds(ComboBox field) {
		return new ArrayList<>(field.getItemIds());
	}

	private static List<String> captions(ComboBox field) {
		return field.getItemIds().stream().map(field::getItemCaption).collect(Collectors.toList());
	}

	/**
	 * Minimal form without fields; {@link #sort(ComboBox)} exposes the protected {@code sortItemsByCaption} to this test.
	 */
	private static class TestForm extends AbstractForm<TestBean> {

		private static final long serialVersionUID = 1L;

		TestForm() {
			super(TestBean.class, "test", new SormasFieldGroupFieldFactory(null, null), false);
		}

		void sort(ComboBox field) {
			sortItemsByCaption(field);
		}

		@Override
		protected String createHtmlLayout() {
			return "";
		}

		@Override
		protected void addFields() {
		}
	}

	public static class TestBean {
	}
}
