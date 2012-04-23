import processing.opengl.*;

import unlekker.util.*;
import unlekker.modelbuilder.*;
import ec.util.*;

UVertexList vl1,vl2,vl3;
UGeometry geo;
UNav3D nav;

void setup() {
  size(600,600,OPENGL);
  build();
  nav=new UNav3D(this);
  nav.setTranslation(width/2,height/2,0);
}

void draw() {
  background(0);
  fill(255);
  lights();
  
  nav.doTransforms();
  geo.draw(this);
}

void keyPressed() {
  if(key==' ') build();
}


