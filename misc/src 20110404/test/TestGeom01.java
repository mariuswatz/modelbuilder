package test;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.util.*;

public class TestGeom01 extends PApplet {
	Geometry obj,sph;
	MouseNav3D nav;
	boolean doInit=true;

	public void setup() {
		size(800,800, OPENGL);
		obj=new Geometry();
		
		obj.beginShape(QUAD_STRIP);
		obj.vertex(0,0,0);
		obj.vertex(100,0,0);
		obj.vertex(0,100,0);
		obj.vertex(100,150,0);
		obj.vertex(0,150,0);
		obj.vertex(150,200,0);
		obj.endShape();
		
		obj.center();
		obj.setDimensions(50);
		
		sph=Primitive.sphere(50,18);
		sph.add(Primitive.cylinder(20, 50, 18, true).translate(100, 0, 0))
			.add(Primitive.box(25, 50, 50).translate(-100, 0, 0))
			.add(Primitive.cone(25, 50, 18).translate(200, 0, 0))
			.add(Primitive.disc(50, 18).translate(300, 0, 0))
			.add(Primitive.rect(50, 50).translate(100, 100, 0))
		;
		
		nav=new MouseNav3D(this);
		nav.trans.set(width/2,height/2,0);
	}
	
	public void doInitialize() {		
		doInit=false;
	}
	
	public void draw() {
//		if(frameCount<20) return;
//		else if(doInit) doInitialize();
		
		background(255);
		
		nav.doTransforms();
		lights();
		
		fill(255,0,0);
		obj.draw(this);
		
	
		fill(255,100,0);
		sph.draw(this);
		
		noFill();
		rect(-50,-50, 100,100);
	}

	public void mouseDragged() {
		nav.mouseDragged();
	}
	
	public void keyPressed() {
		nav.keyPressed();
		
		if(key=='s') {
			Geometry tmp=new Geometry(obj);
			println("n "+tmp.faceNum+" "+tmp.face);
			tmp.add(sph);
			println("n "+tmp.faceNum);
			tmp.rotateX(PI);
			
			tmp.writeSTL(this,
					IO.getIncrementalFilename("test###.stl", sketchPath)
					);
		}
	}
	
	public static void main(String [] args) {
		PApplet.main(new String [] {"test.TestGeom01"});
	}

}
