package de.symeda.sormas.ui.dashboard;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class SvgBarElement extends Label {
	
	private static final int BAR_HEIGHT = 4;
	
	private String fillClass;
	
	public SvgBarElement(String fillClass) {
		this.fillClass = fillClass;
		setContentMode(ContentMode.HTML);
		setHeight(BAR_HEIGHT, Unit.PIXELS);
		updateSvg(100);
	}
	
	public void updateSvg(int percentageValue) {
		setValue("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100%\" height=\"" + BAR_HEIGHT + "px\">"
				+ "<rect class=\"svg-bar " + fillClass + "\" width=\"" + percentageValue + "%\" height=\"" + BAR_HEIGHT + "px\"/>"
				+ "<rect class=\"svg-bar " + CssStyles.SVG_FILL_BACKGROUND + "\" x=\"" + percentageValue + "%\" width=\"" + (100 - percentageValue) + "%\" height=\"" + BAR_HEIGHT + "px\"/>"
				+ "</svg>");
	}

}
