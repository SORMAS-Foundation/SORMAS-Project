/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.campaign.campaigndata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.data.translation.TranslationElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementEnumOptions;
import de.symeda.sormas.api.campaign.form.CampaignFormElementStyle;
import de.symeda.sormas.api.campaign.form.CampaignFormElementOptions;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.NumberNumericValueValidator;
import de.symeda.sormas.ui.utils.SormasFieldGroupFieldFactory;

public class CampaignFormBuilder {

	private final List<CampaignFormElement> formElements;
	private final Map<String, Object> formValuesMap;
	private final GridLayout campaignFormLayout;
	private final Locale userLocale;
	private Map<String, String> userTranslations = null;
	Map<String, Field<?>> fields;
	private List<String> optionsValues;

	public CampaignFormBuilder(List<CampaignFormElement> formElements, List<CampaignFormDataEntry> formValues,
			GridLayout campaignFormLayout, List<CampaignFormTranslations> translations) {
		this.formElements = formElements;
		if (formValues != null) {
			this.formValuesMap = new HashMap<>();
			formValues.forEach(formValue -> formValuesMap.put(formValue.getId(), formValue.getValue()));
		} else {
			this.formValuesMap = new HashMap<>();
		}
		this.campaignFormLayout = campaignFormLayout;
		this.fields = new HashMap<>();

		this.userLocale = I18nProperties.getUserLanguage().getLocale();
		if (userLocale != null) {
			translations.stream().filter(t -> t.getLanguageCode().equals(userLocale.toString())).findFirst().ifPresent(
					filteredTranslations -> userTranslations = filteredTranslations.getTranslations().stream().collect(
							Collectors.toMap(TranslationElement::getElementId, TranslationElement::getCaption)));
		}
	}

