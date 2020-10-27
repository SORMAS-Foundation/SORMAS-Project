package de.symeda.sormas.ui.campaign.campaigns;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Binder;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.utils.AbstractEditableGrid;

public class CampaignDashboardElementsGridComponent extends AbstractEditableGrid<CampaignDashboardElement> {

	public CampaignDashboardElementsGridComponent(List<CampaignDashboardElement> savedElements, List<CampaignDashboardElement> allElements) {
		super(savedElements, allElements);
		setWidth(100, Unit.PERCENTAGE);
	}

	protected Binder<CampaignDashboardElement> addColumnsBinder(List<CampaignDashboardElement> allElements) {
		Binder<CampaignDashboardElement> binder = new Binder<>();

		ComboBox<String> diagramIdCombo = new ComboBox<>(
			Captions.campaignDashboardChart,
			allElements.stream()
				.map(campaignDiagramDefinitionDto -> campaignDiagramDefinitionDto.getDiagramId())
				.distinct()
				.collect(Collectors.toList()));
		diagramIdCombo.setEmptySelectionAllowed(false);
		Binder.Binding<CampaignDashboardElement, String> diagramIdBind =
			binder.bind(diagramIdCombo, CampaignDashboardElement::getDiagramId, CampaignDashboardElement::setDiagramId);
		Grid.Column<CampaignDashboardElement, String> diagramIdColumn =
			grid.addColumn(campaignDashboardElement -> campaignDashboardElement.getDiagramId())
				.setCaption(I18nProperties.getCaption(Captions.campaignDashboardChart));
		diagramIdColumn.setEditorBinding(diagramIdBind);

		ComboBox<String> tabIdCombo = new ComboBox<>(
			Captions.campaignDashboardTabName,
			allElements.stream()
				.map(campaignDiagramDefinitionDto -> campaignDiagramDefinitionDto.getTabId())
				.distinct()
				.collect(Collectors.toList()));

		tabIdCombo.setEmptySelectionAllowed(false);
		tabIdCombo.setTextInputAllowed(true);
		tabIdCombo.setNewItemProvider((ComboBox.NewItemProvider<String>) s -> Optional.of(s));

		Binder.Binding<CampaignDashboardElement, String> tabIdBind =
			binder.bind(tabIdCombo, CampaignDashboardElement::getTabId, CampaignDashboardElement::setTabId);
		Grid.Column<CampaignDashboardElement, String> tabIdColumn = grid.addColumn(campaignDashboardElement -> campaignDashboardElement.getTabId())
			.setCaption(I18nProperties.getCaption(Captions.campaignDashboardTabName));
		tabIdColumn.setEditorBinding(tabIdBind);

		TextField width = new TextField(Captions.campaignDashboardChartWidth);
		Binder.Binding<CampaignDashboardElement, String> widthBind = binder.forField(width)
			.withValidator(percentValidator(), I18nProperties.getValidationError(Validations.campaignDashboardChartPercentage))
			.bind(campaignDashboardElement -> intToString(campaignDashboardElement.getWidth()), (c, s) -> c.setWidth(new Integer(s)));
		Grid.Column<CampaignDashboardElement, String> widthColumn =
			grid.addColumn(campaignDashboardElement -> intToString(campaignDashboardElement.getWidth()))
				.setCaption(I18nProperties.getCaption(Captions.campaignDashboardChartWidth));
		widthColumn.setEditorBinding(widthBind);

		TextField height = new TextField(Captions.campaignDashboardChartHeight);
		Binder.Binding<CampaignDashboardElement, String> heightBind = binder.forField(height)
			.withValidator(percentValidator(), I18nProperties.getValidationError(Validations.campaignDashboardChartPercentage))
			.bind(campaignDashboardElement -> intToString(campaignDashboardElement.getHeight()), (c, s) -> c.setHeight(new Integer(s)));
		Grid.Column<CampaignDashboardElement, String> heightColumn =
			grid.addColumn(campaignDashboardElement -> intToString(campaignDashboardElement.getHeight()))
				.setCaption(I18nProperties.getCaption(Captions.campaignDashboardChartHeight));
		heightColumn.setEditorBinding(heightBind);

		TextField order = new TextField(Captions.campaignDashboardOrder);
		order.setEnabled(false);
		Binder.Binding<CampaignDashboardElement, String> orderBind =
			binder.bind(order, campaignDashboardElement -> intToString(campaignDashboardElement.getOrder()), (c, s) -> c.setOrder(new Integer(s)));
		Grid.Column<CampaignDashboardElement, String> orderColumn =
			grid.addColumn(campaignDashboardElement -> intToString(campaignDashboardElement.getOrder()))
				.setCaption(I18nProperties.getCaption(Captions.campaignDashboardOrder));
		orderColumn.setEditorBinding(orderBind);
		return binder;
	}

	protected Button.ClickListener newRowEvent() {
		return event -> {
			final CampaignDashboardElement campaignDashboardElement = new CampaignDashboardElement();
			final ArrayList<CampaignDashboardElement> gridItems = getItems();
			gridItems.add(campaignDashboardElement);
			campaignDashboardElement.setOrder(gridItems.indexOf(campaignDashboardElement));
			grid.setItems(gridItems);
		};
	}

	private SerializablePredicate<String> percentValidator() {
		return s -> new Integer(s) % 5 == 0;
	}

	private String intToString(Integer h) {
		return h != null ? h.toString() : StringUtils.EMPTY;
	}

	protected String getHeaderString() {
		return Strings.headingCampaignDashboard;
	}

	protected String getAdditionalRowCaption() {
		return Captions.campaignAdditionalChart;
	}

	protected void reorderGrid() {
		final ArrayList<CampaignDashboardElement> gridItems = getItems();
		gridItems.forEach(campaignDashboardElement -> campaignDashboardElement.setOrder(gridItems.indexOf(campaignDashboardElement)));
	}
}
