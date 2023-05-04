package gatling.tests.GET.allUUIDS;

import io.gatling.javaapi.core.ChainBuilder;
import static gatling.constants.ApiPaths.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

import static io.gatling.javaapi.http.HttpDsl.status;

public class GetAllUUIDS {


        private static ChainBuilder getActionsUUIDS = exec(
                http("Get actions UUIDS")
                        .get(actions + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_action_uuid");
                    return session;
                });

        private static ChainBuilder getAdditionaltestsUUIDS = exec(
                http("Get additionaltests UUIDS")
                        .get(additionaltests + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_additionaltests_uuid");
                    return session;
                });

        private static ChainBuilder getAggregatereportsUUIDS = exec(
                http("Get aggregatereports UUIDS")
                        .get(aggregatereports + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_aggregatereports_uuid");
                    return session;
                });

        private static ChainBuilder getAreasUUIDS = exec(
                http("Get areas UUIDS")
                        .get(areas + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_area_uuid");
                    return session;
                });

        private static ChainBuilder getCampaignsUUIDS = exec(
                http("Get campaigns UUIDS")
                        .get(campaigns + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_campaign_uuid");
                    return session;
                });

        private static ChainBuilder getCampaignsFromDataUUIDS = exec(
                http("Get campaignFormData UUIDS")
                        .get(campaignFormData + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_campaignFromData_uuid");
                    return session;
                });

        private static ChainBuilder getCampaignsFromMetaUUIDS = exec(
                http("Get campaignFormMeta UUIDS")
                        .get(campaignFormMeta + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_campaignFromMeta_uuid");
                    return session;
                });

        private static ChainBuilder getCasesUUIDS = exec(
                http("Get cases UUIDS")
                        .get(cases + "/" + uuids)
                        .check(jsonPath("$[0]").saveAs("saved_case_uuid"))
                        .check(status().shouldBe(200), bodyString().exists()));

        private static ChainBuilder getClinicalvisitsUUIDS = exec(
                http("Get clinicalvisits UUIDS")
                        .get(clinicalvisits + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_clinicalvisit_uuid");
                    return session;
                });

        private static ChainBuilder getCommunitiesUUIDS = exec(
                http("Get communities UUIDS")
                        .get(communities + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_community_uuid");
                    return session;
                });

        private static ChainBuilder getContactsUUIDS = exec(
                http("Get contacts UUIDS")
                        .get(contacts + "/" + uuids)
                        .check(jsonPath("$[0]").saveAs("saved_contact_uuid"))
                        .check(status().shouldBe(200), bodyString().exists()));

        private static ChainBuilder getContinentsUUIDS = exec(
                http("Get continents UUIDS")
                        .get(continents + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_continent_uuid");
                    return session;
                });

        private static ChainBuilder getCountryUUIDS = exec(
                http("Get countries UUIDS")
                        .get(countries + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_country_uuid");
                    return session;
                });

        private static ChainBuilder getCustomizableenumvaluesUUIDS = exec(
                http("Get customizableenumvalues UUIDS")
                        .get(customizableenumvalues + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_customizableEnumValue_uuid");
                    return session;
                });

        private static ChainBuilder getDiseaseconfigurationsUUIDS = exec(
                http("Get diseaseconfigurations UUIDS")
                        .get(diseaseconfigurations + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_diseaseconfiguration_uuid");
                    return session;
                });

        private static ChainBuilder getDistrictsUUIDS = exec(
                http("Get districts UUIDS")
                        .get(districts + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_district_uuid");
                    return session;
                });

        private static ChainBuilder getEventsUUIDS = exec(
                http("Get events UUIDS")
                        .get(events + "/" + uuids)
                        .check(jsonPath("$[0]").saveAs("saved_event_uuid"))
                        .check(status().shouldBe(200), bodyString().exists()));

        private static ChainBuilder getEventparticipantsUUIDS = exec(
                http("Get eventparticipants UUIDS")
                        .get(eventparticipants + "/" + uuids)
                        .check(jsonPath("$[0]").saveAs("saved_eventparticipant_uuid"))
                        .check(status().shouldBe(200), bodyString().exists()));

        private static ChainBuilder getFacilitiesUUIDS = exec(
                http("Get facilities UUIDS")
                        .get(facilities + "/" + uuids)
                        .check(jsonPath("$[0]").saveAs("saved_facility_uuid"))
                        .check(status().shouldBe(200), bodyString().exists()));

        private static ChainBuilder getFeatureconfigurationsUUIDS = exec(
                http("Get featureconfigurations UUIDS")
                        .get(featureconfigurations + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_featureconfiguration_uuid");
                    return session;
                });

