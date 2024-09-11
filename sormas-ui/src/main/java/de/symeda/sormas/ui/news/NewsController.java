package de.symeda.sormas.ui.news;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.news.NewsDto;
import de.symeda.sormas.api.news.NewsIndexDto;
import de.symeda.sormas.api.news.NewsReferenceDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.BulkOperationHandler;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class NewsController {

	public static void registerViews(Navigator navigator) {
		navigator.addView(NewsView.VIEW_NAME, NewsView.class);
		navigator.addView(NewsDataView.VIEW_NAME, NewsDataView.class);
	}

	public void updateNewsPopUP(NewsIndexDto newsIndexDto) {
		NewsDto newsDto = FacadeProvider.getNewsFacade().getByUuid(newsIndexDto.getUuid());
		NewsPopUp newsPopUpForm = new NewsPopUp(newsIndexDto.getNewsLink());
		newsPopUpForm.setWidth(98f, Unit.PERCENTAGE);
		newsPopUpForm.setValue(newsDto);
		CommitDiscardWrapperComponent<NewsPopUp> commitDiscardWrapperComponent = getNewsUpdateComponent(newsPopUpForm);
		Window window = VaadinUiUtil.showModalPopupWindow(commitDiscardWrapperComponent, I18nProperties.getCaption(Captions.updateNews));
		window.setWidth(90f, Unit.PERCENTAGE);
		window.setHeight(85f, Unit.PERCENTAGE);
	}

	public NewsDto create() {
		NewsDataForm newsDataForm = new NewsDataForm(true);
		newsDataForm.setValue(NewsDto.build());
		CommitDiscardWrapperComponent<NewsDataForm> createForm =
			new CommitDiscardWrapperComponent<>(newsDataForm, true, newsDataForm.getFieldGroup());
		createForm.addCommitListener(() -> {
			if (!newsDataForm.getFieldGroup().isModified()) {
				NewsDto dto = FacadeProvider.getNewsFacade().save(newsDataForm.getValue());
				Notification.show("News created", Notification.Type.WARNING_MESSAGE);
				navigateToData(dto.getUuid());
			}
		});
		NewsDto news = createForm.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(createForm, I18nProperties.getCaption(Captions.createNew));
		return news;
	}

	private CommitDiscardWrapperComponent<NewsPopUp> getNewsUpdateComponent(NewsPopUp newsPopUpForm) {
		CommitDiscardWrapperComponent<NewsPopUp> commitDiscardWrapperComponent = new CommitDiscardWrapperComponent<>(newsPopUpForm);
		commitDiscardWrapperComponent.addCommitListener(() -> {
			newsPopUpForm.commit();
			NewsDto value = newsPopUpForm.getValue();
			FacadeProvider.getNewsFacade().save(value);
			navigateToIndex();
		});
		return commitDiscardWrapperComponent;
	}

	public void approveNews(Collection<NewsIndexDto> selectedRows, NewsGrid grid) {
		BulkOperationHandler.<NewsIndexDto> forBulkEdit().doBulkOperation(batch -> {
			List<ProcessedEntity> processNews = new ArrayList<>();
			for (NewsIndexDto newsIndexDto : batch) {
				try {
					FacadeProvider.getNewsFacade().markApprove(new NewsReferenceDto(newsIndexDto.getUuid()));
					processNews.add(new ProcessedEntity(newsIndexDto.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (Exception e) {
					processNews.add(new ProcessedEntity(newsIndexDto.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
				}
			}
			return processNews;
		}, new ArrayList<>(selectedRows), bulkOperationCallback(grid));
	}

	public void markAsUnUseful(Collection<NewsIndexDto> selectedRows, NewsGrid grid) {
		BulkOperationHandler.<NewsIndexDto> forBulkEdit().doBulkOperation(batch -> {
			List<ProcessedEntity> processNews = new ArrayList<>();
			for (NewsIndexDto newsIndexDto : batch) {
				try {
					FacadeProvider.getNewsFacade().markUnUseful(new NewsReferenceDto(newsIndexDto.getUuid()));
					processNews.add(new ProcessedEntity(newsIndexDto.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (Exception e) {
					processNews.add(new ProcessedEntity(newsIndexDto.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
				}
			}
			return processNews;
		}, new ArrayList<>(selectedRows), bulkOperationCallback(grid));
	}

	private Consumer<List<NewsIndexDto>> bulkOperationCallback(NewsGrid newsGrid) {
		return remainingEvents -> {
			newsGrid.reload();
			if (remainingEvents != null && !remainingEvents.isEmpty()) {
				newsGrid.asMultiSelect().selectItems(remainingEvents.toArray(new NewsIndexDto[0]));
			} else {
				navigateToIndex();
			}
		};
	}

	public void navigateToIndex() {
		SormasUI.get().getNavigator().navigateTo(NewsView.VIEW_NAME);
	}

	public CommitDiscardWrapperComponent<NewsDataForm> getNewsEditComponent(String uuid, boolean editAllowed) {
		NewsDto eventDto = FacadeProvider.getNewsFacade().getByUuid(uuid);
		NewsDataForm editForm = new NewsDataForm(false);
		editForm.setValue(eventDto);
		CommitDiscardWrapperComponent<NewsDataForm> editComponent = new CommitDiscardWrapperComponent<>(editForm, true, editForm.getFieldGroup());
		editComponent.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				NewsDto dto = FacadeProvider.getNewsFacade().save(editForm.getValue());
				Notification.show(I18nProperties.getCaption(Captions.newsUpdate), Notification.Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			}
		});
		editComponent.setEditable(editAllowed);
		return editComponent;
	}

	public void navigateToData(String uuid) {
		String navigationState = NewsDataView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public Component getNewsHeaderComponent(String uuid) {
		NewsDto newsDto = FacadeProvider.getNewsFacade().getByUuid(uuid);
		final TitleLayout titleLayout = new TitleLayout();
		titleLayout.addMainRow(newsDto.getTitle());
		return titleLayout;
	}
}
