package de.symeda.sormas.ui.utils;

/**
 * Zum einfacheren Erstellen von CustomLayouts.<br/>
 * 
 * Ist weder performant noch gegen Injection abgesichert.
 * 
 * @author HReise
 *
 */
public class LayoutUtil {


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

	public static String flow(String... htmls) {
		StringBuilder sb = new StringBuilder();
		for (String html : htmls) {
			sb.append("<div class='").append(CssStyles.FLOW_LAYOUT).append("'>").append(html).append("</div>\n");
		}
		return sb.toString();
	}

	public static String locs(String... locations) {
		StringBuilder sb = new StringBuilder();
		for (String location : locations) {
			sb.append("<div location='").append(location).append("'></div>\n");
		}
		return sb.toString();
	}

	public static String flowLocs(String... locations) {
		StringBuilder sb = new StringBuilder();
		for (String location : locations) {
			sb.append("<div class='").append(CssStyles.FLOW_LAYOUT).append("' location='").append(location).append("'></div>\n");
		}
		return sb.toString();
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

	public static String span(String content) {
		return element("span", null, content);
	}

	public static String spanCss(String cssClasses, String content) {
		return element("span", cssClasses, content);
	}

	public static String h1(String cssClasses, String content) {
		return element("h1", cssClasses, content);
	}

	public static String h2(String cssClasses, String content) {
		return element("h2", cssClasses, content);
	}

	public static String h3(String cssClasses, String content) {
		return element("h3", cssClasses, content);
	}

	public static String h4(String cssClasses, String content) {
		return element("h4", cssClasses, content);
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
			cols[i] = fluidColumnCss(null, w, 0, locCss(null, locs[i]));
		}
		return LayoutUtil.fluidRowCss(cssClasses, cols);
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
		sb.append("<div class='row-fluid");
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
		return new FluidColumn(cssClasses, span, offset, content);
	}

	public static FluidColumn fluidColumn(int span, int offset, String content) {
		return new FluidColumn(null, span, offset, content);
	}

	public static String div(String... contents) {
		return divCss(null, contents);
	}
	
	public static String divCss(String cssClasses, String... contents) {
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

//	public static String escape(String stringToBeEscaped) {
//		if (stringToBeEscaped == null) {
//			return "";
//		}
//		return StringEscapeUtils.escapeHtml4(stringToBeEscaped);
//	}
	
	
	/**
	 * Erstellt eine "Drittel"-Spalte (span4).
	 */
	public static FluidColumn oneOfThreeCol(String content) {
		return fluidColumnCss(null, 4, 0, content);
	}

	/**
	 * Erstellt eine "Zwei-Drittel"-Spalte (span8).
	 */
	public static FluidColumn twoOfThreeCol(String content) {
		return fluidColumnCss(null, 8, 0, content);
	}

	/**
	 * Erstellt eine "Halb"-Spalte (span6).
	 */
	public static FluidColumn oneOfTwoCol(String content) {
		return fluidColumnCss(null, 6, 0, content);
	}

	public static FluidColumn oneOfTwoCol(String cssClasses, String content) {
		return fluidColumnCss(cssClasses, 6, 0, content);
	}

	public static final class FluidColumn {

		private final String str;

		public FluidColumn(String cssClasses, int span, int offset,
				String content) {

			StringBuilder sb = new StringBuilder();
			sb.append("<div ");
			if (cssClasses != null || span > 0 || offset > 0) {
				sb.append(" class='");
				if (span > 0) {
					sb.append("span").append(span).append(' ');
				}
				if (offset > 0) {
					sb.append("offset").append(offset).append(' ');
				}
				if (cssClasses != null) {
					sb.append(cssClasses);
				}
				sb.append("'");
			}
			sb.append(">");

			sb.append(content);

			sb.append("</div>");

			str = sb.toString();
		}

		@Override
		public String toString() {
			return str;
		}

	}
}
