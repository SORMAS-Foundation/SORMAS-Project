package de.symeda.sormas.ui.campaign.campaigns;

import java.util.ArrayList;
import java.util.List;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.utils.AbstractEditableGrid;

@SuppressWarnings("serial")
public class CampaignFormsGridComponent extends AbstractEditableGrid<CampaignFormMetaReferenceDto> {

	public CampaignFormsGridComponent(List<CampaignFormMetaReferenceDto> savedCampaignFormMetas,
			List<CampaignFormMetaReferenceDto> allCampaignFormMetas) {

		super(savedCampaignFormMetas, allCampaignFormMetas);

		setWidth(100, Unit.PERCENTAGE);
	}

	@Override
	protected Button.ClickListener newRowEvent() {
		return event -> {
			final ArrayList<CampaignFormMetaReferenceDto> gridItems = getItems();
			gridItems.add(new CampaignFormMetaReferenceDto(null, " --Please select--", null, 0));

			grid.setItems(gridItems);

			grid.getEditor().cancel();

			grid.getEditor().editRow(gridItems.size() - 1);

		};
	}



	public void ListnerCampaignFilter(TabSheet.SelectedTabChangeEvent event) {
		final ArrayList<CampaignFormMetaReferenceDto> gridItemss = getItems();
		// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> --
		// "+gridItemss);

		final ArrayList<CampaignFormMetaReferenceDto> gridItems;

		// gridItems.add(new CampaignFormMetaReferenceDto(null, " --Please select--"));
		// tabsheetParent.addSelectedTabChangeListener(event ->
		// Notification.show("changed "
		// +event.getTabSheet().getSelectedTab().getCaption()));

		// Notification.show("----" +
		// event.getTabSheet().getSelectedTab().getCaption());

		System.out.println(event.getTabSheet().getSelectedTab().getCaption() + " | ___________---______O___");

		if (event.getTabSheet().getSelectedTab().getCaption().equals("Pre-Campaign Phase")) {
			gridItems = gridItemss;
			gridItems.removeIf(n -> (n.getFormType().contains("Pre-Campaign")));
			// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> --
			// "+gridItems);

			grid.setItems(gridItems);

		} else if (event.getTabSheet().getSelectedTab().getCaption() == "Intra-Campaign Phase") {
			gridItems = gridItemss;
			gridItems.removeIf(n -> (n.getFormType().contains("Intra-Campaign")));
			grid.setItems(gridItems);

		} else if (event.getTabSheet().getSelectedTab().getCaption() == "Post-Campaign Phase") {
			gridItems = gridItemss;
			gridItems.removeIf(n -> (n.getFormType().contains("Post-Campaign")));
			grid.setItems(gridItems);

		}

		// grid.removeAllColumns();
		grid.getDataProvider().refreshAll();

		// grid.getDataProvider().refreshAll();

		// grid.getEditor().editRow(gridItems.size() - 1);

		// grid.removeAllColumns();

		// Page.getCurrent().getJavaScript().execute("alert(gridItems.toString())");

	}

	@Override
	protected Binder<CampaignFormMetaReferenceDto> addColumnsBinder(List<CampaignFormMetaReferenceDto> allElements) {

		// todo check if we can remove elements that are null
		final Binder<CampaignFormMetaReferenceDto> binder = new Binder<>();

		// This is a bit hacky: The grid is used here to "select" the whole item instead
		// of editing properties
		// This is done by replacing uuid and caption of the item

		ComboBox<CampaignFormMetaReferenceDto> formCombo = new ComboBox<>(Strings.entityCampaignDataForm, allElements);
		
		TextField dateExpiring = new TextField("Date");
		
		dateExpiring.setEnabled(false);
		
		Binding<CampaignFormMetaReferenceDto, String> dateBind = binder.forField(dateExpiring)
				.bind(campaignFormMetaReferenceDto -> new CampaignFormMetaReferenceDto(
						
						campaignFormMetaReferenceDto.getUuid(),campaignFormMetaReferenceDto.getDaysExpired()).toString(),
						(bindedCampaignFormMeta, selectedCampaignFormMeta) -> {
							bindedCampaignFormMeta.setUuid(selectedCampaignFormMeta);
							bindedCampaignFormMeta.setDateExpired(selectedCampaignFormMeta);
							grid.getDataProvider().refreshAll();
						});
	
		
		

		Binder.Binding<CampaignFormMetaReferenceDto, CampaignFormMetaReferenceDto> formBind = binder.forField(formCombo)
				.withValidator(
						campaignFormMetaReferenceDto -> campaignFormMetaReferenceDto != null
								&& campaignFormMetaReferenceDto.getUuid() != null,
						I18nProperties.getValidationError(Validations.campaignDashboardDataFormValueNull))

				.withValidator(campaignFormMetaReferenceDto -> {
					ArrayList<CampaignFormMetaReferenceDto> items = getItems();
					return !items.contains(campaignFormMetaReferenceDto);
				}, I18nProperties.getValidationError(Validations.campaignDashboardDataFormValueDuplicate))
				.bind(campaignFormMetaReferenceDto -> new CampaignFormMetaReferenceDto(
						campaignFormMetaReferenceDto.getUuid(), campaignFormMetaReferenceDto.getCaption(),
						campaignFormMetaReferenceDto.getFormType()),
						(bindedCampaignFormMeta, selectedCampaignFormMeta) -> {
							bindedCampaignFormMeta.setUuid(selectedCampaignFormMeta.getUuid());
							bindedCampaignFormMeta.setCaption(selectedCampaignFormMeta.getCaption());
							bindedCampaignFormMeta.setFormType(selectedCampaignFormMeta.getFormType());
							// workarround: grid doesn't refresh itself for unknown reason
							grid.getDataProvider().refreshAll();
						});
		formCombo.setEmptySelectionAllowed(false);

		Grid.Column<CampaignFormMetaReferenceDto, String> formColumn;
		formColumn = grid.addColumn(ReferenceDto::getCaption)
				.setCaption(I18nProperties.getString(Strings.entityCampaignDataForm));
		
		Grid.Column<CampaignFormMetaReferenceDto, Integer> deadlineColumn;
		deadlineColumn = grid.addColumn(CampaignFormMetaReferenceDto::getDaysExpired)
				.setCaption("Form Deadline (Days)");
		// formColumn =
		// grid.addColumn(ReferenceDto::getFormType).setCaption(I18nProperties.getString(Strings.entityCampaignDataFormPhase));

		formColumn.setId("formtb");
		formColumn.setEditorBinding(formBind);
		
		
		deadlineColumn.setEditorBinding(dateBind);

		/*
		 * Grid.Column<CampaignFormMetaReferenceDto, String> formColumnx =
		 * grid.addColumn(ReferenceDto::getUuid)
		 * .setCaption(I18nProperties.getString(Strings.entityCampaignDataForm));
		 * formColumnx.setId("formtbv"); formColumnx.setEditorBinding(formBind);
		 */

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
