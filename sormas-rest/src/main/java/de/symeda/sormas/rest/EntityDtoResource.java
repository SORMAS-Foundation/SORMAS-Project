package de.symeda.sormas.rest;

import static java.util.Objects.isNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.utils.OutdatedEntityException;
import de.symeda.sormas.api.utils.ValidationException;

public abstract class EntityDtoResource {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private TransactionWrapper transactionWrapper;

	protected <T> List<PushResult> savePushedDto(List<T> dtos, Function<T, T> saveEntityDto) {

		List<PushResult> results = new ArrayList<>(dtos.size());
		for (T dto : dtos) {
			PushResult result;
			try {
				dto = transactionWrapper.execute(saveEntityDto, dto);
				result = PushResult.OK;
			} catch (Exception e) {
				String errorMessage = createErrorMessage(dto);
				errorMessage += e.getMessage();
				result = getPushResultError(e, errorMessage);
			}
			results.add(result);
		}
		return results;
	}

	protected <T> Map<String, Map<PushResult, String>> savePushedDetailedDto(List<T> dtos, Function<T, T> saveEntityDto) {

		Map<String, Map<PushResult, String>> results = new HashMap<>(dtos.size());
		for (T dto : dtos) {
			PushResult result;
			try {
				dto = transactionWrapper.execute(saveEntityDto, dto);
				result = PushResult.OK;

				Map<PushResult, String> map = new EnumMap<>(PushResult.class);
				map.put(result, StringUtils.EMPTY);
				results.put(dto.toString(), map);
			} catch (Exception e) {
				String errorMessage = createErrorMessage(dto);
				errorMessage += e.getMessage();
				result = getPushResultError(e, errorMessage);
				Map<PushResult, String> map = new EnumMap<>(PushResult.class);
				map.put(result, errorMessage + (isNull(e.getMessage()) ? " - " + e.getCause() : ""));
				results.put(dto.toString(), map);
			}
		}
		return results;
	}

	private PushResult getPushResultError(Exception e, String errorMessage) {
		PushResult result;
		if (e instanceof OutdatedEntityException || ExceptionUtils.getRootCause(e) instanceof OutdatedEntityException) {
			logger.warn(errorMessage, e);
			result = PushResult.TOO_OLD;
		} else if (e instanceof ValidationException || ExceptionUtils.getRootCause(e) instanceof ValidationException) {
			logger.error(errorMessage, e);
			result = PushResult.VALIDATION_EXCEPTION;
		} else if (e instanceof javax.ejb.EJBTransactionRolledbackException
			|| ExceptionUtils.getRootCause(e) instanceof javax.ejb.EJBTransactionRolledbackException) {
			logger.error(errorMessage, e);
			result = PushResult.TRANSACTION_ROLLED_BACK_EXCEPTION;
		} else {
			logger.error(errorMessage, e);
			result = PushResult.ERROR;
		}
		return result;
	}

	protected <T> String createErrorMessage(T dto) {

		final EntityDto entityDto = (EntityDto) dto;
		if (entityDto.getChangeDate() == null) {
			return dto.getClass().getSimpleName() + " " + entityDto.getUuid() + "\n";
		} else {
			return dto.getClass().getSimpleName() + " " + entityDto.getUuid() + " "
				+ DateFormat.getDateTimeInstance().format(entityDto.getChangeDate()) + "\n";
		}
	}
}
