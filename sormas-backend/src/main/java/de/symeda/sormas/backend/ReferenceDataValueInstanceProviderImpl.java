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
import de.symeda.sormas.api.infrastructure.country.CountryFacade;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryFacade;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.referencedata.ReferenceDataValueInstanceProvider;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.util.InstanceProvider;
import de.symeda.sormas.backend.util.StringNormalizer;

@ApplicationScoped
public class ReferenceDataValueInstanceProviderImpl implements ReferenceDataValueInstanceProvider {

	public static final Date DATE_ALL_VALUES = new Date(0);
	private Map<Class<? extends ReferenceDto>, Supplier<List<? extends ReferenceDto>>> dictionary;

	@PostConstruct
	public void init() {
		dictionary = Map.ofEntries(
			Map.entry(CountryReferenceDto.class, () -> getInstance(CountryFacade.class).getAllActiveAsReference()),
			Map.entry(RegionReferenceDto.class, () -> getInstance(RegionFacade.class).getAllActiveAsReference()),
			Map.entry(DistrictReferenceDto.class, () -> getInstance(DistrictFacade.class).getAllActiveAsReference()),
			Map.entry(CommunityReferenceDto.class, (Supplier) () -> getInstance(CommunityFacadeEjb.class).getAllAfter(DATE_ALL_VALUES)),
			Map.entry(FacilityReferenceDto.class, (Supplier) () -> getInstance(FacilityFacadeEjb.class).getAllWithoutRegionAfter(DATE_ALL_VALUES)),
			Map.entry(PointOfEntryReferenceDto.class, (Supplier) () -> getInstance(PointOfEntryFacade.class).getAllAfter(DATE_ALL_VALUES))

		// TODO: check number of values that are loaded with those calls, otherwise you search by name
		// TODO: cannot fetch all: FacilityReferenceDto

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
		return getAll(referenceType).stream()
			.filter(referenceDto -> StringNormalizer.normalize(referenceDto.getCaption()).equals(StringNormalizer.normalize(caption)))
			.findAny();
	}

}
