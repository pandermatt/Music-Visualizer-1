package com.MVisualizer;

import com.MVisualizer.Helpers.KAdapter;
import com.MVisualizer.Helpers.WAdapter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class InstructionsWindow {
    private static InstructionsWindow instructionsWindow;
    public static JFrame frame;

    //Make sure type and int given when getInstance is called are the same! If values differ will cause indexOutOfBounds Error
    public static void getInstance(JFrame parent, int w, int h, String title, String instructions) {
        if (instructionsWindow == null) instructionsWindow = new InstructionsWindow(parent, w, h, title, instructions);
        frame.toFront();
    }

    public static void getInstance() {
        if (instructionsWindow == null) {
            instructionsWindow = new InstructionsWindow( null, 675, 480, "Music Visualizer Instructions", MConstants.mvInstructions);
//            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//            instructionsWindow = new InstructionsWindow( null,
//                    (int) (d.width * .35), (int) (d.height * .45),
//                    "Music Visualizer Instructions", MConstants.mvInstructions);
        }
        frame.toFront();
    }

    private InstructionsWindow(JFrame parent, int w, int h, String title, String instructions) {
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception e){EException.append(e);}
        frame = new JFrame(title);
        frame.setIconImage(MConstants.icon);
        frame.setSize(w, h);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WAdapter.WindowClosing(windowEvent -> close()));
        frame.setLocationRelativeTo(parent);
        frame.addKeyListener(new KAdapter(e -> {}, this::close));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.getContentPane().add(panel);

        JLabel label = new JLabel(instructions);
        label.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(label);
        panel.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void close(){instructionsWindow = null; frame.dispose();}
    private void close(KeyEvent e) {if (e.getKeyCode() == KeyEvent.VK_ESCAPE) close();}
}
