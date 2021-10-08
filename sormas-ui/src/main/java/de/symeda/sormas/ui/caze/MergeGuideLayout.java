package de.symeda.sormas.ui.caze;

import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.AbstractMergeGuideLayout;

public class MergeGuideLayout extends AbstractMergeGuideLayout {

	@Override
	protected String getInfoMergingExplanationMessage() {
		return Strings.infoMergingExplanation;
	}

	@Override
	protected String getHeadingHowToMergeMessage() {
		return Strings.headingHowToMergeCases;
	}

	@Override
	protected String getInfoHowToMergeMessage() {
		return Strings.infoHowToMergeCases;
	}

	@Override
	protected String getInfoMergingMergeDescriptionMessage() {
		return Strings.infoMergingMergeDescription;
	}

	@Override
	protected String getInfoMergingPickDescriptionMessage() {
		return Strings.infoMergingPickDescription;
	}

	@Override
	protected String getInfoMergingHideDescriptionMessage() {
		return Strings.infoMergingHideDescription;
	}

	@Override
	protected String getInfoCompletenessMessage() {
		return Strings.infoCaseCompleteness;
	}

	@Override
	protected String getInfoCompletenessMergeMessage() {
		return Strings.infoCompletenessMerge;
	}

	@Override
	protected String getInfoCalculateCompletenessMessage() {
		return Strings.infoCalculateCompleteness;
	}

	@Override
	protected String getInfoMergeIgnoreRegionMessage() {
		return Strings.infoMergeIgnoreRegion;
	}
}
