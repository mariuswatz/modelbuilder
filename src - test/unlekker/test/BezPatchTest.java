package unlekker.test;

import processing.core.*;
import processing.opengl.*;
import unlekker.modelbuilder.*;

public class BezPatchTest extends PApplet {
	UNav3D nav;
	UBezierPatch bez;
	
	public void setup() {
		size(800,800,OPENGL);
		nav=new UNav3D(this).setTranslation(width/2, height/2,0);
		
		build();
	}
	
	private void build() {
		UVec3 cp[][]=new UVec3[(int)random(2,5)*2+1][(int)random(2,5)*2+1];
		int resu=cp.length,resv=cp[0].length;
		for(int i=0; i<resu; i++)
			for (int j=0; j<resv; j++) {
				cp[i][j]=new UVec3(i-resu/2,random(-0.5f,0.5f)*(float)resu,j-resv/2);
				cp[i][j].z*=0.95f*(float)i/(float)(resu-1)+0.05f;
			}
		for(int i=0; i<resu; i++) {
			float t=(float)i/(float)(resu-1);
			for (int j=0; j<resv; j++) {
				float tt=(float)j/(float)(resv-1);
				cp[i][j]=new UVec3(t*10,random(-0.5f,0.5f)*(float)resu,sin(t*PI)*10*(tt));
				if(j>=resv/2) {
//					println(j+" "+(resv/2)+" "+" "+(resv/2-(j-resv/2+1)));
//					cp[i][j].y=cp[i][resv/2-(j-resv/2)].y;
				}
//				cp[i][j].z*=0.95f*+0.05f;
			}		
		}
		
		cp=new UVec3[4][4];
		for(int i=0; i<16; i++) cp[i/4][i%4]=new UVec3((float)(i/4)-1.5f,0,(float)(i%4)-1.5f);
		cp[1][1].y=2;
		cp[1][2].y=2;
		cp[0][0].z=0;
		cp[0][1].z=0;
		cp[0][2].z=0;
		cp[0][3].z=0;
		cp[3][0].z=0;
		cp[3][1].z=0;
		cp[3][2].z=0;
		cp[3][3].z=0;
		cp[1][0].z=-3;
		cp[2][0].set(cp[3][0].x+0.05f,cp[2][0].y,-3);
		cp[2][3].set(cp[3][3].x+0.05f,cp[2][3].y,3);
			
		
		bez=new UBezierPatch(cp);
		bez.eval(10*resu, 10*resv);
		bez.geo.setDimensions(500);
		bez.geo.writeSTL(this, "BezPatchTest.stl");
		
		UGeometry testHandedness=new UGeometry();
		testHandedness.beginShape(QUAD_STRIP);
		for(int i=0; i<5; i++) {
			testHandedness.vertex(i*5,0,0).vertex(i*5,5,0);
		}
		testHandedness.endShape().writeSTL(this, "testHandedness.stl");
	}

	public void keyPressed() {
		if(key==' ') build();
	}
	
	public void draw() {
		background(0);
		lights();
		nav.doTransforms();
		
		fill(255);
		noStroke();
		bez.geo.draw(this);
	}
	
	static public void main(String args[]) {
		// PApplet.main(new String[] { "unlekker006default.AppDefault" });
		PApplet.main(new String[] { "unlekker.test.BezPatchTest" });

	}

}
