package edu.montana.csci.csci366.archivecat.archiver.jobs;

import edu.montana.csci.csci366.archivecat.Server;
import edu.montana.csci.csci366.archivecat.archiver.Archive;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class ImageDownloadJob extends AbstractDownloadJob {

    private String _saveLocation;
    private String _fullPathToImage;

    public ImageDownloadJob(Element element, Archive archive) {
        super(element, archive);
        _fullPathToImage = element.absUrl("src");
    }

    @Override
    public void downloadResource() throws Exception {
        // DONE implement - hint: use the 'Content-Type' header of the
        //                  response to determine the type of image and
        //                  give it the correct file ending
        URL url = new URL(getURL().replaceAll(" ", "%20"));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url.toURI())
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<byte[]> data = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (data.statusCode() >= 300) {
            // failed
            return;
        }

        Optional<String> contentType = data.headers().firstValue("Content-Type");
        if (contentType.isEmpty()) return;

        String[] types = contentType.get().split("/");
        if (!"image".equals(types[0])) return;
        String fileType = types[types.length - 1];
        _saveLocation = getArchive().saveFile(getArchive().computeSHA1(_fullPathToImage) + "." + fileType, data.body());
    }

    @Override
    public void updateElement() {
        if (_saveLocation == null) return; // unable to download image
        String location = Server.LOCATION + "/" + _saveLocation.replaceAll("archived", "archives");
        getElement().attr("src", location);
    }

    @Override
    protected String getURL() {
        return _fullPathToImage;
    }
}
