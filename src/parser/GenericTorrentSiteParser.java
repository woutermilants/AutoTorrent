package parser;

import Logging.DefaultTomcatLogger;
import data.TorrentSiteEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parser.data.TorrentData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Wouter
 */
public class GenericTorrentSiteParser {

    private org.w3c.dom.Element rootElement;
    private Collection<String> pbLinks = new ArrayList<String>();
    private DefaultTomcatLogger logger = new DefaultTomcatLogger();
    private org.w3c.dom.Document xmlDoc;
    private TorrentSiteEnum torrentSite;

    public GenericTorrentSiteParser(TorrentSiteEnum torrentSite) {
        this.xmlDoc = createXmlFile();
        this.torrentSite = torrentSite;
    }

    public org.w3c.dom.Document parseTorrent(String searchParam, int minimumSeeders, String searchType) {

        logger.writeInfoToLog(searchParam + " " + searchType);
        Document urlDoc;
        String activeUrl = "";
        //this.xmlDoc = xmlDoc;
        Collection<String> torrentSiteLinks = new ArrayList<String>();

        if (torrentSite == TorrentSiteEnum.PirateBay) {
            torrentSiteLinks = loadPirateBayLinks();
        } else if (torrentSite == TorrentSiteEnum.KickAssTorrent) {
            torrentSiteLinks = loadKickAssTorrentLinks();
        }
        for (String pburl : loadPirateBayLinks()) {
            try {
                if (Jsoup.connect(pburl).get() != null) {
                    activeUrl = pburl;
                    break;
                } else {
                    logger.writeErrorToLog("ERROR : Unable to connect to " + pburl);
                }
            } catch (MalformedURLException e) {
                logger.writeErrorToLog("Error connecting to host (MalformedURLException)");
            } catch (IOException e) {
                logger.writeErrorToLog(e.getMessage());
            }
        }

        if (activeUrl != null) {

            String searchTerm = null;
            if (torrentSite == TorrentSiteEnum.PirateBay) {
                if (searchType.equals("tv-show")) {
                    searchTerm = activeUrl + "/search/" + removeSpaces(searchParam) + "/0/7/208";
                } else if (searchType.equals("movie")) {
                    searchTerm = activeUrl + "/search/" + removeSpaces(searchParam) + "/0/7/207";
                }
            } else if (torrentSite == TorrentSiteEnum.KickAssTorrent) {
                if (searchType.equals("tv-show")) {
                    searchTerm = activeUrl + "/search/" + removeSpaces(searchParam) + "/0/7/208";
                } else if (searchType.equals("movie")) {
                    searchTerm = activeUrl + "/search/" + removeSpaces(searchParam) + "/0/7/207";
                }
            }


            try {
                urlDoc = Jsoup.connect(searchTerm).get();

                // get all links
                Elements torrents = urlDoc.select("tr");
                for (Element torrent : torrents) {
                    Elements torrentdatas = torrent.select("td");

                    if (torrentdatas.size() >= 4) {
                        TorrentData torrentData = new TorrentData();
                        torrentData.setSeeders(torrentdatas.get(2).text());
                        if (Integer.parseInt(torrentData.getSeeders()) < minimumSeeders) {
                            break;
                        }
                        torrentData.setName(torrentdatas.get(1).getElementsByClass("detName").text());
                        torrentData.setUploaded(torrentdatas.get(1).getElementsByClass("detDesc").text());

                        for (Element link : torrentdatas.get(1).getElementsByAttribute("href")) {
                            if (link.attr("href").startsWith("magnet")) {
                                torrentData.setMagnet(link.attr("href"));
                            }
                        }
                        //torrentData.setLeechers(Integer.parseInt(torrentdatas.get(3).text()));
                        torrentData.setLeechers("0");
                        // torrentName elements
                        org.w3c.dom.Element mediaTag;
                        if (searchType.equals("movie")) {
                            mediaTag = xmlDoc.createElement("movie");
                        } else {
                            mediaTag = xmlDoc.createElement("tv-show");
                        }

                        rootElement.appendChild(mediaTag);
                        addDataToXml(xmlDoc, searchParam, torrentData, mediaTag);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.writeErrorToLog("THERE ARE NO AVAILABLE TORRENT URL's");
        }

        return xmlDoc;
    }

    private void addDataToXml(org.w3c.dom.Document xmlDoc, String searchParam, TorrentData torrentData, org.w3c.dom.Element serieTag) {
        //Add name tag
        serieTag.setAttribute("name", searchParam);

        // torrentname elements
        org.w3c.dom.Element torrentname = xmlDoc.createElement("name");
        torrentname.appendChild(xmlDoc.createTextNode(torrentData.getName()));
        serieTag.appendChild(torrentname);

        // magnet elements
        org.w3c.dom.Element torrentmagnet = xmlDoc.createElement("magnet");
        torrentmagnet.appendChild(xmlDoc.createTextNode(torrentData.getMagnet()));
        serieTag.appendChild(torrentmagnet);

        // seeders elements
        org.w3c.dom.Element seeders = xmlDoc.createElement("seeders");
        seeders.appendChild(xmlDoc.createTextNode(torrentData.getSeeders()));
        serieTag.appendChild(seeders);

        // leechers elements
        org.w3c.dom.Element leechers = xmlDoc.createElement("leechers");
        leechers.appendChild(xmlDoc.createTextNode(torrentData.getLeechers()));
        serieTag.appendChild(leechers);

        // upload info elemetns
        org.w3c.dom.Element uploadinfo = xmlDoc.createElement("uploadinfo");
        leechers.appendChild(xmlDoc.createTextNode(torrentData.getUploaded()));
        serieTag.appendChild(uploadinfo);
    }

    private static String removeSpaces(String searchParam) {
        return searchParam.replace(" ", "%20");
    }

    private org.w3c.dom.Document createXmlFile() {
        //org.w3c.dom.Document xmlDoc = null;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        org.w3c.dom.Element rootEl;
        if (docBuilder != null) {
            xmlDoc = docBuilder.newDocument();
            rootEl = xmlDoc.createElement("torrents");
            xmlDoc.appendChild(rootEl);
        } else {
            rootEl = null;
        }
        rootElement = rootEl;

        return xmlDoc;
    }

    private Collection<String> loadPirateBayLinks() {

        logger.writeInfoToLog("LOADING PirateBay Links...");
        ArrayList<String> pirateBayLinks = new ArrayList<String>();

        try {
            File fXmlFile = new File("webapps/config/piratebaylinks.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("link");

            for (int i = 0; i < nList.getLength(); i++) {

                Node nNode = nList.item(i);
                pirateBayLinks.add(nNode.getTextContent());
                logger.writeInfoToLog(nNode.getTextContent());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.writeInfoToLog("FINISHED LOADING PirateBay Links \n");

        return pirateBayLinks;
    }

    private Collection<String> loadKickAssTorrentLinks() {

        logger.writeInfoToLog("LOADING KickAssTorrent Links...");
        ArrayList<String> pirateBayLinks = new ArrayList<String>();

        try {
            InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("properties/kickasstorrentlinks.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(xmlStream);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("link");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                pirateBayLinks.add(nNode.getTextContent());
                logger.writeInfoToLog(nNode.getTextContent());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.writeInfoToLog("FINISHED LOADING KickAssTorrent Links \n");

        return pirateBayLinks;
    }
}
