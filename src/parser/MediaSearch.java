package parser;

import Logging.DefaultTomcatLogger;
import parser.impl.PirateBayParser;
import parser.torrentsiteinfo.SearchType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Wouter
 */
public class MediaSearch extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DefaultTomcatLogger logger = new DefaultTomcatLogger();

        String mediaSearchTerm = req.getParameter("searchterm");
        SearchType searchType = SearchType.valueOf(req.getParameter("searchtype"));

        resp.setContentType("text/xml;charset=utf-8");
        PrintWriter out = resp.getWriter();

        org.w3c.dom.Document searchResult = doSearch(mediaSearchTerm, searchType);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(searchResult);

            transformer.transform(source, new StreamResult(out));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    protected org.w3c.dom.Document doSearch(String mediaSearchTerm, SearchType searchType) {

        DefaultTomcatLogger logger = new DefaultTomcatLogger();
        PirateBayParser parser = new PirateBayParser();

        return new PirateBayParser().parseTorrent(mediaSearchTerm, 50, searchType);
    }


}
