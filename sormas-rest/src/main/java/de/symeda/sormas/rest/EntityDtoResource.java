package de.symeda.sormas.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.utils.EntityDtoTooOldException;

public abstract class EntityDtoResource {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected <T extends EntityDto> List<PushResult> savePushedDto(List<T> dtos, Function<T, T> saveEntityDto) {

		List<PushResult> results = new ArrayList<>(dtos.size());
		for (T dto : dtos) {
			PushResult result;
			try {
				dto = saveEntityDto.apply(dto);
				result = PushResult.OK;
			} catch (Exception e) {
				if (ExceptionUtils.getRootCause(e) instanceof EntityDtoTooOldException) {
					logger.warn(e.getMessage(), e);
					result = PushResult.TOO_OLD;
				} else {
					logger.error(e.getMessage(), e);
					result = PushResult.ERROR;
				}
			}
			results.add(result);
		}
		return results;
	}
}
