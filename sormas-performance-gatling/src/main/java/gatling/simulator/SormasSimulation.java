package gatling.simulator;

import gatling.tests.GET.allUUIDS.GetAllUUIDS;
import gatling.tests.GET.allUUIDS.GetSpecificEntity;
import gatling.tests.GET.entity.CreateEntity;
import gatling.utils.SetupData;
import gatling.utils.TestingSpecs;
import io.gatling.core.scenario.Scenario;
import io.gatling.javaapi.core.ScenarioBuilder;
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

    ScenarioBuilder scenarioBuilder = scenario("Sormas tests")
            .exec(
                    GetAllUUIDS.getAllUUIDSTests(),
                    GetSpecificEntity.getEntityByUUIDTests(),
                    SetupData.setupData(),
                    CreateEntity.createEntityTests()
            );

    HttpProtocolBuilder httpProtocol =
            http.baseUrl(BASE_URL)
            .header("Content-Type", "application/json")
            .header("Accept-Type", "application/json")
            .header("Accept-Encoding", "gzip")
            .basicAuth(USERNAME, USER_PASSWORD);

    {
        setUp(scenarioBuilder.
                        injectOpen(rampUsers(USERS).
                                during(Duration.ofSeconds(DURATION_SECONDS)))).
                protocols(httpProtocol);
    }
}