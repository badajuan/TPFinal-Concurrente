package com.soporte_tecnico;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private static Log instance;
    private PrintWriter fileWriter;
    private String fileName;
    private String timeStamp;
    private int counter = 1;

    private Log() {
        try {
            Paths.get("logs").toFile().mkdirs();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            timeStamp = dateFormat.format(new Date());
            fileName = String.format("logs/log_%s.txt", timeStamp);

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
        String line = String.format(" %d - %s\n",counter,message);
        //System.out.println(line);
        if (fileWriter != null) {
            fileWriter.println(line);
            fileWriter.flush();
        }
        counter++;
    }

    public void logTransition(int transition){
        String message = String.format("La transicion 'T%d' ha sido disparada exitosamente",transition);
        this.logMessage(message);
    }

    public void closeLog() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
