package unlekker.modelbuilder;

import processing.core.PApplet;
import processing.core.PConstants;
import unlekker.modelbuilder.*;
import unlekker.util.Util;

public class ModelBuilder implements PConstants {
	public static final String MODELRECORDER="unlekker.modelbuilder.ModelRecorder";
	public PApplet p;
	
	public int modelNum;
	public Geometry model[];
	
	public ModelBuilder(PApplet _p) {
		p=_p;
		model=new Geometry[10];
	}

	public void add(Geometry m) {
		if(modelNum==model.length) 
			model=(Geometry [])Util.expandArray(model);
		
		model[modelNum++]=m;
	}

	public void draw(String name) {
		for(int i=0; i<modelNum; i++) 
			if(model[i].name.equals(name)) model[i].draw(p);
		
	}
	
	public static float triangleArea(Vec3 v1,Vec3 v2,Vec3 v3) {
		float p,a,b,c,val=0;
		
		// Heron's formula 
		// http://www.mathopenref.com/heronsformula.html
		a=Vec3.dist(v1, v2);
		b=Vec3.dist(v1,v3);
		c=Vec3.dist(v2,v3);
		p=(a+b+c)*0.5f;
		val=(float)Math.sqrt(p*(p-a)*(p-b)*(p-c));
		
		return val;
	}


	public void draw() {
		for(int i=0; i<modelNum; i++) model[i].draw(p);
	}
}
