package de.symeda.sormas.app.backend.facility;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class FacilityDao extends AbstractAdoDao<Facility> {

    public FacilityDao(Dao<Facility,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Facility> getAdoClass() {
        return Facility.class;
    }

    @Override
    public String getTableName() {
        return Facility.TABLE_NAME;
    }

    /**
     * @param region null will return the change date of facilities that don't have a region
     */
    public Date getLatestChangeDateByRegion(Region region) {

        try {
            QueryBuilder<Facility, Long> queryBuilder = queryBuilder();
            queryBuilder.selectRaw("MAX(" + AbstractDomainObject.CHANGE_DATE + ")");
            if (region != null) {
                queryBuilder.where().eq(Facility.REGION, region);
            } else {
                queryBuilder.where().isNull(Facility.REGION);
            }

            GenericRawResults<Object[]> maxChangeDateResult = queryRaw(queryBuilder.prepareStatementString(), new DataType[]{DataType.DATE_LONG});
            List<Object[]> dateResults = maxChangeDateResult.getResults();
            if (dateResults.size() > 0) {
                return (Date) dateResults.get(0)[0];
            }
            return null;

        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getLatestChangeDateByRegion");
            throw new RuntimeException();
        }
    }

    public List<Facility> getHealthFacilitiesByDistrict(District district, boolean includeStaticFacilities) {

        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and(
                    where.eq(Facility.DISTRICT, district),
                    where.eq(AbstractDomainObject.SNAPSHOT, false),
                    where.or(where.ne(Facility.TYPE, FacilityType.LABORATORY), where.isNull(Facility.TYPE)));
            List<Facility> facilities = builder.orderBy(Facility.NAME, true).query();

            if (includeStaticFacilities) {
                facilities.add(queryUuid(FacilityDto.OTHER_FACILITY_UUID));
                facilities.add(queryUuid(FacilityDto.NONE_FACILITY_UUID));
            }

            return facilities;

        } catch (SQLException | IllegalArgumentException e) {
            Log.e(getTableName(), "Could not perform queryForEq");
            throw new RuntimeException(e);
        }
    }

    public List<Facility> getHealthFacilitiesByCommunity(Community community, boolean includeStaticFacilities) {
        List<Facility> facilities = queryForEq(Facility.COMMUNITY, community, Facility.NAME, true);
        for (Facility facility : facilities) {
            if (facility.getType() == FacilityType.LABORATORY) {
                facilities.remove(facility);
            }
        }

        if (includeStaticFacilities) {
            facilities.add(queryUuid(FacilityDto.OTHER_FACILITY_UUID));
            facilities.add(queryUuid(FacilityDto.NONE_FACILITY_UUID));
        }

        return facilities;
    }

    public List<Facility> getLaboratories(boolean includeOtherLaboratory) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.eq(Facility.TYPE, FacilityType.LABORATORY);
            where.and().eq(AbstractDomainObject.SNAPSHOT, false);
            where.and().ne(Facility.UUID, FacilityDto.OTHER_LABORATORY_UUID).query();
            List<Facility> facilities = builder.orderBy(Facility.NAME, true).query();

            if (includeOtherLaboratory) {
                facilities.add(queryUuid(FacilityDto.OTHER_LABORATORY_UUID));
            }

            return facilities;
        } catch (SQLException | IllegalArgumentException e) {
            Log.e(getTableName(), "Could not perform queryForEq");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Facility saveAndSnapshot(Facility facility) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Facility mergeOrCreate(Facility source) throws DaoException {
        throw new UnsupportedOperationException();
    }

    public int updateOrCreate(Facility data) {
        if (data.getId() == null) {
            return create(data);
        } else {
            return update(data);
        }
    }
}
