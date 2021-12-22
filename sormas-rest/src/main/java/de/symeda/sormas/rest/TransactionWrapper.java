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
 *
 * <b>This has to stay a local bean - otherwise the function would be serialized which may have unwanted side effects.</b>
 */

@LocalBean
@Stateless
@PermitAll
public class TransactionWrapper {

	/**
	 * Calls the passed function in a new JTA transaction.
	 *
	 * <b>Don't access the context of the calling bean from within the function. E.g. don't use the entity manager of the calling bean.</b>
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
