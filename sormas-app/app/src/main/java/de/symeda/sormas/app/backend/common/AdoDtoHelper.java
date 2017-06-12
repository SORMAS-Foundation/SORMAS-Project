package de.symeda.sormas.app.backend.common;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.app.util.DataUtils;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public abstract class AdoDtoHelper<ADO extends AbstractDomainObject, DTO extends DataTransferObject> {

    private static final Logger logger = LoggerFactory.getLogger(AdoDtoHelper.class);

    protected abstract Class<ADO> getAdoClass();
    protected abstract Class<DTO> getDtoClass();

    protected abstract void fillInnerFromDto(ADO ado, DTO dto);
    protected abstract void fillInnerFromAdo(DTO dto, ADO ado);

    protected void preparePulledResult(List<DTO> result) { }

    public void pullEntities(DtoGetInterface<DTO> getInterface, final AbstractAdoDao<ADO> dao, final Context context) throws DaoException, SQLException, IOException {
        try {
            Date maxModifiedDate = dao.getLatestChangeDate();
            // server change date has higher precision
            // adding 1 is workaround to make sure we don't get entities we already know
            maxModifiedDate.setTime(maxModifiedDate.getTime() + 1);

            Call<List<DTO>> dtoCall = getInterface.getAll(maxModifiedDate != null ? maxModifiedDate.getTime() : 0);
            if (dtoCall == null) {
                return;
            }

            Response<List<DTO>> response = dtoCall.execute();
            final List<DTO> result = response.body();
            if (result != null && result.size() > 0) {
                preparePulledResult(result);
                dao.callBatchTasks(new Callable<Void>() {
                    public Void call() throws Exception {
                        boolean empty = dao.countOf() == 0;
                        for (DTO dto : result) {

                            ADO source = fillOrCreateFromDto(null, dto);
                            dao.mergeOrCreate(source);

//                            ADO ado = empty ? null : dao.queryUuid(dto.getUuid());
//
//                            // merge or just saveAndSnapshot?
//                            if (ado != null && ado.isModified()) {
//                                // merge existing changes into incoming data
//                                ADO original = dao.querySnapshotByUuid(dto.getUuid());
//                                AdoMergeHelper.mergeAdo(ado, original, source);
//                                dao.saveAndSnapshot(ado);
//                                dao.saveUnmodified(original);
//
//                                // in theory ado and cloned original could now be equal
//                                // and we no longer need to keep the copy. Ignore this
//
//                            } else {
//                                ado = fillOrCreateFromDto(ado, dto);
//                                dao.saveUnmodified(ado);
//                            }
                        }
                        return null;
                    }
                });

                Log.d(dao.getTableName(), "Pulled: " + result.size());
            }
        } catch (RuntimeException e) {
            Log.e(getClass().getName(), "Exception thrown when trying to pull entities");
            throw new DaoException(e);
        }
    }

    /**
     * @return true: another pull is needed, because data has been changed on the server
     */
    public boolean pushEntities(DtoPostInterface<DTO> postInterface, final AbstractAdoDao<ADO> dao) throws DaoException, IOException {
        try {
            final List<ADO> modifiedAdos = dao.queryForEq(ADO.MODIFIED, true);

            List<DTO> modifiedDtos = new ArrayList<>(modifiedAdos.size());
            for (ADO ado : modifiedAdos) {
                DTO dto = adoToDto(ado);
                modifiedDtos.add(dto);
            }

            if (modifiedDtos.isEmpty()) {
                return false;
            }

            Call<Long> call = postInterface.postAll(modifiedDtos);

            final Long resultChangeDate = call.execute().body();
            if (resultChangeDate == null) {
                throw new ConnectException("PostAll did not work");
            } else {
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
            }

            // do we need to pull again
            return resultChangeDate == null || resultChangeDate == -1;
        } catch (RuntimeException e) {
            Log.e(getClass().getName(), "Exception thrown when trying to push entities");
            throw new DaoException(e);
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
            }
            else if (!ado.getUuid().equals(dto.getUuid())) {
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

    public interface DtoGetInterface<DTO extends DataTransferObject> {
        Call<List<DTO>> getAll(long since);
    }

    public interface DtoPostInterface<DTO extends DataTransferObject> {
        Call<Long> postAll(List<DTO> dtos);
    }

    public static void fillDto(DataTransferObject dto, AbstractDomainObject ado) {
        dto.setChangeDate(ado.getChangeDate());
        dto.setCreationDate(ado.getCreationDate());
        dto.setUuid(ado.getUuid());
    }

    public static void fillReferenceDto(ReferenceDto dto, AbstractDomainObject entity) {
        fillDto(dto, entity);
        dto.setCaption(entity.toString());
    }
}
