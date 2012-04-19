package unlekker.modelbuilder;

import processing.core.PApplet;
import processing.core.PGraphics;
import unlekker.util.UConstants;
import unlekker.util.Util;


/**
 * Utility class to deal with a list of vertices. Provides methods for building and transforming vertex lists.
 * Most methods return a reference to self, allowing jQuery-style stringing 
 * of functions. Example:
 * 
 * <pre>UVertexList vl=new UVertexList();
 *vl.add(0,0,0).add(100,0,0);
 *vl.translate(0,100,0).scale(100).rotateX(PI);
 * </pre>
 * 
 * @author <a href="http://workshop.evolutionzone.com/">Marius Watz</a>
 *
 */
public class UVertexList implements UConstants {	
	/**
	 * Array of vertices. Grows automatically in size as vertices are added. May contain null values, always use <code>n</code> to get the current vertex count. 
	 */
	public UVec3 v[];
	/**
	 * Number of vertices in list.
	 */
	public int n=0;
	/**
	 * Boundng box. 
	 * Automatically calculated by the <code>setDimensions</code> 
	 * methods if <code>bb==null</code>. 
	 */
	public UBBox bb;
	public float w,d,h;

	/**
	 * Creates empty vertex list.
	 */
	public UVertexList() {
		v=new UVec3[100];
		n=0;
	}

	/**
	 * Create new UVertexList by copying the UVertexList given as parameter, leaving the input unchanged.  
	 * @param _vl UVertexList to copy.
	 */
	public UVertexList(UVertexList _vl) {
		n=0;
		v=new UVec3[_vl.n];
		for(int i=0; i<_vl.n; i++) add(_vl.v[i]);
	}

	/**
	 * Parses a VertexList from the string produced by VertexList.toDataString().
	 */
	public UVertexList(String in) {
		String [] tok=PApplet.split(in, "\t");
		v=new UVec3[tok.length];
		for(int i=0; i<tok.length; i++) add(UVec3.parse(tok[i]));		
	}

	/**
	 * Convenience method to calculate <code>res</code> vertices along a circle of <code>R</code> radius  
	 * @param R Radius of circle
	 * @param res Number of points to calculate
	 * @return
	 */
	public static UVertexList getCircle(float R,int res) {
		UVertexList vl=new UVertexList();
		float D=TWO_PI/(float)(res-1);
		for(int i=0; i<res; i++) 
			vl.add(PApplet.cos(D*(float)i)*R,
					PApplet.sin(D*(float)i)*R,
					0);
		
		return vl;
	}
	

	/*
	 *  Reorders a vertex list organized as a QUAD_STRIP duplets of vertices, so that it forms an outline path instead. The logic is as follows:
	 *  <pre>
	 *  int u=n/2;
	 *  UVertexList tmp=new UVertexList();
	 *  
	 *  for(int i=u-1; i>-1; i--) tmp.add(v[i*2]);
	 *  for(int i=0; i<u; i++) tmp.add(v[i*2+1]);
	 *  v=tmp.v;
	 *  n=tmp.n;
	 *  </pre> 
	 */
	public UVertexList QStoOutline() {
		int u=n/2;
		UVertexList tmp=new UVertexList();
		
		for(int i=u-1; i>-1; i--) tmp.add(v[i*2]);
		for(int i=0; i<u; i++) tmp.add(v[i*2+1]);

		v=tmp.v;
		n=tmp.n;
		
		return this;
	}
	
	/**
	 * Calls begin/endShape(QUAD_STRIP) and draws all vertices in the vertex list.
	 * @param p
	 */
	public void drawQuadStrip(PApplet p) {
		int id=0;

		p.beginShape(QUAD_STRIP);		
		int nn=n/2;

		for(int i=0; i<nn; i++) {						
			p.vertex(v[id].x,v[id].y,v[id++].z);
			p.vertex(v[id].x,v[id].y,v[id++].z);
		}
		p.endShape();		
	}

	/**
	 * Calls vertex() with all vertices in the vertex list. NOTE: begin/endShape() are not called.
	 * @param p
	 */
	public void drawVertices(PApplet p) {
		int id=0;

		for(int i=0; i<n; i++) {						
			p.vertex(v[id].x,v[id].y,v[id++].z);
		}
	}
	
