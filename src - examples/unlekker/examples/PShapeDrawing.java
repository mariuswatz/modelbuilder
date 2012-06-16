package unlekker.examples;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.modelbuilder.filter.UTransformDeform;
import unlekker.util.*;

public class PShapeDrawing extends PApplet {
	PShape svg;
	UPShapeTool shapetool;
	
	public void setup() {
		size(500,500, OPENGL);
		svg=loadShape(sketchPath+"/data/ABCXYZ.svg");
		shapetool=new UPShapeTool(svg);
		shapetool.getVertices();
		
		for(int i=0; i<shapetool.geo.vln; i++) {
			shapetool.geo.vl[i].addMidPoints();
			shapetool.geo.vl[i].addMidPoints();
			shapetool.geo.vl[i].addMidPoints();
			
			shapetool.geo.vl[i].calcBounds();
		}
		
		shapetool.geo.calcBounds();
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
		stroke(0,255,255, 20);
		for(int i=0; i<o.vln; i++) {
			UVec3 c=new UVec3(o.vl[i].bb.c);
//			c.rotateZ(radians(frameCount));
			
			for(int j=0; j<o.vl[i].n; j++) {
				float fract=map(frameCount%50,0,49, 1,-1);
				UVec3 cv=UVec3.interpolate(o.vl[i].v[j], c, 
						sin(fract*PI));
				line(o.vl[i].v[j].x,o.vl[i].v[j].y, cv.x,cv.y);
//				if(mousePressed) ellipse(o.vl[i].v[j].x,o.vl[i].v[j].y, 5,5);
			}
		}
		
		noFill();
		stroke(255,255,0);
		if(mousePressed) shapetool.draw(this);
	}

}
