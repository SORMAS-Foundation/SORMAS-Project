package de.symeda.sormas.app.backend.common;

import android.util.Log;

import com.googlecode.openbeans.BeanInfo;
import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
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

    public void pullEntities(DtoGetInterface<DTO> getInterface, final AbstractAdoDao<ADO> dao) throws DaoException, SQLException, IOException {
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
            if (result != null) {
                preparePulledResult(result);
                dao.callBatchTasks(new Callable<Void>() {
                    public Void call() throws Exception {
                        boolean empty = dao.countOf() == 0;
                        for (DTO dto : result) {

                            ADO ado = empty ? null : dao.queryUuid(dto.getUuid());

                            if (ado != null && ado.isModified()) {
                                // merge existing changes into incoming data
                                ADO original = dao.queryClonedOriginalUuid(dto.getUuid());
                                ADO source = fillOrCreateFromDto(null, dto);
                                mergeData(ado, original, source);
                                dao.save(ado);
                                dao.saveUnmodified(original);

                                // in theory ado and cloned original could now be equal
                                // and we no longer need to keep the copy. Ignore this

                            } else {
                                ado = fillOrCreateFromDto(ado, dto);
                                dao.saveUnmodified(ado);
                            }
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
                // TODO throw exception
                Log.e(dao.getTableName(), "PostAll did not work");
            } else {
                dao.callBatchTasks(new Callable<Void>() {
                    public Void call() throws Exception {
                        for (ADO ado : modifiedAdos) {
                            // data has been pushed, we no longer need the old unmodified version
                            ADO unmodifiedAdo = dao.queryClonedOriginalUuid(ado.getUuid());
                            dao.delete(unmodifiedAdo);

                            // all data is send -> be are now unmodified
                            ado.setModified(false);
                            if (resultChangeDate >= 0) {
                                ado.setChangeDate(new Date(resultChangeDate));
                            }
                            dao.update(ado);
                        }
                        return null;
                    }
                });

                Log.d(dao.getTableName(), "Pushed: " + modifiedAdos.size());
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

    /**
     * Merges all changes made in source (compared to original) into target and original.
     * @param target
     * @param original Note: this is also updated!
     * @param source
     */
    public void mergeData(ADO target, ADO original, ADO source) {

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(target.getClass());
        } catch (IntrospectionException e) {
            e.printStackTrace();
            return;
        }

        target.setChangeDate(source.getChangeDate());
        original.setChangeDate(source.getChangeDate());

        for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
            try {
                if (AbstractDomainObject.CREATION_DATE.equals(property.getName())
                        || AbstractDomainObject.CHANGE_DATE.equals(property.getName())
                        || AbstractDomainObject.LOCAL_CHANGE_DATE.equals(property.getName())
                        || AbstractDomainObject.UUID.equals(property.getName())
                        || AbstractDomainObject.ID.equals(property.getName())
                        || AbstractDomainObject.CLONED_ORIGINAL.equals(property.getName())
                        || AbstractDomainObject.MODIFIED.equals(property.getName())
                        || property.getWriteMethod() == null)
                    continue;

                Object baseFieldValue = property.getReadMethod().invoke(original);
                Object sourceFieldValue = property.getReadMethod().invoke(source);
                Object targetFieldValue = property.getReadMethod().invoke(target);

                boolean copyData;
                // we now have to write the value from source into target and base
                // there are three types of properties:

                if (DataHelper.isValueType(property.getPropertyType())) {
                    // 1. "value" types like String, Date, Enum, ...
                    // just copy value from source into target and base
                    copyData = true;
                }
                else if (AbstractDomainObject.class.isAssignableFrom(property.getPropertyType())) {
                    if (property.getPropertyType().isAnnotationPresent(EmbeddedAdo.class)) {
                        // 2. embedded domain objects like a Location or Symptoms
                        // call merge for the object
                        if (sourceFieldValue == null) {
                            throw new NullPointerException("The embedded object " + property.getName() + " is not allowed to be null.");
                        }
                        mergeData((ADO)targetFieldValue, (ADO)baseFieldValue, (ADO)sourceFieldValue);
                        copyData = false;
                    }
                    else {
                        // 3. reference domain objects like a reference to a Person or a District
                        // just copy reference value from source into target and base
                        copyData = true;
                    }
                }
                else {
                    // Other objects are not supported
                    throw new UnsupportedOperationException(property.getPropertyType().getName() + " is not supported as a property type.");
                }

                if (copyData) {

                    if (DataHelper.equal(baseFieldValue, sourceFieldValue)) {
                        continue;
                    }

                    if (!DataHelper.equal(baseFieldValue, targetFieldValue)) {
                        // we have a conflict
                        Log.e(beanInfo.getBeanDescriptor().getName(), "Overriding " + property.getName() +
                                " value changed from '" + DataHelper.toStringNullable(baseFieldValue) +
                                "' to '" + DataHelper.toStringNullable(targetFieldValue) +
                                "' with '" + DataHelper.toStringNullable(sourceFieldValue) + "'");
                    }

                    property.getWriteMethod().invoke(target, sourceFieldValue);
                    property.getWriteMethod().invoke(original, sourceFieldValue);
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
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
