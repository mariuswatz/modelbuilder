/**
 * CameraDemo.pde - Marius Watz, 2012
 * http://workshop.evolutionzone.com
 * 
 * Demonstrates the use of UCameraTransition, which allows
 * the user to set up multiple camera viewpoints and then 
 * interpolate between them.
 */

import controlP5.*;
import java.awt.event.KeyEvent;

import processing.opengl.*;

import unlekker.util.*;
import unlekker.modelbuilder.*;
import ec.util.*;

import processing.core.PApplet;

UCameraTransition cam;
UVertexList boxPos;

public void setup() {
  size(600, 600, OPENGL);
  initCamera();
  initGUI();
  
  boxPos=new UVertexList();
  for (int i=0; i<100; i++) boxPos.add(
  random(-0.5f, 0.5f)*(float)width, 
  random(-0.5f, 0.5f)*(float)height, 
  random(-100));
}

public void draw() {
  background(0);
  pushMatrix();
  translate(width/2, height/2);
  lights();

  cam.update();
  cam.nav.doTransforms();

  for (int i=0; i<boxPos.n; i++) {
    pushMatrix();
    fill(255, 20+(i*5)%155, 0);
    translate(boxPos.v[i].x, boxPos.v[i].y, boxPos.v[i].z);
    box(i+5);
    popMatrix();
  }
  popMatrix();
  
  gui.draw();
}

void initCamera() {
  cam=new UCameraTransition(this);
  cam.nav=new UNav3D(this);
  
  for (int i=0; i<cam.cam.length; i++) {
    cam.nav.setTranslation(random(-0.35f, 0.35f)*width, random(-0.35f, 0.35f)*height, random(200));
    cam.nav.setRotation(random(-0.5f, 0.5f)*radians(60), random(-0.5f, 0.5f)*radians(60), 0);	  	
    cam.setCam(i);
  }
  
  cam.setDuration(60);
}



