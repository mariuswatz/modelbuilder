// unlekker.modelbuilder example
// Marius Watz, 2011
// http://workshop.evolutionzone.com
//
// Shared under Creative Commons "share-alike non-commercial use 
// only" license.

// Advanced parametric form example with GUI created using the
// ControlP5 library by Andreas Schlegel. The library can be
// downloaded here: http://sojamo.de/libraries/controlP5/

import controlP5.*;
import processing.opengl.*;

import unlekker.util.*;
import unlekker.modelbuilder.*;

int num=20,numSeg=20;
int numSeg2=numSeg*2;
float totalH=100, maxRad=50,minRad=50;

boolean doRebuild=false;
UNav3D nav;

CylPanel panel[];
UGeometry model;

void setup() {
  size(600,600, OPENGL);

  initGUI();
  initShaper();
  rndForm();
}

void draw() {
  background(100);

  hint(ENABLE_DEPTH_TEST);
  pushMatrix();
  lights();
  nav.doTransforms();

  fill(255,100,0);
  stroke(0);
  model.draw(this);
  popMatrix();
  
  if(doRebuild) {
    buildModel();
    doRebuild=false;
  }
  
  hint(DISABLE_DEPTH_TEST);
}

public void mouseDragged() {
  if(controlP5.window(this).isMouseOver()) return;
  nav.mouseDragged();
}
	
public void keyPressed() {
  nav.keyPressed();
}
