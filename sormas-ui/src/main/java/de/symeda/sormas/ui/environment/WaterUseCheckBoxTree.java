package de.symeda.sormas.ui.environment;

import java.util.List;

import de.symeda.sormas.api.environment.WaterUse;
import de.symeda.sormas.ui.utils.CheckBoxTree;

public class WaterUseCheckBoxTree extends CheckBoxTree<WaterUse> {

	public WaterUseCheckBoxTree(List<CheckBoxElement<WaterUse>> checkBoxElements, Runnable valueChangeCallback) {
		super(checkBoxElements, false, valueChangeCallback);
	}
}
