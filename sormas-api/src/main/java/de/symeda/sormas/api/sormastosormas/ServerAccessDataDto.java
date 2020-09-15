/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.sormastosormas;

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
