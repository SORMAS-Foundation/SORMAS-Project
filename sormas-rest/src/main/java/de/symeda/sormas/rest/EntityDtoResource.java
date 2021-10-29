package de.symeda.sormas.rest;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.db.TransactionWrapperFacade;
import de.symeda.sormas.api.utils.OutdatedEntityException;

public abstract class EntityDtoResource {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final TransactionWrapperFacade transactionWrapper;

	protected EntityDtoResource() {
		this.transactionWrapper = FacadeProvider.getTransactionWrapperFacade();
	}

	protected <T> List<PushResult> savePushedDto(List<T> dtos, Function<T, T> saveEntityDto) {

		List<PushResult> results = new ArrayList<>(dtos.size());
		for (T dto : dtos) {
			PushResult result;
			try {
				dto = transactionWrapper.execute(saveEntityDto, dto);
				result = PushResult.OK;
			} catch (Exception e) {
				e.printStackTrace();
				String errorMessage = createErrorMessage(dto);
				errorMessage += e.getMessage();
				if (e instanceof OutdatedEntityException || ExceptionUtils.getRootCause(e) instanceof OutdatedEntityException) {
					logger.warn(errorMessage, e);
					result = PushResult.TOO_OLD;
				} else {
					logger.error(errorMessage, e);
					result = PushResult.ERROR;
				}
			}
			results.add(result);
		}
		return results;
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
