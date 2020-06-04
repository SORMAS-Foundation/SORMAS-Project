package de.symeda.sormas.rest;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.utils.OutdatedEntityException;

public abstract class EntityDtoResource {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private TransactionWrapper transactionWrapper;

	protected <T extends Object> List<PushResult> savePushedDto(List<T> dtos, Function<T, T> saveEntityDto) {

		List<PushResult> results = new ArrayList<>(dtos.size());
		for (T dto : dtos) {
			PushResult result;
			try {
				dto = transactionWrapper.execute(saveEntityDto, dto);
				result = PushResult.OK;
			} catch (Exception e) {
				String errorMessage = createErrorMessage(dto);
				errorMessage += e.getMessage();
				if (e instanceof OutdatedEntityException
						|| ExceptionUtils.getRootCause(e) instanceof OutdatedEntityException) {
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

	protected <T extends Object> String createErrorMessage(T dto) {
		final EntityDto entityDto = (EntityDto) dto;
		return dto.getClass().getSimpleName() + " " + entityDto.getUuid() + " " + DateFormat.getDateTimeInstance().format(entityDto.getChangeDate()) + "\n";
	}
}
