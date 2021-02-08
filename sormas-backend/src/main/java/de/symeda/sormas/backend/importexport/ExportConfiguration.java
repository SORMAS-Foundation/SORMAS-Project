package de.symeda.sormas.backend.importexport;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

@Entity
public class ExportConfiguration extends AbstractDomainObject {

	private static final long serialVersionUID = 8901097581909494666L;

	public static final String NAME = "name";
	public static final String SHARED_TO_PUBLIC = "sharedToPublic";
	public static final String EXPORT_TYPE = "exportType";
	public static final String TARGET = "target";
	public static final String USER = "user";
	public static final String PROPERTIES_STRING = "propertiesString";
	public static final String PROPERTIES = "properties";

	private String name;
	private boolean sharedToPublic;
	private ExportType exportType;
	private User user;
	private String propertiesString;
	private Set<String> properties;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getName() {
		return name;
	}

	@Column
	public boolean isSharedToPublic() {
		return sharedToPublic;
	}

	public void setSharedToPublic(boolean sharedToPublic) {
		this.sharedToPublic = sharedToPublic;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Enumerated(EnumType.STRING)
	public ExportType getExportType() {
		return exportType;
	}

	public void setExportType(ExportType exportType) {
		this.exportType = exportType;
	}

	@ManyToOne(cascade = {})
	@JoinColumn
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPropertiesString() {
		return propertiesString;
	}

	public void setPropertiesString(String propertiesString) {
		this.propertiesString = propertiesString;
	}

	@Transient
	public Set<String> getProperties() {
		if (properties == null) {
			if (StringUtils.isEmpty(propertiesString)) {
				properties = new HashSet<>();
			} else {
				properties = Arrays.stream(propertiesString.split(",")).collect(Collectors.toSet());
			}
		}
		return properties;
	}

	public void setProperties(Set<String> properties) {
		this.properties = properties;

		if (this.properties == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		properties.stream().forEach(p -> {
			sb.append(p).append(",");
		});
		if (sb.length() > 0) {
			sb.substring(0, sb.lastIndexOf(","));
		}
		propertiesString = sb.toString();
	}

}
