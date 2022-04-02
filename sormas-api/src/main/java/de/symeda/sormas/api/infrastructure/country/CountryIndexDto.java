package de.symeda.sormas.api.infrastructure.country;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;

public class CountryIndexDto extends EntityDto {

	private static final long serialVersionUID = 8309822957203823162L;

	public static final String I18N_PREFIX = "Country";
	public static final String DEFAULT_NAME = "defaultName";
	public static final String DISPLAY_NAME = "displayName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String ISO_CODE = "isoCode";
	public static final String UNO_CODE = "unoCode";
	public static final String SUBCONTINENT = "subcontinent";

	private String defaultName;
	private String displayName;
	private Long externalId;
	private String isoCode;
	private String unoCode;
	private boolean archived;
	private SubcontinentReferenceDto subcontinent;

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getUnoCode() {
		return unoCode;
	}

	public void setUnoCode(String unoCode) {
		this.unoCode = unoCode;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public SubcontinentReferenceDto getSubcontinent() {
		return subcontinent;
	}

	public void setSubcontinent(SubcontinentReferenceDto subcontinent) {
		this.subcontinent = subcontinent;
	}
}
