package ono.kamiya.sy.jcrawler.analyzer;

import com.google.gson.Gson;
import ono.kamiya.sy.jcrawler.fetcher.WebpageContentFetcher;
import ono.kamiya.sy.jcrawler.util.EventType;
import ono.kamiya.sy.jcrawler.util.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

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

    private String traverseSiteMapLinks(Document doc) throws IOException, URISyntaxException, InterruptedException {
        Element content = doc.getElementById("content");
        Element links = content.getElementsByTag("ul").first();

        List<GalEntry> entries = new ArrayList<>();
        for (Element link : links.children()) {
            entries.add(parseOneEntry(link.child(0)));
        }
        return new Gson().toJson(entries);
    }

    private GalEntry parseOneEntry(Element entryElement) throws IOException, URISyntaxException, InterruptedException {
        GalEntry entry = new GalEntry();
        entry.title = entryElement.attr("title");
        entry.originalURL = java.net.URLDecoder.decode(entryElement.attr("href"), "UTF-8");
        String galPath = new URL(entry.originalURL).getPath();

        Document galEntryPageContent = httpGetOrGetFromFile(galPath, "gals", new WebpageContentFetcher());
        Element article = galEntryPageContent.getElementsByTag("article").first();

        // Baidu Pan link
        String resourceURL = article.select("button").attr("onclick");
        entry.resourceURL = extractResourceURL(resourceURL);

        // Baidu Pan password
        String pwd = article.select("span").text();
        return entry;
    }

    public Object start() {
        WebpageContentFetcher fetcher = new WebpageContentFetcher();

        try {
            Document doc = httpGetOrGetFromFile("sitemap.html", ".", fetcher);
            return traverseSiteMapLinks(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document httpGetOrGetFromFile(String file, String subDirectory, WebpageContentFetcher fetcher)
            throws IOException, URISyntaxException, InterruptedException {
        if (subDirectory == null || subDirectory.equals(""))
            subDirectory = ".";
        Path filePath = Paths.get(String.format("%s/%s/%s/%s", this.localResourceRoot, this.resourceDirectory, subDirectory, file));

        if (Files.exists(filePath)) {
            Logger.logInfo(String.format("Cached: %s", file));
            return fetcher.raw2data(new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8));
        } else {
            String content = fetcher.sendGet(scheme, root, "/" + file);
            Document doc = fetcher.raw2data(content);

            if (content.equals("")) {
                Logger.log(EventType.ERROR, String.format("%s: Blank content", file));
                throw new IOException("Blank content!");
            }

            PrintWriter out = new PrintWriter(filePath.toFile());
            out.println(doc);
            out.close();

            // wait random seconds between 10~30
            int randomNum = ThreadLocalRandom.current().nextInt(10, 31);
            TimeUnit.SECONDS.sleep(randomNum);

            return doc;
        }
    }

    private String extractResourceURL(String url) throws IOException {
        String rscPrefix = "http://pan.baidu.com/s/";
        if (!url.contains(rscPrefix))
            throw new IOException("INVALID RESOURCE URL!");

        String stripped = url.substring(url.indexOf(rscPrefix), url.length() - 1);
        if (!stripped.endsWith("\'"))
            throw new IOException("INVALID RESOURCE URL!");

        return stripped.substring(0, stripped.length() - 1);
    }
}
