package com.MVisualizer;

import controlP5.ControlP5;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import drop.*;
import org.apache.commons.io.FilenameUtils;
import processing.core.PApplet;
import processing.event.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.MVisualizer.MConstants.*;
import static java.lang.Math.signum;

/**
 * Music Visualizer (SoundCloud integration removed due to instability)
 * Author: Caleb Bostic-Gardner
 * Copyright (2016-2017)
 */

public class Visualizer extends PApplet {
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

        dragNDrop = new SDrop(this);

        cp5 = new ControlP5(this);
        Controls.setupControls();
    }

    public void settings() {
        minim = new Minim(this);
        Controls.visualizerRef = this;

        String song = fileChooser();
        song = song == null ? "placeholder.mp3" : song;
        changeSong(song, true);

        size(1080, 720, JAVA2D);
    }

    void changeSong(String songPath, boolean isSetup){
        try {
            String songFile = songPath == null ? fileChooser() : songPath;

            if (songFile != null) {
                if (!isSetup) player.close();

                player = minim.loadFile(songFile, bufferSize);

                fft = new FFT(player.bufferSize(), player.sampleRate());
                fft.logAverages(minBandwidth, bandsPerOctave);
                fft.window(FFT.GAUSS);

                if (isSetup){
                    avgSize = fft.avgSize()-fftOffset;
                    fftSmooth = new float[avgSize];
                }

                if (audioInput != null) audioInput = null;

                isAudioSourceMic_Desktop = false;

                player.play();
            }
        }catch (Exception e){EException.append(e);}
    }

    void changeAudioInput(boolean isSetup){
        try {
            if (!isSetup) player.close();

            audioInput = minim.getLineIn(Minim.STEREO, bufferSize);

            fft = new FFT(audioInput.bufferSize(), audioInput.sampleRate());
            fft.logAverages(minBandwidth, bandsPerOctave);
            fft.window(FFT.GAUSS);

            if (isSetup){
                avgSize = fft.avgSize()-fftOffset;
                fftSmooth = new float[avgSize];
            }

            isAudioSourceMic_Desktop = true;

        }catch (Exception e){EException.append(e);}
    }

    public void stop() {
        try{
            player.close();
            minim.stop();
            super.stop();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            System.exit(-1);
        }
    }

    public void draw() {
        if (isAudioSourceMic_Desktop && audioInput != null) fft.forward(audioInput.mix);
        else fft.forward(player.mix);

        background(0);
        if (drawBackground) {
            stroke(255);
            showBackGround(backgroundStep);
        }
        showSongTime();

        smoothPulse();
        visualize();

        //EException.setText(isSongOver()+"");
    }

