package uk.co.tfd.kindle.signalk;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ieb on 19/06/2020.
 */
public class Simulation {

    private final Timer timer;
    private Map<String, Object> simulationDataSet;
    private final List<Map<String, Object>> messages;
    private int messageNo;

    public Simulation(String simulationFile, int interval, Data.Store store, Calcs calcs) throws IOException, ParseException {
        File f = new File(simulationFile);
        System.err.println("Loading config file " + f.getAbsolutePath());
        FileReader config = new FileReader(f);
        Map<String, Object> simulationDataSet = null;
        JSONParser jsonParser = new JSONParser();
        simulationDataSet = (Map<String, Object>) jsonParser.parse(config);

        config.close();
        messages = (List<Map<String, Object>>) simulationDataSet.get("messages");
        messageNo = -1;
        config.close();
        timer = new Timer(interval, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Map<String, Object> nextUpdate = getNextUpdate();
                store.update("",nextUpdate);
//                calcs.enhance(store);
            }
        });
    }

    private Map<String, Object> getNextUpdate() {
        messageNo = (messageNo+1)%messages.size();
        return messages.get(messageNo);
    }


    public void start() {
        timer.start();
    }
}
