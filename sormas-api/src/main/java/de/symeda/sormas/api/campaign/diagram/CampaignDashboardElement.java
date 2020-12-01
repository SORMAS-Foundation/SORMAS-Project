package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;
import java.util.Objects;

public class CampaignDashboardElement implements Serializable {

	public static String DIAGRAM_ID = "diagramId";
	public static String TAB_ID = "tabId";
	public static String SUB_TAB_ID = "subTabId";
	public static String ORDER = "order";
	public static String WIDTH = "width";
	public static String HEIGHT = "height";

	private String diagramId;
	private String tabId;
	private String subTabId;
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

	public CampaignDashboardElement(String diagramId, String tabId, String subTabId, Integer order, Integer width, Integer height) {
		this(diagramId, tabId, order, width, height);
		this.subTabId = subTabId;
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

	public String getSubTabId() {
		return subTabId;
	}

	public void setSubTabId(String subTabId) {
		this.subTabId = subTabId;
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

	/**
	 * Needed. Otherwise hibernate will persist whenever loading,
	 * because hibernate types creates new instances that aren't equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CampaignDashboardElement that = (CampaignDashboardElement) o;
		return Objects.equals(diagramId, that.diagramId) &&
				Objects.equals(tabId, that.tabId) &&
				Objects.equals(subTabId, that.subTabId) &&
				Objects.equals(order, that.order) &&
				Objects.equals(width, that.width) &&
				Objects.equals(height, that.height);
	}

	@Override
	public int hashCode() {
		return Objects.hash(diagramId, tabId, subTabId, order, width, height);
	}
}
