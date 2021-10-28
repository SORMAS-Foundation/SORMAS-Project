package de.symeda.sormas.api.db;

import javax.ejb.Remote;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.function.Function;

/**
 * <p>
 * Allows you to separate execution in a new JTA transaction while retaining injection features.
 * </p>
 * Usage in the calling class:
 * <ol>
 * <li>Inject {@link TransactionWrapperEjb} with {@code @EJB}.</li>
 * <li>Inject the {@code function} to be called using {@code @Inject}.</li>
 * </ol>
 */

@Remote
public interface TransactionWrapperFacade {

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
	<T, R> R execute(Function<T, R> function, T data);
}
