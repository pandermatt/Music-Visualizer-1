package com.MVisualizer;

import com.Cipher.SCPack;
import controlP5.Accordion;
import controlP5.Button;
import controlP5.ControlP5;
import controlP5.Slider;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import de.voidplus.soundcloud.Track;
import org.apache.commons.io.FilenameUtils;
import processing.core.PApplet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Visualizer extends PApplet {
//    public static AudioInput player;
    public static FFT fft;
    public static Minim minim;
    public static AudioPlayer player;
    public static ControlP5 cp5;
    public static ISoundCloud soundCloudApi = null;

    //  Ints
    public static int avgSize = 0;
    public static int fftOffset = 120;
    public static int barStep = 1;
    public static int backgroundstep = 50;
    public static int bufferSize = 2048;
//    public static int bufferSize = 512*2;
    public static int minBandwidth = 2000;
    public static int bandsPerOctave = 300;
    public static int transformMode = 0;
    public static int visualMode = 0;

    //  Floats
    public float minVal = 0.0f, maxVal = 0.0f;
    public static float smoothing = 0.80f;
    public static float cAmplitude = 90;
    public static float ellipseR = 0;
    public static float bassKick = 2.5f;
    public static float circleRadius = 145.0f;
    public static float strokeWeight = 2.25f;
    public static float amplitude = 1.5f;
    public static float angOffset = 1.4f;
    public static float a = 0;

    public static float maxDrawHeight = 1.0f;
    public static float spectrumScaleFactor = 1.0f;

    //  Booleans
    public static boolean isPaused = false;
    public static boolean isMute = false;

    //  Strings
    public static String iconImage = "logo3.png";
    public static String nameOfSong = "";
    public static String title = "";
    public static Image icon = Toolkit.getDefaultToolkit().getImage(Visualizer.class.getResource("/" + iconImage));

    //  Arrays
    public static float[] fftSmooth;
    public static Slider[] mySliders = new Slider[10];
    public static Button[] myButtons = new Button[10];
    public static Accordion accordion;

    static {
        System.setProperty("sun.java2d.transaccel", "true");
        System.setProperty("sun.java2d.ddforcevram", "true");
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
        Controls.setupControls();
    }

    public void settings() {
        //PJOGL.setIcon("logo3.png");
        minim = new Minim(this);
        Controls.visualizerRef = this;
        try{if (URLLoader.hasConnection()) initSoundCloud();} catch (Exception e){EException.append(e);}
        setUpPlayer();
        //size(1080, 720, P2D);
        size(1080, 720, JAVA2D);
    }

    public void stop() {
        player.close();
        minim.stop();
        super.stop();
    }

    public void draw() {
        EException.update();
        fft.forward(player.mix);

        background(0);
        stroke(255);
        showBackGround(backgroundstep);
        showSongTime();

        smoothPulse();
        visualize();
        //EException.setText(isSongOver()+"");
    }

    private void setUpPlayer() {
        String song = fileChooser();
        if (song != null) {
            player = minim.loadFile(song, bufferSize);
            fft = new FFT(player.bufferSize(), player.sampleRate());
            fft.logAverages(minBandwidth, bandsPerOctave);
            fft.window(FFT.GAUSS);

            avgSize = fft.avgSize() - fftOffset;
            fftSmooth = new float[avgSize];

            //System.out.println(avgSize);
            player.play();
        }
        else System.exit(0);
    }

    public boolean isSongOver() {return (!isPaused && (player.right.level() <= 0 && player.left.level() <= 0));}
    public void songEnded() {
        if (player != null) EException.setText(player.position() + " (" + player.left.level() + " - " + player.right.level() + ") " + player.isPlaying());
    }

    private void visualize() {
        cAmplitude = lerp(cAmplitude, smoothing * fft.calcAvg(minVal, maxVal), .1f);
        ellipseR = constrain(cAmplitude * bassKick, 0.1f, circleRadius * 65.0f);
        maxDrawHeight = (height / 2) * 0.75f;
        spectrumScaleFactor = (maxVal - minVal) + 0.00001f;

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
        for (int i = 0; i < avgSize; i += barStep) {
            float angle = i * angOffset * 2 * PI / avgSize;
            float fftSmoothDisplay = maxDrawHeight * (((amplitude * fftSmooth[i]) - minVal) / spectrumScaleFactor);
            float rad = circleRadius * 2;
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
        fill(lerp(0, map(ellipseR, 0, circleRadius * 3 - 50, 0, 255), .23f), 255, 255);
        ellipse(width / 2, height / 2, ellipseR, ellipseR);
    }

    private void signal() {
        for (int i = 0; i < avgSize - 1; i += barStep) {
            float x1 = map(i, 0, avgSize, 0, width);
            float x2 = map(i + 1, 0, avgSize, 0, width);
            float amp = maxDrawHeight * (((amplitude * fftSmooth[i]) - minVal) / spectrumScaleFactor);
            float y = (height / 2 - 20);

            float c = map(i, 0, fft.specSize(), 0, 285);
            stroke(c, 255, 255);
            strokeWeight(strokeWeight);
            line(x1, y + amp, x2, y - amp);
        }
    }

    private void inverse() {
        for (int i = 0, size = avgSize - 1; i < size; i += barStep) {
            float x1 = map(i, 0, size, 0, width);
            float x2 = map(i + 1, 0, size, 0, width);

            float x3 = map(i, 0, size, width, 0);
            float x4 = map(i + 1, 0, size, width, 0);

            float amp = maxDrawHeight * (((amplitude * fftSmooth[i]) - minVal) / spectrumScaleFactor);
            float y = (height / 2 - 20);

            float c = map(i, 0, fft.specSize(), 0, 275);
            stroke(c, 255, 255);
            strokeWeight(strokeWeight);
            line(x1, y, x2, y + amp);
            line(x3, y, x4, y - amp);
        }
    }

    public void functionMusic() {
        for (int i = 0; i < avgSize - 1; i += barStep) {
            float amp = maxDrawHeight * (((amplitude * fftSmooth[i]) - minVal) / spectrumScaleFactor);

            float x1 = map(i, 0, avgSize, 0, width);
            float x2 = map(i + 1, 0, avgSize, 0, width);
            float y1 = -musicTransform(x1) + height / 2;
            float y2 = -musicTransform(x2) + height / 2;

            float c1 = map(i, 0, fft.specSize(), 0, 275);
            stroke(c1, 255, 255);
            strokeWeight(strokeWeight);
            line(1.7f * x1, y1 + amp, 1.7f * x2, y2 - amp);
        }
        a += 0.015f;
    }

    public float musicTransform(float x) {
        switch (transformMode) {
            case 0:
                return (float) (75f * Math.sin(.015 * x + a));
            case 1:
                float m = 2, a = 325;
                return (float) ((m * (x - a) * Math.signum(a - x) + m * a) - height / 2 + (60 * Math.sin(.08f * x + Visualizer.a)));
            case 2:
                return 20f * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(.04f * x))))))));
            case 3:
                return (float) Math.pow(.02 * x, 2);
            default:
                return (float) (75f * Math.sin(.015 * x + Visualizer.a));
        }
         // case 1 - Triangle Function: http://math.stackexchange.com/questions/544559/is-there-any-equation-for-triangle
    }

    private void showSongTime() {
        try {
            title = nameOfSong + " - " + new SimpleDateFormat("mm:ss").format(new Date(player.position()));
            surface.setTitle(title);
            float posx = map(player.position(), 0, player.length(), 0, width);
            noStroke();
            colorMode(HSB);
            fill(map(player.position(), 0, player.length(), 0, 255), 255, 255);
            rect(0, 0, posx, 12);
        } catch (Exception e) {EException.append(e);}
    }

    public void mouseReleased() {
        try {
            if (mouseY >= 0 && mouseY <= 12) player.cue((int) map(mouseX, 0, width, 0, player.length()));
        } catch (Exception e) {EException.append(e);}
    }

    public static String fileChooser() {
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception x) {EException.append(x);}
        String[] acceptedTypes = {".mp3", ".wav", ".aiff", ".au"};
        FileDialog dialog = new FileDialog((Frame) null, "Select Song :D", FileDialog.LOAD);
        dialog.setFilenameFilter((dir, file) -> isSongCorrectFormat(file, acceptedTypes));
        dialog.setFile("*.mp3; *.wav; *.aiff; *.au");
        dialog.setVisible(true);

        File choice = null;
        String dir = dialog.getDirectory();
        String file = dialog.getFile();

        if (dir != null && file != null) choice = new File(dir, file);

        if (choice != null) {
            //System.out.println(choice.getAbsolutePath());
            String fileExt = FilenameUtils.getExtension(choice.getName());
            StringBuilder t = new StringBuilder(choice.getName());
            nameOfSong = t.delete(t.indexOf("." + fileExt), t.length()).toString();
            //System.out.println(nameOfSong);
            return choice.getAbsolutePath();
        }
        else return null;
    }

    public void initSoundCloud() throws IOException {
        soundCloudApi = new ISoundCloud("h4xVW8Xx30tXHqgTtfUxiXFk2XpTWI8I",
                                           "8tzbr1Q0fFPOre68l9NhAwAXHqTrJO1M",
                                     "scloudv1", SCPack.get(this.getClass(), "/k.mvf", "/l.mvf"));
    }

    private void showBackGround(int step) {
        for (int x = step; x < width; x += step) {
            for (int y = step; y < height; y += step) {
                float n = noise(x * 0.005f, y * 0.007f, frameCount * 0.02f);
                pushMatrix();
                translate(x, y);
                rotate(TWO_PI * n);
                scale(map(ellipseR, 0, circleRadius * 1.85f - 30, 0, 15) * n);
                strokeWeight(.2f);
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

    public void changeTransform(){
        transformMode++;
        if (transformMode > 3) transformMode = 0;
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
        String newSong = fileChooser();
        if (newSong != null) {
            player.pause();
            player.close();
            player = minim.loadFile(newSong, bufferSize);

            fft = new FFT(player.bufferSize(), player.sampleRate());
            fft.logAverages(minBandwidth, bandsPerOctave);
            fft.window(FFT.GAUSS);

            player.play();
        }
    }

    public void changeSong(Track song){
        try{
            if (song != null && song.isStreamable()) {
                player.pause();
                player.close();
                player = minim.loadFile(song.getStreamUrl(), bufferSize);
                nameOfSong = song.getTitle();

                fft = new FFT(player.bufferSize(), player.sampleRate());
                fft.logAverages(minBandwidth, bandsPerOctave);
                fft.window(FFT.GAUSS);

                player.play();
            }
            else showError("Song is not Streamable :( Try another song.", "Non Streamable Link");
        } catch (Exception e){EException.append(e);}
    }

    public static boolean isSongCorrectFormat(String file, String[] extensions){
        for (String extension : extensions) {
            if (file.endsWith(extension)) return true;
        }
        return false;
    }

    public void showError(String message, String title){
        JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
    }

    public void keyReleased() {
        super.keyReleased();

        if (key == 'c') {
            changeTransform();
        }

        if (key == 'm') {
            mutePlayer();
        }

        if (key == 'x') {
            changeVisual();
        }

        if (key == 'z') EException.getInstance(null);

        if (keyCode == KeyEvent.VK_SPACE) {
            pausePlayer();
        }

        if (key == 'v') {
            changeSong();
        }
    }
}