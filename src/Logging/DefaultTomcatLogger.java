package Logging;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wouter
 */
public class DefaultTomcatLogger implements TomcatLogger {

    @Override
    public void writeInfoToLog(String message) {
        System.out.println("INFO: " + getTimeStamp() + " " + message);
    }

    @Override
    public void writeErrorToLog(String message) {
        System.out.println("ERROR: " + getTimeStamp() + " " + message);
    }

    private String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return sdf.format(date);
    }
}
