package de.symeda.sormas.api.labmessage;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface LabResultsAdapter {

	public List<LabMessageDto> getExternalLabMessages(boolean getOnlyNew);

	String convertToHTML(LabMessageDto message);
}
