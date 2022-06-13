package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;

@RunWith(MockitoJUnitRunner.class)
public class GridTemplateAreaCreatorTest {

	@Test
	public void testGridTemplateCreateForDiagramsCase1() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 60, 100));
		dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 40, 50));
		dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 40, 50));

		final String expectedResult = "'d1 d1 d1 d2 d2''d1 d1 d1 d3 d3'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase2() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 50, 50));
		dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 50, 50));
		dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 50, 50));
		dashboardElements.add(new CampaignDashboardElement("d4", null, 4, 50, 50));

		final String expectedResult = "'d1 d2''d3 d4'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase3() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 50, 50));
		dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 50, 50));
		dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 50, 50));
		dashboardElements.add(new CampaignDashboardElement("d4", null, 4, 50, 50));
		dashboardElements.add(new CampaignDashboardElement("d5", null, 5, 50, 50));
		dashboardElements.add(new CampaignDashboardElement("d6", null, 6, 50, 50));

		final String expectedResult = "'d1 d2''d3 d4''d5 d6'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase4() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 50, 100));
		dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 50, 100));

		final String expectedResult = "'d1 d2'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase5() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 40, 50));
		dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 60, 100));
		dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 40, 50));

		final String expectedResult = "'d1 d1 d2 d2 d2''d3 d3 d2 d2 d2'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase6() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 100, 100));

		final String expectedResult = "'d1'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase7() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 60, 100));
		dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 40, 50));
		dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 40, 50));
		dashboardElements.add(new CampaignDashboardElement("d4", null, 4, 60, 100));

		final String expectedResult = "'d1 d1 d1 d2 d2''d1 d1 d1 d3 d3''d4 d4 d4 area2 area2''d4 d4 d4 area3 area3'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase8() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 40, 50));

		final String expectedResult = "'d1 area0'"; // this is questionable
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase9() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 60, 100));
		dashboardElements.add(new CampaignDashboardElement("d2", null, 4, 100, 100));

		final String expectedResult = "'d1 d1 d1 area0 area0''d2 d2 d2 d2 d2'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase10() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 60, 80));
		dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 40, 40));
		dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 40, 40));

		final String expectedResult = "'d1 d1 d1 d2 d2''d1 d1 d1 d3 d3'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase11() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 60, 100));
		dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 40, 50));
		dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 70, 50));

		final String expectedResult =
			"'d1 d1 d1 d1 d1 d1 d2 d2 d2 d2''d1 d1 d1 d1 d1 d1 area1 area1 area1 area1''d3 d3 d3 d3 d3 d3 d3 area2 area2 area2'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}

	@Test
	public void testGridTemplateCreateForDiagramsCase12() {
		final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
		dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 30, 60));
		dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 30, 60));
		dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 70, 90));

		final String expectedResult =
			"'d1 d1 d1 d2 d2 d2 area0 area0 area0 area0''d1 d1 d1 d2 d2 d2 area1 area1 area1 area1''d3 d3 d3 d3 d3 d3 d3 area2 area2 area2''d3 d3 d3 d3 d3 d3 d3 area3 area3 area3''d3 d3 d3 d3 d3 d3 d3 area4 area4 area4'";
		Assert.assertEquals(expectedResult, new GridTemplateAreaCreator(dashboardElements).getFormattedGridTemplate());
	}
}
