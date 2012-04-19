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

UVertexList vl,vl2;

void setup() {
  size(400,400, P3D);

  generate();
}

void draw() {
  background(100);

  noFill();
  stroke(255);
  beginShape();
  // Use UVertexList.drawVertices to make all necessary vertex() calls
  vl.drawVertices(this);
  endShape();
  
  for(int i=0; i<vl.n; i++) ellipse(vl.v[i].x,vl.v[i].y, 6,6);
  
  // you can also use UVertexList.draw() to call begin/endShape w/ all
  // vertices
  fill(0);
  noStroke();
  vl2.draw(this);
}

void mousePressed() {
  generate();
}
