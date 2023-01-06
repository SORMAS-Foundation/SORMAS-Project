package de.symeda.sormas.api.infrastructure.subcontinent;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Light-weight index information on sub-continents for larger queries")
public class SubcontinentIndexDto extends SubcontinentDto {

	public static final String DISPLAY_NAME = "displayName";

	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private String displayName;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
