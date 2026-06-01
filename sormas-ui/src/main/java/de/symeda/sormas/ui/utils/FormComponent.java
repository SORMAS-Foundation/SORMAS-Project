package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.ValidationResult;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator.InvalidValueException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestCategory;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.ApplicableToPathogenTests;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

/**
 * Base class for composable form components that participate in the parent form lifecycle.
 * Extends {@link VerticalLayout} so that Vaadin's {@link #detach()} is called automatically
 * when the parent form is removed — ensuring binder unbinding and listener cleanup.
 * <p>
 * Provides optional field factory methods ({@link #createComboBox}, {@link #createTextField}, etc.)
 * that auto-track fields for visibility/access application. Subclasses that build fields manually
 * can override {@link #applyVisibility} and {@link #applyAccess} directly.
 *
 * @param <T>
 *            the DTO type managed by the parent form
 */
public abstract class FormComponent<T> extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	protected final Binder<T> binder;

	private final List<Registration> registrations = new ArrayList<>();
	private final List<Component> trackedFields = new ArrayList<>();
	private final List<HorizontalLayout> trackedRows = new ArrayList<>();

	protected FormComponent(Class<T> dtoClass) {
		binder = new Binder<>(dtoClass);
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(true);
	}

	/** Tracks a registration for automatic removal on detach. */
	protected void track(Registration registration) {
		registrations.add(registration);
	}

	/** Removes all tracked registrations. Called by detach() automatically, but can also be called explicitly for mid-lifecycle cleanup. */
	protected void removeRegistrations() {
		for (Registration reg : registrations) {
			reg.remove();
		}
		registrations.clear();
	}

	/** Tracks a field for automatic visibility/access application. */
	protected void trackField(Component field) {
		trackedFields.add(field);
	}

	protected DateField createDateField(String propertyId, String i18nPrefix) {
		DateField field = new DateField();
		field.setId(propertyId);
		field.setCaption(I18nProperties.getPrefixCaption(i18nPrefix, propertyId));
		CssStyles.style(field, CssStyles.CAPTION_ON_TOP);
		field.setWidth(100, Unit.PERCENTAGE);
		trackField(field);
		return field;
	}

	protected <V> ComboBox<V> createComboBox(String propertyId, String i18nPrefix) {
		ComboBox<V> field = new ComboBox<>();
		field.setId(propertyId);
		field.setCaption(I18nProperties.getPrefixCaption(i18nPrefix, propertyId));
		CssStyles.style(field, CssStyles.CAPTION_ON_TOP);
		field.setWidth(100, Unit.PERCENTAGE);
		trackField(field);
		return field;
	}

	protected TextField createTextField(String propertyId, String i18nPrefix) {
		TextField field = new TextField();
		field.setId(propertyId);
		field.setCaption(I18nProperties.getPrefixCaption(i18nPrefix, propertyId));
		CssStyles.style(field, CssStyles.CAPTION_ON_TOP);
		field.setWidth(100, Unit.PERCENTAGE);
		trackField(field);
		return field;
	}

	protected TextField createTextField(String propertyId, String i18nPrefix, ValueChangeMode mode) {
		TextField field = createTextField(propertyId, i18nPrefix);
		field.setValueChangeMode(mode);
		return field;
	}

	protected TextArea createTextArea(String propertyId, String i18nPrefix) {
		TextArea field = new TextArea();
		field.setId(propertyId);
		field.setCaption(I18nProperties.getPrefixCaption(i18nPrefix, propertyId));
		CssStyles.style(field, CssStyles.CAPTION_ON_TOP);
		field.setWidth(100, Unit.PERCENTAGE);
		trackField(field);
		return field;
	}

	protected CheckBox createCheckBox(String propertyId, String i18nPrefix) {
		CheckBox field = new CheckBox();
		field.setId(propertyId);
		field.setCaption(I18nProperties.getPrefixCaption(i18nPrefix, propertyId));
		CssStyles.style(field, CssStyles.CAPTION_ON_TOP);
		field.setWidth(100, Unit.PERCENTAGE);
		trackField(field);
		return field;
	}

	protected RadioButtonGroup<Boolean> createBooleanRadioGroup(String propertyId, String i18nPrefix) {
		RadioButtonGroup<Boolean> field = new RadioButtonGroup<>();
		field.setId(propertyId);
		field.setCaption(I18nProperties.getPrefixCaption(i18nPrefix, propertyId));
		CssStyles.style(field, CssStyles.CAPTION_ON_TOP, ValoTheme.OPTIONGROUP_HORIZONTAL);
		field.setItems(Boolean.TRUE, Boolean.FALSE);
		field.setItemCaptionGenerator(
			b -> b
				? I18nProperties.getEnumCaption(de.symeda.sormas.api.utils.YesNoUnknown.YES)
				: I18nProperties.getEnumCaption(de.symeda.sormas.api.utils.YesNoUnknown.NO));
		trackField(field);
		return field;
	}

	protected <E extends Enum<E>> RadioButtonGroup<E> createEnumRadioGroup(String propertyId, String i18nPrefix, Class<E> enumClass) {
		RadioButtonGroup<E> field = new RadioButtonGroup<>();
		field.setId(propertyId);
		field.setCaption(I18nProperties.getPrefixCaption(i18nPrefix, propertyId));
		CssStyles.style(field, CssStyles.CAPTION_ON_TOP, ValoTheme.OPTIONGROUP_HORIZONTAL);
		field.setItems(enumClass.getEnumConstants());
		field.setItemCaptionGenerator(I18nProperties::getEnumCaption);
		field.setWidth(100, Unit.PERCENTAGE);
		trackField(field);
		return field;
	}

	/**
	 * Creates a row layout with the given fields, adds it to this component, and tracks it.
	 * If only one field (or two with the second null), a spacer is added for alignment.
	 */
	protected HorizontalLayout addRow(Component... fields) {
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(true);
		for (Component field : fields) {
			if (field != null) {
				row.addComponent(field);
				row.setExpandRatio(field, 1);
			}
		}
		if (fields.length == 1 || (fields.length == 2 && fields[1] == null)) {
			Label spacer = new Label();
			spacer.setWidth(100, Unit.PERCENTAGE);
			row.addComponent(spacer);
			row.setExpandRatio(spacer, 1);
		}
		addComponent(row);
		trackedRows.add(row);
		return row;
	}

	/** Creates a row with custom expand ratios per field. */
	protected HorizontalLayout addRow(float[] expandRatios, Component... fields) {
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(true);
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null) {
				row.addComponent(fields[i]);
				row.setExpandRatio(fields[i], expandRatios[i]);
			}
		}
		addComponent(row);
		trackedRows.add(row);
		return row;
	}

	/** Creates a 3-component row for field + detail + spacer toggle pattern. */
	protected HorizontalLayout addToggleRow(Component main, Component detail, Label spacer) {
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(true);
		row.addComponent(main);
		row.setExpandRatio(main, 1);
		row.addComponent(detail);
		row.setExpandRatio(detail, 1);
		row.addComponent(spacer);
		row.setExpandRatio(spacer, 1);
		addComponent(row);
		trackedRows.add(row);
		return row;
	}

	/** Creates a row with a leading spacer for right-alignment. */
	protected HorizontalLayout addRowWithLeadingSpacer(Component field) {
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(true);
		Label spacer = new Label();
		spacer.setWidth(100, Unit.PERCENTAGE);
		row.addComponent(spacer);
		row.setExpandRatio(spacer, 1);
		row.addComponent(field);
		row.setExpandRatio(field, 1);
		addComponent(row);
		trackedRows.add(row);
		return row;
	}

	/** Creates a single-field row without spacer (full width). */
	protected HorizontalLayout addFullWidthRow(Component field) {
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(true);
		row.addComponent(field);
		row.setExpandRatio(field, 1);
		addComponent(row);
		trackedRows.add(row);
		return row;
	}

	/**
	 * Updates a ComboBox's items to only those enum values visible for the given disease
	 * (based on {@link Diseases} annotations). Preserves the current selection if still valid.
	 */
	protected static <E extends Enum<E>> void updateComboBoxByDisease(ComboBox<E> comboBox, Class<E> enumClass, Disease disease) {
		E currentValue = comboBox.getValue();
		List<E> filtered = Diseases.DiseasesConfiguration.getVisibleValues(enumClass, disease);
		filtered.sort(Comparator.comparing(Object::toString));
		comboBox.setItems(filtered);
		if (currentValue != null && filtered.contains(currentValue)) {
			comboBox.setValue(currentValue);
		}
	}

	/**
	 * Updates a ComboBox's items to only those enum values visible for the given disease
	 * AND applicable to the given pathogen test type (based on {@link Diseases} and
	 * {@link ApplicableToPathogenTests} annotations). Preserves the current selection if still valid.
	 */
	protected static <E extends Enum<E>> void updateComboBoxByDiseaseAndTestType(
		ComboBox<E> comboBox,
		Class<E> enumClass,
		Disease disease,
		PathogenTestType testType) {

		E currentValue = comboBox.getValue();
		List<E> filtered = Diseases.DiseasesConfiguration.getVisibleValues(enumClass, disease).stream().filter(value -> {
			if (testType == null) {
				return true;
			}
			try {
				java.lang.reflect.Field enumField = enumClass.getField(value.name());
				ApplicableToPathogenTests ann = enumField.getAnnotation(ApplicableToPathogenTests.class);
				return ann == null || java.util.Arrays.asList(ann.value()).contains(testType);
			} catch (NoSuchFieldException e) {
				return true;
			}
		}).sorted(Comparator.comparing(Object::toString)).collect(Collectors.toList());
		comboBox.setItems(filtered);
		if (currentValue != null && filtered.contains(currentValue)) {
			comboBox.setValue(currentValue);
		}
	}

	/**
	 * Scopes the pathogen-test method combo to the methods that are visible for the given disease
	 * (via {@link Diseases}), selectable for new tests (via
	 * {@link PathogenTestType#isSelectableForNewTests}) and — when a category is selected — belong to
	 * that {@link PathogenTestCategory}. With no category selected the combo shows all
	 * selectable, disease-visible methods. The currently saved value is always kept selectable so an
	 * existing test recorded with a now-hidden method can still be edited. Sorted alphabetically.
	 */
	protected static void updateTestTypeItemsByCategory(
		ComboBox<PathogenTestType> comboBox,
		Disease disease,
		PathogenTestCategory category) {
		updateTestTypeItemsByCategory(comboBox, disease, category, comboBox.getValue());
	}

	/**
	 * As {@link #updateTestTypeItemsByCategory(ComboBox, Disease, PathogenTestCategory)} but with an
	 * explicit {@code retainType} that is always kept in the list (and its category honoured) even if it
	 * is hidden or not selectable — used when loading an existing test whose method may be legacy.
	 */
	protected static void updateTestTypeItemsByCategory(
		ComboBox<PathogenTestType> comboBox,
		Disease disease,
		PathogenTestCategory category,
		PathogenTestType retainType) {

		List<PathogenTestType> filtered = Diseases.DiseasesConfiguration.getVisibleValues(PathogenTestType.class, disease)
			.stream()
			.filter(type -> type == retainType || PathogenTestType.isSelectableForNewTests(type))
			.filter(type -> type == retainType || category == null || PathogenTestType.getCategory(type) == category)
			.collect(Collectors.toList());
		// getVisibleValues drops values marked @Diseases(hide = true) for this disease, so the
		// type == retainType guards above cannot re-add a saved legacy method that is hidden here.
		// Add it explicitly so an existing test recorded with such a method still loads and displays.
		if (retainType != null && !filtered.contains(retainType)) {
			filtered.add(retainType);
		}
		filtered.sort(Comparator.comparing(Object::toString));
		comboBox.setItems(filtered);
		if (retainType != null && filtered.contains(retainType)) {
			comboBox.setValue(retainType);
		}
	}

	/**
	 * Lists the {@link PathogenTestCategory} values that have at least one selectable method visible
	 * for the given disease, so empty categories are not offered. Sorted alphabetically.
	 */
	protected static List<PathogenTestCategory> getVisibleTestCategories(Disease disease) {
		return getVisibleTestCategories(disease, null);
	}

	/**
	 * As {@link #getVisibleTestCategories(Disease)} but additionally includes {@code retainCategory}
	 * even when no selectable, disease-visible method maps to it — used when loading an existing test
	 * whose (possibly legacy/hidden) method's category would otherwise be absent from the selector.
	 */
	protected static List<PathogenTestCategory> getVisibleTestCategories(Disease disease, PathogenTestCategory retainCategory) {
		List<PathogenTestCategory> categories = Diseases.DiseasesConfiguration.getVisibleValues(PathogenTestType.class, disease)
			.stream()
			.filter(PathogenTestType::isSelectableForNewTests)
			.map(PathogenTestType::getCategory)
			.filter(java.util.Objects::nonNull)
			.distinct()
			.collect(Collectors.toList());
		if (retainCategory != null && !categories.contains(retainCategory)) {
			categories.add(retainCategory);
		}
		categories.sort(Comparator.comparing(Object::toString));
		return categories;
	}

	protected Label createSpacer() {
		Label spacer = new Label();
		spacer.setWidth(100, Unit.PERCENTAGE);
		return spacer;
	}

	/** Called when the parent form receives a new DTO value. */
	public void setDto(T dto) {
		binder.setBean(dto);
	}

	/** Validates this component's fields; throws {@link InvalidValueException} on failure. */
	public void validate() {
		validateBinder(binder.validate());
	}

	/**
	 * Applies annotation-based field visibility rules.
	 * Default implementation uses tracked fields; subclasses may override for manual control.
	 */
	public void applyVisibility(FieldVisibilityCheckers checkers, Class<?> dtoClass) {
		for (Component field : trackedFields) {
			String id = field.getId();
			if (id != null && !checkers.isVisible(dtoClass, id)) {
				field.setVisible(false);
			}
		}
		updateRowAndSelfVisibility();
	}

	/**
	 * Applies annotation-based field access/editability rules.
	 * Default implementation uses tracked fields; subclasses may override for manual control.
	 */
	public void applyAccess(UiFieldAccessCheckers checkers, Class<?> dtoClass) {
		for (Component field : trackedFields) {
			String id = field.getId();
			if (id != null && !checkers.isAccessible(dtoClass, id)) {
				field.setEnabled(false);
				field.addStyleName(CssStyles.INACCESSIBLE_FIELD);
			}
		}
	}

	/** Subclasses perform any additional cleanup beyond binder unbinding. */
	protected void onCleanup() {
	}

	@Override
	public void detach() {
		for (Registration reg : registrations) {
			reg.remove();
		}
		registrations.clear();
		binder.removeBean();
		onCleanup();
		super.detach();
	}

	/**
	 * Updates visibility of tracked rows and this component itself.
	 * Hides rows where all fields are hidden, and hides this section entirely
	 * if no rows are visible.
	 */
	protected void updateRowAndSelfVisibility() {
		for (HorizontalLayout row : trackedRows) {
			updateRowVisibility(row);
		}

		boolean anyVisible = hasVisibleContent();
		for (HorizontalLayout row : trackedRows) {
			if (row.isVisible()) {
				anyVisible = true;
				break;
			}
		}
		setVisible(anyVisible);
	}

	/**
	 * Subclasses override to indicate additional visible content beyond tracked rows
	 * (e.g. drug susceptibility fields). Default returns false.
	 */
	protected boolean hasVisibleContent() {
		return false;
	}

	/**
	 * Validates a Binder and throws an {@link InvalidValueException} if validation fails.
	 * Creates a structured exception hierarchy so that field captions appear in the error notification.
	 */
	public static <T> void validateBinder(BinderValidationStatus<T> status) {
		if (status.hasErrors()) {
			List<InvalidValueException> fieldCauses = new ArrayList<>();

			for (BindingValidationStatus<?> fieldStatus : status.getFieldValidationErrors()) {
				String caption = null;
				if (fieldStatus.getField() instanceof Component) {
					caption = ((Component) fieldStatus.getField()).getCaption();
				}
				String errorMsg = fieldStatus.getMessage().orElse(caption != null ? caption : "");
				fieldCauses.add(new InvalidValueException(caption != null ? caption : errorMsg));
			}

			for (ValidationResult beanError : status.getBeanValidationErrors()) {
				fieldCauses.add(new InvalidValueException(beanError.getErrorMessage()));
			}

			if (fieldCauses.isEmpty()) {
				String message = status.getValidationErrors().stream().map(ValidationResult::getErrorMessage).collect(Collectors.joining("; "));
				throw new InvalidValueException(message);
			}

			String joinedCaptions = fieldCauses.stream().map(InvalidValueException::getMessage).collect(Collectors.joining(", "));
			throw new InvalidValueException(joinedCaptions, fieldCauses.toArray(new InvalidValueException[0]));
		}
	}

	/**
	 * Hides a layout row if none of its actual field children are visible (ignores spacer Labels).
	 */
	public static void updateRowVisibility(AbstractOrderedLayout row) {
		boolean anyChildVisible = false;
		for (int i = 0; i < row.getComponentCount(); i++) {
			Component child = row.getComponent(i);
			if (child instanceof Label) {
				continue;
			}
			if (child instanceof AbstractOrderedLayout) {
				updateRowVisibility((AbstractOrderedLayout) child);
			}
			if (child.isVisible()) {
				anyChildVisible = true;
			}
		}
		row.setVisible(anyChildVisible);
	}
}
