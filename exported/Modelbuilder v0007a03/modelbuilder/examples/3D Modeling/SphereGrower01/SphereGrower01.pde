import java.util.ArrayList;

import controlP5.ControlEvent;

import processing.core.*;
import unlekker.modelbuilder.*;
import unlekker.util.*;

import processing.opengl.*;
import processing.pdf.PGraphicsPDF;

UGeometry model, unwrapper;
UNav3D nav;
USimpleGUI gui;
private String filename;
ArrayList<Sph> spheres;
float minGrowthMod, maxGrowthMod;
int type, SPHERE=0, BOX=1;


public void setup() {
  size(1000, 800, OPENGL);

  nav=new UNav3D(this);
  nav.setTranslation(width/2, height/2, 0);

  minGrowthMod=0.35f;
  maxGrowthMod=0.6f;

  if (spheres==null) {
    reinit();
    initGUI();
  }

  smooth();
  hint(ENABLE_OPENGL_4X_SMOOTH);
}

public void draw() {
  background(255);

  lights();

  pushMatrix();

  nav.doTransforms();
  stroke(0);
  fill(255, 100, 0);

  for (Sph theSphere : spheres) theSphere.model.draw(this);

  popMatrix();

  gui.draw();
}

public void reinit() {
  // using an ArrayList<> of a local class requires using
  // the parent class as a root when calling new.
  // if you rename the sketch, this call must be changed to use the
  // new sketch name.
  if (spheres==null) spheres=new ArrayList<SphereGrower01.Sph>();
  else spheres.clear();

  spheres.add(new Sph(0, 0, 0, 200));
  spheres.get(0).build();
}


