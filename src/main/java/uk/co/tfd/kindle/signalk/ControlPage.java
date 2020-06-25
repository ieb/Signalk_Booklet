package uk.co.tfd.kindle.signalk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by ieb on 20/06/2020.
 */
public class ControlPage extends JPanel implements StatusUpdates.StatusUpdateListener {
    private static Logger log = LoggerFactory.getLogger(ControlPage.class);
    private final JButton invertButton;
    private final JButton exitButton;
    private final JLabel title;
    private final JLabel instructions;
    private final JPanel titles;
    private final JTextArea statusMessages;
    private final JPanel buttons;


    public interface ControlHook {
        void invertColors();

        void exit();
    }


    public static class ThemePanel extends JPanel {

        public ThemePanel(LayoutManager layoutManager) {
            super(layoutManager);
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

    }

    private java.util.List<String> status = new ArrayList<String>();

    public ControlPage(ControlHook controlHook) {
        this.setLayout(new BorderLayout());

        invertButton = new JButton("Invert Colors");
        invertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlHook.invertColors();
            }
        });
        exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlHook.exit();
            }
        });
        title = new JLabel("SignalK Eink for Kindle");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        instructions = new JLabel("Swipe left or right for pages, and up for this screen");
        instructions.setHorizontalAlignment(JLabel.CENTER);
        instructions.setFont(new Font("Arial", Font.PLAIN, 10));

        titles = new ThemePanel(new GridLayout(2,1));
        titles.add(title);
        titles.add(instructions);

        this.add(titles, BorderLayout.PAGE_START);

        statusMessages = new JTextArea();
        statusMessages.setFont(new Font("Arial", Font.PLAIN, 8));
        statusMessages.setLineWrap(true);
        statusMessages.setEditable(false);
        //statusMessages.setFocusable(false);
        statusMessages.setEnabled(false);
        this.add(statusMessages, BorderLayout.CENTER);

        buttons = new ThemePanel(new BorderLayout());
        //buttons.add(invertButton, BorderLayout.LINE_START);
        buttons.add(exitButton, BorderLayout.LINE_END);
        this.add(buttons, BorderLayout.PAGE_END);
    }




    public void setTheme(MainScreen.Theme theme) {
        this.invertButton.setForeground(theme.getControlForeground());
        this.exitButton.setForeground(theme.getControlForeground());
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


    @Override
    public void onStatusChange(String text) {
        statusMessages.append(text + "\n");
        Document d = statusMessages.getDocument();

        try {
            while (statusMessages.getLineCount() > 20) {
                d.remove(0, statusMessages.getLineEndOffset(0));
            }
        } catch ( BadLocationException e) {
            log.error(e.getMessage(), e);
        }
    }

}
