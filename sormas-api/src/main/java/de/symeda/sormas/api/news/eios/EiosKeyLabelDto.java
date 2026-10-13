package de.symeda.sormas.api.news.eios;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Key/label pair the EIOS API uses for coded values such as the language or country of an article source.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EiosKeyLabelDto {

	private String key;
	private String label;

	/**
	 * Older EIOS responses send the plain label instead of a key/label object.
	 */
	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static EiosKeyLabelDto fromLabel(String label) {
		EiosKeyLabelDto dto = new EiosKeyLabelDto();
		dto.setLabel(label);
		return dto;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
