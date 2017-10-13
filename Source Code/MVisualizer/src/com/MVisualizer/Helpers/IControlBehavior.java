package com.MVisualizer.Helpers;

import controlP5.ControlBehavior;

public class IControlBehavior extends ControlBehavior {
    private Function function;

    public IControlBehavior(Function function) {
        this.function = function;
    }

    public void update() {
        function.apply();
    }
}
