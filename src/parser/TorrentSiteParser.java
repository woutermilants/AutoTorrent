package parser;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parser.torrentsiteinfo.SearchType;
import parser.torrentsiteinfo.TorrentSite;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Wouter
 * Date: 9/05/15
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public abstract class TorrentSiteParser {

    private final static Logger LOG = Logger.getLogger(TorrentSiteParser.class);

    protected static String removeSpaces(String searchParam) {
        return searchParam.replace(" ", "%20");
    }

    public void writeToXml(Document doc, String fileName) throws TransformerException {
        // write the content into xml file

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        final File xmlFileDirectories = new File("webapps/results/");
        if (!xmlFileDirectories.mkdirs()) {
            LOG.error("Could not create directories : " + xmlFileDirectories.getPath());
        }
        final File xmlFile = new File(xmlFileDirectories,  fileName + ".xml");
        try {
            xmlFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Result output = new StreamResult(xmlFile);
        Source input = new DOMSource(doc);

        transformer.transform(input, output);
    }

    public Collection<String> loadTorrentSiteLinks(TorrentSite torrentSite) {
        //logger.writeInfoToLog("LOADING PirateBay Links...");
        LOG.info("Loading " + torrentSite + " links ...");
        ArrayList<String> pirateBayLinks = new ArrayList<String>();

        try {
           // File fXmlFile = new File("webapps/config/"+ torrentSite +"links.xml");
            InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("properties/" + torrentSite + "links.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(xmlStream);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("link");

            for (int i = 0; i < nList.getLength(); i++) {

                Node linkNode = nList.item(i);
                pirateBayLinks.add(linkNode.getTextContent());
                //logger.writeInfoToLog(nNode.getTextContent());
                LOG.info(linkNode.getTextContent());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //logger.writeInfoToLog("FINISHED LOADING PirateBay Links \n");
        LOG.info("Finished loading " + torrentSite + " links \n");

        return pirateBayLinks;
    }

    public abstract Document parseTorrent(String searchParam, int minimumSeeders, SearchType searchType, Document xmlSeriesDoc);

    public abstract Document parseTorrent(String searchParam, int minimumSeeders, SearchType searchType);
}
