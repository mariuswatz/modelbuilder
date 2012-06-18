/**
 * UColorTool_demo_01.pde - Marius Watz, 2012
 * http://workshop.evolutionzone.com
 * 
 * Demonstrates UColorTool in the context of a
 * randomized visual composition.
 */
 
 import unlekker.util.*;
import unlekker.modelbuilder.*;
import ec.util.*;

Element el[];
int nx,ny,bgCol;
float elRad,xd,yd;

void setup() {
  size(800, 600);  
  smooth();

  compose();
}

void draw() {
  background(bgCol);

  for (int i=0; i<el.length; i++) el[i].draw();
}

void mousePressed() {
  compose();
}

