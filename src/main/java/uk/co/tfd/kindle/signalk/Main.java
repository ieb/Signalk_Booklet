package uk.co.tfd.kindle.signalk;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


/**
 * Created by ieb on 06/06/2020.
 */
public class Main {

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.logFile","System.err");
        System.setProperty("org.slf4j.simpleLogger.showDateTime","true");
        System.setProperty("org.slf4j.simpleLogger.showShortLogName","true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat","yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    }

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static Timer timer;

    public static void main(String[] args) throws ParseException, IOException, NoSuchMethodException {
        Util.setKindle(false); // Kindle AWT implementation has some non standard behaviour.
        Util.setScreenResolution(Toolkit.getDefaultToolkit().getScreenResolution());

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        MainScreen mainScreen = new MainScreen(frame,
                "src/test/resources/config.json",
                new MainScreen.MainScreenExit() {
            @Override
            public void exit() {
                System.exit(0);
            }
        });
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mainScreen.start();
                } catch (IOException e1) {
                    log.error(e1.getMessage(), e);
                }
                timer.stop();
            }
        });
        timer.start();

    }
}
