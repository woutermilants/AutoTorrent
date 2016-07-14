package parser;

import Logging.DefaultTomcatLogger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Wouter
 */
public class AndroidInputHandler extends HttpServlet  {

    DefaultTomcatLogger logger = new DefaultTomcatLogger();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String type = req.getParameter("type");
        String param = req.getParameter("param");

        if ("magnet".equals(type)) {
        logger.writeInfoToLog("Received magnet : " + param);
        executeMagnet(param);
        } else if ("show".equals(type)) {
            logger.writeInfoToLog("Received show : " + param);
            addTvShow(param);
        }
    }

    private void addTvShow(String show) {

        //String commands ="sudo sed -i \"'\\\"'\\\"'\"\\\\/<\\\\/shows>\\\\/i \\\\\\\\<show>" + show + "<\\\\/show>\"'\\\"'\\\"'\" Tv-Shows.xml";
        String command = "/home/wouter/addshow.sh " + show;

        Process p = null;
        Runtime run = Runtime.getRuntime();

        try {
            logger.writeInfoToLog("command to be executed : " + command);
            p = run.exec(command);

            p.waitFor();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeMagnet(String magnet) {

        String commands = "/home/wouter/magnet.sh " + magnet;
        Process p = null;
        Runtime run = Runtime.getRuntime();

        try {
            p = run.exec(commands);

            p.waitFor();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
