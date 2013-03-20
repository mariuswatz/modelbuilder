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
float totalH=300, maxRad=50,minRad=50,R=maxRad;

boolean doRebuild=false;
UNav3D nav;

CylPanel panel[];
UGeometry model;

void setup() {
  size(600,600, OPENGL);

  smooth(8);
  initGUI();
  initShaper();
  rndForm();
}

void draw() {
  background(50);

  hint(ENABLE_DEPTH_TEST);
  pushMatrix();
  lights();
  ambientLight(50,50,50, -1,1,1);
  nav.doTransforms();

  fill(255);
//  stroke(0);
  model.draw(this);
  popMatrix();
  
  if(doRebuild) {
    buildModel();
    doRebuild=false;
  }
  
  // draw GUI
  hint(DISABLE_DEPTH_TEST);
  gui.draw();
}

	

