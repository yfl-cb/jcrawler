package ono.kamiya.sy.jcrawler.analyzer;

import com.google.gson.Gson;
import ono.kamiya.sy.jcrawler.fetcher.WebpageContentFetcher;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MyGalgameAnalyzer extends AbstractAnalyzer {
    private final String scheme = "https";
    private final String root = "www.mygalgame.com";
    private final String startPath = "/sitemap.html";
    private final String resourceDirectory = "mygalgame.com";

    private class GalEntry {
        public String originalURL = "";
        public String resourceURL = "";
        public String resourcePwd = "";
        public String title = "";
        public List<String> imageURLs = new ArrayList<>();
        public String introduction = "";
        public List<String> fileMD5s = new ArrayList<>();
        public String remark = "";
        public String totalSize = "";
    }

    private String traverseSiteMapLinks(Document doc) {
        Element content = doc.getElementById("content");
        Elements links = content.getElementsByTag("ul");

        List<GalEntry> entries = new ArrayList<>();
        for (Element link : links) {
            entries.add(parseOneEntry(link.child(0)));
        }
        return new Gson().toJson(entries);
    }

    private GalEntry parseOneEntry(Element entryElement) {
        GalEntry entry = new GalEntry();
        entry.title = entryElement.attr("title");
        entry.originalURL = entryElement.attr("href");

        return entry;
    }

    public Object start() {
        WebpageContentFetcher fetcher = new WebpageContentFetcher();

        try {
            String rawData = httpGetOrGetFromFile("sitemap.html", fetcher);
            Document doc = fetcher.raw2data(rawData);
            return traverseSiteMapLinks(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String httpGetOrGetFromFile(String file, WebpageContentFetcher fetcher)
            throws IOException, URISyntaxException {
        String filePath = String.format("%s/%s/%s", this.localResourceRoot, this.resourceDirectory, file);
        if (Files.exists(Paths.get(filePath))) {
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } else {
            return fetcher.sendGet(scheme, root, startPath);
        }
    }
}
