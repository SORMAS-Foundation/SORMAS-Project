/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

/**
 * Zum einfacheren Erstellen von CustomLayouts.<br/>
 * <p>
 * Ist weder performant noch gegen Injection abgesichert.
 *
 * @author HReise
 */
public final class LayoutUtil {

	private LayoutUtil() {
		// Hide Utility Class Constructor
	}

	public static String locCss(String cssClasses, String location) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div ");
		if (cssClasses != null) {
			sb.append("class='").append(cssClasses).append("' ");
		}
		sb.append("location='").append(location).append("'></div>");

		return sb.toString();
	}

	public static String loc(String location) {
		return locCss(null, location);
	}

	public static String locs(String... locations) {
		StringBuilder sb = new StringBuilder();
		for (String location : locations) {
			sb.append("<div location='").append(location).append("'></div>\n");
		}
		return sb.toString();
	}

	public static String locsCss(String css, String... locations) {
		return divCss(css, locs(locations));
	}

	public static String inlineLocs(String... locations) {
		return locsCss("inline-container", locations);
	}

	public static String filterLocs(String... locations) {
		return locsCss("filters-container", locations);
	}

	public static String filterLocsCss(String css, String... locations) {
		return locsCss("filters-container" + " " + css, locations);
	}

	/**
	 * Ein Html-Element
	 *
	 * @param cssClasses
	 * @param meta
	 * @return
	 */
	public static String element(String type, String cssClasses, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(type);
		if (cssClasses != null) {
			sb.append(" class='").append(cssClasses).append("'");
		}
		sb.append(">");

		sb.append(content);

		sb.append("</").append(type).append(">");

		return sb.toString();
	}

	public static String div(String content) {
		return divCss(null, content);
	}

	public static String divCss(String cssClasses, String content) {
		return element("div", cssClasses, content);
	}

	public static String divs(String... contents) {
		return divsCss(null, contents);
	}

	public static String divsCss(String cssClasses, String... contents) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div ");

		if (cssClasses != null) {
			sb.append("class='").append(cssClasses).append("' ");
		}

		sb.append(">");

		for (String content : contents) {
			sb.append(element("div", null, content));
		}

		sb.append("</div>");
		return sb.toString();
	}

	public static String span(String content) {
		return element("span", null, content);
	}

	public static String spanCss(String cssClasses, String content) {
		return element("span", cssClasses, content);
	}

	public static String h1(String content) {
		return element("h1", null, span(content));
	}

	public static String h2(String content) {
		return element("h2", null, span(content));
	}

	public static String h3(String content) {
		return element("h3", null, span(content));
	}

	public static String h4(String content) {
		return element("h4", null, span(content));
	}

	public static String strong(String cssClasses, String content) {
		return element("strong", cssClasses, content);
	}

	public static String small(String cssClasses, String content) {
		return element("small", cssClasses, content);
	}

	private static int calcFluidColWidth(FluidColumn[] cols) {
		int w;
		switch (cols.length) {
		case 1:
			w = 12;
			break;
		case 2:
			w = 6;
			break;
		case 3:
			w = 4;
			break;
		case 4:
			w = 3;
			break;
		case 5:
		case 6:
			w = 2;
			break;
		default:
			w = 1;
		}
		return w;
	}

	public static String fluidRowLocs(String... locs) {
		return fluidRowLocsCss(null, locs);
	}

	public static String fluidRowLocsCss(String cssClasses, String... locs) {
		FluidColumn[] cols = new FluidColumn[locs.length];
		int w = calcFluidColWidth(cols);

		for (int i = 0; i < cols.length; i++) {
			cols[i] = fluidColumnLoc(w, 0, locs[i]);
		}
		return LayoutUtil.fluidRowCss(cssClasses, cols);
	}

	public static String fluidRowLoc(int spanA, String locA) {
		return LayoutUtil.fluidRow(fluidColumnLoc(spanA, 0, locA));
	}

	public static String fluidRowLocs(int spanA, String locA, int spanB, String locB) {
		return LayoutUtil.fluidRow(fluidColumnLoc(spanA, 0, locA), fluidColumnLoc(spanB, 0, locB));
	}

	public static String fluidRowLocs(int spanA, String locA, int spanB, String locB, int spanC, String locC) {
		return LayoutUtil.fluidRow(fluidColumnLoc(spanA, 0, locA), fluidColumnLoc(spanB, 0, locB), fluidColumnLoc(spanC, 0, locC));
	}

	public static String fluidRow(String... columns) {
		return fluidRowCss(null, columns);
	}

	public static String fluidRowCss(String cssClasses, String... columns) {

		FluidColumn[] cols = new FluidColumn[columns.length];
		int w = calcFluidColWidth(cols);

		for (int i = 0; i < cols.length; i++) {
			cols[i] = fluidColumnCss(null, w, 0, columns[i]);
		}
		return LayoutUtil.fluidRowCss(cssClasses, cols);
	}

	public static String fluidRow(FluidColumn... columns) {
		return fluidRowCss(null, columns);
	}

	public static String fluidRowCss(String cssClasses, FluidColumn... columns) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class='row");
		if (cssClasses != null) {
			sb.append(' ').append(cssClasses);
		}
		sb.append("'");
		sb.append(">");

		for (FluidColumn fluidColumn : columns) {
			sb.append(fluidColumn);
		}

		sb.append("</div>");

		return sb.toString();
	}

	public static FluidColumn fluidColumnCss(String cssClasses, int span, int offset, String content) {
		return new FluidColumn(cssClasses, span, offset, null, content);
	}

	public static FluidColumn fluidColumn(int span, int offset, String content) {
		return new FluidColumn(null, span, offset, null, content);
	}

	public static FluidColumn fluidColumn(int span, int offset, int spanSmall, int offsetSmall, String content) {
		return new FluidColumn(null, span, offset, spanSmall, offsetSmall, null, content);
	}

	public static FluidColumn fluidColumnLoc(int span, int offset, String loc) {
		return new FluidColumn(null, span, offset, loc, null);
	}

	public static FluidColumn fluidColumnLoc(int span, int offset, int spanSmall, int offsetSmall, String loc) {
		return new FluidColumn(null, span, offset, spanSmall, offsetSmall, loc, null);
	}

	public static FluidColumn fluidColumnLocCss(String cssClasses, int span, int offset, String loc) {
		return new FluidColumn(cssClasses, span, offset, loc, null);
	}

	public static FluidColumn oneOfThreeCol(String loc) {
		return fluidColumnLoc(4, 0, loc);
	}

	public static FluidColumn twoOfThreeCol(String loc) {
		return fluidColumnLoc(8, 0, loc);
	}

	public static FluidColumn oneOfTwoCol(String loc) {
		return fluidColumnLoc(6, 0, loc);
	}

	public static FluidColumn oneOfFourCol(String loc) {
		return fluidColumnLoc(3, 0, loc);
	}

	public static FluidColumn threeOfFourCol(String loc) {
		return fluidColumnLoc(9, 0, loc);
	}

	public static FluidColumn oneOfSixCol(String loc) {
		return fluidColumnLoc(2, 0, loc);
	}

	public static final class FluidColumn {

		private final String str;

		public FluidColumn(String cssClasses, int span, int offset, int spanSmall, int offsetSmall, String location, String content) {

			StringBuilder sb = new StringBuilder();
			sb.append("<div ");
			if (cssClasses != null || span > 0 || offset > 0) {
				sb.append(" class='");
				if (span > 0) {
					sb.append("col-lg-").append(span).append(' ');
					sb.append("col-xs-").append(spanSmall).append(' ');
				}
				if (offset > 0) {
					sb.append("col-lg-offset-").append(offset).append(' ');
					sb.append("col-xs-offset-").append(offsetSmall).append(' ');
				}
				if (cssClasses != null) {
					sb.append(cssClasses);
				}
				sb.append("'");
			}
			if (location != null) {
				sb.append(" location='").append(location).append("'");
			}

			sb.append(">");

			if (content != null) {
				sb.append(content);
			}

			sb.append("</div>");

			str = sb.toString();
		}

		public FluidColumn(String cssClasses, int span, int offset, String location, String content) {

			StringBuilder sb = new StringBuilder();
			sb.append("<div ");
			if (cssClasses != null || span > 0 || offset > 0) {
				sb.append(" class='");
				if (span > 0) {
					sb.append("col-xs-").append(span).append(' ');
					sb.append("col-md-").append(span).append(' ');
				}
				if (offset > 0) {
					sb.append("col-xs-offset-").append(offset).append(' ');
					sb.append("col-md-offset-").append(offset).append(' ');
				}
				if (cssClasses != null) {
					sb.append(cssClasses);
				}
				sb.append("'");
			}
			if (location != null) {
				sb.append(" location='").append(location).append("'");
			}

			sb.append(">");

			if (content != null) {
				sb.append(content);
			}

			sb.append("</div>");

			str = sb.toString();
		}

		@Override
		public String toString() {
			return str;
		}
	}
}
