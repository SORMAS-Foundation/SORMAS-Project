package com.cinoteck.application.views.user;

import com.vaadin.flow.component.formlayout.FormLayout;

import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;


public class UserForm  extends FormLayout{
	
	Binder<UserDto> binder = new BeanValidationBinder<>(UserDto.class);

	List<AreaReferenceDto> regions;
	List<RegionReferenceDto> provinces;
	List<DistrictReferenceDto> districts;
	List<CommunityReferenceDto> communities;

	// TODO: Change labels to use IL8N names for internationalisation
	// NOTE: Fields should use the same naming convention as in UserDto.class
	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");
	TextField userEmail = new TextField("Email Address");
	TextField phone = new TextField("Phone Number");
	TextField userPosition = new TextField("Position");
	TextField userOrganisation = new TextField("Organisation");

	ComboBox<AreaReferenceDto> region = new ComboBox<>("Region");
	ComboBox<RegionReferenceDto> province = new ComboBox<>("Province");
	ComboBox<DistrictReferenceDto> district = new ComboBox<>("District");
	MultiSelectComboBox<CommunityReferenceDto> community = new MultiSelectComboBox<>("Community");

	MultiSelectComboBox<UserRole> userRoles = new MultiSelectComboBox<>("User Role");
	// CheckboxGroup<UserRole> userRoles = new CheckboxGroup<>();
	CheckboxGroup<FormAccess> formAccess = new CheckboxGroup<>();
	Checkbox activeCheck = new Checkbox();
	private boolean active = true;

	CheckboxGroup<UserType> usertype = new CheckboxGroup("Common User?");
	ComboBox<Language> language = new ComboBox<>("Language");

	Button save = new Button("Save");
	Button delete = new Button("Delete");
	Button close = new Button("Cancel");
	
	public UserForm(List<AreaReferenceDto> regions, List<RegionReferenceDto> provinces,
			List<DistrictReferenceDto> districts) {
addClassName("contact-form");
		
		HorizontalLayout hor = new HorizontalLayout();
		Icon vaadinIcon = new Icon("lumo", "cross");
		hor.setJustifyContentMode(JustifyContentMode.END);
		hor.setWidthFull();
		hor.add(vaadinIcon);
		hor.setHeight("5px");
		this.setColspan(hor, 2);
		vaadinIcon.addClickListener(event -> fireEvent(new CloseEvent(this)));
		add(hor);
		// Configure what is passed to the fields here
		configureFields();
	}
	
	private void configureFields() {
		H2 pInfo = new H2("Personal Information");
		this.setColspan(pInfo, 2);

		H2 fInfo = new H2("Field Information");
		this.setColspan(fInfo, 2);

		H2 userData = new H2("User Data");
		this.setColspan(userData, 2);

		binder.forField(firstName).asRequired("First Name is Required").bind(UserDto::getFirstName,
				UserDto::setFirstName);

		binder.forField(lastName).asRequired("Last Name is Required").bind(UserDto::getLastName, UserDto::setLastName);
		
		binder.forField(userEmail).asRequired("Email is Required").bind(UserDto::getUserEmail, UserDto::setUserEmail);

		binder.forField(phone).withValidator(e -> e.length() >= 10, "Enter a valid Phone Number")
				.bind(UserDto::getPhone, UserDto::setPhone);

		binder.forField(userPosition).bind(UserDto::getUserPosition, UserDto::setUserPosition);

		binder.forField(userOrganisation).bind(UserDto::getUserOrganisation, UserDto::setUserOrganisation);

		binder.forField(region).bind(UserDto::getArea, UserDto::setArea);
		regions = FacadeProvider.getAreaFacade().getAllActiveAsReference();
		region.setItems(regions);
		region.setItemLabelGenerator(AreaReferenceDto::getCaption);
		region.addValueChangeListener(e -> {
			provinces = FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid());
			province.setItems(provinces);
		});

