package de.symeda.sormas.ui.utils;

import com.vaadin.ui.Component;

import java.util.function.Consumer;

public interface WithChildComponents extends Component {

    void forEachComponent(Consumer<Component> componentConsumer);
}
