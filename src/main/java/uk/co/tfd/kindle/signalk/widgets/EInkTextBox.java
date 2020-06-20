package uk.co.tfd.kindle.signalk.widgets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tfd.kindle.signalk.Data;
import uk.co.tfd.kindle.signalk.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Map;

import static uk.co.tfd.kindle.signalk.Data.DataValue;

/**
 * Created by ieb on 09/06/2020.
 */
public class EInkTextBox extends JPanel implements Data.Listener<DataValue> {

    private static final Logger log = LoggerFactory.getLogger(EInkTextBox.class);
    private final boolean rotate;
    Font normalFont;
    int boxWidth;
    int boxHeight;
    private int fontSize;
    Font largeFont;
    Font mediumFont;
    Font smallFont;
    int mediumLineSpace;
    int smallLineSpace;
    int borderPadding;
    final Data.DisplayUnits displayUnits;
    private boolean withStats;
    String out;
    String outmean;
    String outstdev;
    String outmax;
    String outmin;
    private int boxSize;
    private Map<String, String> labels;
    private java.util.List<String> sources;
    DecimalFormat dataFormat;

    public EInkTextBox(boolean rotate, Map<String, Object> options, Data.DisplayUnits displayUnits, Data.Store store) {
        this.displayUnits = displayUnits;
        this.dataFormat = Util.option(options, "dataFormat", new DecimalFormat("0.##"));
        this.boxSize = Util.option(options, "boxSize", 100);
        this.labels = Util.option(options, "labels", null);
        this.sources = Util.option(options, "sources", null);
        this.withStats = Util.option(options, "withStats",true);
        this.rotate = rotate;
        this.out = "-.-";
        this.outmean = "-.-";
        this.outstdev = "-.-";
        this.outmax = "-.-";
        this.outmin = "-.-";

        this.boxWidth = (int)(boxSize*2.2);
        this.boxHeight = (int)(boxSize*1.2);

        this.fontSize = (int)(boxSize*0.8);
        this.largeFont = new Font("Arial", Font.PLAIN, fontSize);
        this.normalFont = largeFont.deriveFont((float) (fontSize /2.5));
        this.mediumFont = largeFont.deriveFont((float) (fontSize /4));
        this.smallFont = largeFont.deriveFont((float) (fontSize /6));
        this.mediumLineSpace = boxHeight /4;
        this.smallLineSpace = boxHeight /8;
        this.borderPadding = boxWidth / 30;


        this.setSize((int)(this.boxSize*2.2),(int)(this.boxSize*1.2));

        if ( this.sources != null) {
            for (String source : sources) {
                Data.DataKey k = Data.DataKey.get(source);
                if ( k != null) {
                    Data.DataValue dv = store.get(k);
                    if (dv != null) {
                        dv.addListener(this);
                    } else {
                        log.warn("Unable to find data value for {} ", k);
                    }
                } else {
                    log.warn("Unable to find Key for {} ", source);
                }
            }
        }

    }

    @Override
    public void onUpdate(DataValue d) {
        // may want to check for changes.
        if ( formatOutput(d) ) {
            this.repaint();
        }
    }



    boolean formatOutput(DataValue data) {
        if ( this.withStats) {

            String out = this.displayUnits.toDispay(data.getValue(), dataFormat, data.key.type);
            String outmax = this.displayUnits.toDispay(data.getMax(), dataFormat, data.key.type);
            String outmin = this.displayUnits.toDispay(data.getMin(), dataFormat, data.key.type);
            String outmean = "\u03BC " + this.displayUnits.toDispay(data.getMean(), dataFormat, data.key.type);
            String outstdev = "\u03C3 " + this.displayUnits.toDispay(data.getStdev(), dataFormat, data.key.type);
            if (!out.equals(this.out) ||
                    !outmax.equals(this.outmax) ||
                    !outmin.equals(this.outmin) ||
                    !outmean.equals(this.outmean) ||
                    !outstdev.equals(this.outstdev)) {
                this.out = out;
                this.outmax = outmax;
                this.outmin = outmin;
                this.outmean = outmean;
                this.outstdev = outstdev;
                return true;
            }
        } else {
            String out = this.displayUnits.toDispay(data.getValue(), dataFormat, data.key.type);
            if ( !out.equals(this.out)) {
                this.out = out;
                return true;
            }
        }
        return false;
    }

