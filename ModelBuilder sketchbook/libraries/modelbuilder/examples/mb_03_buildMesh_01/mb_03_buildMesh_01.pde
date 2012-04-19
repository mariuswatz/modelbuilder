// unlekker.modelbuilder example
// Marius Watz, 2011
// http://workshop.evolutionzone.com
//
// Shared under Creative Commons "share-alike non-commercial use 
// only" license.

// Cylindrical parametric form. Press space to randomize

import unlekker.util.*;
import unlekker.modelbuilder.*;

int num=60,numSeg=20;
int numSeg2=numSeg*2;
float segH;

UNav3D nav;

CylPanel panel[];
UGeometry model;

void setup() {
  size(600,600, P3D);

  nav=new UNav3D(this);
  nav.trans.set(width/2,height/2,0);
  smooth();
  
  initShape();
}

void draw() {
  background(100);
  lights();
  
  nav.doTransforms();
  if(mousePressed) {
    fill(255,255,255, 128);
    noStroke();
  }
  else {
    fill(255);
    stroke(100);
  }

  model.draw(this);
}

public void mouseDragged() {
  nav.mouseDragged();
}
	
public void keyPressed() {
  nav.keyPressed();

  if(key==' ') initShape();
  else if(key=='s') {
    model.writeSTL(this, 
      IO.getIncrementalFilename("Cyl01 ####.stl",sketchPath));
  }
}
