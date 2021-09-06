package de.symeda.sormas.api.campaign.diagram;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_SMALL;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.campaign.data.translation.TranslationElement;
import de.symeda.sormas.api.i18n.Validations;

public class CampaignDiagramTranslations implements Serializable {

	@Size(max = COLUMN_LENGTH_SMALL, message = Validations.textTooLong)
	private String languageCode;
	@Size(max = COLUMN_LENGTH_DEFAULT, message = Validations.textTooLong)
	private String diagramCaption;
	@Valid
	private List<TranslationElement> stackCaptions;
	@Valid
	private List<TranslationElement> seriesNames;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getDiagramCaption() {
		return diagramCaption;
	}

	public void setDiagramCaption(String diagramCaption) {
		this.diagramCaption = diagramCaption;
	}

	public List<TranslationElement> getStackCaptions() {
		return stackCaptions;
	}

	public void setStackCaptions(List<TranslationElement> stackCaptions) {
		this.stackCaptions = stackCaptions;
	}

	public List<TranslationElement> getSeriesNames() {
		return seriesNames;
	}

	public void setSeriesNames(List<TranslationElement> seriesNames) {
		this.seriesNames = seriesNames;
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
		CampaignDiagramTranslations that = (CampaignDiagramTranslations) o;
		return Objects.equals(languageCode, that.languageCode)
			&& Objects.equals(diagramCaption, that.diagramCaption)
			&& Objects.equals(stackCaptions, that.stackCaptions)
			&& Objects.equals(seriesNames, that.seriesNames);
	}

	@Override
	public int hashCode() {
		return Objects.hash(languageCode, diagramCaption, stackCaptions, seriesNames);
	}
}
