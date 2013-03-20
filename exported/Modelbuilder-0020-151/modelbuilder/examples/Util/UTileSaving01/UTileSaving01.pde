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
 *
 * To set up the tiles UTileSaver needs to modify the camera 
 * view, so if you use PeasyCam or have custom camera views 
 * in your scene it will probably not work with UTileSaver.
 * That scenario has not been tested.
 *
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

float rotX,rotY;

boolean doRender=false,doUpdate=true;
public UTileSaver tiler;
int tiles=4,meshRes=6;

public void setup() {  
  // NOTE: UTileSaver current;y only works properly with square 
  // aspect ratios. Hopefully this will be fixed soon.
  size(600,600,P3D);
  
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
  // take care of pre-render tasks
  if(doRender) renderStart();
  update();
  
  background(0);
//  lights();
ambient(50);
  ambientLight(100,100,100, 1,0,-1);
  directionalLight(255,255,255, -1,0,-1);

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
}

