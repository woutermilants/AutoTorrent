package parser.data;

import parser.torrentsiteinfo.TorrentSite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wouter
 * Date: 4/03/15
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
public class ServerSettingsData {
    private List<TorrentSite> torrentSiteList;
    private String serverUrl;

    public List<TorrentSite> getTorrentSiteList() {
        if (torrentSiteList == null) {
            torrentSiteList = new ArrayList<TorrentSite>();
        }
        return torrentSiteList;
    }

    public void setTorrentSiteList(List<TorrentSite> torrentSiteList) {
        this.torrentSiteList = torrentSiteList;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }
}
