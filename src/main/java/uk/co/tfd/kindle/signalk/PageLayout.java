package uk.co.tfd.kindle.signalk;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import uk.co.tfd.kindle.signalk.widgets.Instruments;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ieb on 20/06/2020.
 */
public  class PageLayout extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(PageLayout.class);
    private final Data.DisplayUnits displayUnits;
    private int pageNo;
    private final Data.Store store;
    private final String configFile;
    private final CardLayout layout;
    private final Instruments instruments;
    private boolean rotation;
    private int pressedAt;
    private int dragStartX;
    private int pagesCount;
    private int dragStartY;

    private static final String DEFAULT_LAYOUT = "pages:\n" +
            "-   id: page1\n" +
            "    vspace: 5\n" +
            "    hspace: 5\n" +
            "    instruments:\n" +
            "        - [ awa,       twa, stw,      psratio    ]\n" +
            "        - [ aws,       tws, pstw,     pvmg   ]\n" +
            "        - [ cogt,      sog, attitude, lee ]\n" +
            "        - [ position, fix,  log,    dbt  ]\n" +
            "-   id: page1\n" +
            "    vspace: 5\n" +
            "    hspace: 5\n" +
            "    instruments:\n" +
            "        - [ awa,       stw ]\n" +
            "        - [ aws,       pstw ]\n" +
            "-   id: page2\n" +
            "    vspace: 5\n" +
            "    hspace: 5\n" +
            "    instruments:\n" +
            "    # row 1\n" +
            "        - [ awa, twa, blank, blank ]\n" +
            "        - [ blank, blank, blank, blank ]\n" +
            "        - [ blank, blank, blank, blank ]\n" +
            "        - [ blank, blank, blank, blank ]\n";

    public PageLayout(String configFile, Data.Store store) throws NoSuchMethodException {
        this.instruments = new Instruments();
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
                dragStartY = e.getYOnScreen();
                log.debug("Clicked {} ",dragStartX);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                log.debug("Released {} ", dragStartX);
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
                int distanceX = e.getXOnScreen() - dragStartX;
                int distanceY = e.getYOnScreen() - dragStartY;
                if ( distanceY < -100) {
                    dragStartX  = e.getXOnScreen();
                    dragStartY  = e.getYOnScreen();
                    layout.show(PageLayout.this, "control");
                }
                if ( distanceX < -100 ) {
                    dragStartX  = e.getXOnScreen();
                    dragStartY  = e.getYOnScreen();
                    pageNo--;
                    if (pageNo < 0) {
                        pageNo = pagesCount-1;
                    }
                    layout.show(PageLayout.this, "page" + pageNo);

                } else if (distanceX > 100) {
                    dragStartX  = e.getXOnScreen();
                    dragStartY  = e.getYOnScreen();
                    dragStartX  = e.getXOnScreen();
                    pageNo++;
                    if (pageNo == pagesCount) {
                        pageNo = 0;
                    }
                    layout.show(PageLayout.this, "page"+pageNo);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

    }

    public void addControl(Component control) {
        this.add("control", control);
    }

    public  String loadConfig() throws IOException, ParseException {
        File f = new File(configFile);
        Map<String, Object> configuration;
        Yaml yaml = new Yaml();
        String loaded;
        if ( f.exists() )  {
            loaded = f.getAbsolutePath();
            log.info("Loading config file {} ", f.getAbsolutePath());
            FileReader config = new FileReader(f);
            configuration = yaml.load(config);
            config.close();
        } else {
            loaded = "defaults";
            log.info("Loading defaults ");
            configuration = yaml.load(DEFAULT_LAYOUT);
        }

        java.util.List<Map<String, Object>> pages = (java.util.List<Map<String, Object>>) configuration.get("pages");
        pagesCount = 0;
        for (Map<String, Object> page : pages) {
            JPanel card = new JPanel();
            this.add("page" + pagesCount, card);
            pagesCount++;
            int i = 0;
            java.util.List<java.util.List<String>> grid = (java.util.List<java.util.List<String>>) page.get("instruments");
            int rows = grid.size();
            int columns = grid.get(0).size();
            int hgap = Util.option(page, "hspace", 10);
            int vgap = Util.option(page, "vspace", 10);
            card.setLayout(new GridLayout((int)rows, (int)columns, (int)hgap, (int)vgap));

            for(java.util.List<String> row : grid) {
                for (String col : row) {
                    log.debug("Adding {}  ", col);
                    card.add(instruments.create(col, rotation, displayUnits, store), i++);
                }
            }
        }
        return loaded;

    }
}