package de.symeda.sormas.app.backend.common;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.caze.CaseDtoHelper;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.rest.CaseFacadeRetro;
import de.symeda.sormas.app.rest.DtoFacadeRetro;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public abstract class AdoDtoHelper<ADO extends AbstractDomainObject, DTO extends DataTransferObject> {

    public ADO fillOrCreateFromDto(ADO ado, DTO dto) {

        if (ado == null) {
            ado = create();
            ado.setCreationDate(dto.getCreationDate());
            ado.setUuid(dto.getUuid());
        }

        ado.setChangeDate(dto.getChangeDate());

        fillInnerFromDto(ado, dto);

        return ado;
    }

    public abstract ADO create();

    public abstract void fillInnerFromDto(ADO ado, DTO dto);

    public void syncEntities(DtoFacadeRetro<DTO> facade, AbstractAdoDao<ADO> dao) {

        Date maxModifiedDate = dao.getLatestChangeDate();

        Call<List<DTO>> dtoCall = facade.getAll(maxModifiedDate != null ? maxModifiedDate.getTime() : 0);

        try {
            List<DTO> result  = dtoCall.execute().body();
            if (result != null) {
                boolean empty = dao.countOf() == 0;
                for (DTO dto : result) {
                    ADO ado = empty ? null : dao.queryUuid(dto.getUuid());
                    ado = fillOrCreateFromDto(ado, dto);
                    Dao.CreateOrUpdateStatus updateStatus = dao.createOrUpdate(ado);
                    if (updateStatus.getNumLinesChanged() != 1) {
                        Log.e(dao.getTableName(), "Could not create or update entity: " + ado);
                    }
                }
                Log.d(dao.getTableName(), "Synced: " + result.size());
            }
        } catch (IOException e) {
            Log.e(dao.getTableName(), e.toString(), e);
        }
    }
}
