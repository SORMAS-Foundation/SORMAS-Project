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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.CollectionUtils;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import android.content.Context;
import android.util.Log;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.component.dialog.SynchronizationDialog;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.ServerCommunicationException;
import de.symeda.sormas.app.rest.ServerConnectionException;
import de.symeda.sormas.app.util.DataUtils;
import retrofit2.Call;
import retrofit2.Response;

public abstract class AdoDtoHelper<ADO extends AbstractDomainObject, DTO extends EntityDto> {

	private static final Logger logger = LoggerFactory.getLogger(AdoDtoHelper.class);

	private Date lastSyncedEntityDate;
	private String lastSyncedEntityUuid;

	protected abstract Class<ADO> getAdoClass();

	protected abstract Class<DTO> getDtoClass();

	protected abstract Call<List<DTO>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException;

	/**
	 * Explicitly pull missing entities.
	 * This is needed, because entities are synced based on user access rights and these might change
	 * e.g. when the district or region of a case is changed.
	 */
	protected abstract Call<List<DTO>> pullByUuids(List<String> uuids) throws NoConnectionException;

	protected abstract Call<List<PostResponse>> pushAll(List<DTO> dtos) throws NoConnectionException;

	protected abstract void fillInnerFromDto(ADO ado, DTO dto);

	protected abstract void fillInnerFromAdo(DTO dto, ADO ado);

	protected void preparePulledResult(List<DTO> result)
		throws NoConnectionException, ServerCommunicationException, ServerConnectionException, DaoException {
	}

	/**
	 * Provides avg size of the entity DTO serialized to JSON.
	 * To define it, create a new instance of the DTO filled with some best guess values for the use case
	 * and check the length of the resulting JSON, multiplied by 8.
	 * Needed to decide how much entities to synchronize at once, given the current bandwidth.
	 */
	protected abstract long getApproximateJsonSizeInBytes();

	/**
	 * Override if access to viewing /editing is restricted
	 */
	protected UserRight getUserRightView() {
		return null;
	}

	protected UserRight getUserRightEdit() {
		return null;
	}

	public boolean pullAndPushEntities(Context context)
		throws DaoException, ServerConnectionException, ServerCommunicationException, NoConnectionException {

		return pullAndPushEntities(context, Optional.empty());
	}

	/**
	 * @return another pull needed?
	 * @param context
	 */
	public boolean pullAndPushEntities(Context context, Optional<SynchronizationDialog.SynchronizationCallbacks> syncCallbacks)
		throws DaoException, ServerConnectionException, ServerCommunicationException, NoConnectionException {

		pullEntities(false, context, syncCallbacks, false);

		return pushEntities(false, syncCallbacks, isViewAllowed());
	}

	public void pullEntities(final boolean markAsRead, Context context, Optional<SynchronizationDialog.SynchronizationCallbacks> syncCallbacks)
		throws DaoException, ServerCommunicationException, ServerConnectionException, NoConnectionException {

		pullEntities(markAsRead, context, syncCallbacks, true);
	}

