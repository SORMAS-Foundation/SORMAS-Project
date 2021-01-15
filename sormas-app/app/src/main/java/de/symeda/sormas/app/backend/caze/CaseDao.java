/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.caze;

import static android.content.Context.NOTIFICATION_SERVICE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.text.Html;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.therapy.Prescription;
import de.symeda.sormas.app.backend.therapy.PrescriptionCriteria;
import de.symeda.sormas.app.backend.therapy.Treatment;
import de.symeda.sormas.app.backend.therapy.TreatmentCriteria;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.LocationService;

public class CaseDao extends AbstractAdoDao<Case> {

	public CaseDao(Dao<Case, Long> innerDao) throws SQLException {
		super(innerDao);
	}

	@Override
	protected Class<Case> getAdoClass() {
		return Case.class;
	}

	@Override
	public String getTableName() {
		return Case.TABLE_NAME;
	}

	@Override
	@Deprecated
	/**
	 * Deprecated: Call queryUuidBasic, queryUuidReference or queryUuidWithEmbedded instead
	 */
	public Case queryUuid(String uuid) {
		return super.queryUuid(uuid);
	}

	public Case queryUuidBasic(String uuid) {
		return super.queryUuid(uuid);
	}

	@Override
	public Date getLatestChangeDate() {
		Date date = super.getLatestChangeDate();
		if (date == null) {
			return null;
		}

		Date symptomsDate = getLatestChangeDateJoin(Symptoms.TABLE_NAME, Case.SYMPTOMS);
		if (symptomsDate != null && symptomsDate.after(date)) {
			date = symptomsDate;
		}

		Date hospitalizationDate = DatabaseHelper.getHospitalizationDao().getLatestChangeDate();
		if (hospitalizationDate != null && hospitalizationDate.after(date)) {
			date = hospitalizationDate;
		}

		Date epiDataDate = DatabaseHelper.getEpiDataDao().getLatestChangeDate();
		if (epiDataDate != null && epiDataDate.after(date)) {
			date = epiDataDate;
		}

		Date therapyDate = DatabaseHelper.getTherapyDao().getLatestChangeDate();
		if (therapyDate != null && therapyDate.after(date)) {
			date = therapyDate;
		}

		Date clinicalCourseDate = DatabaseHelper.getClinicalCourseDao().getLatestChangeDate();
		if (clinicalCourseDate != null && clinicalCourseDate.after(date)) {
			date = clinicalCourseDate;
		}

		Date maternalHistoryDate = DatabaseHelper.getMaternalHistoryDao().getLatestChangeDate();
		if (maternalHistoryDate != null && maternalHistoryDate.after(date)) {
			date = maternalHistoryDate;
		}

		Date portHealthInfoDate = DatabaseHelper.getPortHealthInfoDao().getLatestChangeDate();
		if (portHealthInfoDate != null && portHealthInfoDate.after(date)) {
			date = portHealthInfoDate;
		}

		return date;
	}

