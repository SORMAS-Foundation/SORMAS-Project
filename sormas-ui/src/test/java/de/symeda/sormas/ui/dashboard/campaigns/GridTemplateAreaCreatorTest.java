package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;

@RunWith(MockitoJUnitRunner.class)
public class GridTemplateAreaCreatorTest {

    private GridTemplateAreaCreator gridTemplateAreaCreator = new GridTemplateAreaCreator();

    @Test
    public void testGridTemplateCreateForDiagramsCase1(){
        final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 60, 100));
        dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 40, 50));
        dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 40, 50));

        final String expectedResult = "'d1 d1 d1 d2 d2''d1 d1 d1 d3 d3'";
        Assert.assertEquals(expectedResult, gridTemplateAreaCreator.createGridTemplate(dashboardElements));
    }

    @Test
    public void testGridTemplateCreateForDiagramsCase2(){
        final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 50, 50));
        dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 50, 50));
        dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 50, 50));
        dashboardElements.add(new CampaignDashboardElement("d4", null, 4, 50, 50));

        final String expectedResult = "'d1 d2''d3 d4'";
        Assert.assertEquals(expectedResult, gridTemplateAreaCreator.createGridTemplate(dashboardElements));
    }

    @Test
    public void testGridTemplateCreateForDiagramsCase3(){
        final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 50, 50));
        dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 50, 50));
        dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 50, 50));
        dashboardElements.add(new CampaignDashboardElement("d4", null, 4, 50, 50));
        dashboardElements.add(new CampaignDashboardElement("d5", null, 5, 50, 50));
        dashboardElements.add(new CampaignDashboardElement("d6", null, 6, 50, 50));

        final String expectedResult = "'d1 d2''d3 d4''d5 d6'";
        Assert.assertEquals(expectedResult, gridTemplateAreaCreator.createGridTemplate(dashboardElements));
    }

    @Test
    public void testGridTemplateCreateForDiagramsCase4(){
        final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 50, 100));
        dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 50, 100));

        final String expectedResult = "'d1 d2'";
        Assert.assertEquals(expectedResult, gridTemplateAreaCreator.createGridTemplate(dashboardElements));
    }

    @Test
    public void testGridTemplateCreateForDiagramsCase5(){
        final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 40, 50));
        dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 60, 100));
        dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 40, 50));

        final String expectedResult = "'d1 d1 d2 d2 d2''d3 d3 d2 d2 d2'";
        Assert.assertEquals(expectedResult, gridTemplateAreaCreator.createGridTemplate(dashboardElements));
    }

    @Test
    public void testGridTemplateCreateForDiagramsCase6(){
        final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 100, 100));

        final String expectedResult = "'d1'";
        Assert.assertEquals(expectedResult, gridTemplateAreaCreator.createGridTemplate(dashboardElements));
    }

    @Test
    public void testGridTemplateCreateForDiagramsCase7(){
        final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 60, 100));
        dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 40, 50));
        dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 40, 50));
        dashboardElements.add(new CampaignDashboardElement("d4", null, 4, 60, 100));

        final String expectedResult = "'d1 d1 d1 d2 d2''d1 d1 d1 d3 d3''d4 d4 d4 null null''d4 d4 d4 null null'";
        Assert.assertEquals(expectedResult, gridTemplateAreaCreator.createGridTemplate(dashboardElements));
    }

    @Test
    public void testGridTemplateCreateForDiagramsCase8(){
        final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 40, 50));

        final String expectedResult = "'d1 null'"; // this is questionable
        Assert.assertEquals(expectedResult, gridTemplateAreaCreator.createGridTemplate(dashboardElements));
    }

    @Test
    public void testGridTemplateCreateForDiagramsCase9(){
        final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 60, 100));
        dashboardElements.add(new CampaignDashboardElement("d2", null, 4, 100, 100));

        final String expectedResult = "'d1 d1 d1 null null''d2 d2 d2 d2 d2'";
        Assert.assertEquals(expectedResult, gridTemplateAreaCreator.createGridTemplate(dashboardElements));
    }

    @Test
    public void testGridTemplateCreateForDiagramsCase10(){
        final List<CampaignDashboardElement> dashboardElements = new ArrayList<>();
        dashboardElements.add(new CampaignDashboardElement("d1", null, 1, 60, 80));
        dashboardElements.add(new CampaignDashboardElement("d2", null, 2, 40, 40));
        dashboardElements.add(new CampaignDashboardElement("d3", null, 3, 40, 40));

        final String expectedResult = "'d1 d1 d1 d2 d2''d1 d1 d1 d3 d3'";
        Assert.assertEquals(expectedResult, gridTemplateAreaCreator.createGridTemplate(dashboardElements));
    }
}