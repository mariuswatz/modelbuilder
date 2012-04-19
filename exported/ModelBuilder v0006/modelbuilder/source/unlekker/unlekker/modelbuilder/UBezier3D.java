package unlekker.modelbuilder;

import unlekker.util.*;

public class UBezier3D {
	public UVertexList vl,result;
	public int numSeg;
	
	public UBezier3D() {
		set(null);
	}
	
	public UBezier3D(UVertexList _vl) {
		set(_vl);
	}
	
	public UBezier3D set(UVertexList _vl) {
		vl=_vl;
		if(vl==null) return null;
		numSeg=(vl.n-1)/3;
		
		return this;
	}
	
	public void eval(int steps) {
		float ct,ctsq,tsq,t,t0,t1,t2,t3;
		UVec3 v1=new UVec3(),v2=new UVec3(),
			v3=new UVec3(),v4=new UVec3(),
			res=new UVec3();
		
		result=new UVertexList();

		for(int j=0; j<numSeg; j++) {
			for(int i=0; i<steps; i++) {
				t=(float)i/(float)(steps-1);
				ct=1f-t;
				ctsq=ct*ct;
				tsq=t*t;

				t0=ctsq*ct;
				t1=ctsq*t;
				t2=ct*tsq;
				t3=t*tsq;

				int id=j*3;
				v1=vl.v[id];
				v2=vl.v[id+1];
				v3=vl.v[id+2];
				v4=vl.v[id+3];
				
				res.set(
						 v1.x*t0+3*v2.x*t1+3*v3.x*t2+v4.x*t3,
						 v1.y*t0+3*v2.y*t1+3*v3.y*t2+v4.y*t3,
						 v1.z*t0+3*v2.z*t1+3*v3.z*t2+v4.z*t3);
				result.add(res);
			}
		}
	}

	/**
	 * Based on code by Golan Levin:
	 * http://www.flong.com/blog/2009/bezier-approximation-of-a-circular-arc-in-processing/
	 * @return
	 */
	public static UBezier3D arcApproximation() {
		UBezier3D bez=null;
		
		return bez;
	}
	
	/*
//--------------------------
// Global variables: 
// The true coordinates of the Bezier control points:
float px0,py0;
float px1,py1;
float px2,py2;
float px3,py3;
float radius = 200; // radius of the circular arc
float cx = 300; // center point of the circular arc
float cy = 300;
 
//--------------------------
void draw(){
  background(230);
 
  // Establish arc parameters.
  // (Note: assert theta != TWO_PI)
  float theta = radians(mouseX/3.0); // spread of the arc.
  float startAngle = radians(mouseY/8.0); // as in arc()
  float endAngle = startAngle + theta;    // as in arc()
 
  // Compute raw Bezier coordinates.
  float x0 = cos(theta/2.0);
  float y0 = sin(theta/2.0);
  float x3 = x0;
  float y3 = 0-y0;
  float x1 = (4.0-x0)/3.0;
  float y1 = ((1.0-x0)*(3.0-x0))/(3.0*y0); // y0 != 0...
  float x2 = x1;
  float y2 = 0-y1;
 
  // Compute rotationally-offset Bezier coordinates, using:
  // x' = cos(angle) * x - sin(angle) * y;
  // y' = sin(angle) * x + cos(angle) * y;
  float bezAng = startAngle + theta/2.0;
  float cBezAng = cos(bezAng);
  float sBezAng = sin(bezAng);
  float rx0 = cBezAng * x0 - sBezAng * y0;
  float ry0 = sBezAng * x0 + cBezAng * y0;
  float rx1 = cBezAng * x1 - sBezAng * y1;
  float ry1 = sBezAng * x1 + cBezAng * y1;
  float rx2 = cBezAng * x2 - sBezAng * y2;
  float ry2 = sBezAng * x2 + cBezAng * y2;
  float rx3 = cBezAng * x3 - sBezAng * y3;
  float ry3 = sBezAng * x3 + cBezAng * y3;
 
  // Compute scaled and translated Bezier coordinates.
  px0 = cx + radius*rx0;
  py0 = cy + radius*ry0;
  px1 = cx + radius*rx1;
  py1 = cy + radius*ry1;
  px2 = cx + radius*rx2;
  py2 = cy + radius*ry2;
  px3 = cx + radius*rx3;
  py3 = cy + radius*ry3;
 
  // Draw the Bezier control points.
  stroke(0,0,0, 64);
  fill  (0,0,0, 64);
  ellipse(px0,py0, 8,8);
  ellipse(px1,py1, 8,8);
  ellipse(px2,py2, 8,8);
  ellipse(px3,py3, 8,8);
  line (cx,cy,   px0,py0);
  line (px0,py0, px1,py1);
  line (px1,py1, px2,py2);
  line (px2,py2, px3,py3);
  line (px3,py3,   cx,cy);
 
  //--------------------------
  // BLUE IS THE "TRUE" CIRULAR ARC:
  noFill();
  stroke(0,0,180, 128);
  arc(cx, cy, radius*2, radius*2, startAngle, endAngle);
 
  //--------------------------
  // RED IS THE BEZIER APPROXIMATION OF THE CIRCULAR ARC:
  noFill();
  stroke(255,0,0, 128);
  bezier(px0,py0, px1,py1, px2,py2, px3,py3);
 
}	 */
	
	/*
	public void eval(int steps) {
		float ct,ctsq,tsq,t,t0,t1,t2,t3;
		UVec3 res=new UVec3();
		
		result=new UVertexList();

		for(int i=0; i<steps; i++) {
			t=(float)i/(float)(steps-1);
			ct=1f-t;
			ctsq=ct*ct;
			tsq=t*t;

			t0=ctsq*ct;
			t1=ctsq*t;
			t2=ct*tsq;
			t3=t*tsq;

			res.set(
					 vl.v[0].x*t0+3*vl.v[1].x*t1+3*vl.v[2].x*t2+vl.v[3].x*t3,
					 vl.v[0].y*t0+3*vl.v[1].y*t1+3*vl.v[2].y*t2+vl.v[3].y*t3,
					 vl.v[0].z*t0+3*vl.v[1].z*t1+3*vl.v[2].z*t2+vl.v[3].z*t3);
			result.add(res);
		}
	}*/

}
