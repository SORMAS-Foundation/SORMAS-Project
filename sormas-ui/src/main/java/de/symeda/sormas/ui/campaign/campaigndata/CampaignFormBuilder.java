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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import com.vaadin.ui.AbstractComponent;
import com.vaadin.v7.ui.DateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property.ReadOnlyException;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.validator.RegexpValidator;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.MapperUtil;
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
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.campaign.jsonHelpers.BasicCheckboxHelper;
import de.symeda.sormas.ui.campaign.jsonHelpers.BasicRadioGroupHelper;
import de.symeda.sormas.ui.campaign.jsonHelpers.CheckboxBasicGroup;
import de.symeda.sormas.ui.campaign.jsonHelpers.RadioBasicGroup;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.NumberNumericValueValidator;
import de.symeda.sormas.ui.utils.SormasFieldGroupFieldFactory;


public class CampaignFormBuilder {

	private final List<CampaignFormElement> formElements;
	private final Map<String, Object> formValuesMap;
	private final VerticalLayout campaignFormLayout;
	private final Locale userLocale;
	private Map<String, String> userTranslations = null;
	private Map<String, String> userOptTranslations = null;
	Map<String, Field<?>> fields;
	private Map<String, String> optionsValues = null;
	private List<String> constraints;
	private List<CampaignFormTranslations> translationsOpt;

	public CampaignFormBuilder(List<CampaignFormElement> formElements, List<CampaignFormDataEntry> formValues,
			VerticalLayout campaignFormLayout, List<CampaignFormTranslations> translations) {
		this.formElements = formElements;
		if (formValues != null) {
			this.formValuesMap = new HashMap<>();
			formValues.forEach(formValue -> formValuesMap.put(formValue.getId(), formValue.getValue()));
		} else {
			this.formValuesMap = new HashMap<>();
		}
		this.campaignFormLayout = campaignFormLayout;
		this.fields = new HashMap<>();
		this.translationsOpt = translations;

		this.userLocale = I18nProperties.getUserLanguage().getLocale();
		if (userLocale != null) {
			translations.stream().filter(t -> t.getLanguageCode().equals(userLocale.toString())).findFirst().ifPresent(
					filteredTranslations -> userTranslations = filteredTranslations.getTranslations().stream().collect(
							Collectors.toMap(TranslationElement::getElementId, TranslationElement::getCaption)));
		}
	}

