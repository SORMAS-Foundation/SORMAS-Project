/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.externaljournal;

import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.EMAIL_TAKEN;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.INVALID_EMAIL;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.INVALID_PHONE;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.NO_EMAIL;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.NO_PHONE_OR_EMAIL;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.PHONE_TAKEN;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.SEVERAL_EMAILS;
import static de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError.SEVERAL_PHONES_OR_EMAILS;
import static de.symeda.sormas.backend.externaljournal.PatientDiaryClient.EMAIL_QUERY_PARAM;
import static de.symeda.sormas.backend.externaljournal.PatientDiaryClient.MOBILE_PHONE_QUERY_PARAM;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryIdatId;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonData;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonDto;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryQueryResponse;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryValidationError;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

@Stateless
@LocalBean
public class PersonContactInfoValidator {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private PatientDiaryClient patientDiaryClient;

	public EnumSet<PatientDiaryValidationError> validateContactInfo(PersonDto person) {
		List<ContactInfoValidator> wrappers = new ArrayList<>();
		wrappers.add(new PersonEmailValidator(person));
		PatientDiaryValidationError severalNonPrimaryError = SEVERAL_EMAILS;
		PatientDiaryValidationError allBlankError = NO_EMAIL;

		if (configFacade.getPatientDiaryConfig().isAcceptPhoneContact()) {
			wrappers.add(new PersonPhoneValidator(person));
			severalNonPrimaryError = SEVERAL_PHONES_OR_EMAILS;
			allBlankError = NO_PHONE_OR_EMAIL;
		}

		return validateContactInfo(wrappers, severalNonPrimaryError, allBlankError);
	}

	private EnumSet<PatientDiaryValidationError> validateContactInfo(
		List<ContactInfoValidator> contactInfo,
		PatientDiaryValidationError severalNonPrimaryError,
		PatientDiaryValidationError allBlankError) {
		EnumSet<PatientDiaryValidationError> validationErrors = EnumSet.noneOf(PatientDiaryValidationError.class);

		boolean allBlank = contactInfo.stream().allMatch(ContactInfoValidator::isBlank);
		boolean severalNonPrimary = allBlank && contactInfo.stream().anyMatch(ContactInfoValidator::isSeveralNonPrimary);

		if (severalNonPrimary) {
			validationErrors.add(severalNonPrimaryError);
		} else if (allBlank) {
			validationErrors.add(allBlankError);
		}

		contactInfo.forEach(i -> {
			if (!i.isBlank()) {
				if (i.isMalformatted()) {
					validationErrors.add(i.invalidError);
				} else if (i.isUnavailable()) {
					validationErrors.add(i.takenError);
				}
			}
		});

		return validationErrors;
	}

	private static abstract class ContactInfoValidator {

		private String contactInfo;
		private boolean severalNonPrimary;

		private final PatientDiaryValidationError invalidError;
		private final PatientDiaryValidationError takenError;

		public ContactInfoValidator(ContactInfoSupplier supplier, PatientDiaryValidationError invalidError, PatientDiaryValidationError takenError) {
			this.invalidError = invalidError;
			this.takenError = takenError;

			contactInfo = "";
			severalNonPrimary = false;

			try {
				contactInfo = supplier.get();
			} catch (PersonDto.SeveralNonPrimaryContactDetailsException e) {
				severalNonPrimary = true;
			}
		}

		public boolean isSeveralNonPrimary() {
			return severalNonPrimary;
		}

		public boolean isBlank() {
			return StringUtils.isBlank(contactInfo);
		}

		// Shall return true if the format of the contactInfo is not legit
		abstract boolean isMalformatted();

		// Shall return true if existing entries block the usage of the contactInfo for another person
		abstract boolean isUnavailable();

	}

	private interface ContactInfoSupplier {

		String get() throws PersonDto.SeveralNonPrimaryContactDetailsException;
	}

	private class PersonEmailValidator extends ContactInfoValidator {

		private final PersonDto person;

		public PersonEmailValidator(PersonDto person) {
			super(() -> person.getEmailAddress(false), INVALID_EMAIL, EMAIL_TAKEN);
			this.person = person;
		}

		@Override
		boolean isMalformatted() {
			EmailValidator validator = EmailValidator.getInstance();
			return !validator.isValid(super.contactInfo);
		}

		@Override
		boolean isUnavailable() {
			PatientDiaryQueryResponse response = patientDiaryClient.queryPatientDiary(EMAIL_QUERY_PARAM, super.contactInfo)
				.orElseThrow(() -> new RuntimeException("Could not query patient diary for Email address availability"));

			return hasBlockingEntries(response, person);
		}
	}

	private class PersonPhoneValidator extends ContactInfoValidator {

		private final PersonDto person;

		public PersonPhoneValidator(PersonDto person) {
			super(() -> person.getPhone(false), INVALID_PHONE, PHONE_TAKEN);

			this.person = person;
		}

		@Override
		boolean isMalformatted() {
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			try {
				Phonenumber.PhoneNumber germanNumberProto = phoneUtil.parse(super.contactInfo, "DE");
				return !phoneUtil.isValidNumber(germanNumberProto);
			} catch (NumberParseException e) {
				logger.warn("NumberParseException was thrown: " + e.toString());
				return true;
			}
		}

		@Override
		boolean isUnavailable() {
			PatientDiaryQueryResponse response = patientDiaryClient.queryPatientDiary(MOBILE_PHONE_QUERY_PARAM, super.contactInfo)
				.orElseThrow(() -> new RuntimeException("Could not query patient diary for phone number availability"));

			return hasBlockingEntries(response, person);
		}
	}

	/**
	 * @param response
	 *            response data from patient diary containing entries of all persons already related to a contact info.
	 * @param person
	 *            person to be associated with that contact info.
	 * @return false if response does not contain any blocking entry. A blocking entry is a different person with identical first name.
	 */
	private static boolean hasBlockingEntries(PatientDiaryQueryResponse response, PersonDto person) {
		boolean notUsed = response.getCount() == 0;
		boolean samePerson = response.getResults()
			.stream()
			.map(PatientDiaryPersonData::getIdatId)
			.map(PatientDiaryIdatId::getIdat)
			.map(PatientDiaryPersonDto::getPersonUUID)
			.anyMatch(uuid -> person.getUuid().equals(uuid));
		boolean differentFirstNames = response.getResults()
			.stream()
			.map(PatientDiaryPersonData::getIdatId)
			.map(PatientDiaryIdatId::getIdat)
			.noneMatch(patientDiaryPerson -> person.getFirstName().equals(patientDiaryPerson.getFirstName()));
		return !(notUsed || samePerson || differentFirstNames);
	}
}
