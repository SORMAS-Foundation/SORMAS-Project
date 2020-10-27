package de.symeda.sormas.backend.externaljournal;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
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
import de.symeda.sormas.api.externaljournal.PatientDiaryPersonDto;
import de.symeda.sormas.api.externaljournal.PatientDiaryPersonQueryResponse;
import de.symeda.sormas.api.externaljournal.PatientDiaryPersonValidation;
import de.symeda.sormas.api.externaljournal.PatientDiaryRegisterResult;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;

@Stateless
@LocalBean
public class ExternalJournalService {

	private static final String SYMPTOM_JOURNAL_KEY = "symptomJournal";
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
	 * Retrieves a token used for authenticating in the symptom journal. The token will be cached.
	 * 
	 * @return the authentication token
	 */
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
			Client client = ClientBuilder.newClient();
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
		SymptomJournalStatus savedStatus = personFacade.getPersonByUuid(contact.getPerson().getUuid()).getSymptomJournalStatus();
		if (SymptomJournalStatus.REGISTERED.equals(savedStatus) || SymptomJournalStatus.ACCEPTED.equals(savedStatus)) {
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
	 * @param existingPerson
	 *            the person already available in the external journal
	 * @param updatedPerson
	 *            the updated person in SORMAS
	 */
	public void notifyExternalJournalPersonUpdate(PersonDto existingPerson, PersonDto updatedPerson) {
		if (shouldNotify(existingPerson, updatedPerson)) {
			if (configFacade.getSymptomJournalConfig().getUrl() != null) {
				notifySymptomJournal(existingPerson.getUuid());
			}
			if (configFacade.getPatientDiaryConfig().getUrl() != null) {
				notifyPatientDiary(existingPerson.getUuid());
			}
		}
	}

	/**
	 * Note: This method just checks for changes in the Person data.
	 * It can not check for Contact related data such as FollowUpUntil dates.
	 */
	private boolean shouldNotify(PersonDto existingPerson, PersonDto updatedPerson) {
		boolean relevantPerson = SymptomJournalStatus.ACCEPTED.equals(existingPerson.getSymptomJournalStatus())
			|| SymptomJournalStatus.REGISTERED.equals(existingPerson.getSymptomJournalStatus());
		boolean relevantFieldsUpdated = Comparator.comparing(PersonDto::getFirstName, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(PersonDto::getLastName, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(PersonDto::getEmailAddress, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(PersonDto::getPhone, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(PersonDto::getBirthdateDD, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(PersonDto::getBirthdateMM, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(PersonDto::getBirthdateYYYY, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(PersonDto::getSex, Comparator.nullsLast(Comparator.naturalOrder()))
			.compare(existingPerson, updatedPerson)
			!= 0;
		return relevantPerson && relevantFieldsUpdated;
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
			PatientDiaryPersonDto patientDiaryPersonDto = mapper.treeToValue(idatData, PatientDiaryPersonDto.class);
			String endDate = node.get("endDate").textValue();
			patientDiaryPersonDto.setEndDate(endDate);
			return Optional.of(patientDiaryPersonDto);
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
		Client client = ClientBuilder.newClient();
		return client.target(externalDataUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("x-access-token", getPatientDiaryAuthToken());
	}

	/**
	 * Check whether a person has valid data in order to be registered in the patient diary
	 * 
	 * @param person
	 *            the person to validate
	 * @return the result of the validation
	 */
	public PatientDiaryPersonValidation validatePatientDiaryPerson(PersonDto person) {
		String email = person.getEmailAddress();
		String phone = person.getPhone();
		boolean validEmail = true;
		boolean validPhone = true;
		boolean validBirthdate = true;
		boolean emailAvailable = true;
		boolean phoneAvailable = true;
		if (StringUtils.isNotEmpty(email)) {
			EmailValidator validator = EmailValidator.getInstance();
			validEmail = validator.isValid(email);
			emailAvailable = isEmailAvailable(person.getEmailAddress());
		}
		if (StringUtils.isNotEmpty(phone)) {
			validPhone = false;
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			try {
				Phonenumber.PhoneNumber germanNumberProto = phoneUtil.parse(phone, "DE");
				validPhone = phoneUtil.isValidNumber(germanNumberProto);
				String internationalPhone = phoneUtil.format(germanNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
				phoneAvailable = isPhoneAvailable(internationalPhone);
			} catch (NumberParseException e) {
				logger.warn("NumberParseException was thrown: " + e.toString());
			}
		}
		if (ObjectUtils.anyNotNull(person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY())) {
			validBirthdate = ObjectUtils.allNotNull(person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY());
		}

		boolean hasPhoneOrEmail = !StringUtils.isAllEmpty(email, phone);
		boolean valid = hasPhoneOrEmail && validEmail && validPhone && validBirthdate && emailAvailable && phoneAvailable;
		String message = getValidationMessage(hasPhoneOrEmail, validEmail, validPhone, validBirthdate, emailAvailable, phoneAvailable);
		return new PatientDiaryPersonValidation(valid, message);
	}

	private boolean isEmailAvailable(String emailAddress) {
		return queryPatientDiary(EMAIL_QUERY_PARAM, emailAddress).getCount() == 0;
	}

	private boolean isPhoneAvailable(String phone) {
		return queryPatientDiary(MOBILE_PHONE_QUERY_PARAM, phone).getCount() == 0;
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
	public PatientDiaryPersonQueryResponse queryPatientDiary(String key, String value) {
		try {
			String probandsUrl = configFacade.getPatientDiaryConfig().getProbandsUrl() + "/probands";
			String queryParam = "\"" + key + "\" = \"" + value + "\"";
			String encodedParams = URLEncoder.encode(queryParam, StandardCharsets.UTF_8.toString());
			String fullUrl = probandsUrl + "?q=" + encodedParams;
			Client client = ClientBuilder.newClient();
			Response response = client.target(fullUrl)
					.request(MediaType.APPLICATION_JSON)
					.header("x-access-token", getPatientDiaryAuthToken())
					.get();
			return response.readEntity(PatientDiaryPersonQueryResponse.class);
		} catch (IOException e) {
			logger.error("Could not retrieve patient query response: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private String getValidationMessage(
		boolean hasPhoneOrEmail,
		boolean validEmail,
		boolean validPhone,
		boolean validBirthdate,
		boolean emailAvailable,
		boolean phoneAvailable) {
		StringBuilder message = new StringBuilder();
		if (!hasPhoneOrEmail) {
			message.append(I18nProperties.getValidationError(Validations.externalJournalPersonValidationNoEmailOrPhone));
			message.append('\n');
		}
		if (!validEmail) {
			message.append(I18nProperties.getValidationError(Validations.externalJournalPersonValidationEmail));
			message.append('\n');
		}
		if (!validPhone) {
			message.append(I18nProperties.getValidationError(Validations.externalJournalPersonValidationPhone));
			message.append('\n');
		}
		if (!validBirthdate) {
			message.append(I18nProperties.getValidationError(Validations.externalJournalPersonValidationBirthdate));
			message.append('\n');
		}
		if (!emailAvailable) {
			message.append(I18nProperties.getValidationError(Validations.externalJournalPersonValidationEmailTaken));
			message.append('\n');
		}
		if (!phoneAvailable) {
			message.append(I18nProperties.getValidationError(Validations.externalJournalPersonValidationPhoneTaken));
			message.append('\n');
		}
		return message.toString();
	}
}
