package uk.co.tfd.kindle.signalk;

import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;

/**
 * Created by ieb on 20/06/2020.
 */
public class MainScreen {

    private final Calcs calcs;
    private final SignalkTcpClient sd;
    private final Data.Store store;

    public interface MainScreenExit {

        void exit();
    }

    public MainScreen(Container root, String configFile,  MainScreenExit exitHook) throws IOException, NoSuchMethodException, ParseException {

        ControlPage controlPage = new ControlPage(exitHook);
        store = new Data.Store();
        store.addStatusUpdateListener(controlPage);
        PageLayout l = new PageLayout(configFile, store);
        l.addControl(controlPage);
        String configSource = l.loadConfig();
        controlPage.onStatusChange("Config from "+configSource);
        root.add(l);
        root.doLayout();
        root.setVisible(true);
        calcs = new Calcs(store);
        calcs.addStatusUpdateListener(controlPage);

        sd = new SignalkTcpClient(store);
        sd.addStatusUpdateListener(controlPage);
    }

    public void start() throws IOException {
        calcs.start();
        store.start();
        sd.startDiscovery();
    }
}
