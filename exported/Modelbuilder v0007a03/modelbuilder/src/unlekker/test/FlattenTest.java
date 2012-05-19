package unlekker.test;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.util.*;

public class FlattenTest extends PApplet {
	UNav3D nav;
	UGeometry geo,flat;
	
	public void setup() {
		size(800,800,OPENGL);
		nav=new UNav3D(this).setTranslation(width/2, height/2,0);
		
		build();
	}
	
	private void build() {
		geo=UPrimitive.box(100, 100, 100);
		
		geo.writeSTL(this, "testbox.stl");
		flat=new UGeometry(geo);		
		flat.flatten();
		
		for(int i=0; i<geo.faceNum; i++) {
			geo.face[i].calcNormal();
			geo.face[i].translate(new UVec3(geo.face[i].n).mult(15f));
		}
		
	}

	public void keyPressed() {
	}
	
	public void draw() {
		background(0);
		lights();
		nav.doTransforms();
		
		fill(255);
		geo.draw(this);
		translate(0,150,0);
		flat.draw(this);
		
		fill(255,0,0);
		textFont(createFont("arial", 20));
		textAlign(CENTER);
		flat.drawFaceLabels(this);
		geo.drawFaceLabels(this);
	}
	
}
