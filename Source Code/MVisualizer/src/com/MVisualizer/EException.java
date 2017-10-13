package com.MVisualizer;

import com.MVisualizer.Helpers.KAdapter;
import com.MVisualizer.Helpers.WAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EException {
    private static EException EException = null;
    public static JFrame frame;
    private static JTextArea textArea = new JTextArea();

    //public static void main(String[] args) {}

    public static void getInstance(JFrame parent) {
        if (EException == null) {EException = new EException(parent);} frame.toFront();
    }

    private EException(JFrame parent){
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception x){x.printStackTrace();}
        frame = new JFrame("Exception Log");
        frame.setIconImage(MConstants.icon);
        frame.setSize(430, 260);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WAdapter.WindowClosing(e -> closeWindow()));
        frame.setLocationRelativeTo(parent);
        frame.addKeyListener(new KAdapter(e->{},e->{
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) closeWindow();
            if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_C)) {textArea.setText("");}
        }));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.add(panel);

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setDoubleBuffered(true);
        textArea.setDragEnabled(false);
        textArea.setEnabled(false);
        textArea.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Color.BLACK);
        scrollPane.getViewport().add(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static String logException(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private static void write(String s) {
        textArea.append(s + "\n");
    }

    static void setText(String s) {textArea.setText(s);}

    static void append(Exception except) {
        textArea.setText(new SimpleDateFormat("h:mm:ss a").format(new Date()) + logException(except) + "\n");
    }

    private static void closeWindow() {
        EException = null;
        frame.dispose();
    }
}
