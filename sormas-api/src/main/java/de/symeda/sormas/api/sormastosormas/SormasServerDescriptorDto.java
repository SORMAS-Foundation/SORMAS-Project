package de.symeda.sormas.api.sormastosormas;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.symeda.sormas.api.audit.Auditable;

import java.io.Serializable;
import java.util.Objects;

public class SormasServerDescriptorDto implements Auditable, Serializable {

	private static final long serialVersionUID = 42341345234L;

	private final String id;
	private String name;
	private String hostName;

	public SormasServerDescriptorDto(String id) {
		this.id = id;
	}

	public SormasServerDescriptorDto(String id, String name) {
		this.id = id;
		this.name = name;
	}

	@JsonCreator
	public SormasServerDescriptorDto(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("hostName") String hostName) {
		this.id = id;
		this.name = name;
		this.hostName = hostName;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getHostName() {
		return hostName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SormasServerDescriptorDto that = (SormasServerDescriptorDto) o;
		return Objects.equals(id, that.getId()) && Objects.equals(hostName, that.hostName) && Objects.equals(getName(), that.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, hostName);
	}

	@Override
	public String toString() {
		if (name != null) {
			return name;
		} else {
			return id;
		}
	}

	@Override
	public String getAuditRepresentation() {
		return String.format("%s(id=%s, name=%s, hostName=%s)", getClass().getSimpleName(), id, name, hostName);
	}
}
