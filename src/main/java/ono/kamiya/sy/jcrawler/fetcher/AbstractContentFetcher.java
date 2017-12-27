package ono.kamiya.sy.jcrawler.fetcher;

import ono.kamiya.sy.jcrawler.util.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

abstract class AbstractContentFetcher {
    protected String get(String scheme,
                         String root,
                         String path,
                         List<NameValuePair> headerOptions,
                         List<NameValuePair> parameters)
            throws URISyntaxException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(buildURI(scheme, root, path, parameters));
        for (NameValuePair option : headerOptions) {
            httpGet.addHeader(option.getName(), option.getValue());
        }
        CloseableHttpResponse response = httpClient.execute(httpGet);

        try {
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            EntityUtils.consume(entity);

            int statusCode = response.getStatusLine().getStatusCode();
            Logger.logInfo(String.format("[%d] %s", statusCode, httpGet.toString()));

            if (statusCode != 200) {
                throw new IOException("GET failed!");
            }
            return result.toString();
        } finally {
            response.close();
        }
    }

    protected String post(String scheme,
                          String root,
                          String path,
                          List<NameValuePair> headerOptions,
                          List<NameValuePair> parameters,
                          List<NameValuePair> formData)
            throws URISyntaxException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(buildURI(scheme, root, path, parameters));
        for (NameValuePair option : headerOptions) {
            httpPost.addHeader(option.getName(), option.getValue());
        }
        httpPost.setEntity(new UrlEncodedFormEntity(formData));
        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            // TODO: Log this httpPost attempt

            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            EntityUtils.consume(entity);
            return result.toString();
        } finally {
            response.close();
        }
    }

    private URI buildURI(String scheme, String root, String path, List<NameValuePair> parameters)
            throws URISyntaxException {
        return new URIBuilder()
                .setScheme(scheme)
                .setHost(root)
                .setPath(path)
                .addParameters(parameters)
                .build();
    }

    public abstract Object raw2data(String content);
}
