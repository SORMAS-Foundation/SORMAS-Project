package de.symeda.sormas.app;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.sample.Sample;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Mate Strysewske on 13.06.2017.
 */
public class TestEnvironmentInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) {
        Response response = null;
        String responseString;
        final URI uri = chain.request().url().uri();

        // API version
        if (uri.getPath().endsWith("version")) {
            responseString = "\""+InfoProvider.getVersion()+"\"";
        } else if (uri.getPath().contains("persons/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("persons/push")) {
            responseString = "0";
        } else if (uri.getPath().contains("cases/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("cases/push")) {
            responseString = "0";
        } else if (uri.getPath().contains("contacts/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("contacts/push")) {
            responseString = "0";
        } else if (uri.getPath().contains("events/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("events/push")) {
            responseString = "0";
        } else if (uri.getPath().contains("eventparticipants/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("eventparticipants/push")) {
            responseString = "0";
        } else if (uri.getPath().contains("visits/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("visits/push")) {
            responseString = "0";
        } else if (uri.getPath().contains("samples/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("samples/push")) {
            responseString = "0";
        } else if (uri.getPath().contains("sampletests/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("sampletests/push")) {
            responseString = "0";
        } else if (uri.getPath().contains("tasks/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("tasks/push")) {
            responseString = "0";
        } else if (uri.getPath().contains("regions/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("districts/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("communities/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("facilities/all")) {
            responseString = "[]";
        } else if (uri.getPath().contains("users/all")) {
            responseString = "[]";
        } else {
            responseString = "";
        }

        response = new Response.Builder()
                .code(200)
                .message("OK")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseString))
                .addHeader("content-type", "application/json")
                .build();

        return response;
    }

}
