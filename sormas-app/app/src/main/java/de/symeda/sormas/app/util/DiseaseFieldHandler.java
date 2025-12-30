package de.symeda.sormas.app.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FormType;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.formbuilder.FormBuilder;
import de.symeda.sormas.app.backend.formfield.FormField;
import de.symeda.sormas.app.caze.CaseSection;
import de.symeda.sormas.app.component.controls.ControlButton;
import de.symeda.sormas.app.component.controls.ControlCheckBoxField;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSwitchField;
import de.symeda.sormas.app.component.controls.ControlTextReadField;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.contact.ContactSection;

public class DiseaseFieldHandler {
    private static String TAG = DiseaseFieldHandler.class.getSimpleName();

    private Context context;
    private static  List<FormBuilder> diseaseForms;

    public DiseaseFieldHandler(Context context) {
        this.context = context;
    }

    public void hideFieldsForDisease(Disease diseaseName, LinearLayout mainContent, FormType formType) {
        // Get the relevant fields for the given disease
        List<FormField> relevantFields = getFieldsForDisease(diseaseName, formType);
        Log.d(TAG, "Relevant fields retrieved: " + relevantFields);
        // Log each field's details for debugging
        for (int i = 0; i < relevantFields.size(); i++) {
            FormField field = relevantFields.get(i);
            if (field != null) {
                Log.d(TAG, "Field[" + i + "]: fieldName=" + field.getFieldName() + ", formType=" + field.getFormType() + ", active=" + field.getActive());
            } else {
                Log.w(TAG, "Field[" + i + "]: is NULL");
            }
        }

        if (relevantFields.isEmpty()) {
            Log.d(TAG, "No relevant fields found, making all fields visible.");
            setAllFieldsVisibility(mainContent, View.VISIBLE);
            return;
        }

        // Get field names for visibility checking
        List<String> fieldNames = relevantFields.stream()
                .map(FormField::getFieldName)
                .collect(Collectors.toList());
        Log.d(TAG, "Field names for visibility check: " + fieldNames);
        // Set visibility based on relevant fields
        for (int i = 0; i < mainContent.getChildCount(); i++) {
            View child = mainContent.getChildAt(i);
            handleChildView(child, fieldNames);
        }

        Log.d(TAG, "Starting hideFieldsForDisease with disease: " + diseaseName + " and formType: " + formType);

        reorderFieldsForDisease(relevantFields, mainContent);
    }

