package org.sormas.e2etests.entities.pojo.demis.labNotificationsChunks;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;

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
