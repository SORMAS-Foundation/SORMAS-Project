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

package de.symeda.sormas.ui.labmessage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;

public class CorrectionPanel<T> extends HorizontalLayout {

	private static final long serialVersionUID = -8809131575832967278L;

	private Button cancelButton;
	private CommitDiscardWrapperComponent<AbstractEditForm<T>> commitDiscardForm;

	public CorrectionPanel(
		Supplier<AbstractEditForm<T>> formSupplier,
		T formData,
		T updatedFormData,
		String originalDataFormHeaderTag,
		String editFormHeaderTag,
		List<String[]> changedFields) {
		setHeightFull();
		setMargin(true);

		addStyleName(CssStyles.LAB_MESSAGE_NO_REQUiRED_BORDER);
		addComponent(createSplitPanel(formSupplier, formData, updatedFormData, originalDataFormHeaderTag, editFormHeaderTag, changedFields));
	}

	private Panel createSplitPanel(
		Supplier<AbstractEditForm<T>> formSupplier,
		T formData,
		T updatedFormData,
		String originalDataFormHeaderTag,
		String editFormHeaderTag,
		List<String[]> changedFields) {
		AbstractEditForm<T> originalDataForm = formSupplier.get();
		originalDataForm.setValue(formData);
		originalDataForm.setEnabled(false);
		originalDataForm.setWidthUndefined();
		originalDataForm.setHeading(I18nProperties.getString(originalDataFormHeaderTag));

		AbstractEditForm<T> updateForm = formSupplier.get();
		updateForm.setValue(updatedFormData);
		updateForm.setWidthUndefined();
		updateForm.setHeading(I18nProperties.getString(editFormHeaderTag));
		markChangedFields(updateForm, changedFields);

		commitDiscardForm = new CommitDiscardWrapperComponent<>(updateForm, updateForm.getFieldGroup());
		cancelButton = LabMessageUiHelper.addCancelAndUpdateLabels(commitDiscardForm, Captions.actionDiscardAllAndContinue);

		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel(originalDataForm, commitDiscardForm);
		splitPanel.setSizeFull();
		splitPanel.setSplitPosition(50, Unit.PERCENTAGE);

		Panel panel = new Panel();
		panel.setHeightFull();
		panel.setContent(splitPanel);
		return panel;
	}

	private void markChangedFields(AbstractEditForm<T> form, List<String[]> changedFields) {
		iterateFields(form, (field, path) -> {
			boolean isChanged = changedFields.stream().anyMatch(changedPath -> Objects.deepEquals(changedPath, path));
			if (isChanged) {
				markChanged(field);
			}

			Object originalValue = field.getValue();
			field.addValueChangeListener(e -> {
				if (DataHelper.equal(e.getProperty().getValue(), originalValue)) {
					if (!isChanged) {
						unmarkChanged(field);
					}
				} else {
					markChanged(field);
				}
			});
		});
	}

	private void markChanged(Field<?> field) {
		field.addStyleName(CssStyles.LAB_MESSAGE_PROCESSING_BORDER_DIRTY);
	}

	private void unmarkChanged(Field<?> field) {
		field.removeStyleName(CssStyles.LAB_MESSAGE_PROCESSING_BORDER_DIRTY);
	}

	private void iterateFields(AbstractEditForm<T> form, BiConsumer<Field<?>, String[]> action) {
		Collection<Field<?>> fields = form.getFieldGroup().getFields();

		fields.forEach(field -> {
			String[] fieldPath = new String[] {
				field.getId() };

			if (AbstractEditForm.class.isAssignableFrom(field.getClass())) {
				iterateFields((AbstractEditForm) field, (subField, p) -> {
					action.accept(subField, ArrayUtils.addAll(fieldPath, p));
				});
			} else {
				action.accept(field, fieldPath);
			}
		});
	}

	public void setCancelListener(Button.ClickListener listener) {
		cancelButton.addClickListener(listener);
	}

	public void setDiscardListener(CommitDiscardWrapperComponent.DiscardListener listener) {
		commitDiscardForm.addDiscardListener(listener);
	}

	public void setCommitListener(Consumer<T> listener) {
		commitDiscardForm.addCommitListener(() -> {
			listener.accept(commitDiscardForm.getWrappedComponent().getValue());
		});
	}

	public void disableContinueButtons() {
		commitDiscardForm.getCommitButton().setEnabled(false);
		commitDiscardForm.getDiscardButton().setEnabled(false);
	}
}
