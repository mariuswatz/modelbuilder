package unlekker.test;

import java.io.File;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.util.UIO;
import unlekker.util.USimpleGUI;

public class UGeometryTransformerTest extends PApplet {
	UGeometry grid;
	UGeometryTransformer trans;
	UNav3D nav;
	USimpleGUI gui;
	int gridsize=10;
	float maxforce=150,wmod=1,extrudeMod=1;
	
	public void setup() {
		size(1000,700, OPENGL);
		
		reinit();
		trans=new UGeometryTransformer();
		
		gui=new USimpleGUI(this);
		gui.layoutVertical=false;
		gui.addSlider("gridsize",gridsize,5,50);
		gui.addSlider("wmod",wmod,0.5f,3);
		gui.newRow();

		gui.addSlider("maxforce",maxforce,100,500);
		gui.addSlider("extrudeMod",extrudeMod,0.2f,3);
		gui.newRow();

		gui.addButton("attractor");
		gui.addButton("repulsor");
		gui.addPos(30, 0);
		
		gui.addButton("reinit");
		gui.addButton("loadFile");
		gui.addButton("saveSTL");
		gui.newRow();

		nav=new UNav3D(this).setTranslation(width/2,height/2,0);
		smooth();
	}
	
	public void draw() {
		background(0);
	
		pushMatrix();
		lights();
		nav.doTransforms();
		fill(255,100,0);
		stroke(0);
		grid.draw(this);
		popMatrix();
		
		gui.draw();
	}
	
	public void loadFile() {
		
		grid=UGeometry.readSTL(this, sketchPath+"/data/UGeometryTransformerTest 009.stl");
		grid.setDimensions(200);
		
	}
	
	public void reinit() {
		grid=UPrimitive.cylinderGrid(100*wmod, 300, gridsize*2, gridsize, true);
		grid.setDimensions(500);
		grid=new UGeometryTransformer().extrude(grid, 5*extrudeMod, 5*extrudeMod,false);
	}
	
	public void saveSTL() {
		grid.writeSTL(this, 
				UIO.getIncrementalFilename(
						this.getClass().getSimpleName()+" ###.stl", 
						sketchPath+"/save"));
	}

	public void repulsor() {
		deform(true);
	}
	
	public void attractor() {
		deform(false);
	}

	void deform(boolean repulsor) {
		UVertexList vv=new UVertexList();
		float f[]=new float[5];
		for(int i=0; i<f.length; i++) {
			UVec3 pv;
			do {pv=grid.vert.v[(int)random(grid.vert.n)];} while(pv.y>100);
			vv.add(pv);
			f[i]=random(0.5f,1)*maxforce;
		}
		
		if(repulsor) grid=trans.repulsor(grid, vv, f);
		else grid=trans.attractor(grid, vv, f);
		
		grid.calcBounds();
	}


}
