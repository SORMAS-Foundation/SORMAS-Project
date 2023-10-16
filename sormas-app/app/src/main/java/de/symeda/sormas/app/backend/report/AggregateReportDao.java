package de.symeda.sormas.app.backend.report;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.AgeGroupUtils;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.app.backend.pointofentry.PointOfEntry;
import de.symeda.sormas.app.backend.region.District;
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

	public List<AggregateReport> getReportsByEpiWeek(EpiWeek epiWeek) {

		try {
			QueryBuilder<AggregateReport, Long> builder = queryBuilder();
			Where<AggregateReport, Long> where = builder.where();
			where.eq(AbstractDomainObject.SNAPSHOT, false);
			where.and();
			where.eq(AggregateReport.YEAR, epiWeek.getYear());
			where.and();
			where.eq(AggregateReport.EPI_WEEK, epiWeek.getWeek());
			where.and();

			User user = ConfigProvider.getUser();
			switch (user.getJurisdictionLevel()) {
			case DISTRICT:
				where.eq(AggregateReport.DISTRICT + "_id", user.getDistrict());
				break;
			case HEALTH_FACILITY:
				where.eq(AggregateReport.HEALTH_FACILITY + "_id", user.getHealthFacility());
				break;
			case POINT_OF_ENTRY:
				where.eq(AggregateReport.POINT_OF_ENTRY + "_id", user.getPointOfEntry());
				break;
			default:
				throw new UnsupportedOperationException(
					"Aggregate reports can't be retrieved for jurisdiction level " + user.getJurisdictionLevel().toString());
			}

			List<AggregateReport> reports = builder.query();
			sortAggregateReports(reports);
			return reports;
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getReportsByEpiWeek");
			throw new RuntimeException(e);
		}
	}

	public void sortAggregateReports(List<AggregateReport> reports) {
		Function<AggregateReport, String> diseaseComparator = r -> r.getDisease().toString();

		Function<AggregateReport, Boolean> expiredAgeGroup =
			aggregateReport -> !isCurrentAgeGroup(aggregateReport.getDisease(), aggregateReport.getAgeGroup());

		Comparator<AggregateReport> comparator = Comparator.comparing(diseaseComparator)
			.thenComparing(expiredAgeGroup)
			.thenComparing(AggregateReport::getAgeGroup, AgeGroupUtils.getComparator());
		reports.sort(comparator);
	}

	public boolean isCurrentAgeGroup(Disease disease, String ageGroup) {
		List<String> ageGroups = DatabaseHelper.getDiseaseConfigurationDao().getDiseaseConfiguration(disease).getAgeGroups();
		if (ageGroups == null) {
			return ageGroup == null;
		} else {
			return ageGroups.contains(ageGroup);
		}
	}

	public AggregateReport build(Disease disease, EpiWeek epiWeek, InfrastructureAdo infrastructure) {

		AggregateReport report = super.build();

		User user = ConfigProvider.getUser();
		report.setReportingUser(user);
		report.setRegion(user.getRegion());
		report.setDistrict(infrastructure instanceof District ? (District) infrastructure : user.getDistrict());
		report.setHealthFacility(infrastructure instanceof Facility ? (Facility) infrastructure : user.getHealthFacility());
		report.setPointOfEntry(infrastructure instanceof PointOfEntry ? (PointOfEntry) infrastructure : user.getPointOfEntry());
		report.setYear(epiWeek.getYear());
		report.setEpiWeek(epiWeek.getWeek());
		report.setDisease(disease);

		return report;
	}

	public AggregateReport build(Disease disease, EpiWeek epiWeek) {

		User user = ConfigProvider.getUser();
		return build(
			disease,
			epiWeek,
			user.getHealthFacility() != null
				? user.getHealthFacility()
				: user.getPointOfEntry() != null ? user.getPointOfEntry() : user.getDistrict());
	}
}
