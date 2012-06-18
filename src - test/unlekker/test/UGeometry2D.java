package unlekker.test;

import processing.core.*;
import unlekker.modelbuilder.*;

public class UGeometry2D extends PApplet {
	UGeometry geo;
	UVertexList vl,vl2;
	
	public void setup() {
		size(600,600,JAVA2D);
		
		vl=UVertexList.getCircle(100, 36);
		vl2=new UVertexList(vl).scale(1.5f);
		
		geo=new UGeometry().quadStrip(vl,vl2);
		
	}
	
	public void draw() {
		background(255);
		
		noStroke();
		geo.draw(this);

		stroke(255,0,0);
		vl.draw(this);
		vl2.draw(this);
		
	}
	
}
