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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramTranslations;
import de.symeda.sormas.api.campaign.diagram.DiagramType;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class CampaignDiagramDefinition extends AbstractDomainObject {

	private static final long serialVersionUID = 7360131476160449930L;

	public static String DIAGRAM_ID = "diagramId";

	private String diagramId;
	private String diagramCaption;
	private DiagramType diagramType;
	private List<CampaignDiagramSeries> campaignDiagramSeries;
	private List<CampaignDiagramSeries> campaignSeriesTotal;
	private boolean percentageDefault;
	private String campaignDiagramTranslations;
	private List<CampaignDiagramTranslations> campaignDiagramTranslationsList;

	@Column
	public String getDiagramId() {
		return diagramId;
	}

	public void setDiagramId(String diagramId) {
		this.diagramId = diagramId;
	}

	@Column
	public String getDiagramCaption() {
		return diagramCaption;
	}

	public void setDiagramCaption(String diagramCaption) {
		this.diagramCaption = diagramCaption;
	}

	@Enumerated(EnumType.STRING)
	public DiagramType getDiagramType() {
		return diagramType;
	}

	public void setDiagramType(DiagramType diagramType) {
		this.diagramType = diagramType;
	}

	@AuditedIgnore
	@Type(type = "json")
	@Column(columnDefinition = "json")
	public List<CampaignDiagramSeries> getCampaignDiagramSeries() {
		return campaignDiagramSeries;
	}

	public void setCampaignDiagramSeries(List<CampaignDiagramSeries> campaignDiagramSeries) {
		this.campaignDiagramSeries = campaignDiagramSeries;
	}

	@AuditedIgnore
	@Type(type = "json")
	@Column(columnDefinition = "json")
	public List<CampaignDiagramSeries> getCampaignSeriesTotal() {
		return campaignSeriesTotal;
	}

	public void setCampaignSeriesTotal(List<CampaignDiagramSeries> campaignSeriesTotal) {
		this.campaignSeriesTotal = campaignSeriesTotal;
	}

	@Column
	public boolean isPercentageDefault() {
		return percentageDefault;
	}

	public void setPercentageDefault(boolean percentageDefault) {
		this.percentageDefault = percentageDefault;
	}

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	public String getCampaignDiagramTranslations() {
		return campaignDiagramTranslations;
	}

	public void setCampaignDiagramTranslations(String campaignDiagramTranslations) {
		this.campaignDiagramTranslations = campaignDiagramTranslations;
		campaignDiagramTranslationsList = null;
	}

	@Transient
	public List<CampaignDiagramTranslations> getCampaignDiagramTranslationsList() {
		if (campaignDiagramTranslationsList == null) {
			if (StringUtils.isBlank(campaignDiagramTranslations)) {
				campaignDiagramTranslationsList = new ArrayList<>();
			} else {
				try {
					ObjectMapper mapper = new ObjectMapper();
					campaignDiagramTranslationsList =
						Arrays.asList(mapper.readValue(campaignDiagramTranslations, CampaignDiagramTranslations[].class));
				} catch (IOException e) {
					throw new ValidationRuntimeException(
						"Content of campaignDiagramTranslations could not be parsed to List<CampaignDiagramTranslations> - ID: " + getId());
				}
			}
		}
		return campaignDiagramTranslationsList;
	}

	public void setCampaignDiagramTranslationsList(List<CampaignDiagramTranslations> campaignDiagramTranslationsList) {
		this.campaignDiagramTranslationsList = campaignDiagramTranslationsList;

		if (this.campaignDiagramTranslationsList == null) {
			campaignDiagramTranslations = null;
			return;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			campaignDiagramTranslations = mapper.writeValueAsString(campaignDiagramTranslationsList);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Content of campaignDiagramTranslationsList could not be parsed to JSON String - ID: " + getId());
		}
	}
}
