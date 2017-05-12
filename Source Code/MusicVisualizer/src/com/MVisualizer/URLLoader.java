package com.MVisualizer;

import com.MVisualizer.Helpers.MAdapter;
import com.MVisualizer.Helpers.WAdapter;
import de.voidplus.soundcloud.Track;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static com.MVisualizer.Visualizer.icon;
import static com.MVisualizer.Visualizer.soundCloudApi;

public class URLLoader {
    private static URLLoader urlLoader = null;
    public static JFrame frame;
    private static JTextField textField;
    public static JCheckBox checkBox;
    public static JButton go, cancel;

    public static URLLoader getInstance(){
        if (urlLoader == null) {urlLoader = new URLLoader();} frame.toFront(); return urlLoader;
    }

    private URLLoader() {
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception e1){EException.append(e1);}
        frame = new JFrame("SoundCloud Song");
        frame.setIconImage(icon);
        frame.setSize(431, 134);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WAdapter.WindowClosing(e -> close()));
        frame.setLocationRelativeTo(null);

        JLabel lblPleaseEnterA = new JLabel("Please Enter A SoundCloud URL :D");
        lblPleaseEnterA.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblPleaseEnterA.setHorizontalAlignment(SwingConstants.CENTER);
        frame.getContentPane().add(lblPleaseEnterA, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        textField = new JTextField(10);
        textField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        frame.getContentPane().add(textField, BorderLayout.CENTER);

        checkBox = new JCheckBox();
        checkBox.setFont(new Font("Tahoma", Font.PLAIN, 17));
        checkBox.setText("Keep on Ok/Go");
        panel.add(checkBox);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem cut = new JMenuItem("Cut");
        cut.addActionListener(e -> {if (e.getSource() == cut) {textField.cut();}});
        popupMenu.add(cut);
        JMenuItem copy = new JMenuItem("Copy");
        copy.addActionListener(e -> {if (e.getSource() == copy) {textField.copy();}});
        popupMenu.add(copy);
        JMenuItem paste = new JMenuItem("Paste");
        paste.addActionListener(e -> {if (e.getSource() == paste) {textField.paste();}});
        popupMenu.add(paste);
        JMenuItem clear = new JMenuItem("Clear");
        clear.addActionListener(e -> {if (e.getSource() == clear) {textField.setText("");}});
        popupMenu.add(clear);
        addPopup(textField, popupMenu);

        go = new JButton("Ok / Go");
        go.addActionListener(e -> SwingUtilities.invokeLater(URLLoader::handleSoundCloudRequest));
        go.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(go);

        cancel = new JButton("Cancel");
        cancel.addActionListener(e -> close());
        cancel.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(cancel);

        frame.setVisible(true);
    }

    public static void handleSoundCloudRequest() {
        boolean error = false;
        Track requestedTrack = null;
        String requestURL = textField.getText();

        if (requestURL != null && !requestURL.isEmpty() && (requestURL.startsWith("http://") || requestURL.startsWith("https://"))) {
            try {requestedTrack = soundCloudApi.getTrackFromURL(requestURL);
            } catch (IOException e) {
                EException.append(e);
                error = true;
                Controls.visualizerRef.showError("<html><h4>Could Not Request Song :(" +
                        "<br> Possible Reasons: <ul>" +
                        "<li>No Internet (Wifi, Ethernet) Connection </li> " +
                        "<li>SoundCloud Song is Not Streamable</li> " +
                        "<li>Entered an Invalid Link</li>" +
                        "</ul></h4></html>", "Request Error");
            }

            if (!error) {
                Controls.visualizerRef.changeSong(requestedTrack);
                if (!checkBox.isSelected()) close();
            }
        }
    }

    private void addPopup(Component c, JPopupMenu p) {
        c.addMouseListener(new MAdapter(e -> {if (e.isPopupTrigger()) {showMenu(e, p);}}, e -> {if (e.isPopupTrigger()) {showMenu(e, p);}}));
    }
    private void showMenu(MouseEvent e, JPopupMenu p) {p.show(e.getComponent(), e.getX(), e.getY());}
    private static void close() {frame.dispose(); urlLoader = null;}
}
