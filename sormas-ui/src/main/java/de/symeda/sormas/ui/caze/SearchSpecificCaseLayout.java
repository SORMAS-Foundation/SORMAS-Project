package de.symeda.sormas.ui.caze;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class SearchSpecificCaseLayout extends VerticalLayout {

	public SearchSpecificCaseLayout(Runnable closePopupCallback) {

		setMargin(true);
		setSpacing(false);
		setWidth(100, Unit.PERCENTAGE);

		Label lblCaseSearchDescription = new Label(I18nProperties.getString(Strings.infoSpecificCaseSearch), ContentMode.HTML);
		lblCaseSearchDescription.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(lblCaseSearchDescription, CssStyles.VSPACE_2);
		addComponent(lblCaseSearchDescription);

		TextField searchField = new TextField();
		ConfirmationComponent confirmationComponent = new ConfirmationComponent(false) {

			@Override
			protected void onConfirm() {
				String foundCaseUuid = FacadeProvider.getCaseFacade().getUuidByUuidEpidNumberOrExternalId(searchField.getValue());

				if (foundCaseUuid != null) {
					ControllerProvider.getCaseController().navigateToCase(foundCaseUuid);
					closePopupCallback.run();
				} else {
					VaadinUiUtil.showSimplePopupWindow(
						I18nProperties.getString(Strings.headingNoCaseFound),
						I18nProperties.getString(Strings.messageNoCaseFound));
				}
			}

			@Override
			protected void onCancel() {
				closePopupCallback.run();
			}
		};

		searchField.setPlaceholder(I18nProperties.getString(Strings.promptSearch));
		searchField.setWidth(300, Unit.PIXELS);
		searchField.addValueChangeListener(e -> confirmationComponent.getConfirmButton().setEnabled(StringUtils.isNotEmpty(e.getValue())));
		CssStyles.style(searchField, CssStyles.VSPACE_2);
		addComponent(searchField);

		confirmationComponent.getConfirmButton().setCaption(I18nProperties.getCaption(Captions.caseSearchCase));
		confirmationComponent.getConfirmButton().setEnabled(false);
		confirmationComponent.getCancelButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
		confirmationComponent.setMargin(true);
		addComponent(confirmationComponent);
		setComponentAlignment(confirmationComponent, Alignment.MIDDLE_RIGHT);
	}
}
