package de.symeda.sormas.app.backend.report;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;

public class AggregateReportDao extends AbstractAdoDao<AggregateReport> {

	public AggregateReportDao(Dao<AggregateReport, Long> innerDao) {
		super(innerDao);
	}

	@Override
	protected Class<AggregateReport> getAdoClass() {
		return AggregateReport.class;
	}

	@Override
	public String getTableName() {
		return AggregateReport.TABLE_NAME;
	}

	public List<AggregateReport> getReportsByEpiWeekAndUser(EpiWeek epiWeek, User user) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(
				where.eq(AbstractDomainObject.SNAPSHOT, false),
				where.eq(AggregateReport.REPORTING_USER + "_id", user),
				where.eq(AggregateReport.YEAR, epiWeek.getYear()),
				where.eq(AggregateReport.EPI_WEEK, epiWeek.getWeek()));

			List<AggregateReport> reports = builder.query();
			Set<Disease> diseasesWithReports = new HashSet<>();
			for (AggregateReport report : reports) {
				diseasesWithReports.add(report.getDisease());
			}

			sortAggregateReports(reports);

			return reports;
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByEpiWeekAndUser");
			throw new RuntimeException(e);
		}
	}

	public static void sortAggregateReports(List<AggregateReport> reports) {
		Function<AggregateReport, String> diseaseComparator = r -> r.getDisease().toString();
		Comparator<AggregateReport> comparator = Comparator.comparing(diseaseComparator)
			.thenComparing(
				r -> r.getAgeGroup() != null
					? r.getAgeGroup().split("_")[0].replaceAll("[^a-zA-Z]", StringUtils.EMPTY).toUpperCase()
					: StringUtils.EMPTY)
			.thenComparing(
					r -> r.getAgeGroup() != null ? Integer.parseInt(r.getAgeGroup().split("_")[0].replaceAll("[^0-9]", StringUtils.EMPTY)) : 0);

		Collections.sort(reports, comparator);
	}

	public AggregateReport build(Disease disease, EpiWeek epiWeek) {
		AggregateReport report = super.build();

		User user = ConfigProvider.getUser();
		report.setReportingUser(user);
		report.setRegion(user.getRegion());
		report.setDistrict(user.getDistrict());
		report.setHealthFacility(user.getHealthFacility());
		report.setPointOfEntry(user.getPointOfEntry());
		report.setYear(epiWeek.getYear());
		report.setEpiWeek(epiWeek.getWeek());
		report.setDisease(disease);

		return report;
	}
}
