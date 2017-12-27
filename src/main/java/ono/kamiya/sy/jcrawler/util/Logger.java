package ono.kamiya.sy.jcrawler.util;

public class Logger {
    public static void log(EventType eventType, String msg) {
        String formattedMsg = String.format("%s: %s", eventType.str(), msg);

        if (eventType == EventType.INFO) {
            System.out.println(formattedMsg);
        } else {
            System.err.println(formattedMsg);
        }
    }

    public static void logInfo(String msg) {
        log(EventType.INFO, msg);
    }

    public static void logHttpGet(String request) {

    }
}
