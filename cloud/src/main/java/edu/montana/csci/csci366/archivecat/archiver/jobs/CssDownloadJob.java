package edu.montana.csci.csci366.archivecat.archiver.jobs;

import edu.montana.csci.csci366.archivecat.Server;
import edu.montana.csci.csci366.archivecat.archiver.Archive;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CssDownloadJob extends AbstractDownloadJob {
    private String _fullPathToCss;
    private String _saveLocation;

    public CssDownloadJob(Element element, Archive archive) {
        super(element, archive);
        System.out.println("Full path is: " + element.absUrl("href"));
        _fullPathToCss = element.absUrl("href");
    }

    @Override
    public void downloadResource() throws Exception {
        URL url;
        url = new URL(_fullPathToCss);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url.toURI())
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<byte[]> res = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (res.statusCode() >= 300) return; // Unable to download css file

        byte[] body = res.body();
        _saveLocation = getArchive().saveFile(getArchive().computeSHA1(_fullPathToCss) + ".css", body);
    }

    @Override
    public void updateElement() {
        if (_saveLocation == null) return; // Unable to download css file
        final String path = Server.LOCATION + "/" + _saveLocation.replace("archived", "archives");
        System.out.println("CSS Save location is " + path);
        getElement().attr("href", path);
    }

    @Override
    protected String getURL() {
        return _fullPathToCss;
    }
}
