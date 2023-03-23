package gatling.tests.GET.allUUIDS;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import static gatling.constants.ApiPaths.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

import static io.gatling.javaapi.http.HttpDsl.status;

public class GetAllUUIDS {

    static String theJson = "";

    public static ScenarioBuilder getScenario() {

        ChainBuilder getActionsUUIDS = exec(
                http("Get actions UUIDS")
                        .get(actions + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getAdditionaltestsUUIDS = exec(
                http("Get additionaltests UUIDS")
                        .get(additionaltests + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getAggregatereportsUUIDS = exec(
                http("Get aggregatereports UUIDS")
                        .get(aggregatereports + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getAreasUUIDS = exec(
                http("Get areas UUIDS")
                        .get(areas + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getCampaignsUUIDS = exec(
                http("Get campaigns UUIDS")
                        .get(campaigns + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getCampaignsFromDataUUIDS = exec(
                http("Get campaignFormData UUIDS")
                        .get(campaignFormData + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getCampaignsFromMetaUUIDS = exec(
                http("Get campaignFormMeta UUIDS")
                        .get(campaignFormMeta + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getCasesUUIDS = exec(
                http("Get cases UUIDS")
                        .get(cases + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getClinicalvisitsUUIDS = exec(
                http("Get clinicalvisits UUIDS")
                        .get(clinicalvisits + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getCommunitiesUUIDS = exec(
                http("Get communities UUIDS")
                        .get(communities + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getContactsUUIDS = exec(
                http("Get contacts UUIDS")
                        .get(contacts + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getContinentsUUIDS = exec(
                http("Get continents UUIDS")
                        .get(continents + "/" + uuids)
                        .check(bodyString().saveAs("RESPONSE_DATA"))
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    theJson = session.getString("RESPONSE_DATA");
                    return session;
                });

        ChainBuilder getCountryUUIDS = exec(
                http("Get countries UUIDS")
                        .get(countries + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()))
                .exec(session -> {
                    System.out.println("Collected json --->>>>>>>>>>");
                    System.out.println(theJson);
                    return session;
                });

        ChainBuilder getCustomizableenumvaluesUUIDS = exec(
                http("Get customizableenumvalues UUIDS")
                        .get(customizableenumvalues + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getDiseaseconfigurationsUUIDS = exec(
                http("Get diseaseconfigurations UUIDS")
                        .get(diseaseconfigurations + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getDistrictsUUIDS = exec(
                http("Get districts UUIDS")
                        .get(districts + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getEventsUUIDS = exec(
                http("Get events UUIDS")
                        .get(events + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getEventparticipantsUUIDS = exec(
                http("Get eventparticipants UUIDS")
                        .get(eventparticipants + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getFacilitiesUUIDS = exec(
                http("Get facilities UUIDS")
                        .get(facilities + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getFeatureconfigurationsUUIDS = exec(
                http("Get featureconfigurations UUIDS")
                        .get(featureconfigurations + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getImmunizationsUUIDS = exec(
                http("Get immunizations UUIDS")
                        .get(immunizations + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getPathogentestsUUIDS = exec(
                http("Get pathogentests UUIDS")
                        .get(pathogentests + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getPersonsUUIDS = exec(
                http("Get persons UUIDS")
                        .get(persons + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getPointsofentryUUIDS = exec(
                http("Get pointsofentry UUIDS")
                        .get(pointsofentry + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getPrescriptionsUUIDS = exec(
                http("Get prescriptions UUIDS")
                        .get(prescriptions + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getRegionsUUIDS = exec(
                http("Get regions UUIDS")
                        .get(regions + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getOutbreaksUUIDS = exec(
                http("Get outbreaks UUIDS")
                        .get(outbreaks + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getSamplesUUIDS = exec(
                http("Get samples UUIDS")
                        .get(samples + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getSubcontinentsUUIDS = exec(
                http("Get subcontinents UUIDS")
                        .get(subcontinents + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getTasksUUIDS = exec(
                http("Get tasks UUIDS")
                        .get(tasks + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getTreatmentsUUIDS = exec(
                http("Get treatments UUIDS")
                        .get(treatments + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getUsersUUIDS = exec(
                http("Get users UUIDS")
                        .get(users + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getUsersRolesUUIDS = exec(
                http("Get userroles UUIDS")
                        .get(userroles + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getVisitsUUIDS = exec(
                http("Get visits UUIDS")
                        .get(visits + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getWeeklyreportsUUIDS = exec(
                http("Get weeklyreports UUIDS")
                        .get(weeklyreports + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));
        
        return scenario("Get existing UUIDS").exec(getCountryUUIDS, getContinentsUUIDS, getContactsUUIDS, getActionsUUIDS, getAdditionaltestsUUIDS,
                getAggregatereportsUUIDS, getAreasUUIDS, getCampaignsUUIDS, getCampaignsFromDataUUIDS, getCampaignsFromMetaUUIDS, getCasesUUIDS,
                getClinicalvisitsUUIDS, getCommunitiesUUIDS, getCustomizableenumvaluesUUIDS, getDiseaseconfigurationsUUIDS, getDistrictsUUIDS, getEventparticipantsUUIDS,
                getOutbreaksUUIDS, getPathogentestsUUIDS, getPrescriptionsUUIDS, getTasksUUIDS, getSamplesUUIDS, getVisitsUUIDS, getEventsUUIDS, getFacilitiesUUIDS,
                getPersonsUUIDS, getImmunizationsUUIDS, getFeatureconfigurationsUUIDS, getPointsofentryUUIDS, getRegionsUUIDS, getSubcontinentsUUIDS, getTreatmentsUUIDS,
                getUsersUUIDS, getUsersRolesUUIDS, getWeeklyreportsUUIDS);
    }
}
