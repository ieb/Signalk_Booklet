package uk.co.tfd.kindle.signalk;

import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.Timer;

/**
 * Created by ieb on 29/06/2020.
 */
public class SignalkHttpClient  extends StatusUpdates  {

    private static final Logger log = LoggerFactory.getLogger(SignalkHttpClient.class);


    private final Data.Store store;
    private Set<String> rejectedPaths = new HashSet<String>();

    public SignalkHttpClient(Data.Store store) {
        this.store = store;

    }
    public boolean fetch(String url) {
        InputStreamReader in = null;
        try {
            URL u = new URL(url + "/signalk/v1/api/vessels/self");
            in = new InputStreamReader(u.openStream());
            JSONParser parser = new JSONParser();
            //updateStatus("Fetched state from "+url);
            //log.info("Fetched state from {} ", url);
            Map<String, Object> skdata = (Map<String, Object>) parser.parse(in);
            Map<String, Object> rejects = store.update("", skdata);
            for(String e : rejects.keySet()) {
                if ( !rejectedPaths.contains(e)) {
                    log.info("Path ignored {} ", e);
                    rejectedPaths.add(e);
                }
            }
            return true;
        } catch (Exception ex) {
            log.error("Fetch from {} failed with {} ", url, ex.getMessage());
            updateStatus("Fetch from "+url+" failed with "+ex.getMessage());
            return false;
        } finally {
            if ( in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    log.error("Error Closing Stream {} ", e1.getMessage());
                }
            }
        }
    }


}
