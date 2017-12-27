package ono.kamiya.sy.jcrawler.fetcher;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class WebpageContentFetcher extends AbstractContentFetcher {
    private final String defaultUserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";


    @Override
    public Document raw2data(String content) {
        return Jsoup.parse(content);
    }

    public String sendGet(String scheme,
                          String root,
                          String path)
            throws IOException, URISyntaxException {
        List<NameValuePair> headerOptions = new ArrayList<NameValuePair>();
        headerOptions.add(new BasicNameValuePair("User-Agent", defaultUserAgent));
        return this.get(scheme, root, path, headerOptions, new ArrayList<NameValuePair>());
    }

    public String sendGet(String scheme,
                          String root,
                          String path,
                          List<NameValuePair> headerOptions,
                          List<NameValuePair> params)
            throws IOException, URISyntaxException {
        return this.get(scheme, root, path, headerOptions, params);
    }

    public String sentPost() {
        // TODO: Implement
        return null;
    }
}
