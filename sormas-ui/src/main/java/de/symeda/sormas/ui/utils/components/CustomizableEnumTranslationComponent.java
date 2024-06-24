/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.CustomField;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class CustomizableEnumTranslationComponent extends CustomField<List<CustomizableEnumTranslation>> {

	private VerticalLayout rowsLayout;
	private List<TranslationRow> rows;
	private Label lblNoTranslations;
	private List<CustomizableEnumTranslation> translations;

	@Override
	protected Component initContent() {

		VerticalLayout layout = new VerticalLayout();
		layout.setWidthFull();
		layout.setMargin(new MarginInfo(false, false, true, false));
		layout.setSpacing(false);
		CssStyles.style(layout, CssStyles.VSPACE_TOP_4);

		lblNoTranslations = new Label(I18nProperties.getString(Strings.infoNoCustomizableEnumTranslations));
		layout.addComponent(lblNoTranslations);

		rowsLayout = new VerticalLayout();
		rowsLayout.setWidthFull();
		rowsLayout.setMargin(false);
		rowsLayout.setSpacing(false);
		layout.addComponent(rowsLayout);

		if (rows != null) {
			buildRowsLayout();
			updateNoTranslationsLabelVisibility();
		}

		Button btnAdd = ButtonHelper
			.createIconButtonWithCaption(null, null, VaadinIcons.PLUS, e -> buildTranslationRow(null, null, true), CssStyles.VSPACE_TOP_5);
		btnAdd.setHeight(25, Unit.PIXELS);
		btnAdd.setWidthFull();
		layout.addComponent(btnAdd);

		return layout;
	}

	@Override
	public Class<? extends List<CustomizableEnumTranslation>> getType() {
		//noinspection unchecked,InstantiatingObjectToGetClassObject,InstantiatingObjectToGetClassObject
		return (Class<? extends List<CustomizableEnumTranslation>>) new ArrayList<CustomizableEnumTranslation>(0).getClass();
	}

	private void buildRowsLayout() {

		if (rowsLayout == null) {
			return;
		}

		rowsLayout.removeAllComponents();
		rows.forEach(r -> rowsLayout.addComponent(r));
	}

	private void buildTranslationRows() {

		rows = new ArrayList<>();
		if (translations != null) {
			translations.forEach(t -> buildTranslationRow(Language.fromLocaleString(t.getLanguageCode()), t.getValue()));
		}
	}

	private void buildTranslationRow(Language language, String caption) {
		buildTranslationRow(language, caption, false);
	}

	private void buildTranslationRow(Language language, String caption, boolean render) {

		TranslationRow row = new TranslationRow(language, caption);
		row.setDeleteCallback(() -> {
			rows.remove(row);
			rowsLayout.removeComponent(row);
			updateNoTranslationsLabelVisibility();
		});
		rows.add(row);
		updateNoTranslationsLabelVisibility();

		if (render) {
			rowsLayout.addComponent(row);
		}
	}

	@Override
	public void setValue(List<CustomizableEnumTranslation> newFieldValue) throws ReadOnlyException, Converter.ConversionException {

		super.setValue(newFieldValue);
		this.translations = newFieldValue;
		buildTranslationRows();
		buildRowsLayout();
	}

	@Override
	public void validate() throws Validator.InvalidValueException {

		if (rows.stream().anyMatch(r -> r.getLanguage() == null || StringUtils.isBlank(r.getCaption()))) {
			throw new Validator.InvalidValueException(I18nProperties.getValidationError(Validations.customizableEnumValueEmptyTranslations));
		}
		Set<Language> selectedLanguages = new HashSet<>();
		if (rows.stream().anyMatch(r -> !selectedLanguages.add(r.getLanguage()))) {
			throw new Validator.InvalidValueException(I18nProperties.getValidationError(Validations.customizableEnumValueDuplicateLanguage));
		}
	}

	@Override
	protected List<CustomizableEnumTranslation> getInternalValue() {

		return rows != null
			? rows.stream()
				.map(r -> new CustomizableEnumTranslation(r.getLanguage().getLocale().toString(), r.getCaption()))
				.collect(Collectors.toList())
			: null;
	}

	private void updateNoTranslationsLabelVisibility() {

		if (lblNoTranslations == null) {
			return;
		}

		lblNoTranslations.setVisible(CollectionUtils.isEmpty(rows));
	}

	private static final class TranslationRow extends HorizontalLayout {

		private static final long serialVersionUID = 6883911907756570894L;

		private final ComboBox cbLanguage;
		private final TextField tfCaption;

		private Runnable deleteCallback;

		public TranslationRow(Language language, String caption) {

			tfCaption = new TextField();
			tfCaption.setWidthFull();
			if (caption != null) {
				tfCaption.setValue(caption);
			}
			tfCaption.setPlaceholder(I18nProperties.getString(Strings.promptCustomizableEnumTranslationLanguage));
			cbLanguage = new ComboBox(null, Arrays.asList(Language.values()));
			cbLanguage.setWidth(250, Unit.PIXELS);
			cbLanguage.setInputPrompt(I18nProperties.getString(Strings.promptCustomizableEnumTranslationCaption));
			CssStyles.style(CssStyles.VSPACE_NONE, cbLanguage, tfCaption);
			CssStyles.style(cbLanguage, CssStyles.COMBO_BOX_WITH_FLAG_ICON);
			ControllerProvider.getUserController().setFlagIcons(cbLanguage);
			cbLanguage.addValueChangeListener(e -> tfCaption.setEnabled(e.getProperty().getValue() != null));
			if (language != null) {
				cbLanguage.setValue(language);
			}
			Button btnDelete = ButtonHelper.createIconButtonWithCaption(null, null, VaadinIcons.TRASH, e -> deleteCallback.run());
			addComponent(cbLanguage);
			addComponent(tfCaption);
			addComponent(btnDelete);
			setExpandRatio(tfCaption, 1);

			setWidthFull();
			setMargin(false);
			CssStyles.style(this, CssStyles.VSPACE_4);
		}

		public Language getLanguage() {
			return (Language) cbLanguage.getValue();
		}

		public String getCaption() {
			return tfCaption.getValue();
		}

		public void setDeleteCallback(Runnable deleteCallback) {
			this.deleteCallback = deleteCallback;
		}
	}
}
