package de.symeda.sormas.api.patch.partial_retrieval;

import java.util.Map;

public class PartialRetrievalResponse {

	private Map<String, FieldInfo> fieldInfoDictionary;

	private Map<String, PartialRetrievalFailureCause> failuresDictionary;
}
