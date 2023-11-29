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
import java.util.Objects;
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
import android.os.Build;
import android.text.Html;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.app.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventCriteria;
import de.symeda.sormas.app.backend.event.EventEditAuthorization;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.exposure.Exposure;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.pointofentry.PointOfEntry;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.therapy.Prescription;
import de.symeda.sormas.app.backend.therapy.PrescriptionCriteria;
import de.symeda.sormas.app.backend.therapy.Treatment;
import de.symeda.sormas.app.backend.therapy.TreatmentCriteria;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserRole;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.JurisdictionHelper;
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

		Date epiDataDate = getLatestChangeDateJoin(EpiData.TABLE_NAME, Case.EPI_DATA);
		if (epiDataDate != null && epiDataDate.after(date)) {
			date = epiDataDate;
		}

		Date exposureDate = getLatestChangeDateSubJoin(EpiData.TABLE_NAME, Case.EPI_DATA, Exposure.TABLE_NAME);
		if (exposureDate != null && exposureDate.after(date)) {
			date = exposureDate;
		}

		Date activityAsCaseDate = getLatestChangeDateSubJoin(EpiData.TABLE_NAME, Case.EPI_DATA, ActivityAsCase.TABLE_NAME);
		if (activityAsCaseDate != null && activityAsCaseDate.after(date)) {
			date = activityAsCaseDate;
		}

		Date therapyDate = DatabaseHelper.getTherapyDao().getLatestChangeDate();
		if (therapyDate != null && therapyDate.after(date)) {
			date = therapyDate;
		}

		Date clinicalCourseDate = getLatestChangeDateJoin(ClinicalCourse.TABLE_NAME, Case.CLINICAL_COURSE);
		if (clinicalCourseDate != null && clinicalCourseDate.after(date)) {
			date = clinicalCourseDate;
		}

		Date healthConditionsDate = getLatestChangeDateJoin(HealthConditions.TABLE_NAME, Case.HEALTH_CONDITIONS);
		if (healthConditionsDate != null && healthConditionsDate.after(date)) {
			date = healthConditionsDate;
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

		if (ConfigProvider.hasUserRight(UserRight.CASE_RESPONSIBLE)) {
			caze.setSurveillanceOfficer(user);
		} else if (user.hasJurisdictionLevel(JurisdictionLevel.HEALTH_FACILITY, JurisdictionLevel.COMMUNITY, JurisdictionLevel.POINT_OF_ENTRY)) {
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

		// health conditions
		caze.setHealthConditions(DatabaseHelper.getHealthConditionsDao().build());

		// Location
		User currentUser = ConfigProvider.getUser();

		// Set the disease if a default disease is available
		Disease defaultDisease = DiseaseConfigurationCache.getInstance().getDefaultDisease();
		if (defaultDisease != null) {
			caze.setDisease(defaultDisease);
		}

		if (UserRole.isPortHealthUser(currentUser.getUserRoles())) {
			caze.setResponsibleRegion(currentUser.getRegion());
			caze.setResponsibleDistrict(currentUser.getDistrict());
			caze.setDisease(Disease.UNDEFINED);
			caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
			caze.setPointOfEntry(ConfigProvider.getUser().getPointOfEntry());
		} else if (currentUser.getHealthFacility() != null) {
			caze.setResponsibleRegion(currentUser.getHealthFacility().getRegion());
			caze.setResponsibleDistrict(currentUser.getHealthFacility().getDistrict());
			caze.setResponsibleCommunity(currentUser.getHealthFacility().getCommunity());
			caze.setHealthFacility(currentUser.getHealthFacility());
			caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
		} else {
			caze.setResponsibleRegion(currentUser.getRegion());
			caze.setResponsibleDistrict(currentUser.getDistrict());
			caze.setResponsibleCommunity(currentUser.getCommunity());
			caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
		}

		return caze;
	}

	public Case build(Person person, Case caze) {
		Case newCase = build(person);
		if (caze != null) {
			newCase.setDisease(caze.getDisease());
			newCase.setDiseaseVariant(caze.getDiseaseVariant());
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
		newCase.getEpiData().setContactWithSourceCaseKnown(YesNoUnknown.YES);
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
		if (FacilityType.HOSPITAL.equals(oldCase.getFacilityType())) {
			caze.getHospitalization()
				.getPreviousHospitalizations()
				.add(DatabaseHelper.getPreviousHospitalizationDao().buildPreviousHospitalizationFromHospitalization(caze, oldCase));
			caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
		}
		if (FacilityType.HOSPITAL.equals(caze.getFacilityType())) {
			caze.getHospitalization().setAdmissionDate(new Date());
			caze.getHospitalization().setDischargeDate(null);
			caze.getHospitalization().setIsolated(null);
			caze.getHospitalization().setIntensiveCareUnit(null);
			caze.getHospitalization().setLeftAgainstAdvice(null);
			caze.getHospitalization().setAdmittedToHealthFacility(null);
			caze.getHospitalization().setHospitalizationReason(null);
			caze.getHospitalization().setOtherHospitalizationReason(null);
		}
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
			&& !Objects.equals(currentCase.getOutcomeDate(), source.getOutcomeDate())) {
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
			content.append("<b>").append(mergedCase.buildCaption()).append("</b><br/>");

			Intent notificationIntent = new Intent(context, CaseReadActivity.class);
			notificationIntent.putExtras(CaseReadActivity.buildBundle(mergedCase.getUuid(), false).get());
			PendingIntent pi = PendingIntent.getActivity(
				context,
				mergedCase.getId().intValue(),
				notificationIntent,
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_ONE_SHOT);
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

			boolean responsibleRegionChanged = !DataHelper.isSame(changedCase.getResponsibleRegion(), existingCase.getResponsibleRegion());
			boolean regionChanged = !DataHelper.isSame(changedCase.getRegion(), existingCase.getRegion());

			boolean responsibleDistrictChanged = !DataHelper.isSame(changedCase.getResponsibleDistrict(), existingCase.getResponsibleDistrict());
			boolean districtChanged = !DataHelper.isSame(changedCase.getDistrict(), existingCase.getDistrict());

			boolean responsibleCommunityChanged = !DataHelper.isSame(changedCase.getResponsibleCommunity(), existingCase.getResponsibleCommunity());
			boolean communityChanged = !DataHelper.isSame(changedCase.getCommunity(), existingCase.getCommunity());

			boolean facilityChanged = !DataHelper.isSame(changedCase.getHealthFacility(), existingCase.getHealthFacility());

			// If the case is moved from the surveillance officer's jurisdiction, assign a new surveillance officer
			if (DatabaseHelper.getFeatureConfigurationDao()
				.isPropertyValueTrue(FeatureType.CASE_SURVEILANCE, FeatureTypeProperty.AUTOMATIC_RESPONSIBILITY_ASSIGNMENT)
				&& changedCase.getSurveillanceOfficer() == null
				|| ((responsibleDistrictChanged || districtChanged)
					&& !DataHelper.isSame(changedCase.getResponsibleDistrict(), changedCase.getSurveillanceOfficer().getDistrict())
					&& !DataHelper.isSame(changedCase.getDistrict(), changedCase.getSurveillanceOfficer().getDistrict()))) {

				changedCase.setSurveillanceOfficer(
					DatabaseHelper.getUserDao().getRandomDistrictUser(changedCase.getResponsibleDistrict(), UserRight.CASE_RESPONSIBLE));
				if (changedCase.getSurveillanceOfficer() == null) {
					changedCase.setSurveillanceOfficer(
						DatabaseHelper.getUserDao().getRandomDistrictUser(changedCase.getDistrict(), UserRight.CASE_RESPONSIBLE));
				}
			}

			// if the case's jurisdiction has changed, re-assign tasks
			if (responsibleRegionChanged
				|| responsibleDistrictChanged
				|| responsibleCommunityChanged
				|| regionChanged
				|| districtChanged
				|| communityChanged
				|| facilityChanged) {
				for (Task task : DatabaseHelper.getTaskDao().queryByCase(existingCase)) {
					if (task.getTaskStatus() != TaskStatus.PENDING) {
						continue;
					}

					User assigneeUser = task.getAssigneeUser();
					DatabaseHelper.getUserDao().initUserRoles(assigneeUser);
					if (assigneeUser != null
						&& CaseJurisdictionBooleanValidator
							.of(JurisdictionHelper.createCaseJurisdictionDto(changedCase), JurisdictionHelper.createUserJurisdiction(assigneeUser))
							.isRootInJurisdiction()) {
						continue;
					}

					assignOfficerOrSupervisorToTask(changedCase, task);

					try {
						DatabaseHelper.getTaskDao().saveAndSnapshot(task);
					} catch (DaoException e) {
						Log.e(getTableName(), "Failed to save an updated task in onCaseChanged");
					}
				}
			}
		}
	}

	private void assignOfficerOrSupervisorToTask(Case changedCase, Task task) {

		User assignee = null;

		if (changedCase.getSurveillanceOfficer() != null) {
			// 1) The surveillance officer that is responsible for the case
			assignee = changedCase.getSurveillanceOfficer();
		} else {
			// 2) A random user with UserRight.CASE_RESPONSIBLE from the case responsible district
			assignee = getRandomDistrictCaseResponsible(changedCase.getResponsibleDistrict());
		}

		if (assignee == null && changedCase.getDistrict() != null) {
			// 3) A random surveillance officer from the case district
			assignee = getRandomDistrictCaseResponsible(changedCase.getDistrict());
		}

		if (assignee == null) {
			if (changedCase.getReportingUser() != null && (ConfigProvider.hasUserRight(UserRight.TASK_ASSIGN))) {
				// 4) If the case was created by a surveillance supervisor, assign them
				assignee = changedCase.getReportingUser();
			} else {
				// 5) Assign a random surveillance supervisor from the case responsible region
				assignee = getRandomRegionCaseResponsible(changedCase.getResponsibleRegion());
			}
			if (assignee == null && changedCase.getRegion() != null) {
				// 6) Assign a random surveillance supervisor from the case region
				assignee = getRandomRegionCaseResponsible(changedCase.getRegion());
			}
		}

		task.setAssigneeUser(assignee);
		if (assignee == null) {
			Log.w(getClass().getSimpleName(), "No valid assignee user found for task " + task.getUuid());
		}
	}

	private User getRandomDistrictCaseResponsible(District district) {

		return DatabaseHelper.getUserDao().getRandomDistrictUser(district, UserRight.CASE_RESPONSIBLE);
	}

	private User getRandomRegionCaseResponsible(Region region) {

		return DatabaseHelper.getUserDao().getRandomRegionUser(region, UserRight.CASE_RESPONSIBLE);
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

		//Remove events linked to case by removing case_id from event participants - delete event participant and 
		List<EventParticipant> eventParticipants = DatabaseHelper.getEventParticipantDao().getByCase(caze);
		for (EventParticipant eventParticipant : eventParticipants) {
			DatabaseHelper.getEventParticipantDao().deleteEventParticipant(eventParticipant);
		}

		//Remove events outside jurisdiction which were pulled in due to linking with an accessible case
		EventCriteria eventCriteria = new EventCriteria();
		eventCriteria.caze(caze);
		List<Event> eventList = DatabaseHelper.getEventDao().queryByCriteria(eventCriteria, 0, 0);
		for (Event event : eventList) {
			List<EventParticipant> eventParticipantByEventList = DatabaseHelper.getEventParticipantDao().getByEvent(event);
			if (eventParticipantByEventList.isEmpty()) {
				Boolean isEventInJurisdiction = EventEditAuthorization.isEventEditAllowed(event);
				if (!isEventInJurisdiction) {
					DatabaseHelper.getEventDao().delete(event);
				}
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
			CaseCriteria caseCriteria = criteria.getCaseCriteria();
			where.and().eq(Case.DISEASE, caseCriteria.getDisease());
			Where<Case, Long> regionFilter = where.and()
				.eq(Case.RESPONSIBLE_REGION + "_id", caseCriteria.getResponsibleRegion())
				.or()
				.eq(Case.REGION + "_id", caseCriteria.getResponsibleRegion());
			if (caseCriteria.getRegion() != null) {
				regionFilter.or()
					.eq(Case.RESPONSIBLE_REGION + "_id", caseCriteria.getRegion())
					.or()
					.eq(Case.REGION + "_id", caseCriteria.getRegion());
			}
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
			return buildQueryBuilder(criteria).orderBy(Case.LOCAL_CHANGE_DATE, false).offset(offset).limit(limit).query();
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

		if (criteria != null) {
			if (criteria.getIncludeCasesFromOtherJurisdictions().equals(false)) {
				createJurisdictionFilter(whereStatements, where);
			}
			createCriteriaFilter(whereStatements, where, criteria);
		}

		if (!whereStatements.isEmpty()) {
			Where<Case, Long> whereStatement = where.and(whereStatements.size());
			queryBuilder.setWhere(whereStatement);
		}

		queryBuilder = queryBuilder.leftJoin(personQueryBuilder);
		return queryBuilder;
	}

	public Where createJurisdictionFilter(List<Where<Case, Long>> whereStatements, Where<Case, Long> where) throws SQLException {
		List<Where> whereJurisdictionFilterStatements = new ArrayList<>();

		User currentUser = ConfigProvider.getUser();
		if (currentUser == null) {
			return null;
		}

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();

		switch (jurisdictionLevel) {
		case DISTRICT:
			District district = currentUser.getDistrict();
			if (district != null) {
				whereJurisdictionFilterStatements
					.add(where.or(where.eq((Case.DISTRICT), district), where.eq(Case.RESPONSIBLE_DISTRICT, district.getId())));
			}
			break;

		case HEALTH_FACILITY:
			Facility healthFacility = currentUser.getHealthFacility();
			if (healthFacility != null) {
				whereJurisdictionFilterStatements.add(where.eq(Case.HEALTH_FACILITY, healthFacility.getId()));
			}
			break;
		case COMMUNITY:
			Community community = currentUser.getCommunity();
			if (community != null) {
				whereJurisdictionFilterStatements
					.add(where.or(where.eq((Case.COMMUNITY), community), where.eq(Case.RESPONSIBLE_COMMUNITY, community.getId())));
			}
			break;
		case POINT_OF_ENTRY:
			PointOfEntry pointOfEntry = currentUser.getPointOfEntry();
			if (pointOfEntry != null) {
				whereJurisdictionFilterStatements.add(where.eq(Case.POINT_OF_ENTRY, pointOfEntry.getId()));
			}
			break;
		default:
		}

		if (!whereJurisdictionFilterStatements.isEmpty()) {
			where.or(whereJurisdictionFilterStatements.size());
			whereStatements.add(where);
		}

		return where;
	}

	public Where<Case, Long> createCriteriaFilter(List<Where<Case, Long>> whereStatements, Where<Case, Long> where, CaseCriteria criteria)
		throws SQLException {
		List<Where> whereCriteriaFilterStatements = new ArrayList<>();

		if (criteria.getInvestigationStatus() != null) {
			whereCriteriaFilterStatements.add(where.eq(Case.INVESTIGATION_STATUS, criteria.getInvestigationStatus()));
		}

		if (criteria.getDisease() != null) {
			whereCriteriaFilterStatements.add(where.eq(Case.DISEASE, criteria.getDisease()));
		}

		if (criteria.getCaseClassification() != null) {
			whereCriteriaFilterStatements.add(where.eq(Case.CASE_CLASSIFICATION, criteria.getCaseClassification()));
		}

		if (criteria.getOutcome() != null) {
			whereCriteriaFilterStatements.add(where.eq(Case.OUTCOME, criteria.getOutcome()));
		}

		if (criteria.getEpiWeekFrom() != null) {
			whereCriteriaFilterStatements.add(where.ge(Case.REPORT_DATE, DateHelper.getEpiWeekStart(criteria.getEpiWeekFrom())));
		}

		if (criteria.getEpiWeekTo() != null) {
			whereCriteriaFilterStatements.add(where.le(Case.REPORT_DATE, DateHelper.getEpiWeekEnd(criteria.getEpiWeekTo())));
		}

		if (criteria.getCaseOrigin() != null) {
			whereCriteriaFilterStatements.add(where.eq(Case.CASE_ORIGIN, criteria.getCaseOrigin()));
		}

		if (!StringUtils.isEmpty(criteria.getTextFilter())) {
			String[] textFilters = criteria.getTextFilter().split("\\s+");
			for (String filter : textFilters) {
				String textFilter = "%" + filter.toLowerCase() + "%";
				if (!StringUtils.isEmpty(textFilter)) {
					whereCriteriaFilterStatements.add(
						where.or(
							where.raw(Case.TABLE_NAME + "." + Case.UUID + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
							where.raw(Case.TABLE_NAME + "." + Case.EPID_NUMBER + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
							where.raw(Case.TABLE_NAME + "." + Case.EXTERNAL_ID + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
							where.raw(Person.TABLE_NAME + "." + Person.FIRST_NAME + " LIKE '" + textFilter.replaceAll("'", "''") + "'"),
							where.raw(Person.TABLE_NAME + "." + Person.LAST_NAME + " LIKE '" + textFilter.replaceAll("'", "''") + "'")));
				}
			}
		}

		if (!whereCriteriaFilterStatements.isEmpty()) {
			where.and(whereCriteriaFilterStatements.size());
			whereStatements.add(where);
		}

		return where;
	}

	public static Region getRegionWithFallback(Case caze) {
		if (caze.getRegion() == null) {
			return caze.getResponsibleRegion();
		}

		return caze.getRegion();
	}

	public static District getDistrictWithFallback(Case caze) {
		if (caze.getDistrict() == null) {
			return caze.getResponsibleDistrict();
		}

		return caze.getDistrict();
	}

	public static Community getCommunityWithFallback(Case caze) {
		if (caze.getRegion() == null) {
			return caze.getResponsibleCommunity();
		}

		return caze.getCommunity();
	}
}
