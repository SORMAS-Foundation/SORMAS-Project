package de.symeda.sormas.api.campaign.data.translation;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_SMALL;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;

public class TranslationElement implements Serializable {

	@Size(max = COLUMN_LENGTH_SMALL, message = Validations.textTooLong)
	private String elementId;
	@Size(max = COLUMN_LENGTH_DEFAULT, message = Validations.textTooLong)
	private String caption;

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
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
		TranslationElement that = (TranslationElement) o;
		return Objects.equals(elementId, that.elementId) && Objects.equals(caption, that.caption);
	}

	@Override
	public int hashCode() {
		return Objects.hash(elementId, caption);
	}
}
