package de.symeda.sormas.api.infrastructure.country;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Light-weight index information on contries for larger queries")
public class CountryIndexDto extends EntityDto {

	private static final long serialVersionUID = 8309822957203823162L;

	public static final String I18N_PREFIX = "Country";
	public static final String DEFAULT_NAME = "defaultName";
	public static final String DISPLAY_NAME = "displayName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String ISO_CODE = "isoCode";
	public static final String UNO_CODE = "unoCode";
	public static final String SUBCONTINENT = "subcontinent";

	@Schema(description = "Country's default name")
	private String defaultName;
	@Schema(description = "Country's display name")
	private String displayName;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private String externalId;
	@Schema(description = "Country's ISO 3166 code")
	private String isoCode;
	@Schema(description = "Country's United Nations Code for Trade and Transport Locations")
	private String unoCode;
	@Schema(description = "Indicates whether this object has been archived")
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

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
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

	@Override
	public String buildCaption() {
		return getDefaultName();
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}
}
