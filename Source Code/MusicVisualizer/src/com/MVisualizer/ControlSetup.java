package com.MVisualizer;

import controlP5.*;
import processing.core.PFont;
import java.awt.*;
import static controlP5.ControlP5Constants.CENTER;
import static com.MVisualizer.Visualizer.*;

public class ControlSetup {
    public static Visualizer ref;
    public static PFont sliderfont = new PFont(new Font("Times", Font.PLAIN, 14), true);
    public static PFont buttonfont = new PFont(new Font("Times", Font.PLAIN, 13), true);

    public static void setupControls(){
        int h = 50;
        int spacing = 30;

        // group number 1
        Group g1 = cp5.addGroup("controls").setHeight(25);
        g1.getCaptionLabel().setFont(sliderfont);
        g1.getCaptionLabel().align(CENTER, CENTER);

        createSlider(g1, "smoothing", 0, 0, 30, 200, 15, 0.5f, 1.0f,
                () -> smoothing = mySliders[0].getValue(), smoothing, Slider.FLEXIBLE, sliderfont);

        createSlider(g1, "amplitude", 1, 0, h + spacing, 200, 15, 1, 50,
                () -> amplitude = mySliders[1].getValue(), amplitude, Slider.FLEXIBLE, sliderfont);

        createSlider(g1, "bass-kick", 2, 0, h * 2 + spacing, 200, 15, 0.1f, 50,
                () -> bassKick = mySliders[2].getValue(), bassKick, Slider.FLEXIBLE, sliderfont);

        createSlider(g1, "radius", 3, 0, h * 3 + spacing, 200, 15, 50, 500,
                () -> r = mySliders[3].getValue(), r, Slider.FLEXIBLE, sliderfont);

        createSlider(g1, "bar-step", 4, 0, h * 4 + spacing, 200, 15, 1, 15,
                () -> barStep = (int) mySliders[4].getValue(), barStep, Slider.FLEXIBLE, sliderfont);

        createSlider(g1, "stroke-weight", 5, 0, h * 5 + spacing, 200, 15, 1, 35,
                () -> strokeWeight = mySliders[5].getValue(), strokeWeight, Slider.FLEXIBLE, sliderfont);

        createSlider(g1, "angle-offset", 6, 0, h * 6 + spacing, 200, 15, 1, 250,
                () -> angOffset = mySliders[6].getValue(), angOffset, Slider.FLEXIBLE, sliderfont);
        mySliders[6].setScrollSensitivity(.004f);

        createSlider(g1, "background-step", 7, 0, h * 7 + spacing, 200, 15, 10, 120,
                () -> backgroundstep = (int) mySliders[7].getValue(), backgroundstep, Slider.FLEXIBLE, sliderfont);

        createButton(g1, "change song", 0, 0, h * 8 + spacing, 85, 15, new IControlEvent(e -> ref.changeSong()), buttonfont);
        createButton(g1, "change visual", 1, 85 + 30, h * 8 + spacing, 85, 15, new IControlEvent(e -> ref.changeVisual()), buttonfont);
        createButton(g1, "pause song", 2, 0, h * 9 + spacing, 85, 15, new IControlEvent(e -> {
            ref.pausePlayer();
            if (isPaused) myButtons[2].setCaptionLabel("play song");
            else myButtons[2].setCaptionLabel("pause song");
        }), buttonfont);

        createButton(g1, "mute song", 3, 85 + 30, h * 9 + spacing, 85, 15, new IControlEvent(e -> {
            ref.mutePlayer();
            if (isMute) myButtons[3].setCaptionLabel("unmute song");
            else myButtons[3].setCaptionLabel("mute song");
        }), buttonfont);

        accordion = cp5.addAccordion("acc")
                .setPosition(5, 25)
                .setWidth(200)
                .addItem(g1)
                .setCollapseMode(Accordion.SINGLE)
                .open(0);
    }

    private static void createSlider(Group group, String name, int index, float locx, float locy, int sizex, int sizey, float rangex,
                                     float rangey, Function behavior, float initialval, int slidermode, PFont font){
        // add a vertical slider
        mySliders[index] = cp5.addSlider(name)
                .setPosition(locx, locy)
                .setSize(sizex, sizey)
                .setRange(rangex, rangey)
                .setBehavior(new IControlBehavior(behavior))
                .setValue(initialval)
                .setSliderMode(slidermode)
                .moveTo(group)
        ;

        // reposition the Label
        cp5.getController(name).getValueLabel().setFont(font);
        cp5.getController(name).getValueLabel().align(ControlP5.RIGHT, ControlP5.TOP_OUTSIDE).setPaddingX(0);
        cp5.getController(name).getCaptionLabel().setFont(font);
        cp5.getController(name).getCaptionLabel().align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).setPaddingX(0);
    }

    private static void createButton(Group group, String name, int index, float locx, float locy, int sizex, int sizey, IControlEvent function, PFont font){
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
