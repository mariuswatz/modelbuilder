package test;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.util.*;

public class TestGeom02 extends PApplet {
	Geometry obj,sph;
	MouseNav3D nav;
	boolean doInit=true;

	public void setup() {
		size(800,800, OPENGL);
		obj=new Geometry();
		
		VertexList vl=new VertexList();
		for(int i=0; i<12; i++) {
			Vec3 vv=new Vec3(random(50,150),0,0);
			vv.rotateY(radians(30*i+random(-10,10)));
			vl.add(vv);
		}
		
		Util.log("vl.n "+vl.n);
		
		VertexList vla[]=new VertexList[10];
		for(int i=0; i<10; i++) {
			vla[i]=new VertexList(vl);
			Util.log(i+" "+vla[i].n);
			vla[i].scale(1+(float)i/10f);
			vla[i].rotateY(PI*((float)i/10f));
			vla[i].translate(0, -30*i, 0);
		}
		
		obj.quadStrip(vla, 10);
		obj.triangleFan(vla[9],true);
		obj.triangleFan(vla[0],false);
		
		nav=new MouseNav3D(this);
		nav.trans.set(width/2,height/2,0);
	}
	
	public void draw() {
		background(255);
		
		lights();
		nav.doTransforms();
		
		fill(255,0,0);
		obj.draw(this);
		
	}

	public void mouseDragged() {
		nav.mouseDragged();
	}
	
	public void keyPressed() {
		nav.keyPressed();
		
		if(key=='s') {
			obj.writeSTL(this,
					IO.getIncrementalFilename("test###.stl", sketchPath)
					);
		}
	}
	
	public static void main(String [] args) {
		PApplet.main(new String [] {"test.TestGeom02"});
	}

}
