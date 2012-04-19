// unlekker.modelbuilder example
// Marius Watz, 2011
// http://workshop.evolutionzone.com

// Construct 3D ribbon as a QUAD_STRIP

import unlekker.util.*;
import unlekker.modelbuilder.*;

MouseNav3D nav;

UGeometry model;

void setup() {
  size(400,400, P3D);

  // add MouseNav3D navigation
  nav=new MouseNav3D(this);
  nav.trans.set(width/2,height/2,0);
  
  buildModel();
}

void draw() {
  background(100);

  lights();
  
  // call MouseNav3D transforms
  nav.doTransforms();
  fill(255);

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
