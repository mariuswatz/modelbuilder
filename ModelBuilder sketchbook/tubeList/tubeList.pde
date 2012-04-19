import processing.opengl.*;

import unlekker.util.*;
import unlekker.modelbuilder.*;
import ec.util.*;

UVertexList vl[];
UGeometry model;
UNav3D nav;

void setup() {
  size(800,800,OPENGL);
  
  nav=new UNav3D(this);
  nav.trans.set(width/2,height/2,0);
  
  generate();
}

void draw() {
  background(0);
  lights();
//  nav.doTransforms();
  translate(nav.trans.x,nav.trans.y,nav.trans.z);
  rotateX(nav.rot.x);
  rotateY(nav.rot.y);
  stroke(255);
  fill(255,0,0);
  
  model.draw(this);
}
