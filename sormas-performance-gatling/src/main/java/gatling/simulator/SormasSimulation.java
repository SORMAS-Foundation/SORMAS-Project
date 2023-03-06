package gatling.simulator;

import gatling.envconfig.manager.RunningConfiguration;
import gatling.tests.GET.allUUIDS.GetAllUUIDS;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SormasSimulation extends Simulation {

    private static RunningConfiguration runningConfiguration = new RunningConfiguration();

    public static String BASE_URL =
            runningConfiguration.getEnvironmentUrlForMarket("performance") + "/sormas-rest/";
    public static final int USERS = 1;
    public static final int DURATION_MIN = 10;

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .header("Content-Type", "application/json")
            .header("Accept-Type", "application/json")
            .header("Accept-Encoding", "gzip")
            .basicAuth("RestAuto", "QTj]qF90U~-CMLa/");

    //this is the test runner, collects all tests that we want to trigger
    {
        setUp(GetAllUUIDS.getScenario().injectOpen(rampUsers(USERS)
                .during(Duration.ofSeconds(DURATION_MIN))))
                .protocols(httpProtocol);
    }
}