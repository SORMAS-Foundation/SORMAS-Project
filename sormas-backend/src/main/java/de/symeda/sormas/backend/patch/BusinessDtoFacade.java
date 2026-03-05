package de.symeda.sormas.backend.patch;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;

@ApplicationScoped
public class BusinessDtoFacade {

	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;

	@EJB
	private ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal immunizationFacade;

	private final Map<Class<? extends EntityDto>, Consumer<? extends EntityDto>> dtoSaveDictionary = new HashMap<>();

	private final Map<Class<? extends EntityDto>, Function<CaseDataDto, ? extends EntityDto>> dtoRetrieverDictionary = new HashMap<>();

	@PostConstruct
	private void init() {
		registerSaveOperations();
		getRegisterFetchOperations();
	}

	private void getRegisterFetchOperations() {
		registerFetch(PersonDto.class, caseDataDto -> personFacade.getByUuid(caseDataDto.getPerson().getUuid()));
	}

	private <T extends EntityDto> void registerFetch(Class<T> dtoClass, Function<CaseDataDto, T> fct) {
		dtoRetrieverDictionary.put(dtoClass, fct);
	}

	private void registerSaveOperations() {
		registerSave(PersonDto.class, personDto -> personFacade.save(personDto));
		registerSave(ImmunizationDto.class, immunizationDto -> immunizationFacade.save(immunizationDto));
	}

	private <T extends EntityDto> void registerSave(Class<T> dtoClass, Consumer<T> consumer) {
		dtoSaveDictionary.put(dtoClass, consumer);
	}

	public CaseDataDto getCaseDataDto(String caseUuid) {
		return caseFacade.getByUuid(caseUuid);
	}

	public <T extends EntityDto> Optional<Function<CaseDataDto, T>> fetch(@NotNull Class<T> entity) {
		return Optional.ofNullable((Function<CaseDataDto, T>) dtoRetrieverDictionary.get(entity));
	}

	public <T extends EntityDto> Optional<Consumer<T>> save(@NotNull EntityDto entityDto) {
		return Optional.ofNullable((Consumer<T>) dtoSaveDictionary.get(entityDto));
	}
}
