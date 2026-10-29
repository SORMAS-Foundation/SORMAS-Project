package de.symeda.sormas.ui.externalperson;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.personaldata.PersonalDataForDisplayDto;
import de.symeda.sormas.api.personaldata.PersonalDataProviderFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.person.PersonsView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class ExternalPersonDataComponent extends DetailSubComponentWrapper {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ExternalPersonDataComponent.class);

	public static boolean isAvailable() {
		return UiUtil.permitted(UserRight.EXTERNAL_PERSONAL_DATA_PROVIDER_ACCESS)
			&& Boolean.parseBoolean(
				FacadeProvider.getSystemConfigurationValueFacade().getValue(PersonalDataProviderFacade.EXTERNAL_PERSONAL_DATA_PROVIDER_ENABLED));
	}

	public ExternalPersonDataComponent(PersonReferenceDto personReference) {
		super(() -> null);
		setSizeFull();
		if (!isAvailable()) {
			return;
		}

		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(personReference.getUuid());
		if (StringUtils.isBlank(person.getNationalHealthId())) {
			ExternalPersonsView search = new ExternalPersonsView(person, external -> {
				Component searchComponent = getComponent(0);
				removeAllComponents();
				load(external.getNationalHealthId());

				addComponentAsFirst(new Button(I18nProperties.getCaption(Captions.externalPersonsGoBackToList), event -> {
					removeAllComponents();
					addComponent(searchComponent);
					setExpandRatio(searchComponent, 1);
				}));

				Button link = new Button(I18nProperties.getCaption(Captions.externalPersonLinkNationalHealthId), event -> {
					String state = UI.getCurrent().getNavigator().getState();
					String target = state.replace("/externalPersonData/", state.startsWith(PersonsView.VIEW_NAME + "/") ? "/data/" : "/person/");
					UI.getCurrent().getNavigator().navigateTo(target);
					applyNationalHealthId(
						(Component) UI.getCurrent().getNavigator().getCurrentView(),
						person.getUuid(),
						external.getNationalHealthId());
				});
				link.setVisible(UiUtil.permitted(UserRight.PERSON_EDIT) && FacadeProvider.getPersonFacade().isEditAllowed(person.getUuid()));
				addComponent(link, 2);
			});

			addComponent(search);
			setExpandRatio(search, 1);

		} else {
			load(person.getNationalHealthId());
		}
	}

	public ExternalPersonDataComponent(String nationalHealthId) {
		super(() -> null);
		setSizeFull();
		if (isAvailable()) {
			load(nationalHealthId);
		}
	}

	private void load(String nationalHealthId) {
		if (StringUtils.isBlank(nationalHealthId)) {
			showError(Strings.errorNoNationalHealthIdInContext);
			return;
		}

		PersonalDataForDisplayDto displayData;
		try {
			displayData = FacadeProvider.getPersonalDataProviderFacade().findDisplayDataByNationalHealthId(nationalHealthId);
		} catch (RuntimeException e) {
			logger.error("Could not load external person data", e);
			showError(Strings.errorExternalPersonDataLoad);
			return;
		}

		if (displayData == null || StringUtils.isBlank(displayData.getHtml())) {
			addComponent(new Label(I18nProperties.getString(Strings.infoNoExternalPersonData)));
			return;
		}

		Label html = new Label(displayData.getHtml(), ContentMode.HTML);
		html.setWidth(100, Unit.PERCENTAGE);
		Panel panel = new Panel(html);
		panel.setSizeFull();
		addComponent(panel);
		setExpandRatio(panel, 1);
	}

	private static void applyNationalHealthId(Component component, String personUuid, String nationalHealthId) {
		if (component instanceof PersonEditForm) {
			PersonEditForm form = (PersonEditForm) component;
			Field<?> field = form.getField(PersonDto.NATIONAL_HEALTH_ID);
			if (personUuid.equals(form.getValue().getUuid()) && field.isEnabled() && !field.isReadOnly() && field.isVisible()) {
				((TextField) field).setValue(nationalHealthId);
			}
		} else if (component instanceof HasComponents) {
			for (Component child : (HasComponents) component) {
				applyNationalHealthId(child, personUuid, nationalHealthId);
			}
		}
	}

	private void showError(String messageKey) {
		Label error = new Label(I18nProperties.getString(messageKey));
		error.addStyleName(CssStyles.ERROR_COLOR_PRIMARY);
		addComponent(error);
	}
}
