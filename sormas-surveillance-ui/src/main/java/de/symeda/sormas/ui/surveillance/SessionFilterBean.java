package de.symeda.sormas.ui.surveillance;

import java.io.IOException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

@Stateless
@LocalBean
public class SessionFilterBean {

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void doFilter(FilterChain chain, ServletRequest request, ServletResponse response) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

}
