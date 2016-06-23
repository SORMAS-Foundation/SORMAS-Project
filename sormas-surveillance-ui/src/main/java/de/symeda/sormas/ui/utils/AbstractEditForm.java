package de.symeda.sormas.ui.utils;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Field;

import de.symeda.sormas.api.DataTransferObject;

@SuppressWarnings("serial")
public abstract class AbstractEditForm <DTO extends DataTransferObject> extends CustomLayout implements DtoEditForm<DTO> {

	private final BeanFieldGroup<DTO> fieldGroup;
	
	protected AbstractEditForm(Class<DTO> type) {
		
		fieldGroup = new BeanFieldGroup<DTO>(type) {

			@Override
			protected void configureField(Field<?> field) {
				field.setBuffered(isBuffered());

				field.setEnabled(isEnabled());

				if (field.getPropertyDataSource().isReadOnly()) {
					field.setReadOnly(true);
				} else if (isReadOnly()) {
					field.setReadOnly(true);
				}
			}
		};

		initLayout();
	}
	
	public void initLayout() {
		setLayout();
	    setSizeFull();
		addFields();
	}
	
	protected abstract void setLayout();
	protected abstract void addFields();
	
	
	@Override
	public DTO getDto() {
		BeanItem<DTO> beanItem = getFieldGroup().getItemDataSource();
		if (beanItem == null) {
			return null;
		} else {
			return beanItem.getBean();
		}
	}

	@Override
	public void setDto(DTO dto) {
		BeanFieldGroup<DTO> fieldGroup = getFieldGroup();
		fieldGroup.setItemDataSource(new BeanItem<DTO>(dto));
	}

	@Override
	public BeanFieldGroup<DTO> getFieldGroup() {
		return this.fieldGroup;
	}

}
