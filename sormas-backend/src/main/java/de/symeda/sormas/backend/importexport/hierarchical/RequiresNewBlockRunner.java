package de.symeda.sormas.backend.importexport.hierarchical;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import de.symeda.sormas.backend.importexport.hierarchical.engine.BlockRunner;

@Stateless
@LocalBean
public class RequiresNewBlockRunner implements BlockRunner {

	@Resource
	private SessionContext sessionContext;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void run(Runnable work) {
		try {
			work.run();
		} catch (RuntimeException e) {
			// ValidationRuntimeException does not roll back by itself
			sessionContext.setRollbackOnly();
			throw e;
		}
	}
}
