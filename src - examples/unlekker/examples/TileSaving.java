package unlekker.examples;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.modelbuilder.filter.UTransformDeform;
import unlekker.util.*;

public class TileSaving extends PApplet {

	boolean doRender=false,doUpdate=true;
	
	public UTileSaver tiler;
	UNav3D nav;
	USimpleGUI gui;
	UGeometry obj[];
	
	float rotX,rotY,zoomZ;
	
	int tiles=4;

	public void setup() {
		
		// NOTE: Currently UTileSaver only works properly with square 
		// aspect ratios. Hopefully this will be fixed soon.
		size(600,600,OPENGL);
		
	  nav=new UNav3D(this);
	  nav.setTranslation(width/2,height/2,0);
	  
	  gui=new USimpleGUI(this);
	  gui.addToggle("doUpdate", doUpdate);
	  gui.addSlider("tiles", tiles, 2, 10);
	  gui.addButton("render");
	  gui.addButton("build");
	  gui.setLayout(false);
	  
	  nav.setGUI(gui);

		build();
	}
	
	public void draw() {
		if(doRender) renderStart();
		else update();
		
		background(0);
		lights();

		pushMatrix();
		nav.doTransforms();		
		drawObjects();		
		popMatrix();
		
		if(doRender) renderEnd();

		gui.draw();
	}

	void renderEnd() {
		tiler.post();
		if(!tiler.isTiling) {
			doRender=false;
			tiler=null;
			gui.enable();
			nav.enable();
		}
	}

	void drawObjects() {
//		translate(0,0,zoomZ);
		rotateX(rotX);
		rotateY(rotY);
				
		for(int i=0; i<obj.length; i++) {
			// calculate a fraction to determine color
			float colFract=map(i,0,obj.length-1,0,1);
						
			if(colFract<0.5f)
				fill(
						UColorTool.interpolate(colFract*2,
						"FFFF00", "00FFFF"));
			else fill(
					UColorTool.interpolate((colFract-0.5f)*2,
					"FF0000", "FF6600"));

			obj[i].draw(this);
		}
	}

	public void build() {
		obj=new UGeometry[(int)random(10,100)];
		
		for(int i=0; i<obj.length; i++) {
			float h=random(100,250);	
			if(random(100)>85) h*=2;
			
			obj[i]=
					UPrimitive.cylinderGrid(random(20,50), h, 6, 12, true);
			
			// center
			obj[i].
				calcBounds().
				translate(0,h*0.5f,0);
			
			// apply bend transform
			new UTransformDeform().
				taper(-0.75f).
				bend(radians(random(30,90)/3)).
				transform(obj[i]);
			
			obj[i].
				rotateZ(random(TWO_PI)).
				rotateX(random(TWO_PI));
		}
		
	}


	void render() {
		doRender=true;
		
		// disable interactive controls
		gui.disable();
		nav.disable();
	}
	
	void renderStart() {
		// init tiler if tiler==null
		if(tiler==null) {
			String filename=
				UIO.getIncrementalFilename(
  				this.getClass().getSimpleName()+" ####.png", 
  				savePath("render"));
		
		  tiler=new UTileSaver(this, tiles, filename);
		}
		
		tiler.pre();
	}

	// For tile saving to work correctly you need to separate drawing 
	// and updating logic. When tiling update() should not be called.
	void update() {
		if(!doUpdate) return;
		
		rotX+=radians(1);
		rotY+=radians(0.5f);
		
		// silly trick to zoom in and out using sin() to
		// interpolate, using frameCount as input
		zoomZ=300*sin(map(frameCount%300,0,299, 0, TWO_PI));
	}

	public void keyPressed() {
		
	}
}
