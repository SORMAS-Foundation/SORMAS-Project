package de.symeda.sormas.ui.person;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.personaldata.PersonalDataForDisplayDto;
import de.symeda.sormas.api.personaldata.PersonalDataProviderFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
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
		String nationalHealthId = person.getNationalHealthId();
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

	private void showError(String messageKey) {
		Label error = new Label(I18nProperties.getString(messageKey));
		error.addStyleName(CssStyles.ERROR_COLOR_PRIMARY);
		addComponent(error);
	}
}
