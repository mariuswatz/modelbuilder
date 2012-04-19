package unlekker.modelbuilder;

import processing.core.PApplet;
import unlekker.util.Util;

public class VertexList {	
	public Vec3 v[];
	public int n=0;
	public BBox bb;

	public VertexList() {
		v=new Vec3[100];
		n=0;
	}

	public VertexList(VertexList _vl) {
		n=0;
		v=new Vec3[_vl.n];
		for(int i=0; i<_vl.n; i++) add(_vl.v[i]);
	}

	public void calcBounds() {
		if(bb!=null) bb=new BBox();
		bb.calc(this);
 	}

	public void center() {
		if(bb==null) calcBounds();
		translate(-bb.centroid.x,-bb.centroid.y,-bb.centroid.z);
	}
	
	public void setDimensions(float m) {
		calcBounds();
		scale(m/bb.maxDimension);
	}

	public void setDimensionsXZ(float m) {
		calcBounds();
		float dim=bb.dim.x;
		if(bb.dim.z>dim) dim=bb.dim.z;
		
		scale(m/dim);
	}

	public void setDimensionsXY(float m) {
		calcBounds();
		float dim=bb.dim.x;
		if(bb.dim.y>dim) dim=bb.dim.y;
		
		scale(m/dim);
	}

	public void setDimensionsYZ(float m) {
		calcBounds();
		float dim=bb.dim.z;
		if(bb.dim.y>dim) dim=bb.dim.y;
		
		scale(m/dim);
	}

	public void add(Vec3 _v) {
		if(n==v.length) v=(Vec3[])Util.expandArray(v);
		v[n++]=new Vec3(_v);
	}

	public void add(float x,float y,float z) {
		add(new Vec3(x,y,z));
		
	}

	public void rotateX(float a) {
		for(int i=0; i<n; i++) v[i].rotateX(a);
	}

	public void rotateY(float a) {
		for(int i=0; i<n; i++) v[i].rotateY(a);
	}

	public void rotateZ(float a) {
		for(int i=0; i<n; i++) v[i].rotateZ(a);
	}

	public void translate(Vec3 _v) {
		for(int i=0; i<n; i++) v[i].add(_v);
	}

	public void translate(float x,float y,float z) {
		for(int i=0; i<n; i++) v[i].add(x,y,z);
	}
	
	public void scale(float m) {
		for(int i=0; i<n; i++) v[i].mult(m);
	}

	public void scale(float mx,float my,float mz) {
		for(int i=0; i<n; i++) v[i].mult(mx,my,mz);
	}

	public void vertexOut(PApplet p) {
		for(int i=0; i<n; i++) p.vertex(v[i].x, v[i].y, v[i].z);
	}
}
