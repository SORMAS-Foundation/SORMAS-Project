/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.report;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.report.WeeklyReportEntryDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 12.09.2017.
 */

public class WeeklyReportEntryDtoHelper extends AdoDtoHelper<WeeklyReportEntry, WeeklyReportEntryDto> {

	@Override
	protected Class<WeeklyReportEntry> getAdoClass() {
		return WeeklyReportEntry.class;
	}

	@Override
	protected Class<WeeklyReportEntryDto> getDtoClass() {
		return WeeklyReportEntryDto.class;
	}

	@Override
	protected Call<List<WeeklyReportEntryDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<WeeklyReportEntryDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<WeeklyReportEntryDto> weeklyReportEntryDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(WeeklyReportEntry target, WeeklyReportEntryDto source) {
		target.setDisease(source.getDisease());
		target.setNumberOfCases(source.getNumberOfCases());
	}

	@Override
	public void fillInnerFromAdo(WeeklyReportEntryDto target, WeeklyReportEntry source) {
		target.setDisease(source.getDisease());
		target.setNumberOfCases(source.getNumberOfCases());
	}
}
