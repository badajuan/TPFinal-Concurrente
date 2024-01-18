package com.soporte_tecnico;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world from Main!");
        Log log = Log.getInstance();
        Random random = new Random();
        int randomTransition;
        for (int i = 0; i < 200; i++) {
            randomTransition = random.nextInt(10) + 1;
            log.logTransition(randomTransition);
        }

        log.closeLog();
        System.out.println("Bye world from Main!");
    }
}