//    public boolean isSongOver() {return (!isPaused && (player.right.level() <= 0 && player.left.level() <= 0));}
//    public void songEnded() {
//        if (player != null) EException.setText(player.position() + " (" + player.left.level() + " - " + player.right.level() + ") " + player.isPlaying());
//    }

    private void visualize() {
        cAmplitude = lerp(cAmplitude, smoothing * fft.calcAvg(minVal, maxVal), .1f);
        ellipseR = constrain(cAmplitude * bassKick, 0.1f, circleRadius * 65.0f);
        maxDrawHeight = (height / 2) * 0.75f;
        spectrumScaleFactor = (maxVal - minVal) + 0.00001f;

        switch (visualMode) {
            case 0: circularPulse(); break;
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

    private void circularPulse() {
        for (int i = 0; i < avgSize; i += barStep) {
            float angle = i * angOffset * 2 * PI / avgSize;
            float fftSmoothDisplay = maxDrawHeight * (((amplitude * fftSmooth[i]) - minVal) / spectrumScaleFactor);
            float rad = circleRadius * 2;
            float x = rad * cos(angle);
            float y = rad * sin(angle);
            float x2 = (rad + fftSmoothDisplay) * cos(angle) + .85f;
            float y2 = (rad + fftSmoothDisplay) * sin(angle) + .85f;

            //int c = (int) map(i, 0, fft.specSize(), 0, 470);
            float c = map(i, 0, avgSize, 0, 275);
            
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

            float c = map(i, 0, avgSize-1, 0, 275);

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

            float c = map(i, 0, avgSize-1, 0, 275);

            stroke(c, 255, 255);
            strokeWeight(strokeWeight);
            line(x1, y, x2, y + amp);
            line(x3, y, x4, y - amp);
        }
    }

    private void functionMusic() {
        for (int i = 0; i < avgSize - 1; i += barStep) {
            float amp = maxDrawHeight * (((amplitude * fftSmooth[i]) - minVal) / spectrumScaleFactor);

            float x1 = map(i, 0, avgSize, 0, width);
            float x2 = map(i + 1, 0, avgSize, 0, width);
            float y1 = -musicTransform(x1) + height / 2;
            float y2 = -musicTransform(x2) + height / 2;

            float c1 = map(i, 0, avgSize-1, 0, 255);

            stroke(c1, 255, 255);
            strokeWeight(strokeWeight);
            line(1.7f * x1, y1 + amp, 1.7f * x2, y2 - amp);
        }
        angle += 0.015f;
    }

    private float musicTransform(float x) {
        switch (transformMode) {
            case 0:
                return 75f * sin(.015f * x + angle);
            case 1:
                float m = 2, b = 325;
                return m * (x - b) * signum(b - x) + m * b - height / 2 + 60 * sin(.08f * x + angle);
            case 2:
                return 50f * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(2 * sin(.04f * x))))))));
            case 3:
                return pow(.02f * x, 2);
            default:
                return 75f * sin(.015f * x + angle);
        }
         // case 1 - Triangle Function: http://math.stackexchange.com/questions/544559/is-there-any-equation-for-triangle
    }

    private void showSongTime() {
        try {
            if (isAudioSourceMic_Desktop){
                title = "Visualizing from Microphone";
                surface.setTitle(title);
            }
            else{
                title = nameOfSong + " - " + new SimpleDateFormat("mm:ss").format(new Date(player.position()));
                surface.setTitle(title);

                float posx = map(player.position(), 0, player.length(), 0, width);
                noStroke();
                colorMode(HSB);

                fill(map(player.position(), 0, player.length(), 0, 275), 255, 255);
                rect(0, 0, posx, 12);
            }
        } catch (Exception e) {EException.append(e);}
    }

    public void mouseClicked(MouseEvent event) {
        super.mouseClicked(event);
        try {
            if (!isAudioSourceMic_Desktop)
                if (mouseY >= 0 && mouseY <= 12)
                    player.cue((int) map(mouseX, 0, width, 0, player.length()));
        }
        catch (Exception e) {EException.append(e);}
    }

    private static String fileChooser() {
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception x) {EException.append(x);}
        String[] acceptedTypes = {".mp3", ".wav", ".aiff", ".au"};
        FileDialog dialog = new FileDialog((Dialog) null, "Select Song :D", FileDialog.LOAD);
        dialog.setFilenameFilter((dir, file) -> isSongCorrectFormat(file, acceptedTypes));
        dialog.setFile("*.mp3; *.wav; *.aiff; *.au");
        dialog.setVisible(true);

        File choice = null;
        String dir = dialog.getDirectory();
        String file = dialog.getFile();

        if (dir != null && file != null) choice = new File(dir, file);

        if (choice != null) {
            //System.out.println(choice.getAbsolutePath());

            nameOfSong = FilenameUtils.getBaseName(choice.getName());

            //System.out.println(nameOfSong);
            return choice.getAbsolutePath();
        }
        else return null;
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

    void changeVisual(){
        visualMode++;
        if (visualMode > 3) visualMode = 0;
    }

    void changeTransform(){
        transformMode++;
        if (transformMode > 3) transformMode = 0;
    }

    void pausePlayer(){
        if (player.isPlaying()) {
            player.pause();
            isPaused = true;
        }
        else {
            player.play();
            isPaused = false;
        }
    }

    void mutePlayer(){
        if (player.isMuted()) {
            player.unmute();
            isMute = false;
        }
        else {
            player.mute();
            isMute = true;
        }
    }

    private static boolean isSongCorrectFormat(String file, String[] extensions){
        for (String extension : extensions)
            if (file.endsWith(extension))
                return true;

        return false;
    }

//    public void showError(String message, String title){
//        JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
//    }

    void dropEvent(DropEvent dropContent) {
        if(dropContent.isFile()) changeSong(dropContent.filePath(), false);
    }

    public void keyReleased(processing.event.KeyEvent event) {
        super.keyReleased(event);

        if (key == 'q') SwingUtilities.invokeLater(InstructionsWindow::getInstance);

        if (key == 'c') changeTransform();

        if (key == 'm') mutePlayer();

        if (key == 's') drawBackground = !drawBackground;

        if (key == 'x') changeVisual();

        if (key == 'z') EException.getInstance(null);

        if (keyCode == KeyEvent.VK_SPACE) pausePlayer();

        if (key == 'v') changeSong(null, false);

        if (event.isShiftDown() && event.isControlDown() && keyCode == KeyEvent.VK_Q) System.exit(0);
    }
}