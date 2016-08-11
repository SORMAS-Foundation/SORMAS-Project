package de.symeda.sormas.app.backend.common;

import android.util.Log;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.ReferenceDto;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public abstract class AdoDtoHelper<ADO extends AbstractDomainObject, DTO extends DataTransferObject> {

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

    public abstract ADO create();
    public abstract DTO createDto();

    public abstract void fillInnerFromDto(ADO ado, DTO dto);
    public abstract void fillInnerFromAdo(DTO dto, ADO ado);

    public void createOrUpdateMembers(ADO ado) { }

    public void pullEntities(DtoGetInterface<DTO> getInterface, final AbstractAdoDao<ADO> dao) {

        Date maxModifiedDate = dao.getLatestChangeDate();

        Call<List<DTO>> dtoCall = getInterface.getAll(maxModifiedDate != null ? maxModifiedDate.getTime() : 0);

        try {
            Response<List<DTO>> response = dtoCall.execute();
            final List<DTO> result  = response.body();
            if (result != null) {

                dao.callBatchTasks(new Callable<Void>() {
                    public Void call() throws Exception {
                        boolean empty = dao.countOf() == 0;
                        for (DTO dto : result) {
                            ADO ado = empty ? null : dao.queryUuid(dto.getUuid());
                            ado = fillOrCreateFromDto(ado, dto);
                            int result;
                            if (ado.getId() == null) {
                                result = dao.create(ado);
                            } else {
                                result = dao.update(ado);
                            }

                            if (result != 1) {
                                Log.e(dao.getTableName(), "Could not create or update entity: " + ado);
                            }

                            createOrUpdateMembers(ado);
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
                            ado.setChangeDate(ado.getLocalChangeDate());
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

    public static ReferenceDto toReferenceDto(AbstractDomainObject ado) {
        if (ado == null) {
            return null;
        }
        ReferenceDto dto = new ReferenceDto();
        dto.setChangeDate(ado.getChangeDate());
        dto.setCreationDate(ado.getCreationDate());
        dto.setUuid(ado.getUuid());
        dto.setCaption(ado.toString());
        return dto;
    }
}
