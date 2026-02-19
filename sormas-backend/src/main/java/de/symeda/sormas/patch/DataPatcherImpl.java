package de.symeda.sormas.patch;

import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.patch.DataPatchRequest;
import de.symeda.sormas.api.patch.DataPatchResponse;
import de.symeda.sormas.api.patch.DataPatcher;
import de.symeda.sormas.patch.mapping.FieldCustomMapperRegistry;
import de.symeda.sormas.patch.mapping.ValueMapperRegistry;

@Stateless
public class DataPatcherImpl implements DataPatcher {

	// might be more subtle: person.toto but also *.uuid (or uuid). includes approach ?
	private Set<String> forbiddenFields;

	@Inject
	private ValueMapperRegistry valueMapperRegistry;

	@Inject
	private FieldCustomMapperRegistry fieldCustomMapperRegistry;

	@Override
	public DataPatchResponse patch(DataPatchRequest request) {

		/*
		 * Implementation steps:
		 * - lazily produce list of allowed fields to avoid.
		 * - Iterate over patch dictionary
		 * - Filter out empty values.
		 * - Check if field exists.
		 * - Check for forbidden fields
		 * - Check for FieldCustomMapper to use custom mapping strategy
		 * - Go to the appropriate (sub) field
		 * - TODO: if appropriate: multiple patching into same field strategy!!
		 * <p>
		 * WARN: Root will be either: (breaks trivial check if exists approach).
		 * - CaseData
		 * - Person
		 */

		throw new UnsupportedOperationException("Not supported yet.");
	}
}
