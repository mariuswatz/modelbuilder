
import java.awt.event.KeyEvent;

import processing.opengl.*;

import unlekker.test.*;
import unlekker.util.*;
import unlekker.modelbuilder.*;
import ec.util.*;

import processing.core.PApplet;

UCameraTransition cam;
UVertexList boxPos;

public void setup() {
  size(600, 600, OPENGL);
  initCamera();

  boxPos=new UVertexList();
  for (int i=0; i<100; i++) boxPos.add(
  random(-0.5f, 0.5f)*(float)width, 
  random(-0.5f, 0.5f)*(float)height, 
  random(-100));
}

public void draw() {
  background(0);
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


public void keyPressed() {
  if (keyEvent.isControlDown()) {
    if (keyCode==KeyEvent.VK_R) {
      if (cam.isRunning)cam.stop(); 
      else cam.run();
    }
    if (keyEvent.isAltDown()) {
      if (keyCode==KeyEvent.VK_1) cam.setCam(0);
      if (keyCode==KeyEvent.VK_2) cam.setCam(1);
      if (keyCode==KeyEvent.VK_3) cam.setCam(2);
    }
    else if (keyEvent.isShiftDown()) {
      if (keyCode==KeyEvent.VK_1) cam.setView(0);
      if (keyCode==KeyEvent.VK_2) cam.setView(1);
      if (keyCode==KeyEvent.VK_3) cam.setView(2);
    }
  }
}