	/**
	 * Calls beginShape() without parameters, then vertex() with all vertices in the vertex list before finally calling endShape().
	 * @param p
	 */
	public void draw(PApplet p) {
		int id=0;

		p.beginShape();		
		for(int i=0; i<n; i++) {						
			p.vertex(v[id].x,v[id].y,v[id++].z);
		}
		p.endShape();		
	}

	/**
	 * Calls beginShape() without parameters, then vertex() with all vertices in the vertex list before finally calling endShape().
	 * @param p
	 */
	public void draw(PGraphics p) {
		int id=0;

		p.beginShape();		
		for(int i=0; i<n; i++) {						
			p.vertex(v[id].x,v[id].y,v[id++].z);
		}
		p.endShape();		
	}

	/**
	 * Calculates bounding box
	 * @return Returns reference to self
	 */
	public UVertexList calcBounds() {
		if(bb==null) bb=new UBBox();
		bb.calc(this, true);
		
		return this;
 	}

	/**
	 * Translates all vertices by subtracting the centroid to each vertex, i.e. translate(-bb.c.x,-bb.c.y,-bb.c.z). Calculates bounding box only if not already calculated (<code>bb==null</code>). 
	 * @return Returns reference to self
	 */
	public UVertexList center() {
		if(bb==null) calcBounds();
		translate(-bb.c.x,-bb.c.y,-bb.c.z);
		return this;
	}

	/*
	 * Moves vertex list to origin by translating by its minimum bounds, i.e. translate(-bb.min.x,-bb.min.y,-bb.min.z). Calculates bounding box only if not already calculated (<code>bb==null</code>).
	 * @return Returns reference to self
	 */
	public UVertexList toOrigin() {
		if(bb==null) calcBounds();
		translate(-bb.min.x,-bb.min.y,-bb.min.z);
		return this;
	}

	/*
	 * Normalizes vertex list by calling scale(m/bb.maxDimension).
	 * @param m
	 * @return Returns reference to self 
	 */
	public UVertexList setDimensions(float m) {
		calcBounds();
		scale(m/bb.maxDimension);
		return this;
	}

	/*
	 * Calculates bounding box and normalizes vertex list using the maximum dimensions in XZ plane.
	 * @param m
	 * @return Returns reference to self 
	 */
	public UVertexList setDimensionsXZ(float m) {
		calcBounds();
		float dim=bb.sz.x;
		if(bb.sz.z>dim) dim=bb.sz.z;
		
		scale(m/dim);
		return this;
	}

	/*
	 * Calculates bounding box and normalizes vertex list using the maximum dimensions in XY plane.
	 * @param m
	 * @return Returns reference to self 
	 */
	public UVertexList setDimensionsXY(float m) {
		calcBounds();
		float dim=bb.sz.x;
		if(bb.sz.y>dim) dim=bb.sz.y;
		
		scale(m/dim);
		return this;
	}

	/*
	 * Calculates bounding box and normalizes vertex list using the maximum dimensions in YZ plane.
	 * @param m
	 * @return Returns reference to self 
	 */
	public UVertexList setDimensionsYZ(float m) {
		calcBounds();
		float dim=bb.sz.z;
		if(bb.sz.y>dim) dim=bb.sz.y;
		
		scale(m/dim);
		return this;
	}

	public UVertexList add(UVec3 _v) {
		if(n==v.length) v=(UVec3[])Util.expandArray(v);
		v[n++]=new UVec3(_v);
		return this;
	}

	public UVertexList add(UVec3 _v[],int _n) {
		for(int i=0; i<_n; i++) add(_v[i]);
		return this;
	}

/**
 * 	 Convenience method to add X,Y vertices	
 * @param x
 * @param y
 * @return Returns reference to self 
 */
	public UVertexList add(float x, float y) {
		add(x,y,0);		
		return this;
	}

	/**
	 * Adds x,y,z vertex to list
	 * @param x
	 * @param y
	 * @param z
	 * @return Returns reference to self 
	 */
	public UVertexList add(float x, float y,float z) {
		add(new UVec3(x,y,z));		
		return this;
	}
	
