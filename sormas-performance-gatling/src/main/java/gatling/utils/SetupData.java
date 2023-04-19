package gatling.utils;

import gatling.envconfig.constants.UserRoles;
import gatling.envconfig.manager.RunningConfiguration;
import gatling.environmentdata.constants.*;
import gatling.environmentdata.manager.EnvironmentManager;
import io.gatling.javaapi.core.ChainBuilder;

import java.util.Calendar;
import java.util.UUID;

import static io.gatling.javaapi.core.CoreDsl.*;

public class SetupData {

    private static EnvironmentManager environmentManager = new EnvironmentManager();
    private static RunningConfiguration runningConfiguration = new RunningConfiguration();

    private static ChainBuilder setTimeStamp =
            exec(session -> session.set("timestamp", Calendar.getInstance().getTimeInMillis()));

    private static ChainBuilder setPersonGUID =
            exec(session -> session.set("personGUID", UUID.randomUUID()));

    private static ChainBuilder setImmunizationGUID =
            exec(session -> session.set("immunizationGUID", UUID.randomUUID()));

    private static ChainBuilder setReportDate =
            exec(session -> session.set("todayDate", Calendar.getInstance().getTimeInMillis()));

    private static ChainBuilder getRegionUUID =
            exec(session -> session.set("regionUUID",
                    environmentManager.getRegionUUID(RegionsValues.VoreingestellteBundeslander.getName())));

    private static ChainBuilder getDistrictUUID =
            exec(session -> session.set("districtUUID",
                    environmentManager.getDistrictUUID(DistrictsValues.VoreingestellterLandkreis.getName())));

    private static ChainBuilder getCommunityUUID =
            exec(session -> session.set("communityUUID",
                    environmentManager.getCommunityUUID(CommunityValues.VoreingestellteGemeinde.getName())));

    private static ChainBuilder getNationalUserUUID =
            exec(session -> session.set("nationalUserUUID",
                    runningConfiguration.getUserByRole(TestingSpecs.getEnvironment(), UserRoles.NationalUser.getRole()).getUuid()));

    private static ChainBuilder getSurveillanceUserUUID =
            exec(session -> session.set("surveillanceOfficerUUID",
                    runningConfiguration.getUserByRole(TestingSpecs.getEnvironment(), UserRoles.SurveillanceOfficer.getRole()).getUuid()));

    private static ChainBuilder getRestUserUUID =
            exec(session -> session.set("restUserUUID",
                    runningConfiguration.getUserByRole(TestingSpecs.getEnvironment(), UserRoles.RestUser.getRole()).getUuid()));

    private static ChainBuilder getContactSupervisorUUID =
            exec(session -> session.set("contactSupervisorUUID",
                    runningConfiguration.getUserByRole(TestingSpecs.getEnvironment(), UserRoles.ContactSupervisor.getRole()).getUuid()));

    private static ChainBuilder setCaseGUID =
            exec(session -> session.set("caseGUID", UUID.randomUUID()));

    private static ChainBuilder setClincalCourseUUID =
            exec(session -> session.set("clinicalCourseUUID", UUID.randomUUID()));

    private static ChainBuilder setHealthConditionUUID_forCase =
            exec(session -> session.set("healthConditionUUID_forCase", UUID.randomUUID()));

    private static ChainBuilder setHealthConditionUUID_forContact =
            exec(session -> session.set("healthConditionUUID_forNewContact", UUID.randomUUID()));

    private static ChainBuilder setHospitalizationUUID =
            exec(session -> session.set("hospitalizationUUID", UUID.randomUUID()));

    private static ChainBuilder setTherapyUUID =
            exec(session -> session.set("therapyUUID", UUID.randomUUID()));

    private static ChainBuilder setSymptomsUUID =
            exec(session -> session.set("symptomsUUID", UUID.randomUUID()));

    private static ChainBuilder setEpiDataUUID_forCase =
            exec(session -> session.set("epiDataUUID_forCase", UUID.randomUUID()));

    private static ChainBuilder setEpiDataUUID_forContact =
            exec(session -> session.set("epiDataUUID_forNewContact", UUID.randomUUID()));

    private static ChainBuilder setPortHealthInfoUUID =
            exec(session -> session.set("portHealthInfoUUID", UUID.randomUUID()));

    private static ChainBuilder getHealthFacilityUUID =
            exec(session -> session.set("healthFacilityUUID", environmentManager.getHealthFacilityUUID(
                    RegionsValues.VoreingestellteBundeslander.getName(),
                    HealthFacilityValues.StandardEinrichtung.getName())));

    private static ChainBuilder getLaboratoryUUID =
            exec(session -> session.set("laboratoryUUID", environmentManager.getLaboratoryUUID(
                    RegionsValues.VoreingestellteBundeslander.getName(),
                    LaboratoryValues.VOREINGESTELLTES_LABOR.getCaptionEnglish())));

    private static ChainBuilder setMaternalHistoryUUID =
            exec(session -> session.set("maternalHistoryUUID", UUID.randomUUID()));

    private static ChainBuilder setContactGUID =
            exec(session -> session.set("contactGUID", UUID.randomUUID()));

    private static ChainBuilder setTaskGUID =
            exec(session -> session.set("taskGUID", UUID.randomUUID()));

    private static ChainBuilder setEventGUID =
            exec(session -> session.set("eventGUID", UUID.randomUUID()));

    private static ChainBuilder setEventParticipantGUID =
            exec(session -> session.set("eventParticipantGUID", UUID.randomUUID()));

    private static ChainBuilder setEventLocationUUID =
            exec(session -> session.set("eventLocationGUID", UUID.randomUUID()));

    private static ChainBuilder setSampleGUID =
            exec(session -> session.set("sampleGUID", UUID.randomUUID()));



    public static ChainBuilder setupData(){
        return exec(setTimeStamp, setPersonGUID, setImmunizationGUID, setReportDate, getRegionUUID, getDistrictUUID,
                getCommunityUUID, getNationalUserUUID, setCaseGUID, setClincalCourseUUID, setHealthConditionUUID_forCase, setPortHealthInfoUUID,
                getSurveillanceUserUUID, setHospitalizationUUID, setTherapyUUID, setSymptomsUUID, setEpiDataUUID_forCase, getHealthFacilityUUID,
                setMaternalHistoryUUID, setContactGUID, setTaskGUID, getContactSupervisorUUID, getRestUserUUID, setEventGUID, setEventLocationUUID,
                setEpiDataUUID_forContact, setHealthConditionUUID_forContact, setSampleGUID, getLaboratoryUUID, setEventParticipantGUID);
    }
}
