package org.sormas.e2etests.pojo.api.cases;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder(toBuilder = true, builderClassName = "Builder")
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
@NonNull
public class ContactToCase {
  public String uuid;
  public String caption;
  public ContactName contactName;
  public CaseName caseName;
  public String captionAlwaysWithUuid;
}
