package com.soporte_tecnico;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Log {

    private static Log instance;          // Puntero a la instancia Log.
    private PrintWriter fileWriter;       // Writer del log.
    List<String> transitionList;          // Lista de transiciones registradas.


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

        transitionList = new ArrayList<>();
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
     * Registra el disparo de una transicion en la lista de transiciones.
     * @param transition disparo a registrar.
     */
    public void addTransition(int transition) {
        transitionList.add("T"+String.valueOf(transition));
    }


    /**
     * Cierra el archivo de log.
     */
    public void closeLog() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }


    /**
     * Escribe la lista de transiciones en el archivo de log.
     * Luego cierra el log.
     */
    public void writeLog() {
        fileWriter.print(String.join("", transitionList));
        closeLog();
    }
}
