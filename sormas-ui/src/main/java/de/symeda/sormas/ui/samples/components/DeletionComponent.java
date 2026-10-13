/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples.components;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;

import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.utils.FormComponent;

/**
 * Deletion reason fields using Vaadin 8 components with own Binder.
 * Self-manages visibility of otherDeletionReason based on deletionReason value.
 */
public class DeletionComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private ComboBox<DeletionReason> deletionReasonField;
	private TextArea otherReasonField;

	public DeletionComponent() {
		super(PathogenTestDto.class);
		buildLayout();
		bindFields();
	}

	private void buildLayout() {
		deletionReasonField = createComboBox(PathogenTestDto.DELETION_REASON, PathogenTestDto.I18N_PREFIX);
		deletionReasonField.setItems(DeletionReason.values());
		deletionReasonField.setItemCaptionGenerator(DeletionReason::toString);
		addFullWidthRow(deletionReasonField);

		otherReasonField = createTextArea(PathogenTestDto.OTHER_DELETION_REASON, PathogenTestDto.I18N_PREFIX);
		otherReasonField.setRows(3);
		addFullWidthRow(otherReasonField);

		// Hidden by default
		deletionReasonField.setVisible(false);
		otherReasonField.setVisible(false);

		// Self-managed visibility: show otherReason only when OTHER_REASON selected
		track(deletionReasonField.addValueChangeListener(e -> {
			boolean showOther = e.getValue() == DeletionReason.OTHER_REASON;
			otherReasonField.setVisible(showOther);
			if (!showOther) {
				otherReasonField.clear();
			}
			updateRowAndSelfVisibility();
		}));
	}

	private void bindFields() {
		binder.forField(deletionReasonField).bind(PathogenTestDto::getDeletionReason, PathogenTestDto::setDeletionReason);
		binder.forField(otherReasonField).bind(PathogenTestDto::getOtherDeletionReason, PathogenTestDto::setOtherDeletionReason);
	}

	/** Shows the deletion reason field (and other reason if applicable) for a deleted record. */
	public void showForDeletedRecord(DeletionReason reason) {
		deletionReasonField.setVisible(true);
		otherReasonField.setVisible(reason == DeletionReason.OTHER_REASON);
		updateRowAndSelfVisibility();
	}
}
