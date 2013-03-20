/**
 * UTimerDemo.pde - Marius Watz, 2012
 * http://workshop.evolutionzone.com
 *
 * Demonstrates the use of global and local time (T) to control
 * drawing behavior using the UTimer class from Modelbuilder.
 *
 */

import unlekker.util.*;
import unlekker.modelbuilder.*;
import unlekker.modelbuilder.filter.*;
import ec.util.*;



Particle part[];
float globalT;
UColorTool colors;

public void setup() {
  size(600, 600, P3D);

  initColors();
  initScene();  
}

public void draw() {
  background(0);
  lights();
  
  // calculate global T
  globalT=(float)(frameCount%500)/500f;

  // rotate camera according to global T
  translate(width/2, height/2);
  rotateX(radians(-30+globalT*60));
  rotateY(radians(30-globalT*60));
  
  // draw 3D axis indicator to show global rotation
  fill(255);
  noStroke();
  pushMatrix();
  box(100,2,2);
  rotateY(HALF_PI);
  box(100,2,2);
  rotateZ(HALF_PI);
  box(100,2,2);
  popMatrix();
  
  // draw objects
  stroke(0);
  for (int i=0; i<part.length; i++) part[i].draw();
  
  // re-initialize scene if globalT > 0.999f.
  // note: never check for globalT==1, floats aren't reliable due to rounding
  if(globalT>0.999f) initScene();
}

void initScene() {
  // initialize scene
  part=new Particle[(int)random(50, 100)];
  for (int i=0; i<part.length; i++) part[i]=new Particle();
}

void initColors() {
  colors=new UColorTool();
  colors.addGradient(3, 10, "02D4E5", "1D385A");
  colors.addGradient(3, 10, "FFFF00", "FFFFFF");
  colors.addGradient(3, 10, "FFB700", "FF680A");
  colors.generateColors();
}


