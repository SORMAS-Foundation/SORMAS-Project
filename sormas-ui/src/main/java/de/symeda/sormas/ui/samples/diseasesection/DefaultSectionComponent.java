package de.symeda.sormas.ui.samples.diseasesection;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;

/**
 * Disease section for diseases with no extra fields.
 * Handles auto-set test result for diseases that don't have a dedicated section component.
 */
public class DefaultSectionComponent extends AbstractDiseaseSectionComponent {

	private static final Map<Disease, List<PathogenTestType>> AUTO_POSITIVE_TYPES = buildAutoPositiveTypes();

	private static Map<Disease, List<PathogenTestType>> buildAutoPositiveTypes() {
		Map<Disease, List<PathogenTestType>> map = new HashMap<>();
		map.put(
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
			Collections.unmodifiableList(Arrays.asList(PathogenTestType.SEQUENCING, PathogenTestType.WHOLE_GENOME_SEQUENCING)));
		map.put(Disease.INFLUENZA, Collections.singletonList(PathogenTestType.ISOLATION));
		return Collections.unmodifiableMap(map);
	}

	@Override
	protected void buildLayout() {
	}

	@Override
	protected void wireVisibility() {
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			PathogenTestType testType = event.getTestType();
			if (testType == null) {
				return;
			}

			List<PathogenTestType> positiveTypes = AUTO_POSITIVE_TYPES.get(disease);
			if (positiveTypes != null && positiveTypes.contains(testType)) {
				eventBus.fire(new SetTestResultEvent(PathogenTestResultType.POSITIVE));
			} else {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));
	}

	@Override
	protected void clearOwnedFields() {
	}
}
