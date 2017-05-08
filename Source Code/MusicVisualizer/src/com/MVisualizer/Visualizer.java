package com.MVisualizer;

import controlP5.*;
import controlP5.Button;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Visualizer extends PApplet {
    //public static AudioInput player;
    public static FFT fft;
    public static Minim minim;
    public static AudioPlayer player;
    public static ControlP5 cp5;

    //  Ints
    public static int avgSize;
    public static int barStep = 1;
    public static int backgroundstep = 50;
    public static int buffersize = 512;
    public static int visualMode = 0;

    //  Floats
    public float minVal = 0.0f, maxVal = 0.0f;
    public static float smoothing = 0.80f;
    public static float cAmplitude = 90;
    public static float ellipseR = 0;
    public static float bassKick = 2.2f;
    public static float r = 150.0f;
    public static float strokeWeight = 2.6f;
    public static float amplitude = 1;
    public static float angOffset = 2.10f;
    public static float a = 0;

    //  Booleans
    public static boolean isPaused = false;
    public static boolean isMute = false;

    //  Strings
    public static String iconImage = "logo3.png";
    public static String music = "Wildfire.mp3";
    public static Image icon = Toolkit.getDefaultToolkit().getImage(Visualizer.class.getResource("/" + iconImage));

    //  Arrays
    public static float[] fftSmooth;
    public static Slider[] mySliders = new Slider[10];
    public static Button[] myButtons = new Button[10];
    public static Accordion accordion;

    static {
        System.setProperty("sun.java2d.transaccel", "True");
        System.setProperty("sun.java2d.ddforcevram", "True");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
    }

    public static void main(String[] args) {
        PApplet.main(Visualizer.class);
    }

    public void setup() {
        surface.setResizable(true);
        surface.setFrameRate(120);
        surface.setIcon(loadImage(iconImage));
        smooth();
        strokeCap(SQUARE);
        noiseDetail(1, 0.95f);

        cp5 = new ControlP5(this);
        ControlSetup.setupControls();
    }

    public void settings() {
        minim = new Minim(this);
        ControlSetup.ref = this;
        setUpPlayer();
        size(1080, 720, JAVA2D);
    }

    public void draw() {
        background(0);
        EException.update();
        fft.forward(player.mix);
        showBackGround(backgroundstep);
        showSongTime();
        cAmplitude = lerp(cAmplitude, smoothing * fft.calcAvg(minVal, maxVal), .1f);
        ellipseR = constrain(cAmplitude * bassKick, 0.1f, r * 65.0f);
        visualize();
        //EException.setText(isSongOver()+"");
        //songEnded();
    }

    private void setUpPlayer() {
        //player = minim.getLineIn();
        player = minim.loadFile(fileChooser(music), buffersize);
        fft = new FFT(player.bufferSize(), player.sampleRate());
        fft.logAverages(3000, 200);
        avgSize = fft.avgSize();
        fftSmooth = new float[avgSize];
        player.play();
    }

    public boolean isSongOver() {return (!isPaused && (player.right.level() <= 0 && player.left.level() <= 0));}
    public void songEnded() {
        if (player != null) EException.setText(player.position() + " (" + player.left.level() + " - " + player.right.level() + ") " + player.isPlaying());
    }

    private void visualize() {
        switch (visualMode) {
            case 0: circulerPulse(); break;
            case 1: signal(); break;
            case 2: inverse(); break;
            case 3: functionMusic(); break;
            default: break;
        }
    }

    private void smoothPulse() {
        for (int i = 0; i < avgSize; i++) {
            // Smooth using exponential moving average
            fftSmooth[i] = smoothing * fftSmooth[i] + (1 - smoothing) * fft.getAvg(i);
            // Find max and min values ever displayed across whole spectrum
            if (fftSmooth[i] > maxVal) maxVal = fftSmooth[i];
            if (fftSmooth[i] < minVal) minVal = fftSmooth[i];
        }
    }

    private void circulerPulse() {
        smoothPulse();
        final float maxHeight = (height / 2) * 0.75f;

        // Calculate the total range of smoothed spectrum; this will be used to scale all values to range 0...1
        final float range = maxVal - minVal;
        final float scaleFactor = range + 0.00001f; // avoid div. by zero

        for (int i = 0; i < avgSize; i += barStep) {
            float angle = i * angOffset * (2 * PI) / avgSize;
            float fftSmoothDisplay = maxHeight * (((amplitude * fftSmooth[i]) - minVal) / scaleFactor);
            float rad = r * 2;
            float x = rad * cos(angle);
            float y = rad * sin(angle);
            float x2 = (rad + fftSmoothDisplay) * cos(angle) + .85f;
            float y2 = (rad + fftSmoothDisplay) * sin(angle) + .85f;

            //int c = (int) map(i, 0, fft.specSize(), 0, 470);
            float c = map(i, 0, fft.specSize(), 0, 275);
            colorMode(HSB);
            stroke(c, 255, 255);
            strokeWeight(strokeWeight);
            line(x + width / 2, y + height / 2, x2 + width / 2, y2 + height / 2);
        }

        noStroke();
        fill(lerp(0, map(ellipseR, 0, (r * 3) - 50, 0, 255), .23f), 255, 255);
        ellipse(width / 2, height / 2, ellipseR, ellipseR);
    }

    private void signal() {
        final float maxHeight = (height / 2) * 0.75f;
        smoothPulse();

        // Calculate the total range of smoothed spectrum; this will be used to scale all values to range 0...1
        final float range = maxVal - minVal;
        final float scaleFactor = range + 0.00001f; // avoid div. by zero

        for (int i = 0; i < avgSize - 1; i += barStep) {
            float x1 = map(i, 0, avgSize, 0, width);
            float x2 = map(i + 1, 0, avgSize, 0, width);
            float amp = maxHeight * (((amplitude * fftSmooth[i]) - minVal) / scaleFactor);
            float y = (height / 2 - 20);

            int c = (int) map(i, 0, fft.specSize(), 0, 285);
            stroke(c, 255, 255);
            strokeWeight(strokeWeight);
            line(x1, y + amp, x2, y - amp);
        }
    }

    private void inverse() {
        final float maxHeight = (height / 2) * 0.75f;
        smoothPulse();

        // Calculate the total range of smoothed spectrum; this will be used to scale all values to range 0...1
        final float range = maxVal - minVal;
        final float scaleFactor = range + 0.00001f; // avoid div. by zero

        for (int i = 0, size = avgSize - 1; i < size; i += barStep) {
            float x1 = map(i, 0, size, 0, width);
            float x2 = map(i + 1, 0, size, 0, width);

            float x3 = map(i, 0, size, width, 0);
            float x4 = map(i + 1, 0, size, width, 0);

            float amp = maxHeight * (((amplitude * fftSmooth[i]) - minVal) / scaleFactor);
            float y = (height / 2 - 20);

            int c = (int) map(i, 0, fft.specSize(), 0, 285);
            stroke(c, 255, 255);
            strokeWeight(strokeWeight);
            line(x1, y, x2, y + amp);
            line(x3, y, x4, y - amp);
        }
    }

    public void functionMusic() {
        final float maxHeight = (height / 2) * 0.75f;
        smoothPulse();

        // Calculate the total range of smoothed spectrum; this will be used to scale all values to range 0...1
        final float range = maxVal - minVal;
        final float scaleFactor = range + 0.00001f; // avoid div. by zero

        for (int i = 0; i < avgSize - 1; i += barStep) {
            float amp = maxHeight * (((amplitude * fftSmooth[i]) - minVal) / scaleFactor);

            float x1 = map(i, 0, avgSize, 0, width);
            float x2 = map(i + 1, 0, avgSize, 0, width);
            float y1 = -musicTransform(x1) + height / 2;
            float y2 = -musicTransform(x2) + height / 2;

            int c1 = (int) map(i, 0, fft.specSize(), 0, 285);
            stroke(c1, 255, 255);
            strokeWeight(strokeWeight);
            line(1.7f * x1, y1 + amp, 1.7f * x2, y2 - amp);
        }
        a += 0.015f;
    }

    public float musicTransform(float x) {
        //float m = 2, a = 325;
        //return (float) ((m * (x - a) * Math.signum(a - x) + m * a) - height / 2 + (60 * Math.sin(.08f * x + Visualizer.a))); // Triangle Function: http://math.stackexchange.com/questions/544559/is-there-any-equation-for-triangle
        return (float) (75f * Math.sin(.015 * x + a));
        //return 20f * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(.04f * x))))))));
        //return (float) Math.pow(.02 * x, 2);
    }

    private void showSongTime() {
        try {
            surface.setTitle(String.format("%d X %d -- Song Time: %s", width, height, (new SimpleDateFormat("mm:ss")).format(new Date(player.position()))));
            float posx = map(player.position(), 0, player.length(), 0, width);
            noStroke();
            colorMode(HSB);
            fill(map(player.position(), 0, player.length(), 0, 255), 255, 255);
            rect(0, 0, posx, 12);
        } catch (Exception e) {
            EException.append(e);
        }
    }

    public void mouseReleased() {
        try {
            if (mouseY >= 0 && mouseY <= 12) {
                player.cue(((int) map(mouseX, 0, width, 0, player.length())));
            }
        } catch (Exception e) {EException.append(e);}
    }

    public String fileChooser(String if_null) {
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception x) {EException.append(x);}
        FileDialog dialog = new FileDialog((Frame) null, "Select", FileDialog.LOAD);
        dialog.setFilenameFilter((dir, name) -> (name.endsWith(".mp3") || name.endsWith(".wav")));
        dialog.setFile("*.mp3; *.wav");
        dialog.show();

        File choice = null;
        String dir = dialog.getDirectory();
        String file = dialog.getFile();

        if (dir != null && file != null) {
            choice = new File(dir, file);
        }

        if (choice != null) return choice.getAbsolutePath();
        else return if_null;
    }

    private void showBackGround(int step) {
        for (int x = step; x < width; x += step) {
            for (int y = step; y < height; y += step) {
                float n = noise((float) (x * 0.005), (float) (y * 0.007), (float) (frameCount * 0.02));
                pushMatrix();
                translate(x, y);
                rotate(TWO_PI * n);
                scale(map(ellipseR, 0, (r * 1.85f) - 30, 0, 15) * n);
                strokeWeight(.2f);
                stroke(map(n, 0, Float.POSITIVE_INFINITY, 0, 255));
                fill(255);
                rect(0, 0, .8f, .8f);
                popMatrix();
            }
        }
    }

    public void changeVisual(){
        visualMode++;
        if (visualMode > 3) visualMode = 0;
    }

    public void pausePlayer(){
        if (player.isPlaying()) {
            player.pause();
            isPaused = true;
        } else {
            player.play();
            isPaused = false;
        }
    }

    public void mutePlayer(){
        if (player.isMuted()) {player.unmute(); isMute = false;}
        else {player.mute(); isMute = true;}
    }

    public void changeSong(){
        String filePath = fileChooser(music);
        if (!filePath.equals(music)) {
            player.pause();
            player = minim.loadFile(filePath, buffersize);
            fft = new FFT(player.bufferSize(), player.sampleRate());
            fft.logAverages(3000, 200);
            avgSize = fft.avgSize();
            fftSmooth = new float[avgSize];
            player.play();
        }
    }

    private boolean toggle(boolean t) {
        return !t;
    }

    public void keyReleased() {
        super.keyReleased();

        if (key == 'm') {
            mutePlayer();
        }

        if (key == 'x') {
            changeVisual();
        }

        if (keyEvent.isControlDown() && keyEvent.isAltDown()) EException.getInstance(null);

        if (keyCode == KeyEvent.VK_SPACE) {
            pausePlayer();
        }

        if (key == 'v') {
            changeSong();
        }
    }
}