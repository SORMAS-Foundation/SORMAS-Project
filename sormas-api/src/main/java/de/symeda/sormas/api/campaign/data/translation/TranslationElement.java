package de.symeda.sormas.api.campaign.data.translation;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_SMALL;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.MapperUtil;
import de.symeda.sormas.api.i18n.Validations;

public class TranslationElement implements Serializable {

	@Size(max = CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String elementId;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String caption;
	private List<MapperUtil> options;

	public String getElementId() { //setOptionsListValues
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
	
	
	public List<MapperUtil> getOptions() {
		return options;
	}

	public void setOptions(List<MapperUtil> options) {
		this.options = options;
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
		return Objects.equals(elementId, that.elementId) && Objects.equals(caption, that.caption) && Objects.equals(options, that.options);
	}

	@Override
	public int hashCode() {
		return Objects.hash(elementId, caption, options);
	}
}
