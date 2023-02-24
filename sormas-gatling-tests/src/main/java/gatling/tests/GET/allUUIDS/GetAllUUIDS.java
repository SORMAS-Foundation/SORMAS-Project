package gatling.tests.GET.allUUIDS;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.status;

public class CountryUUIDS {

    public static ScenarioBuilder getScenario() {
        ChainBuilder getUUIDS = exec(
                http("Get countries UUIDS")
                        .get("countries/uuids")
                        .check(status().shouldBe(200)));
        return scenario("Get countries UUIDS").exec(getUUIDS);
    }
}
