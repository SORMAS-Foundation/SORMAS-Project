package de.symeda.sormas.ui.externalperson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.personaldata.PersonalDataProviderFacade;
import de.symeda.sormas.api.personaldata.PersonalDataSummaryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

/** Compares provider data with the fields of the current, unsaved creation form. */
public class ExternalPersonDataLookup extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ExternalPersonDataLookup.class);

	private final List<Runnable> selectedChanges = new ArrayList<>();
	private final List<CheckBox> selections = new ArrayList<>();
	private final Button apply = ButtonHelper.createButton(Captions.actionApply);

	public static boolean isAvailable() {
		return UiUtil.permitted(UserRight.EXTERNAL_PERSONAL_DATA_PROVIDER_ACCESS)
			&& StringUtils.isNotBlank(
				FacadeProvider.getSystemConfigurationValueFacade()
					.getValue(PersonalDataProviderFacade.PERSONAL_DATA_PROVIDER_ADAPTER_JNDI_CONFIG_KEY));
	}

	public ExternalPersonDataLookup(
		String nationalHealthId,
		TextField firstName,
		TextField lastName,
		ComboBox birthYear,
		ComboBox birthMonth,
		ComboBox birthDay) {

		setWidth(800, Unit.PIXELS);
		PersonalDataSummaryDto summary = load(nationalHealthId);
		if (summary == null) {
			return;
		}

		GridLayout comparison = new GridLayout(4, 1);
		comparison.setSpacing(true);
		comparison.setWidth(100, Unit.PERCENTAGE);
		comparison.addComponent(new Label(""));
		comparison.addComponent(new Label(I18nProperties.getCaption(Captions.externalPersonDataCurrentValue)));
		comparison.addComponent(new Label(I18nProperties.getCaption(Captions.externalPersonDataProviderValue)));
		comparison.addComponent(new Label(I18nProperties.getCaption(Captions.actionOverwrite)));
		addComponent(comparison);

		addComparison(
			comparison,
			PersonDto.FIRST_NAME,
			firstName.getValue(),
			summary.getFirstName(),
			() -> isEditable(firstName),
			() -> firstName.setValue(summary.getFirstName()));
		addComparison(
			comparison,
			PersonDto.LAST_NAME,
			lastName.getValue(),
			summary.getLastName(),
			() -> isEditable(lastName),
			() -> lastName.setValue(summary.getLastName()));

		LocalDate birthDate = summary.getBirthDate();
		String currentBirthDate =
			DateFormatHelper.formatDate((Integer) birthDay.getValue(), (Integer) birthMonth.getValue(), (Integer) birthYear.getValue());
		String externalBirthDate =
			birthDate == null ? null : DateFormatHelper.formatDate(birthDate.getDayOfMonth(), birthDate.getMonthValue(), birthDate.getYear());
		addComparison(
			comparison,
			PersonDto.BIRTH_DATE,
			currentBirthDate,
			externalBirthDate,
			() -> isEditable(birthYear) && isEditable(birthMonth) && isEditable(birthDay),
			() -> {
				// Year and month listeners rebuild the day list, so set the day last.
				birthYear.setValue(birthDate.getYear());
				birthMonth.setValue(birthDate.getMonthValue());
				birthDay.setValue(birthDate.getDayOfMonth());
			});

		if (summary.getAdditionalFields() != null && !summary.getAdditionalFields().isEmpty()) {
			GridLayout additional = new GridLayout(2, summary.getAdditionalFields().size());
			additional.setSpacing(true);
			additional.setWidth(100, Unit.PERCENTAGE);
			summary.getAdditionalFields().forEach((caption, value) -> {
				additional.addComponent(new Label(StringUtils.defaultString(caption)));
				additional.addComponent(new Label(StringUtils.defaultString(value)));
			});
			addComponent(additional);
		}

		apply.setEnabled(false);
		apply.addClickListener(event -> {
			selectedChanges.forEach(Runnable::run);
			close();
		});
		Button cancel = ButtonHelper.createButton(Captions.actionCancel, event -> close());
		addComponent(new HorizontalLayout(apply, cancel));
	}

	private PersonalDataSummaryDto load(String nationalHealthId) {
		if (StringUtils.isBlank(nationalHealthId)) {
			addComponent(new Label(I18nProperties.getString(Strings.errorNoNationalHealthIdInContext)));
			return null;
		}
		try {
			PersonalDataSummaryDto summary = FacadeProvider.getPersonalDataProviderFacade().findDataByNationalHealthId(nationalHealthId);
			if (summary == null) {
				addComponent(new Label(I18nProperties.getString(Strings.infoNoExternalPersonData)));
			}
			return summary;
		} catch (RuntimeException e) {
			logger.error("Could not load external person summary", e);
			Label error = new Label(I18nProperties.getString(Strings.errorExternalPersonDataLoad));
			error.addStyleName(CssStyles.ERROR_COLOR_PRIMARY);
			addComponent(error);
			return null;
		}
	}

	private void addComparison(GridLayout comparison, String property, String current, String external, BooleanSupplier editable, Runnable change) {

		comparison.addComponent(new Label(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, property)));
		comparison.addComponent(new Label(StringUtils.defaultString(current)));
		comparison.addComponent(new Label(StringUtils.defaultString(external)));
		CheckBox overwrite = new CheckBox();
		overwrite.setEnabled(StringUtils.isNotBlank(external) && editable.getAsBoolean());
		overwrite.setDescription(I18nProperties.getCaption(Captions.actionOverwrite));
		overwrite.addValueChangeListener(event -> apply.setEnabled(selections.stream().anyMatch(CheckBox::getValue)));
		selections.add(overwrite);
		selectedChanges.add(() -> {
			if (overwrite.isEnabled() && overwrite.getValue() && editable.getAsBoolean()) {
				change.run();
			}
		});
		comparison.addComponent(overwrite);
	}

	private static boolean isEditable(Field<?> field) {
		return field.isEnabled() && !field.isReadOnly() && field.isVisible();
	}

	private void close() {
		Window window = findAncestor(Window.class);
		if (window != null) {
			window.close();
		}
	}
}
