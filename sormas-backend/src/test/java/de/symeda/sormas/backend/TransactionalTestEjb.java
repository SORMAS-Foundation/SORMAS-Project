package de.symeda.sormas.backend;

import de.symeda.junit.cdi.jee.ejb.TransactionalEjb;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@RequestScoped
@TransactionalEjb
public class TransactionalTestEjb {

    @Inject
    EntityManager entityManager;

    public void executeInTransaction(Consumer<EntityManager> callback) {
        callback.accept(entityManager);
    }

    public <R> R executeInTransaction(Function<EntityManager, R> callback) {
        return callback.apply(entityManager);
    }

    public <T,R> R executeInTransaction(BiFunction<EntityManager, T, R> callback, T param) {
        return callback.apply(entityManager, param);
    }
}
