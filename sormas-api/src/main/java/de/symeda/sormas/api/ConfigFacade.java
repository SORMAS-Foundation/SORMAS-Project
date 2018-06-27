package de.symeda.sormas.api;

import javax.ejb.Remote;

@Remote
public interface ConfigFacade {
	
	String getCountryName();
	
	String getAppUrl();
	
	String getEmailSenderAddress();
	
	String getEmailSenderName();
	
	String getSmsSenderName();
	
	String getSmsAuthKey();
	
	String getSmsAuthSecret();
	
	String getTempFilesPath();
	
	String getGeneratedFilesPath();

	char getCsvSeparator();

	String getAppLegacyUrl();

	void validateAppUrls();
	
}