	/**
	 * Adds array of floats as vertices, treating values as XYZ triplets or XY duplets depending on whether vv.length%2==0 or vv.length%3==0.
	 * @param vv Array of XY duplets or XYZ triplets. 
	 * @return Returns reference to self 
	 */
	public UVertexList add(float[] vv) {
		int id=0,vn=vv.length;
		
		if(vn==2) add(vv[0],vv[1]);
		else if(vn==3) add(vv[0],vv[1],vv[2]);
		else {
			if(vn%2==0) {
				for(int i=0; i<vn/2; i++) add(vv[id++],vv[id++]);
			}
			else if(vn%3==0) {
				for(int i=0; i<vn/1; i++) add(vv[id++],vv[id++],vv[id++]);
			}
			else {
				Util.logErr(
						"VertexList.add(float[]): Unsure what to do with an array of "+
						vn+" positions.");
				Util.logErr("Expecting an array of length divisible by 2 or 3.");
			}
		}
		return this;
	}

	/**
	 * Adds quad of vertices (order: bottom left, bottom right, top right, top left). if q.length%2==0 the array data treated as 2D XY duplets.
	 * @param vv Array of XY duplets or XYZ triplets. 
	 * @return Returns reference to self 
	 */

	public UVertexList addQuad(float [] q) {
		int id=0;
		if(q.length==8) {
			add(q[id++],q[id++]);
			add(q[id++],q[id++]);
			add(q[id++],q[id++]);
			add(q[id++],q[id++]);
			add(q[0],q[1]);
		}
		else if(q.length==12) {
			add(q[id++],q[id++],q[id++]);
			add(q[id++],q[id++],q[id++]);
			add(q[id++],q[id++],q[id++]);
			add(q[id++],q[id++],q[id++]);
			add(q[0],q[1],q[2]);
		}
		return this;
	}

	/**
	 * Returns array of UVec3 objcts containing all vertices stored in this UVertexList.
	 * @return
	 */
	public UVec3[] getVertices() {
		UVec3 res[]=new UVec3[n];
		for(int i=0; i<n; i++) res[i]=new UVec3(v[i]);
		return res;
	}

	/**
	 * Returns array of UVec3 objcts containing an extract of vertices stored in this UVertexList, from the index given by start to the index given by end.   
	 * @param start
	 * @param end
	 * @return
	 */
	public UVec3[] getVertices(int start,int end) {
		UVec3 res[]=new UVec3[end-start];
		for(int i=start; i<end; i++) res[i-start]=new UVec3(v[i]);
		return res;
	}

	/////////////////////////////////////////////////////////
	// TRANSFORMATIONS
	
	/**
	 * Rotates vertices in list around X axis
	 * @param a Angle to rotate by.
	 * @return Returns reference to self 
	 */
	public UVertexList rotateX(float a) {
		for(int i=0; i<n; i++) v[i].rotateX(a);
		if(bb!=null) calcBounds();
		return this;
	}

	/**
	 * Rotates vertices in list around Y axis
	 * @param a Angle to rotate by.
	 * @return Returns reference to self 
	 */
	public UVertexList rotateY(float a) {
		for(int i=0; i<n; i++) v[i].rotateY(a);
		if(bb!=null) calcBounds();
		return this;
	}

	/**
	 * Rotates vertices in list around Z axis
	 * @param a Angle to rotate by.
	 * @return Returns reference to self 
	 */
	public UVertexList rotateZ(float a) {
		for(int i=0; i<n; i++) v[i].rotateZ(a);
		if(bb!=null) calcBounds();
		return this;
	}

	/**
	 * Translates all vertices by subtracting the values of _v from each vertex. 
	 * @param _v Vector to subtract
	 * @return
	 */
	public UVertexList translateNeg(UVec3 _v) {
		return translate(-_v.x,-_v.y,-_v.z);
	}

	/**
	 * Translates all vertices by adding the values of _v to each vertex. 
	 * @param _v Vector to add.
	 * @return
	 */
	public UVertexList translate(UVec3 _v) {
		for(int i=0; i<n; i++) v[i].add(_v);
		if(bb!=null) bb.translate(_v);
		return this;
	}

	/**
	 * Translates all vertices by adding x,y,z to each vertex. 
	 * @return
	 */
	public UVertexList translate(float x,float y,float z) {
		for(int i=0; i<n; i++) v[i].add(x,y,z);
		if(bb!=null) bb.translate(x,y,z);
		return this;
	}

