package me.acablade.skcompile.utils;

import java.util.logging.Logger;

public class LogManager {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RED_UNDERLINE = "\u001B[31;4m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";




    public static boolean debugMode = false;

    public static void send(String color, String msg){
        System.out.println(ANSI_BLUE + "LOGGER " + " | " + color + msg + ANSI_RESET);
//        Logger.getLogger("[SKCOMPILE]").info(ANSI_BLUE + "LOGGER " + " | " + color + msg + ANSI_RESET);
    }

    public static void info(String msg, Object... format){
        send(ANSI_RESET, "[INFO] " + String.format(msg, format));
    }

    public static void debug(String msg, Object... format){
        if(!debugMode) return;
        send(ANSI_BLUE, "[DEBUG] " + String.format(msg, format));
    }

    public static void warn(String msg, Object... format){
        send(ANSI_YELLOW, "[WARN] " + String.format(msg, format));
    }

    public static void err(String msg, Object... format){
        send(ANSI_RED, "[ERR] " + String.format(msg, format));
    }

}