	public void pullEntities(
		final boolean markAsRead,
		Context context,
		Optional<SynchronizationDialog.SynchronizationCallbacks> syncCallbacks,
		boolean callLoadNext)
		throws DaoException, ServerCommunicationException, ServerConnectionException, NoConnectionException {

		if (!isViewAllowed()) {
			return;
		}

		try {
			final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());

			Date maxModifiedDate = dao.getLatestChangeDate();
			long approximateJsonSizeInBytes = getApproximateJsonSizeInBytes();
			final int batchSize = approximateJsonSizeInBytes != 0
				? RetroProvider.getNumberOfEntitiesToBePulledInOneBatch(approximateJsonSizeInBytes, context)
				: Integer.MAX_VALUE;
			int lastBatchSize = batchSize;

			lastSyncedEntityDate = maxModifiedDate;

			while (lastBatchSize == batchSize) {
				Call<List<DTO>> dtoCall = pullAllSince(
					lastSyncedEntityDate.getTime(),
					batchSize,
					lastSyncedEntityDate != null && lastSyncedEntityUuid != null ? lastSyncedEntityUuid : EntityDto.NO_LAST_SYNCED_UUID);

				if (dtoCall == null) {
					return;
				}

				Response<List<DTO>> response;
				try {
					response = dtoCall.execute();
				} catch (IOException e) {
					throw new ServerCommunicationException(e);
				}

				lastBatchSize = handlePullResponse(markAsRead, dao, response, syncCallbacks);
			}

			if (callLoadNext) {
				syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			}
		} catch (RuntimeException e) {
			Log.e(getClass().getName(), "Exception thrown when trying to pull entities");
			throw new DaoException(e);
		}
	}

	public void repullEntities(Context context, Optional<SynchronizationDialog.SynchronizationCallbacks> syncCallbacks)
		throws DaoException, ServerCommunicationException, ServerConnectionException, NoConnectionException {

		if (!isViewAllowed()) {
			return;
		}

		try {
			final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());

			long approximateJsonSizeInBytes = getApproximateJsonSizeInBytes();
			final int batchSize = approximateJsonSizeInBytes != 0
				? RetroProvider.getNumberOfEntitiesToBePulledInOneBatch(approximateJsonSizeInBytes, context)
				: Integer.MAX_VALUE;
			int lastBatchSize = batchSize;

			while (lastBatchSize == batchSize) {
				Call<List<DTO>> dtoCall = pullAllSince(
					lastSyncedEntityDate != null ? lastSyncedEntityDate.getTime() : 0,
					batchSize,
					lastSyncedEntityDate != null && lastSyncedEntityUuid != null ? lastSyncedEntityUuid : EntityDto.NO_LAST_SYNCED_UUID);

				if (dtoCall == null) {
					return;
				}

				Response<List<DTO>> response;
				try {
					response = dtoCall.execute();
				} catch (IOException e) {
					throw new ServerCommunicationException(e);
				}

				lastBatchSize = handlePullResponse(false, dao, response, syncCallbacks);
			}

			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
		} catch (RuntimeException e) {
			Log.e(getClass().getName(), "Exception thrown when trying to pull entities");
			throw new DaoException(e);
		}
	}

	/**
	 * @return Number of pulled entities
	 */
	protected int handlePullResponse(
		final boolean markAsRead,
		final AbstractAdoDao<ADO> dao,
		Response<List<DTO>> response,
		Optional<SynchronizationDialog.SynchronizationCallbacks> syncCallbacks)
		throws ServerCommunicationException, DaoException, ServerConnectionException, NoConnectionException {

		if (!response.isSuccessful()) {
			RetroProvider.throwException(response);
		}

		final List<DTO> result = response.body();
		if (result != null && result.size() > 0) {
			return handlePulledList(dao, result, syncCallbacks);
		}
		return 0;
	}

	public int handlePulledList(AbstractAdoDao<ADO> dao, List<DTO> result, Optional<SynchronizationDialog.SynchronizationCallbacks> syncCallbacks)
		throws DaoException, NoConnectionException, ServerConnectionException, ServerCommunicationException {

		if (CollectionUtils.isEmpty(result)) {
			return 0;
		}

		preparePulledResult(result);
		dao.callBatchTasks((Callable<Void>) () -> {
// 		boolean empty = dao.countOf() == 0;
			Iterator<DTO> iterator = result.iterator();
			while (iterator.hasNext()) {
				final DTO dto = iterator.next();
				handlePulledDto(dao, dto);
				syncCallbacks.ifPresent(c -> c.getUpdatePullsCallback().accept(1));
				// TODO #704
//				if (entity != null && markAsRead) {
//					dao.markAsRead(entity);
//				}
				if (!iterator.hasNext()) {
					lastSyncedEntityUuid = dto.getUuid();
					lastSyncedEntityDate = dto.getChangeDate();
				}
			}
			executeHandlePulledListAddition(result.size());
			return null;
		});

		Log.d(dao.getTableName(), "Pulled: " + result.size());
		return result.size();
	}

	/**
	 * Calls handlePulledDto for each DTO that has been pulled from the server, and returns the
	 * number of pulled DTOs.
	 */
	public int handlePulledList(AbstractAdoDao<ADO> dao, List<DTO> result)
		throws DaoException, NoConnectionException, ServerConnectionException, ServerCommunicationException {

		return handlePulledList(dao, result, Optional.empty());
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

		return pushEntities(onlyNewEntities, Optional.empty());
	}

	public boolean pushEntities(boolean onlyNewEntities, Optional<SynchronizationDialog.SynchronizationCallbacks> syncCallbacks)
		throws DaoException, ServerConnectionException, ServerCommunicationException, NoConnectionException {

		return pushEntities(onlyNewEntities, syncCallbacks, false);
	}

	public boolean pushEntities(
		boolean onlyNewEntities,
		Optional<SynchronizationDialog.SynchronizationCallbacks> syncCallbacks,
		boolean forceLoadNext)
		throws DaoException, ServerConnectionException, ServerCommunicationException, NoConnectionException {

		if (!isEditAllowed()) {
			if (forceLoadNext) {
				syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			}
			return false;
		}

		final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());

		final List<ADO> modifiedAdos = onlyNewEntities ? dao.queryForNew() : dao.queryForModified();

		List<DTO> modifiedDtos = new ArrayList<>(modifiedAdos.size());
		for (ADO ado : modifiedAdos) {
			DTO dto = adoToDto(ado);
			modifiedDtos.add(dto);
		}

		if (modifiedDtos.isEmpty()) {
			syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
			return false;
		}

		syncCallbacks.ifPresent(c -> c.getUpdatePushTotalCallback().accept(modifiedDtos.size()));

		Call<List<PostResponse>> call = pushAll(modifiedDtos);
		Response<List<PostResponse>> response;
		try {
			response = call.execute();
		} catch (IOException e) {
			throw new ServerCommunicationException(e);
		}

		if (!response.isSuccessful()) {
			RetroProvider.throwException(response);
		}

		final List<PostResponse> pushResponses = response.body();
		if (pushResponses.size() != modifiedDtos.size()) {
			throw new ServerCommunicationException(
				"Server responded with wrong count of received entities: " + pushResponses.size() + " - expected: " + modifiedDtos.size());
		}

		pushedTooOldCount = 0;
		pushedErrorCount = 0;
		dao.callBatchTasks(new Callable<Void>() {

			public Void call() throws Exception {

				for (int i = 0; i < modifiedAdos.size(); i++) {
					ADO ado = modifiedAdos.get(i);
					PostResponse pushResponse = pushResponses.get(i);
					switch (pushResponse.getStatusCode()) {
					case 200:
					case 201:
					case 202:
					case 203:
					case 204:
					case 205:
					case 206:
					case 207:
					case 208:
						// data has been pushed, we no longer need the old unmodified version
						dao.accept(ado);
						syncCallbacks.ifPresent(c -> c.getUpdatePushesCallback().accept(1));
						break;
					case 409: // outdated entity
						pushedTooOldCount++;
						break;
					case 400: // invalid entity
					case 403: // forbidden
					case 422: // could not be processed -> any unhandled exception
					default:
						pushedErrorCount++;
						break;
					}
				}
				return null;
			}
		});

		if (modifiedAdos.size() > 0) {
			Log.d(dao.getTableName(), "Pushed: " + modifiedAdos.size() + " Too old: " + pushedTooOldCount + " Erros: " + pushedErrorCount);
		}

		syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());

		return true;
	}

	public boolean isAnyMissing(List<String> uuids) {

		final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());
		uuids = dao.filterMissing(uuids);
		return !uuids.isEmpty();
	}

	public void pullMissing(List<String> uuids) throws ServerCommunicationException, ServerConnectionException, DaoException, NoConnectionException {

		pullMissing(uuids, Optional.empty());
	}

	public void pullMissing(List<String> uuids, Optional<SynchronizationDialog.SynchronizationCallbacks> syncCallbacks)
		throws ServerCommunicationException, ServerConnectionException, DaoException, NoConnectionException {

		if (!isViewAllowed()) {
			return;
		}

		final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());
		uuids = dao.filterMissing(uuids);

		if (!uuids.isEmpty()) {
			Response<List<DTO>> response;
			try {
				response = pullByUuids(uuids).execute();
			} catch (IOException e) {
				throw new ServerCommunicationException(e);
			}

			handlePullResponse(false, dao, response, syncCallbacks);
		}

		syncCallbacks.ifPresent(c -> c.getLoadNextCallback().run());
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

	public boolean isViewAllowed() {
		try {
			return DtoUserRightsHelper.isViewAllowed(getDtoClass());
		} catch (UnsupportedOperationException e) {
			return true;
		}
	}

	public boolean isEditAllowed() {
		try {
			return DtoUserRightsHelper.isEditAllowed(getDtoClass());
		} catch (UnsupportedOperationException e) {
			return true;
		}
	}

	protected void executeHandlePulledListAddition(int resultSize) {
		// Not implemented by default
	}
}