	/**
	 * Translates all vertices by adding x,y to each vertex's XY coordinates. 
	 * @param _v Vector to subtract
	 * @return Returns reference to self 
	 */
	public UVertexList translate(float x,float y) {
		translate(x,y,0);
		return this;
	}

	/**
	 * Uniformly scales all vertices by multiplying with the value m. 
	 * @param m Factor to scale by.
	 * @return Returns reference to self 
	 */
	public UVertexList scale(float m) {
		for(int i=0; i<n; i++) v[i].mult(m);
		if(bb!=null) bb.scale(m);
		w*=m;
		h*=m;
		d*=m;
		return this;
	}

	/**
	 * Scales XYZ dimensions of all vertices by multiplying them with individual factors. 
	 * @param mx Factor to scale X values.
	 * @param my Factor to scale Y values.
	 * @param mz Factor to scale Z values.
	 * @return Returns reference to self 
	 */
	public UVertexList scale(float mx,float my,float mz) {
		for(int i=0; i<n; i++) v[i].mult(mx,my,mz);
		if(bb!=null) bb.scale(mx, my, mz);
		return this;
	}

	/**
	 * Calculates points along a 3D bezier curve, using the {@link  unlekker.modelbuilder.UBezier3D Bezier3D} class.  
	 * @param curvePts Array of 4 vertices to be treated as control points. 
	 * @param steps Number of vertices along the curve to calculate.
	 * @return New UVertexList with the calculated vertices 
	 */
	public static UVertexList calcBezier(UVertexList curvePts, int steps) {
		UVertexList res;
		UBezier3D bez=new UBezier3D(curvePts);
		bez.eval(steps);

		return bez.result;
	}

	/**
	 * Adds all vertices from another vertex list. All vertices are copied as new UVec3 instances, so changes in either vertex list will not affect the other  
	 * @param _vl Input vertex list
	 * @return Returns reference to self 
	 */
	public UVertexList add(UVertexList _vl) {
		for(int i=0; i<_vl.n; i++) add(_vl.v[i]);		
		return this;
	}

	/**
	 * Clears vertex list by setting <code>n=0</code> and <code>bb=null</code>.
	 * @return Returns reference to self 
	 */
	public UVertexList reset() {
		n=0;
		bb=null;
		return this;
	}

	public static UVertexList interpolate(float t,UVertexList vl1,UVertexList vl2) {
		UVertexList vl[]=new UVertexList[3];
		vl[0]=vl1;
		vl[1]=vl2;
		interpolate(t, vl);
		
		return vl[2];
	}

	public static UVertexList[] interpolate(float t,UVertexList vl[]) {
		float n;
		UVertexList vt=new UVertexList();
		UVec3 vv=new UVec3();
		
		n=vl[0].n;
		
		for(int i=0; i<n; i++) {
			vv.set(vl[0].v[i]);
			vv.add(
					(vl[1].v[i].x-vl[0].v[i].x)*t,
					(vl[1].v[i].y-vl[0].v[i].y)*t,
					(vl[1].v[i].z-vl[0].v[i].z)*t
					);
			vt.add(vv);
		}
		
		vl[2]=vt;
		
		return vl;
	}

	public String toString() {
		return toDataString();
	}

	public String toDataString() {
		String s="[";
		for(int i=0; i<n; i++) {
			s+=v[i].toString();
			if(i<n-1) s+=",";
		}
		return s+"]";
	}

	/**
	 * Reverse order of vertices, so that v[n-1] becomes v[0] etc.
	 * @return Returns reference to self 
	 */
	public UVertexList reverseOrder() {
		UVec3 u[]=new UVec3[n];
		for(int i=0; i<n; i++) u[i]=v[n-1-i];
		v=u;		
		return this;
	}

	public void set(UVertexList vlOld) {
		int oldn=n;
		n=0;
		
		if(oldn<vlOld.n) {
			for(int i=0; i<oldn; i++) v[i].set(vlOld.v[i]);
			for(int i=oldn; i<vlOld.n; i++) add(vlOld.v[i]);
		}
		else for(int i=0; i<oldn; i++) v[i].set(vlOld.v[i]);
		n=vlOld.n;		
		bb=null;
	}

}
