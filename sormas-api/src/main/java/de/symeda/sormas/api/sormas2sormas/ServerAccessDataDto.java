package de.symeda.sormas.api.sormas2sormas;

import java.util.Objects;

public class ServerAccessDataDto {

    private String commonName;
    private String healthDepartment;
    private String url;

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getHealthDepartment() {
        return healthDepartment;
    }

    public void setHealthDepartment(String healthDepartment) {
        this.healthDepartment = healthDepartment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerAccessDataDto that = (ServerAccessDataDto) o;
        return Objects.equals(commonName, that.commonName) &&
                Objects.equals(healthDepartment, that.healthDepartment) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commonName, healthDepartment, url);
    }

    @Override
    public String toString() {
        return "ServerAccessDataDto{" +
                "commonName='" + commonName + '\'' +
                ", healthDepartment='" + healthDepartment + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
