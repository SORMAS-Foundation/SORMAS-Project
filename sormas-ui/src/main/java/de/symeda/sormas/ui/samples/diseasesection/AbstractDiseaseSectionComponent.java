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
package de.symeda.sormas.ui.samples.diseasesection;

import java.util.function.Consumer;

import com.vaadin.data.HasValue;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.v7.data.fieldgroup.FieldGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Base class for disease-specific sections in the pathogen test form.
 * Extends {@link FormComponent} and adds disease-section-specific lifecycle:
 * <ul>
 * <li>Deferred initialization via {@link #initialize}</li>
 * <li>Mid-lifecycle {@link #cleanup()} that clears owned DTO fields before unbinding</li>
 * <li>Legacy {@link FieldGroup} support for DrugSusceptibilityForm</li>
 * <li>Drug susceptibility field management</li>
 * </ul>
 */
public abstract class AbstractDiseaseSectionComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	protected FormEventBus eventBus;
	protected PathogenTestFormConfig config;
	protected Disease disease;

	/** Kept only for DrugSusceptibilityForm legacy binding */
	protected FieldGroup fieldGroup;

	private Component drugSusceptibilityField;
	private Consumer<Boolean> visibilityCallback;

	protected AbstractDiseaseSectionComponent() {
		super(PathogenTestDto.class);
	}

	public void setVisibilityCallback(Consumer<Boolean> callback) {
		this.visibilityCallback = callback;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visibilityCallback != null) {
			visibilityCallback.accept(visible);
		}
	}

	public void initialize(FieldGroup fieldGroup, FormEventBus eventBus, PathogenTestFormConfig config, Disease disease) {
		this.fieldGroup = fieldGroup;
		this.eventBus = eventBus;
		this.config = config;
		this.disease = disease;

		buildLayout();
		wireVisibility();
	}

	protected abstract void buildLayout();

	protected abstract void wireVisibility();

	/**
	 * Clears owned DTO fields and unbinds. Called explicitly before section swap
	 * to prevent stale values from being persisted when the disease changes.
	 */
	public void cleanup() {
		removeRegistrations();
		clearOwnedFields();
		binder.removeBean();
		unbindLegacyFields();
	}

	/**
	 * Subclasses null out all DTO properties they own.
	 * Called during cleanup() while the binder still holds the bean.
	 */
	protected abstract void clearOwnedFields();

	/** Override to unbind any FieldGroup-bound legacy fields (e.g. DrugSusceptibilityForm) */
	protected void unbindLegacyFields() {
	}

	protected void addDrugSusceptibilityField(Component field) {
		this.drugSusceptibilityField = field;
		field.setVisible(false);
		addComponent(field);
	}

	protected void setDrugSusceptibilityRowVisible(boolean visible) {
		if (drugSusceptibilityField != null) {
			drugSusceptibilityField.setVisible(visible);
			updateRowAndSelfVisibility();
		}
	}

	/**
	 * Visible clear
	 *
	 * @param components
	 */
	public void setVisibleClear(boolean isVisible, AbstractComponent... components) {
		if (components == null) {
			return;
		}
		for (AbstractComponent component : components) {
			component.setVisible(isVisible);
			if (component instanceof HasValue) {
				((HasValue<?>) component).clear();
			}
		}
	}

	@Override
	protected boolean hasVisibleContent() {
		return drugSusceptibilityField != null && drugSusceptibilityField.isVisible();
	}

	protected <V> ComboBox<V> createComboBox(String propertyId) {
		return createComboBox(propertyId, PathogenTestDto.I18N_PREFIX);
	}

	protected TextField createTextField(String propertyId) {
		return createTextField(propertyId, PathogenTestDto.I18N_PREFIX);
	}

	protected CheckBox createCheckBox(String propertyId) {
		return createCheckBox(propertyId, PathogenTestDto.I18N_PREFIX);
	}

	protected RadioButtonGroup<Boolean> createRadioButtonGroup(String propertyId) {
		return createBooleanRadioGroup(propertyId, PathogenTestDto.I18N_PREFIX);
	}

	protected <E extends Enum<E>> RadioButtonGroup<E> createEnumRadioButtonGroup(String propertyId, Class<E> enumClass) {
		return createEnumRadioGroup(propertyId, PathogenTestDto.I18N_PREFIX, enumClass);
	}
}
