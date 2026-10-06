package de.symeda.sormas.api.importexport.hierarchical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ImportTemplateDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<String> headers;
	private final List<String> captions;

	public ImportTemplateDto(List<String> headers, List<String> captions) {
		this.headers = Collections.unmodifiableList(new ArrayList<>(headers));
		this.captions = Collections.unmodifiableList(new ArrayList<>(captions));
	}

	public List<String> getHeaders() {
		return headers;
	}

	public List<String> getCaptions() {
		return captions;
	}

	public Map<String, String> captionsByHeader() {
		Map<String, String> captionsByHeader = new LinkedHashMap<>();
		for (int i = 0; i < headers.size(); i++) {
			captionsByHeader.put(headers.get(i), captions.get(i));
		}
		return captionsByHeader;
	}
}
