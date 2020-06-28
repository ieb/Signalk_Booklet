package uk.co.tfd.kindle.signalk;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.signalk.widgets.Instruments;

import javax.swing.*;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ieb on 20/06/2020.
 */
public  class PageLayout extends JPanel implements  MouseListener, MouseMotionListener {
    private static final Logger log = LoggerFactory.getLogger(PageLayout.class);
    private final Data.DisplayUnits displayUnits;
    private int pageNo;
    private final Data.Store store;
    private final String configFile;
    private final CardLayout layout;
    private final Instruments instruments;
    private int pressedAt;
    private int dragStartX;
    private int pagesCount;
    private int dragStartY;

    private static final String DEFAULT_LAYOUT = "{\n" +
            "   \"pages\" : [\n" +
            "      {\n" +
            "         \"instruments\" : [\n" +
            "            [  \"awa\", \"twa\", \"stw\", \"psratio\" ],\n" +
            "            [  \"aws\", \"tws\", \"pstw\",\"pvmg\"    ],\n" +
            "            [  \"cogt\",\"sog\",\"attitude\",\"lee\"  ],\n" +
            "            [  \"position\",\"fix\",\"log\",\"dbt\"   ]\n" +
            "         ],\n" +
            "         \"vspace\" : 5,\n" +
            "         \"hspace\" : 5,\n" +
            "         \"id\" : \"page1\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"vspace\" : 5,\n" +
            "         \"instruments\" : [\n" +
            "            [\"awa\", \"stw\" ],\n" +
            "            [\"aws\", \"pstw\"]\n" +
            "         ],\n" +
            "         \"id\" : \"page2\",\n" +
            "         \"hspace\" : 5\n" +
            "      },\n" +
            "      {\n" +
            "         \"vspace\" : 5,\n" +
            "         \"instruments\" : [\n" +
            "            [\"awa\" ],\n" +
            "            [\"aws\"]\n" +
            "         ],\n" +
            "         \"id\" : \"page4\",\n" +
            "         \"hspace\" : 5\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\" : \"page3\",\n" +
            "         \"hspace\" : 5,\n" +
            "         \"vspace\" : 5,\n" +
            "         \"instruments\" : [\n" +
            "            [\"awa\", \"twa\", \"blank\", \"blank\" ],\n" +
            "            [ \"blank\", \"blank\", \"blank\", \"blank\" ],\n" +
            "            [ \"blank\", \"blank\", \"blank\", \"blank\" ],\n" +
            "            [ \"blank\", \"blank\", \"blank\", \"blank\" ]\n" +
            "         ]\n" +
            "      }\n" +
            "   ]\n" +
            "}\n";
    private Map<String, Object> configuration;
    private java.util.List<Map<String, Object>> pageList = new ArrayList<Map<String, Object>>();
    private boolean rotate = false;
    private boolean dragging = false;
    private JPanel control;

    public PageLayout(String configFile, Data.Store store) throws NoSuchMethodException {
        this.instruments = new Instruments();
        displayUnits = Data.createSIDisplayUnits();
        layout = new CardLayout();
        this.setLayout(layout);
        this.pageNo = 0;
        this.store = store;
        this.configFile = configFile;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

    }

    public void addControl(JPanel control) {
        this.control = control;
        this.control.addMouseListener(this);
        this.control.addMouseMotionListener(this);
        this.add("control", control);
    }

