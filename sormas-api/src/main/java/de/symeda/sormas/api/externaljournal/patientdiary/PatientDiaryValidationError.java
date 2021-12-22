package de.symeda.sormas.api.externaljournal.patientdiary;

import de.symeda.sormas.api.i18n.Validations;

public enum PatientDiaryValidationError {

	NO_EMAIL(Validations.externalJournalPersonValidationNoEmail),
	NO_PHONE_OR_EMAIL(Validations.externalJournalPersonValidationNoEmailOrPhone),
	INVALID_EMAIL(Validations.externalJournalPersonValidationEmail),
	INVALID_PHONE(Validations.externalJournalPersonValidationPhone),
	INVALID_BIRTHDATE(Validations.externalJournalPersonValidationBirthdate),
	EMAIL_TAKEN(Validations.externalJournalPersonValidationEmailTaken),
	PHONE_TAKEN(Validations.externalJournalPersonValidationPhoneTaken),
	SEVERAL_EMAILS(Validations.externalJournalPersonValidationSeveralEmails),
	SEVERAL_PHONES_OR_EMAILS(Validations.externalJournalPersonValidationSeveralPhonesOrEmails);

	private final String errorLanguageKey;

	PatientDiaryValidationError(String errorLanguageKey) {
		this.errorLanguageKey = errorLanguageKey;
	}

	public String getErrorLanguageKey() {
		return errorLanguageKey;
	}
}
