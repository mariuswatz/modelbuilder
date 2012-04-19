package unlekker.modelbuilder;

import processing.core.PApplet;
import unlekker.util.Util;

public class Primitive {
	public static int SPHERE=1,BOX=2,CYL=3,CONE=4,DISC=5;
	public int type;
	
	public static Geometry getSphere(float rad,int detail) {
		Geometry g=new Geometry();
		
		return g;
	}
	
	/*
	//////////////////////////////////////
	// STATIC UTILITY FUNCTIONS
	
	public static void cylinderDetail(int num) {
	  preCylNum=num;
	  preCylX=new float[num];
	  preCylY=new float[num];

	  float deg=(360/(float)num);
	  for(int i=0; i<num; i++) {
	    preCylX[i]=Util.COS((float)i*deg);
	    preCylY[i]=Util.SIN((float)i*deg);
	  }
	}

	public static void cylinder(PApplet p,float x,float y,float z, float w,float h) {
	  // checking to see if cylinderDetail() has been
	  // called yet
	  int next;
	  if(preCylX==null) cylinderDetail(12);  
	  
	  p.pushMatrix(); // save current world matrix
	  p.translate(x,y,z); 
	  
	  p.beginShape(TRIANGLES);
	  for(int i=0; i<preCylNum; i++) {
	  	next=(i+1)%preCylNum;
	  	p.vertex(preCylX[i]*w, h, preCylY[i]*w);
		  p.vertex(0,h,0);
	  	p.vertex(preCylX[next]*w, h, preCylY[next]*w);
	  }
	  p.endShape();

	  p.beginShape(TRIANGLES);
	  for(int i=preCylNum-1; i>-1; i--) {
	  	next=i-1;
	  	if(next<0) next=preCylNum-1;
	  	p.vertex(preCylX[next]*w, -h, preCylY[next]*w);
		  p.vertex(0,-h,0);
	  	p.vertex(preCylX[i]*w, -h, preCylY[i]*w);
	  }
	  p.endShape();
	  
	  p.beginShape(TRIANGLES);
	  for(int i=0; i<preCylNum; i++) {
	  	next=(i+1)%preCylNum;
	  	
	  	p.vertex(preCylX[i]*w, -h, preCylY[i]*w);
	  	p.vertex(preCylX[i]*w, h, preCylY[i]*w);
	  	p.vertex(preCylX[next]*w, h, preCylY[next]*w);

	  	p.vertex(preCylX[i]*w, -h, preCylY[i]*w);
	  	p.vertex(preCylX[next]*w, h, preCylY[next]*w);
	  	p.vertex(preCylX[next]*w, -h, preCylY[next]*w);
	  }
	  p.endShape();

	  p.popMatrix(); // restore saved world matrix
	}
	
	public static void hollowCylinder(PApplet p,float x,float y,float z, float r1,float r2,float h) {
		hollowCylinderArc(p,x,y,z,0,360,r1,r2,h);
	}

	public static void hollowCylinderArc(PApplet p,float x,float y,float z, 
			float deg1,float deg2, float r1,float r2,float h) {

		int num=preCylNum;
		float cx[],cy[];

		if(num==0) {
			cylinderDetail(12);
			num=preCylNum;
		}
		
	  cx=new float[num];
	  cy=new float[num];

//	  Util.log("hollowCylinderArc: "+deg1+" "+deg2);
	  if(deg1<0) deg1+=360;
	  if(deg2<0) deg2+=360;
//	  Util.log("hollowCylinderArc: "+deg1+" "+deg2+" (corrected)");
	  
	  if(deg1>deg2) {
	  	float tmp=deg2;
	  	deg2=deg1;
	  	deg1=tmp;
	  }
	  
	  float deg=((deg2-deg1)/(float)(num-1));
	  int deglut;
	  for(int i=0; i<num; i++) {
	  	deglut=(int)(((float)i*deg+deg1)*Util.SINCOS_INVPREC);
	  	deglut=deglut % Util.SINCOS_LENGTH;
	    cx[i]=Util.cosLUT[deglut];
	    cy[i]=-Util.sinLUT[deglut];
	    
	    cx[i]=(float)Math.cos(((float)i*deg+deg1)*DEG_TO_RAD);
	    cy[i]=-(float)Math.sin(((float)i*deg+deg1)*DEG_TO_RAD);
	  }

		
		p.pushMatrix();
		p.translate(x,y,z);

	
	  p.beginShape(TRIANGLES);
	  for(int i=0; i<preCylNum-1; i++) {
	  	p.vertex(cx[i]*r2, h, cy[i]*r2);
	  	p.vertex(cx[i]*r2, -h, cy[i]*r2);
	  	p.vertex(cx[i+1]*r2, -h, cy[i+1]*r2);

	  	p.vertex(cx[i+1]*r2, -h, cy[i+1]*r2);
	  	p.vertex(cx[i+1]*r2, h, cy[i+1]*r2);
	  	p.vertex(cx[i]*r2, h, cy[i]*r2);
	  }
	  p.endShape();

	  p.beginShape(TRIANGLES);
	  for(int i=preCylNum-1; i>0; i--) {
	  	p.vertex(cx[i-1]*r2, h, cy[i-1]*r2);
	  	p.vertex(cx[i]*r2, h, cy[i]*r2);
	  	p.vertex(cx[i]*r1, h, cy[i]*r1);

	  	p.vertex(cx[i]*r1, h, cy[i]*r1);
	  	p.vertex(cx[i-1]*r1, h, cy[i-1]*r1);
	  	p.vertex(cx[i-1]*r2, h, cy[i-1]*r2);
	  }
	  p.endShape();

	  p.beginShape(TRIANGLES);
	  for(int i=preCylNum-1; i>0; i--) {
	  	p.vertex(cx[i]*r1, -h, cy[i]*r1);
	  	p.vertex(cx[i]*r2, -h, cy[i]*r2);
	  	p.vertex(cx[i-1]*r2, -h, cy[i-1]*r2);

	  	p.vertex(cx[i-1]*r2, -h, cy[i-1]*r2);
	  	p.vertex(cx[i-1]*r1, -h, cy[i-1]*r1);
	  	p.vertex(cx[i]*r1, -h, cy[i]*r1);
	  }
	  p.endShape();

	  p.beginShape(TRIANGLES);
	  for(int i=0; i<preCylNum-1; i++) {
	  	p.vertex(cx[i+1]*r1, -h, cy[i+1]*r1);
	  	p.vertex(cx[i]*r1, -h, cy[i]*r1);
	  	p.vertex(cx[i]*r1, h, cy[i]*r1);

	  	p.vertex(cx[i]*r1, h, cy[i]*r1);
	  	p.vertex(cx[i+1]*r1, h, cy[i+1]*r1);
	  	p.vertex(cx[i+1]*r1, -h, cy[i+1]*r1);
	  }
	  p.endShape();
		
	  p.popMatrix();
	}

	public static void cone(PApplet p,float x,float y,float z, 
	  float w,float w2,float h) {
	  // checking to see if cylinderDetail() has been
	  // called yet
	  if(preCylX==null) cylinderDetail(12);  
	  
	  p.pushMatrix(); // save current world matrix
	  p.translate(x,y,z); 
	  
	  p.beginShape(TRIANGLE_FAN);
	  p.vertex(0,h,0);
	  for(int i=0; i<preCylNum; i++) 
	  	p.vertex(preCylX[i]*w, h, preCylY[i]*w); 

	  // provide last vertex to come full circle  
	  p.vertex(preCylX[0]*w, h, preCylY[0]*w);
	  p.endShape();

	  p.beginShape(TRIANGLE_FAN);
	  p.vertex(0,-h,0);
	  for(int i=0; i<preCylNum; i++)
	  	p.vertex(preCylX[i]*w2, -h, preCylY[i]*w2); 

	  // provide last vertex to come full circle  
	  p.vertex(preCylX[0]*w2, -h, preCylY[0]*w2);
	  p.endShape();
	  
	  p.beginShape(QUAD_STRIP);
	  for(int i=0; i<preCylNum; i++) {
	  	p.vertex(preCylX[i]*w2, -h, preCylY[i]*w2);
	  	p.vertex(preCylX[i]*w, h, preCylY[i]*w);
	  }
	  p.vertex(preCylX[0]*w2, -h, preCylY[0]*w2);
	  p.vertex(preCylX[0]*w, h, preCylY[0]*w);
	  p.endShape();
	  
	  p.popMatrix(); // restore saved world matrix
	}	
	 */
}