		binder.forField(province).bind(UserDto::getRegion, UserDto::setRegion);
		province.setItemLabelGenerator(RegionReferenceDto::getCaption);
		province.addValueChangeListener(e -> {
			districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid());
			district.setItems(districts);
		});

		binder.forField(district).bind(UserDto::getDistrict, UserDto::setDistrict);
		district.setItemLabelGenerator(DistrictReferenceDto::getCaption);
		district.addValueChangeListener(e -> {
			communities = FacadeProvider.getCommunityFacade().getAllActiveByDistrict(e.getValue().getUuid());
			community.setItemLabelGenerator(CommunityReferenceDto::getCaption);
			community.setItems(communities);
		});

		binder.forField(community).bind(UserDto::getCommunity, UserDto::setCommunity);

		// TODO: Change implemenation to only add assignable roles sormas style.
		// userRoles.setLabel("User Roles");
		userRoles.setItems(UserRole.getAssignableRoles(FacadeProvider.getUserRoleConfigFacade().getEnabledUserRoles()));
		binder.forField(userRoles).asRequired("User Role is Required").bind(UserDto::getUserRoles, UserDto::setUserRoles);
		this.setColspan(userRoles, 1);
		userRoles.addValueChangeListener(e -> updateFieldsByUserRole(e.getValue()));

		formAccess.setLabel("Form Access");
		// formAccess.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
		formAccess.setItems(UserUiHelper.getAssignableForms());
		binder.forField(formAccess).bind(UserDto::getFormAccess, UserDto::setFormAccess);
		
		this.setColspan(activeCheck, 2);
		activeCheck.setLabel("Active ?");
		activeCheck.setValue(active);
		binder.forField(activeCheck).bind(UserDto::isActive, UserDto::setActive);

		// usertype.addThemeVariants(CheckboxGroupVariant.LUMO_HELPER_ABOVE_FIELD);
		usertype.setItems(UserType.values());
		// this.setColspan(usertype, 2);
		language.setItemLabelGenerator(Language::toString);
		language.setItems(Language.getAssignableLanguages());
		binder.forField(language).asRequired("Language is Required").bind(UserDto::getLanguage, UserDto::setLanguage);

		add(activeCheck, pInfo, firstName, lastName, userEmail, phone, userPosition, userOrganisation,fInfo, userData,
				language, userRoles, usertype, formAccess,  region, province, district, community);
		createButtonsLayout();
	}

	private void createButtonsLayout() {
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		// save.addClickShortcut(Key.ENTER);
		close.addClickShortcut(Key.ESCAPE);

		save.setEnabled(true);
		save.addClickListener(event -> validateAndSave());
		delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
		close.addClickListener(event -> fireEvent(new CloseEvent(this)));

		binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
		HorizontalLayout horizontallayout = new HorizontalLayout(save, close);
		horizontallayout.setMargin(true);
		add(horizontallayout);
		this.setColspan(horizontallayout, 2);
	}

	private void validateAndSave() {
		if (binder.isValid()) {
			fireEvent(new SaveEvent(this, binder.getBean()));
		}
	}

	public void setUser(UserDto user) {
		binder.setBean(user);
	}

	// Events
	public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
		private UserDto contact;

		protected UserFormEvent(UserForm source, UserDto contact) {
			super(source, false);
			this.contact = contact;
		}

		public UserDto getContact() {
			return contact;
		}
	}

	public static class SaveEvent extends UserFormEvent {
		SaveEvent(UserForm source, UserDto contact) {
			super(source, contact);
		}
	}

	public static class DeleteEvent extends UserFormEvent {
		DeleteEvent(UserForm source, UserDto contact) {
			super(source, contact);
		}

	}

	public static class CloseEvent extends UserFormEvent {
		CloseEvent(UserForm source) {
			super(source, null);
		}
	}

	public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
		return addListener(DeleteEvent.class, listener);
	}

	public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
		return addListener(SaveEvent.class, listener);
	}

	public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
		return addListener(CloseEvent.class, listener);
	}

	// TODO: This algorithm can be written better for good time and space complexity
	private void updateFieldsByUserRole(Set<UserRole> userRoles) {

		final JurisdictionLevel jurisdictionLevel = UserRole.getJurisdictionLevel(userRoles);
		final boolean useCommunity = jurisdictionLevel == JurisdictionLevel.COMMUNITY;
		final boolean useDistrict = jurisdictionLevel == JurisdictionLevel.DISTRICT || useCommunity;
		final boolean useRegion = jurisdictionLevel == JurisdictionLevel.REGION || useDistrict;
		final boolean useArea = jurisdictionLevel == JurisdictionLevel.AREA || useRegion;

		if (useCommunity) {
			community.setVisible(true);
			district.setVisible(true);
			province.setVisible(true);
			region.setVisible(true);
		} else if (useDistrict) {
			community.setVisible(false);
			district.setVisible(true);
			province.setVisible(true);
			region.setVisible(true);
		} else if (useRegion) {
			community.setVisible(false);
			district.setVisible(false);
			province.setVisible(true);
			region.setVisible(true);
		} else if (useArea) {
			community.setVisible(false);
			district.setVisible(false);
			province.setVisible(false);
			region.setVisible(true);
		} else {
			community.setVisible(false);
			district.setVisible(false);
			province.setVisible(false);
			region.setVisible(false);
		}

	}
}
