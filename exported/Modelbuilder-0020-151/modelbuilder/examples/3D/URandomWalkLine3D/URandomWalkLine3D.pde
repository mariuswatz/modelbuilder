import java.util.ArrayList;

import controlP5.ControlEvent;

import unlekker.modelbuilder.*;
import unlekker.util.*;

import processing.opengl.*;
import processing.pdf.PGraphicsPDF;

UGeometry model;
UNav3D nav;
USimpleGUI gui;

// last point and current direction
UVec3 dir, last;
float maxRot=60, length=60;
int buildSpeed=25;
boolean doBuild=true;

int cnt=0;

public void setup() {
  size(1000, 800, OPENGL);

  nav=new UNav3D(this);
  nav.setTranslation(width/2, height/2, 0);

  if(model==null) {
    clear();
    initGUI();
  }
}

public void draw() {
  background(255);
  lights();

  pushMatrix();

  nav.doTransforms();
  noStroke();
  fill(255, 100, 0);

  if (doBuild && frameCount%(50-buildSpeed)==0) randomWalk();

  rotateY(radians(frameCount)*0.2f);
  rotateZ(radians(frameCount)*0.1f);
  model.draw(this);

  popMatrix();

  gui.draw();
}


