package com.soporte_tecnico;

public class Main {
    public static void main(String[] args) {
        
        System.out.println("Hello world from Main!");
        Log log = Log.getInstance();
        for (int i = 0; i < 500; i++) {
            log.logTransition(i+1);
        }
        log.closeLog();
    }
}
