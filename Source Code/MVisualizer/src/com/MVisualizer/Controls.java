package com.MVisualizer;

import com.MVisualizer.Helpers.Function;
import com.MVisualizer.Helpers.IControlBehavior;
import com.MVisualizer.Helpers.IControlListener;
import controlP5.*;
import controlP5.Button;
import processing.core.PFont;

import javax.swing.*;
import java.awt.*;
import static com.MVisualizer.MConstants.*;
import static controlP5.ControlP5Constants.CENTER;

public class Controls {
    public static Visualizer visualizerRef;
    private static PFont sliderfont = new PFont(new Font("Times", Font.PLAIN, 14), true);
    private static PFont buttonfont = new PFont(new Font("Times", Font.PLAIN, 13), true);
    public static Slider[] mySliders = new Slider[10];
    public static Button[] myButtons = new Button[10];
    public static Accordion accordion;

    public static void setupControls(){
        int h = 50;
        int spacing = 30;

        // group number 1
        Group g1 = cp5.addGroup("controls (press q for help)").setHeight(25);
        g1.getCaptionLabel().setFont(sliderfont);
        g1.getCaptionLabel().align(CENTER, CENTER);

        createSlider(0.04f, g1, "smoothing", 0, 0, 30, 200, 15, 0.5f, 1.0f,
                () -> smoothing = mySliders[0].getValue(), smoothing, Slider.FLEXIBLE, sliderfont);

        createSlider(0.02f, g1, "amplitude", 1, 0, h + spacing, 200, 15, 1, 120,
                () -> amplitude = mySliders[1].getValue(), amplitude, Slider.FLEXIBLE, sliderfont);

        createSlider(0.05f, g1, "bass-kick", 2, 0, h * 2 + spacing, 200, 15, 0.1f, 120,
                () -> bassKick = mySliders[2].getValue(), bassKick, Slider.FLEXIBLE, sliderfont);

        createSlider(0.04f, g1, "radius", 3, 0, h * 3 + spacing, 200, 15, 50, 500,
                () -> circleRadius = mySliders[3].getValue(), circleRadius, Slider.FLEXIBLE, sliderfont);

        createSlider(0.3f, g1, "bar-step", 4, 0, h * 4 + spacing, 200, 15, 1, 30,
                () -> barStep = (int) mySliders[4].getValue(), barStep, Slider.FLEXIBLE, sliderfont);

        createSlider(0.03f, g1, "stroke-weight", 5, 0, h * 5 + spacing, 200, 15, 1, 60,
                () -> strokeWeight = mySliders[5].getValue(), strokeWeight, Slider.FLEXIBLE, sliderfont);

        createSlider(0.004f, g1, "angle-offset", 6, 0, h * 6 + spacing, 200, 15, 1, 250,
                () -> angOffset = mySliders[6].getValue(), angOffset, Slider.FLEXIBLE, sliderfont);
        //mySliders[6].setScrollSensitivity(.004f);

        createSlider(0.05f, g1, "background-step", 7, 0, h * 7 + spacing, 200, 15, 10, 180,
                () -> backgroundStep = (int) mySliders[7].getValue(), backgroundStep, Slider.FLEXIBLE, sliderfont);

        createButton(g1, "change song", 0, 0, h * 8 + spacing, 85, 15, e -> visualizerRef.changeSong(null,false), buttonfont);
        createButton(g1, "next visual", 1, 85 + 30, h * 8 + spacing, 85, 15, e -> visualizerRef.changeVisual(), buttonfont);
        createButton(g1, "pause song", 2, 0, h * 9 + spacing, 85, 15, e -> {
            visualizerRef.pausePlayer();
            if (isPaused) myButtons[2].setCaptionLabel("play song");
            else myButtons[2].setCaptionLabel("pause song");
        }, buttonfont);

        createButton(g1, "mute song", 3, 85 + 30, h * 9 + spacing, 85, 15, e -> {
            visualizerRef.mutePlayer();
            if (isMute) myButtons[3].setCaptionLabel("unmute song");
            else myButtons[3].setCaptionLabel("mute song");
        }, buttonfont);

        createButton(g1, "Use Microphone Input", 4, 0, h * 10 + spacing, 200, 15, e -> {
            if (MConstants.audioInput == null) visualizerRef.changeAudioInput(false);
        }, buttonfont);

        accordion = cp5.addAccordion("acc")
                .setPosition(5, 25)
                .setWidth(250)
                .addItem(g1)
                .setMoveable(true)
                .setCollapseMode(Accordion.SINGLE)
                .open();
    }

    private static void createSlider(float scrollSense, Group group, String name, int index, float locx, float locy, int sizex, int sizey, float rangex,
                                     float rangey, Function behavior, float initialval, int slidermode, PFont font){
        // add a vertical slider
        mySliders[index] = cp5.addSlider(name)
                .setPosition(locx, locy)
                .setSize(sizex, sizey)
                .setRange(rangex, rangey)
                .setBehavior(new IControlBehavior(behavior))
                .setValue(initialval)
                .setScrollSensitivity(scrollSense)
                .setSliderMode(slidermode)
                .moveTo(group)
        ;

        // reposition the Label
        cp5.getController(name).getValueLabel().setFont(font);
        cp5.getController(name).getValueLabel().align(ControlP5.RIGHT, ControlP5.TOP_OUTSIDE).setPaddingX(0);
        cp5.getController(name).getCaptionLabel().setFont(font);
        cp5.getController(name).getCaptionLabel().align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).setPaddingX(0);
    }

    private static void createButton(Group group, String name, int index, float locx, float locy, int sizex, int sizey, IControlListener function, PFont font){
        // add a button
        myButtons[index] = cp5.addButton(name)
                .setPosition(locx, locy)
                .setSize(sizex, sizey)
                .addListener(function)
                .moveTo(group);

        // reposition the Label
        cp5.getController(name).getValueLabel().setFont(font);
        cp5.getController(name).getValueLabel().align(ControlP5.RIGHT, ControlP5.TOP_OUTSIDE).setPaddingX(0);
        cp5.getController(name).getCaptionLabel().setFont(font);
        cp5.getController(name).getCaptionLabel().align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).setPaddingX(0);
    }
}
