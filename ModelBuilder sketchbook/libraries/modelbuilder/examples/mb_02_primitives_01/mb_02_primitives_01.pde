// 2011.0219 Interactive Parametrics Workshop 
// Marius Watz with Studio Mode and MakerBot
// http://modelab.nu/?p=4152
// http://workshop.evolutionzone.com
//
// Shared under Creative Commons "share-alike non-commercial use 
// only" license.

// Example showing how to construct mesh primitives and
// add them together as a single UGeometry instance.

import unlekker.util.*;
import unlekker.modelbuilder.*;

UNav3D nav;

UGeometry model;

void setup() {
  size(400,400, P3D);

  // add UNav3D navigation
  nav=new UNav3D(this);
  nav.trans.set(width/2,height/2,0);
  smooth();
  
  buildModel();
}

void draw() {
  background(100);

  lights();
  
  // call UNav3D transforms
  nav.doTransforms();

  if(mousePressed) noFill();
  else fill(255,100,0);
  model.draw(this);
}

public void mouseDragged() {
  nav.mouseDragged();
}
	
public void keyPressed() {
  nav.keyPressed();

  if(key=='s') {
    model.writeSTL(this, "Test.stl");
  }
}
