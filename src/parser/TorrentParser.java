package parser;

/**
 * Created By Wouter
 */

import Logging.DefaultTomcatLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import parser.data.ServerSettingsData;
import parser.impl.KickAssParser;
import parser.impl.PirateBayParser;
import parser.torrentsiteinfo.SearchType;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class TorrentParser extends HttpServlet implements ServletContextListener {

    static org.w3c.dom.Element rootElement;
    Collection<String> pbLinks = new ArrayList<String>();
    DefaultTomcatLogger logger = new DefaultTomcatLogger();

    //Executed on server startup
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        Timer timer = new Timer();

        TimerTask timedTask = new TimerTask() {

            @Override
            public void run() {

                ServerSettingsData setting = loadSettings();
                loadTvShowList();
                if (false) {
                    final PirateBayParser pirateBayParser = new PirateBayParser();
                    final KickAssParser kickAssParser = new KickAssParser();
                    Collection<String> tvShows = loadTvShowList();

                    Document xmlDoc = null;
                    for (String tvShow : tvShows) {
                        logger.writeInfoToLog("STARTED loading torrents for: " + tvShow.toUpperCase());
                        //pirateBayParser.parseTorrent return a variable initialized in the constructor of PirateParse()
                        xmlDoc = kickAssParser.parseTorrent(tvShow, 100, SearchType.TVSHOW);
                       // xmlDoc = pirateBayParser.parseTorrent(tvShow, 100, SearchType.TVSHOW);
                        logger.writeInfoToLog("FINISHED loading torrents for: " + tvShow.toUpperCase() + "\n");
                    }

                    try {
                         kickAssParser.writeToXml(xmlDoc);
                        logger.writeInfoToLog("--- FINISHED writing results to XML file --- \n");
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        // schedule the task to run starting now and then every x seconds
        timer.schedule(timedTask, 0l, 1000 * 60 * 60 * 1);     //1 hour

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.writeInfoToLog("TOMCAT SERVER IS SHUTTING DOWN");
    }

    private String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return sdf.format(date);
    }

    private Collection<String> loadTvShowList() {

        logger.writeInfoToLog("LOADING TV-SHOWS...");
        ArrayList<String> tvShows = new ArrayList<String>();
        try {
            InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("properties/tv-shows.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlStream);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("shows");

            for (int i = 0; i < nList.getLength(); i++) {

                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
                    NodeList showList = eElement.getElementsByTagName("show");
                    for (int j = 0; j < showList.getLength(); j++) {
                        String currentShow = eElement.getElementsByTagName("show").item(j).getTextContent();
                        logger.writeInfoToLog("Tv-Show : " + currentShow.toUpperCase());
                        tvShows.add(currentShow);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.writeInfoToLog("FINISHED LOADING TV-SHOWS \n");

        return tvShows;
    }

    private ServerSettingsData loadSettings() {
        logger.writeInfoToLog("LOADING SETTINGS...");
        Properties prop = new Properties();

        ServerSettingsData settings = new ServerSettingsData();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("properties/serversettings.properties");

        try {
            prop.load(inputStream);
            prop.getProperty("serverurl");
            prop.getProperty("piratebayenabled");
            prop.getProperty("kickassenabled");
            prop.getProperty("torrentpolltime");
            prop.getProperty("serverurl");
            prop.getProperty("serverurl");
            prop.getProperty("serverurl");
            prop.getProperty("serverurl");

        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("properties/tv-shows.xml");
        try {
            readXml(xmlStream);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return settings;
    }

    public static Document readXml(InputStream is) throws SAXException, IOException,
            ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setNamespaceAware(true);

        DocumentBuilder db = null;
        db = dbf.newDocumentBuilder();

        return db.parse(is);
    }
}
