package de.symeda.sormas.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Success status of a POST-endpoint operation.")
public enum PushResult {

	OK,
	TOO_OLD,
	ERROR,
	VALIDATION_EXCEPTION,
	TRANSACTION_ROLLED_BACK_EXCEPTION
}
