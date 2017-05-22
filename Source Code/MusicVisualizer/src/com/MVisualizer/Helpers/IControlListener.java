package com.MVisualizer.Helpers;

import controlP5.ControlEvent;
import controlP5.ControlListener;

@FunctionalInterface
public interface IControlListener extends ControlListener{
    void controlEvent(ControlEvent controlEvent);
}
