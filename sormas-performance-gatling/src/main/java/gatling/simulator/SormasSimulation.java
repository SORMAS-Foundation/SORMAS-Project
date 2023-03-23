package gatling.simulator;

import gatling.tests.GET.allUUIDS.GetAllUUIDS;
import gatling.utils.TestingSpecs;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SormasSimulation extends Simulation {

    public static final String BASE_URL = TestingSpecs.getTestingEnvironment();

    public static final int USERS = TestingSpecs.getNumberOfUsers();
    public static final int DURATION_SECONDS = TestingSpecs.getExecutionTime();
    public static final String USERNAME = TestingSpecs.getUsername();
    public static final String USER_PASSWORD = TestingSpecs.getPassword();

    HttpProtocolBuilder httpProtocol =
            http.baseUrl(BASE_URL)
            .header("Content-Type", "application/json")
            .header("Accept-Type", "application/json")
            .header("Accept-Encoding", "gzip")
            .basicAuth(USERNAME, USER_PASSWORD);

    //this is the test runner, collects all tests that we want to trigger
    {
        setUp(GetAllUUIDS.getScenario().injectOpen(rampUsers(USERS)
                .during(Duration.ofSeconds(DURATION_SECONDS)))
        //more tests here
        ).protocols(httpProtocol);
    }
}