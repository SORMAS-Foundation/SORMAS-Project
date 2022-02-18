package de.symeda.sormas.backend.externaljournal;

import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.INVALID_BIRTHDATE;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.INVALID_EMAIL;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.SEVERAL_PHONES_OR_EMAILS;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
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

import de.symeda.sormas.api.externaljournal.ExternalJournalSyncResponseDto;
import de.symeda.sormas.api.externaljournal.ExternalJournalValidation;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryResult;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.JournalPersonDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
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

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private PatientDiaryClient patientDiaryClient;
	private PersonContactInfoValidator contactInfoValidator;
	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@Resource
	private ManagedScheduledExecutorService executorService;

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
	 * Notify external journals that a person has been updated
	 * 
	 * @param existingJournalPerson
	 *            the person already available in the external journal
	 * @return true if the person data change was considered relevant for external journals, false otherwise.
	 *
	 */
	public DataHelper.Pair<Boolean, ExternalJournalSyncResponseDto> notifyExternalJournalPersonUpdate(JournalPersonDto existingJournalPerson) {
		boolean shouldNotify = shouldNotify(existingJournalPerson);
		if (shouldNotify) {
			if (configFacade.getSymptomJournalConfig().isActive()) {
				return DataHelper.Pair.createPair(true, notifySymptomJournal(existingJournalPerson.getUuid()));
			}
			if (configFacade.getPatientDiaryConfig().isActive()) {
				return DataHelper.Pair.createPair(true, patientDiaryClient.notifyPatientDiary(existingJournalPerson.getUuid()));
			}
		}
		return DataHelper.Pair.createPair(shouldNotify, null);
	}

	public void handleExternalJournalPersonUpdateAsync(PersonReferenceDto person) {
		if (!configFacade.isExternalJournalActive()) {
			return;
		}

		/**
		 * The .getPersonForJournal(...) here gets the person in the state it is (most likely) known to an external journal.
		 * Changes of related data is assumed to be not yet persisted in the database.
		 * 5 second delay added before notifying of update so that current transaction can complete and
		 * new data can be retrieved from DB
		 */
		JournalPersonDto existingPerson = personFacade.getPersonForJournal(person.getUuid());
		executorService.schedule((Runnable) () -> notifyExternalJournalPersonUpdate(existingPerson), 5, TimeUnit.SECONDS);
	}

	public ExternalJournalSyncResponseDto handleExternalJournalPersonUpdateSync(PersonDto existingPerson) {
		if (!configFacade.isExternalJournalActive()) {
			return null;
		}

		JournalPersonDto existingJournalPerson = personFacade.getPersonForJournal(existingPerson);
		return notifyExternalJournalPersonUpdate(existingJournalPerson).getElement1();
	}

	/**
	 * Note: This method just checks for changes in the Person data.
	 * It can not check for Contact related data such as FollowUpUntil dates.
	 */
	private boolean shouldNotify(JournalPersonDto existingJournalPerson) {
		PersonDto detailedExistingPerson = personFacade.getPersonByUuid(existingJournalPerson.getUuid());
		if (detailedExistingPerson.isEnrolledInExternalJournal()) {
			JournalPersonDto updatedJournalPerson = personFacade.getPersonForJournal(existingJournalPerson.getUuid());
			return !existingJournalPerson.equals(updatedJournalPerson);
		}
		return false;
	}

	private ExternalJournalSyncResponseDto notifySymptomJournal(String personUuid) {
		// agree with PIA how this should be done
		return null;
	}

	public void validateExternalJournalPerson(PersonDto person) {
		if (configFacade.getSymptomJournalConfig().isActive()) {
			ExternalJournalValidation validationResult = validateSymptomJournalPerson(person);
			if (!validationResult.isValid()) {
				throw new ValidationRuntimeException(validationResult.getMessage());
			}
		}
		if (configFacade.getPatientDiaryConfig().isActive()) {
			ExternalJournalValidation validationResult = validatePatientDiaryPerson(person);
			if (!validationResult.isValid()) {
				throw new ValidationRuntimeException(validationResult.getMessage());
			}
		}
	}

	public ExternalJournalValidation validateSymptomJournalPerson(PersonDto person) {
		EnumSet<PatientDiaryValidationError> validationErrors = EnumSet.noneOf(PatientDiaryValidationError.class);

		boolean severalEmails = false;
		String email = "";

		try {
			email = person.getEmailAddress(false);
		} catch (PersonDto.SeveralNonPrimaryContactDetailsException e) {
			severalEmails = true;
		}

		if (severalEmails) {
			validationErrors.add(SEVERAL_PHONES_OR_EMAILS);
		}

		if (StringUtils.isNotEmpty(email)) {
			EmailValidator validator = EmailValidator.getInstance();
			if (!validator.isValid(email)) {
				validationErrors.add(INVALID_EMAIL);
			}
		}

		return new ExternalJournalValidation(validationErrors.isEmpty(), getValidationMessage(validationErrors));
	}

	/**
	 * Check whether a person has valid data in order to be registered in the patient diary.
	 * NOTE: since CLIMEDO is only used in Germany, only German numbers are considered valid at the moment
	 *
	 * @param person
	 *            the person to validate
	 * @return {@link PatientDiaryResult PatientDiaryResult} containing details about the result
	 */
	public ExternalJournalValidation validatePatientDiaryPerson(PersonDto person) {
		EnumSet<PatientDiaryValidationError> validationErrors = contactInfoValidator.validateContactInfo(person);

		if (ObjectUtils.anyNotNull(person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY())) {
			boolean validBirthdate = ObjectUtils.allNotNull(person.getBirthdateDD(), person.getBirthdateMM(), person.getBirthdateYYYY());
			if (!validBirthdate) {
				validationErrors.add(INVALID_BIRTHDATE);
			}
		}

		return new ExternalJournalValidation(validationErrors.isEmpty(), getValidationMessage(validationErrors));
	}

	private String getValidationMessage(EnumSet<PatientDiaryValidationError> validationErrors) {
		return validationErrors.stream()
			.map(PatientDiaryValidationError::getErrorLanguageKey)
			.map(I18nProperties::getValidationError)
			.collect(Collectors.joining("\n"));
	}

	public void setPatientDiaryClient(PatientDiaryClient patientDiaryClient) {
		this.patientDiaryClient = patientDiaryClient;
	}

	@EJB
	public void setContactInfoValidator(PersonContactInfoValidator contactInfoValidator) {
		this.contactInfoValidator = contactInfoValidator;
	}
}
