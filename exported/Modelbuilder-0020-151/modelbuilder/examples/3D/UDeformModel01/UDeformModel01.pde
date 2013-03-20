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


UGeometry geo,loadedModel;
float amount=0.5f;
int u=8, v=16;

public void setup() {
  size(800, 600, OPENGL);

  initGUI();
  resetForm();
}

public void draw() {
  background(0);

  pushMatrix();
  lights();
  nav.doTransforms();

  fill(255);
  stroke(50, 50);
  geo.draw(this);

  noFill();
  stroke(255, 100);
  for (float i=0; i<11; i++) {
    for (float j=0; j<11; j++) {
      line(i*50-250, 0, -250, i*50-250, 0, 250);
      line(-250, 0, i*50-250, 250, 0, i*50-250);
    }
  }
  popMatrix();



  hint(DISABLE_DEPTH_TEST);
  gui.draw();
  hint(ENABLE_DEPTH_TEST);
}

