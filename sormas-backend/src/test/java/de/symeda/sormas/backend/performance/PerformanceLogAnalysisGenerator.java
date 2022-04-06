/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

public class PerformanceLogAnalysisGenerator {

	private static final Pattern startPattern =
		Pattern.compile("([^ ]* [^ ]*) TRACE *(.*) d.s.s.b.u.PerformanceLoggingInterceptor - Started: ([^ ]*) with parameters .*$");
	private static final Pattern finishPattern =
		Pattern.compile("([^ ]* [^ ]*) DEBUG *(.*) d.s.s.b.u.PerformanceLoggingInterceptor - Finished in ([0-9]*) ms: (.*)$");

	//@formatter:off
    private static final String HTML_HEADER = "<html>\n" +
            "<header>\n" +
            "<title>Performance Log Analysis</title>" +
            "<style>\n" +
            "* {font-family: Verdana, Arial, Helvetica, sans-serif;}\n" +
            "a {font-weight: bold; color: #666; text-decoration: none;}\n" +
            "a:hover {color: #ff9900;}\n" +
            "th {font-weight: bold; background: #EEF}\n" +
            "th,td {padding: 0 5px 0 5px; vertical-align: top;}\n" +
            "tr:nth-child(odd) {background: #EEE}\n" +
            "tr:nth-child(even) {background: #FFF}\n" +
            "</style>\n" +
            "</header>\n" +
            "<body>\n" +
            "<table>\n";
    private static final String HTML_FOOTER = "</table>\n" +
            "</body>\n" +
            "</html>\n";
    //@formatter:on

	public static final int TIME_TRESHOLD_YELLOW = 100;
	public static final int TIME_TRESHOLD_ORANGE = 300;
	public static final int TIME_TRESHOLD_RED = 1000;

	public static final String OUTPUT_DIRECTORY = "target/performance/";

	private Map<String, Stack<String>> callstacks;

	// PerformanceLogAnalysisGenerator is a tool to generate an analysis of performance log files.
	// To analyze a log file, run with argument <path_to_logfile>. If the argument is omitted, an analysis of the example
	// log file 'exampleApplication.debug' is generated.
	// To enable performance logging, change the log level of the 'PerformanceLoggingInterceptor' to 'TRACE' in the
	// SORMAS server's 'logback.xml':
	// 
	// <logger name="de.symeda.sormas.backend.util.PerformanceLoggingInterceptor" level="TRACE" />
	// 
	// PerformanceLogAnalysisGenerator generates the following files in 'target/performance':
	// 
	// <logfile_name>.html
	// <logfile_name>.csv
	// <logfile_name>.txt
	// 
	// Make sure working directory is 'SORMAS-Project/sormas-backend'

	public static void main(String[] args) throws IOException, URISyntaxException {

		File logFile;

		if (args.length > 0 && args[0] != null) {
			logFile = new File(args[0]);
		} else {
			logFile = new File(PerformanceLogAnalysisGenerator.class.getResource("/performance/exampleApplication.debug").toURI());
		}

		new PerformanceLogAnalysisGenerator().analyzePerformanceLog(logFile);
	}