    private void recalcSize() {
        int wBoxSize = (int)(this.getWidth()/2.2);
        int hBoxSize = (int)(this.getHeight()/1.2);
        int nBoxSize = Math.min(wBoxSize, hBoxSize);

        if ( nBoxSize != this.boxSize ) {
            this.boxSize = nBoxSize;

            this.boxWidth = (int)(boxSize*2.2);
            this.boxHeight = (int)(boxSize*1.2);

            this.fontSize = (int)(boxSize*0.8);
            this.largeFont = new Font("Arial", Font.PLAIN, fontSize);
            this.normalFont = largeFont.deriveFont((float) (fontSize /2.5));
            this.mediumFont = largeFont.deriveFont((float) (fontSize /4));
            this.smallFont = largeFont.deriveFont((float) (fontSize /6));
            this.mediumLineSpace = boxHeight /4;
            this.smallLineSpace = boxHeight /8;
            this.borderPadding = boxWidth / 30;
        }
    }



    @Override
    public void paint(Graphics graphics) {
        recalcSize();

        Graphics2D g2 = (Graphics2D)graphics;

        if ( rotate ) {
            g2.translate(0, boxWidth);
            g2.rotate(-Math.PI / 2);
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);


        g2.drawRoundRect(0, 0, boxWidth, boxHeight, 10, 10);

        renderInstrument(g2);
    }

    void drawBaseLine(String l, String r, Graphics2D g2) {
        Util.drawString(l, borderPadding, boxHeight, mediumFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
        Util.drawString(r, boxWidth - borderPadding, boxHeight, mediumFont, Util.HAlign.RIGHT, Util.VAlign.BOTTOM, g2);
    }
    public void drawTopLine(String l, Graphics2D g2) {
        Util.drawString(l, borderPadding, 0, mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
    }

    void twoLineLeft(String line1, String line2, Graphics2D g2) {
        Util.drawString(line1, borderPadding, mediumLineSpace, mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
        Util.drawString(line2, borderPadding, boxHeight-mediumLineSpace, mediumFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);

    }
    void twoLineRight(String line1, String line2, Graphics2D g2) {
        Util.drawString(line1, boxWidth - borderPadding, mediumLineSpace, mediumFont, Util.HAlign.RIGHT, Util.VAlign.TOP, g2);
        Util.drawString(line2, boxWidth - borderPadding, boxHeight-mediumLineSpace, mediumFont, Util.HAlign.RIGHT, Util.VAlign.BOTTOM, g2);

    }

    public void twoLineCenter(String line1, String line2, Graphics2D g2) {
        Util.drawString(line1, boxWidth / 2, mediumLineSpace, mediumFont, Util.HAlign.CENTER, Util.VAlign.TOP, g2);
        Util.drawString(line2, boxWidth / 2, boxHeight-mediumLineSpace, mediumFont, Util.HAlign.CENTER, Util.VAlign.BOTTOM, g2);
    }



    void renderInstrument(Graphics2D g2) {
        Rectangle2D rect = largeFont.getStringBounds(this.out, g2.getFontRenderContext());
        if (rect.getWidth() > (boxWidth * 0.8)) {
            double newSize = fontSize *(boxWidth *0.8)/rect.getWidth();
            Font f = largeFont.deriveFont((float)newSize);
            Util.drawString(this.out, boxWidth / 2, boxHeight / 2, f, Util.HAlign.CENTER , Util.VAlign.CENTER, g2);
        } else {
            Util.drawString(this.out, boxWidth / 2, boxHeight / 2, largeFont, Util.HAlign.CENTER, Util.VAlign.CENTER, g2);
        }
        if (labels != null) {
            if (!this.withStats) {
                Util.drawString(labels.get("tl"), borderPadding, mediumLineSpace, mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
                Util.drawString(labels.get("tr"), boxWidth - borderPadding, mediumLineSpace, mediumFont, Util.HAlign.RIGHT, Util.VAlign.TOP, g2);
            }
            Util.drawString(labels.get("bl"), borderPadding, boxHeight, mediumFont, Util.HAlign.LEFT, Util.VAlign.BOTTOM, g2);
            Util.drawString(labels.get("br"), boxWidth - borderPadding, boxHeight, mediumFont, Util.HAlign.RIGHT, Util.VAlign.BOTTOM, g2);
        }
        if (this.withStats) {
            Util.drawString(this.outmin, borderPadding, 0, mediumFont, Util.HAlign.LEFT, Util.VAlign.TOP, g2);
            Util.drawString(this.outmax, boxWidth - borderPadding, 0, mediumFont, Util.HAlign.RIGHT, Util.VAlign.TOP, g2);
            Util.drawString(this.outmean, (boxWidth / 2), 0, smallFont, Util.HAlign.CENTER, Util.VAlign.TOP, g2);
            Util.drawString(this.outstdev, (boxWidth / 2), smallLineSpace, smallFont, Util.HAlign.CENTER, Util.VAlign.TOP, g2);
        }
    }


}