	public List<Case> queryBaseForEq(String fieldName, Object value, String orderBy, boolean ascending, long offset, long limit) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(fieldName, value);
			where.and().eq(AbstractDomainObject.SNAPSHOT, false).query();
			builder.selectColumns(
				Case.UUID,
				Case.LAST_OPENED_DATE,
				Case.LOCAL_CHANGE_DATE,
				Case.MODIFIED,
				Case.REPORT_DATE,
				Case.REPORTING_USER,
				Case.DISEASE,
				Case.DISEASE_DETAILS,
				Case.PERSON,
				Case.CASE_CLASSIFICATION,
				Case.INVESTIGATION_STATUS,
				Case.OUTCOME,
				Case.HEALTH_FACILITY);
			return builder.orderBy(orderBy, ascending).offset(offset).limit(limit).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryForEq");
			throw new RuntimeException(e);
		}
	}

	@Override
	public Case build() {
		throw new UnsupportedOperationException("Use build(Person) instead");
	}

	public Case build(Person person) {
		Case caze = super.build();
		caze.setPerson(person);

		User user = ConfigProvider.getUser();
		caze.setReportingUser(user);

		if (user.hasUserRole(UserRole.SURVEILLANCE_OFFICER)) {
			caze.setSurveillanceOfficer(user);
		} else if (user.hasUserRole(UserRole.HOSPITAL_INFORMANT)
			|| user.hasUserRole(UserRole.COMMUNITY_INFORMANT)
			|| user.hasUserRole(UserRole.POE_INFORMANT)) {
			caze.setSurveillanceOfficer(user.getAssociatedOfficer());
		}

		caze.setInvestigationStatus(InvestigationStatus.PENDING);
		caze.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
		caze.setOutcome(CaseOutcome.NO_OUTCOME);

		// Symptoms
		caze.setSymptoms(DatabaseHelper.getSymptomsDao().build());

		// Hospitalization
		caze.setHospitalization(DatabaseHelper.getHospitalizationDao().build());

		// Epi Data
		caze.setEpiData(DatabaseHelper.getEpiDataDao().build());

		// Therapy
		caze.setTherapy(DatabaseHelper.getTherapyDao().build());

		// Clinical Course
		caze.setClinicalCourse(DatabaseHelper.getClinicalCourseDao().build());

		// Maternal History
		caze.setMaternalHistory(DatabaseHelper.getMaternalHistoryDao().build());

		// Port Health Info
		caze.setPortHealthInfo(DatabaseHelper.getPortHealthInfoDao().build());

		// Location
		User currentUser = ConfigProvider.getUser();

		// Set the disease if a default disease is available
		Disease defaultDisease = DiseaseConfigurationCache.getInstance().getDefaultDisease();
		if (defaultDisease != null) {
			caze.setDisease(defaultDisease);
		}

		if (UserRole.isPortHealthUser(currentUser.getUserRoles())) {
			caze.setRegion(currentUser.getRegion());
			caze.setDistrict(currentUser.getDistrict());
			caze.setDisease(Disease.UNDEFINED);
			caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
			caze.setPointOfEntry(ConfigProvider.getUser().getPointOfEntry());
		} else if (currentUser.getHealthFacility() != null) {
			caze.setRegion(currentUser.getHealthFacility().getRegion());
			caze.setDistrict(currentUser.getHealthFacility().getDistrict());
			caze.setCommunity(currentUser.getHealthFacility().getCommunity());
			caze.setHealthFacility(currentUser.getHealthFacility());
			caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
		} else {
			caze.setRegion(currentUser.getRegion());
			caze.setDistrict(currentUser.getDistrict());
			caze.setCommunity(currentUser.getCommunity());
			caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
		}

		return caze;
	}

	public Case build(Person person, Case caze) {
		Case newCase = build(person);
		if (caze != null) {
			newCase.setDisease(caze.getDisease());
			newCase.setDiseaseDetails(caze.getDiseaseDetails());
			newCase.setPlagueType(caze.getPlagueType());
			newCase.setDengueFeverType(caze.getDengueFeverType());
		}

		return newCase;
	}

	public Case build(Contact contact) {
		Case newCase = build(contact.getPerson());
		newCase.setDisease(contact.getDisease());
		newCase.setDiseaseDetails(contact.getDiseaseDetails());
		return newCase;
	}

	public Case build(EventParticipant eventParticipant) {
		Case newCase = build(eventParticipant.getPerson());
		Event event = eventParticipant.getEvent();
		newCase.setDisease(event.getDisease());
		newCase.setDiseaseDetails(event.getDiseaseDetails());
		return newCase;
	}

	public void createPreviousHospitalizationAndUpdateHospitalization(Case caze, Case oldCase) {
		caze.getHospitalization()
			.getPreviousHospitalizations()
			.add(DatabaseHelper.getPreviousHospitalizationDao().buildPreviousHospitalizationFromHospitalization(caze, oldCase));
		caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
		caze.getHospitalization().setAdmissionDate(new Date());
		caze.getHospitalization().setDischargeDate(null);
		caze.getHospitalization().setIsolated(null);
	}

	/**
	 * Returns the number of cases reported by the current user over the course of the given epi week.
	 */
	public int getNumberOfCasesForEpiWeek(EpiWeek epiWeek, User user) {
		return getNumberOfCasesForEpiWeekAndDisease(epiWeek, null, user);
	}

	/**
	 * Returns the number of cases with the given disease reported by the current user over the course of the given epi week.
	 */
	public int getNumberOfCasesForEpiWeekAndDisease(EpiWeek epiWeek, Disease disease, User user) {
		if (!(ConfigProvider.hasUserRight(UserRight.WEEKLYREPORT_CREATE))) {
			throw new UnsupportedOperationException(
				"Can only retrieve the number of reported cases by epi week and disease for " + "users that can create weekly reports.");
		}

		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(
				where.eq(Case.REPORTING_USER, user),
				where.ge(Case.REPORT_DATE, DateHelper.getEpiWeekStart(epiWeek)),
				where.le(Case.REPORT_DATE, DateHelper.getEpiWeekEnd(epiWeek)));

			if (disease != null) {
				where.and(where, where.eq(Case.DISEASE, disease));
			}

			return (int) builder.countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getNumberOfCasesForEpiWeekAndDisease");
			throw new RuntimeException(e);
		}
	}

	// TODO #704
