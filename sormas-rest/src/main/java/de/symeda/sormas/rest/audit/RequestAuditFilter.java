package de.symeda.sormas.rest.audit;

import de.symeda.sormas.api.FacadeProvider;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.USER)
public class RequestAuditFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String actionMethod = requestContext.getMethod();

		FacadeProvider.getAuditLoggerFacade().logRestCall(actionMethod);
	}
}
