package de.symeda.sormas.app.backend.report;

import com.j256.ormlite.dao.Dao;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

public class AggregateReportDao extends AbstractAdoDao<AggregateReport> {

    public AggregateReportDao(Dao<AggregateReport, Long> innerDao) {
        super(innerDao);
    }

    @Override
    protected Class<AggregateReport> getAdoClass() {
        return null;
    }

    @Override
    public String getTableName() {
        return AggregateReport.TABLE_NAME;
    }



}
