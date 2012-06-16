import processing.opengl.*;

import unlekker.util.*;
import unlekker.modelbuilder.*;
import unlekker.modelbuilder.filter.*;
import ec.util.*;

PShape svg;
UPShapeTool shapetool;

public void setup() {
  size(500,500, OPENGL);
  svg=loadShape(sketchPath+"/data/ABCXYZ.svg");
  shapetool=new UPShapeTool(svg);
  shapetool.getVertices();
  shapetool.geo.center().setDimensions(width-50);
  
  println(shapetool.vertn+" "+shapetool.geo.vln);
  smooth();
}

public void draw() {
  background(0);

  translate(width/2,height/2);
  
  UGeometry o=shapetool.geo;
  fill(255,0,0);
  noStroke();
  for(int i=0; i<o.vln; i++) {
    for(int j=0; j<o.vl[i].n; j++) {
      ellipse(o.vl[i].v[j].x,o.vl[i].v[j].y, 5,5);
    }
  }
  
  noFill();
  stroke(255,255,0);
  if(mousePressed) shapetool.draw(this);
}
