package de.symeda.sormas.backend.externaljournal;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

import de.symeda.sormas.backend.common.ConfigFacadeEjb;

@Stateless
@LocalBean
public class ExternalJournalService {

    private static final String SYMPTOM_JOURNAL_KEY = "symptomJournal";
    private static final String PATIENT_DIARY_KEY = "patientDiary";
    private static final Cache<String, String> authTokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(6, TimeUnit.HOURS)
            .build();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @EJB
    private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

    public String getSymptomJournalAuthToken() {
        try {
            return authTokenCache.get(SYMPTOM_JOURNAL_KEY, this::getSymptomJournalAuthTokenInternal);
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    private String getSymptomJournalAuthTokenInternal() {
        String authenticationUrl = configFacade.getSymptomJournalConfig().getAuthUrl();
        String clientId = configFacade.getSymptomJournalConfig().getClientId();
        String secret = configFacade.getSymptomJournalConfig().getSecret();

        if (StringUtils.isBlank(authenticationUrl)) {
            throw new IllegalArgumentException("Property interface.symptomjournal.authurl is not defined");
        }
        if (StringUtils.isBlank(clientId)) {
            throw new IllegalArgumentException("Property interface.symptomjournal.clientid is not defined");
        }
        if (StringUtils.isBlank(secret)) {
            throw new IllegalArgumentException("Property interface.symptomjournal.secret is not defined");
        }
        try {
            Client client = ClientBuilder.newClient();
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(clientId, secret);
            client.register(feature);
            WebTarget webTarget = client.target(authenticationUrl);
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.post(Entity.json(""));
            String responseJson = response.readEntity(String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(responseJson, JsonNode.class);
            return node.get("auth").textValue();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public String getPatientDiaryAuthToken() {
        try {
            return authTokenCache.get(PATIENT_DIARY_KEY, this::getPatientDiaryAuthTokenInternal);
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public String getPatientDiaryAuthTokenInternal() {
        String authenticationUrl = configFacade.getPatientDiaryConfig().getAuthUrl();
        String email = configFacade.getPatientDiaryConfig().getEmail();
        String pass = configFacade.getPatientDiaryConfig().getPassword();

        if (StringUtils.isBlank(authenticationUrl)) {
            throw new IllegalArgumentException("Property interface.patientdiary.authurl is not defined");
        }
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Property interface.patientdiary.email is not defined");
        }
        if (StringUtils.isBlank(pass)) {
            throw new IllegalArgumentException("Property interface.patientdiary.password is not defined");
        }

        try {
            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target(authenticationUrl);
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.post(Entity.json(ImmutableMap.of(
                    "email", email,
                    "password", pass)
            ));
            String responseJson = response.readEntity(String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(responseJson, JsonNode.class);
            boolean success = node.get("success").booleanValue();
            if (!success) {
                throw new ExternalJournalLoginException("Could not log in to patient diary with provided email and password");
            }
            return node.get("token").textValue();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
