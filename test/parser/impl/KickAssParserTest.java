package parser.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import parser.torrentsiteinfo.SearchType;

import javax.xml.transform.TransformerException;

/**
 * Created with IntelliJ IDEA.
 * User: Wouter
 * Date: 9/05/15
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class KickAssParserTest {

    @Test
    public void testTopMoviesParseTorrent() {
        KickAssParser kickAssParser = new KickAssParser();
        String searchParam = "movies";
        int minimumSeeders = 50;
        SearchType searchType = SearchType.TOP_MOVIES;
        Document doc = kickAssParser.parseTorrent(searchParam, minimumSeeders, searchType);

        try {
            kickAssParser.writeToXml(doc, "kickassTest");
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(null, doc);
    }

    @Test
    public void testTvShowParseTorrent() {
        KickAssParser kickAssParser = new KickAssParser();
        String searchParam = "sherlock";
        int minimumSeeders = 50;
        SearchType searchType = SearchType.TVSHOW;
        Document doc = kickAssParser.parseTorrent(searchParam, minimumSeeders, searchType);

        try {
            kickAssParser.writeToXml(doc, "kickassTestTvShow");
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(null, doc);
    }

}
