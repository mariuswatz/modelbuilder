import controlP5.*;

import unlekker.test.*;
import unlekker.util.*;
import unlekker.modelbuilder.*;
import ec.util.*;
import java.util.*;

ArrayList<Particle> p;
int maxLevel=3,maxBranches=4;
float startSpeed=3,rotMod=1,speedMod=1;
float branchAngle=30;

void setup() {
  size(1000,600);  
  smooth();
  
  initGUI();
  reinit();
}

void draw() {
  background(0);

  for(int i=0; i<p.size(); i++) p.get(i).draw();
  
  gui.draw();
}

void reinit() {
  initColors();

  p=new ArrayList<Particle>();
  p.add(new Particle(null));
}
