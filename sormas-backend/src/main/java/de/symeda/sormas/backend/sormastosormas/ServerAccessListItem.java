package de.symeda.sormas.backend.sormastosormas;

import java.util.Objects;

import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;

public class ServerAccessListItem {

	private String healthDepartmentId;
	private String healthDepartmentName;
	private String url;
	private String restUserPassword;

	public ServerAccessListItem() {
	}

	public ServerAccessListItem(String healthDepartmentId, String healthDepartmentName, String url) {
		this.healthDepartmentId = healthDepartmentId;
		this.healthDepartmentName = healthDepartmentName;
		this.url = url;
	}

	public String getHealthDepartmentId() {
		return healthDepartmentId;
	}

	public void setHealthDepartmentId(String healthDepartmentId) {
		this.healthDepartmentId = healthDepartmentId;
	}

	public String getHealthDepartmentName() {
		return healthDepartmentName;
	}

	public void setHealthDepartmentName(String healthDepartmentName) {
		this.healthDepartmentName = healthDepartmentName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRestUserPassword() {
		return restUserPassword;
	}

	public void setRestUserPassword(String restUserPassword) {
		this.restUserPassword = restUserPassword;
	}

	public ServerAccessDataReferenceDto toReference() {
		return new ServerAccessDataReferenceDto(healthDepartmentId, healthDepartmentName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ServerAccessListItem that = (ServerAccessListItem) o;
		return Objects.equals(healthDepartmentId, that.healthDepartmentId)
			&& Objects.equals(healthDepartmentName, that.healthDepartmentName)
			&& Objects.equals(url, that.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(healthDepartmentId, healthDepartmentName, url);
	}

	@Override
	public String toString() {
		return "ServerAccessDataDto{" + "commonName='" + healthDepartmentId + '\'' + ", healthDepartment='" + healthDepartmentName + '\'' + ", url='"
			+ url + '\'' + '}';
	}
}
