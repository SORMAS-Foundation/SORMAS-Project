package de.symeda.sormas.ui.utils;

import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.*;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.clinicalcourse.HealthConditionsForm;
import de.symeda.sormas.ui.epidata.EpiDataBurialsField;
import de.symeda.sormas.ui.epidata.EpiDataGatheringsField;
import de.symeda.sormas.ui.epidata.EpiDataTravelsField;
import de.symeda.sormas.ui.hospitalization.PreviousHospitalizationsField;
import de.symeda.sormas.ui.location.LocationEditForm;

import java.util.Date;
import java.util.List;

public class SormasFieldGroupFieldFactory extends DefaultFieldGroupFieldFactory {
	private static final long serialVersionUID = 471700572643936674L;

	private final UserRight editOrCreateUserRight;

	SormasFieldGroupFieldFactory(UserRight editOrCreateUserRight) {
		this.editOrCreateUserRight = editOrCreateUserRight;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
		if (type.isEnum()) {
			if (fieldType.isAssignableFrom(Field.class) // no specific fieldType defined?
					&& (SymptomState.class.isAssignableFrom(type)
							|| YesNoUnknown.class.isAssignableFrom(type))) {
				OptionGroup field = super.createField(type, OptionGroup.class);
				CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_CAPTION_INLINE);
				return (T) field;
			} else {
				if (Disease.class.isAssignableFrom(type)) {
					fieldType = (Class<T>) ComboBox.class;
					ComboBox field = new ComboBox();
					field.setImmediate(true);
					field.setNullSelectionAllowed(true);
					field.setFilteringMode(FilteringMode.CONTAINS);
					populateWithDiseaseData(field);
					return (T) field;
				} else {
					if (!AbstractSelect.class.isAssignableFrom(fieldType)) {
						fieldType = (Class<T>) ComboBox.class;
					}
					T field = super.createField(type, fieldType);
					if (field instanceof OptionGroup) {
						CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL);
					} else if (field instanceof ComboBox) {
						((ComboBox) field).setFilteringMode(FilteringMode.CONTAINS);
						((ComboBox) field).setNullSelectionAllowed(true);
					}
					return field;
				}
			}
		}
		else if (Boolean.class.isAssignableFrom(type)) {
			OptionGroup field = createBooleanField(OptionGroup.class);
			CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL);
			return (T) field;
		}
		else if (AbstractSelect.class.isAssignableFrom(fieldType)) {
			AbstractSelect field = createCompatibleSelect((Class<? extends AbstractSelect>) fieldType);
			field.setNullSelectionAllowed(true);
			return (T) field;
		} 
		else if (LocationEditForm.class.isAssignableFrom(fieldType)) {
			return (T) new LocationEditForm(editOrCreateUserRight);
		} 
		else if (HealthConditionsForm.class.isAssignableFrom(fieldType)) {
			return (T) new HealthConditionsForm(editOrCreateUserRight);
		}
		else if (DateTimeField.class.isAssignableFrom(fieldType)) {
//			TODO - lgal
			DateTimeField field = new DateTimeField();
			field.setConverter(new SormasDefaultConverterFactory().createDateConverter(Date.class));
			return (T) field;
		} 
		else if (DateField.class.isAssignableFrom(fieldType)) {
			DateField field = super.createField(type, DateField.class);
			field.setDateFormat(DateFormatHelper.getDateFormatPattern());
			field.setLenient(true);
			field.setConverter(new SormasDefaultConverterFactory().createDateConverter(Date.class));
			return (T) field;
		}
		else if (PreviousHospitalizationsField.class.isAssignableFrom(fieldType)) {
			return (T) new PreviousHospitalizationsField();
		} 
		else if (EpiDataBurialsField.class.isAssignableFrom(fieldType)) {
			return (T) new EpiDataBurialsField();
		}
		else if (EpiDataGatheringsField.class.isAssignableFrom(fieldType)) {
			return (T) new EpiDataGatheringsField();
		}
		else if (EpiDataTravelsField.class.isAssignableFrom(fieldType)) {
			return (T) new EpiDataTravelsField();
		}
		else if (fieldType.equals(Field.class)) {
			// no specific field type defined -> fallbacks
			if (Date.class.isAssignableFrom(type)) {
				DateField field = super.createField(type, DateField.class);
				field.setDateFormat(DateFormatHelper.getDateFormatPattern());
				field.setLenient(true);
				field.setConverter(new SormasDefaultConverterFactory().createDateConverter(Date.class));
				return (T) field;
			}
			else if (ReferenceDto.class.isAssignableFrom(type)) {
				return (T) new ComboBox();
			}
		}

		return super.createField(type, fieldType);
	}

	@Override
	protected <T extends AbstractTextField> T createAbstractTextField(Class<T> fieldType) {
		T textField = super.createAbstractTextField(fieldType);
		textField.setNullRepresentation("");
		return textField;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected <T extends Field> T createBooleanField(Class<T> fieldType) {
		if (OptionGroup.class.isAssignableFrom(fieldType)) {
			AbstractSelect s = createCompatibleSelect(OptionGroup.class);
			s.addItem(Boolean.TRUE);
			s.setItemCaption(Boolean.TRUE, I18nProperties.getEnumCaption(YesNoUnknown.YES));
			s.addItem(Boolean.FALSE);
			s.setItemCaption(Boolean.FALSE, I18nProperties.getEnumCaption(YesNoUnknown.NO));
			return (T) s;
		} else {
			return super.createBooleanField(fieldType);
		}
	}

	protected void populateWithDiseaseData(ComboBox diseaseField) {

		diseaseField.removeAllItems();
        for (Object p : diseaseField.getContainerPropertyIds()) {
        	diseaseField.removeContainerProperty(p);
        }
        diseaseField.addContainerProperty(CAPTION_PROPERTY_ID, String.class, "");
        diseaseField.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);
        @SuppressWarnings("unchecked")
		List<Disease> diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
        for (Object r : diseases) {
            Item newItem = diseaseField.addItem(r);
            newItem.getItemProperty(CAPTION_PROPERTY_ID).setValue(r.toString());
        }
	}
}