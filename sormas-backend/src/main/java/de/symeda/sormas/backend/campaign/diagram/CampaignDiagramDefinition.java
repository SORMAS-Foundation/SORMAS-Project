package de.symeda.sormas.backend.campaign.diagram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Transient;

import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.api.campaign.diagram.DiagramType;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = "campaignDiagramDefinition")
@Audited
public class CampaignDiagramDefinition extends AbstractDomainObject {

	private String diagramId;
	private DiagramType diagramType;
	private String campaignDiagramSeries;
	private List<CampaignDiagramSeries> campaignDiagramSeriesList;

	@Column
	public String getDiagramId() {
		return diagramId;
	}

	public void setDiagramId(String diagramId) {
		this.diagramId = diagramId;
	}

	@Enumerated(EnumType.STRING)
	public DiagramType getDiagramType() {
		return diagramType;
	}

	public void setDiagramType(DiagramType diagramType) {
		this.diagramType = diagramType;
	}

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	public String getCampaignDiagramSeries() {
		return campaignDiagramSeries;
	}

	public void setCampaignDiagramSeries(String campaignDiagramSeries) {
		this.campaignDiagramSeries = campaignDiagramSeries;
		campaignDiagramSeriesList = null;
	}

	@Transient
	public List<CampaignDiagramSeries> getCampaignDiagramSeriesList() {
		if (campaignDiagramSeriesList == null) {
			if (StringUtils.isBlank(campaignDiagramSeries)) {
				campaignDiagramSeriesList = new ArrayList<>();
			} else {
				try {
					ObjectMapper mapper = new ObjectMapper();
					campaignDiagramSeriesList = Arrays.asList(mapper.readValue(campaignDiagramSeries, CampaignDiagramSeries[].class));
				} catch (IOException e) {
					throw new ValidationRuntimeException(
						"Content of campaignDiagramSeries could not be parsed to List<CampaignDiagramSeries> - ID: " + getId());
				}
			}
		}
		return campaignDiagramSeriesList;
	}

	public void setCampaignDiagramSeriesList(List<CampaignDiagramSeries> campaignDiagramSeriesList) {
		this.campaignDiagramSeriesList = campaignDiagramSeriesList;

		if (this.campaignDiagramSeriesList == null) {
			campaignDiagramSeries = null;
			return;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			campaignDiagramSeries = mapper.writeValueAsString(campaignDiagramSeriesList);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Content of campaignDiagramSeriesList could not be parsed to JSON String - ID: " + getId());
		}
	}
}
