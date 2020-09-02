package de.symeda.sormas.rest;

import java.util.function.Function;

import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * <p>
 * Allows you to separate execution in a new JTA transaction while retaining injection features.
 * </p>
 * Usage in the calling class:
 * <ol>
 * <li>Inject {@link TransactionWrapper} with {@code @EJB}.</li>
 * <li>Inject the {@code function} to be called using {@code @Inject}.</li>
 * </ol>
 */
@LocalBean
@Stateless
@PermitAll
public class TransactionWrapper {

	/**
	 * Calls the passed function in a new JTA transaction.
	 * 
	 * @param function
	 *            The business logic to be executed.
	 * @param data
	 *            The data to be processed.
	 * @return The return value of the processing defined by {@code function} (typically a result/report).
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public <T, R> R execute(Function<T, R> function, T data) {

		return function.apply(data);
	}
}
