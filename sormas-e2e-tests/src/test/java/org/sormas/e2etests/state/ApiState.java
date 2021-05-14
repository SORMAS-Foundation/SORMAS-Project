package org.sormas.e2etests.state;

import cucumber.runtime.java.guice.ScenarioScoped;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;

@ScenarioScoped
@Getter
@Setter
public class ApiState {
  Response response;
}
