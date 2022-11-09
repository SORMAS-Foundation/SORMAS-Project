package de.symeda.sormas.api.campaign.diagram;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

public class CampaignDiagramDefinitionDto extends EntityDto {

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diagramId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String diagramCaption;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@NotNull
	private String formType;
	private DiagramType diagramType;
	@Valid
	private List<CampaignDiagramSeries> campaignDiagramSeries;
	@Valid
	private List<CampaignDiagramSeries> campaignSeriesTotal;
	private boolean percentageDefault;
	private List<CampaignDiagramTranslations> campaignDiagramTranslations;
	

	public static CampaignDiagramDefinitionDto build() {
		CampaignDiagramDefinitionDto campaignDiagramDefinition = new CampaignDiagramDefinitionDto();
		campaignDiagramDefinition.setUuid(DataHelper.createUuid());
		return campaignDiagramDefinition;
	}

	public String getDiagramId() {
		return diagramId;
	}

	public void setDiagramId(String diagramId) {
		this.diagramId = diagramId;
	}

	public String getDiagramCaption() {
		return diagramCaption;
	}

	public void setDiagramCaption(String diagramCaption) {
		this.diagramCaption = diagramCaption;
	}

	public DiagramType getDiagramType() {
		return diagramType;
	}

	public void setDiagramType(DiagramType diagramType) {
		this.diagramType = diagramType;
	}

	public List<CampaignDiagramSeries> getCampaignDiagramSeries() {
		return campaignDiagramSeries;
	}

	public void setCampaignDiagramSeries(List<CampaignDiagramSeries> campaignDiagramSeries) {
		this.campaignDiagramSeries = campaignDiagramSeries;
	}

	public List<CampaignDiagramSeries> getCampaignSeriesTotal() {
		return campaignSeriesTotal;
	}

	public void setCampaignSeriesTotal(List<CampaignDiagramSeries> campaignSeriesTotal) {
		this.campaignSeriesTotal = campaignSeriesTotal;
	}

	public boolean isPercentageDefault() {
		return percentageDefault;
	}

	public void setPercentageDefault(boolean percentageDefault) {
		this.percentageDefault = percentageDefault;
	}

	public List<CampaignDiagramTranslations> getCampaignDiagramTranslations() {
		return campaignDiagramTranslations;
	}

	public void setCampaignDiagramTranslations(List<CampaignDiagramTranslations> campaignDiagramTranslations) {
		this.campaignDiagramTranslations = campaignDiagramTranslations;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}
	
	
}
