package de.symeda.sormas.app.backend.common;

import android.util.Log;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.app.util.DataUtils;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public abstract class AdoDtoHelper<ADO extends AbstractDomainObject, DTO extends EntityDto> {

    private static final Logger logger = LoggerFactory.getLogger(AdoDtoHelper.class);

    protected abstract Class<ADO> getAdoClass();

    protected abstract Class<DTO> getDtoClass();

    protected abstract Call<List<DTO>> pullAllSince(long since);

    /**
     * Explicitly pull missing entities.
     * This is needed, because entities are synced based on user access rights and these might change
     * e.g. when the district or region of a case is changed.
     */
    protected abstract Call<List<DTO>> pullByUuids(List<String> uuids);

    protected abstract Call<Integer> pushAll(List<DTO> dtos);

    protected abstract void fillInnerFromDto(ADO ado, DTO dto);

    protected abstract void fillInnerFromAdo(DTO dto, ADO ado);

    protected void preparePulledResult(List<DTO> result) {
    }

    /**
     * @return another pull needed?
     */
    public boolean pullAndPushEntities()
            throws DaoException, ServerConnectionException, SynchronizationException {

        pullEntities(false);

        return pushEntities();
    }

    public void pullEntities(final boolean markAsRead) throws DaoException, ServerConnectionException {
        try {
            final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());

            Date maxModifiedDate = dao.getLatestChangeDate();
            Call<List<DTO>> dtoCall = pullAllSince(maxModifiedDate != null ? maxModifiedDate.getTime() + 1 : 0);
            if (dtoCall == null) {
                return;
            }

            Response<List<DTO>> response;
            try {
                response = dtoCall.execute();
            } catch (IOException e) {
                throw new ServerConnectionException(e);
            }

            handlePullResponse(markAsRead, dao, response);

        } catch (RuntimeException e) {
            Log.e(getClass().getName(), "Exception thrown when trying to pull entities");
            throw new DaoException(e);
        }
    }

    public void repullEntities() throws DaoException, ServerConnectionException {
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
                throw new ServerConnectionException(e);
            }

            handlePullResponse(false, dao, response);

        } catch (RuntimeException e) {
            Log.e(getClass().getName(), "Exception thrown when trying to pull entities");
            throw new DaoException(e);
        }
    }

    /**
     * @return Number or pulled entities
     */
    protected int handlePullResponse(final boolean markAsRead, final AbstractAdoDao<ADO> dao, Response<List<DTO>> response) throws ServerConnectionException {
        if (!response.isSuccessful()) {
            throw ServerConnectionException.fromResponse(response);
        }

        final List<DTO> result = response.body();
        if (result != null && result.size() > 0) {
            preparePulledResult(result);
            dao.callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    boolean empty = dao.countOf() == 0;
                    for (DTO dto : result) {
                        ADO source = fillOrCreateFromDto(null, dto);
                        source = dao.mergeOrCreate(source);
                        if (markAsRead) {
                            dao.markAsRead(source);
                        }
                    }
                    return null;
                }
            });

            Log.d(dao.getTableName(), "Pulled: " + result.size());
            return result.size();
        }
        return 0;
    }

    /**
     * @return true: another pull is needed, because data has been changed on the server
     */
    public boolean pushEntities() throws DaoException, ServerConnectionException, SynchronizationException {
        try {
            final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());

            final List<ADO> modifiedAdos = dao.queryForEq(ADO.MODIFIED, true);

            List<DTO> modifiedDtos = new ArrayList<>(modifiedAdos.size());
            for (ADO ado : modifiedAdos) {
                DTO dto = adoToDto(ado);
                modifiedDtos.add(dto);
            }

            if (modifiedDtos.isEmpty()) {
                return false;
            }

            Call<Integer> call = pushAll(modifiedDtos);
            Response<Integer> response;
            try {
                response = call.execute();
            } catch (IOException e) {
                throw new ServerConnectionException(e);
            }

            if (!response.isSuccessful()) {
                String responseErrorBodyString;
                try {
                    responseErrorBodyString = response.errorBody().string();
                } catch (IOException e) {
                    responseErrorBodyString = "Exception accessing error body: " + e.getMessage();
                }
                throw new SynchronizationException("Pushing changes to server did not work: " + responseErrorBodyString);
            } else if (response.body() != modifiedDtos.size()) {
                throw new SynchronizationException("Server responded with wrong count of changed entities: " + response.body() + " - expected: " + modifiedDtos.size());
            }

            dao.callBatchTasks(new Callable<Void>() {
                public Void call() throws Exception {
                    for (ADO ado : modifiedAdos) {
                        // data has been pushed, we no longer need the old unmodified version
                        dao.accept(ado);
                    }
                    return null;
                }
            });

            if (modifiedAdos.size() > 0) {
                Log.d(dao.getTableName(), "Pushed: " + modifiedAdos.size());
            }

            return true;
        } catch (RuntimeException e) {
            Log.e(getClass().getName(), "Exception thrown when trying to push entities");
            throw new DaoException(e);
        }
    }



    public void pullMissing(List<String> uuids) throws ServerConnectionException {

        final AbstractAdoDao<ADO> dao = DatabaseHelper.getAdoDao(getAdoClass());
        uuids = dao.filterMissing(uuids);

        if (!uuids.isEmpty()) {
            Response<List<DTO>> response;
            try {
                response = pullByUuids(uuids).execute();
            } catch (IOException e) {
                throw new ServerConnectionException(e);
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
