package de.symeda.sormas.backend.sormas2sormas;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.opencsv.CSVReader;

import de.symeda.sormas.api.sormas2sormas.ServerAccessDataDto;
import de.symeda.sormas.api.sormas2sormas.ServerAccessDataFacade;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class ServerAccessDataFacadeEjb implements ServerAccessDataFacade {

	public static final String SERVER_LIST_FILE_NAME = "server-list.csv";

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	public List<ServerAccessDataDto> getServerAccessDataList() {
		File inputFile = Paths.get(configFacade.getSormas2sormasFilesPath(), SERVER_LIST_FILE_NAME).toFile();
		try (Reader reader = new InputStreamReader(new FileInputStream(inputFile), "UTF-8");
			CSVReader csvReader = CSVUtils.createCSVReader(reader, ',')) {
			return csvReader.readAll().stream()
					.map(this::getServerAccessDataDto)
					.collect(Collectors.toList());
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}

	private ServerAccessDataDto getServerAccessDataDto(String[] row) {
		ServerAccessDataDto dto = new ServerAccessDataDto();
		if (row[0] != null) {
			dto.setCommonName(row[0]);
		}
		if (row[1] != null) {
			dto.setHealthDepartment(row[1]);
		}
		if (row[2] != null) {
			dto.setUrl(row[2]);
		}
		return dto;
	}

	@LocalBean
	@Stateless
	public static class ServerAccessDataFacadeEjbLocal extends ServerAccessDataFacadeEjb {
	}
}