	public void analyzePerformanceLog(File logFile) throws IOException {

		callstacks = new HashMap<>();
		MethodStats allStats = new MethodStats("all");

		try (BufferedReader reader = new BufferedReader((new FileReader(logFile)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher matcherStart = startPattern.matcher(line);
				Matcher matcherFinish = finishPattern.matcher(line);
				if (matcherStart.find()) {
					String startThread = matcherStart.group(2);
					String startMethod = matcherStart.group(3);

					Stack<String> startCallstack = callstacks.get(startThread);
					if (startCallstack == null) {
						startCallstack = new Stack<>();
						callstacks.put(startThread, startCallstack);
					}
					startCallstack.push(startMethod);
				} else if (matcherFinish.find()) {
					String finishThread = matcherFinish.group(2);
					String finishMethod = matcherFinish.group(4);
					String finishTime = matcherFinish.group(3);

					Stack<String> finishCallstack = callstacks.get(finishThread);
					if (finishCallstack != null && finishMethod.equals(finishCallstack.peek())) {
						finishCallstack.pop();
					}

					long callTime = Long.parseLong(finishTime);
					allStats.callSub(finishMethod, callTime);
					if (finishCallstack != null && !finishCallstack.isEmpty()) {
						allStats.getSub(finishCallstack.peek()).callSub(finishMethod, callTime);
					}

				} else {
					System.out.println("Not recognized:\n" + line);
				}
			}

			String logFilebasename = FilenameUtils.getBaseName(logFile.getName());

			List<MethodStats> allMethodStats = new ArrayList<>(allStats.subcalls.values());
			Collections.sort(allMethodStats);

			File directory = new File(OUTPUT_DIRECTORY);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			FileWriter txtFileWriter = new FileWriter(new File(OUTPUT_DIRECTORY + logFilebasename + ".txt"));
			for (MethodStats method : allMethodStats) {
				txtFileWriter.write(method + "\n");
			}
			txtFileWriter.close();

			FileWriter csvFileWriter = new FileWriter(new File(OUTPUT_DIRECTORY + logFilebasename + ".csv"));
			csvFileWriter.write(allStats.getSubCallsCsv());
			csvFileWriter.close();

			FileWriter htmlFileWriter = new FileWriter(new File(OUTPUT_DIRECTORY + logFilebasename + ".html"));
			htmlFileWriter.append(HTML_HEADER);
			htmlFileWriter.write("<h2>Performance Log <span style=\"background:#eee;\">&nbsp;" + logFile.toURI() + "&nbsp;</span></h2>\n\n");
			htmlFileWriter.write("<h3>Methods started but not finished in this log:</h3>\n\n");
			htmlFileWriter.write(getUnfinishedMethodsHtml());
			htmlFileWriter.write("<h3>Time spent per method:</h3>\n\n");
			htmlFileWriter.write(allStats.getSubCallsTable(true));
			htmlFileWriter.append(HTML_FOOTER);
			htmlFileWriter.close();
		}
	}

	private String getUnfinishedMethodsHtml() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<table>\n");
		stringBuilder.append("<tr><th>method</th></tr>\n");

		Set<String> unfinishedMethods = new HashSet<String>();
		for (Stack<String> stack : callstacks.values()) {
			while (!stack.isEmpty()) {
				unfinishedMethods.add(stack.pop());
			}
		}

		if (unfinishedMethods.isEmpty()) {
			stringBuilder.append("<tr><td>-- all methods completed --</td></tr>\n");
		} else {
			List<String> unfinishedMethodsSorted = Arrays.asList(unfinishedMethods.toArray(new String[unfinishedMethods.size()]));
			Collections.sort(unfinishedMethodsSorted);

			for (String unfinishedMethod : unfinishedMethodsSorted) {
				stringBuilder
					.append("<tr><td>" + "<a href=\"#" + getId("meth", unfinishedMethod) + "\">" + unfinishedMethod + "</a>" + "</td></tr>\n");
			}
		}

		stringBuilder.append("</table>\n\n");

		return stringBuilder.toString();
	}

	public static String getId(String prefix, String name) {
		return prefix + "_" + name.replaceAll("[^a-zA-Z]", "_");
	}

	private class MethodStats implements Comparable<MethodStats> {

		public final String method;

		int calls = 0;
		long totalTime = 0;
		long maxTime = 0;
		long minTime = Long.MAX_VALUE;

		Map<String, MethodStats> subcalls = new HashMap<>();

		public MethodStats(String method) {
			this.method = method;
		}

		public void call(long callTime) {
			calls++;
			totalTime += callTime;
			maxTime = Math.max(maxTime, callTime);
			minTime = Math.min(minTime, callTime);
		}

		public MethodStats getSub(String method) {
			MethodStats subMethodStats = subcalls.get(method);
			if (subMethodStats == null) {
				subMethodStats = new MethodStats(method);
				subcalls.put(method, subMethodStats);
			}
			return subMethodStats;
		}

		public void callSub(String method, long callTime) {
			MethodStats subMethodStats = subcalls.get(method);
			if (subMethodStats == null) {
				subMethodStats = new MethodStats(method);
				subcalls.put(method, subMethodStats);
			}
			subMethodStats.call(callTime);
		}

		public long meanTime() {
			return totalTime / calls;
		}

		public String toString() {
			String out = method + " - calls: " + calls + ", subcalls: " + subcalls.size() + ", total: " + totalTime + ", max: " + maxTime + ", min: "
				+ minTime + ", mean: " + meanTime();
			List<MethodStats> subMethods = new ArrayList<>(subcalls.values());
			Collections.sort(subMethods);
			for (MethodStats subMethod : subMethods) {
				out += "\n  " + (subMethod.meanTime() > 300 ? "* " : "  ") + subMethod;
			}

			return out;
		}

