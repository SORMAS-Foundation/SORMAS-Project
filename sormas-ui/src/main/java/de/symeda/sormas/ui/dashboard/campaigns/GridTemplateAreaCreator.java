package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;

public class GridTemplateAreaCreator {

	public static final String APOSTROPHE = "'";

	public String createGridTemplate(List<CampaignDashboardElement> dashboardElements) {

		dashboardElements =
			dashboardElements.stream().sorted(Comparator.comparingInt(CampaignDashboardElement::getOrder)).collect(Collectors.toList());

		final List<Integer> widths = dashboardElements.stream().map(cde -> cde.getWidth()).collect(Collectors.toList());
		final List<Integer> heights = dashboardElements.stream().map(cde -> cde.getHeight()).collect(Collectors.toList());
		final Integer oneWidthAreaPercentage = gcd(widths);
		final Integer oneHeightAreaPercentage = gcd(heights);
		final Integer nrOfGridAreaColumns = 100 / oneWidthAreaPercentage;
		final Integer widthsSum = widths.stream().reduce(0, Integer::sum);
		final Integer nrOfGridAreaRows = (widthsSum / 100 + (widthsSum % 100 == 0 ? 0 : 1)) * 100 / gcd(heights);

		final String[][] grid = new String[nrOfGridAreaColumns][nrOfGridAreaRows];

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
			final GridElementIndex nextDiagramFirstGridElement = findNextDiagramsStartingIndex(
				dashboardElements,
				elementIndex,
				oneWidthAreaPercentage,
				widthAreas,
				heightAreas,
				nrOfGridAreaColumns,
				nrOfGridAreaRows,
				grid,
				startingRow,
				startingColumn);
			startingColumn = nextDiagramFirstGridElement.getX();
			startingRow = nextDiagramFirstGridElement.getY();
		}

		return formatGridArea(nrOfGridAreaColumns, nrOfGridAreaRows, grid);
	}

	private String formatGridArea(Integer nrOfGridColumnAreas, Integer nrOfGridRowAreas, String[][] grid) {
		final StringBuilder result = new StringBuilder();

		for (int x = 0; x < nrOfGridRowAreas; x++) {
			if (rowIsNull(grid, x, nrOfGridColumnAreas)) {
				continue;
			} else {
				result.append(APOSTROPHE);
				for (int y = 0; y < nrOfGridColumnAreas; y++) {
					result.append(grid[y][x] + (y == nrOfGridColumnAreas - 1 ? StringUtils.EMPTY : StringUtils.SPACE));
				}
				result.append(APOSTROPHE);
			}
		}
		return result.toString();
	}

	private boolean rowIsNull(String[][] grid, int x, Integer nrOfGridColumnAreas) {
		for (int y = 0; y < nrOfGridColumnAreas; y++) {
			if (grid[y][x] != null)
				return false;
		}
		return true;
	}

	private GridElementIndex findNextDiagramsStartingIndex(
		List<CampaignDashboardElement> dashboardElements,
		int elementIndex,
		int oneWidthAreaPercentage,
		int widthAreas,
		int heightAreas,
		int nrOfGridAreaColumns,
		int nrOfGridAreaRows,
		String[][] grid,
		int startingRow,
		int startingColumn) {
		if (elementIndex < dashboardElements.size() - 1) {
			final CampaignDashboardElement nextCampaignDashboardElement = dashboardElements.get(elementIndex + 1);
			int nextWidthAreas = nextCampaignDashboardElement.getWidth() / oneWidthAreaPercentage;
			if (widthAreas + startingColumn + nextWidthAreas <= nrOfGridAreaColumns) {
				startingColumn += widthAreas;
			} else {
				final GridElementIndex firstEmptyGridElement = findFirstEmptyGridElement(grid, nrOfGridAreaColumns, nrOfGridAreaRows);
				if (startingRow + heightAreas >= nrOfGridAreaRows
					|| (firstEmptyGridElement.getY() < startingRow + heightAreas && nextWidthAreas <= nrOfGridAreaColumns - widthAreas)) {
					startingColumn = firstEmptyGridElement.getX();
					startingRow = firstEmptyGridElement.getY();
				} else {
					startingRow += heightAreas;
					startingColumn = startingRow == nrOfGridAreaRows || grid[0][startingRow] != null ? startingColumn : 0;
				}
			}
		}
		return new GridElementIndex(startingColumn, startingRow);
	}

	private GridElementIndex findFirstEmptyGridElement(String[][] matrix, Integer nrOfColumns, Integer nrOfRows) {
		for (int x = 0; x < nrOfColumns; x++) {
			for (int y = 0; y < nrOfRows; y++) {
				if (matrix[x][y] == null) {
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
