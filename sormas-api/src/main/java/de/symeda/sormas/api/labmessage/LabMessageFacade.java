package de.symeda.sormas.api.labmessage;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.naming.NamingException;
import javax.validation.Valid;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface LabMessageFacade {

	LabMessageDto save(@Valid LabMessageDto dto);

	// Also returns deleted lab messages
	LabMessageDto getByUuid(String uuid);

	void deleteLabMessage(String uuid);

	void deleteLabMessages(List<String> uuids);

	void bulkAssignLabMessages(List<String> uuids, UserReferenceDto userRef);

	// Does not return deleted lab messages
	List<LabMessageDto> getForSample(SampleReferenceDto sample);

	/**
	 * This method is used to check whether a labMessage is marked processed in the database.
	 * It can be used to check for recent changes.
	 *
	 * @param uuid
	 *            of the labMessage
	 * @return true if the labMessage is marked processed in the database, false otherwise.
	 */
	Boolean isProcessed(String uuid);

	long count(LabMessageCriteria criteria);

	List<LabMessageIndexDto> getIndexList(LabMessageCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	Page<LabMessageIndexDto> getIndexPage(LabMessageCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	/**
	 * Fetches external lab messages from the connected external system and saves them in the database.
	 *
	 * @return a {@link LabMessageFetchResult LabMessageFetchResult}. If any error occurred, an appropriate message is provided in the
	 *         object.
	 */
	LabMessageFetchResult fetchAndSaveExternalLabMessages(Date since);

	String getLabMessagesAdapterVersion() throws NamingException;

	boolean exists(String uuid);

	boolean existsLabMessageForEntity(ReferenceDto entityRef);

	// Also returns deleted lab messages
	List<LabMessageDto> getByReportId(String reportId);

	// Also considers deleted lab messages
	boolean existsForwardedLabMessageWith(String reportId);

}