	public void buildForm() {
		int currentCol = -1;
		GridLayout currentLayout = campaignFormLayout;
		int sectionCount = 0;
		for (CampaignFormElement formElement : formElements) {
			CampaignFormElementType type = CampaignFormElementType.fromString(formElement.getType());
			List<CampaignFormElementStyle> styles;
			// List<CampaignFormElementOptions> options;
			if (formElement.getStyles() != null) {
				styles = Arrays.stream(formElement.getStyles()).map(CampaignFormElementStyle::fromString)
						.collect(Collectors.toList());
			} else {
				styles = new ArrayList<>();
			}

			if (formElement.getOptions() != null) {

				CampaignFormElementOptions campaignFormElementOptions = new CampaignFormElementOptions();
				optionsValues = (List) Arrays.stream(formElement.getOptions()).collect(Collectors.toList());
				ListIterator<String> lstItems = optionsValues.listIterator();
				int i = 1;
				/*
				while (lstItems.hasNext()) {
					switch (i) {
					case 1:
						campaignFormElementOptions.setOpt1(lstItems.next());
						break;
					case 2:
						campaignFormElementOptions.setOpt2(lstItems.next());
						break;
					case 3:
						campaignFormElementOptions.setOpt3(lstItems.next());
						break;
					case 4:
						campaignFormElementOptions.setOpt4(lstItems.next());
						break;
					case 5:
						campaignFormElementOptions.setOpt5(lstItems.next());
						break;
					case 6:
						campaignFormElementOptions.setOpt6(lstItems.next());
						break;
					case 7:
						campaignFormElementOptions.setOpt7(lstItems.next());
						break;
					case 8:
						campaignFormElementOptions.setOpt8(lstItems.next());
						break;
					case 9:
						campaignFormElementOptions.setOpt9(lstItems.next());
						break;
					case 10:
						campaignFormElementOptions.setOpt10(lstItems.next());
						break;

					}
					i++;
				}*/

				campaignFormElementOptions.setOptionsListValues(optionsValues);
			} else {
				optionsValues = new ArrayList<>();
			}

			String dependingOnId = formElement.getDependingOn();
			Object[] dependingOnValues = formElement.getDependingOnValues();

			Object value = formValuesMap.get(formElement.getId());

			int occupiedColumns = getOccupiedColumns(type, styles);

			if (type == CampaignFormElementType.SECTION) {
				sectionCount++;
				GridLayout sectionLayout = new GridLayout(12, 1);
				sectionLayout.setMargin(new MarginInfo(true, true));
				CssStyles.style(sectionLayout, CssStyles.GRID_LAYOUT_SECTION,
						sectionCount % 2 == 0 ? CssStyles.GRID_LAYOUT_EVEN : CssStyles.GRID_LAYOUT_ODD);
				sectionLayout.setWidth(100, Unit.PERCENTAGE);
				currentLayout = sectionLayout;

				campaignFormLayout.addComponent(sectionLayout, 0, campaignFormLayout.getRows() - 1, 11,
						campaignFormLayout.getRows() - 1);
				campaignFormLayout.insertRow(campaignFormLayout.getRows());
			} else if (type == CampaignFormElementType.LABEL) {
				if ((currentCol + 1) + (occupiedColumns - 1) > 11
						|| currentCol > -1 && styles.contains(CampaignFormElementStyle.FIRST)) {
					currentLayout.insertRow(currentLayout.getRows());
					currentCol = -1;
				}

				Label field = new Label(get18nCaption(formElement.getId(), formElement.getCaption()));
				field.setId(formElement.getId());
				prepareComponent(field, formElement.getId(), formElement.getCaption(), type, styles);

				currentLayout.addComponent(field, (currentCol + 1), currentLayout.getRows() - 1,
						(currentCol + 1) + (occupiedColumns - 1), currentLayout.getRows() - 1);

				if (styles.contains(CampaignFormElementStyle.INLINE)) {
					currentCol += occupiedColumns;
				} else {
					currentLayout.insertRow(currentLayout.getRows());
					currentCol = -1;
				}

				if (dependingOnId != null && dependingOnValues != null) {
					setVisibilityDependency(field, dependingOnId, dependingOnValues);
				}
			} else {
				if ((currentCol + 1) + (occupiedColumns - 1) > 11
						|| currentCol > -1 && styles.contains(CampaignFormElementStyle.FIRST)) {
					currentLayout.insertRow(currentLayout.getRows());
					currentCol = -1;
				}

				Field<?> field = createField(formElement.getId(), formElement.getCaption(), type, styles,
						optionsValues);
				
				setFieldValue(field, type, value, optionsValues);
				field.setId(formElement.getId());
				field.setCaption(get18nCaption(formElement.getId(), formElement.getCaption()));
				field.setSizeFull();

				currentLayout.addComponent(field, (currentCol + 1), currentLayout.getRows() - 1,
						(currentCol + 1) + (occupiedColumns - 1), currentLayout.getRows() - 1);

				if (styles.contains(CampaignFormElementStyle.ROW)) {
					currentLayout.insertRow(currentLayout.getRows());
					currentCol = -1;
				} else {
					currentCol += occupiedColumns;
				}

				fields.put(formElement.getId(), field);

				if (dependingOnId != null && dependingOnValues != null) {
					setVisibilityDependency((AbstractComponent) field, dependingOnId, dependingOnValues);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Field<?>> T createField(String fieldId, String caption, CampaignFormElementType type,
			List<CampaignFormElementStyle> styles, List optionz) {
		SormasFieldGroupFieldFactory fieldFactory = new SormasFieldGroupFieldFactory(new FieldVisibilityCheckers(),
				UiFieldAccessCheckers.getNoop());

		T field;
		if (type == CampaignFormElementType.YES_NO) {
			field = fieldFactory.createField(Boolean.class, (Class<T>) NullableOptionGroup.class);
		} else if (type == CampaignFormElementType.TEXT || type == CampaignFormElementType.NUMBER) {
			field = fieldFactory.createField(String.class, (Class<T>) TextField.class);
		} else if (type == CampaignFormElementType.TEXTBOX) {
			field = fieldFactory.createField(String.class, (Class<T>) TextArea.class);
			
		} else if (type == CampaignFormElementType.RADIO) {
			field = fieldFactory.createField(CampaignFormElementEnumOptions.class, (Class<T>) CustomField.class);
			
		} else if (type == CampaignFormElementType.DROPDOWN) {
			field = fieldFactory.createField(CampaignFormElementOptions.class, (Class<T>) ComboBox.class);
			
		} else if (type == CampaignFormElementType.CHECKBOX) {
			//Flash class is only use as a placeholder
			field = fieldFactory.createField(CampaignFormElementEnumOptions.class, (Class<T>) TextArea.class);
			
		} else {
			field = null;
		}

		prepareComponent((AbstractComponent) field, fieldId, caption, type, styles);
		return field;
	}

	private <T extends AbstractComponent> void prepareComponent(T field, String fieldId, String caption,
			CampaignFormElementType type, List<CampaignFormElementStyle> styles) {
		Styles cssStyles = Page.getCurrent().getStyles();

		if (type == CampaignFormElementType.LABEL) {
			((Label) field).setContentMode(ContentMode.HTML);
		} else if (type == CampaignFormElementType.YES_NO 
				//|| type == CampaignFormElementType.CHECKBOX
				//|| type == CampaignFormElementType.RADIO 
				|| type == CampaignFormElementType.DROPDOWN) {
			if (!styles.contains(CampaignFormElementStyle.INLINE)) {
				CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_CAPTION_INLINE,
						CssStyles.FLOAT_RIGHT);
			}
			CssStyles.style(field, CssStyles.OPTIONGROUP_GRID_LAYOUT);
		} else if (type == CampaignFormElementType.TEXT || type == CampaignFormElementType.TEXTBOX
				|| type == CampaignFormElementType.NUMBER) {
			if (styles.contains(CampaignFormElementStyle.ROW)) {
				CssStyles.style(field, CssStyles.TEXTFIELD_ROW, CssStyles.TEXTFIELD_CAPTION_INLINE);
			}

			if (type == CampaignFormElementType.NUMBER) {
				((TextField) field).addValidator(new NumberNumericValueValidator(
						I18nProperties.getValidationError(Validations.onlyNumbersAllowed, caption)));
			}
			// TODO: ADD VALIDATOR TYPE TEXTBOX, LIMITING ALLOWED TEXT/CHAR
		

		}

		cssStyles.add("#" + fieldId + " { width: " + calculateComponentWidth(type, styles) + "% !important; }");
	}

	private int getOccupiedColumns(CampaignFormElementType type, List<CampaignFormElementStyle> styles) {
		List<CampaignFormElementStyle> colStyles = styles.stream().filter(s -> s.toString().startsWith("col"))
				.collect(Collectors.toList());

		if (type == CampaignFormElementType.YES_NO && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.RADIO && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.CHECKBOX && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.DROPDOWN && !styles.contains(CampaignFormElementStyle.INLINE)
						|| type == CampaignFormElementType.TEXTBOX && !styles.contains(CampaignFormElementStyle.INLINE)
				|| (type == CampaignFormElementType.TEXT || type == CampaignFormElementType.NUMBER && styles.contains(CampaignFormElementStyle.ROW))
				){
			return 12;
		}

		if (colStyles.isEmpty()) {
			switch (type) {
			case LABEL:
			case SECTION:
				return 12;
			default:
				return 4;
			}
		}

		// Multiple col styles are not supported; use the first one
		String colStyle = colStyles.get(0).toString();
		return Integer.parseInt(colStyle.substring(colStyle.indexOf("-") + 1));
	}

	private float calculateComponentWidth(CampaignFormElementType type, List<CampaignFormElementStyle> styles) {
		List<CampaignFormElementStyle> colStyles = styles.stream().filter(s -> s.toString().startsWith("col"))
				.collect(Collectors.toList());

		if (type == CampaignFormElementType.YES_NO && styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.RADIO && styles.contains(CampaignFormElementStyle.INLINE)
						|| type == CampaignFormElementType.CHECKBOX && styles.contains(CampaignFormElementStyle.INLINE)
								|| type == CampaignFormElementType.DROPDOWN && styles.contains(CampaignFormElementStyle.INLINE)
				|| (type == CampaignFormElementType.TEXT || type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.TEXTBOX)
						&& !styles.contains(CampaignFormElementStyle.ROW)
				|| type == CampaignFormElementType.LABEL || type == CampaignFormElementType.SECTION) {
			return 100f;
		}

		if (colStyles.isEmpty()) {
			return 33.3f;
		}

		// Multiple col styles are not supported; use the first one
		String colStyle = colStyles.get(0).toString();
		return Integer.parseInt(colStyle.substring(colStyle.indexOf("-") + 1)) / 12f * 100;
	}

	public <T extends Field<?>> void setFieldValue(T field, CampaignFormElementType type, Object value, List options) {

		switch (type) {
		case YES_NO:
			((NullableOptionGroup) field).setValue(Sets.newHashSet(value));
			break;
		case TEXT:
		case NUMBER:
			((TextField) field).setValue(value != null ? value.toString() : null);
			break;
		case TEXTBOX:
				((TextArea) field).setValue(value != null ? value.toString() : null);
				break;
		case RADIO:
			//((RadioButtonGroup) field).setValue(Sets.newHashSet(value));
			break;
		case CHECKBOX:
			((OptionGroup) field).setValue(Sets.newHashSet(value));
			break;
		case DROPDOWN:
			((ComboBox) field).setValue(Sets.newHashSet(value));
			break;
		default:
			throw new IllegalArgumentException(type.toString());
		}
	}

	private void setVisibilityDependency(AbstractComponent component, String dependingOnId,
			Object[] dependingOnValues) {
		Field<?> dependingOnField = fields.get(dependingOnId);
		List<Object> dependingOnValuesList = Arrays.asList(dependingOnValues);

		if (dependingOnField == null) {
			return;
		}

		component.setVisible(dependingOnValuesList.stream()
				.anyMatch(v -> fieldValueMatchesDependingOnValues(dependingOnField, dependingOnValuesList)));
		dependingOnField.addValueChangeListener(e -> {
			boolean visible = fieldValueMatchesDependingOnValues(dependingOnField, dependingOnValuesList);
			component.setVisible(visible);
			if (component instanceof Field) {
				if (!visible) {
					((Field<?>) component).setValue(null);
				}
			}
		});
	}

	private boolean fieldValueMatchesDependingOnValues(Field<?> dependingOnField, List<Object> dependingOnValuesList) {
		if (dependingOnField.getValue() == null) {
			return false;
		}

		if (dependingOnField instanceof NullableOptionGroup) {
			String booleanValue = Boolean.TRUE.equals(((NullableOptionGroup) dependingOnField).getNullableValue())
					? "true"
					: "false";
			String stringValue = Boolean.TRUE.equals(((NullableOptionGroup) dependingOnField).getNullableValue())
					? "yes"
					: "no";

			return dependingOnValuesList.stream().anyMatch(
					v -> v.toString().equalsIgnoreCase(booleanValue) || v.toString().equalsIgnoreCase(stringValue));
		} else {
			return dependingOnValuesList.stream()
					.anyMatch(v -> v.toString().equalsIgnoreCase(dependingOnField.getValue().toString()));
		}
	}

	public String get18nCaption(String elementId, String defaultCaption) {
		if (userTranslations != null && userTranslations.containsKey(elementId)) {
			return userTranslations.get(elementId);
		}

		return defaultCaption;
	}

	public List<CampaignFormDataEntry> getFormValues() {
		return fields.keySet().stream().map(id -> {
			Field<?> field = fields.get(id);
			if (field instanceof NullableOptionGroup) {
				return new CampaignFormDataEntry(id, ((NullableOptionGroup) field).getNullableValue());
			} else {
				return new CampaignFormDataEntry(id, field.getValue());
			}
		}).collect(Collectors.toList());
	}

	public void validateFields() throws Validator.InvalidValueException {
		fields.forEach((key, value) -> {
			value.validate();
		});
	}

	public void resetFormValues() {
		fields.keySet().forEach(key -> {
			Field<?> field = fields.get(key);
			((Field<Object>) field).setValue(formValuesMap.get(key));
		});
	}

	public List<CampaignFormElement> getFormElements() {
		return formElements;
	}

	public Map<String, Field<?>> getFields() {
		return fields;
	}
}
