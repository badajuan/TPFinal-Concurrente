package com.soporte_tecnico;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log {

    private static Log instance;
    private PrintWriter fileWriter;
    private String fileName;

    private Log() {
        try {
            fileName= "log.txt";
            fileWriter = new PrintWriter(new FileWriter(fileName, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Log creado bajo el nombre '%s'\n",fileName);
    }

    public static Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    public void logMessage(String message) {
        System.out.println(message);

        if (fileWriter != null) {
            fileWriter.println(message);
            fileWriter.flush();
        }
    }

    public void closeLog() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
