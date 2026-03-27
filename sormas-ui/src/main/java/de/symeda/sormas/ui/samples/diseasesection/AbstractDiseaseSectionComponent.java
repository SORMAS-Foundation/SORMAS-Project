package de.symeda.sormas.ui.samples.diseasesection;

import java.util.function.Consumer;

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
