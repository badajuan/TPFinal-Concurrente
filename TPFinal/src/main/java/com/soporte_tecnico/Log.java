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
        if (fileWriter != null) {
            fileWriter.printf(message);
            fileWriter.flush();
        }
    }

    public void logTransition(int transition){
        //System.out.printf("La transicion 'T%d' ha sido disparada exitosamente\n",transition);
        this.logMessage("T"+String.valueOf(transition));
    }

    public void closeLog() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