    public  String loadConfig() throws IOException, ParseException {
        File f = new File(configFile);
        JSONParser jsonParser = new JSONParser();
        String loaded;
        if ( f.exists() )  {
            loaded = f.getAbsolutePath();
            log.info("Loading config file {} ", f.getAbsolutePath());
            FileReader config = new FileReader(f);
            configuration = (Map<String, Object>) jsonParser.parse(config);
            config.close();
        } else {
            loaded = "defaults";
            log.info("Loading defaults ");
            configuration = (Map<String, Object>) jsonParser.parse(DEFAULT_LAYOUT);
        }
        store.addConfiguration(configuration);
        instruments.addConfiguration(configuration);


        refresh();

        return loaded;

    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dragStartX = e.getX();
        dragStartY = e.getY();
        log.debug("Clicked {} ", dragStartX);
        dragging = true;
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

    @Override
    public void mouseDragged(MouseEvent e) {
        if ( !dragging ) {
            log.debug("Not dragging");
            return;
        }
        int distanceX = e.getX() - dragStartX;
        int distanceY = e.getY() - dragStartY;
        log.debug("Dragging {} {}", distanceX, distanceY );
        if (rotate) {
            if (distanceX < -200) {
                layout.show(PageLayout.this, "control");
                rotate = false;
                dragging = false;
            } else if (distanceY > 200) {
                pageNo--;
                if (pageNo < 0) {
                    pageNo = pagesCount - 1;
                }
                layout.show(PageLayout.this, "page" + pageNo);
                rotate = Util.option(pageList.get(pageNo), "rotate", false);
                dragging = false;

            } else if (distanceY < -200) {
                pageNo++;
                if (pageNo == pagesCount) {
                    pageNo = 0;
                }
                layout.show(PageLayout.this, "page" + pageNo);
                rotate = Util.option(pageList.get(pageNo), "rotate", false);
                dragging = false;
            }

        } else {
            if (distanceY < -200) {
                layout.show(PageLayout.this, "control");
                rotate = false;
                dragging = false;
            } else if (distanceX < -200) {
                pageNo--;
                if (pageNo < 0) {
                    pageNo = pagesCount - 1;
                }
                layout.show(PageLayout.this, "page" + pageNo);
                rotate = Util.option(pageList.get(pageNo), "rotate", false);
                dragging = false;

            } else if (distanceX > 200) {
                pageNo++;
                if (pageNo == pagesCount) {
                    pageNo = 0;
                }
                layout.show(PageLayout.this, "page" + pageNo);
                rotate = Util.option(pageList.get(pageNo), "rotate", false);
                dragging = false;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
    private void refresh() {

        java.util.List<Map<String, Object>> pages = (java.util.List<Map<String, Object>>) configuration.get("pages");
        pagesCount = 0;
        for (Map<String, Object> page : pages) {
            Card card = new Card();
            pageList.add(page);
            page.put("card", card);
            this.add("page" + pagesCount, card);
            pagesCount++;
            int i = 0;
            boolean rotatePage = Util.option(page, "rotate", false);
            java.util.List<java.util.List<String>> grid = (java.util.List<java.util.List<String>>) page.get("instruments");
            int nrows = grid.size();
            int ncols = grid.get(0).size();
            long hgap = Util.option(page, "hspace", 10L);
            long vgap = Util.option(page, "vspace", 10L);
            if ( rotatePage ) {
                card.setLayout(new GridLayout((int) ncols, (int) nrows, (int) hgap, (int) vgap));
            } else {
                card.setLayout(new GridLayout((int) nrows, (int) ncols, (int) hgap, (int) vgap));
            }

            if ( rotatePage ) {
                for (int c = ncols-1 ; c >= 0; c--) {
                    for (int r = 0 ; r < nrows; r++) {
                        String name = grid.get(r).get(c);
                        log.debug("Adding Rotated {} {} {}  ", r, c, name);
                        card.add(instruments.create(name, rotatePage, displayUnits, store), i++);
                    }

                }

            } else {
                for (int r = 0 ; r < nrows; r++) {
                    for (int c = 0 ; c < ncols; c++) {
                        String name = grid.get(r).get(c);
                        log.debug("Adding {} {} {}  ", r, c, name);
                        card.add(instruments.create(name, rotatePage, displayUnits, store), i++);
                    }
                }
            }
            log.info("Loaded Page {} c{} r{} ", page.get("id"), ncols, nrows);
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        for(Component c: this.getComponents()) {
            c.setForeground(fg);
        }
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        for(Component c: this.getComponents()) {
            c.setBackground(bg);
        }
    }


    private class Card extends JPanel {
        @Override
        public void setForeground(Color fg) {
            super.setForeground(fg);
            for(Component c: this.getComponents()) {
                c.setForeground(fg);
            }
        }

        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg);
            for(Component c: this.getComponents()) {
                c.setBackground(bg);
            }
        }
    }
}