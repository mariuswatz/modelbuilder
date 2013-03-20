/**
 * DeformModel02.pde - Marius Watz, 2012
 * http://workshop.evolutionzone.com
 * 
 * Demonstrates use of unlekker.modelbuilder.filter.UTransformDeform,
 * allowing various deformations of mesh objects.
 * 
 */

import controlP5.*;

import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.modelbuilder.filter.*;
import unlekker.util.*;


UGeometry geo;
float amount=0.5f;
int u=40, v=60, num=4;
int posIndex;
float radius=100, force=0.4f;
UVec3 attractorPos;
UGeometry loadedModel;

public void setup() {
  size(800, 600,OPENGL);

  initGUI();
  resetForm();
}

public void draw() {
  background(0);

  pushMatrix();
  lights();
  nav.doTransforms();

  fill(255);
//  stroke(50, 50);
  geo.draw(this);

  drawAttractor();

  popMatrix();

  noSmooth();
  hint(DISABLE_DEPTH_TEST);
  gui.draw();
  hint(ENABLE_DEPTH_TEST);
  
  if(doRandomAttractor) {
    doRandomAttractor=false;
    for(int i=0; i<50; i++) randomAttractor();
  }
}

void drawAttractor() {
  pushMatrix();
  UVec3 pp=geo.vert.v[posIndex];
  pp=attractorPos;
  translate(pp.x, pp.y, pp.z);
  fill(255, 100, 0, 50);
  sphere(radius);
  popMatrix();
}

