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

  generate();
}

void draw() {
  background(100);

  noFill();

  // Use UVertexList.drawVertices to make all necessary vertex() calls
  beginShape();
  vl.drawVertices(this);
  endShape();
  
}

