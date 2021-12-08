package edu.montana.csci.csci366.archivecat;

import edu.montana.csci.csci366.archivecat.archiver.Archive;
import edu.montana.csci.csci366.archivecat.archiver.Archiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;

import static spark.Spark.*;

public class Server {

    private static Logger LOGGER = LoggerFactory.getLogger(Server.class);
    public static final int PORT = 8001;
    public static final String LOCATION = "http://localhost:" + PORT;

    public static String renderTemplate(String index, Object... args) {
        HashMap<Object, Object> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (++i < args.length) {
                map.put(arg, args[i]);
            }
        }
        return new VelocityTemplateEngine().render(new ModelAndView(map, index));
    }

    public static void main(String[] args) {

        port(PORT);

        staticFiles.location("/public");

        /*
         * The root url that serves the main archiving UI form
         */
        get("/", (req, resp) -> {
            return renderTemplate("templates/index.vm");
        });

        /*
         * The url that displays a given archive with an IFRAME
         */
        get("/archived/:sha", (req, resp) -> {
            return renderTemplate("templates/archived.vm", "sha", req.params("sha"));
        });

        /*
         * The urls from which a given archive is retrieved
         */
        get("/archives/*", (req, resp) -> {
            String archivePath = req.splat()[0];
            if (archivePath.endsWith(".css")) {
                resp.type("text/css");
            } else if (archivePath.endsWith(".js")) {
                resp.type("text/javascript");
            }
            return Archive.getContent(archivePath);
        });

        /*
         * The url that starts an archive
         */
        post("/archive", (req, resp) -> {
            Archiver archiver = new Archiver(req.queryParams("url"));
            archiver.archive();
            resp.header("HX-Redirect", "/archived/" + archiver.getBaseSHA());
            return "";
        });

        delete("/archive", (req, resp) -> {
            Archive.clearAll();
            return "Cleared...";
        });
    }
}
