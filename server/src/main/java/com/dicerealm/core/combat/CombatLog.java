package com.dicerealm.core.combat;

import java.util.ArrayList;
import java.util.List;

public class CombatLog {
    private static List<String> logEntries = new ArrayList<>();

    // Logs a message and prints it immediately
    public static void log(String message) {
        logEntries.add(message);
    }

    // Retrieve all combat log entries
    public static List<String> getAllLogs() {
        return new ArrayList<>(logEntries);
    }

    // Print all combat logs
    public static void printAllLogs() {
        if (logEntries.isEmpty()) {
            System.out.println("No combat logs available.");
        } else {
            logEntries.forEach(System.out::println);
        }
    }

    public static String printLatestReadout() {
        if (!logEntries.isEmpty()) {
            return logEntries.get(logEntries.size() - 1);  // Print the latest log entry
        } else {
            return "No actions have been logged yet.";
        }
    }

    // Retrieve a specific combat log entry by index
    public static String getLogByIndex(int index) {
        if (index < 0 || index >= logEntries.size()) {
            return "Invalid log index.";
        }
        return logEntries.get(index);
    }

    // Clear Combat log
    public static void clearLog() {
        logEntries.clear();
    }
}

