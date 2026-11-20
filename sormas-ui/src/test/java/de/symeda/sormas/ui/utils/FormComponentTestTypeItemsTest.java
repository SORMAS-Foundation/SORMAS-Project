package de.symeda.sormas.ui.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestCategory;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class FormComponentTestTypeItemsTest {

	@SuppressWarnings("unchecked")
	private static List<PathogenTestType> items(ComboBox<PathogenTestType> combo) {
		return new ArrayList<>(((ListDataProvider<PathogenTestType>) combo.getDataProvider()).getItems());
	}

	@Test
	public void otherCategorySortsLast() {
		List<PathogenTestCategory> categories = FormComponent.getVisibleTestCategories(FieldVisibilityCheckers.withDisease(Disease.MUMPS));
		assertEquals(PathogenTestCategory.OTHER, categories.get(categories.size() - 1));
	}

	@Test
	public void methodComboSortsIgnoringCapitals() {
		ComboBox<PathogenTestType> combo = new ComboBox<>();
		FormComponent.updateTestTypeItemsByCategory(combo, FieldVisibilityCheckers.withDisease(Disease.DIPHTHERIA), null);
		List<PathogenTestType> methods = items(combo);
		assertNotEquals(PathogenTestType.Q_PCR, methods.get(methods.size() - 1));
	}

	@Test
	public void otherCanBeChosenThroughItsCategory() {
		ComboBox<PathogenTestType> combo = new ComboBox<>();
		FormComponent.updateTestTypeItemsByCategory(combo, FieldVisibilityCheckers.withDisease(Disease.MUMPS), PathogenTestCategory.OTHER);
		assertEquals(List.of(PathogenTestType.OTHER), items(combo));
	}
}
