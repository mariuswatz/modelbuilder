// 2011.0219 Interactive Parametrics Workshop 
// Marius Watz with Studio Mode and MakerBot
// http://modelab.nu/?p=4152
// http://workshop.evolutionzone.com
//
// Shared under Creative Commons "share-alike non-commercial use 
// only" license.

// Uses UVertexList to store a path of vertices

import unlekker.util.*;
import unlekker.modelbuilder.*;

UVertexList vl;

void setup() {
  size(400,400, P3D);

  vl=new UVertexList();
  for(float i=0; i<1.01; i+=0.05f) {
    // add point on a sine curve
    vl.add(i*(float)width,sin(TWO_PI*i),0);
  }
  
  // scale and translate vertex list so that it has
  // "height" height and is centered in the middle of the canvas
  //
  // translate() and scale() return a reference back to the original
  // UVertexList instance and can therefore be chained jQuery-style:
  vl.scale(1,height/2,1).translate(0,height/2,0);
}

void draw() {
  background(100);

  noFill();
  beginShape();
  // Use UVertexList.drawVertices to make all necessary vertex() calls
  vl.drawVertices(this);
  endShape();
}


