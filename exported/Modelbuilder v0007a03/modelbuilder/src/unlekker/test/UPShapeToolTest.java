package unlekker.test;

import java.io.File;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;

public class UPShapeToolTest extends PApplet {
	PShape svg;
	UPShapeTool shapetool;
	
	public void setup() {
		size(500,500, OPENGL);
		svg=loadShape(sketchPath+"/data/ABCXYZ.svg");
		shapetool=new UPShapeTool(svg);
		shapetool.getVertices();
		shapetool.geo.center().setDimensions(width);
		
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
//				line(o.vl[i].v[j].x,o.vl[i].v[j].y, o.vl[i].v[j].x*2,o.vl[i].v[j].y*2);
				ellipse(o.vl[i].v[j].x,o.vl[i].v[j].y, 5,5);
			}
		}
		
		noFill();
		stroke(255,255,0);
		shapetool.draw(this);
	}
}
