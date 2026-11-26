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

package de.symeda.sormas.ui.exposure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.v7.data.util.ObjectProperty;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.AbstractUiBeanTest;

class ExposuresFieldTest extends AbstractUiBeanTest {

	@Test
	void shouldAllowAddingFurtherExposuresToContact() {
		// a contact person can have been exposed to the source case several times
		ExposuresField field = createField(ContactDto.class, true, 2);

		assertTrue(findAddButton(field).isVisible());
	}

	@Test
	void shouldAllowAddingFirstExposureToContact() {
		ExposuresField field = createField(ContactDto.class, true, 0);

		assertTrue(findAddButton(field).isVisible());
	}

	@Test
	void shouldAllowAddingFurtherExposuresToCase() {
		ExposuresField field = createField(CaseDataDto.class, true, 2);

		assertTrue(findAddButton(field).isVisible());
	}

	@Test
	void shouldNotAllowAddingExposuresWithoutEditRight() {
		ExposuresField field = createField(ContactDto.class, false, 0);

		assertFalse(findAddButton(field).isVisible());
	}

	private static ExposuresField createField(Class<? extends EntityDto> epiDataParentClass, boolean isEditAllowed, int exposureCount) {
		ExposuresField field =
			new ExposuresField(Disease.MUMPS, FieldVisibilityCheckers.withDisease(Disease.MUMPS), UiFieldAccessCheckers.getNoop(), isEditAllowed);
		field.setEpiDataParentClass(epiDataParentClass);

		List<ExposureDto> exposures = new ArrayList<>();
		for (int i = 0; i < exposureCount; i++) {
			exposures.add(ExposureDto.build(ExposureType.WORK));
		}
		field.setPropertyDataSource(new ObjectProperty<>(exposures, List.class));

		return field;
	}

	private static Button findAddButton(Component root) {
		Button button = findButton(root, I18nProperties.getCaption(Captions.actionNewEntry));
		assertTrue(button != null, "new entry button not found");
		return button;
	}

	private static Button findButton(Component root, String caption) {
		if (root instanceof Button && caption.equals(root.getCaption())) {
			return (Button) root;
		}
		if (root instanceof HasComponents) {
			for (Component child : (HasComponents) root) {
				Button button = findButton(child, caption);
				if (button != null) {
					return button;
				}
			}
		}
		return null;
	}
}
