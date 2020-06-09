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

package de.symeda.sormas.app.backend.common;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import android.util.Log;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.ServerCommunicationException;
import de.symeda.sormas.app.rest.ServerConnectionException;
import de.symeda.sormas.app.util.DataUtils;
import retrofit2.Call;
import retrofit2.Response;

public abstract class AdoDtoHelper<ADO extends AbstractDomainObject, DTO extends EntityDto> {

	private static final Logger logger = LoggerFactory.getLogger(AdoDtoHelper.class);

	protected abstract Class<ADO> getAdoClass();

	protected abstract Class<DTO> getDtoClass();

	protected abstract Call<List<DTO>> pullAllSince(long since) throws NoConnectionException;

	/**
	 * Explicitly pull missing entities.
	 * This is needed, because entities are synced based on user access rights and these might change
	 * e.g. when the district or region of a case is changed.
	 */
	protected abstract Call<List<DTO>> pullByUuids(List<String> uuids) throws NoConnectionException;

	protected abstract Call<List<PushResult>> pushAll(List<DTO> dtos) throws NoConnectionException;

	protected abstract void fillInnerFromDto(ADO ado, DTO dto);

	protected abstract void fillInnerFromAdo(DTO dto, ADO ado);

	protected void preparePulledResult(List<DTO> result) {
	}

	/**
	 * @return another pull needed?
	 */
	public boolean pullAndPushEntities() throws DaoException, ServerConnectionException, ServerCommunicationException, NoConnectionException {

		pullEntities(false);

		return pushEntities(false);
	}

