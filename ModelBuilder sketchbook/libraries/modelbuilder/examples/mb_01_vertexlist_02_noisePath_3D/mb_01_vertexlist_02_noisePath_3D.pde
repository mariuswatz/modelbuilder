// unlekker.modelbuilder example
// Marius Watz, 2011
// http://workshop.evolutionzone.com

// Uses UVertexList to store a QUAD_STRIP path

import unlekker.util.*;
import unlekker.modelbuilder.*;

UNav3D nav;
UVertexList vl;
float h=100; // height of path
float t=0; // noise offset

void setup() {
  size(400,400, P3D);
  vl=new UVertexList();
  
  // add UNav3D navigation
  nav=new UNav3D(this);
  nav.trans.set(width/2,height/2,0);
}

void draw() {
  background(100);

  lights();
  
  // call UNav3D transforms
//  nav.rot.x+=radians(0.1);
//  nav.rot.y+=radians(1);
  nav.doTransforms();

  // calculate path
  build();
  
  // Use UVertexList.drawQuadStrip to draw QUAD_STRIP
  vl.drawQuadStrip(this);
}