//    @Override
//    /**
//     * @param caze person has to be initialized
//     */
//    public void markAsRead(Case caze) {
//        super.markAsRead(caze);
//        DatabaseHelper.getPersonDao().markAsRead(caze.getPerson());
//    }

	@Override
	public Case mergeOrCreate(Case source) throws DaoException {
		Case currentCase = super.queryUuid(source.getUuid());

		// date of outcome can be set by the server site automatically and at the same time in the app
		// see CaseEditActivity.updateOutcomeAndPersonCondition
		if (currentCase != null
			&& currentCase.isModified()
			&& currentCase.getOutcomeDate() != null
			&& source.getOutcomeDate() != null
			&& currentCase.getOutcomeDate() != source.getOutcomeDate()) {
			// this could be the situation, but we also have to check the snapshot - the outcome date has to be null
			Case snapshotCase = querySnapshotByUuid(source.getUuid());
			if (snapshotCase != null && snapshotCase.getOutcomeDate() == null) {
				// we now have to ignore the conflict -> the outcome date of the app always wins
				source.setOutcomeDate(currentCase.getOutcomeDate());
			}
		}

		Case mergedCase = super.mergeOrCreate(source);

		// Build and send a notification when the disease has changed
		if (currentCase != null && mergedCase != null && currentCase.getDisease() != mergedCase.getDisease()) {
			Context context = DatabaseHelper.getContext();

			StringBuilder content = new StringBuilder();
			content.append("<b>").append(mergedCase.toString()).append("</b><br/>");

			Intent notificationIntent = new Intent(context, CaseReadActivity.class);
			notificationIntent.putExtras(CaseReadActivity.buildBundle(mergedCase.getUuid(), false).get());
			PendingIntent pi = PendingIntent.getActivity(context, mergedCase.getId().intValue(), notificationIntent, 0);
			Resources r = context.getResources();

			NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(context, NotificationHelper.NOTIFICATION_CHANNEL_CASE_CHANGES_ID)
					.setTicker(r.getString(R.string.heading_case_disease_changed))
					.setSmallIcon(R.mipmap.ic_launcher_foreground)
					.setContentTitle(r.getString(R.string.heading_case_disease_changed))
					.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(content.toString())))
					.setContentIntent(pi)
					.setAutoCancel(true)
					.setContentIntent(pi);

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
			int notificationId = mergedCase.getId().intValue();
			notificationManager.notify(notificationId, notificationBuilder.build());
		}
		return mergedCase;
	}

	@Override
	public Case saveAndSnapshot(final Case caze) throws DaoException {
		final Case existingCase = queryUuidBasic(caze.getUuid());

		if (existingCase == null) {
			caze.setCreationVersion(InfoProvider.get().getVersion() + " (App)");
		}

		onCaseChanged(existingCase, caze);
		return super.saveAndSnapshot(caze);
	}

	private void onCaseChanged(Case existingCase, Case changedCase) {
		changedCase.setCompleteness(calculateCompleteness(changedCase));
		if (existingCase == null) {
			// If a new case is created, use the last available location to update its report latitude and longitude
			Location location = LocationService.instance().getLocation();
			if (location != null) {
				changedCase.setReportLat(location.getLatitude());
				changedCase.setReportLon(location.getLongitude());
				changedCase.setReportLatLonAccuracy(location.getAccuracy());
			}
		} else {
			// classification
			if (changedCase.getCaseClassification() != existingCase.getCaseClassification()) {
				changedCase.setClassificationDate(new Date());
				changedCase.setClassificationUser(ConfigProvider.getUser());
			}

			// change the disease of all contacts if the case disease or disease details have changed
			if (existingCase.getDisease() != changedCase.getDisease()
				|| !StringUtils.equals(existingCase.getDiseaseDetails(), changedCase.getDiseaseDetails())) {
				for (Contact contact : DatabaseHelper.getContactDao().getByCase(changedCase)) {
					contact.setDisease(changedCase.getDisease());
					contact.setDiseaseDetails(changedCase.getDiseaseDetails());
					try {
						DatabaseHelper.getContactDao().saveAndSnapshot(contact);
					} catch (DaoException e) {
						Log.e(getTableName(), "Failed to save an updated contact in onCaseChanged");
					}
				}
			}

			// If the district has changed, assign a new surveillance officer and re-assign tasks
			if (!changedCase.getDistrict().getUuid().equals(existingCase.getUuid())) {
				List<User> districtOfficers =
					DatabaseHelper.getUserDao().getByDistrictAndRole(changedCase.getDistrict(), UserRole.SURVEILLANCE_OFFICER, User.UUID);
				if (districtOfficers.size() == 1) {
					changedCase.setSurveillanceOfficer(districtOfficers.get(0));
				} else {
					changedCase.setSurveillanceOfficer(null);
				}

				for (Task task : DatabaseHelper.getTaskDao().queryByCase(existingCase)) {
					if (task.getTaskStatus() != TaskStatus.PENDING) {
						continue;
					}

					if (changedCase.getSurveillanceOfficer() != null) {
						task.setAssigneeUser(changedCase.getSurveillanceOfficer());
					} else {
						// TODO roles? what happens when there are no supervisors? assignee user cannot be null
						List<User> survSupervisors =
							DatabaseHelper.getUserDao().getByRegionAndRole(changedCase.getRegion(), UserRole.SURVEILLANCE_SUPERVISOR);
						List<User> caseSupervisors =
							DatabaseHelper.getUserDao().getByRegionAndRole(changedCase.getRegion(), UserRole.CASE_SUPERVISOR);
						if (survSupervisors.size() >= 1) {
							task.setAssigneeUser(survSupervisors.get(0));
						} else if (caseSupervisors.size() >= 1) {
							task.setAssigneeUser(caseSupervisors.get(0));
						} else {
							task.setAssigneeUser(null);
						}
					}

					try {
						DatabaseHelper.getTaskDao().saveAndSnapshot(task);
					} catch (DaoException e) {
						Log.e(getTableName(), "Failed to save an updated task in onCaseChanged");
					}
				}
			}
		}
	}

	private Float calculateCompleteness(Case caze) {

		Set<UserRight> rights = ConfigProvider.getUserRights();
		int points = 0;

		if (InvestigationStatus.DONE.equals(caze.getInvestigationStatus())) {
			points += 20;
		}
		if (!CaseClassification.NOT_CLASSIFIED.equals(caze.getCaseClassification())) {
			points += 20;
		}
		if (caze.getId() != null
			&& rights.contains(UserRight.SAMPLE_VIEW)
			&& DatabaseHelper.getSampleDao().getSampleCountByCaseId(caze.getId()) > 0) {
			points += 15;
		}
		if (Boolean.TRUE.equals(caze.getSymptoms().getSymptomatic())) {
			points += 15;
		}
		if (rights.contains(UserRight.CONTACT_VIEW) && DatabaseHelper.getContactDao().getContactCountByCaseUuid(caze.getUuid()) > 0) {
			points += 10;
		}
		if (!CaseOutcome.NO_OUTCOME.equals(caze.getOutcome())) {
			points += 5;
		}
		if (caze.getPerson().getBirthdateYYYY() != null || caze.getPerson().getApproximateAge() != null) {
			points += 5;
		}
		if (caze.getPerson().getSex() != null) {
			points += 5;
		}
		if (caze.getSymptoms().getOnsetDate() != null) {
			points += 5;
		}

		float goal = 100;

		if (!rights.contains(UserRight.SAMPLE_VIEW)) {
			goal -= 15;
		}
		if (!rights.contains(UserRight.CONTACT_VIEW)) {
			goal -= 10;
		}

		return points / goal * 100;
	}

	public void deleteCaseAndAllDependingEntities(String caseUuid) throws SQLException {
		Case caze = queryUuidWithEmbedded(caseUuid);

		// Cancel if not in local database
		if (caze == null) {
			return;
		}

		// Delete contacts, contact tasks and visits
		List<Contact> contacts = DatabaseHelper.getContactDao().getByCase(caze);
		for (Contact contact : contacts) {
			DatabaseHelper.getContactDao().deleteContactAndAllDependingEntities(contact);
		}

		// Delete samples, pathogen tests and additional tests
		List<Sample> samples = DatabaseHelper.getSampleDao().queryByCase(caze);
		for (Sample sample : samples) {
			DatabaseHelper.getSampleDao().deleteSampleAndAllDependingEntities(sample);
		}

		// Delete case tasks
		List<Task> tasks = DatabaseHelper.getTaskDao().queryByCase(caze);
		for (Task task : tasks) {
			DatabaseHelper.getTaskDao().deleteCascade(task);
		}

		// Delete treatments and prescriptions
		if (caze.getTherapy() != null) {
			for (Treatment treatment : DatabaseHelper.getTreatmentDao().findBy(new TreatmentCriteria().therapy(caze.getTherapy()))) {
				DatabaseHelper.getTreatmentDao().delete(treatment);
			}
			for (Prescription prescription : DatabaseHelper.getPrescriptionDao().findBy(new PrescriptionCriteria().therapy(caze.getTherapy()))) {
				DatabaseHelper.getPrescriptionDao().delete(prescription);
			}
		}

		// Delete clinical visits
		if (caze.getClinicalCourse() != null) {
			for (ClinicalVisit clinicalVisit : DatabaseHelper.getClinicalVisitDao()
				.findBy(new ClinicalVisitCriteria().clinicalCourse(caze.getClinicalCourse()))) {
				DatabaseHelper.getClinicalVisitDao().delete(clinicalVisit);
			}
		}

		// Delete case
		deleteCascade(caze);
	}

	public List<Case> getSimilarCases(CaseSimilarityCriteria criteria) {
		try {
			QueryBuilder<Case, Long> queryBuilder = queryBuilder();
			QueryBuilder<Person, Long> personQueryBuilder = DatabaseHelper.getPersonDao().queryBuilder();

			Where<Case, Long> where = queryBuilder.where().eq(AbstractDomainObject.SNAPSHOT, false);
			where.and().eq(Case.DISEASE, criteria.getCaseCriteria().getDisease());
			where.and().eq(Case.REGION + "_id", criteria.getCaseCriteria().getRegion());
			where.and().raw(Person.TABLE_NAME + "." + Person.UUID + " = '" + criteria.getPersonUuid() + "'");
			where.and()
				.between(Case.REPORT_DATE, DateHelper.subtractDays(criteria.getReportDate(), 30), DateHelper.addDays(criteria.getReportDate(), 30));

			queryBuilder.setWhere(where);
			queryBuilder = queryBuilder.leftJoin(personQueryBuilder);

			return queryBuilder.orderBy(Case.CREATION_DATE, false).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getSimilarCases on Case");
			throw new RuntimeException(e);
		}
	}

	public long countByCriteria(CaseCriteria criteria) {
		try {
			return buildQueryBuilder(criteria).countOf();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform countByCriteria on Case");
			throw new RuntimeException(e);
		}
	}

	public List<Case> queryByCriteria(CaseCriteria criteria, long offset, long limit) {
		try {
			return buildQueryBuilder(criteria).orderBy(Case.REPORT_DATE, false).offset(offset).limit(limit).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryByCriteria on Case");
			throw new RuntimeException(e);
		}
	}

	private QueryBuilder<Case, Long> buildQueryBuilder(CaseCriteria criteria) throws SQLException {
		QueryBuilder<Case, Long> queryBuilder = queryBuilder();
		QueryBuilder<Person, Long> personQueryBuilder = DatabaseHelper.getPersonDao().queryBuilder();

		List<Where<Case, Long>> whereStatements = new ArrayList<>();
		Where<Case, Long> where = queryBuilder.where();
		whereStatements.add(where.eq(AbstractDomainObject.SNAPSHOT, false));

		if (criteria.getInvestigationStatus() != null) {
			whereStatements.add(where.eq(Case.INVESTIGATION_STATUS, criteria.getInvestigationStatus()));
		}
		if (criteria.getDisease() != null) {
			whereStatements.add(where.eq(Case.DISEASE, criteria.getDisease()));
		}
		if (criteria.getCaseClassification() != null) {
			whereStatements.add(where.eq(Case.CASE_CLASSIFICATION, criteria.getCaseClassification()));
		}
		if (criteria.getOutcome() != null) {
			whereStatements.add(where.eq(Case.OUTCOME, criteria.getOutcome()));
		}
		if (criteria.getEpiWeekFrom() != null) {
			whereStatements.add(where.ge(Case.REPORT_DATE, DateHelper.getEpiWeekStart(criteria.getEpiWeekFrom())));
		}
		if (criteria.getEpiWeekTo() != null) {
			whereStatements.add(where.le(Case.REPORT_DATE, DateHelper.getEpiWeekEnd(criteria.getEpiWeekTo())));
		}
		if (criteria.getCaseOrigin() != null) {
			whereStatements.add(where.eq(Case.CASE_ORIGIN, criteria.getCaseOrigin()));
		}
		if (!StringUtils.isEmpty(criteria.getTextFilter())) {
			String[] textFilters = criteria.getTextFilter().split("\\s+");
			for (String filter : textFilters) {
				String textFilter = "%" + filter.toLowerCase() + "%";
				if (!StringUtils.isEmpty(textFilter)) {
					whereStatements.add(
						where.or(
							where.raw(Case.TABLE_NAME + "." + Case.UUID + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
							where.raw(Case.TABLE_NAME + "." + Case.EPID_NUMBER + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
							where.raw(Person.TABLE_NAME + "." + Person.FIRST_NAME + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
							where.raw(Person.TABLE_NAME + "." + Person.LAST_NAME + " LIKE '" + textFilter.replaceAll("'", "''") + "'")));
				}
			}
		}

		if (!whereStatements.isEmpty()) {
			Where<Case, Long> whereStatement = where.and(whereStatements.size());
			queryBuilder.setWhere(whereStatement);
		}
		queryBuilder = queryBuilder.leftJoin(personQueryBuilder);
		return queryBuilder;
	}
}
