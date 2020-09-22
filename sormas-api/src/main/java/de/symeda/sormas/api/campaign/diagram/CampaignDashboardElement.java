package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;

public class CampaignDashboardElement implements Serializable {

    private String diagramId;
    private String tabId;
    private Integer order;
    private Integer width;
    private Integer height;

    public CampaignDashboardElement() {
    }

    public CampaignDashboardElement(String diagramId, String tabId, Integer order, Integer width, Integer height) {
        this.diagramId = diagramId;
        this.tabId = tabId;
        this.order = order;
        this.width = width;
        this.height = height;
    }

    public String getDiagramId() {
        return diagramId;
    }

    public void setDiagramId(String diagramId) {
        this.diagramId = diagramId;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
