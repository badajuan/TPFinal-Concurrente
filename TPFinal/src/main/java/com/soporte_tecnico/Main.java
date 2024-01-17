package com.soporte_tecnico;

public class Main {
    public static void main(String[] args) {

        Log log = Log.getInstance();
        log.logMessage("Hello World from Log!");
        log.closeLog();

        System.out.println("Hello world from Main!");
    }
}
