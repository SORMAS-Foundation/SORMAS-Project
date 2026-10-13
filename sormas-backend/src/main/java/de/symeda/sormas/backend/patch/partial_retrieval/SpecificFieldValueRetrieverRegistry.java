package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;

@ApplicationScoped
public class SpecificFieldValueRetrieverRegistry {

	private final static Logger logger = LoggerFactory.getLogger(SpecificFieldValueRetrieverRegistry.class);

	@Inject
	private Instance<SpecificFieldValueRetriever> instances;

	public SpecificFieldValueRetrieverRegistry() {
	}

	public Optional<FieldInfo> getFieldInfo(@NotNull String fieldName, @NotNull EntityDto entityDto) {
		return instances.stream()
			.filter(retriever -> retriever.supports(fieldName))
			.findAny()
			.map(retriever -> {
				logger.debug("Field [{}] will be handled by retriever: [{}]", fieldName, retriever);
				return retriever.getFieldInfo(fieldName, entityDto);
			});
	}
}