	public void buildForm() {
		int currentCol = -1;
		//GridLayout currentLayout = campaignFormLayout;
		int sectionCount = 0;
		
		int ii=0;
		//System.out.println("Got one____");
		VerticalLayout vertical = new VerticalLayout ();
		vertical.setSizeFull();
		vertical.setWidthFull();
		vertical.setHeightFull();
		vertical.setSpacing(false);
		
		TabSheet accrd = new TabSheet();
		accrd.setHeight(750, Unit.PIXELS);
        
		int accrd_count = 0;
		
		for (CampaignFormElement formElement : formElements) {
			System.out.println("Gotint it..."+ ii++);
			CampaignFormElementType type = CampaignFormElementType.fromString(formElement.getType());
			
			
			List<CampaignFormElementStyle> styles;
			if (formElement.getStyles() != null) {
				styles = Arrays.stream(formElement.getStyles()).map(CampaignFormElementStyle::fromString)
						.collect(Collectors.toList());
			} else {
				styles = new ArrayList<>();
			}

			if (formElement.getOptions() != null) {
					
				if (userLocale != null) {
					translationsOpt.stream().filter(t -> t.getLanguageCode().equals(userLocale.toString()))
					.findFirst().ifPresent(filteredTranslations -> filteredTranslations.getTranslations().stream()
					.filter(cd -> cd.getOptions() != null)
					.findFirst().ifPresent(optionsList -> userOptTranslations = optionsList.getOptions().stream()
					.filter(c -> c.getCaption() != null).collect(Collectors.toMap(MapperUtil::getKey, MapperUtil::getCaption))));
				}
				

				CampaignFormElementOptions campaignFormElementOptions = new CampaignFormElementOptions();
				optionsValues = formElement.getOptions().stream().collect(Collectors.toMap(MapperUtil::getKey, MapperUtil::getCaption));  // .collect(Collectors.toList());
				
				System.out.println("_______________________ "+userOptTranslations);
				if(userOptTranslations == null) {
					campaignFormElementOptions.setOptionsListValues(optionsValues);
					//get18nOptCaption(formElement.getId(), optionsValues));
				}else {
					campaignFormElementOptions.setOptionsListValues(userOptTranslations);
					
				}
				
			} else {
				optionsValues = new HashMap<String, String>();
			}

			
			
			
			
			if (formElement.getConstraints() != null) {
				System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiii");
				CampaignFormElementOptions campaignFormElementOptions = new CampaignFormElementOptions();
				constraints = (List) Arrays.stream(formElement.getConstraints()).collect(Collectors.toList());
				ListIterator<String> lstItemsx = constraints.listIterator();
				int i = 1;
				System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiii");
				while (lstItemsx.hasNext()) {
					String lss = lstItemsx.next().toString();
					if (lss.toLowerCase().contains("max")) {
						campaignFormElementOptions.setMax(Integer.parseInt(lss.substring(lss.lastIndexOf("=") + 1)));
					} else if (lss.toLowerCase().contains("min")) {
						campaignFormElementOptions.setMin(Integer.parseInt(lss.substring(lss.lastIndexOf("=") + 1)));
					}
					else if (lss.toLowerCase().contains("expression")) {
						System.out.println("iiiiiiiiiii6666666666666666666666iiiiiiiiiiiiiii");
						campaignFormElementOptions.setExpression(true);
					}
				}

			}
			
		

			String dependingOnId = formElement.getDependingOn();
			Object[] dependingOnValues = formElement.getDependingOnValues();

			Object value = formValuesMap.get(formElement.getId());

			int occupiedColumns = getOccupiedColumns(type, styles);
			
			
			
	
			if (type == CampaignFormElementType.DAYWISE) {
				accrd_count++;
				if(accrd_count > 1){
					
					 final VerticalLayout layout = new VerticalLayout(vertical);
					 layout.setMargin(true);
					// layout.addComponent(label);
					 int temp = accrd_count;
					 temp = temp-1;
					 layout.setStyleName("daywise_background_"+temp); //.addStyleName(dependingOnId);
					 accrd.addTab(layout, formElement.getCaption());
					 
					 
					 vertical = new VerticalLayout ();
						vertical.setSizeFull();
						vertical.setWidthFull();
						vertical.setHeightFull();
						vertical.setSpacing(false);
					 
				}
			}else if (type == CampaignFormElementType.SECTION) {
				sectionCount++;
				//vertical = new HorizontalLayout ();
				//vertical.addComponent(new TextField("sectionCount"));
			//	GridLayout sectionLayout = new GridLayout(12, 1);
			//	sectionLayout.setMargin(new MarginInfo(true, true));
				CssStyles.style(vertical, CssStyles.GRID_LAYOUT_SECTION,
						sectionCount % 2 == 0 ? CssStyles.GRID_LAYOUT_EVEN : CssStyles.GRID_LAYOUT_ODD);
				vertical.setId("dkjfaihsodjkfaldfhlasdf-"+sectionCount);
				//vertical.setWidth(100, Unit.PERCENTAGE);
			//	currentLayout = sectionLayout;

			//	campaignFormLayout.addComponent(sectionLayout, 0, campaignFormLayout.getRows() - 1, 11,
			//			campaignFormLayout.getRows() - 1);
			//	campaignFormLayout.insertRow(campaignFormLayout.getRows());
				//Html html = new Html("<fieldset id='ddddddddddddddddddddddddddddddnn'>");
				
			//	vertical.addComponent((Component) html);
				
			} else if (type == CampaignFormElementType.LABEL) {
			/*	if ((currentCol + 1) + (occupiedColumns - 1) > 11
						|| currentCol > -1 && styles.contains(CampaignFormElementStyle.FIRST)) {
					currentLayout.insertRow(currentLayout.getRows());
					currentCol = -1;
				}*/

				Label field = new Label(get18nCaption(formElement.getId(), formElement.getCaption()));
				field.setId(formElement.getId());
				prepareComponent(field, formElement.getId(), formElement.getCaption(), type, styles, true, null);

				vertical.addComponent(field);//, (currentCol + 1), currentLayout.getRows() - 1,
						//(currentCol + 1) + (occupiedColumns - 1), currentLayout.getRows() - 1);

				//if (styles.contains(CampaignFormElementStyle.INLINE)) {
				//	currentCol += occupiedColumns;
			//	} else {
			//		currentLayout.insertRow(currentLayout.getRows());
				//	currentCol = -1;
				//}

				if (dependingOnId != null && dependingOnValues != null) {
					setVisibilityDependency(field, dependingOnId, dependingOnValues);
				}
			} else {
				//if ((currentCol + 1) + (occupiedColumns - 1) > 11
				//		|| currentCol > -1 && styles.contains(CampaignFormElementStyle.FIRST)) {
				//	currentLayout.insertRow(currentLayout.getRows());
				//	currentCol = -1;
				//}

				Field<?> field = createField(formElement.getId(), formElement.getCaption(), type, styles,
						optionsValues, formElement.isWarnonerror(), formElement.getErrormessage());

				setFieldValue(field, type, value, optionsValues);
				field.setId(formElement.getId());
				field.setCaption(get18nCaption(formElement.getId(), formElement.getCaption()));
				field.setSizeFull();
				field.setRequired(formElement.isImportant());

				vertical.addComponent(field);//, (currentCol + 1), currentLayout.getRows() - 1,
					//	(currentCol + 1) + (occupiedColumns - 1), currentLayout.getRows() - 1);

				//if (styles.contains(CampaignFormElementStyle.ROW)) {
				//	currentLayout.insertRow(currentLayout.getRows());
				//	currentCol = -1;
				//} else {
				//	currentCol += occupiedColumns;
				//}

				fields.put(formElement.getId(), field);

				if (dependingOnId != null && dependingOnValues != null) {
					setVisibilityDependency((AbstractComponent) field, dependingOnId, dependingOnValues);
				}
			}
		//	verticalx.addComponent(vertical);
			
			if(accrd_count == 0) {
				
				campaignFormLayout.addComponent(vertical);
			}else {
				
				campaignFormLayout.addComponent(accrd);
			}
			
			
			//
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Field<?>> T createField(String fieldId, String caption, CampaignFormElementType type,
			List<CampaignFormElementStyle> styles, Map optionz, boolean isOnError, String errormsg) {
		SormasFieldGroupFieldFactory fieldFactory = new SormasFieldGroupFieldFactory(new FieldVisibilityCheckers(),
				UiFieldAccessCheckers.getNoop());

		T field;

		Converter converter = null;
		if (type == CampaignFormElementType.YES_NO) {
			field = fieldFactory.createField(Boolean.class, (Class<T>) NullableOptionGroup.class);
		} else if (type == CampaignFormElementType.TEXT || type == CampaignFormElementType.NUMBER
				|| type == CampaignFormElementType.RANGE || type == CampaignFormElementType.DECIMAL) {
			field = fieldFactory.createField(String.class, (Class<T>) TextField.class);
		} else if (type == CampaignFormElementType.TEXTBOX) {
			field = fieldFactory.createField(String.class, (Class<T>) TextArea.class);

		} else if (type == CampaignFormElementType.RADIO) {
			field = fieldFactory.createField(CampaignFormElementEnumOptions.class, (Class<T>) RadioBasicGroup.class);

		} else if (type == CampaignFormElementType.RADIOBASIC) {
			field = fieldFactory.createField(CampaignFormElementEnumOptions.class,
					(Class<T>) BasicRadioGroupHelper.class);

		} else if (type == CampaignFormElementType.DROPDOWN) {
			field = fieldFactory.createField(CampaignFormElementOptions.class, (Class<T>) ComboBox.class);

		} else if (type == CampaignFormElementType.CHECKBOX) {
			// Flash class is only use as a placeholder
			field = fieldFactory.createField(CampaignFormElementEnumOptions.class, (Class<T>) CheckboxBasicGroup.class);

		} else if (type == CampaignFormElementType.CHECKBOXBASIC) {
			// Flash class is only use as a placeholder
			field = fieldFactory.createField(CampaignFormElementEnumOptions.class,
					(Class<T>) BasicCheckboxHelper.class);

		} else if (type == CampaignFormElementType.DATE) {
			// DateField class is only use as a placeholder
			field = fieldFactory.createField(CampaignFormElementEnumOptions.class, (Class<T>) DateField.class);

		} else if (type == CampaignFormElementType.ARRAY) {
			// DateField class is only use as a placeholder
			field = fieldFactory.createField(CampaignFormElementEnumOptions.class, (Class<T>) DateField.class);

		} else if (type == CampaignFormElementType.RANGE) {
			// DateField class is only use as a placeholder
			field = fieldFactory.createField(CampaignFormElementEnumOptions.class, (Class<T>) DateField.class);

		} else {
			field = null;
		}

		prepareComponent((AbstractComponent) field, fieldId, caption, type, styles, isOnError, errormsg);
		return field;
	}

	@SuppressWarnings("deprecation")
	private <T extends AbstractComponent> void prepareComponent(T field, String fieldId, String caption,
			CampaignFormElementType type, List<CampaignFormElementStyle> styles, boolean isOnError, String errormsg) {
		
		System.out.println(fieldId+" ddddddddddddddddddddddddddddddddd "+errormsg); 
		CampaignFormElementOptions constrainsVal = new CampaignFormElementOptions();

		Styles cssStyles = Page.getCurrent().getStyles();

		if (type == CampaignFormElementType.LABEL) {
			((Label) field).setContentMode(ContentMode.HTML);
		} else if (type == CampaignFormElementType.YES_NO || type == CampaignFormElementType.CHECKBOX
				|| type == CampaignFormElementType.RADIO || type == CampaignFormElementType.DATE
				|| type == CampaignFormElementType.DROPDOWN) {
			if (!styles.contains(CampaignFormElementStyle.INLINE)) {
			//	CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_CAPTION_INLINE,
			//			CssStyles.FLOAT_RIGHT);
			}
			CssStyles.style(field, CssStyles.OPTIONGROUP_GRID_LAYOUT);
		} else if (type == CampaignFormElementType.TEXT || type == CampaignFormElementType.TEXTBOX
				|| type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL
				|| type == CampaignFormElementType.ARRAY || type == CampaignFormElementType.RANGE
				|| type == CampaignFormElementType.DATE) {
			if (styles.contains(CampaignFormElementStyle.ROW)) {
			//	CssStyles.style(field, CssStyles.TEXTFIELD_ROW, CssStyles.TEXTFIELD_CAPTION_INLINE);
			}

			if (type == CampaignFormElementType.NUMBER) {

				((TextField) field).addValueChangeListener(e -> {
					if (e.getProperty().getValue() != null && e.getProperty().getValue().toString().contains(".0")) {
						e.getProperty().setValue(e.getProperty().getValue().toString().replace(".0", ""));
					}
				});

				((TextField) field).addValidator(new NumberNumericValueValidator(
						I18nProperties.getValidationError(errormsg == null ? Validations.onlyNumbersAllowed : errormsg, caption)));
			}
			if (type == CampaignFormElementType.DECIMAL) {

				/*((TextField) field).addValueChangeListener(e -> {
					if (e.getProperty().getValue() != null && !e.getProperty().getValue().toString().contains(".")) {
						e.getProperty().setValue(e.getProperty().getValue().toString() + ".0");
					}
				});*/

				((TextField) field).addValidator(new NumberNumericValueValidator(
						I18nProperties.getValidationError(errormsg == null ? Validations.onlyDecimalNumbersAllowed : errormsg, caption), null, null,
						true));
			}

			

			if (type == CampaignFormElementType.RANGE) {
				String validationMessageTag = "";
				Map<String, Object> validationMessageArgs = new HashMap<>();
			
				
				if (constrainsVal.isExpression()) {
/*
					System.out.println(type + "____________________1");

					final String validationMessageTagx = Validations.numberNotInRange;

					((TextField) field).addValueChangeListener(e -> {
						
						System.out.println(ww+ww + "_________________"+e.getProperty().getValue()+"______2");
						if (e.getProperty().getValue() != null) {
							System.out.println(type + "_______________"+ ww+1 +"___________3");
							if (e.getProperty().getValue().toString().equals("0")) {
								System.out.println(type + "____________"+ f+1 +"______________4");
								validationMessageArgs.put("min", 1);
								validationMessageArgs.put("max", 90);

								((TextField) field)
										.addValidator(
												new NumberNumericValueValidator(
														caption.toUpperCase() + ": "
																+ I18nProperties.getValidationError(
																		validationMessageTagx, validationMessageArgs),
														1, 90, true, isOnError));

							}
						}

					});
					*/
					constrainsVal.setExpression(false);
					
					
					((TextField) field).addValidator(
							new RegexpValidator("^[1-9]\\d*$", errormsg.equals(null) ? "Number entered not in allowed range" : errormsg )); 
					
					
				} else {
					
							
						if (constrainsVal.getMin() != null || constrainsVal.getMax() != null) {
							
							
							if (constrainsVal.getMin() == null) {
								validationMessageTag = Validations.numberTooBig;
								validationMessageArgs.put("value", constrainsVal.getMax());
							} else if (constrainsVal.getMax() == null) {
								validationMessageTag = Validations.numberTooSmall;
								validationMessageArgs.put("value", constrainsVal.getMin());
							} else {
								validationMessageTag = Validations.numberNotInRange;
								validationMessageArgs.put("min", constrainsVal.getMin());
								validationMessageArgs.put("max", constrainsVal.getMax());
							}
		
							//field.addValidator(
								//new NumberValidator(I18nProperties.getValidationError(validationMessageTag, validationMessageArgs), minValue, maxValue));
						
				
					((TextField) field).addValidator(new NumberNumericValueValidator(
							caption.toUpperCase()+": " + I18nProperties.getValidationError(validationMessageTag, validationMessageArgs),
							constrainsVal.getMin(), constrainsVal.getMax(), true, isOnError));
					
					
					}
				}
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
				|| type == CampaignFormElementType.CHECKBOXBASIC && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.RADIOBASIC && !styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.TEXTBOX && !styles.contains(CampaignFormElementStyle.INLINE)
				|| (type == CampaignFormElementType.TEXT || type == CampaignFormElementType.DATE
				|| type == CampaignFormElementType.NUMBER || type == CampaignFormElementType.DECIMAL
				|| type == CampaignFormElementType.RANGE)){// && styles.contains(CampaignFormElementStyle.ROW)) {
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
				|| type == CampaignFormElementType.RADIOBASIC && styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.CHECKBOX && styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.CHECKBOXBASIC && styles.contains(CampaignFormElementStyle.INLINE)
				|| type == CampaignFormElementType.DROPDOWN && styles.contains(CampaignFormElementStyle.INLINE)
				|| (type == CampaignFormElementType.TEXT || type == CampaignFormElementType.NUMBER
						|| type == CampaignFormElementType.DECIMAL || type == CampaignFormElementType.RANGE
						|| type == CampaignFormElementType.DATE || type == CampaignFormElementType.TEXTBOX)
						//&& !styles.contains(CampaignFormElementStyle.ROW)
				|| type == CampaignFormElementType.LABEL || type == CampaignFormElementType.SECTION) {
			return 100f;
		}
		if(1 == 1) {
		return 100f;
		}
		
		if (colStyles.isEmpty()) {
		//	return 33.3f;
		}

		// Multiple col styles are not supported; use the first one
		String colStyle = colStyles.get(0).toString();
		return Integer.parseInt(colStyle.substring(colStyle.indexOf("-") + 1)) / 12f * 100;
	}

	public <T extends Field<?>> void setFieldValue(T field, CampaignFormElementType type, Object value, Map<String,String> options) {

		switch (type) {
		case YES_NO:
			((NullableOptionGroup) field).setValue(Sets.newHashSet(value));
			break;
		case TEXT:
		case NUMBER:
		case RANGE:

			((TextField) field).setValue(value != null ? value.toString() : null);
			break;
		case DECIMAL:
			if (value != null) {
				
				((TextField) field).setValue(value != null ? value.toString()  : null);
			}
			break;
		case TEXTBOX:
			if (value != null) {

				if (value.equals(true)) {
					field.setEnabled(true);
				} else if (value.equals(false)) {
					field.setEnabled(false);
					// Notification.show("Warning:", Title "Expression resulted in wrong value
					// please check your data 1", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			;
			((TextArea) field).setValue(value != null ? value.toString() : null);
			break;
		case DATE:
			if (value != null) {
				try {

					String vc = value + "";
					System.out.println(value);
					Date dst = vc.contains("00:00:00") ? dateFormatter(value) : dateFormatterLongAndMobile(value);
				

					((DateField) field).setValue(value != null ? dst : null);

				} catch (ReadOnlyException | ConversionException e) {
					// TODO Auto-generated catch block
					((DateField) field).setValue(null);
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			;
			break;
		case RADIO:
			((OptionGroup) field).select(Sets.newHashSet(value).toString().replace("[", "").replace("]", ""));
			break;
		case RADIOBASIC:
			((OptionGroup) field).select(Sets.newHashSet(value).toString().replace("[", "").replace("]", ""));
			break;
		case CHECKBOX:
			if (value != null) {
				String dcs = value.toString().replace("[", "").replace("]", "").replaceAll(", ", ",");
				String strArray[] = dcs.split(",");
				for (int i = 0; i < strArray.length; i++) {
					((OptionGroup) field).select(strArray[i]);
				}
			}
			;
			break;
		case CHECKBOXBASIC:

			if (value != null) {
				String dcxs = value.toString().replace("[", "").replace("]", "").replaceAll(", ", ",");
				String strArraxy[] = dcxs.split(",");
				for (int i = 0; i < strArraxy.length; i++) {
					((OptionGroup) field).select(strArraxy[i]);
				}
			}
			;
			break;
		case DROPDOWN:

			if (value != null) {

				if (value.equals(true)) {
					field.setEnabled(true);
				} else if (value.equals(false)) {
					field.setEnabled(false);
				}
			}
			;
			if (value != null) {
				String dxz = options.get(value);
					((ComboBox) field).select(value);
			}
			;

			break;
		default:
			throw new IllegalArgumentException(type.toString());
		}
	}
	
	
	
	private Date dateFormatterLongAndMobile(Object value){

		String dateStr = value+"";
		DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
		DateFormat formatterx = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		System.out.println("date in question "+value);
		
		try {
			date = (Date) formatter.parse(dateStr);
		} catch (ParseException e) {
			
			try {
				date = (Date) formatterx.parse(dateStr);
			} catch (ParseException ed) {
				 date = new Date((Long) value);
			 }
		}
		
	return date;
	}
	
	

	private Date dateFormatter(Object value) throws ParseException {
		// TODO Auto-generated method stub

		String dateStr = value+"";
		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		Date date;

		date = (Date) formatter.parse(dateStr);

		System.out.println(date);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.YEAR);
		System.out.println("formatedDate : " + formatedDate);

		Date res = new Date(formatedDate + "");

		return res;
	}

	private void setVisibilityDependency(AbstractComponent component, String dependingOnId,
			Object[] dependingOnValues) {
		Field<?> dependingOnField = fields.get(dependingOnId);
		List<Object> dependingOnValuesList = Arrays.asList(dependingOnValues);

		if (dependingOnField == null) {
			return;
		}
//fieldValueMatchesDependingOnValuesNOTValuer
		if(dependingOnValuesList.stream()
				.anyMatch(v -> v.toString().contains("!"))) {
			
			//hide on default
			component.setVisible(dependingOnValuesList.stream()
					.anyMatch(v -> fieldValueMatchesDependingOnValuesNOTValuer(dependingOnField, dependingOnValuesList)));
			
			//check value and determine if to hide or show
			dependingOnField.addValueChangeListener(e -> {
				boolean visible = fieldValueMatchesDependingOnValuesNOTValuer(dependingOnField, dependingOnValuesList);
				
				component.setVisible(visible);
				if (component instanceof Field) {
					if (!visible) {
						((Field<?>) component).setValue(null);
					}
				}
			});
		} else {
		
		//hide on default
		component.setVisible(dependingOnValuesList.stream()
				.anyMatch(v -> fieldValueMatchesDependingOnValues(dependingOnField, dependingOnValuesList)));
		
		//check value and determine if to hide or show
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
	

	
	private boolean fieldValueMatchesDependingOnValuesNOTValuer(Field<?> dependingOnField, List<Object> dependingOnValuesList) {
		if (dependingOnField.getValue() == null) {
			return false;
		}

		if (dependingOnField instanceof NullableOptionGroup) {
			String booleanValue = Boolean.TRUE.equals(((NullableOptionGroup) dependingOnField).getNullableValue())
					? "false"
					: "true";
			String stringValue = Boolean.TRUE.equals(((NullableOptionGroup) dependingOnField).getNullableValue())
					? "no"
					: "yes";

			return dependingOnValuesList.stream().anyMatch(
					v -> v.toString().replaceAll("!", "").equalsIgnoreCase(booleanValue) || v.toString().replaceAll("!", "").equalsIgnoreCase(stringValue));
		} else {
			
			return dependingOnValuesList.stream()
					.anyMatch(v -> !v.toString().replaceAll("!", "").equalsIgnoreCase(dependingOnField.getValue().toString()));
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
			} /*
				 * else if (field instanceof DateField) {
				 * 
				 * System.out.println("----xx: "+field.getValue());
				 * 
				 * // Sys The number you entered is not valid.
				 * println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ field is date"
				 * + dateFormat.format(((DateField)
				 * field).getDateFormat().format(field.getValue()+"", null))); return new
				 * CampaignFormDataEntry(id, ((DateField)
				 * field).getDateFormat().format(field.getValue()+"", null)); }
				 */else {
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
