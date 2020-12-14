package de.symeda.sormas.ui.contact;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class MergeContactsGrid extends TreeGrid<ContactIndexDto> {

	public static final String COLUMN_DISEASE = Captions.columnDiseaseShort;
	public static final String COLUMN_ACTIONS = "actions";
	public static final String COLUMN_COMPLETENESS = "completenessValue";
	public static final String COLUMN_UUID = "uuidLink";

	private ContactCriteria criteria;
	private boolean ignoreRegion;

	private List<String[]> hiddenUuidPairs;

	@SuppressWarnings("unchecked")
	public MergeContactsGrid() {

		super(ContactIndexDto.class);
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);

		Column<ContactIndexDto, String> diseaseColumn =
			addColumn(contact -> DiseaseHelper.toString(contact.getDisease(), contact.getDiseaseDetails()));
		diseaseColumn.setId(COLUMN_DISEASE);

		addComponentColumn(indexDto -> {
			return buildButtonLayout(indexDto);
		}).setId(COLUMN_ACTIONS);

		addComponentColumn(indexDto -> {
			Label label =
				new Label(indexDto.getCompleteness() != null ? new DecimalFormat("#").format(indexDto.getCompleteness() * 100) + " %" : "-");
			if (indexDto.getCompleteness() != null) {
				if (indexDto.getCompleteness() < 0.25f) {
					CssStyles.style(label, CssStyles.LABEL_CRITICAL);
				} else if (indexDto.getCompleteness() < 0.5f) {
					CssStyles.style(label, CssStyles.LABEL_IMPORTANT);
				} else if (indexDto.getCompleteness() < 0.75f) {
					CssStyles.style(label, CssStyles.LABEL_RELEVANT);
				} else {
					CssStyles.style(label, CssStyles.LABEL_POSITIVE);
				}
			}
			return label;
		}).setId(COLUMN_COMPLETENESS);

		addComponentColumn(indexDto -> {
			Link link = new Link(
				DataHelper.getShortUuid(indexDto.getUuid()),
				new ExternalResource(
					SormasUI.get().getPage().getLocation().getRawPath() + "#!" + ContactDataView.VIEW_NAME + "/" + indexDto.getUuid()));
			link.setTargetName("_blank");
			return link;
		}).setId(COLUMN_UUID);

		setColumns(
			COLUMN_UUID,
			COLUMN_DISEASE,
			ContactIndexDto.CASE_CLASSIFICATION,
			ContactIndexDto.PERSON_FIRST_NAME,
			ContactIndexDto.PERSON_LAST_NAME,
			ContactIndexDto.CONTACT_PROXIMITY,
			ContactIndexDto.FOLLOW_UP_STATUS,
			ContactIndexDto.CONTACT_CATEGORY,
			ContactIndexDto.FOLLOW_UP_UNTIL,
			ContactIndexDto.CREATION_DATE,
			COLUMN_COMPLETENESS,
			COLUMN_ACTIONS);

		Language userLanguage = I18nProperties.getUserLanguage();
		((Column<ContactIndexDto, Date>) getColumn(ContactIndexDto.CREATION_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}
		getColumn(COLUMN_ACTIONS).setCaption("");
		getColumn(COLUMN_UUID).setCaption(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.UUID));
		getColumn(COLUMN_COMPLETENESS).setCaption(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.COMPLETENESS));
		getColumn(COLUMN_COMPLETENESS).setSortable(false);

		this.setStyleGenerator(new StyleGenerator<ContactIndexDto>() {

			@Override
			public String apply(ContactIndexDto item) {
				TreeDataProvider<ContactIndexDto> dataProvider = (TreeDataProvider<ContactIndexDto>) getDataProvider();
				TreeData<ContactIndexDto> data = dataProvider.getTreeData();

				if (data.getRootItems().contains(item)) {
					return "v-treegrid-parent-row";
				} else {
					return "v-treegrid-child-row";
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private HorizontalLayout buildButtonLayout(ContactIndexDto contact) {
		TreeDataProvider<ContactIndexDto> dataProvider = (TreeDataProvider<ContactIndexDto>) getDataProvider();
		TreeData<ContactIndexDto> data = dataProvider.getTreeData();

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(false);
		Button btnMerge = ButtonHelper.createIconButton(Captions.actionMerge, VaadinIcons.COMPRESS_SQUARE, e -> {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmChoice),
				new Label(I18nProperties.getString(Strings.confirmationMergeContactAndDeleteOther)),
				I18nProperties.getCaption(Captions.actionConfirm),
				I18nProperties.getCaption(Captions.actionCancel),
				640,
				confirmed -> {
					if (confirmed.booleanValue()) {
						ContactIndexDto caseToMergeAndDelete =
							data.getParent(contact) != null ? data.getParent(contact) : data.getChildren(contact).get(0);
						FacadeProvider.getContactFacade().mergeContact(contact.getUuid(), caseToMergeAndDelete.getUuid());
						FacadeProvider.getContactFacade().deleteContactAsDuplicate(caseToMergeAndDelete.getUuid(), contact.getUuid());

						if (FacadeProvider.getContactFacade().isDeleted(caseToMergeAndDelete.getUuid())) {
							reload();
							new Notification(I18nProperties.getString(Strings.messageCasesMerged), Notification.Type.TRAY_NOTIFICATION)
								.show(Page.getCurrent());
						} else {
							new Notification(I18nProperties.getString(Strings.errorCaseMerging), Notification.Type.ERROR_MESSAGE)
								.show(Page.getCurrent());
						}
					}
				});
		});
		Button btnPick = ButtonHelper.createIconButton(Captions.actionPick, VaadinIcons.CHECK, e -> {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmChoice),
				new Label(I18nProperties.getString(Strings.confirmationPickContactAndDeleteOther)),
				I18nProperties.getCaption(Captions.actionConfirm),
				I18nProperties.getCaption(Captions.actionCancel),
				640,
				confirmed -> {
					if (confirmed.booleanValue()) {
						ContactIndexDto contactToDelete =
							data.getParent(contact) != null ? data.getParent(contact) : data.getChildren(contact).get(0);
						FacadeProvider.getContactFacade().deleteContactAsDuplicate(contactToDelete.getUuid(), contact.getUuid());

						if (FacadeProvider.getContactFacade().isDeleted(contactToDelete.getUuid())) {
							data.removeItem(data.getParent(contact) == null ? contact : data.getParent(contact));
							dataProvider.refreshAll();
							new Notification(I18nProperties.getString(Strings.messageCaseDuplicateDeleted), Notification.Type.TRAY_NOTIFICATION)
								.show(Page.getCurrent());
						} else {
							new Notification(I18nProperties.getString(Strings.errorCaseDuplicateDeletion), Notification.Type.ERROR_MESSAGE)
								.show(Page.getCurrent());
						}
					}
				});
		});

		Button btnHide = null;

		if (data.getParent(contact) == null) {
			CssStyles.style(btnMerge, CssStyles.HSPACE_RIGHT_5, ValoTheme.BUTTON_PRIMARY);
			CssStyles.style(btnPick, CssStyles.HSPACE_RIGHT_5, ValoTheme.BUTTON_PRIMARY);

			btnHide = ButtonHelper.createIconButton(Captions.actionHide, VaadinIcons.CLOSE, e -> {
				hiddenUuidPairs.add(
					new String[] {
						contact.getUuid(),
						data.getChildren(contact).get(0).getUuid() });
				dataProvider.getTreeData().removeItem(contact);
				dataProvider.refreshAll();
			});
		} else {
			CssStyles.style(btnMerge, ValoTheme.BUTTON_LINK);
			CssStyles.style(btnPick, ValoTheme.BUTTON_LINK);
		}

		if (ignoreRegion) {
			btnMerge.setEnabled(false);
			btnPick.setEnabled(false);
		}
		layout.addComponent(btnMerge);
		layout.addComponent(btnPick);

		if (btnHide != null) {
			layout.addComponent(btnHide);
		}

		return layout;
	}

	@SuppressWarnings("unchecked")
	public void reload() {

		TreeDataProvider<ContactIndexDto> dataProvider = (TreeDataProvider<ContactIndexDto>) getDataProvider();
		TreeData<ContactIndexDto> data = dataProvider.getTreeData();
		data.clear();

		if (hiddenUuidPairs == null) {
			hiddenUuidPairs = new ArrayList<>();
		}

		List<ContactIndexDto[]> casePairs = FacadeProvider.getContactFacade().getContactsForDuplicateMerging(criteria, ignoreRegion);
		for (ContactIndexDto[] casePair : casePairs) {
			boolean uuidPairExists = false;
			for (String[] hiddenUuidPair : hiddenUuidPairs) {
				if (hiddenUuidPair[0].equals(casePair[0].getUuid()) && hiddenUuidPair[1].equals(casePair[1].getUuid())) {
					uuidPairExists = true;
				}
			}

			if (uuidPairExists) {
				continue;
			}

			data.addItem(null, casePair[0]);
			data.addItem(casePair[0], casePair[1]);
		}

		expandRecursively(data.getRootItems(), 0);
		dataProvider.refreshAll();
	}

	public void reload(boolean ignoreRegion) {
		this.ignoreRegion = ignoreRegion;
		reload();
	}

	@SuppressWarnings("unchecked")
	public void calculateCompletenessValues() {
		TreeDataProvider<ContactIndexDto> dataProvider = (TreeDataProvider<ContactIndexDto>) getDataProvider();
		TreeData<ContactIndexDto> data = dataProvider.getTreeData();

		for (ContactIndexDto parent : data.getRootItems()) {
			FacadeProvider.getContactFacade().updateCompleteness(parent.getUuid());
			FacadeProvider.getContactFacade().updateCompleteness(data.getChildren(parent).get(0).getUuid());
		}

		reload();
	}

	public void setCriteria(ContactCriteria criteria) {
		this.criteria = criteria;
	}
}
