package de.symeda.sormas.backend.campaign.diagram;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.api.campaign.diagram.DiagramType;
import de.symeda.sormas.backend.AbstractBeanTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CampaignDiagramDefinitionFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testSaveAndGetCampaignDiagramDefinition() {

		final CampaignDiagramDefinitionDto campaignDiagramDefinitionDto = new CampaignDiagramDefinitionDto();
		campaignDiagramDefinitionDto.setDiagramId("testDiagram");
		campaignDiagramDefinitionDto.setDiagramType(DiagramType.COLUMN);
		final ArrayList<CampaignDiagramSeries> campaignDiagramSeriesList = new ArrayList<>();
		campaignDiagramSeriesList.add(diagramSeries("recordedAbsentDuring", "tallySheetTotalsForm", "days 1-3"));
		campaignDiagramSeriesList.add(diagramSeries("recordedAbsentAfter", "tallySheetTotalsForm", "days 1-3"));
		campaignDiagramSeriesList.add(diagramSeries("recordedNewbornSleepSick", "tallySheetTotalsForm", "days 1-3"));
		campaignDiagramSeriesList.add(diagramSeries("recordedRefusal", "tallySheetTotalsForm", "days 1-3"));
		campaignDiagramSeriesList.add(diagramSeries("missedStillAbsent", "monitoringRevisitForm", "revisit"));
		campaignDiagramSeriesList.add(diagramSeries("missedRefusals", "monitoringRevisitForm", "revisit"));
		campaignDiagramSeriesList.add(diagramSeries("missedTeamNegligence", "monitoringRevisitForm", "revisit"));
		campaignDiagramDefinitionDto.setCampaignDiagramSeries(campaignDiagramSeriesList);

		final CampaignDiagramDefinitionDto savedDiagramDefinition = getCampaignDiagramDefinitionFacade().save(campaignDiagramDefinitionDto);

		assertEquals("testDiagram", savedDiagramDefinition.getDiagramId());
		assertEquals(DiagramType.COLUMN, savedDiagramDefinition.getDiagramType());
		final List<CampaignDiagramSeries> savedCampaignDiagramSeriesList = savedDiagramDefinition.getCampaignDiagramSeries();
		assertEquals(7, savedCampaignDiagramSeriesList.size());
		final CampaignDiagramSeries campaignDiagramSeries1 = savedCampaignDiagramSeriesList.get(0);
		assertEquals("recordedAbsentDuring", campaignDiagramSeries1.getFieldId());
		assertEquals("tallySheetTotalsForm", campaignDiagramSeries1.getFormId());
		assertEquals("days 1-3", campaignDiagramSeries1.getStack());
		final CampaignDiagramSeries campaignDiagramSeries7 = savedCampaignDiagramSeriesList.get(6);
		assertEquals("missedTeamNegligence", campaignDiagramSeries7.getFieldId());
		assertEquals("monitoringRevisitForm", campaignDiagramSeries7.getFormId());
		assertEquals("revisit", campaignDiagramSeries7.getStack());

		savedDiagramDefinition.setDiagramId("test2");
		getCampaignDiagramDefinitionFacade().save(savedDiagramDefinition);
	}

	private CampaignDiagramSeries diagramSeries(String fieldId, String formId, String stack) {
		final CampaignDiagramSeries campaignDiagramSeries = new CampaignDiagramSeries();
		campaignDiagramSeries.setFieldId(fieldId);
		campaignDiagramSeries.setFormId(formId);
		campaignDiagramSeries.setStack(stack);
		return campaignDiagramSeries;
	}

}
