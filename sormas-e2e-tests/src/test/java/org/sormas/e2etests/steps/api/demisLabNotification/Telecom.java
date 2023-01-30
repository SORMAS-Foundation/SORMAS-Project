package org.sormas.e2etests.steps.api.demisLabNotification;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Telecom {
  public String system;
  public String value;
  public String use;
  public ArrayList<Extension> extension;
}
