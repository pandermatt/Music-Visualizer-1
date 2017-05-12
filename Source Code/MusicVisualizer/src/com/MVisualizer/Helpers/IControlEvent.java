package com.MVisualizer.Helpers;

import controlP5.ControlEvent;
import controlP5.ControlListener;

import java.util.function.Consumer;

public class IControlEvent implements ControlListener {
    private Consumer<ControlEvent> function;

    public IControlEvent(Consumer<ControlEvent> function) {
        this.function = function;
    }

    public void controlEvent(ControlEvent controlEvent) {
        function.accept(controlEvent);
    }
}
