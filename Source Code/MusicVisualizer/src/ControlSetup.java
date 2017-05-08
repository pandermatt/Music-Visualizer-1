import controlP5.Accordion;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Slider;
import processing.core.PFont;
import java.awt.*;
import static controlP5.ControlP5Constants.CENTER;

public class ControlSetup {
    public static PFont sliderfont = new PFont(new Font("Times", Font.PLAIN, 14), true);

    public static void setupSliders(){
        int h = 50;
        int spacing = 10;

        // group number 1
        Group g1 = Visualizer.cp5.addGroup("controls").setHeight(25);
        g1.getCaptionLabel().setFont(sliderfont);
        g1.getCaptionLabel().align(CENTER, CENTER);

        createSlider(g1,"smoothing", 0, 0, 10, 200, 15, 0.5f, 1.0f,
                () -> Visualizer.smoothing = Visualizer.mySliders[0].getValue(),
                Visualizer.smoothing, Slider.FLEXIBLE, sliderfont);

        createSlider(g1,"amplitude", 1, 0, h + spacing, 200, 15, 1, 50,
                () -> Visualizer.amplitude = Visualizer.mySliders[1].getValue(),
                Visualizer.amplitude, Slider.FLEXIBLE, sliderfont);

        createSlider(g1,"bass-kick", 2, 0, h * 2 + spacing, 200, 15, 0.1f, 50,
                () -> Visualizer.bassKick = Visualizer.mySliders[2].getValue(),
                Visualizer.bassKick, Slider.FLEXIBLE, sliderfont);

        createSlider(g1,"radius", 3, 0, h * 3 + spacing, 200, 15, 50, 500,
                () -> Visualizer.r = Visualizer.mySliders[3].getValue(),
                Visualizer.r, Slider.FLEXIBLE, sliderfont);

        createSlider(g1,"bar-step", 4, 0, h * 4 + spacing, 200, 15, 1, 15,
                () -> Visualizer.barStep = (int) Visualizer.mySliders[4].getValue(),
                Visualizer.barStep, Slider.FLEXIBLE, sliderfont);

        createSlider(g1,"stroke-weight", 5, 0, h * 5 + spacing, 200, 15, 1, 35,
                () -> Visualizer.strokeWeight = Visualizer.mySliders[5].getValue(),
                Visualizer.strokeWeight, Slider.FLEXIBLE, sliderfont);

        createSlider(g1,"angle-offset", 6, 0, h * 6 + spacing, 200, 15, 1, 250,
                () -> Visualizer.angOffset = Visualizer.mySliders[6].getValue(),
                Visualizer.angOffset, Slider.FLEXIBLE, sliderfont);
        Visualizer.mySliders[6].setScrollSensitivity(.004f);

        Visualizer.accordion = Visualizer.cp5.addAccordion("acc")
                .setPosition(5, 25)
                .setWidth(200)
                .setSize(200, 50)
                .setCollapseMode(Accordion.SINGLE)
                .addItem(g1)
                .open(0);

        Visualizer.accordion.getCaptionLabel().setFont(ControlSetup.sliderfont);
        Visualizer.accordion.setCollapseMode(Accordion.MULTI);
    }

    private static void createSlider(Group group, String name, int index, float locx, float locy, int sizex, int sizey, float rangex,
                                     float rangey, Function behavior, float initialval, int slidermode, PFont font){
        // add a vertical slider
        Visualizer.mySliders[index] = Visualizer.cp5.addSlider(name)
                .setPosition(locx, locy)
                .setSize(sizex, sizey)
                .setRange(rangex, rangey)
                .setBehavior(new IControlBehavior(behavior))
                .setValue(initialval)
                .setSliderMode(slidermode)
                .moveTo(group)
        ;

        // reposition the Label
        Visualizer.cp5.getController(name).getValueLabel().setFont(font);
        Visualizer.cp5.getController(name).getValueLabel().align(ControlP5.RIGHT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
        Visualizer.cp5.getController(name).getCaptionLabel().setFont(font);
        Visualizer.cp5.getController(name).getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
    }
}
