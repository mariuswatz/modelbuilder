package unlekker.test;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;
import unlekker.util.*;

public class ColorToolTest extends PApplet {
	UColorTool colors;
	UVertexList vl;
	
	public void setup() {
		size(400,400,OPENGL);
		initColors();
		
		vl=new UVertexList();
		for(int i=0; i<50; i++) vl.add(new UVec3(random(width),random(100,height)));
	}
	
	public void draw() {
		background(50);
		lights();

		colors.drawColors(this, 20,20);

		for(int i=0; i<50; i++) {
			fill(colors.colors[i % colors.n]);
			ellipse(vl.v[i].x,vl.v[i].y, i*2+5,i*2+5);
		}
		
	}

	public void keyPressed() {
		initColors();
		
	}
	public void initColors() {
		colors=new UColorTool();
		colors.add(0,255,255).add(0,50,100).add("FF0099");
		colors.addGradient(3,10, colors.toColor(255,0,0), colors.toColor(255,200,0));
		colors.addGradient(3,10, colors.toColor(255,255,255), colors.toColor(255,255,0));
		colors.addGradient(3,10, colors.toColor(0,100,200), colors.toColor(0,255,255));
		colors.addGradient(3,10, colors.toColor(100,200,0), colors.toColor(0,50,0));
		
		// generate at least 10 colors, with a 30% chance of skipping a gradient
		colors.generateColors(10,30);
		
		String s=colors.toDataString();
		UUtil.log(s);
		
		colors=UColorTool.parse(s);
	}


  static public void main(String args[]) {
    PApplet.main(new String[] { "unlekker.test.ColorToolTest" }); 
  }

}
