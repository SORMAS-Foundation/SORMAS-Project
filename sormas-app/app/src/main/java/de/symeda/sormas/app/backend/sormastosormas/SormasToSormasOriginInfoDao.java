package de.symeda.sormas.app.backend.sormastosormas;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class SormasToSormasOriginInfoDao extends AbstractAdoDao<SormasToSormasOriginInfo> {
    public SormasToSormasOriginInfoDao(Dao<SormasToSormasOriginInfo, Long> innerDao) {
        super(innerDao);
    }

    @Override
    protected Class<SormasToSormasOriginInfo> getAdoClass() {
        return SormasToSormasOriginInfo.class;
    }

    @Override
    public String getTableName() {
        return SormasToSormasOriginInfo.TABLE_NAME;
    }


}
