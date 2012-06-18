/**
 * GoogleData01.pde - Marius Watz, 2012
 * http://workshop.evolutionzone.com
 * 
 * Constructs a solid 3D representation of Google stock
 * prices for 2008 (the year of the crash).
 */

import controlP5.*;

import processing.opengl.*;

import unlekker.util.*;
import unlekker.modelbuilder.*;
import unlekker.modelbuilder.filter.*;
import ec.util.*;

UNav3D nav;

void setup() {
  size(800,600, OPENGL);

  nav=new UNav3D(this);
  nav.setTranslation(width/2,height/2,0);  
  nav.setRotation(radians(150),radians(30),0);
  
  loadStockData();
  initGUI();
  build();
  
  textFont(createFont("Arial",10));
}

void draw() {
  background(0);
  lights();
  
  pushMatrix();
  nav.doTransforms();
  
  fill(255,100,0);
  geo.draw(this);

  rotateX(-HALF_PI);
  for(int i=50; i<700; i+=50) {
    stroke(255,150);
    noFill();
    translate(0,0,50*(300.0/maxDataVal));
    ellipse(0,0,590,590);
    
    noStroke();
    fill(255);
    for(int j=0; j<4; j++) {
      pushMatrix();
      rotateZ(radians(j*90));
      translate(0,-295,0);
      rotateX(-HALF_PI);
      text("$"+nf(i,3),0,8);
      popMatrix();
    }
  }
  
  popMatrix();
  
  gui.draw();
  
  if(doRebuild) {
    build();
    doRebuild=false;
  }
}
