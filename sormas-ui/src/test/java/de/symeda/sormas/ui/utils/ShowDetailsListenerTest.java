package de.symeda.sormas.ui.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;

import org.junit.Test;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.ItemClick;

import de.symeda.sormas.api.contact.ContactIndexDto;

/**
 * @see ShowDetailsListener
 */
public class ShowDetailsListenerTest {

	@Test
	@SuppressWarnings({
		"rawtypes",
		"unchecked" })
	public void testItemClickDoesNotCallHandlerWhenItemIsNull() {

		Consumer<ContactIndexDto> handler = mock(Consumer.class);
		String detailsColumnId = ContactIndexDto.UUID;
		ShowDetailsListener<ContactIndexDto> cut = new ShowDetailsListener<>(detailsColumnId, handler);

		ItemClick<ContactIndexDto> event = mock(ItemClick.class);
		ContactIndexDto item = mock(ContactIndexDto.class);

		// 1a. ColumnClick
		Column column = mock(Column.class);
		when(event.getColumn()).thenReturn(column);
		when(column.getId()).thenReturn(detailsColumnId);
		cut.itemClick(event);
		verify(handler, never()).accept(null);

		// 1b. ColumnClick doing it when item is set
		when(event.getItem()).thenReturn(item);
		cut.itemClick(event);
		verify(handler).accept(item);

		reset(handler, event);

		// 2a. DoubleClick
		MouseEventDetails mouseEventDetails = mock(MouseEventDetails.class);
		when(event.getMouseEventDetails()).thenReturn(mouseEventDetails);
		when(mouseEventDetails.isDoubleClick()).thenReturn(true);
		cut.itemClick(event);
		verify(handler, never()).accept(null);

		// 2b. DoubleClick doing it when item is set
		when(event.getItem()).thenReturn(item);
		cut.itemClick(event);
		verify(handler).accept(item);
	}
}
