package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.Disease;

public class SerotypeSalmonellosisTest extends AbstractEnumMembersVisibleForDiseaseTest<Serotype> {

	private static final Serotype[] ALLOWED_SEROTYPES = new Serotype[] {
		Serotype.UNKNOWN,
		Serotype.OTHER };

	@Override
	protected Serotype[] getAllowedMembers() {
		return ALLOWED_SEROTYPES;
	}

	@Override
	protected Disease getDisease() {
		return Disease.SALMONELLOSIS;
	}

	@Override
	protected Class<Serotype> getEnumClass() {
		return Serotype.class;
	}
}
