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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslation;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.AbstractCampaignView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import org.vaadin.hene.popupbutton.PopupButton;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class CampaignDataView extends AbstractCampaignView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigndata";

	private final CampaignFormDataCriteria criteria;
	private final CampaignDataGrid grid;
	private CampaignFormDataFilterForm filterForm;

	public CampaignDataView() {
		super(VIEW_NAME);

		criteria = ViewModelProviders.of(getClass()).get(CampaignFormDataCriteria.class);
		grid = new CampaignDataGrid(criteria);

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addComponent(createFilterBar());
		mainLayout.addComponent(grid);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(false);
		mainLayout.setSizeFull();
		mainLayout.setExpandRatio(grid, 1);
		mainLayout.setStyleName("crud-main-layout");

		VerticalLayout newFormLayout = new VerticalLayout();
		{
			newFormLayout.setSpacing(true);
			newFormLayout.setMargin(true);
			newFormLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			newFormLayout.setWidth(350, Unit.PIXELS);

			PopupButton newFormButton = ButtonHelper.createIconPopupButton(Captions.actionNewForm, VaadinIcons.PLUS_CIRCLE, newFormLayout);
			newFormButton.setId("new-form");

			for (CampaignFormMetaReferenceDto campaignForm : FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferences()) {
				Button campaignFormButton = ButtonHelper
					.createButton(campaignForm.toString(), e -> ControllerProvider.getCampaignController().createCampaignDataForm(campaignForm));
				campaignFormButton.setWidth(100, Unit.PERCENTAGE);
				newFormLayout.addComponent(campaignFormButton);
			}

			addHeaderComponent(newFormButton);
		}

		addComponent(mainLayout);
	}

	public CampaignFormDataFilterForm createFilterBar() {
		filterForm = new CampaignFormDataFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!navigateTo(criteria, false)) {
				filterForm.updateResetButtonState();
				grid.reload();
			}
		});

		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(CampaignDataView.class).remove(CampaignFormDataCriteria.class);
			navigateTo(null, true);
		});

		filterForm.setFormMetaChangedCallback(createFormMetaChangedCallback());

		return filterForm;
	}

	private Consumer<CampaignFormMetaReferenceDto> createFormMetaChangedCallback() {
		return formMetaReference -> {
			if (formMetaReference != null) {
				CampaignFormMetaDto formMeta = FacadeProvider.getCampaignFormMetaFacade().getCampaignFormMetaByUuid(formMetaReference.getUuid());
				Language userLanguage = UserProvider.getCurrent().getUser().getLanguage();
				CampaignFormTranslations translations = null;
				if (userLanguage != null) {
					translations = formMeta.getCampaignFormTranslations()
						.stream()
						.filter(t -> t.getLanguageCode().equals(userLanguage.getLocale().toString()))
						.findFirst()
						.orElse(null);
				}

				List<CampaignFormElement> formListElements =
					formMeta.getCampaignFormElements().stream().filter(CampaignFormElement::isImportant).collect(Collectors.toList());
				for (CampaignFormElement element : formListElements) {
					String caption = null;
					if (translations != null) {
						caption = translations.getTranslations()
							.stream()
							.filter(t -> t.getElementId().equals(element.getId()))
							.map(CampaignFormTranslation::getCaption)
							.findFirst()
							.orElse(null);
					}
					if (caption == null) {
						caption = element.getCaption();
					}

					grid.addCustomColumn(element.getId(), caption);
				}
			}
		};
	}

	@Override
	public void enter(ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		filterForm.setValue(criteria);
		grid.reload();

		super.enter(event);
	}

}