		public String getSubCallsCsv() {
			if (subcalls.isEmpty()) {
				return "";
			}
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("method;calls;subcalls;total;max;min;mean\n");
			List<MethodStats> subcallsList = new ArrayList<>(subcalls.values());
			Collections.sort(subcallsList);
			for (MethodStats subMethod : subcallsList) {
				stringBuilder.append(subMethod.getCsvRow()).append("\n");
			}
			return stringBuilder.toString();
		}

		public String getCsvRow() {
			return method + ";" + calls + ";" + subcalls.size() + ";" + totalTime + ";" + maxTime + ";" + minTime + ";" + meanTime();
		}

		public String getSubCallsTable(boolean withSubcalls) {
			if (subcalls.isEmpty()) {
				return "--";
			}

			long overallTotalTime = totalTime;
			if (overallTotalTime == 0L) {
				for (MethodStats subMethod : subcalls.values()) {
					overallTotalTime += subMethod.totalTime;
				}
			}

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<table>");
			stringBuilder.append(
				"<tr>" + header("method") + header("calls") + header("total") + header("max") + header("min") + header("mean")
					+ (withSubcalls ? header("subcalls") : "") + "</tr>");
			List<MethodStats> subcallsList = new ArrayList<>(subcalls.values());
			Collections.sort(subcallsList);
			for (MethodStats subMethod : subcallsList) {
				stringBuilder.append(subMethod.getTableRow(withSubcalls, overallTotalTime)).append("\n");
			}
			stringBuilder.append("</table>\n");
			return stringBuilder.toString();
		}

		public String getTableRow(boolean withSubcalls, long overallTotalTime) {

			return "<tr>" + methodCell(method, withSubcalls) + cell(calls) + totalTimeCell(overallTotalTime) + timeCell(maxTime) + timeCell(minTime)
				+ timeCell(meanTime())
				// + (withSubcalls ? cell(subcalls.size()) : "") + "</tr>";
				+ (withSubcalls ? subcallsCell() : "") + "</tr>";
		}

		@NotNull
		private String subcallsCell() {
			if (subcalls.isEmpty()) {
				return cell("--");
			}
			String minimizeId = getId("minimize", method);
			String expandId = getId("expand", method);
			String minimizeDiv = "<div id=\"" + minimizeId + "\"><a onclick=\"document.getElementById('" + minimizeId
				+ "').style.display='none';document.getElementById('" + expandId + "').style.display='block';\">[+]</a> " + subcalls.size()
				+ "</div>";
			String expandDiv = "<div id=\"" + expandId + "\" style=\"display:none\"><a onclick=\"document.getElementById('" + minimizeId
				+ "').style.display='block';document.getElementById('" + expandId + "').style.display='none';\">[-]</a>" + getSubCallsTable(false)
				+ "</div>";
			return cell(minimizeDiv + expandDiv);
		}

		private String cell(Object content) {
			return "<td>" + content + "</td>";
		}

		private String methodCell(String method, boolean withSubcalls) {
			String id = getId("meth", method);
			return cell(withSubcalls ? "<span id=\"" + id + "\">" + method + "</span>" : "<a href=\"#" + id + "\">" + method + "</a>");
		}

		private String totalTimeCell(long overallTotalTime) {
			String timeColor = String.format("%02X", overallTotalTime > 0L ? 256L - (200L * totalTime) / overallTotalTime : 256L);
			String style = " style=\"background: #ff" + timeColor + timeColor + "\"";
			return "<td" + style + ">" + totalTime + "</td>";
		}

		private String timeCell(long time) {
			String style = "";
			if (time >= TIME_TRESHOLD_YELLOW) {
				style = " style=\"background: #ffee00\"";
			}
			if (time >= TIME_TRESHOLD_ORANGE) {
				style = " style=\"background: #ff9900\"";
			}
			if (time >= TIME_TRESHOLD_RED) {
				style = " style=\"background: #ff0000\"";
			}
			return "<td" + style + ">" + time + "</td>";
		}

		private String header(String caption) {
			return "<th>" + caption + "</th>";
		}

		@Override
		public int compareTo(@NotNull MethodStats o) {
			return Long.compare(o.meanTime(), meanTime());
		}
	}
}
