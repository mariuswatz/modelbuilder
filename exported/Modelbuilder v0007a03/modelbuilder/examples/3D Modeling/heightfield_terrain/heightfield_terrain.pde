import unlekker.util.*;
import unlekker.modelbuilder.*;
import ec.util.*;

// modelab.nu Processing Tutorial - Marius Watz, 2010
// http://modelab.nu/?p=4147 / http://workshop.evolutionzone.com
//
// Shared under Creative Commons "share-alike non-commercial use 
// only" license.

import controlP5.*;
import processing.opengl.*;
import java.awt.event.*;

ControlP5 controlP5; // instance of the controlP5 library

int slGridResolution; // slider value for grid resolution
float Z; // controls the height difference in the terrain
float noiseXD,noiseYD; // modifiers for X,Y noise
boolean toggleSolid=true; // controls rendering style

UNav3D nav; // camera controller
Terrain terrain; // Terrain object

PImage img;


void setup() {
  size(600,600, OPENGL);
  
  // input image must be square or have a greater height than width.

  // this image is borrowed from the excellent contour map tutorial
  // by OnFormative:
  // http://onformative.com/lab/creating-contour-maps/
  img=loadImage("heightmap.png");
    
  initControllers(); // initialize interface, see "GUI" tab
  generateMesh(); // initialize mesh surface, see "Terrain"
}

void draw() {
  background(255);
  smooth();

  // because we want controlP5 to be drawn on top of everything
  // we need to disable OpenGL's depth testing at the end
  // of draw(). that means we need to turn it on again here.
  hint(ENABLE_DEPTH_TEST); 
  
  pushMatrix();    
  lights();
    
  nav.doTransforms(); // transformations using Nav3D
  terrain.draw();
  
  popMatrix();
  
  // turn off depth test so the controlP5 GUI draws correctly
  hint(DISABLE_DEPTH_TEST);
}

// initializes 3D mesh
void generateMesh() {
  if(terrain==null) terrain=new Terrain(this);
  terrain.buildModel();
}
