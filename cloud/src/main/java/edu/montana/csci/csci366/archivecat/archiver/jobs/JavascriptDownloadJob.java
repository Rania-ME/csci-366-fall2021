package edu.montana.csci.csci366.archivecat.archiver.jobs;

import edu.montana.csci.csci366.archivecat.Server;
import edu.montana.csci.csci366.archivecat.archiver.Archive;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JavascriptDownloadJob extends AbstractDownloadJob {
    private final String _fullPathToJS;
    private String _archiveFileName;
    private String _saveLocation;

    public JavascriptDownloadJob(Element element, Archive archive) {
        super(element, archive);
        _fullPathToJS = element.absUrl("src");
        _archiveFileName = archive.computeSHA1(_fullPathToJS) + ".js";
    }

    @Override
    public void downloadResource() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .uri(new URL(_fullPathToJS).toURI())
                .build();

        HttpResponse<byte[]> res = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        // failure
        if (res.statusCode() >= 300) return;

        _saveLocation = getArchive().saveFile(_archiveFileName, res.body());
    }

    @Override
    public void updateElement() {
        if (_saveLocation == null) return; // unable to download js file
        String fullPath = Server.LOCATION + "/" + _saveLocation.replaceAll("archived", "archives");
        getElement().attr("src", fullPath);
    }

    @Override
    protected String getURL() {
        return _fullPathToJS;
    }
}