    private void setAllFieldsVisibility(ViewGroup parent, int visibility) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);

            if (isFieldView(child)) {
                child.setVisibility(visibility);
            } else if (child instanceof ViewGroup) {
                setAllFieldsVisibility((ViewGroup) child, visibility);
            }
        }
    }

    private boolean isFieldView(View view) {
        return view instanceof TextView || view instanceof ControlPropertyField || view instanceof ControlCheckBoxField || view instanceof ControlDateField || view instanceof ControlTextReadField
                || view instanceof ControlSwitchField || view instanceof ControlButton;
    }

    private void handleChildView(View child, List<String> relevantFields) {
        if (isFieldView(child)) {
            setViewVisibility(child, relevantFields);
        } else if (child instanceof ViewGroup) {
            handleViewGroup((ViewGroup) child, relevantFields);
        }
    }

    private void handleViewGroup(ViewGroup viewGroup, List<String> relevantFields) {
        boolean groupHasVisibleField = false;

        for (int j = 0; j < viewGroup.getChildCount(); j++) {
            View grandChild = viewGroup.getChildAt(j);
            if (isFieldView(grandChild)) {
                if (setViewVisibility(grandChild, relevantFields)) {
                    groupHasVisibleField = true;
                }
            } else if (grandChild instanceof ViewGroup) {
                handleViewGroup((ViewGroup) grandChild, relevantFields);
                if (grandChild.getVisibility() == View.VISIBLE) {
                    groupHasVisibleField = true;
                }
            }
        }
        viewGroup.setVisibility(groupHasVisibleField ? View.VISIBLE : View.GONE);
    }

    private boolean setViewVisibility(View view, List<String> relevantFields) {
        // Check for a valid ID before retrieving the resource name
        if (view.getId() == View.NO_ID || view.getId() == 0) {
            Log.d(TAG, "Skipping view with no valid ID.");
            return false;
        }

        String viewIdName;
        try {
            viewIdName = context.getResources().getResourceEntryName(view.getId());
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Resource ID not found for view ID: " + view.getId(), e);
            return false;
        }

        boolean isVisible = relevantFields.isEmpty() || relevantFields.contains(viewIdName);
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        return isVisible;
    }


    private void reorderFieldsForDisease(List<FormField> orderedFields, ViewGroup parent) {
        // Create a lookup map for all views
        Map<String, View> viewsByFieldName = new HashMap<>();

        // Hide all views initially
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            view.setVisibility(View.GONE);

            try {
                String resourceName = context.getResources().getResourceEntryName(view.getId());
                viewsByFieldName.put(resourceName, view);
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Could not find resource name for ID: " + view.getId());
            }
        }

        // Now show only the views in orderedFields, in the specified order
        for (FormField field : orderedFields) {
            String fieldName = field.getFieldName();
            View view = viewsByFieldName.get(fieldName);

            if (view != null) {
                view.setVisibility(View.VISIBLE);
                // Bring the view to front so it appears in the correct order visually
                view.bringToFront();
            } else {
                Log.d(TAG, "No matching View found for FormField with name: " + fieldName);
            }
        }

        parent.requestLayout();
        parent.invalidate();
    }

    // Helper class to store view information
    private static class ViewInfo {
        View view;
        ViewGroup container;

        ViewInfo(View view, ViewGroup container) {
            this.view = view;
            this.container = container;
        }
    }

    private void gatherChildViewsWithContainers(ViewGroup parent, Map<Integer, ViewInfo> viewInfoMap, ViewGroup container) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);

            String resourceName = "";
            try {
                if (child.getId() != View.NO_ID) {
                    resourceName = context.getResources().getResourceEntryName(child.getId());
                    Log.d(TAG, "Processing view: " + resourceName);
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Resource not found for view ID: " + child.getId());
            }

            if (child.getId() != View.NO_ID) {
                // Add the view itself if it has an ID
                viewInfoMap.put(child.getId(), new ViewInfo(child, container));
            }

            if (child instanceof ViewGroup) {
                // Always recurse into ViewGroups, whether they're containers or not
                gatherChildViewsWithContainers((ViewGroup) child, viewInfoMap,
                        (isContainer(child) ? (ViewGroup)child : container));
            }
        }
    }

    private boolean isContainer(View view) {
        if (!(view instanceof ViewGroup)) return false;
        try {
            String resourceName = context.getResources().getResourceEntryName(view.getId());
            return resourceName != null && (resourceName.contains("_layout") || resourceName.contains("_container") || view.getId() == R.id.btns);
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }


    public List<FormField> getFieldsForDisease(Disease diseaseName, FormType formType) {
        FormBuilder formBuilder = DatabaseHelper.getFormBuilderDao().getFormBuilder(formType, diseaseName);

        if (formBuilder != null) {
            List<FormField> orderedFields = DatabaseHelper.getFormBuilderDao().getOrderedFormBuilderFormFields(formBuilder);
            Log.d(TAG, "Ordered fields retrieved from database: count=" + orderedFields.size());
            // Log details of each field
            for (FormField field : orderedFields) {
                if (field != null) {
                    Log.d(TAG, "Field details - fieldName: " + field.getFieldName() + ", formType: " + field.getFormType() + ", active: " + field.getActive() + ", id: " + field.getId());
                } else {
                    Log.w(TAG, "Found null field in orderedFields list");
                }
            }
            return orderedFields;
        }
        Log.d(TAG, "No FormBuilder found for Disease=" + diseaseName + ", FormType=" + formType);
        return new ArrayList<>();
    }

    /**
     * Check if a form exists for a given disease and form type.
     *
     * @param formType The form type (e.g., "ClinicalVisits").
     * @return true if the form exists, false otherwise.
     */
    private static boolean isFormNotAvailableForDisease(FormType formType) {
        if (diseaseForms.isEmpty()) {
            return false;
        }

        for (FormBuilder formBuilder : diseaseForms) {
            if (formBuilder.getFormType().name() == formType.name()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Remove menu items if a form is available for a given disease.
     *
     * @param menuItems The list of menu items.
     */
    public static List<PageMenuItem> handleMenuDataForDisease(List<PageMenuItem> menuItems, Disease disease) {
        diseaseForms = DatabaseHelper.getFormBuilderDao().getFormBuilders(disease);

        removeMenuItemIfFormAvailable(menuItems, FormType.PERSON_EDIT, CaseSection.PERSON_INFO);
        removeMenuItemIfFormAvailable(menuItems, FormType.MATERNAL_HISTORY_EDIT, CaseSection.MATERNAL_HISTORY);
        removeMenuItemIfFormAvailable(menuItems, FormType.HOSPITALIZATION_EDIT, CaseSection.HOSPITALIZATION);
        removeMenuItemIfFormAvailable(menuItems, FormType.PORT_HEALTH_INFO_EDIT, CaseSection.PORT_HEALTH_INFO);
        removeMenuItemIfFormAvailable(menuItems, FormType.SYMPTOMS_EDIT, CaseSection.SYMPTOMS);
        removeMenuItemIfFormAvailable(menuItems, FormType.HEALTH_CONDITION_EDIT, CaseSection.HEALTH_CONDITIONS);
        removeMenuItemIfFormAvailable(menuItems, FormType.EPIDEMIOLOGICAL_EDIT, CaseSection.EPIDEMIOLOGICAL_DATA);
        removeMenuItemIfFormAvailable(menuItems, FormType.SAMPLE_EDIT, CaseSection.SAMPLES);
        removeMenuItemIfFormAvailable(menuItems, FormType.CONTACT_EDIT, CaseSection.CONTACTS);
        removeMenuItemIfFormAvailable(menuItems, FormType.PRESCRIPTION_EDIT, CaseSection.PRESCRIPTIONS);
        removeMenuItemIfFormAvailable(menuItems, FormType.TREATMENT_EDIT, CaseSection.TREATMENTS);
        removeMenuItemIfFormAvailable(menuItems, FormType.CLINICAL_VISIT_EDIT, CaseSection.CLINICAL_VISITS);
        removeMenuItemIfFormAvailable(menuItems, FormType.TASK_EDIT, CaseSection.TASKS);
        removeMenuItemIfFormAvailable(menuItems, FormType.EVENT_EDIT, CaseSection.EVENTS);
        removeMenuItemIfFormAvailable(menuItems, FormType.IMMUNIZATION_EDIT, CaseSection.IMMUNIZATIONS);
        removeMenuItemIfFormAvailable(menuItems, FormType.VACCINATION_EDIT, CaseSection.VACCINATIONS);
        return menuItems;
    }

    public static List<PageMenuItem> handleContactMenuDataForDisease(List<PageMenuItem> menuItems, Disease disease) {
        diseaseForms = DatabaseHelper.getFormBuilderDao().getFormBuilders(disease);

        removeMenuItemIfFormAvailable(menuItems, FormType.PERSON_EDIT, ContactSection.PERSON_INFO);
        removeMenuItemIfFormAvailable(menuItems, FormType.EPIDEMIOLOGICAL_EDIT, ContactSection.EPIDEMIOLOGICAL_DATA);
        removeMenuItemIfFormAvailable(menuItems, FormType.FOLLOW_UP_VISITS, ContactSection.VISITS);
        removeMenuItemIfFormAvailable(menuItems, FormType.TASK_EDIT, ContactSection.TASKS);
        removeMenuItemIfFormAvailable(menuItems, FormType.IMMUNIZATION_EDIT, ContactSection.IMMUNIZATIONS);
        removeMenuItemIfFormAvailable(menuItems, FormType.VACCINATION_EDIT, ContactSection.VACCINATIONS);
        return menuItems;
    }

    /**
     * Remove a menu item if a form is available for a given disease and form type.
     *
     * @param menuItems The list of menu items.
     * @param formType The form type to check.
     * @param section The section to remove if the form is available.
     */
    private static void removeMenuItemIfFormAvailable(List<PageMenuItem> menuItems, FormType formType, CaseSection section) {
        boolean isFormAvailable = isFormNotAvailableForDisease(formType);
        if (isFormAvailable) {
            if (menuItems.size() > section.ordinal() && menuItems.get(section.ordinal()) != null) {
                menuItems.set(section.ordinal(), null);
            }
        }
    }

    private static void removeMenuItemIfFormAvailable(List<PageMenuItem> menuItems, FormType formType, ContactSection section) {
        boolean isFormAvailable = isFormNotAvailableForDisease(formType);
        if (isFormAvailable) {
            if (menuItems.size() > section.ordinal() && menuItems.get(section.ordinal()) != null) {
                menuItems.set(section.ordinal(), null);
            }
        }
    }


}

