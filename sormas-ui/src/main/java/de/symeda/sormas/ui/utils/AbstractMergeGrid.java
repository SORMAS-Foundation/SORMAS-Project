/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.MergeableIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.ui.SormasUI;

public abstract class AbstractMergeGrid<T1 extends MergeableIndexDto, T2 extends BaseCriteria> extends TreeGrid<T1> {

	public static final String COLUMN_ACTIONS = "actions";
	public static final String COLUMN_COMPLETENESS = "completenessValue";
	public static final String COLUMN_UUID = "uuidLink";

	private static final String UUID = "uuid";
	private static final String COMPLETENESS = "completeness";

	protected T2 criteria;
	protected boolean ignoreRegion;

	protected List<String[]> hiddenUuidPairs;

	private String viewName;
	private String i18nPrefix;
	private String confirmMessage;
	private String pickMessage;

	public AbstractMergeGrid(Class<T1> beanType, String viewName, String i18nPrefix, String confirmMessage, String pickMessage) {
		super(beanType);

		this.viewName = viewName;
		this.i18nPrefix = i18nPrefix;
		this.confirmMessage = confirmMessage;
		this.pickMessage = pickMessage;

		setSizeFull();
		setSelectionMode(SelectionMode.NONE);

		addComponentColumn(this::buildButtonLayout).setId(COLUMN_ACTIONS);

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
				new ExternalResource(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + viewName + "/" + indexDto.getUuid()));
			link.setTargetName("_blank");
			return link;
		}).setId(COLUMN_UUID);

		buildColumns();

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(i18nPrefix, column.getId(), column.getCaption()));
			column.setMaximumWidth(300);
		}
		getColumn(COLUMN_ACTIONS).setCaption("");
		getColumn(COLUMN_UUID).setCaption(I18nProperties.getPrefixCaption(i18nPrefix, UUID));
		getColumn(COLUMN_COMPLETENESS).setCaption(I18nProperties.getPrefixCaption(i18nPrefix, COMPLETENESS));
		getColumn(COLUMN_COMPLETENESS).setSortable(false);

		this.setStyleGenerator((StyleGenerator<T1>) item -> {
			TreeDataProvider<T1> dataProvider = (TreeDataProvider<T1>) getDataProvider();
			TreeData<T1> data = dataProvider.getTreeData();

			if (data.getRootItems().contains(item)) {
				return "v-treegrid-parent-row";
			} else {
				return "v-treegrid-child-row";
			}
		});
	}

	protected abstract void buildColumns();

	@SuppressWarnings("unchecked")
	private HorizontalLayout buildButtonLayout(T1 item) {
		TreeDataProvider<T1> dataProvider = (TreeDataProvider<T1>) getDataProvider();
		TreeData<T1> data = dataProvider.getTreeData();

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(false);
		Button btnMerge = ButtonHelper.createIconButton(Captions.actionMerge, VaadinIcons.COMPRESS_SQUARE, e -> {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmChoice),
				new Label(I18nProperties.getString(confirmMessage)),
				I18nProperties.getCaption(Captions.actionConfirm),
				I18nProperties.getCaption(Captions.actionCancel),
				640,
				confirmed -> {
					if (confirmed) {
						T1 itemToMergeAndDelete = data.getParent(item) != null ? data.getParent(item) : data.getChildren(item).get(0);
						merge(item, itemToMergeAndDelete);
					}
				});
		});
		Button btnPick = ButtonHelper.createIconButton(Captions.actionPick, VaadinIcons.CHECK, e -> {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmChoice),
				new Label(I18nProperties.getString(pickMessage)),
				I18nProperties.getCaption(Captions.actionConfirm),
				I18nProperties.getCaption(Captions.actionCancel),
				640,
				confirmed -> {
					if (confirmed) {
						T1 itemToDelete = data.getParent(item) != null ? data.getParent(item) : data.getChildren(item).get(0);
						pick(item, itemToDelete);
					}
				});
		});

		Button btnHide = null;

		if (data.getParent(item) == null) {
			CssStyles.style(btnMerge, CssStyles.HSPACE_RIGHT_5, ValoTheme.BUTTON_PRIMARY);
			CssStyles.style(btnPick, CssStyles.HSPACE_RIGHT_5, ValoTheme.BUTTON_PRIMARY);

			btnHide = ButtonHelper.createIconButton(Captions.actionHide, VaadinIcons.CLOSE, e -> {
				hiddenUuidPairs.add(
					new String[] {
						item.getUuid(),
						data.getChildren(item).get(0).getUuid() });
				dataProvider.getTreeData().removeItem(item);
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

		TreeDataProvider<T1> dataProvider = (TreeDataProvider<T1>) getDataProvider();
		TreeData<T1> data = dataProvider.getTreeData();
		data.clear();

		if (hiddenUuidPairs == null) {
			hiddenUuidPairs = new ArrayList<>();
		}

		List<T1[]> itemPairs = getItemForDuplicateMerging();
		for (T1[] itemPair : itemPairs) {
			boolean uuidPairExists = false;
			for (String[] hiddenUuidPair : hiddenUuidPairs) {
				if (hiddenUuidPair[0].equals(itemPair[0].getUuid()) && hiddenUuidPair[1].equals(itemPair[1].getUuid())) {
					uuidPairExists = true;
				}
			}

			if (uuidPairExists) {
				continue;
			}

			data.addItem(null, itemPair[0]);
			data.addItem(itemPair[0], itemPair[1]);
		}

		expandRecursively(data.getRootItems(), 0);
		dataProvider.refreshAll();
	}

	public void reload(boolean ignoreRegion) {
		this.ignoreRegion = ignoreRegion;
		reload();
	}

	protected abstract List<T1[]> getItemForDuplicateMerging();

	protected abstract void merge(T1 targetedItem, T1 itemToMergeAndDelete);

	protected abstract void pick(T1 targetedItem, T1 itemToDelete);

	public abstract void calculateCompletenessValues();

	public void setCriteria(T2 criteria) {
		this.criteria = criteria;
	}
}
