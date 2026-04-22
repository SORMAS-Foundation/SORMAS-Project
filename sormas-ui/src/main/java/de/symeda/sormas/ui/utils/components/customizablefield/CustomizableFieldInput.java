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

package de.symeda.sormas.ui.utils.components.customizablefield;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Binder;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Abstract base for editable customizable field input components (Vaadin v8).
 * <p>
 * Each concrete subclass is responsible for a specific {@link de.symeda.sormas.api.customizablefield.CustomizableFieldType}.
 * The base class handles metadata-driven configuration (caption, mandatory indicator, read-only state)
 * and owns a {@link Binder} that keeps {@link CustomizableFieldValueDto#getValue() dto.value} in sync
 * with the widget in both directions:
 * <ul>
 * <li><b>DTO → widget:</b> {@link #setFieldValue(CustomizableFieldValueDto)} calls {@link Binder#setBean},
 * which reads the DTO and pushes the value into this {@code CustomField} via {@link #doSetValue}.</li>
 * <li><b>widget → DTO:</b> subclasses must call {@link #setValue(Object)} whenever the inner widget
 * changes (typically via a value-change listener); the binder immediately writes the new value
 * into the bean so {@link #getFieldValue()} never needs a manual flush.</li>
 * </ul>
 * <p>
 * Subclass contract:
 * <ul>
 * <li>{@link #buildInputComponent()} – called once by {@link #initContent()}; return the inner editable widget
 * and wire a value-change listener that calls {@link #setValue(Object)}.</li>
 * <li>{@link #applyValueToWidget(Object)} – propagates the value down to the inner widget;
 * called by the final {@link #doSetValue(Object)} after storing the new value internally.</li>
 * <li>{@link #configureBinding(Binder.BindingBuilder)} – optional; override to attach
 * {@link com.vaadin.data.Validator}s to the binding before it is finalised.</li>
 * </ul>
 */
@SuppressWarnings("java:S2160") // sonar missing equals, ok for Vaadin UI components
public abstract class CustomizableFieldInput<T> extends CustomField<T> {

	private static final long serialVersionUID = 1L;

	private final CustomizableFieldMetadataDto fieldMetadata;
	private final Binder<CustomizableFieldValueDto> binder;
	/**
	 * Stores the current logical value of this field.
	 * <p>
	 * {@link #getValue()} reads from here instead of from the inner widget.
	 * This ensures that {@link com.vaadin.ui.AbstractField#setValue(Object)} can
	 * correctly detect value changes: when the inner widget fires a change event
	 * and the listener calls {@link #setValue(Object)}, the old logical value is
	 * still present here, so the equality check succeeds and a
	 * {@link com.vaadin.data.HasValue.ValueChangeEvent} is fired to notify the binder.
	 * The field is updated inside {@link #doSetValue(Object)}, which is invoked by
	 * Vaadin only after the equality check has already passed.
	 */
	private transient T currentValue;

	/**
	 * Constructs the input and wires a {@link Binder} that keeps the DTO's {@code value} field
	 * continuously in sync with the widget state.
	 *
	 * @param metadata
	 *            field metadata; must not be {@code null}
	 */
	protected CustomizableFieldInput(CustomizableFieldMetadataDto metadata) {
		this.fieldMetadata = Objects.requireNonNull(metadata, "fieldMetadata must not be null");

		binder = new Binder<>(CustomizableFieldValueDto.class);
		Binder.BindingBuilder<CustomizableFieldValueDto, T> bindingBuilder = binder.forField(this);
		if (metadata.isMandatory()) {
			bindingBuilder = bindingBuilder.asRequired();
		}
		bindingBuilder = configureBinding(bindingBuilder);
		bindingBuilder.bind(getValueGetter(), getValueSetter());

		applyMetadata();
	}

	/**
	 * Optional hook called during construction, just before the binder binding is finalised.
	 * Subclasses can override this to attach additional {@link com.vaadin.data.Validator}s
	 * (e.g. pattern validators for numeric types).
	 * <p>
	 * <b>Note:</b> this method is called from the superclass constructor. Overrides must
	 * only reference compile-time constants or static members, never uninitialized instance fields.
	 *
	 * @param builder
	 *            the in-progress binding builder; never {@code null}
	 * @return the (possibly augmented) builder; must not be {@code null}
	 */
	protected Binder.BindingBuilder<CustomizableFieldValueDto, T> configureBinding(Binder.BindingBuilder<CustomizableFieldValueDto, T> builder) {
		return builder;
	}

	/**
	 * Build and return the inner editable UI component.
	 * Called exactly once during {@link #initContent()}.
	 * <p>
	 * Implementations should wire a value-change listener on the inner widget that
	 * calls {@link #setValue(Object)} so that the value stored in the field's internal
	 * state stays in sync.
	 *
	 * @return the inner component; never {@code null}
	 */
	protected abstract Component buildInputComponent();

	@Override
	protected final Component initContent() {
		return buildInputComponent();
	}

	/**
	 * Returns the current logical value of this field.
	 * <p>
	 * Reads from the internal {@link #currentValue} store rather than from the inner widget,
	 * so that {@link com.vaadin.ui.AbstractField#setValue(Object)} can detect changes correctly:
	 * when the inner widget fires a change and the listener calls {@link #setValue(Object)},
	 * the old value is still present here, allowing the equality check to pass and a
	 * {@link com.vaadin.data.HasValue.ValueChangeEvent} to be fired to the binder.
	 */
	@Override
	public T getValue() {
		return currentValue;
	}

	/**
	 * Called by Vaadin when a new value is set on this field (via {@link #setValue(Object)}).
	 * Stores the new value in {@link #currentValue}, then delegates widget propagation to
	 * {@link #applyValueToWidget(Object)}.
	 * <p>
	 * Made {@code final} so that subclasses cannot accidentally bypass the value-storage step.
	 */
	@Override
	protected final void doSetValue(T value) {
		this.currentValue = value;
		applyValueToWidget(value);
	}

	/**
	 * Propagates {@code value} down to the inner UI widget.
	 * Called by {@link #doSetValue(Object)} after the new value has been stored.
	 * <p>
	 * If the inner widget has not been created yet (component not yet rendered),
	 * implementations should stash the value as a pending value and apply it in
	 * {@link #buildInputComponent()}.
	 *
	 * @param value
	 *            the new value to display; may be {@code null}
	 */
	protected abstract void applyValueToWidget(T value);

	/**
	 * Returns a {@link ValueProvider} that reads the typed value from a {@link CustomizableFieldValueDto}.
	 * Called once during construction; implementations must only reference compile-time constants.
	 */
	protected abstract ValueProvider<CustomizableFieldValueDto, T> getValueGetter();

	/**
	 * Returns a {@link Setter} that writes the typed value back to a {@link CustomizableFieldValueDto}.
	 * Called once during construction; implementations must only reference compile-time constants.
	 */
	protected abstract Setter<CustomizableFieldValueDto, T> getValueSetter();

	/**
	 * Sets the {@link CustomizableFieldValueDto} as the binder's active bean.
	 * The binder reads {@code dto.getValue()} and pushes it into the widget immediately.
	 * From this point on every user edit automatically writes back to the same DTO instance.
	 * Pass {@code null} to detach the current bean and clear the widget.
	 *
	 * @param fieldValue
	 *            the value DTO, or {@code null} to clear
	 */
	public void setFieldValue(CustomizableFieldValueDto fieldValue) {
		binder.setBean(fieldValue);
	}

	/**
	 * Returns the currently bound {@link CustomizableFieldValueDto}.
	 * Because the binder writes user edits to the bean immediately, the returned DTO
	 * always reflects the current widget state — no manual flush required.
	 *
	 * @return the live bean, or {@code null} if none was set
	 */
	public CustomizableFieldValueDto getFieldValue() {
		return binder.getBean();
	}

	/**
	 * Returns the metadata that configures this input.
	 *
	 * @return field metadata; never {@code null}
	 */
	public CustomizableFieldMetadataDto getFieldMetadata() {
		return fieldMetadata;
	}

	/**
	 * Applies caption, mandatory indicator and read-only state from metadata.
	 * The caption and description are resolved from the translations map for the current user language,
	 * falling back to the stored name/description when no matching translation exists.
	 * Called once in the constructor, before the inner component is built.
	 */
	private void applyMetadata() {
		String caption = resolveTranslation(CustomizableFieldMetadataDto.NAME, fieldMetadata.getName());
		if (StringUtils.isNotBlank(caption)) {
			setCaption(caption);
		}
		String description = resolveTranslation(CustomizableFieldMetadataDto.DESCRIPTION, fieldMetadata.getDescription());
		if (StringUtils.isNotBlank(description)) {
			setDescription(description);
		}
		setRequiredIndicatorVisible(fieldMetadata.isMandatory());
		setReadOnly(fieldMetadata.isReadOnly());
	}

	/**
	 * Looks up {@code key} (e.g. "name" or "description") in the metadata's translations map
	 * for the current user language. Tries the full locale string first (e.g. "de_DE"), then
	 * falls back to the language-only prefix (e.g. "de"), then to {@code fallback}.
	 */
	private String resolveTranslation(String key, String fallback) {
		Map<String, Map<String, String>> translations = fieldMetadata.getTranslations();
		if (translations != null) {
			Language userLanguage = I18nProperties.getUserLanguage();
			if (userLanguage != null) {
				String localeStr = userLanguage.getLocale().toString();
				String translated = getTranslationFromMap(translations, localeStr, key);
				if (translated != null) {
					return translated;
				}
				// Try language-only prefix (e.g. "en" from "en_GB")
				int underscore = localeStr.indexOf('_');
				if (underscore > 0) {
					translated = getTranslationFromMap(translations, localeStr.substring(0, underscore), key);
					if (translated != null) {
						return translated;
					}
				}
			}
		}
		return fallback;
	}

	private static String getTranslationFromMap(Map<String, Map<String, String>> translations, String localeKey, String key) {
		Map<String, String> langMap = translations.get(localeKey);
		if (langMap != null) {
			String value = langMap.get(key);
			if (StringUtils.isNotBlank(value)) {
				return value;
			}
		}
		return null;
	}
}
