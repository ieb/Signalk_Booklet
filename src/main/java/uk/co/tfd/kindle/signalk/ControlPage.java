package uk.co.tfd.kindle.signalk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by ieb on 20/06/2020.
 */
public class ControlPage extends JPanel implements StatusUpdates.StatusUpdateListener {
    private final JLabel label;
    private java.util.List<String> status = new ArrayList<String>();

    public ControlPage(MainScreen.MainScreenExit exitHook) {
        this.setLayout(new FlowLayout());
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitHook.exit();
            }
        });
        this.add(new Label("<html><h1>SignalK Eink for Kindle</h1></html>"));
        this.add(new Label("<html><p>Swipe left or right for pages, and up for this screen</p></html>"));
        label = new JLabel();
        this.add(label);
        this.add(exitButton);
    }


    @Override
    public void onStatusChange(String text) {
        status.add(text);
        while (status.size() > 20 ) {
            status.remove(0);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        for(String s : status) {
            sb.append(s).append("<br>");
        }
        label.setText(sb.toString());
    }
}
