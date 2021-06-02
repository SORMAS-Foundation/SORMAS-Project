package org.sormas.e2etests.state;

import cucumber.runtime.java.guice.ScenarioScoped;
import lombok.Getter;
import lombok.Setter;

@ScenarioScoped
@Getter
@Setter
public class BodyResources {
  String body;
  String personUUID;
}
