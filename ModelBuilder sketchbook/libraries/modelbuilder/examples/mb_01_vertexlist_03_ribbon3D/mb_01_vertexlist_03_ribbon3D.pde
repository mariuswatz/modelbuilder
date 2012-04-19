import processing.opengl.*;

// unlekker.modelbuilder example
// Marius Watz, 2011
// http://workshop.evolutionzone.com

// Construct 3D ribbon as a QUAD_STRIP and store it as UGeometry

import unlekker.util.*;
import unlekker.modelbuilder.*;

UNav3D nav;
UGeometry model;

void setup() {
  size(400,400, OPENGL);

  // add UNav3D navigation
  nav=new UNav3D(this);
  nav.trans.set(width/2,height/2,0);
  
  buildModel();
}

void draw() {
  background(100);

  lights();
  
  // call UNav3D transforms
  nav.doTransforms();

  fill(0);
  stroke(255);
  model.draw(this);
}

public void mouseDragged() {
  nav.mouseDragged();
}
	
public void keyPressed() {
  nav.keyPressed();

  if(key=='s') {
    model.writeSTL(this, "ribbon.stl");
  }
}
