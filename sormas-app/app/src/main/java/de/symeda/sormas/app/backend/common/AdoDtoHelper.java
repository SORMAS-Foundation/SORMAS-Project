package de.symeda.sormas.app.backend.common;

import android.util.Log;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public abstract class AdoDtoHelper<ADO extends AbstractDomainObject, DTO extends DataTransferObject> {

    private static final Logger logger = LoggerFactory.getLogger(AdoDtoHelper.class);

    public ADO fillOrCreateFromDto(ADO ado, DTO dto) {

        if (dto == null) {
            return null;
        }

        if (ado == null) {
            ado = create();
            ado.setCreationDate(dto.getCreationDate());
            ado.setUuid(dto.getUuid());
        }

        ado.setChangeDate(dto.getChangeDate());

        fillInnerFromDto(ado, dto);

        return ado;
    }

    public DTO adoToDto(ADO ado) {

        DTO dto = createDto();
        dto.setUuid(ado.getUuid());
        dto.setChangeDate(new Timestamp(ado.getLocalChangeDate().getTime()));
        dto.setCreationDate(new Timestamp(ado.getCreationDate().getTime()));

        fillInnerFromAdo(dto, ado);

        return dto;
    }

    protected abstract ADO create();
    protected abstract DTO createDto();

    protected abstract void fillInnerFromDto(ADO ado, DTO dto);
    protected abstract void fillInnerFromAdo(DTO dto, ADO ado);

    protected void preparePulledResult(List<DTO> result) { }

    public void pullEntities(DtoGetInterface<DTO> getInterface, final AbstractAdoDao<ADO> dao) {

        Date maxModifiedDate = dao.getLatestChangeDate();
        // server change date has higher precision
        // adding 1 is workaround to make sure we don't get entities we already know
        maxModifiedDate.setTime(maxModifiedDate.getTime()+1);

        Call<List<DTO>> dtoCall = getInterface.getAll(maxModifiedDate != null ? maxModifiedDate.getTime() : 0);
        if (dtoCall == null) {
            return;
        }

        try {
            Response<List<DTO>> response = dtoCall.execute();
            final List<DTO> result  = response.body();
            if (result != null) {
                preparePulledResult(result);
                dao.callBatchTasks(new Callable<Void>() {
                    public Void call() throws Exception {
                        boolean empty = dao.countOf() == 0;
                        for (DTO dto : result) {
                            ADO ado = empty ? null : dao.queryUuid(dto.getUuid());
                            if (ado == null || !ado.isModified()) {
                                try {
                                    // isModified check is a workarround to make sure data entered in the app is not lost (#53)
                                    // this has to be replaced with a proper merging of old and new data (#46)
                                    ado = fillOrCreateFromDto(ado, dto);
                                    dao.saveUnmodified(ado);
                                } catch (Exception exception) {
                                    logger.error(exception, "Exception thrown during pull process is ignored on a single entity base");
                                }
                            }
                        }
                        return null;
                    }
                });

                Log.d(dao.getTableName(), "Pulled: " + result.size());
            }
        } catch (IOException e) {
            Log.e(dao.getTableName(), e.toString(), e);
        }
    }

    public void pushEntities(DtoPostInterface<DTO> postInterface, final AbstractAdoDao<ADO> dao) {

        final List<ADO> modifiedAdos = dao.queryForEq(ADO.MODIFIED, true);

        List<DTO> modifiedDtos = new ArrayList<>(modifiedAdos.size());
        for (ADO ado : modifiedAdos) {
            DTO dto = adoToDto(ado);
            modifiedDtos.add(dto);
        }

        if (modifiedDtos.isEmpty()) {
            return;
        }

        Call<Integer> call = postInterface.postAll(modifiedDtos);

        try {
            final Integer result = call.execute().body();
            if (result == null || result != modifiedAdos.size()) {
                Log.e(dao.getTableName(), "PostAll result '" + result + "' does not match expected '" + modifiedAdos.size() + "'");
            } else {

                dao.callBatchTasks(new Callable<Void>() {
                    public Void call() throws Exception {
                        for (ADO ado : modifiedAdos) {
                            ado.setModified(false);
                            // TODO set change date to timestamp returned by server
                            //ado.setChangeDate(ado.getLocalChangeDate());
                            dao.update(ado);
                        }
                        return null;
                    }
                });

                Log.d(dao.getTableName(), "Pushed: " + modifiedAdos.size());
            }
        } catch (IOException e) {
            Log.e(dao.getTableName(), e.toString(), e);
        }
    }

    public interface DtoGetInterface<DTO extends DataTransferObject> {

        Call<List<DTO>> getAll(long since);
    }

    public interface DtoPostInterface<DTO extends DataTransferObject> {

        Call<Integer> postAll(List<DTO> dtos);
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
