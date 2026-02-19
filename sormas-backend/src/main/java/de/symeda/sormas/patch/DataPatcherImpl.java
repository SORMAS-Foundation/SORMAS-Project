package de.symeda.sormas.patch;

import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.patch.DataPatchRequest;
import de.symeda.sormas.api.patch.DataPatchResponse;
import de.symeda.sormas.api.patch.DataPatcher;
import de.symeda.sormas.patch.mapping.FieldCustomMapperRegistry;
import de.symeda.sormas.patch.mapping.ValueMapperRegistry;

@Stateless
public class DataPatcherImpl implements DataPatcher {

	@Inject
	private ValueMapperRegistry valueMapperRegistry;

	@Inject
	private FieldCustomMapperRegistry FieldCustomMapperRegistry;

	@Override
	public DataPatchResponse patch(DataPatchRequest request) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