	public void pullEntities(final boolean markAsRead)
		throws DaoException, ServerCommunicationException, ServerConnectionException, NoConnectionException {
		try {
			final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());

			Date maxModifiedDate = dao.getLatestChangeDate();
			Call<List<DTO>> dtoCall = pullAllSince(maxModifiedDate != null ? maxModifiedDate.getTime() : 0);
			if (dtoCall == null) {
				return;
			}

			Response<List<DTO>> response;
			try {
				response = dtoCall.execute();
			} catch (IOException e) {
				throw new ServerCommunicationException(e);
			}

			handlePullResponse(markAsRead, dao, response);

		} catch (RuntimeException e) {
			Log.e(getClass().getName(), "Exception thrown when trying to pull entities");
			throw new DaoException(e);
		}
	}

	public void repullEntities() throws DaoException, ServerCommunicationException, ServerConnectionException, NoConnectionException {
		try {
			final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());

			Call<List<DTO>> dtoCall = pullAllSince(0);
			if (dtoCall == null) {
				return;
			}

			Response<List<DTO>> response;
			try {
				response = dtoCall.execute();
			} catch (IOException e) {
				throw new ServerCommunicationException(e);
			}

			handlePullResponse(false, dao, response);

		} catch (RuntimeException e) {
			Log.e(getClass().getName(), "Exception thrown when trying to pull entities");
			throw new DaoException(e);
		}
	}

	/**
	 * @return Number of pulled entities
	 */
	protected int handlePullResponse(final boolean markAsRead, final AbstractAdoDao<ADO> dao, Response<List<DTO>> response)
		throws ServerCommunicationException, DaoException, ServerConnectionException {
		if (!response.isSuccessful()) {
			RetroProvider.throwException(response);
		}

		final List<DTO> result = response.body();
		if (result != null && result.size() > 0) {
			return handlePulledList(dao, result);
		}
		return 0;
	}

	public int handlePulledList(AbstractAdoDao<ADO> dao, List<DTO> result) throws DaoException {
		preparePulledResult(result);
		dao.callBatchTasks((Callable<Void>) () -> {
//            boolean empty = dao.countOf() == 0;
			for (DTO dto : result) {
				handlePulledDto(dao, dto);
				// TODO #704
//                        if (entity != null && markAsRead) {
//                            dao.markAsRead(entity);
//                        }
			}
			return null;
		});

		Log.d(dao.getTableName(), "Pulled: " + result.size());
		return result.size();
	}

	/**
	 * @return The resulting entity. May be null!
	 */
	protected ADO handlePulledDto(AbstractAdoDao<ADO> dao, DTO dto) throws DaoException, SQLException {
		ADO source = fillOrCreateFromDto(null, dto);
		return dao.mergeOrCreate(source);
	}

	private int pushedTooOldCount, pushedErrorCount;

	/**
	 * @return true: another pull is needed, because data has been changed on the server
	 */
	public boolean pushEntities(boolean onlyNewEntities)
		throws DaoException, ServerConnectionException, ServerCommunicationException, NoConnectionException {
		final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());

		final List<ADO> modifiedAdos = onlyNewEntities ? dao.queryForNull(ADO.CHANGE_DATE) : dao.queryForEq(ADO.MODIFIED, true);

		List<DTO> modifiedDtos = new ArrayList<>(modifiedAdos.size());
		for (ADO ado : modifiedAdos) {
			DTO dto = adoToDto(ado);
			modifiedDtos.add(dto);
		}

		if (modifiedDtos.isEmpty()) {
			return false;
		}

		Call<List<PushResult>> call = pushAll(modifiedDtos);
		Response<List<PushResult>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			throw new ServerCommunicationException(e);
		}

		if (!response.isSuccessful()) {
			RetroProvider.throwException(response);
		}

		final List<PushResult> pushResults = response.body();
		if (pushResults.size() != modifiedDtos.size()) {
			throw new ServerCommunicationException(
				"Server responded with wrong count of received entities: " + pushResults.size() + " - expected: " + modifiedDtos.size());
		}

		pushedTooOldCount = 0;
		pushedErrorCount = 0;
		dao.callBatchTasks(new Callable<Void>() {

			public Void call() throws Exception {
				for (int i = 0; i < modifiedAdos.size(); i++) {
					ADO ado = modifiedAdos.get(i);
					PushResult pushResult = pushResults.get(i);
					switch (pushResult) {
					case OK:
						// data has been pushed, we no longer need the old unmodified version
						dao.accept(ado);
						break;
					case TOO_OLD:
						pushedTooOldCount++;
						break;
					case ERROR:
						pushedErrorCount++;
						break;
					default:
						throw new IllegalArgumentException(pushResult.toString());
					}
				}
				return null;
			}
		});

		if (modifiedAdos.size() > 0) {
			Log.d(dao.getTableName(), "Pushed: " + modifiedAdos.size() + " Too old: " + pushedTooOldCount + " Erros: " + pushedErrorCount);
		}

		return true;
	}

	public boolean isAnyMissing(List<String> uuids) {

		final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());
		uuids = dao.filterMissing(uuids);
		return !uuids.isEmpty();
	}

	public void pullMissing(List<String> uuids) throws ServerCommunicationException, ServerConnectionException, DaoException, NoConnectionException {

		final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());
		uuids = dao.filterMissing(uuids);

		if (!uuids.isEmpty()) {
			Response<List<DTO>> response;
			try {
				response = pullByUuids(uuids).execute();
			} catch (IOException e) {
				throw new ServerCommunicationException(e);
			}

			handlePullResponse(false, dao, response);
		}
	}

	public ADO fillOrCreateFromDto(ADO ado, DTO dto) {
		if (dto == null) {
			return null;
		}

		try {
			if (ado == null) {
				ado = getAdoClass().newInstance();
				ado.setCreationDate(dto.getCreationDate());
				ado.setUuid(dto.getUuid());
			} else if (!ado.getUuid().equals(dto.getUuid())) {
				throw new RuntimeException("Existing object uuid does not match dto: " + ado.getUuid() + " vs. " + dto.getUuid());
			}

			ado.setChangeDate(dto.getChangeDate());

			fillInnerFromDto(ado, dto);

			return ado;
		} catch (InstantiationException e) {
			Log.e(DataUtils.class.getName(), "Could not perform fillOrCreateFromDto", e);
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			Log.e(DataUtils.class.getName(), "Could not perform fillOrCreateFromDto", e);
			throw new RuntimeException(e);
		}
	}

	public DTO adoToDto(ADO ado) {
		try {
			DTO dto = getDtoClass().newInstance();
			dto.setUuid(ado.getUuid());
			dto.setChangeDate(new Timestamp(ado.getChangeDate().getTime()));
			dto.setCreationDate(new Timestamp(ado.getCreationDate().getTime()));
			fillInnerFromAdo(dto, ado);
			return dto;
		} catch (InstantiationException e) {
			Log.e(DataUtils.class.getName(), "Could not perform createNew", e);
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			Log.e(DataUtils.class.getName(), "Could not perform createNew", e);
			throw new RuntimeException(e);
		}
	}

	public static void fillDto(EntityDto dto, AbstractDomainObject ado) {
		dto.setChangeDate(ado.getChangeDate());
		dto.setCreationDate(ado.getCreationDate());
		dto.setUuid(ado.getUuid());
	}
}
