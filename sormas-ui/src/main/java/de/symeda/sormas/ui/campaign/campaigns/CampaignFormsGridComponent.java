package de.symeda.sormas.ui.campaign.campaigns;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.utils.AbstractEditableGrid;

@SuppressWarnings("serial")
public class CampaignFormsGridComponent extends AbstractEditableGrid<CampaignFormMetaReferenceDto> {

	public CampaignFormsGridComponent(
		List<CampaignFormMetaReferenceDto> savedCampaignFormMetas,
		List<CampaignFormMetaReferenceDto> allCampaignFormMetas) {

		super(savedCampaignFormMetas, allCampaignFormMetas);
		setWidth(40, Unit.PERCENTAGE);
	}

	@Override
	protected Button.ClickListener newRowEvent() {
		return event -> {
			final ArrayList<CampaignFormMetaReferenceDto> gridItems = getItems();
			gridItems.add(new CampaignFormMetaReferenceDto(null, ""));
			grid.setItems(gridItems);
		};
	}

	@Override
	protected Binder<CampaignFormMetaReferenceDto> addColumnsBinder(List<CampaignFormMetaReferenceDto> allElements) {
		final Binder<CampaignFormMetaReferenceDto> binder = new Binder<>();

		// This is a bit hacky: The grid is used here to "select" the whole item instead of editing properties
		// This is done by replacing uuid and caption of the item

		ComboBox<CampaignFormMetaReferenceDto> formCombo = new ComboBox<>(Strings.entityCampaignDataForm, allElements);

		Binder.Binding<CampaignFormMetaReferenceDto, CampaignFormMetaReferenceDto> formBind = binder.forField(formCombo)
				.withValidator(
						campaignFormMetaReferenceDto -> campaignFormMetaReferenceDto != null && campaignFormMetaReferenceDto.getUuid() != null,
						I18nProperties.getValidationError(Validations.campaignDashboardDataFormValueNull))
				.withValidator(campaignFormMetaReferenceDto -> {
					ArrayList<CampaignFormMetaReferenceDto> items = getItems();
					return !items.contains(campaignFormMetaReferenceDto);
				}, I18nProperties.getValidationError(Validations.campaignDashboardDataFormValueDuplicate))
				.bind(campaignFormMetaReferenceDto -> new CampaignFormMetaReferenceDto(campaignFormMetaReferenceDto.getUuid(), campaignFormMetaReferenceDto.getCaption()),
						(bindedCampaignFormMeta, selectedCampaignFormMeta) -> {
							bindedCampaignFormMeta.setUuid(selectedCampaignFormMeta.getUuid());
							bindedCampaignFormMeta.setCaption(selectedCampaignFormMeta.getCaption());
							// workarround: grid doesn't refresh itself for unknown reason
							grid.getDataProvider().refreshAll();
						});
		formCombo.setEmptySelectionAllowed(false);

		Grid.Column<CampaignFormMetaReferenceDto, String> formColumn =
				grid.addColumn(ReferenceDto::getCaption)
						.setCaption(I18nProperties.getString(Strings.entityCampaignDataForm));

		formColumn.setEditorBinding(formBind);
		return binder;
	}

	protected String getHeaderString() {
		return Strings.headingCampaignData;
	}

	@Override
	protected void reorderGrid() {
	}

	protected String getAdditionalRowCaption() {
		return Captions.campaignAdditionalForm;
	}
}
