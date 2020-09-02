package de.symeda.sormas.api.importexport;

import java.util.Set;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ExportConfigurationDto extends EntityDto {

	private static final long serialVersionUID = 215274108066954814L;

	public static final String I18N_PREFIX = "ExportConfiguration";

	public static final String NAME = "name";

	private String name;
	private ExportType exportType;
	private UserReferenceDto user;
	private Set<String> properties;

	public static ExportConfigurationDto build(UserReferenceDto user) {

		ExportConfigurationDto config = new ExportConfigurationDto();
		config.setUuid(DataHelper.createUuid());
		config.setUser(user);
		return config;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExportType getExportType() {
		return exportType;
	}

	public void setExportType(ExportType exportType) {
		this.exportType = exportType;
	}

	public UserReferenceDto getUser() {
		return user;
	}

	public void setUser(UserReferenceDto user) {
		this.user = user;
	}

	public Set<String> getProperties() {
		return properties;
	}

	public void setProperties(Set<String> properties) {
		this.properties = properties;
	}
}
