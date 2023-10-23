package de.symeda.sormas.api.externalmessage;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.naming.NamingException;
import javax.validation.Valid;

import de.symeda.sormas.api.PermanentlyDeletableFacade;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface ExternalMessageFacade extends PermanentlyDeletableFacade {

	ExternalMessageDto save(@Valid ExternalMessageDto dto);

	ExternalMessageDto saveAndProcessLabmessage(@Valid ExternalMessageDto dto);

	void validate(ExternalMessageDto dto);

	// Also returns deleted lab messages
	ExternalMessageDto getByUuid(String uuid);

	void bulkAssignExternalMessages(List<String> uuids, UserReferenceDto userRef);

	// Does not return deleted lab messages
	List<ExternalMessageDto> getForSample(SampleReferenceDto sample);

	/**
	 * This method is used to check whether a externalMessage is marked processed in the database.
	 * It can be used to check for recent changes.
	 *
	 * @param uuid
	 *            of the externalMessage
	 * @return true if the externalMessage is marked processed in the database, false otherwise.
	 */
	boolean isProcessed(String uuid);

	long count(ExternalMessageCriteria criteria);

	List<ExternalMessageIndexDto> getIndexList(ExternalMessageCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	Page<ExternalMessageIndexDto> getIndexPage(ExternalMessageCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	/**
	 * Fetches external messages from the connected external system and saves them in the database.
	 *
	 * @return a {@link ExternalMessageFetchResult ExternalMessageFetchResult}. If any error occurred, an appropriate message is provided in
	 *         the
	 *         object.
	 */
	ExternalMessageFetchResult fetchAndSaveExternalMessages(Date since);

	String getExternalMessagesAdapterVersion() throws NamingException;

	boolean exists(String uuid);

	boolean existsExternalMessageForEntity(ReferenceDto entityRef);

	// Also returns deleted lab messages
	List<ExternalMessageDto> getByReportId(String reportId);

	// Also considers deleted lab messages
	boolean existsForwardedExternalMessageWith(String reportId);

	ExternalMessageDto getForSurveillanceReport(SurveillanceReportReferenceDto surveillanceReport);
}
