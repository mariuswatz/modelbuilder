package unlekker.test;

import java.lang.reflect.Method;

import processing.core.PApplet;
import unlekker.util.*;

public class AppTest extends PApplet {
	UApp app;
	float x=500,y=500;
	USimpleGUI gui;
	
	public void setup() {
		app.setup();
//		size(app.appWidth,app.appHeight,app.appRenderer);
		gui=new USimpleGUI(this);
	}
	
	public void init() {
		app=new UApp(this);
		app.loadConfig("AppTest.conf");
//		app.conf.listProperties();
		
		app.setupWindow(1024, 768, -1024, 0, OPENGL, false);
		app.init();
		
		super.init();
	}
	
	public void draw() {
		background(255);
		
		ellipse(x,y,50,50);
	}
	
	public void keyPressed() {
		println(key+" "+keyCode+" "+app.keyIsALTKey()+" "+app.keyIsCTRLKey()+" "+app.keyIsSHIFTKey());
	}
	
	public void mouseWheelMoved(float e) {
		y+=e;
	}
}
