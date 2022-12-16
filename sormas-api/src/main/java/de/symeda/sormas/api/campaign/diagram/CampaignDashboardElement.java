package de.symeda.sormas.api.campaign.diagram;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;
import io.swagger.v3.oas.annotations.media.Schema;

public class CampaignDashboardElement implements Serializable {

	private static final long serialVersionUID = 807316058932832033L;

	public static final String DIAGRAM_ID = "diagramId";
	public static final String TAB_ID = "tabId";
	public static final String SUB_TAB_ID = "subTabId";
	public static final String ORDER = "order";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";

	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Identifier of the diagram")
	private String diagramId;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Identifier of the page tab")
	private String tabId;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Identifier of the sub-tab")
	private String subTabId;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private Integer order;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private Integer width;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
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
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CampaignDashboardElement that = (CampaignDashboardElement) o;
		return Objects.equals(diagramId, that.diagramId)
			&& Objects.equals(tabId, that.tabId)
			&& Objects.equals(subTabId, that.subTabId)
			&& Objects.equals(order, that.order)
			&& Objects.equals(width, that.width)
			&& Objects.equals(height, that.height);
	}

	@Override
	public int hashCode() {
		return Objects.hash(diagramId, tabId, subTabId, order, width, height);
	}
}
