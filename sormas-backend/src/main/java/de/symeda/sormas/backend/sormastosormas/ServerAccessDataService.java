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

package de.symeda.sormas.backend.sormastosormas;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.opencsv.CSVReader;

import de.symeda.sormas.api.SormasToSormasConfig;
import de.symeda.sormas.api.utils.CSVUtils;

@Stateless
@LocalBean
public class ServerAccessDataService {

	private static final String SERVER_ACCESS_DATA_FILE_NAME = "server-access-data.csv";

	private static final String SERVER_LIST_FILE_NAME = "server-list.csv";

	@Inject
	private SormasToSormasConfig sormasToSormasConfig;

	public Optional<ServerAccessData> getServerAccessData() {
		Path inputFile = Paths.get(sormasToSormasConfig.getFilePath(), SERVER_ACCESS_DATA_FILE_NAME);
		try (Reader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
			CSVReader csvReader = CSVUtils.createCSVReader(reader, ',')) {
			return Optional.of(buildServerAccessData(csvReader.readNext()));

		} catch (IOException e) {
			return Optional.empty();
		}
	}

	public List<ServerAccessListItem> getServerList() {
		Path inputFile = Paths.get(sormasToSormasConfig.getFilePath(), SERVER_LIST_FILE_NAME);
		try (Reader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
			CSVReader csvReader = CSVUtils.createCSVReader(reader, ',')) {
			return csvReader.readAll()
				.stream()
				// skip the empty line at the end
				.filter(r -> r.length > 1)
				// parse non-empty lines
				.map(this::buildServerAccessListItem)
				.collect(Collectors.toList());
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}

	public Optional<ServerAccessListItem> getServerListItemByCommonName(String commonName) {
		return getServerList().stream().filter(i -> i.getHealthDepartmentId().equals(commonName)).findFirst();
	}

	private ServerAccessData buildServerAccessData(String[] csvRow) {
		ServerAccessData data = new ServerAccessData();

		if (csvRow[0] != null) {
			data.setHealthDepartmentId(csvRow[0]);
		}
		if (csvRow[1] != null) {
			data.setHealthDepartmentName(csvRow[1]);
		}
		if (csvRow[2] != null) {
			data.setRestUserPassword(csvRow[2]);
		}

		return data;
	}

	private ServerAccessListItem buildServerAccessListItem(String[] row) {
		ServerAccessListItem dto = new ServerAccessListItem();
		if (row[0] != null) {
			dto.setHealthDepartmentId(row[0]);
		}
		if (row[1] != null) {
			dto.setHealthDepartmentName(row[1]);
		}
		if (row[2] != null) {
			dto.setUrl(row[2]);
		}
		if (row[3] != null) {
			dto.setRestUserPassword(row[3]);
		}

		return dto;
	}
}
