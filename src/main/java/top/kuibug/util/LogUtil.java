package top.kuibug.util;

import java.util.logging.*;

public class LogUtil {
    public static Logger log = Logger.getLogger(LogUtil.class.toString());

    static {
        Handler console = new ConsoleHandler();
        console.setLevel(Level.SEVERE);
        log.addHandler(console);
        log.setLevel(Level.INFO);
    }

    public static void fine(String msg) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        int lineNumber = stackTraceElement.getLineNumber();
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        log.logp(Level.FINE, className + ":" + lineNumber, methodName, msg);
    }

    public static void error(String msg) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        int lineNumber = stackTraceElement.getLineNumber();
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        log.logp(Level.SEVERE, className + ":" + lineNumber, methodName, msg);
    }

    public static void warn(String msg) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String methodName = stackTraceElement.getMethodName();
        log.logp(Level.WARNING, "", methodName, msg);
    }

    public static void info(String msg) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String methodName = stackTraceElement.getMethodName();
        log.logp(Level.INFO, "", methodName, msg);
    }

}
