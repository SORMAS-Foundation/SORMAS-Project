package de.symeda.sormas.ui.dashboard;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class SvgCircleElement extends Label {
	
	private final boolean showPercentage;
	private int percentage;
	
	public SvgCircleElement(boolean showPercentage) {
		setContentMode(ContentMode.HTML);
		this.showPercentage = showPercentage;
		updateSvg();
	}

	public void updateSvg(int percentage, SvgCircleElementPart ...svgCircleElementParts) {
		this.percentage = percentage;
		updateSvg(svgCircleElementParts);
	}
	
	public void updateSvg(SvgCircleElementPart ...svgCircleElementParts) {
		StringBuilder sb = new StringBuilder();
		if (svgCircleElementParts.length > 0) {
			sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" viewbox=\"0 0 36 36\">");
			int percentageSoFar = 0;
			for (SvgCircleElementPart circlePart : svgCircleElementParts) {
				sb.append("<path class=\"svg-circle " + circlePart.strokeClass + "\" "
						+ "stroke-dasharray=\"" + circlePart.percentage + ", 100\" "
						+ "stroke-dashoffset=\"-" + percentageSoFar + "\" "
						+ "d=\"M18 2.0845 "
						+ "a 15.9155 15.9155 0 0 1 0 31.831 "
						+ "a 15.9155 15.9155 0 0 1 0 -31.831\"/>");
				percentageSoFar += circlePart.percentage;
			}
			if (showPercentage) {
				sb.append("<text x=\"18\" y=\"20.35\" fill=\"#005A9C\" "
						+ "style=\"font-size: 0.75em; font-weight: bold; text-anchor: middle;\">" 
						+ percentage + "%</text>");
			}
			sb.append("</svg>");
		}
		
		setValue(sb.toString());
	}

	public class SvgCircleElementPart {
		private String strokeClass;
		private int percentage;

		public SvgCircleElementPart(String strokeClass, int percentage) {
			this.strokeClass = strokeClass;
			this.percentage = percentage;
		}
	}

}
