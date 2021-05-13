package de.symeda.sormas.ui.contact;

import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.AbstractMergeGuideLayout;

public class MergeGuideLayout extends AbstractMergeGuideLayout {

	@Override
	protected String getInfoMergingExplanationMessage() {
		return Strings.infoContactMergingExplanation;
	}

	@Override
	protected String getHeadingHowToMergeMessage() {
		return Strings.headingHowToMergeContacts;
	}

	@Override
	protected String getInfoHowToMergeMessage() {
		return Strings.infoHowToMergeContacts;
	}

	@Override
	protected String getInfoMergingMergeDescriptionMessage() {
		return Strings.infoContactMergingMergeDescription;
	}

	@Override
	protected String getInfoMergingPickDescriptionMessage() {
		return Strings.infoContactMergingPickDescription;
	}

	@Override
	protected String getInfoMergingHideDescriptionMessage() {
		return Strings.infoContactMergingHideDescription;
	}

	@Override
	protected String getInfoCompletenessMessage() {
		return Strings.infoContactCompleteness;
	}

	@Override
	protected String getInfoCompletenessMergeMessage() {
		return Strings.infoContactCompletenessMerge;
	}

	@Override
	protected String getInfoCalculateCompletenessMessage() {
		return Strings.infoContactCalculateCompleteness;
	}

	@Override
	protected String getInfoMergeIgnoreRegionMessage() {
		return Strings.infoContactMergeIgnoreRegion;
	}
}
