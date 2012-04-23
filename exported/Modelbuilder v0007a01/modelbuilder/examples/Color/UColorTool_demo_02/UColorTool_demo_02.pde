import controlP5.*;

import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.util.*;

UColorTool colors;
UVertexList vl;
USimpleGUI gui;
boolean disablePalettes;

public void setup() {
  size(600, 600, OPENGL);
  initGUI();
  initColors();

  vl=new UVertexList();
  for (int i=0; i<50; i++) 
    vl.add(new UVec3(random(width), random(100, height)));
}

public void draw() {
  background(50);
  lights();

  for (int i=0; i<50; i++) {
    fill(colors.colors[i % colors.n]);
    ellipse(vl.v[i].x, vl.v[i].y, i*2+5, i*2+5);
  }
  
  gui.draw();
  colors.drawColors(this, 0, (int)gui.cph);
}

