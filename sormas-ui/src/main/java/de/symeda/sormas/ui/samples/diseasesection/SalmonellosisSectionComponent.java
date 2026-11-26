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
package de.symeda.sormas.ui.samples.diseasesection;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.Serotype;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

public class SalmonellosisSectionComponent extends AbstractDiseaseSectionComponent {

	private static final long serialVersionUID = 1L;

	private ComboBox<Serotype> serotypeField;
	private TextField serotypeTextField;
	private Label serotypeTextSpacer;

	private PathogenTestType currentTestType;

	@Override
	protected void buildLayout() {
		serotypeField = createComboBox(PathogenTestDto.SEROTYPE);
		serotypeField.setItemCaptionGenerator(Serotype::toString);
		serotypeField.setVisible(false);
		updateComboBoxByDisease(serotypeField, Serotype.class, disease);

		serotypeTextField = createTextField(PathogenTestDto.SEROTYPE_TEXT);
		serotypeTextField.setVisible(false);
		serotypeTextSpacer = createSpacer();
		addToggleRow(serotypeField, serotypeTextField, serotypeTextSpacer);

		binder.forField(serotypeField).bind(PathogenTestDto::getSerotype, PathogenTestDto::setSerotype);
		binder.forField(serotypeTextField).bind(PathogenTestDto::getSerotypeText, PathogenTestDto::setSerotypeText);
	}

	@Override
	protected void wireVisibility() {
		track(serotypeField.addValueChangeListener(e -> {
			boolean showText = serotypeField.isVisible() && e.getValue() == Serotype.OTHER;
			serotypeTextField.setVisible(showText);
			serotypeTextSpacer.setVisible(!showText);
			if (!showText) {
				serotypeTextField.clear();
			}
			updateRowAndSelfVisibility();
		}));

		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();

			// Keeps the DefaultSectionComponent behaviour
			if (currentTestType != null) {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));
	}

	private void updateVisibility() {
		boolean showSerotype = currentTestType == PathogenTestType.SEROTYPING;
		serotypeField.setVisible(showSerotype);

		if (!showSerotype) {
			setVisibleClear(false, serotypeField, serotypeTextField);
			serotypeTextSpacer.setVisible(true);
		}

		updateRowAndSelfVisibility();
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setSerotype(null);
		dto.setSerotypeText(null);
	}

	ComboBox<Serotype> serotypeField() {
		return serotypeField;
	}

	TextField serotypeTextField() {
		return serotypeTextField;
	}
}
