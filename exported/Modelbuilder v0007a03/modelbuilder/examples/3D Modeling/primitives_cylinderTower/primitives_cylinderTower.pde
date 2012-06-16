// http://workshop.evolutionzone.com
//
// Shared under Creative Commons "share-alike non-commercial use 
// only" license.

// Construct tower of randomly placed cylinders

import unlekker.util.*;
import unlekker.modelbuilder.*;

UNav3D nav;

UGeometry model;

void setup() {
  size(600,600, P3D);

  nav=new UNav3D(this);
  nav.trans.set(width/2,height/2,0);
  
  build();
  smooth();
}

void draw() {
  background(100);
  lights();

  nav.doTransforms();
  
  noFill();
  box(300);
  
  fill(255,100,0);
  model.draw(this);
}

public void mouseDragged() {
  nav.mouseDragged();
}
	
public void keyPressed() {
  nav.keyPressed();

  if(key==' ') {
    build();
  }
  if(key=='s') {
    model.writeSTL(this, "Test.stl");
  }
}
