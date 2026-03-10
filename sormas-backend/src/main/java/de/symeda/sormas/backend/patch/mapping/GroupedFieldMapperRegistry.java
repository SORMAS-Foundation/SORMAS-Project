package de.symeda.sormas.backend.patch.mapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.patch.mapping.GroupedFieldsMapper;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsRequest;
import de.symeda.sormas.api.patch.mapping.GroupedFieldsResponse;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.util.CollectorUtils;

@ApplicationScoped
public class GroupedFieldMapperRegistry {

	private final static Logger logger = LoggerFactory.getLogger(GroupedFieldMapperRegistry.class);

	@Inject
	private Instance<GroupedFieldsMapper<?>> instances;

	private Set<Tuple<String, ? extends GroupedFieldsMapper<?>>> prefixMapperTuples;

	@PostConstruct
	void init() {
		Map<String, List<GroupedFieldsMapper<?>>> mappersByPrefixDictionary = instances.stream()
			.flatMap(mapper -> mapper.aggregatedPrefixes().stream().map(prefix -> Tuple.of(mapper, prefix)))
			.collect(Collectors.groupingBy(Tuple::getSecond, Collectors.mapping(Tuple::getFirst, Collectors.toList())));

		Set<Tuple<String, Set<String>>> duplicateMappings = mappersByPrefixDictionary.entrySet()
			.stream()
			.filter(entry -> CollectionUtils.size(entry.getValue()) > 1)
			.map(
				entry -> Tuple
					.of(entry.getKey(), entry.getValue().stream().map(mapper -> mapper.getClass().getSimpleName()).collect(Collectors.toSet())))
			.collect(Collectors.toSet());

		if (CollectionUtils.isNotEmpty(duplicateMappings)) {
			throw new IllegalStateException(
				String.format(
					"There are duplicate grouped field mappings: [%s]. \nA prefix can only be handled by a single mapper",
					duplicateMappings));
		}

		prefixMapperTuples =
			mappersByPrefixDictionary.entrySet().stream().map(entry -> Tuple.of(entry.getKey(), entry.getValue().get(0))).collect(Collectors.toSet());
	}

	@NotNull
	public List<GroupedFieldsResponse<?>> aggregatedPatch(@NotNull GroupedFieldsRequest request) {
		Map<GroupedFieldsMapper<?>, Map<String, Object>> dictionary = request.getPartialPatchDictionary()
			.entrySet()
			.stream()
			.map(
				patchKeyValueEntry -> prefixMapperTuples.stream()
					.filter(prefixMapperTuple -> StringUtils.startsWith(patchKeyValueEntry.getKey(), prefixMapperTuple.getFirst()))
					.findAny()
					.map(tuple -> Tuple.of(tuple.getSecond(), patchKeyValueEntry)))
			.flatMap(Optional::stream)
			.collect(
				Collectors.groupingBy(
					Tuple::getFirst,
					CollectorUtils.toNullSafeMap(tuple -> tuple.getSecond().getKey(), tuple -> tuple.getSecond().getValue())));

		logger.debug("GroupedFieldsMapper dictionary: [{}] for request: [{}]", dictionary, request);

		return dictionary.entrySet()
			.stream()
			.map(entry -> entry.getKey().aggregatedPatch(buildCopy(request).setPartialPatchDictionary(entry.getValue())))
			.collect(Collectors.toList());
	}

	private static GroupedFieldsRequest buildCopy(GroupedFieldsRequest request) {
		return new GroupedFieldsRequest().setDisease(request.getDisease())
			.setCaseData(request.getCaseData())
			.setPerson(request.getPerson())
			.setAllowFallbackValues(request.isAllowFallbackValues())
			.setEmptyValueBehavior(request.getEmptyValueBehavior())
			.setOrigin(request.getOrigin())
			.setInputLanguages(request.getInputLanguages())
			.setPatchedInCaseOfFailures(request.isPatchedInCaseOfFailures())
			.setReplacementStrategy(request.getReplacementStrategy());
	}
}
