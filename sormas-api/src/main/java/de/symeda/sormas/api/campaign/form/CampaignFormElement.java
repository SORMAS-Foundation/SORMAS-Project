package de.symeda.sormas.api.campaign.form;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class CampaignFormElement implements Serializable {

	public static final String ID = "id";
	public static final String TYPE = "type";
	public static final String CAPTION = "caption";

	private static final long serialVersionUID = 5553496750859734167L;

	public static final String[] VALID_TYPES = {
		CampaignFormElementType.LABEL.toString(),
		CampaignFormElementType.SECTION.toString(),
		CampaignFormElementType.NUMBER.toString(),
		CampaignFormElementType.TEXT.toString(),
		CampaignFormElementType.YES_NO.toString() };

	public static final String[] VALID_STYLES = {
		CampaignFormElementStyle.INLINE.toString(),
		CampaignFormElementStyle.ROW.toString(),
		CampaignFormElementStyle.FIRST.toString(),
		CampaignFormElementStyle.COL_1.toString(),
		CampaignFormElementStyle.COL_2.toString(),
		CampaignFormElementStyle.COL_3.toString(),
		CampaignFormElementStyle.COL_4.toString(),
		CampaignFormElementStyle.COL_5.toString(),
		CampaignFormElementStyle.COL_6.toString(),
		CampaignFormElementStyle.COL_7.toString(),
		CampaignFormElementStyle.COL_8.toString(),
		CampaignFormElementStyle.COL_9.toString(),
		CampaignFormElementStyle.COL_10.toString(),
		CampaignFormElementStyle.COL_11.toString(),
		CampaignFormElementStyle.COL_12.toString() };

	public static final String[] ALLOWED_HTML_TAGS = {
		"br",
		"p",
		"b",
		"i",
		"u",
		"h1",
		"h2",
		"h3",
		"h4",
		"h5",
		"h6" };

	private String type;
	private String id;
	private String caption;
	private String[] styles;
	private String dependingOn;
	private String[] dependingOnValues;
	private boolean important;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String[] getStyles() {
		return styles;
	}

	public void setStyles(String[] styles) {
		this.styles = styles;
	}

	public String getDependingOn() {
		return dependingOn;
	}

	public void setDependingOn(String dependingOn) {
		this.dependingOn = dependingOn;
	}

	public String[] getDependingOnValues() {
		return dependingOnValues;
	}

	public void setDependingOnValues(String[] dependingOnValues) {
		this.dependingOnValues = dependingOnValues;
	}

	public boolean isImportant() {
		return important;
	}

	public void setImportant(boolean important) {
		this.important = important;
	}

	/**
	 * Needed. Otherwise hibernate will persist whenever loading,
	 * because hibernate types creates new instances that aren't equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CampaignFormElement that = (CampaignFormElement) o;
		return important == that.important &&
				Objects.equals(type, that.type) &&
				Objects.equals(id, that.id) &&
				Objects.equals(caption, that.caption) &&
				Arrays.equals(styles, that.styles) &&
				Objects.equals(dependingOn, that.dependingOn) &&
				Arrays.equals(dependingOnValues, that.dependingOnValues);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(type, id, caption, dependingOn, important);
		result = 31 * result + Arrays.hashCode(styles);
		result = 31 * result + Arrays.hashCode(dependingOnValues);
		return result;
	}
}
