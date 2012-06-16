package unlekker.test;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.modelbuilder.filter.UFilter;
import unlekker.modelbuilder.filter.USubdivider;
import unlekker.util.*;

public class SubDivTest extends PApplet {
	UNav3D nav;
	UGeometry geo,subdiv;
	
	public void setup() {
		size(800,800,OPENGL);
		nav=new UNav3D(this).setTranslation(width/2, height/2,0);
		
		build();
	}
	
	private void build() {
		geo=new UGeometry();
		geo.beginShape(QUAD_STRIP);
		geo.vertex(0,0,0).vertex(0,200,0).
			vertex(200,0,0).vertex(200,200,0).endShape();
		
		subdiv=new USubdivider().build(geo);
		subdiv.translate(0,200,0);
		subdiv.add(
				new USubdivider().setType(UFilter.SUBDIVIDE_CENTROID).build(geo));
		geo.translate(0,-200,0);
	}

	public void keyPressed() {
	}
	
	public void draw() {
		background(0);
		
		noFill();
		translate(width/2, height/2);
		
		stroke(255,0,0);
		geo.draw(this);

		stroke(255);
		subdiv.draw(this);
	}
	
}
