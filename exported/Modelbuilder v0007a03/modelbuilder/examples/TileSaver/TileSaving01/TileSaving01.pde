/**
 * TileSaving01.pde - Marius Watz, 2012
 * http://workshop.evolutionzone.com
 * 
 * Demonstrates use of UTileSaver to render hi-res
 * images from OpenGL sketches. Currently 
 * square aspect ratios (widht==height) are supported.
 * Final resolution is width*tiles x height*tiles
 *
 * For tile saving to work the image drawn must be static
 * while tiling, see the update() method for an example of
 * how to toggle updating of the sketch.
 */
 
 import unlekker.util.*;
import unlekker.modelbuilder.*;
import unlekker.modelbuilder.filter.*;
import ec.util.*;

import controlP5.*;
import processing.opengl.*;

UNav3D nav;
USimpleGUI gui;
UGeometry obj[];

float rotX,rotY,zoomZ;

boolean doRender=false,doUpdate=true;
public UTileSaver tiler;
int tiles=4,meshRes=6;

public void setup() {  
  // NOTE: UTileSaver current;y only works properly with square 
  // aspect ratios. Hopefully this will be fixed soon.
  size(600,600,OPENGL);
  
  nav=new UNav3D(this);
  nav.setTranslation(width/2,height/2,0);
  
  gui=new USimpleGUI(this);
  gui.addToggle("doUpdate", doUpdate);
  gui.addSlider("tiles", tiles, 2, 10);
  gui.addSlider("meshRes", meshRes, 4, 30);
  gui.addButton("render");
  gui.addButton("build");
  gui.setLayout(false);  
  nav.setGUI(gui);

  build();
}

public void draw() {
  if(doRender) renderStart();
  update();
  
  background(0);
  lights();

  pushMatrix();
  nav.doTransforms();    
  drawObjects();    
  popMatrix();
  
  if(doRender) renderEnd();

  gui.draw();
}

// For tile saving to work correctly you need to separate drawing 
// and updating logic, so that updating can be disabled during 
// tiling. Below "doUpdate" is used to toggle updating.
void update() {
  if(!doUpdate) return;
  
  rotX+=radians(1);
  rotY+=radians(0.5f);
  
  // silly trick to zoom in and out using sin() to
  // interpolate, using frameCount as input
  zoomZ=300*sin(map(frameCount%300,0,299, 0, TWO_PI));
}

