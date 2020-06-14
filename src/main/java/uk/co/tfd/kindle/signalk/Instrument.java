package uk.co.tfd.kindle.signalk;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ieb on 06/06/2020.
 */
public class Instrument  extends JPanel {

    private String value = "0.0";

    public Instrument(int v) {
        value = String.valueOf(v);
    }



    @Override
    public void paint(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Serif", Font.PLAIN, 20);
        g2.setFont(font);

        System.err.println("PaintCalled "+value);
        g2.drawString(value, 40, 120);
    }

    public void update(String value)  {
        this.value = value;
        System.err.println("Value is "+this.value);
        this.repaint();
    }


}
