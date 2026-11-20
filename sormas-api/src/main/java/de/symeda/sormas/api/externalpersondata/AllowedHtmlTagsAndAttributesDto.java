package de.symeda.sormas.api.externalpersondata;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * @implNote Current implementation in UI uses JSON's @{@link org.jsoup.safety.Safelist}. This DTO's abstracts covers the integrity logic,
 *           to avoid putting into the frontend.
 */
@AuditedClass
public class AllowedHtmlTagsAndAttributesDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Set<String> allowedTagsWithoutAttributes = new HashSet<>();

	private Map<String, Set<String>> allowedAttributesByTag = new HashMap<>();

	private AllowedHtmlTagsAndAttributesDto() {
	}

	public static AllowedHtmlTagsAndAttributesDto of(Map<String, Set<String>> allowedAttributesByTag) {
		if (MapUtils.isEmpty(allowedAttributesByTag)) {
			return new AllowedHtmlTagsAndAttributesDto();
		}

		HashSet<String> tagsWithoutAttributes = new HashSet<>();

		Map<String, Set<String>> filteredAllowedAttributesByTag = allowedAttributesByTag.entrySet()
			.stream()
			.map(
				entry -> new HashMap.SimpleEntry<>(
					entry.getKey(),
					entry.getValue().stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet())))
			.filter(entry -> {
				boolean hasAttributes = CollectionUtils.isNotEmpty(entry.getValue());

				if (!hasAttributes) {
					tagsWithoutAttributes.add(entry.getKey());
				}

				return hasAttributes;
			})
			.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

		AllowedHtmlTagsAndAttributesDto result = new AllowedHtmlTagsAndAttributesDto();

		result.setAllowedAttributesByTag(filteredAllowedAttributesByTag);
		result.setAllowedTagsWithoutAttributes(tagsWithoutAttributes);

		return result;
	}

	public Set<String> getAllowedTagsWithoutAttributes() {
		return allowedTagsWithoutAttributes;
	}

	public void setAllowedTagsWithoutAttributes(Set<String> allowedTagsWithoutAttributes) {
		this.allowedTagsWithoutAttributes = allowedTagsWithoutAttributes;
	}

	public Map<String, Set<String>> getAllowedAttributesByTag() {
		return allowedAttributesByTag;
	}

	public void setAllowedAttributesByTag(Map<String, Set<String>> allowedAttributesByTag) {
		this.allowedAttributesByTag = allowedAttributesByTag;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		AllowedHtmlTagsAndAttributesDto that = (AllowedHtmlTagsAndAttributesDto) o;
		return Objects.equals(allowedTagsWithoutAttributes, that.allowedTagsWithoutAttributes)
			&& Objects.equals(allowedAttributesByTag, that.allowedAttributesByTag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(allowedTagsWithoutAttributes, allowedAttributesByTag);
	}
}
