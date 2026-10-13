package de.symeda.sormas.ui.utils;

import com.vaadin.ui.JavaScript;

/**
 * Allows pushing log messages from server-side Vaadin code to the browser's JavaScript console.
 * Intended for local debugging only: remove calls before committing production code.
 * <p>
 * Example usage:
 * ConsoleLogUtils.info(String.format("calledMethod: param1 [%s], param2 [%s]", param1, param2));
 */
public final class ConsoleLogUtils {

    private ConsoleLogUtils() {
        // Hide Utility Class Constructor
    }

    public static void log(Object message) {
        execute("log", message);
    }

    public static void info(Object message) {
        execute("info", message);
    }

    public static void warn(Object message) {
        execute("warn", message);
    }

    public static void error(Object message) {
        execute("error", message);
    }

    private static void execute(String level, Object message) {
        JavaScript.getCurrent().execute(String.format("console.%s(%s);", level, toJsStringLiteral(String.valueOf(message))));
    }

    private static String toJsStringLiteral(String value) {
        String escaped = value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("</", "<\\/");
        return "\"" + escaped + "\"";
    }
}
