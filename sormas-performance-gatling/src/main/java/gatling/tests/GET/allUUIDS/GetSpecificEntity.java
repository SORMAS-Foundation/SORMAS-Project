package gatling.tests.GET.allUUIDS;

import io.gatling.javaapi.core.ChainBuilder;

import static gatling.constants.ApiPaths.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class GetSpecificEntity {

    private static ChainBuilder getCaseByUUID = exec(
            http("Get case by UUID")
                    .get(cases + "/" + "#{saved_case_uuid}")
                    .check(bodyString().exists())
                    .check(status().shouldBe(200), bodyString().exists()));

    private static ChainBuilder getContactByUUID = exec(
            http("Get contact by UUID")
                    .get(contacts + "/" + "#{saved_contact_uuid}")
                    .check(bodyString().exists())
                    .check(status().shouldBe(200), bodyString().exists()));

    private static ChainBuilder getEventParticipantByUUID = exec(
            http("Get event participant by UUID")
                    .get(eventparticipants + "/" + "#{saved_eventparticipant_uuid}")
                    .check(bodyString().exists())
                    .check(status().shouldBe(200), bodyString().exists()));

    private static ChainBuilder getEventByUUID = exec(
            http("Get event by UUID")
                    .get(events + "/" + "#{saved_event_uuid}")
                    .check(bodyString().exists())
                    .check(status().shouldBe(200), bodyString().exists()));

    private static ChainBuilder getImmunizationByUUID = exec(
            http("Get immunization by UUID")
                    .get(immunizations + "/" + "#{saved_immunization_uuid}")
                    .check(bodyString().exists())
                    .check(status().shouldBe(200), bodyString().exists()));

    private static ChainBuilder getPersonByUUID = exec(
            http("Get person by UUID")
                    .get(persons + "/" + "#{saved_person_uuid}")
                    .check(bodyString().exists())
                    .check(status().shouldBe(200), bodyString().exists()));

    private static ChainBuilder getSampleByUUID = exec(
            http("Get sample by UUID")
                    .get(samples + "/" + "#{saved_sample_uuid}")
                    .check(bodyString().exists())
                    .check(status().shouldBe(200), bodyString().exists()));

    private static ChainBuilder getTaskByUUID = exec(
            http("Get task by UUID")
                    .get(tasks + "/" + "#{saved_task_uuid}")
                    .check(bodyString().exists())
                    .check(status().shouldBe(200), bodyString().exists()));

    private static ChainBuilder getUserByUUID = exec(
            http("Get user by UUID")
                    .get(users + "/" + "#{saved_user_uuid}")
                    .check(bodyString().exists())
                    .check(status().shouldBe(200), bodyString().exists()));


        //Tests collector
        public static ChainBuilder getEntityByUUIDTests(){
            return group("Get entity by UUID").on(exec(getCaseByUUID, getContactByUUID, getEventByUUID, getEventParticipantByUUID, getImmunizationByUUID,
                    getPersonByUUID, getSampleByUUID, getTaskByUUID, getUserByUUID));
        }

}
