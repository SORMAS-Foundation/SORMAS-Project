package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;

public class GridTemplateAreaCreator {

	public static final String APOSTROPHE = "'";

	private String[][] grid;
	private Integer widthsSum;
	private Integer nrOfGridAreaColumns;
	private Integer nrOfGridAreaRows;
	private Integer oneWidthAreaPercentage;
	private Integer oneHeightAreaPercentage;

	public GridTemplateAreaCreator(List<CampaignDashboardElement> dashboardElements) {
		dashboardElements =
			dashboardElements.stream().sorted(Comparator.comparingInt(CampaignDashboardElement::getOrder)).collect(Collectors.toList());

		final List<Integer> widths = dashboardElements.stream().map(cde -> cde.getWidth()).collect(Collectors.toList());
		final List<Integer> heights = dashboardElements.stream().map(cde -> cde.getHeight()).collect(Collectors.toList());
		oneWidthAreaPercentage = gcd(widths);
		oneHeightAreaPercentage = gcd(heights);
		nrOfGridAreaColumns = 100 / oneWidthAreaPercentage;
		widthsSum = widths.stream().reduce(0, Integer::sum);
		nrOfGridAreaRows = (widthsSum / 100 + (widthsSum % 100 == 0 ? 0 : 1)) * 100 / gcd(heights); //grid-container

		grid = new String[nrOfGridAreaColumns][nrOfGridAreaRows];

		int startingColumn = 0;
		int startingRow = 0;

		for (int elementIndex = 0; elementIndex < dashboardElements.size(); elementIndex++) {
			final CampaignDashboardElement campaignDashboardElement = dashboardElements.get(elementIndex);
			final int widthAreas = campaignDashboardElement.getWidth() / oneWidthAreaPercentage;
			final int heightAreas = campaignDashboardElement.getHeight() / oneHeightAreaPercentage;

			for (int y = 0; y < widthAreas; y++) {
				for (int x = 0; x < heightAreas; x++) {
					grid[y + startingColumn][x + startingRow] = campaignDashboardElement.getDiagramId();
				}
			}
			final GridElementIndex nextDiagramFirstGridElement =
				findNextDiagramsStartPosition(dashboardElements, elementIndex, oneWidthAreaPercentage, widthAreas, startingRow, startingColumn);
			startingColumn = nextDiagramFirstGridElement.getX();
			startingRow = nextDiagramFirstGridElement.getY();
		}
	}

	public String getFormattedGridTemplate() {
		final StringBuilder result = new StringBuilder();

		for (int x = 0; x < nrOfGridAreaRows; x++) {
			if (rowIsNull(x, nrOfGridAreaColumns)) {
				continue;
			} else {
				result.append(APOSTROPHE);
				for (int y = 0; y < nrOfGridAreaColumns; y++) {
					final String diagramId = grid[y][x];
					final String area = diagramId != null ? diagramId : ("area" + x);
					result.append(area + (y == nrOfGridAreaColumns - 1 ? StringUtils.EMPTY : StringUtils.SPACE));
				}
				result.append(APOSTROPHE);
			}
		}
		return result.toString();
	}

	public Integer getGridContainerHeight() {
		int nonNullRows = 0;
		for (int x = 0; x < nrOfGridAreaRows; x++) {
			if (!rowIsNull(x, nrOfGridAreaColumns)) {
				nonNullRows++;
			}
		}
		return nonNullRows * oneHeightAreaPercentage;
	}

	public Integer getWidthsSum() {
		return widthsSum;
	}

	public Integer getGridColumns() {
		return nrOfGridAreaColumns;
	}

	public int getGridRows() {
		int rows = 0;
		for (int x = 0; x < nrOfGridAreaRows; x++) {
			if (rowIsNull(x, nrOfGridAreaColumns)) {
				continue;
			} else {
				rows++;
			}
		}
		return rows;
	}

	private boolean rowIsNull(int x, Integer nrOfGridColumnAreas) {
		for (int y = 0; y < nrOfGridColumnAreas; y++) {
			if (grid[y][x] != null)
				return false;
		}
		return true;
	}

	private GridElementIndex findNextDiagramsStartPosition(
		List<CampaignDashboardElement> dashboardElements,
		int elementIndex,
		int oneWidthAreaPercentage,
		int widthAreas,
		int startingRow,
		int startingColumn) {
		if (elementIndex < dashboardElements.size() - 1) {
			final CampaignDashboardElement nextCampaignDashboardElement = dashboardElements.get(elementIndex + 1);
			int nextWidthAreas = nextCampaignDashboardElement.getWidth() / oneWidthAreaPercentage;
			if (widthAreas + startingColumn + nextWidthAreas <= nrOfGridAreaColumns) {
				startingColumn += widthAreas;
			} else {
				final int nextStartingColumn =
					startingColumn == 0 || (startingColumn + widthAreas) >= nrOfGridAreaColumns ? widthAreas : startingColumn + widthAreas;
				final int nrOfColumnsToBeParsed =
					nextStartingColumn + nextWidthAreas <= nrOfGridAreaColumns ? nrOfGridAreaColumns : nextStartingColumn;
				final GridElementIndex firstEmptyGridElement = findFirstEmptyGridElement(nrOfColumnsToBeParsed, nrOfGridAreaRows);
				startingColumn = firstEmptyGridElement.getX();
				startingRow = firstEmptyGridElement.getY();
			}
		}
		return new GridElementIndex(startingColumn, startingRow);
	}

	private GridElementIndex findFirstEmptyGridElement(Integer nrOfColumnsToBeParsed, Integer nrOfRows) {
		for (int y = 0; y < nrOfRows; y++) {
			for (int x = 0; x < nrOfColumnsToBeParsed; x++) {
				if (grid[x][y] == null) {
					return new GridElementIndex(x, y);
				}
			}
		}
		return null;
	}

	private static Long gcd(long a, long b) {
		while (b > 0) {
			long temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}

	private static Integer gcd(List<Integer> input) {
		Integer result = input.get(0);
		for (int i = 1; i < input.size(); i++) {
			result = gcd((long) result, (long) input.get(i)).intValue();
		}
		return result;
	}

	private static class GridElementIndex {

		private int x;
		private int y;

		public GridElementIndex(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

}
