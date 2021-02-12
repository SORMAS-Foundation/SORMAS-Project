package de.symeda.sormas.backend.externaljournal;

import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.EMAIL_TAKEN;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.INVALID_BIRTHDATE;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.INVALID_EMAIL;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.INVALID_PHONE;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.NO_PHONE_OR_EMAIL;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.PHONE_TAKEN;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.externaljournal.ExternalJournalValidation;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryIdatId;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonData;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonDto;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryQueryResponse;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryRegisterResult;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.JournalPersonDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.util.ClientHelper;

/**
 * This service provides methods for communicating with external symptom journals.
 * <br>
 * Currently, 2 symptom journals are supported: PIA and CLIMEDO.
 * <br>
 * Methods containing <code>symptomJournal</code> refer to PIA.
 * <br>
 * Methods containing <code>patientDiary</code> refer to CLIMEDO.
 */
@Stateless
@LocalBean
public class ExternalJournalService {

	private static final String PATIENT_DIARY_KEY = "patientDiary";
	private static final Cache<String, String> authTokenCache = CacheBuilder.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).build();
	private static final int NOT_FOUND_STATUS = 404;
	private static final String EMAIL_QUERY_PARAM = "Email";
	private static final String MOBILE_PHONE_QUERY_PARAM = "Mobile phone";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;

	/**
	 * Retrieves a token used for authenticating in the symptom journal.
	 * The token will not be cached since it's only used once when opening the symptom journal
	 *
	 * @return the authentication token
	 */
	public String getSymptomJournalAuthToken() {
		return getSymptomJournalAuthTokenInternal();
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
			Client client = ClientHelper.newBuilderWithProxy().build();
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

	/**
	 * Retrieves a token used for authenticating in the patient diary. The token will be cached.
	 * 
	 * @return the authentication token
	 */
	public String getPatientDiaryAuthToken() {
		try {
			return authTokenCache.get(PATIENT_DIARY_KEY, this::getPatientDiaryAuthTokenInternal);
		} catch (ExecutionException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	private String getPatientDiaryAuthTokenInternal() {
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
			Client client = ClientHelper.newBuilderWithProxy().build();
			WebTarget webTarget = client.target(authenticationUrl);
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			Response response = invocationBuilder.post(Entity.json(ImmutableMap.of("email", email, "password", pass)));

			String responseJson = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readValue(responseJson, JsonNode.class);
			boolean success = node.get("success").booleanValue();
			if (!success) {
				throw new ExternalJournalException("Could not log in to patient diary with provided email and password");
			}
			return node.get("token").textValue();
		} catch (IOException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * Notify external journals that a followUpUntilDate has been updated
	 *
	 * @param contact
	 *            a contact assigned to a person already available in the external journal
	 * @param previousFollowUpUntilDate
	 *            the follow-up end date before the update
	 */
	public void notifyExternalJournalFollowUpUntilUpdate(ContactDto contact, Date previousFollowUpUntilDate) {
		PersonDto person = personFacade.getPersonByUuid(contact.getPerson().getUuid());
		if (person.isEnrolledInExternalJournal()) {
			if (contact.getFollowUpUntil().after(previousFollowUpUntilDate)) {
				if (configFacade.getSymptomJournalConfig().getUrl() != null) {
					notifySymptomJournal(contact.getPerson().getUuid());
				}
				if (configFacade.getPatientDiaryConfig().getUrl() != null) {
					notifyPatientDiary(contact.getPerson().getUuid());
				}
			}
		}
	}

	/**
	 * Notify external journals that a person has been updated
	 * 
	 * @param existingJournalPerson
	 *            the person already available in the external journal
	 * @return true if the person data change was considered relevant for external journals, false otherwise.
	 *
	 */
	public boolean notifyExternalJournalPersonUpdate(JournalPersonDto existingJournalPerson) {
		boolean shouldNotify = shouldNotify(existingJournalPerson);
		if (shouldNotify) {
			if (configFacade.getSymptomJournalConfig().getUrl() != null) {
				notifySymptomJournal(existingJournalPerson.getUuid());
			}
			if (configFacade.getPatientDiaryConfig().getUrl() != null) {
				notifyPatientDiary(existingJournalPerson.getUuid());
			}
		}
		return shouldNotify;
	}

	/**
	 * Note: This method just checks for changes in the Person data.
	 * It can not check for Contact related data such as FollowUpUntil dates.
	 */
	private boolean shouldNotify(JournalPersonDto existingJournalPerson) {
		PersonDto detailedExistingPerson = personFacade.getPersonByUuid(existingJournalPerson.getUuid());
		if (SymptomJournalStatus.ACCEPTED.equals(detailedExistingPerson.getSymptomJournalStatus())
			|| SymptomJournalStatus.REGISTERED.equals(detailedExistingPerson.getSymptomJournalStatus())) {
			JournalPersonDto updatedJournalPerson = personFacade.getPersonForJournal(existingJournalPerson.getUuid());
			return !existingJournalPerson.equals(updatedJournalPerson);
		}
		return false;
	}

	private void notifySymptomJournal(String personUuid) {
		// agree with PIA how this should be done
	}

	private void notifyPatientDiary(String personUuid) {
		try {
			Invocation.Builder invocationBuilder = getExternalDataPersonInvocationBuilder(personUuid);
			Response response = invocationBuilder.put(Entity.json(""));
			String responseJson = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readValue(responseJson, JsonNode.class);
			boolean success = node.get("success").booleanValue();
			if (!success) {
				String message = node.get("message").textValue();
				logger.warn("Could not notify patient diary of person update: " + message);
			} else {
				logger.info("Successfully notified patient diary to update patient " + personUuid);
			}
		} catch (IOException e) {
			logger.error("Could not notify patient diary: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieves the person from the external patient diary with the given uuid
	 * 
	 * @param personUuid
	 *            the uuid of the person to be retrieved
	 * @return optional containing the person
	 */
	public Optional<PatientDiaryPersonDto> getPatientDiaryPerson(String personUuid) {
		try {
			Invocation.Builder invocationBuilder = getExternalDataPersonInvocationBuilder(personUuid);
			Response response = invocationBuilder.get();
			if (response.getStatus() == NOT_FOUND_STATUS) {
				return Optional.empty();
			}
			String responseJson = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readValue(responseJson, JsonNode.class);
			JsonNode idatData = node.get("idatData");
			PatientDiaryPersonDto personDto = mapper.treeToValue(idatData, PatientDiaryPersonDto.class);
			String endDate = node.get("endDate").textValue();
			personDto.setEndDate(endDate);
			return Optional.of(personDto);
		} catch (IOException e) {
			logger.error("Could not retrieve patient: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Attempts to register a new patient in the CLIMEDO patient diary.
	 * Sets the person symptom journal status to REGISTERED if successful.
	 *
	 * @param person
	 *            the person to register as a patient in CLIMEDO
	 * @return true if the registration was successful, false otherwise
	 */
	public PatientDiaryRegisterResult registerPatientDiaryPerson(PersonDto person) {
		try {
			Invocation.Builder invocationBuilder = getExternalDataPersonInvocationBuilder(person.getUuid());
			Response response = invocationBuilder.post(Entity.json(""));
			String responseJson = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readValue(responseJson, JsonNode.class);
			boolean success = node.get("success").booleanValue();
			String message = node.get("message").textValue();
			if (!success) {
				logger.warn("Could not create new patient diary person: " + message);
			} else {
				logger.info("Successfully registered patient " + person.getUuid() + " in patient diary.");
				person.setSymptomJournalStatus(SymptomJournalStatus.REGISTERED);
				personFacade.savePerson(person);
			}
			return new PatientDiaryRegisterResult(success, message);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return new PatientDiaryRegisterResult(false, e.getMessage());
		}
	}

	private Invocation.Builder getExternalDataPersonInvocationBuilder(String personUuid) {
		String externalDataUrl = configFacade.getPatientDiaryConfig().getProbandsUrl() + "/external-data/" + personUuid;
		Client client = ClientHelper.newBuilderWithProxy().build();
		return client.target(externalDataUrl).request(MediaType.APPLICATION_JSON).header("x-access-token", getPatientDiaryAuthToken());
	}

	/**
	 * Check whether a person has valid data in order to be registered in the patient diary.
	 * NOTE: since CLIMEDO is only used in Germany, only German numbers are considered valid at the moment
	 *
	 * @param person
	 *            the person to validate
	 * @return the result of the validation
	 */
	public ExternalJournalValidation validatePatientDiaryPerson(PersonDto person) {
		EnumSet<PatientDiaryValidationError> validationErrors = EnumSet.noneOf(PatientDiaryValidationError.class);

		String email = person.getEmailAddress();
		String phone = person.getPhone();
		boolean hasPhoneOrEmail = !StringUtils.isAllEmpty(email, phone);
		if (!hasPhoneOrEmail) {
			validationErrors.add(NO_PHONE_OR_EMAIL);
		}

		if (StringUtils.isNotEmpty(email)) {
			EmailValidator validator = EmailValidator.getInstance();
			if (!validator.isValid(email)) {
				validationErrors.add(INVALID_EMAIL);
			}
			if (!isEmailAvailable(person)) {
				validationErrors.add(EMAIL_TAKEN);
			}
		}

		if (StringUtils.isNotEmpty(phone)) {
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			try {
				Phonenumber.PhoneNumber germanNumberProto = phoneUtil.parse(phone, "DE");
				if (!phoneUtil.isValidNumber(germanNumberProto)) {
					validationErrors.add(INVALID_PHONE);
				}
				String internationalPhone = phoneUtil.format(germanNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
				if (!isPhoneAvailable(person, internationalPhone)) {
					validationErrors.add(PHONE_TAKEN);
				}
			} catch (NumberParseException e) {
				logger.warn("NumberParseException was thrown: " + e.toString());
				validationErrors.add(INVALID_PHONE);
			}
		}

		if (ObjectUtils.anyNotNull(person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY())) {
			boolean validBirthdate = ObjectUtils.allNotNull(person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY());
			if (!validBirthdate) {
				validationErrors.add(INVALID_BIRTHDATE);
			}
		}

		return new ExternalJournalValidation(validationErrors.isEmpty(), getValidationMessage(validationErrors));
	}

	private boolean isEmailAvailable(PersonDto person) {
		PatientDiaryQueryResponse response = queryPatientDiary(EMAIL_QUERY_PARAM, person.getEmailAddress())
			.orElseThrow(() -> new RuntimeException("Could not query patient diary for Email address availability"));
		boolean notUsed = response.getCount() == 0;
		boolean samePerson = response.getResults()
			.stream()
			.map(PatientDiaryPersonData::getIdatId)
			.map(PatientDiaryIdatId::getIdat)
			.map(PatientDiaryPersonDto::getPersonUUID)
			.anyMatch(uuid -> person.getUuid().equals(uuid));
		boolean sameFamily = response.getResults()
			.stream()
			.map(PatientDiaryPersonData::getIdatId)
			.map(PatientDiaryIdatId::getIdat)
			.anyMatch(patientDiaryPerson -> inSameFamily(person, patientDiaryPerson));
		return notUsed || samePerson || sameFamily;
	}

	private boolean inSameFamily(PersonDto person, PatientDiaryPersonDto patientDiaryPerson) {
		return patientDiaryPerson.getLastName().equals(person.getLastName()) && !patientDiaryPerson.getFirstName().equals(person.getFirstName());
	}

	private boolean isPhoneAvailable(PersonDto person, String phone) {
		PatientDiaryQueryResponse response = queryPatientDiary(MOBILE_PHONE_QUERY_PARAM, phone)
			.orElseThrow(() -> new RuntimeException("Could not query patient diary for phone number availability"));
		boolean notUsed = response.getCount() == 0;
		boolean samePerson = response.getResults()
			.stream()
			.map(PatientDiaryPersonData::getIdatId)
			.map(PatientDiaryIdatId::getIdat)
			.map(PatientDiaryPersonDto::getPersonUUID)
			.anyMatch(uuid -> person.getUuid().equals(uuid));
		return notUsed || samePerson;
	}

	/**
	 * Queries the CLIMEDO patients for ones matching the given property
	 *
	 * @param key
	 *            the name of the property to match
	 * @param value
	 *            the value of the property to match
	 * @return result of query
	 */
	public Optional<PatientDiaryQueryResponse> queryPatientDiary(String key, String value) {
		try {
			String probandsUrl = configFacade.getPatientDiaryConfig().getProbandsUrl() + "/probands";
			String queryParam = "\"" + key + "\" = \"" + value + "\"";
			String encodedParams = URLEncoder.encode(queryParam, StandardCharsets.UTF_8.toString());
			String fullUrl = probandsUrl + "?q=" + encodedParams;
			Client client = ClientHelper.newBuilderWithProxy().build();
			Response response = client.target(fullUrl).request(MediaType.APPLICATION_JSON).header("x-access-token", getPatientDiaryAuthToken()).get();
			if (response.getStatus() == NOT_FOUND_STATUS) {
				return Optional.empty();
			}
			return Optional.ofNullable(response.readEntity(PatientDiaryQueryResponse.class));
		} catch (IOException e) {
			logger.error("Could not retrieve patient query response: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private String getValidationMessage(EnumSet<PatientDiaryValidationError> validationErrors) {
		return validationErrors.stream()
			.map(PatientDiaryValidationError::getErrorLanguageKey)
			.map(I18nProperties::getValidationError)
			.collect(Collectors.joining("\n"));
	}
}
