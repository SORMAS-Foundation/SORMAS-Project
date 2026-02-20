package de.symeda.sormas.backend;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.referencedata.ReferenceDataValueInstanceProvider;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;

@ApplicationScoped
public class ReferenceDataValueInstanceProviderImpl implements ReferenceDataValueInstanceProvider {

	CountryFacadeEjb ok = new CountryFacadeEjb();

	private Map<Class<? extends ReferenceDto>, Supplier<List<? extends ReferenceDto>>> dictionary;

	@PostConstruct
	public void init() {
		dictionary = Map.ofEntries(Map.entry(CountryReferenceDto.class, () -> getInstance(CountryFacadeEjb.class).getAllActiveAsReference()));
	}

	private static CountryFacadeEjb getInstance(Class<?> ejb) {
		return referenceDataLoaderProvider(countryFacadeEjbClass);
	}

	@Override
	public <T extends ReferenceDto> List<T> getAll(Class<T> referenceType) {
		return List.of();
	}

	@Override
	public <T extends ReferenceDto> Optional<T> getOne(String caption, Class<T> referenceType) {
		return Optional.empty();
	}
}
