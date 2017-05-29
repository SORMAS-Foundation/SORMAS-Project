package de.symeda.sormas.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

/**
 * @see <a href="https://jersey.github.io/documentation/latest/index.html">Jersey documentation</a>
 */
@ApplicationPath("*")
public class RestApplication extends ResourceConfig  {
	
	public RestApplication() {
        super(RestApplication.class);
        
        // Resources.
        packages(getClass().getPackage().getName());
        
        // as described in https://jersey.github.io/documentation/latest/security.html
        register(RolesAllowedDynamicFeature.class);
    }
}
