package unlekker.test;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.util.*;

public class VertexGridTest extends PApplet {
	UNav3D nav;
	UVertexGrid g1,g2;
	UVertexGrid box[];
	UBezierPatch bez[];
	
	public void setup() {
		size(800,800,OPENGL);
		nav=new UNav3D(this).setTranslation(width/2, height/2,0);
		
		build();
	}
	
	private void build() {
		g1=new UVertexGrid(8, 8, 200, 200);
		g2=new UVertexGrid(8, 8, 200, 200).rotateX(30*DEG_TO_RAD).rotateY(20*DEG_TO_RAD).translate(250,0,0);
		
		box=new UVertexGrid[5];
		box[0]=new UVertexGrid(4, 4, 100, 100).translate(0,0,50).rotateX(HALF_PI);
		box[1]=new UVertexGrid(4, 4, 100, 100).rotateY(-85*DEG_TO_RAD).translate(-50,0,0);
		box[2]=new UVertexGrid(4, 4, 100, 100).rotateY(85*DEG_TO_RAD).translate(50,0,0);
		box[3]=new UVertexGrid(4, 4, 100, 100).rotateX(85*DEG_TO_RAD).translate(0,50,0);
		box[4]=new UVertexGrid(4, 4, 100, 100).rotateX(-85*DEG_TO_RAD).translate(0,-50,0);

//		box[0].joinEdge(UVertexGrid.LEFT_EDGE, UVertexGrid.RIGHT_EDGE, box[1]);
//		box[0].joinEdge(UVertexGrid.RIGHT_EDGE, UVertexGrid.RIGHT_EDGE, box[2]);
		
		box[0].joinEdge(UVertexGrid.TOP_EDGE, UVertexGrid.BOTTOM_EDGE, box[4], false);
//		box[0].joinEdge(UVertexGrid.BOTTOM_EDGE, UVertexGrid.BOTTOM_EDGE, box[4], true);
//		box[0].joinEdge(UVertexGrid.BOTTOM_EDGE, UVertexGrid.TOP_EDGE, box[4]);
//		box[2].joinEdge(UVertexGrid.TOP_EDGE, UVertexGrid.RIGHT_EDGE, box[3]);
//		box[1].joinEdge(UVertexGrid.TOP_EDGE, UVertexGrid.LEFT_EDGE, box[3]);
//		box[1].joinEdge(UVertexGrid.BOTTOM_EDGE, UVertexGrid.LEFT_EDGE, box[4]);
		
		
//		box[0].joinEdge(box[0].RIGHT_EDGE, box[2]);
//		box[0].joinEdge(box[0].BOTTOM_EDGE, box[4]);
//		box[0].joinEdge(box[0].TOP_EDGE, box[3]);
////		box[2].joinEdge(box[2].BOTTOM_EDGE, box[3]);

		
		for(int i=0; i<10; i++) {
			UVec3 v=box[UUtil.rnd.integer(box.length)].getRandomVertex();
//			v.add(UUtil.rnd.random(2.5f,5f)*UUtil.rnd.randomSign(),
//					UUtil.rnd.random(2.5f,5f)*UUtil.rnd.randomSign(),
//					UUtil.rnd.random(2.5f,5f)*UUtil.rnd.randomSign());
//			v.norm(UUtil.rnd.random(100,120));
		}
		
		bez=new UBezierPatch[box.length];
		for(int i=0; i<bez.length; i++) {
			bez[i]=new UBezierPatch(box[i].grid);
			bez[i].eval(30, 30);		
		}
		UVertexGrid.translate(box, -200,0,0);
		
		UGeometry mesh=UVertexGrid.toUGeometry(box);
		mesh.writeSTL(this, this.getClass().getSimpleName()+" Box.stl");
	}

	public void keyPressed() {
		if(key==' ') build();
		if(key=='1') g1.joinEdge(g1.LEFT_EDGE,g1.RIGHT_EDGE, g2, false);
		if(key=='2') g1.joinEdge(g1.RIGHT_EDGE,g1.LEFT_EDGE, g2, false);
		if(key=='3') g1.joinEdge(g1.TOP_EDGE,g1.BOTTOM_EDGE, g2, false);
		if(key=='4') g1.joinEdge(g1.BOTTOM_EDGE,g1.TOP_EDGE, g2, false);
	}
	
	public void draw() {
		background(0);
		lights();
		nav.doTransforms();
		
		fill(255);
		noStroke();
		
		noFill();
		stroke(255);
		
		g1.draw(this);
		g2.draw(this);
		
		stroke(0);
		fill(255);
		box[0].draw(this);
		fill(255,0,0);
		box[1].draw(this);
		fill(0,255,255);
		box[2].draw(this);
		fill(255,100,0);
		box[3].draw(this);
		fill(0,200,0);
		box[4].draw(this);
		
		stroke(0);
		fill(255);
		for(int i=0; i<bez.length; i++) bez[i].geo.draw(this);
	}
	
	static public void main(String args[]) {
		// PApplet.main(new String[] { "unlekker006default.AppDefault" });
		PApplet.main(new String[] { "unlekker.test.VertexGridTest" });

	}

}
