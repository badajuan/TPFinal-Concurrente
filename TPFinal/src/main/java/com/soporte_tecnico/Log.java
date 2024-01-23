package com.soporte_tecnico;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private static Log instance;          // Puntero a la instancia Log.
    private PrintWriter fileWriter;       // Writer del log.


    /**
     * Constructor. Privado para garantizar singleton.
     */
    private Log() {
        String fileName;
        String timeStamp;
        try {
            Paths.get("logs").toFile().mkdirs();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            timeStamp = dateFormat.format(new Date());
            fileName = String.format("logs/log_%s.txt", timeStamp);
            fileWriter = new PrintWriter(new FileWriter(fileName, false));
            System.out.printf("Log creado bajo el nombre '%s'\n",fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }


    /**
     * Devuelve una unica instancia de clase Log. Si no existe instancia, crea una.
     * @return puntero a la instancia de Log.
     */
    public static Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }


    /**
     * Registra un mensaje en el log.
     * @param message string a registrar en el log.
     */
    public void logMessage(String message) {
        if (fileWriter != null) {
            fileWriter.printf(message);
            fileWriter.flush();
        }
    }


    /**
     * Registra el disparo de una transicion en el log.
     * @param transition disparo a registrar.
     */
    public void logTransition(int transition){
        //System.out.printf("La transicion 'T%d' ha sido disparada exitosamente\n",transition);
        this.logMessage("T"+String.valueOf(transition));
    }


    /**
     * Cierra el archivo de log.
     */
    public void closeLog() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
