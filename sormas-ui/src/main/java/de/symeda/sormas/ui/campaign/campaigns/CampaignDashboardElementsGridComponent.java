package de.symeda.sormas.ui.campaign.campaigns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Binder;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElementWithCaption;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.diagram.DiagramIdCaption;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.ui.utils.AbstractEditableGrid;

public class CampaignDashboardElementsGridComponent extends AbstractEditableGrid<CampaignDashboardElementWithCaption> {

	public CampaignDashboardElementsGridComponent(
		List<CampaignDashboardElementWithCaption> savedElements,
		List<CampaignDashboardElementWithCaption> allElements) {
		super(savedElements, allElements);
		setWidth(100, Unit.PERCENTAGE);
	}

	protected Binder<CampaignDashboardElementWithCaption> addColumnsBinder(List<CampaignDashboardElementWithCaption> allElements) {
		Binder<CampaignDashboardElementWithCaption> binder = new Binder<>();

		final List<CampaignDiagramDefinitionDto> campaignDiagramDefinitionDtos = FacadeProvider.getCampaignDiagramDefinitionFacade().getAll();

		ComboBox<DiagramIdCaption> diagramIdCaptionCombo = new ComboBox<>(
			Captions.campaignDashboardChart,
			campaignDiagramDefinitionDtos.stream()
				.map(cdd -> new DiagramIdCaption(cdd.getDiagramId(), cdd.getDiagramCaption()))
				.collect(Collectors.toList()));
		diagramIdCaptionCombo.setEmptySelectionAllowed(false);

		final Map<String, String> diagramIdCaptionMap = campaignDiagramDefinitionDtos.stream()
			.collect(Collectors.toMap(CampaignDiagramDefinitionDto::getDiagramId, CampaignDiagramDefinitionDto::getDiagramCaption));

		Binder.Binding<CampaignDashboardElementWithCaption, DiagramIdCaption> diagramIdCaptionBind = binder.bind(
			diagramIdCaptionCombo,
			cde -> new DiagramIdCaption(cde.getDiagramId(), diagramIdCaptionMap.get(cde.getDiagramId())),
			(campaignDashboardElementWithCaption, diagramIdCaption) -> {
				campaignDashboardElementWithCaption.setDiagramId(diagramIdCaption.getDiagramId());
				campaignDashboardElementWithCaption.setDiagramCaption(diagramIdCaption.getDiagramCaption());
			});

		final Grid.Column<CampaignDashboardElementWithCaption, String> diagramIdColumn =
			grid.addColumn(campaignDashboardElement -> campaignDashboardElement.getDiagramCaption())
				.setCaption(I18nProperties.getCaption(Captions.campaignDashboardChart));
		diagramIdColumn.setEditorBinding(diagramIdCaptionBind);

		final List<String> existingTabIds = allElements.stream()
			.map(campaignDiagramDefinitionDto -> campaignDiagramDefinitionDto.getTabId())
			.filter(s -> StringUtils.isNotEmpty(s))
			.distinct()
			.collect(Collectors.toList());
		final ComboBox<String> tabIdCombo = new ComboBox<>(Captions.campaignDashboardTabName, existingTabIds);

		tabIdCombo.setEmptySelectionAllowed(false);
		tabIdCombo.setTextInputAllowed(true);
		tabIdCombo.setNewItemProvider((ComboBox.NewItemProvider<String>) s -> Optional.of(s));

		final Binder.Binding<CampaignDashboardElementWithCaption, String> tabIdBind =
			binder.bind(tabIdCombo, CampaignDashboardElement::getTabId, CampaignDashboardElement::setTabId);
		final Grid.Column<CampaignDashboardElementWithCaption, String> tabIdColumn =
			grid.addColumn(campaignDashboardElement -> campaignDashboardElement.getTabId())
				.setCaption(I18nProperties.getCaption(Captions.campaignDashboardTabName));
		tabIdColumn.setEditorBinding(tabIdBind);

		TextField width = new TextField(Captions.campaignDashboardChartWidth);
		Binder.Binding<CampaignDashboardElementWithCaption, String> widthBind = binder.forField(width)
			.withValidator(percentValidator(), I18nProperties.getValidationError(Validations.campaignDashboardChartPercentage))
			.bind(campaignDashboardElement -> intToString(campaignDashboardElement.getWidth()), (c, s) -> c.setWidth(new Integer(s)));
		Grid.Column<CampaignDashboardElementWithCaption, String> widthColumn =
			grid.addColumn(campaignDashboardElement -> intToString(campaignDashboardElement.getWidth()))
				.setCaption(I18nProperties.getCaption(Captions.campaignDashboardChartWidth));
		widthColumn.setEditorBinding(widthBind);

		TextField height = new TextField(Captions.campaignDashboardChartHeight);
		Binder.Binding<CampaignDashboardElementWithCaption, String> heightBind = binder.forField(height)
			.withValidator(percentValidator(), I18nProperties.getValidationError(Validations.campaignDashboardChartPercentage))
			.bind(campaignDashboardElement -> intToString(campaignDashboardElement.getHeight()), (c, s) -> c.setHeight(new Integer(s)));
		Grid.Column<CampaignDashboardElementWithCaption, String> heightColumn =
			grid.addColumn(campaignDashboardElement -> intToString(campaignDashboardElement.getHeight()))
				.setCaption(I18nProperties.getCaption(Captions.campaignDashboardChartHeight));
		heightColumn.setEditorBinding(heightBind);

		TextField order = new TextField(Captions.campaignDashboardOrder);
		order.setEnabled(false);
		Binder.Binding<CampaignDashboardElementWithCaption, String> orderBind =
			binder.bind(order, campaignDashboardElement -> intToString(campaignDashboardElement.getOrder()), (c, s) -> c.setOrder(new Integer(s)));
		Grid.Column<CampaignDashboardElementWithCaption, String> orderColumn =
			grid.addColumn(campaignDashboardElement -> intToString(campaignDashboardElement.getOrder()))
				.setCaption(I18nProperties.getCaption(Captions.campaignDashboardOrder));
		orderColumn.setEditorBinding(orderBind);
		return binder;
	}

	protected Button.ClickListener newRowEvent() {
		return event -> {
			final CampaignDashboardElementWithCaption campaignDashboardElement = new CampaignDashboardElementWithCaption();
			final ArrayList<CampaignDashboardElementWithCaption> gridItems = getItems();
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
		final ArrayList<CampaignDashboardElementWithCaption> gridItems = getItems();
		gridItems.forEach(campaignDashboardElement -> campaignDashboardElement.setOrder(gridItems.indexOf(campaignDashboardElement)));
	}

}
