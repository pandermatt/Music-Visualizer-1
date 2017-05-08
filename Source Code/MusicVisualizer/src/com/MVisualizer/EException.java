package com.MVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EException {
    private static EException EException = null;
    private static String ELOG = "";
    public static JFrame frame;
    private static JTextArea textArea = new JTextArea(ELOG);

    //public static void main(String[] args) {}

    public static EException getInstance(JFrame parent) {
        if (EException == null) {EException = new EException(parent);} frame.toFront(); return EException;
    }

    private EException(JFrame parent){
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception x){x.printStackTrace();}
        frame = new JFrame("Exception Log");
        frame.setIconImage(Visualizer.icon);
        frame.setSize(430, 260);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent windowEvent) {closeWindow();}});
        frame.setLocationRelativeTo(parent);
        frame.addKeyListener(new KeyAdapter() {public void keyReleased(KeyEvent e) {if (e.getKeyCode() == 27) {closeWindow();}
            if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_C)) {ELOG = "";}
        }});

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.add(panel);

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setDoubleBuffered(true);
        textArea.setDragEnabled(false);
        textArea.setEnabled(false);
        textArea.setFont(new Font("Arial", Font.ITALIC, 17));
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Color.BLACK);
        scrollPane.getViewport().add(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static String logException(Exception e){StringWriter sw = new StringWriter(); e.printStackTrace(new PrintWriter(sw)); return sw.toString();}
    private static void write(String s){ELOG += s+"\n";}
    public static void setText(String s){textArea.setText(s);}
    public static void append(Exception except){ELOG += "" + new SimpleDateFormat("h:mm:ss a").format(new Date()) + " - " + (logException(except)) + "\n";}
    public static void update(){try {if (textArea != null) {textArea.setText(ELOG);}} catch (Exception ex) {append(ex);}}
    private static void closeWindow() {EException = null; frame.dispose();}
}
