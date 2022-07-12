package de.symeda.sormas.ui.campaign.expressions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;

@Tag("div")
	public class Texttest extends Component {

	    Element labelElement = new Element("label");
	    Element inputElement = new Element("input");

	    public Texttest() {
	        inputElement
	            .addPropertyChangeListener("value", "change", e -> {});
	        getElement()
	            .appendChild(labelElement, inputElement);
	    }

	}
