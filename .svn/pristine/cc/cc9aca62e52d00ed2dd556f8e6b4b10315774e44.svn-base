package parser.impl;

import Logging.DefaultTomcatLogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import parser.TorrentSiteParser;
import parser.data.TorrentData;
import parser.torrentsiteinfo.SearchType;
import parser.torrentsiteinfo.TorrentSite;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Wouter
 */
public class KickAssParser extends TorrentSiteParser {

    private static final String XML_DOC_ROOTELEMENT = "torrents";
    private org.w3c.dom.Element rootElement;
    private Collection<String> kickassLinks = new ArrayList<String>();
    private DefaultTomcatLogger logger = new DefaultTomcatLogger();

    /*public KickAssParser() {
        this.xmlDoc = createXmlFile();
    }*/

    @Override
    public org.w3c.dom.Document parseTorrent(String searchParam, int minimumSeeders, SearchType searchType) {
        return parseTorrent(searchParam, minimumSeeders, searchType, createXmlFile());
    }


    @Override
    public org.w3c.dom.Document parseTorrent(String searchParam, int minimumSeeders, SearchType searchType, org.w3c.dom.Document xmlDoc) {

        Document urlDoc;

        int pageNumber = 1;
        do {
            urlDoc = getJsoupDocumentFromUrl(searchParam, searchType, Integer.toString(pageNumber));

            List<TorrentData> torrentDataList = createTorrentDataObjectsFromUrl(urlDoc, searchParam, minimumSeeders);
            // torrentName elements
            for (TorrentData torrentData : torrentDataList) {
                org.w3c.dom.Element mediaTag;
                if (SearchType.MOVIE.equals(searchType) || SearchType.TOP_MOVIES.equals(searchType)) {
                    mediaTag = xmlDoc.createElement("movie");
                } else {
                    mediaTag = xmlDoc.createElement("tv-show");
                }

                xmlDoc.getFirstChild().appendChild(mediaTag);
                addDataToXml(xmlDoc, searchParam, torrentData, mediaTag);
            }
            pageNumber++;
        } while (pageNumber < 6);
        return xmlDoc;
    }

    private Document getJsoupDocumentFromUrl(String searchParam, SearchType searchType, String page) {
        logger.writeInfoToLog(searchParam + " " + searchType);
        Document urlDoc = null;
        String activeUrl = getActiveTorrentSiteAdress();

        if (SearchType.TOP_MOVIES.equals(searchType)) {
            activeUrl += "/movies/" + page;
        } else if (SearchType.TVSHOW.equals(searchType)) {
            activeUrl += "/usearch/" + removeSpaces(searchParam) + addFilterOptions(searchType) + page + sortSeedersDescending();
        } else if (SearchType.MOVIE.equals(searchType)) {
            //TODO
        }

        try {
            urlDoc = Jsoup.connect(activeUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlDoc;
    }

    private String sortSeedersDescending() {
        return "?field=seeders&sorder=desc";
    }

    private String addFilterOptions(SearchType searchType) {
        String filter = "";
        if (SearchType.TVSHOW.equals(searchType)) {
            filter = "%20category%3Atv/";
        } else if (SearchType.MOVIE.equals(searchType)) {
            filter = "%20category:movie/";
        }

        return filter;
    }

    private String getActiveTorrentSiteAdress() {
        String activeUrl = null;
        for (String pburl : loadTorrentSiteLinks(TorrentSite.KICKASS)) {
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

        if (activeUrl == null) {
            throw new IllegalArgumentException("Unable to find correct url");
        }
        return activeUrl;
    }

    private void addDataToXml(org.w3c.dom.Document xmlDoc, String searchParam, TorrentData torrentData, org.w3c.dom.Element mediaTag) {
        //Add name tag
        mediaTag.setAttribute("name", searchParam);

        // torrentname elements
        org.w3c.dom.Element torrentname = xmlDoc.createElement("name");
        torrentname.appendChild(xmlDoc.createTextNode(torrentData.getName() != null ? torrentData.getName() : ""));
        mediaTag.appendChild(torrentname);

        // magnet elements
        org.w3c.dom.Element torrentmagnet = xmlDoc.createElement("magnet");
        torrentmagnet.appendChild(xmlDoc.createTextNode(torrentData.getMagnet() != null ? torrentData.getMagnet() : ""));
        mediaTag.appendChild(torrentmagnet);

        // seeders elements
        org.w3c.dom.Element seeders = xmlDoc.createElement("seeders");
        seeders.appendChild(xmlDoc.createTextNode(torrentData.getSeeders() != null ? torrentData.getSeeders() : ""));
        mediaTag.appendChild(seeders);

        // leechers elements
        org.w3c.dom.Element leechers = xmlDoc.createElement("leechers");
        leechers.appendChild(xmlDoc.createTextNode(torrentData.getLeechers() != null ? torrentData.getLeechers() : ""));
        mediaTag.appendChild(leechers);

        // upload info elemetns
        org.w3c.dom.Element uploadinfo = xmlDoc.createElement("uploadinfo");
        uploadinfo.appendChild(xmlDoc.createTextNode(torrentData.getUploaded() != null ? torrentData.getUploaded() : ""));
        mediaTag.appendChild(uploadinfo);

        // size elements
        org.w3c.dom.Element torrentSize = xmlDoc.createElement("torrentsize");
        torrentSize.appendChild(xmlDoc.createTextNode(torrentData.getSize() != null ? torrentData.getSize() : ""));
        mediaTag.appendChild(torrentSize);
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
        org.w3c.dom.Document xmlDoc = null;
        if (docBuilder != null) {
            xmlDoc = docBuilder.newDocument();
            rootEl = xmlDoc.createElement(XML_DOC_ROOTELEMENT);
            xmlDoc.appendChild(rootEl);
        }

        return xmlDoc;
    }

    public List<TorrentData> createTorrentDataObjectsFromUrl(Document urlDoc, String searchParam, int minimumSeeders) {

        List<TorrentData> torrentDataList = new ArrayList<TorrentData>();

        for (Element element : urlDoc.getElementsByAttributeValueStarting("id", "torrent_" + searchParam)) {
            TorrentData torrentData = new TorrentData();

            String seeders = element.getElementsByClass("green").get(0).text();
            if (Integer.parseInt(seeders) < minimumSeeders) {
                break;
            }
            torrentData.setName(element.getElementsByClass("cellMainLink").text());
            torrentData.setMagnet(element.getElementsByClass("imagnet").get(0).attr("href"));
            torrentData.setSize(element.getElementsByClass("nobr").get(0).text());
            torrentData.setLeechers(element.getElementsByClass("lasttd").get(0).text());
            torrentData.setSeeders(seeders);

            torrentDataList.add(torrentData);

        }

        return torrentDataList;
    }
}