        private static ChainBuilder getImmunizationsUUIDS = exec(
                http("Get immunizations UUIDS")
                        .get(immunizations + "/" + uuids)
                        .check(jsonPath("$[0]").saveAs("saved_immunization_uuid"))
                        .check(status().shouldBe(200), bodyString().exists()));

        private static ChainBuilder getPathogentestsUUIDS = exec(
                http("Get pathogentests UUIDS")
                        .get(pathogentests + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_pathogentest_uuid");
                    return session;
                });

        private static ChainBuilder getPersonsUUIDS = exec(
                http("Get persons UUIDS")
                        .get(persons + "/" + uuids)
                        .check(jsonPath("$[0]").saveAs("saved_person_uuid"))
                        .check(status().shouldBe(200), bodyString().exists()));

        private static ChainBuilder getPointsofentryUUIDS = exec(
                http("Get pointsofentry UUIDS")
                        .get(pointsofentry + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                   jsonPath("$[0]").saveAs("saved_pointofentry_uuid");
                    return session;
                });

        private static ChainBuilder getPrescriptionsUUIDS = exec(
                http("Get prescriptions UUIDS")
                        .get(prescriptions + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_prescription_uuid");
                    return session;
                });

        private static ChainBuilder getRegionsUUIDS = exec(
                http("Get regions UUIDS")
                        .get(regions + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_region_uuid");
                    return session;
                });

        private static ChainBuilder getOutbreaksUUIDS = exec(
                http("Get outbreaks UUIDS")
                        .get(outbreaks + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_outbreak_uuid");
                    return session;
                });

        private static ChainBuilder getSamplesUUIDS = exec(
                http("Get samples UUIDS")
                        .get(samples + "/" + uuids)
                        .check(jsonPath("$[0]").saveAs("saved_sample_uuid"))
                        .check(status().shouldBe(200), bodyString().exists()));

        private static ChainBuilder getSubcontinentsUUIDS = exec(
                http("Get subcontinents UUIDS")
                        .get(subcontinents + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_subcontinent_uuid");
                    return session;
                });

        private static ChainBuilder getTasksUUIDS = exec(
                http("Get tasks UUIDS")
                        .get(tasks + "/" + uuids)
                        .check(jsonPath("$[0]").saveAs("saved_task_uuid"))
                        .check(status().shouldBe(200), bodyString().exists()));

        private static ChainBuilder getTreatmentsUUIDS = exec(
                http("Get treatments UUIDS")
                        .get(treatments + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_treatment_uuid");
                    return session;
                });

        private static ChainBuilder getUsersUUIDS = exec(
                http("Get users UUIDS")
                        .get(users + "/" + uuids)
                        .check(jsonPath("$[0]").saveAs("saved_user_uuid"))
                        .check(status().shouldBe(200), bodyString().exists()));
        private static ChainBuilder getUsersRolesUUIDS = exec(
                http("Get userroles UUIDS")
                        .get(userroles + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_userrole_uuid");
                    return session;
                });

        private static ChainBuilder getVisitsUUIDS = exec(
                http("Get visits UUIDS")
                        .get(visits + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_visit_uuid");
                    return session;
                });

        private static ChainBuilder getWeeklyreportsUUIDS = exec(
                http("Get weeklyreports UUIDS")
                        .get(weeklyreports + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    jsonPath("$[0]").saveAs("saved_weeklyreport_uuid");
                    return session;
                });

        //Tests collector
        public static ChainBuilder getAllUUIDSTests(){
            return group("Get existing UUIDS").on(exec(getCountryUUIDS, getContinentsUUIDS, getContactsUUIDS, getActionsUUIDS, getAdditionaltestsUUIDS,
                    getAggregatereportsUUIDS, getAreasUUIDS, getCampaignsUUIDS, getCampaignsFromDataUUIDS, getCampaignsFromMetaUUIDS, getCasesUUIDS,
                    getClinicalvisitsUUIDS, getCommunitiesUUIDS, getCustomizableenumvaluesUUIDS, getDiseaseconfigurationsUUIDS, getDistrictsUUIDS, getEventparticipantsUUIDS,
                    getOutbreaksUUIDS, getPathogentestsUUIDS, getPrescriptionsUUIDS, getTasksUUIDS, getSamplesUUIDS, getVisitsUUIDS, getEventsUUIDS, getFacilitiesUUIDS,
                    getPersonsUUIDS, getImmunizationsUUIDS, getFeatureconfigurationsUUIDS, getPointsofentryUUIDS, getRegionsUUIDS, getSubcontinentsUUIDS, getTreatmentsUUIDS,
                    getUsersUUIDS, getUsersRolesUUIDS, getWeeklyreportsUUIDS));
        }

}
