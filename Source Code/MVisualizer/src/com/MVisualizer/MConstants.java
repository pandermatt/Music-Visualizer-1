package com.MVisualizer;

import controlP5.ControlP5;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import drop.SDrop;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

abstract class MConstants {
    static AudioInput audioInput;
    static FFT fft;
    static Minim minim;
    static AudioPlayer player;
    static ControlP5 cp5;
    static SDrop dragNDrop;

    //  Strings
    static final String mvInstructions = getHtml("/MVInstructions.html");
    static String iconImage = "logo3.png";
    static String nameOfSong = "";
    static String title = "";
    static Image icon = Toolkit.getDefaultToolkit().getImage(Visualizer.class.getResource("/" + iconImage));

    //  Arrays
    static float[] fftSmooth;

    //  Ints
    static int avgSize = 0;
    static int fftOffset = 120;
    static int barStep = 1;
    static int backgroundStep = 50;
    static int bufferSize = 2048;

    //    public static short bufferSize = 512*2;
    static int minBandwidth = 2000;
    static int bandsPerOctave = 300;
    static int transformMode = 0;
    static int visualMode = 0;

    //  Floats
    static float minVal = 0.0f;
    static float maxVal = 0.0f;
    static float smoothing = 0.80f;
    static float cAmplitude = 90;
    static float ellipseR = 0;
    static float bassKick = 2.5f;
    static float circleRadius = 145.0f;
    static float strokeWeight = 2.25f;
    static float amplitude = 1.5f;
    static float angOffset = 1.4f;
    static float angle = 0.0f;

    static float maxDrawHeight = 1.0f;
    static float spectrumScaleFactor = 1.0f;

    //  Booleans
    static boolean isPaused = false;
    static boolean isMute = false;
    static boolean drawBackground = true;
    static boolean isAudioSourceMic_Desktop = false;

    private static String getHtml(String file){
        try (BufferedReader br = new BufferedReader(new InputStreamReader(MConstants.class.getResourceAsStream(file)))){
            StringBuilder lines = new StringBuilder(1024);
            String line;
            while ((line = br.readLine()) != null) lines.append(line);
            return lines.toString();
        }
        catch (Exception e){EException.append(e);}
        return null;
    }
}
