package gatling.tests.GET.entity;

import io.gatling.javaapi.core.ChainBuilder;

import static gatling.constants.ApiPaths.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class CreateEntity {

    private final static String OK_MESSAGE = "[{\"statusCode\":200}]";

    private static ChainBuilder createPerson =
            exec(http("Create new Person")
                    .post(persons + "/" + push)
                    .body(ElFileBody("jsons/createPerson.json"))
                    .check(bodyString().is(OK_MESSAGE))
                    .check(status().shouldBe(200)));

    private static ChainBuilder createImmunization =
            exec(http("Create new Immunization")
                    .post(immunizations + "/" + push)
                    .body(ElFileBody("jsons/createImmunization.json"))
                    .check(bodyString().is(OK_MESSAGE))
                    .check(status().shouldBe(200)));

    private static ChainBuilder createCase =
            exec(http("Create new Case")
                    .post(cases + "/" + push)
                    .body(ElFileBody("jsons/createCase.json"))
                    .check(bodyString().is(OK_MESSAGE))
                    .check(status().shouldBe(200)));

    private static ChainBuilder createContact =
            exec(http("Create new Contact")
                    .post(contacts + "/" + push)
                    .body(ElFileBody("jsons/createContact.json"))
                    .check(bodyString().is(OK_MESSAGE))
                    .check(status().shouldBe(200)));

    private static ChainBuilder createTask =
            exec(http("Create new Task")
                    .post(tasks + "/" + push)
                    .body(ElFileBody("jsons/createTask.json"))
                    .check(bodyString().is(OK_MESSAGE))
                    .check(status().shouldBe(200)));

    private static ChainBuilder createEvent =
            exec(http("Create new Event")
                    .post(events + "/" + push)
                    .body(ElFileBody("jsons/createEvent.json"))
                    .check(bodyString().is(OK_MESSAGE))
                    .check(status().shouldBe(200)));

    private static ChainBuilder createEventParticipant =
            exec(http("Create new Event Participant")
                    .post(eventparticipants + "/" + push)
                    .body(ElFileBody("jsons/createEventParticipant.json"))
                    .check(bodyString().is(OK_MESSAGE))
                    .check(status().shouldBe(200)));

    private static ChainBuilder createSample =
            exec(http("Create new Sample")
                    .post(samples + "/" + push)
                    .body(ElFileBody("jsons/createSample.json"))
                    .check(bodyString().is(OK_MESSAGE))
                    .check(status().shouldBe(200)));


    //Tests collector
    public static ChainBuilder createEntityTests(){
        return group("Create entities").on(exec(createPerson, createImmunization, createCase, createContact, createTask,
                createEvent, createEventParticipant, createSample));
    }

}
