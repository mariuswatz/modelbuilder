/**
 * USimpleGUI.pde - Marius Watz, 2012
 * http://workshop.evolutionzone.com
 * 
 * Demonstrates UColorTool in the context of a
 * randomized visual composition.
 */

import controlP5.*;
import unlekker.util.*;

public USimpleGUI gui;

public void setup() {
  size(320, 300);
  gui=new USimpleGUI(this);
  //		gui.layoutVertical=false;

  gui.addButton("switchLayout");
  gui.addButton("test02");
  gui.addButton("goOnCallSantaClaus");
  gui.newRow();

  gui.addRadioButton(
  "rb", new String[] {
    "abc", "b", "c"
  }
  , 50);
  gui.addDropDown("drop", new String[] {
    "abc", "b", "c"
  }
  , 50);
  gui.addTextField("dimensions", 100, 20);
  gui.addToggle("toggle", true);
  gui.newRow();

  gui.addSlider("slider", 0, 0, 100);
  gui.addSlider("sliderlyHeaven", 0, 0, 100);
  gui.setLayout(false);
}

public void switchLayout() {
  gui.setLayout(!gui.layoutVertical);
  gui.setText("dimensions", "WxH : "+(int)gui.cpw+" x "+(int)gui.cph+" ");
}

public void draw() {
  background(0);

  gui.draw();
}

