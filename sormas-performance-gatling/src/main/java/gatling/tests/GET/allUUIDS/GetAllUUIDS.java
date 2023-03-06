package gatling.tests.GET.allUUIDS;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.jsonpath.GreaterOperator;

import static gatling.constants.ApiPaths.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

import static io.gatling.javaapi.http.HttpDsl.status;

public class GetAllUUIDS {

    public static ScenarioBuilder getScenario() {

        ChainBuilder getCountryUUIDS = exec(
                http("Get countries UUIDS")
                        .get(countries + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getContinentsUUIDS = exec(
                http("Get continents UUIDS")
                        .get(continents + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder getContactsUUIDS = exec(
                http("Get contacts UUIDS")
                        .get(contacts + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));

        ChainBuilder myfail = exec(
                http("my failing test")
                        .get("dsafsd" + "/" + uuids)
                        .check(status().shouldBe(200), bodyString().exists()));


        return scenario("Get all UUIDS").exec(getCountryUUIDS, getContinentsUUIDS, getContactsUUIDS, myfail);
    }
}
