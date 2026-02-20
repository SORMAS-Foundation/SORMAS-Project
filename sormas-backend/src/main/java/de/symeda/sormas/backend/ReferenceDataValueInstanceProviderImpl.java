package de.symeda.sormas.backend;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.referencedata.ReferenceDataValueInstanceProvider;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.util.InstanceProvider;

@ApplicationScoped
public class ReferenceDataValueInstanceProviderImpl implements ReferenceDataValueInstanceProvider {

	private Map<Class<? extends ReferenceDto>, Supplier<List<? extends ReferenceDto>>> dictionary;

	@PostConstruct
	public void init() {
		dictionary = Map.ofEntries(
			Map.entry(CountryReferenceDto.class, () -> getInstance(CountryFacadeEjb.class).getAllActiveAsReference()),
			Map.entry(RegionReferenceDto.class, () -> getInstance(RegionFacadeEjb.class).getAllActiveAsReference()),
			Map.entry(DistrictReferenceDto.class, () -> getInstance(DistrictFacadeEjb.class).getAllActiveAsReference()),
			Map.entry(CommunityReferenceDto.class, (Supplier) () -> getInstance(CommunityFacadeEjb.class).getAllAfter(new Date()))

		// TODO: check number of values that are loaded with those calls, otherwise you search by name
		// TODO: cannot fetch all: FacilityReferenceDto
		// Map.entry(FacilityReferenceDto.class, () -> getInstance(FacilityFacadeEjb.class).getAllWithoutRegionAfter(new Date()))

		);
	}

	private <T> T getInstance(Class<T> ejb) {
		return InstanceProvider.getInstanceFor(ejb);
	}

	@Override
	public <T extends ReferenceDto> List<T> getAll(Class<T> referenceType) {
		return (List<T>) Optional.ofNullable(dictionary.get(referenceType).get()).orElse(Collections.emptyList());
	}

	@Override
	public <T extends ReferenceDto> Optional<T> getOne(String caption, Class<T> referenceType) {
		// TODO: think f
		return Optional.empty();
	}
}
