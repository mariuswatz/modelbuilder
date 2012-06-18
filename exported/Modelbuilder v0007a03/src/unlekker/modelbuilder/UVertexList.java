package unlekker.modelbuilder;

import processing.core.PApplet;
import processing.core.PGraphics;
import unlekker.util.*;


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
	public UVec3 v[],vtan[],rad[];
	/**
	 * Number of vertices in list.
	 */
	public int n=0;
	/**
	 * Array containing vertex colors (if any)
	 */
	public int vertexCol[],currCol;
	/**
	 * Boundng box. 
	 * Automatically calculated by the <code>setDimensions</code> 
	 * methods if <code>bb==null</code>. 
	 */
	public UBBox bb;
	public boolean doColor,doNoDuplicates,isClosed;
	
	private float ptDist[];

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
		set(_vl);
//		UUtil.log("vl "+n+" "+_vl.n+" "+_vl.toDataString());
	}

	/**
	 * Parses a VertexList from the string produced by VertexList.toDataString().
	 */
	public UVertexList(String in) {
//		UUtil.log("UVertexList(String in) "+in);
		in=UUtil.chopBraces(in).replaceAll(">,<", ">\t<");
		String [] tok=PApplet.split(in, "\t");
//		UUtil.log(Str.toString(tok));
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

	/**
	 * Creates and returns an array of n empty UVertexLists.
	 * @param n
	 * @return
	 */
	public static UVertexList [] getVertexLists(int n) {
		UVertexList [] vl=new UVertexList[n];
		for(int i=0; i<n; i++) vl[i]=new UVertexList();
		return vl;
	}

	public static UVertexList mergeToQS(UVertexList vl1,UVertexList vl2) {
		UVertexList tmp=new UVertexList();
		for(int i=0; i<vl1.n; i++) {
			tmp.add(vl1.v[i]);
			tmp.add(vl2.v[i]);
		}
		return tmp;
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
		return QStoOutline(false);
	}

	public UVertexList QStoOutline(boolean reverse) {
		int u=n/2;
		UVertexList tmp=new UVertexList();
		
		if(reverse) {
			for(int i=0; i<u; i++) tmp.add(v[i*2+1]);
			for(int i=u-1; i>-1; i--) tmp.add(v[i*2]);
		}
		else {
			for(int i=u-1; i>-1; i--) tmp.add(v[i*2]);
			for(int i=0; i<u; i++) tmp.add(v[i*2+1]);
		}

		
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
		draw(p.g);
	}

	/**
	 * Calls beginShape() without parameters, then vertex() with all vertices in the vertex list before finally calling endShape().
	 * @param p
	 */
	public void draw(PGraphics p) {
		int id=0;

		if(p.getClass().getSimpleName().equals("PGraphicsJava2D")) {
			p.beginShape();		
			for(int i=0; i<n; i++) {						
				p.vertex(v[id].x,v[id++].y);
			}
			p.endShape();		
			
			return;
		}
		
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
	 * Calculates bounding box
	 * @return Returns reference to self
	 */
	public float[] calcDistances() {
		if(ptDist==null || ptDist.length!=n) ptDist=new float[n];
		
		for(int i=0; i<n-1; i++) ptDist[i]=v[i].distanceTo(v[i+1]);

		return ptDist;
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

	/**
	 * Calculates the actual vector length of all vertices, then
	 * returns the vertex closest to the distance represented by
	 * <code>distanceTravelled > t*getLength()</code>. <code>getPointAtFraction</code> does 
	 * not interpolate between two points to find the precise result, but 
	 * rather returns the first point that fulfills the above criteria. 
	 *  
	 * @param t
	 * @return
	 */
	public UVec3 getPointAtFraction(float t) {
		UVec3 vv=new UVec3();
		
		float len=getLength();
		t=len*t;
		len=0;
		
		for(int i=0; i<n; i++) {
			len+=ptDist[i];
			vv=v[i];
			
			if(len>t) return vv;
		}
				
		return vv;
	}

	/**
	 * Calculates the actual vector length of all vertices, then
	 * returns the ID of the vertex closest to the distance represented by
	 * <code>distanceTravelled > t*getLength()</code>. <code>getPointAtFraction</code> does 
	 * not interpolate between two points to find the precise result, but 
	 * rather returns the first point that fulfills the above criteria. 
	 *  
	 * @param t
	 * @return
	 */
	public int getPointIDAtFraction(float t) {
		int id=0;
		
		float len=getLength();
		t=len*t;
		len=0;
		
		for(int i=0; i<n; i++) {
			len+=ptDist[i];
			id=i;
			
			if(len>t) return id;
		}
				
		return id;
	}
	
	public UVec3 getPointInterpolated(int id1,int id2, float t) {
		return UVec3.interpolate(v[id1], v[id2], t);
	}

	/** 
	 * Returns length of the path represented by this UVertexList, calculated by adding up the distances between all the vertices it contains.
	 * @return Total length of vertex list
	 */
	public float getLength() {
		float dist=0;

		if(ptDist==null || ptDist.length!=n) calcDistances();
		for(int i=0; i<n; i++) dist+=ptDist[i];

		return dist;
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

	public UVertexList add(UVec3 _v,int _col) {
		currCol=_col;
		return add(_v);
	}

	public UVertexList addAt(int id,float x,float y,float z) {
		if(n==v.length) {
			v=(UVec3[])UUtil.expandArray(v);
			if(doColor) vertexCol=UUtil.expandArray(vertexCol, v.length);
		}
		
		System.arraycopy(v, id, v, id+1, n-id);
		v[id]=new UVec3(x,y,z);				
		
		return this;
	}

	public UVertexList addAt(int id,UVec3 vv) {
		return addAt(id,vv.x,vv.y,vv.z);
	}
	
	public UVertexList addAtStart(float x,float y,float z) {
		return addAt(0,x,y,z);
	}
	
	public UVertexList addAtStart(UVec3 _v) {
		return addAt(0,_v);
//		if(n==v.length) {
//			v=(UVec3[])UUtil.expandArray(v);
//			if(doColor) vertexCol=UUtil.expandArray(vertexCol, v.length);
//		}
//		
//		System.arraycopy(v, 0, v, 1, n);
//		v[0]=new UVec3(_v);
//		n++;
//		return this;
	}
	
	/**
	 * Adds vertex to list, first checking to see if the vertex is already in the 
	 * list. If it is the function returns the existing position in the v array,
	 * if not it adds the vertex and returns the last position in the array.
	 * @param _v Vertex to add
	 * @return Position of vertex in v array
	 */
	public int addGetID(UVec3 _v) {
		
		// check to see if already in list
		int id=-1;
		if(doNoDuplicates) {
			id=getID(_v);
			if(id!=-1) return id;
		}
		
		// not found in list, return
		add(_v);
		return n-1;
	}
	
	public UVertexList add(UVec3 _v) {
		if(doNoDuplicates) {
			for(int i=0; i<n; i++) if(v[i].cmp(_v)) {
				UUtil.log("Duplicate");
				return this;
			}
		}
		
		if(n==v.length) {
			v=(UVec3[])UUtil.expandArray(v);
			if(doColor) vertexCol=UUtil.expandArray(vertexCol, v.length);
		}
		
		if(doColor) vertexCol[n]=currCol;
		v[n++]=new UVec3(_v);
		
		return this;
	}

	public UVertexList add(UVec3 _v[],int _n) {
		for(int i=0; i<_n; i++) add(_v[i]);
		return this;
	}

	public UVertexList insert(int _id,UVec3 v) {
		return remove(0,1);
	}

	public UVertexList removeFirst() {
		return remove(0,1);
	}

	public UVertexList removeLast() {
		n--;
		return this;
	}

	/**
	 * Removes a number of entries in vertex list.
	 * @param _id
	 * @param _num
	 * @return
	 */
	public UVertexList remove(int _id,int _num) {
		if(_id>n-1 || (_id+_num)>n-1) {
			UUtil.logErr("Invalid UVertexList.remove() call. id="+
					_id+" num="+_num+", actual n = "+n);
			return null;
		}
		System.arraycopy(v, _id, v, _id+_num, _num);
		n-=_num;
		return this;
	}

	public int[][] removeDuplicates() {
		int[][] id; // id[0][0..n-1] are old IDs, id[1][0..n-1] are new IDs
		UVertexList vlnew=new UVertexList().noDuplicates();
		id=new int[2][n];

		int dupes=0;
		for(int i=0; i<n; i++) {
			id[0][i]=i;
			id[1][i]=vlnew.addGetID(v[i]);
			UUtil.log(i+" "+id[0][i]+" "+id[1][i]);
			if(id[1][i]!=i) dupes++;
		}
		
		UUtil.log("UVertexList.removeDuplicates() - "+dupes+" duplicates found.");
		UUtil.log("Vertices - before: "+n+" after: "+vlnew.n);
		
		v=vlnew.v;
		n=vlnew.n;
		return id;
	}
	
	/**
	 * Sets flag to not allow duplicate vertices (this is not retro-active, existing duplicates will remain) 
	 * @return
	 */
	public UVertexList noDuplicates() {
		doNoDuplicates=true;
		return this;
	}

	/**
	 * Sets flag to allow duplicate vertices 
	 * @return
	 */
	public UVertexList allowDuplicates() {
		doNoDuplicates=false;
		return this;
	}

	public UVertexList addMidPoints() {
		UVertexList nv=new UVertexList();
		
		for(int i=0; i<n-1; i++) {
			nv.add(v[i]);
			if(v[i].distanceTo(v[i+1])>0.0001f) 
				nv.add(UVec3.interpolate(v[i], v[i+1], 0.5f));
		}
		nv.add(last());
		
		bb=null;
		n=nv.n;
		v=nv.v;
		
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
	 * Adds array of floats as vertices, treating values as XYZ triplets or XY duplets depending on whether the <code>isXYZ</code> parameter is <code>true</code>.
	 * @param isXYZ Flag indicating whether array is in XY or XYZ format
	 * @param vv Array of XY or XYZ coordinates
	 * @return Returns reference to self 
	 */
	public UVertexList add(float[] vv, boolean isXYZ) {
		int id=0,vn=vv.length;
		
		if(vn==2) add(vv[0],vv[1]);
		else if(vn==3) add(vv[0],vv[1],vv[2]);
		else {
			if(isXYZ) {
				for(int i=0; i<vn/3; i++) add(vv[id++],vv[id++],vv[id++]);
			}
			else {
				for(int i=0; i<vn/2; i++) add(vv[id++],vv[id++]);
			}
		}
		return this;
	}

	/**
	 * TODO Function for storing vertex color - not yet supported
	 * @param col
	 */
	public void vertexColor(int col[]) {
		if(!doColor) {
			doColor=true;
			vertexCol=new int[v.length];
		}
		System.arraycopy(col, 0, vertexCol, 0, col.length);
	}
	
	/**
	 * TODO Function for storing vertex color - not yet supported
	 * @param col
	 */
	public void vertexColor(int col) {
		if(!doColor) {
			doColor=true;
			vertexCol=new int[v.length];
			if(n>0) {
				UUtil.log("vertexColor activated on non-empty vertex list. Current color has been retroactively added to existing vertices.");
			}
		}
		currCol=col;
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

	public UVertexList addEllipseXZ(float rad,int detail) {
		for(int i=0; i<detail; i++) add(
				new UVec3(rad,0,0).
				rotateY(PApplet.map(i,0,detail,0,TWO_PI)));
		close();
		return this;
	}
	
	public UVertexList addArcXZ(float x,float y,float z, float rad,float start,float end,int numSteps) {
		UVec3 cp=new UVec3(x,y,z),pos=new UVec3(rad,0,0);
		
//		add(pos.rotateY(start));
		pos.rotateY(start);
		end=(end-start)/(numSteps);
		
		for(int i=0; i<numSteps; i++) {
			pos.rotateY(end);
			add(pos);
		}
		add(first());
		
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
	
	public UVertexList getTangents2D() {		
		return getTangents2D(0, n);
	}

	public UVertexList getTangents2D(int start,int end) {
		vtan=new UVec3[end-start];
		for(int i=start; i<end; i++) vtan[i-start]=new UVec3(v[i]);
		for(int i=start; i<end-1; i++) {
			vtan[i].set(v[i+1]).sub(v[i]);
			vtan[i].z=0;
//			vtan[i].set(-vtan[i].y,vtan[i].x);
			vtan[i].rotateZ(-HALF_PI).norm();
			vtan[i].norm();
		}
		vtan[end-1].set(vtan[end-2]).norm();
		
//		int nn=vtan.length;
//		UVec3[] tmp=new UVec3[nn];
//		for(int i=0; i<nn; i++) {
//			if(isClosed) {
//				if(i==0) tmp[0]=new UVec3(vtan[0]).add(vtan[nn-1]).add(vtan[1]).div(3);
//				else if(i==nn-1) tmp[i]=new UVec3(vtan[i]).add(vtan[i-1]).add(vtan[0]).div(3);
//				else tmp[i]=new UVec3(vtan[i]).add(vtan[i-1]).add(vtan[i+1]).div(3);
//			}
//			else {
//				if(i==0) tmp[0]=new UVec3(vtan[0]).add(vtan[i+1]).div(2);
//				else if(i==nn-1) tmp[i]=new UVec3(vtan[i]).add(vtan[i-1]).div(2);
//				else tmp[i]=new UVec3(vtan[i]).add(vtan[i-1]).add(vtan[i+1]).div(3);
//			}
//
//		}
//		
//		vtan=tmp;
		
		return this;
	}
	
	public void drawTangents(PApplet p,float len) {
		if(vtan==null) return;
		p.beginShape(p.LINES);
		for(int i=0; i<n; i++) {
			p.vertex(v[i].x,v[i].y,v[i].z);
			p.vertex(v[i].x+vtan[i].x*len,v[i].y+vtan[i].y*len,v[i].z+vtan[i].z*len);
		}
		p.endShape();
		
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
	 * Trims vertex array so that it is of n length, as it can otherwise have trailing empty
	 * positions. 
	 * @return
	 */
	public UVertexList trimEmpty() {
		if(v.length>n) v=(UVec3 [])UUtil.resizeArray(v, n);
		return this;
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

	/**
	 * Interpolates between two vertex lists (vl1 and vl2) given a parameter t=0..1. Assumes that the vertex lists 
	 * are of identical length, no error checking is performed.
	 * @param t
	 * @param vl1
	 * @param vl2
	 * @return
	 */
	public static UVertexList interpolate(float t,UVertexList vl1,UVertexList vl2) {
		UVertexList vl[]=new UVertexList[3];
		vl[0]=vl1;
		vl[1]=vl2;
		interpolate(t, vl);
		
		return vl[2];
	}

	/**
	 * Interpolates between two vertex lists (vl[0] and vl[1]) given a parameter t=0..1. Assumes that the vertex lists 
	 * are of identical length, no error checking is performed.
	 * @param t
	 * @param vl
	 * @return
	 */
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

	public UVertexList expand2D(float offs) {
		UVertexList tmp=new UVertexList();
		if(vtan==null) getTangents2D();
		
		for(int i=0; i<n; i++) 
			tmp.add(new UVec3(vtan[i]).mult(offs).add(v[i]));
		
		return tmp;
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

	public UVertexList set(UVertexList vlOld) {
		int oldn=n;

		v=null;
		
		/* TODO */
		if(v==null) {
			v=new UVec3[100];
			for(int i=0; i<vlOld.n; i++) add(vlOld.v[i]);	
		}
		else if(oldn<vlOld.n) {
//			for(int i=0; i<oldn; i++) v[i].set(vlOld.v[i]);
			for(int i=oldn; i<vlOld.n; i++) add(vlOld.v[i]);
		}
		else for(int i=0; i<oldn; i++) v[i].set(vlOld.v[i]);
		
		n=vlOld.n;		
		bb=null;

		if(vlOld.doColor) {
			doColor=true;
			vertexCol=new int[v.length];
			System.arraycopy(vlOld.vertexCol, 0, vertexCol, 0, n);
		}
		
		return this;
	}

	/**
	 * Get ID of UVec3 in UVec3 array "v"
	 * Returns -1 if no match is found
	 * @param vv
	 * @return
	 */
	public int getID(UVec3 vv) {
		for(int i=0; i<n; i++) if(vv.cmp(v[i])) return i;
		
		// no match found
		return -1;
	}

	/**
	 * Closes vertex list by adding a copy of the first vertex in the list.
	 * @return
	 */
	public UVertexList close() {
		return add(v[0]);		
	}

	public UVertexList add(UVec3[] vv) {
		for(int i=0; i<vv.length; i++) add(vv[i]);
		return this;
	}

	/**
	 * Returns first vertex in vertex list
	 * @return
	 */
	public UVec3 first() {
		// TODO Auto-generated method stub
		if(n<1) return null;
		return v[0];
	}

	/**
	 * Returns last vertex in vertex list
	 * @return
	 */
	public UVec3 last() {
		// TODO Auto-generated method stub
		if(n<1) return null;
		return v[n-1];
	}

}
