import unlekker.util.*;
import unlekker.modelbuilder.*;
import ec.util.*;

import controlP5.*;

import processing.opengl.*;

boolean doShowAll=true;
UGeometry model;
int stepId=0;
UNav3D nav;

public void setup() {
  size(800, 800, OPENGL);
  nav=new UNav3D(this);
  nav.setTranslation(width/2, height/2, 0);

  initGUI();
}

public void draw() {  
  background(0);
  lights();

  pushMatrix();
  nav.doTransforms();
  
  if(model!=null) {
    noStroke();
    fill(255);
    
    if(doShowAll) model.draw(this);
    else {
      model.face[stepId].draw(this);      
      stroke(150,50);
      noFill();
      model.draw(this);
    }
    
  }
  
  popMatrix();
  
  if(!doShowAll) {
    fill(255);
    text("Poly ID "+stepId,gui.cpw+10,20);
  }
  
  gui.draw();
}
