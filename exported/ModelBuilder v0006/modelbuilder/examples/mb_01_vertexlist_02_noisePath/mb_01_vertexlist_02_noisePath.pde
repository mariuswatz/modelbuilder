// unlekker.modelbuilder example
// Marius Watz, 2011
// http://workshop.evolutionzone.com

// Uses UVertexList to store a QUAD_STRIP path

import unlekker.util.*;
import unlekker.modelbuilder.*;

UVertexList vl;
float h=100; // height of path
float t=0; // noise offset

void setup() {
  size(400,400, P3D);
  vl=new UVertexList();
}

void draw() {
  background(100);

  // calculate path
  build();
  
  // Use UVertexList.drawQuadStrip to draw QUAD_STRIP
  vl.drawQuadStrip(this);
}

// calculate a QUAD_STRIP using noise()
void build() {
  // clear any stored vertex data. this is faster than creating
  // a new UVertexList instance.
  vl.reset();
  
  // add pairs of vertices to make up a QUAD_STRIP
  for(float i=0; i<1.01; i+=0.01f) {
    // add point on a sine curve
    vl.add(i*(float)width,noise(i*1+t)*(float)(height-h)-h*0.5,0);
    vl.add(i*(float)width,noise(i*1+t)*(float)(height-h)+h*0.5,0);
  } 
  t=t+0.01f;
}
