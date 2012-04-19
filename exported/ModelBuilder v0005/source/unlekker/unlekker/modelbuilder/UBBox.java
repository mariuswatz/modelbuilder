package unlekker.modelbuilder;

import unlekker.util.Util;

public class UBBox {
	public UVec3 min,max,sz,c;
	public float maxDimension;
	
	public UBBox() {
		min=new UVec3();
		max=new UVec3();
		sz=new UVec3();
		c=new UVec3();				
		reset();
	}
	
	public void calc(UGeometry g) {
		reset();
		for(int i=0; i<g.faceNum; i++) {
			addPoint(g.face[i].v[0]);
			addPoint(g.face[i].v[1]);
			addPoint(g.face[i].v[2]);
		}
		
		for(int i=0; i<g.vln; i++) calc(g.vl[i], false);
		
		finishCalc();
		g.w=sz.x;
		g.h=sz.y;
		g.d=sz.z;
	}

	public void calc(UVertexList vl) {
		calc(vl,false);
	}

	public void calc(UVertexList vl, boolean doReset) {
		if(doReset) reset();
		for(int i=0; i<vl.n; i++) addPoint(vl.v[i]);
		finishCalc();
		vl.w=sz.x;
		vl.h=sz.y;
		vl.d=sz.z;
	}

	public void calc(UVertexList[] vl) {
		reset();
		for(int i=0; i<vl.length; i++) if(vl[i]!=null) {
			addPoint(vl[i].v,vl[i].n);
		}
		finishCalc();
	}

	public void finishCalc() {
		c.set(min).add(max).mult(0.5f);
		
		sz.set(max).sub(min);
		maxDimension=sz.x;
  	if(sz.y>maxDimension) maxDimension=sz.y;
  	if(sz.z>maxDimension) maxDimension=sz.z;
	}
	
	public void addBBox(UBBox bb) {
		min.x= min.x > bb.min.x ? bb.min.x : min.x; 
		min.y= min.y > bb.min.y ? bb.min.y : min.y; 
		min.z= min.z > bb.min.z ? bb.min.z : min.z; 
		max.x= max.x < bb.max.x ? bb.max.x : max.x; 
		max.y= max.y < bb.max.y ? bb.max.y : max.y; 
		max.z= max.z < bb.max.z ? bb.max.z : max.z; 
	}
	
	public void add(UVec3 v) {
		addPoint(v);		
	}

	private void addPoint(UVec3 v) {
		if(v.x<min.x) min.x=v.x;
		if(v.y<min.y) min.y=v.y;
		if(v.z<min.z) min.z=v.z;
		if(v.x>max.x) max.x=v.x;
		if(v.y>max.y) max.y=v.y;
		if(v.z>max.z) max.z=v.z;
	}
	
	public void addPoint(UVec3[] v, int n) {
		for(int i=0; i<n; i++) addPoint(v[i]);		
	}

	
	public void add(UVertexList vl) {
		for(int i=0; i<vl.n; i++) addPoint(vl.v[i]);
	}


	public void reset() {
		min.set(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
		max.set(Float.MIN_VALUE,Float.MIN_VALUE,Float.MIN_VALUE);
	}
	
	public String toString() {
		String s="BB: c="+c.toString()+
		" min="+min.toString()+
		" max="+max.toString();
		
		return s;
	}

	public void translate(float x, float y, float z) {
		c.add(x,y,z);
		min.add(x,y,z);
		max.add(x,y,z);		
		finishCalc();
	}

	public void translate(UVec3 v) {
		c.add(v);
		min.add(v);
		max.add(v);		
		finishCalc();
	}

	public void scale(float mx,float my,float mz) {
		c.mult(mx,my,mz);
		min.mult(mx,my,mz);
		max.mult(mx,my,mz);		
		finishCalc();
	}

	public void scale(float m) {
		c.mult(m);
		min.mult(m);
		max.mult(m);		
		finishCalc();
	}

}
