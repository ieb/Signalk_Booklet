package uk.co.tfd.kindle.signalk;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import uk.co.tfd.kindle.signalk.widgets.EInkTextBox;

import javax.jmdns.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.net.*;
import java.util.*;
import java.util.List;


/**
 * Created by ieb on 06/06/2020.
 */
public class Main {


    public static class Layout extends JPanel {
        private final Data.DisplayUnits displayUnits;
        private final int pageNo;
        private final Data.Store store;
        private final String configFile;
        private final CardLayout layout;
        private boolean rotation;
        private int pressedAt;
        private int dragStartX;

        public Layout(String configFile, Data.Store store) {
            displayUnits = Data.createSIDisplayUnits();
            layout = new CardLayout();
            this.setLayout(layout);
            this.pageNo = 0;
            this.rotation = false;
            this.store = store;
            this.configFile = configFile;
            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    dragStartX = e.getXOnScreen();
                    System.err.println("Clicked " + dragStartX);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    System.err.println("Released " + dragStartX);
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            this.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int distance = e.getXOnScreen() - dragStartX;
                    if ( distance < -100 ) {
                        System.err.println("Drag Left "+distance);
                        dragStartX  = e.getXOnScreen();
                        layout.previous(Layout.this);

                    } else if (distance > 100) {
                        System.err.println("Drag Right "+distance);
                        dragStartX  = e.getXOnScreen();
                        layout.next(Layout.this);
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {

                }
            });
        }

        public  void loadConfig() throws IOException, ParseException {
            File f = new File(configFile);
            System.err.println("Loading config file " + f.getAbsolutePath());
            FileReader config = new FileReader(f);
            Map<String, Object> configuration = null;
            if ( configFile.endsWith(".yaml")) {
                Yaml yaml = new Yaml();
                configuration = yaml.load(config);
            } else {
                JSONParser jsonParser = new JSONParser();
                configuration = (Map<String, Object>) jsonParser.parse(config);
                DumperOptions options = new DumperOptions();
                options.setWidth(80);
                options.setIndent(4);
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(options);
                System.err.println(yaml.dump(configuration));

            }
            config.close();

            List<Map<String, Object>> pages = (List<Map<String, Object>>) configuration.get("pages");
            int pageId = 0;
            for (Map<String, Object> page : pages) {
                JPanel card = new JPanel();
                int rows = Util.option(page, "rows", 4);
                int columns = Util.option(page, "cols", 4);
                int hgap = Util.option(page, "hspace", 10);
                int vgap = Util.option(page, "vspace", 10);
                card.setLayout(new GridLayout((int)rows, (int)columns, (int)hgap, (int)vgap));
                this.add(card, pageId);
                pageId++;
                int i = 0;
                List<Map<String, Object>> instruments = (List<Map<String, Object>>) page.get("instruments");
                for(Map<String, Object> instrument : instruments) {
                    String type = (String) instrument.get("type");
                    Map<String, Object> options = (Map<String, Object>) instrument.get("options");
                    List<String> sources = (List<String>) instrument.get("sources");
                    try {
                        Class clazz = Class.forName("uk.co.tfd.kindle.signalk.widgets." + type);

                        // boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits
                        Constructor c = clazz.getConstructor(boolean.class, Map.class, Data.DisplayUnits.class);
                        EInkTextBox box = (EInkTextBox) c.newInstance(this.rotation, options, this.displayUnits);

                        System.err.println("Adding  "+type+" "+options+" "+ sources);

                        for (String source : sources) {
                            Data.DataKey k = Data.DataKey.get(source);
                            if ( k != null) {
                                Data.DataValue dv = store.get(k);
                                if (dv != null) {
                                    dv.addListener(box);
                                } else {
                                    System.err.println("Unable to find data value for "+k);
                                }
                            } else {
                                System.err.println("Unable to find Key for "+source);
                            }
                        }
                        card.add(box,i++);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }





    public static void main(String[] args) throws ParseException, IOException {

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        Data.Store store = new Data.Store();
        Calcs calcs = new Calcs(store);
        calcs.start();
        store.start();
        SignalkTcpClient sd  = new SignalkTcpClient(store);
        sd.startDiscovery();
        Layout l = new Layout("src/test/resources/config.yaml", store);
        frame.add(l);
        l.loadConfig();
        frame.pack();
        frame.setVisible(true);


        //Simulation simulation = new Simulation("src/test/resources/events.json", 1000, store, calcs);
        //simulation.start();



    }
}
