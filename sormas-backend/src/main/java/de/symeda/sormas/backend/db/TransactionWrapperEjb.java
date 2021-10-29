package de.symeda.sormas.backend.db;

import de.symeda.sormas.api.db.TransactionWrapperFacade;

import java.util.function.Function;

import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless(name = "TransactionWrapperFacade")
@PermitAll
public class TransactionWrapperEjb implements TransactionWrapperFacade {

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public <T, R> R execute(Function<T, R> function, T data) {
		return function.apply(data);
	}

	@LocalBean
	@Stateless
	public static class TransactionWrapperEjbLocal extends TransactionWrapperEjb {

	}
}
