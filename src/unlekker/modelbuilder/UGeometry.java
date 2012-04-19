package unlekker.modelbuilder;

/*
 * TODO
 * 
 * - UQuad and UFace primarily based on vertex indices
 * - UQuad descendant of UFace
 * - Fix UGeometry.set()
 * - UQuadList stores only quad indices
 * 
 */
/**
 * Utility class to deal with mesh geometries. It uses the PApplet's <code>beginShape() / vertex() / endShape()</code> to build meshes.
 * A secondary function of the class is to act as a collection of 
 * {@link unlekker.modelbuilder.UVertexList UVertexList} objects, for 
 * instance to perform transformations. 
 *  
 * @author <a href="http://workshop.evolutionzone.com">Marius Watz</a> (portfolio: <a href="http://mariuswatz.com">mariuswatz.com</a>
 */

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import processing.core.PApplet;
import processing.core.PConstants;
import unlekker.util.*;

public class UGeometry implements UConstants {
	public static String NONAME="No name";
	
	protected boolean checkForDuplicates=false;
	public String name;
	
	private int bvCnt,bvCol[],shapeType=-1;
	private UVec3 bv[];

	/**
	 * List of faces.
	 */
	public UFace face[];
	/**
	 * Current number of faces.
	 */
	public int faceNum;
	/**
	 * Array of vertex lists that are stored in this <code>UGeometry</code> object. This is only intended as a convenient
	 * way to collect vertex lists, for instance to perform collective transformations on them. The vertex lists may be drawn
	 * to screen using <code>drawVertexLists()</code>.
	 */
	public UVertexList vl[];
	
	/**
	 * Vertex list containing all vertices used in this object. Manipulating 
	 * vertices directly will change the geometry.
	 */
	public UVertexList vert=new UVertexList();
	
	public UQuad quad[];
	public int quadNum;

	public UStrip strip[];
	public int stripNum;

	/**
	 * Number of vertex lists stored in this object.
	 */
	public int vln;
	/**
	 * Boundng box. 
	 */
	public UBBox bb;
	public float w,d,h;
	
	/** 
	 * Create unnamed instance.
	 */
	public UGeometry() {
		name=NONAME;
	}

	/** 
	 * Create named instance.
	 */
	public UGeometry(String _name) {
		name=_name;
	}
	
	/** 
	 * Create copy of existing UGeometry object.
	 */
	public UGeometry(UGeometry _g) {
		set(_g);
	}
	
	/**
	 * Sets the contents of this UGeometry object by 
	 * copying the input geometry.  
	 * @param _g
	 */
	public void set(UGeometry _g) {
		reset();
		name=_g.name;

		if(_g.faceNum>0) {
			for(int i=0; i<_g.faceNum; i++) addFace(_g.face[i].getVertices());
			if(_g.bb!=null) calcBounds();
		}
//		UUtil.log("Added "+faceNum+" faces.");
		
		if(_g.vln>0) {			
			for(int i=0; i<_g.vln; i++) addVertexList(new UVertexList(_g.vl[i]));
		}
		calcBounds();
	}

	/**
	 * Resets geometry to empty.
	 */	
	public void reset() {
		faceNum=0;
		vln=0;
		bb=null;
    bvCnt=0;
    quadNum=0;
    stripNum=0;
    vert.reset();
	}

	/**
	 * Adds QUAD_STRIP of faces from a list of vertex pairs.
	 * @param vl Array of vertex pairs
	 * @param reverseOrder Build in reverse order?
	 */
	public UGeometry quadStrip(UVertexList vl) {
		return quadStrip(vl, false);
	}
	
	/**
	 * Adds QUAD_STRIP of faces from a list of vertex pairs.
	 * @param vl Array of vertex pairs
	 * @param reverseOrder Build in reverse order?
	 */
	public UGeometry quadStrip(UVertexList vl, boolean reverseOrder) {
		int id=0,numv=vl.n/2;

		beginShape(QUAD_STRIP);
		if(reverseOrder) {
			id=vl.n-2;
			for(int j=0; j<numv; j++) {
				vertex(vl.v[id]);
				vertex(vl.v[id+1]);
				id-=2;
			}
		}
		else for(int j=0; j<numv; j++) {
			vertex(vl.v[id++]);
			vertex(vl.v[id++]);
		}
		
		endShape();
		return this;
	}

	/**
	 * Adds QUAD_STRIP of faces built from two vertex lists. 
	 * @param v1 First edge of QUAD_STRIP
	 * @param v2 Second edge of QUAD_STRIP
	 */
	public UGeometry quadStrip(UVertexList v1,UVertexList v2) {
		return quadStrip(v1,v2,false);
	}
	
	/**
	 * Adds QUAD_STRIP of faces built from two vertex lists. 
	 * @param v1 First edge of QUAD_STRIP
	 * @param v2 Second edge of QUAD_STRIP
	 * @param reverse Build in reverse order?
	 */
	public UGeometry quadStrip(UVertexList v1,UVertexList v2,boolean reverse) {
		int numv=v1.n;
		int id=0;

		if(v1.doColor && v2.doColor) { // render with vertex color
//			UUtil.log("render with vertex color");
			beginShape(QUAD_STRIP);
			if(reverse) {
				id=numv-1;
				for(int j=0; j<numv; j++) {
					vertex(v1.v[id],v1.vertexCol[id]);
					vertex(v2.v[id],v2.vertexCol[id]);
					id--;
				}
			}
			else {
				id=0;
				for(int j=0; j<numv; j++) {
					vertex(v1.v[id],v1.vertexCol[id]);
					vertex(v2.v[id],v2.vertexCol[id]);
					id++;
				}
			}
			endShape();
		}
		else {
//			UUtil.log("don't render with vertex color");
			beginShape(QUAD_STRIP);
			if(reverse) {
				id=numv-1;
				for(int j=0; j<numv; j++) {
					vertex(v1.v[id]);
					vertex(v2.v[id]);
					id--;
				}
			}
			else {
				id=0;
				for(int j=0; j<numv; j++) {
					vertex(v1.v[id]);
					vertex(v2.v[id]);
					id++;
				}
			}
			endShape();
		}
		
		return this;
	}
	
	/**
	 * Adds a mesh of QUAD_STRIPs built from an array of vertex lists.
	 * Each vertex list is treated as a single edge and will be connected to the next edge in the array.
	 * All vertex lists must have the same number of vertices. 
	 * @param vl Array of vertex lists to be used as edges.
	 */
	public UGeometry quadStrip(UVertexList vl[]) {
		return quadStrip(vl,vl.length);
	}
		
	/**
	 * Adds a mesh of QUAD_STRIPs built from an array of vertex lists.
	 * Each vertex list is treated as a single edge and will be connected to the next edge in the array.
	 * All vertex lists must have the same number of vertices. 
	 * @param vl Array of vertex lists to be used as edges.
	 * @param vln Number of lists to use from array
	 */
	public UGeometry quadStrip(UVertexList vl[],int vln) {
		if(vln<2) return this;
		int numv=vl[0].n;

		for(int i=0; i<vln-1; i++) {
			quadStrip(vl[i], vl[i+1]);
		}		
		
		return this;
	}
	
	/**
	 * Adds a TRIANGLE_FAN constructed from input vertex list. If useCentroid==true then a centroid is calculated 
	 * and used as the center point for the fan, if not the first vertex in the vertex list is used. 
	 * @param vl Array of vertex pairs
	 * @param useCentroid Build from calculated centroid?
	 * @param reverseOrder Build in reverse order?
	 */
	public void triangleFan(UVertexList vl,boolean useCentroid,boolean reverseOrder) {
		UVec3 c=new UVec3();
		int vstart=0;
		
		if(vl.n<3) return;
		
		if(useCentroid) {
			for(int i=0; i<vl.n; i++) c.add(vl.v[i]);
			c.div(vl.n);
		}		
		else {
			c=vl.v[0];
			vstart=1;
		}
		
		beginShape(TRIANGLE_FAN);
		vertex(c);
		if(reverseOrder) {
			for(int i=vl.n-1; i>vstart-1; i--) vertex(vl.v[i]);			
		}
		else {
			for(int i=vstart; i<vl.n; i++) vertex(vl.v[i]);
		}
		endShape();
		
	}
	
	/**
	 * Sweep a 2D profile along a 2D path to make a 3D sweeped object. 
	 * Assumes a UVertexList path in the XZ plane and a profile in the XY plane.
	 * @param prof
	 * @param path
	 * @return
	 */
	public UGeometry sweepXZ(UVertexList prof,UVertexList path) {
		float a[]=new float[path.n];
		UVec3 vv=new UVec3();
		UVertexList pp[]=new UVertexList[path.n];
		
		for(int i=0; i<path.n-1; i++) {
			vv.set(path.v[i+1]).sub(path.v[i]);//.rotateY(-HALF_PI);
			vv.set(-vv.z,vv.x).norm();

			a[i]=-vv.angle2D();
		}
		
		a[path.n-1]=a[path.n-2];
		
		for(int i=0; i<path.n; i++) {
			pp[i]=new UVertexList(prof);
			pp[i].rotateY(-a[i]).translate(path.v[i]);
		}

		for(int i=0; i<path.n-1; i++) {
			beginShape(QUAD_STRIP);
			for(int j=0; j<prof.n; j++) {
				vertex(pp[i].v[j]);
				vertex(pp[i+1].v[j]);
			}
			endShape();
		}

		return this;
	}
	
	///////////////////////////////////////////////////
	// BEGINSHAPE / ENDSHAPE METHODS
	
	
	/**
	 * Starts building a new series of faces, using the same logic 
	 * as <a href="http://processing.org/reference/beginShape_.html">PApplet.beginShape()</a>.
	 * Currently supports the following types: TRIANGLE_FAN, TRIANGLE_STRIP, TRIANGLES, QUADS, QUAD_STRIP
	 * 
	 * While shape is being built vertices are stored in a temporary 
	 * array, and only the ones that are used are copied to the vert vertexlist.
	 * @param _type Shape type (TRIANGLE_FAN, TRIANGLE_STRIP, TRIANGLES, QUADS, QUAD_STRIP)
	 */
	public UGeometry beginShape(int _type) {
		bvCnt=0;
		if(bv==null) bv=new UVec3[100];
		if(face==null) face=new UFace[100];

		shapeType=_type;
		return this;
	}

	public UGeometry endShape() {
    switch (shapeType) {
      case TRIANGLE_FAN: {
      	UStrip s=new UStrip(TRIANGLE_FAN, 
      			this,(UVec3 [])UUtil.expandArray(bv, bvCnt));
      	add(s);
//        int stop = bvCnt - 1;
//        for (int i =0; i<stop; i++) {
////          addFace(bv[0], bv[i+1],bv[i]);
//        	addFace(0, i+1, i);
//        }
      }
      break;

      case TRIANGLES: {
        int stop = bvCnt - 2;
        for (int i = 0; i < stop; i += 3) 
//        	addFace(bv[i], bv[i+2], bv[i+1]);
        	addFace(new UVec3[] {bv[i], bv[i+2], bv[i+1]});
      }
      break;

      case TRIANGLE_STRIP: {
        int stop = bvCnt - 2;
        for (int i = 0; i < stop; i++) {
        	// HANDED-NESS ISSUE
//        	if(i%2==1) addFace(bv[i], bv[i+2], bv[i+1]);
//        	else addFace(bv[i], bv[i+1], bv[i+2]);
        	if(i%2==1) addFace(new UVec3[] {bv[i], bv[i+2], bv[i+1]});
        	else addFace(new UVec3[] {bv[i], bv[i+1], bv[i+2]});
        }
      }
      break;

      // Processing order: bottom left,bottom right,top right,top left
//      addTriangle(i, i+1, i+2);
//      addTriangle(i, i+2, i+3);
      case QUADS: {
        int stop = bvCnt-3;
        for (int i = 0; i < stop; i += 4) {
        	addFace(new UVec3[] {bv[i],bv[i+3],bv[i+1],bv[i+2]});
        }
      }
      break;

      case QUAD_STRIP: {
//      	UUtil.log("QUAD_STRIP "+bvCnt+" "+UUtil.toString(bv));
      	UStrip s=new UStrip(QUAD_STRIP, 
    			this,(UVec3 [])UUtil.expandArray(bv, bvCnt));
//        	// HANDED-NESS ISSUE
////        	addFace(i+3, i+1, i+0);
////        	addFace(i, i+2, i+3);
//        }
        add(s);
      }
      break;

      case POLYGON:{
      	UUtil.log("beginShape(POLYGON) currently unsupported.");
//      	UUtil.log("beginShape(POLYGON) "+bvCnt+" "+bv.length);
//      	if(bvCnt!=bv.length) bv=(Vec3 [])UUtil.resizeArray(bv, bvCnt);
//      	
//      	Vec3 tri[]=Triangulate.triangulate(bv); 
//      	if(tri!=null && tri.length>0) {
//      		int id=0;
//      		for(int i=0; i<tri.length/3; i++) {
//      			addFace(tri[id++],tri[id++],tri[id++]);
//      		}
//
//      	}
//        addPolygonTriangles();
      }
      break;
    }
    bvCnt=0;

//		UUtil.log("Faces: "+faceNum);
		return this;

	}
	
	/**
	 * Add vertex to shape being built by <code>beginShape() / endShape()</code>
	 * @param x
	 * @param y
	 * @param z
	 * @return 
	 */
	public UGeometry vertex(float x,float y,float z) {
		vertex(new UVec3(x,y,z));
		return this;
	}

	/**
	 * Add UVec3 vertex to shape being built by <code>beginShape() / endShape()</code>
	 * The vertex information is copied, leaving the original UVec3 instance unchanged.
	 * @param v
	 * @return 
	 */
	public UGeometry vertex(UVec3 v) {
		if(bv.length==bvCnt) bv=(UVec3 [])UUtil.expandArray(bv);
		bv[bvCnt]=new UVec3(v);
		bvCnt++;
		return this;
	}

	public UGeometry vertex(UVec3 v, int col) {
		if(bvCol==null) bvCol=new int[bv.length];
		vertex(v);
		if(bvCol.length==bvCnt) bvCol=UUtil.expandArray(bvCol); 
		bvCol[bvCnt-1]=col;
		return this;
	}

	/**
	 * Add vertex list to shape being built by <code>beginShape() / endShape()</code>. 
	 * All vertices are copied, leaving the original instances unchanged.
	 * @param vl 
	 * @param reverseOrder Add in reverse order?
	 * @return 
	 */
	public UGeometry vertex(UVertexList vl,boolean reverseOrder) {
		vertex(vl.v,vl.n, reverseOrder);
		return this;
	}

	/**
	 * Adds vertex list to shape being built by <code>beginShape() / endShape()</code>. 
	 * All vertices are copied, leaving the original instances unchanged.
	 * @return 
	 */
	public UGeometry vertex(UVertexList vl) {
		return vertex(vl.v,vl.n, false);
	}

	/**
	 * Adds array of UVec3 vertices to shape being built by <code>beginShape() / endShape()</code>. 
	 * All objects are copied, leaving the original instances unchanged.
	 * @param _v Array of vertices
	 * @param nv Number of vertices to add
	 * @return 
	 */
	public UGeometry vertex(UVec3 _v[],int nv) {
		return vertex(_v,nv,false);
	}

	/**
	 * Adds array of UVec3 vertices to shape being built by <code>beginShape() / endShape()</code>. 
	 * All objects are copied, leaving the original instances unchanged.
	 * @param _v Array of vertices
	 * @param nv Number of vertices to add
	 * @param reverseOrder Add in reverse order?
	 */
	public UGeometry vertex(UVec3 _v[],int nv,boolean reverseOrder) {
		if(bv.length<bvCnt+nv) 
			bv=(UVec3 [])UUtil.expandArray(bv,bvCnt+nv+100);		
		
		if(reverseOrder) {
			for(int i=nv-1; i>-1; i--) bv[bvCnt++]=new UVec3(_v[i]);
		}
		else {
			for(int i=0; i<nv; i++) bv[bvCnt++]=new UVec3(_v[i]);
		}
		return this;
	}

	
	/**
	 * Adds single face or quad. If <code>vv.length==3</code> a UFace will be
	 * added, otherwise a UQuad will be added.
	 * @param vv Array of 3 or 4 vertices, depending on whether face is a triangle
	 * or quad.
	 * @return  
	 */
	public int addFace(UVec3 vv[]) {
		int type=TRIANGLE;
		if(vv.length==4) type=QUAD;
		
		int id[]=addVerticesToMasterList(vv);
		
		if(type==TRIANGLE) {
			if(face==null) face=new UFace[100];
			if(face.length==faceNum) face=(UFace[])UUtil.expandArray(face);
			face[faceNum++]=new UFace(this,id);
			return faceNum-1;
		}
		else {
			if(quad==null) quad=new UQuad[100];
			if(quad.length==quadNum) quad=(UQuad[])UUtil.expandArray(quad);
			quad[quadNum++]=new UQuad(this,vv);
			return quadNum-1;
		}


	}

	/**
	 * Adds a UVertexList object to the <code>vl</code> array. The object 
	 * is stored by reference, so any changes made to it through this class will
	 * also affect the original instance.  
	 * Not to be confused with {@link #vertex(UVertexList vl)}  
	 * @param vv 
	 * @return
	 */
	public UGeometry addVertexList(UVertexList vv) {
		if(vl==null) vl=new UVertexList[10];
		else if(vln==vl.length) vl=(UVertexList[])UUtil.expandArray(vl);
		vl[vln++]=vv;
		
		return this;
	}
	
	public void removeDuplicateFaces() {
		boolean ok;
		for(int i=1; i<faceNum; i++) {
			for(int j=i; j<faceNum; j++) {
				if(i!=j && face[i].compareTo(face[j])==0) {
					UUtil.log("Duplicate found."+face[j].toString());
					face[j].translate(0, 0, UUtil.rnd.random(-100, 100));
				}
			}
		}
	}
	
	/**
	 * Adds all the faces of a UGeometry instance. The faces is copied and the original instance is left unchanged. 
	 * @param g UGeometry to add
	 * @return
	 */
	public UGeometry add(UGeometry g) {
		for(int i=0; i<g.faceNum; i++) addFace(g.face[i].getVertices());
		
		return this;
	}
	
	/**
	 * Adds single vertex to master vertex list and returns the generated
	 * vertex ID.
	 * @param v
	 * @return Vertex ID
	 */
	public int addVertexToMasterList(UVec3 vv) {
		if(vert==null) vert=new UVertexList();
		int id=vert.addGetID(vv);
		return id;
	}
	
	/**
	 * Adds array of vertices to master vertex list and returns the generated
	 * vertex IDs
	 * @param v
	 * @return Array of vertex IDs
	 */
	public int [] addVerticesToMasterList(UVec3 vv[]) {
		if(vert==null) vert=new UVertexList();
		int id[]=new int[vv.length];
		for(int i=0; i<vv.length; i++) id[i]=vert.addGetID(vv[i]);
		return id;
	}
	
	/**
	 * Add single face by copying vertices from a UFace instance
	 * @param f
	 */
	public void add(UFace f) {
		if(f==null) return;
		addFace(f.getVertices());		
	}
	
	public void add(UFace[] f) {
		if(f==null) return;
		for(int i=0; i<f.length; i++) if(f[i]!=null) add(f[i]);
	}

	
	public void add(UQuad uQuad) {
		add(uQuad.f1);		
		add(uQuad.f2);		
		
		if(quad==null) quad=new UQuad[100];
		if(quad.length==quadNum) quad=(UQuad [])UUtil.expandArray(quad);
		quad[quadNum++]=uQuad;
	}


	/**
	 * Draws all faces contained in this UGeometry object.
	 * @param p Reference to PApplet instance to draw into
	 */

	public void draw(PApplet p) {
		UFace f;
		int fid=0;
		UVec3 vv;

		p.beginShape(TRIANGLES);		
		for(int i=0; i<faceNum; i++) {			
			f=face[i];
			fid=0;
			vv=vert.v[f.vid[fid++]];
			p.vertex(vv.x,vv.y,vv.z);
			vv=vert.v[f.vid[fid++]];
			p.vertex(vv.x,vv.y,vv.z);
			vv=vert.v[f.vid[fid]];
			p.vertex(vv.x,vv.y,vv.z);
		}
		p.endShape();		
	}

/*	public void drawColor(PApplet p) {
		UFace f;
		int fid=0,id=0;
		float vArr[];

		p.beginShape(TRIANGLES);		
		for(int i=0; i<faceNum; i++) {			
			f=face[i];
			if(f.vArr==null) f.calcVertexArray();
			vArr=f.vArr;
			fid=0;
			if(f.c==null) { 
				p.vertex(vArr[fid++],vArr[fid++],vArr[fid++]);
				p.vertex(vArr[fid++],vArr[fid++],vArr[fid++]);
				p.vertex(vArr[fid++],vArr[fid++],vArr[fid++]);
			}
			else { 
				p.fill(f.c[0]);
				p.vertex(vArr[fid++],vArr[fid++],vArr[fid++]);
				p.fill(f.c[1]);
				p.vertex(vArr[fid++],vArr[fid++],vArr[fid++]);
				p.fill(f.c[2]);
				p.vertex(vArr[fid++],vArr[fid++],vArr[fid++]);
			}

		}
		p.endShape();		
	}*/
	
	/**
	 * Static convenience function to draw a quad strip to screen from
	 * two UVertexList objects.
	 * @param p
	 * @param vl1
	 * @param vl2
	 */
	public static void drawQuadstrip(PApplet p,UVertexList vl1,UVertexList vl2) {
		if(p.g.getClass().getSimpleName().indexOf("Java2D")!=-1) {
			p.beginShape(QUAD_STRIP);
			for(int i=0; i<vl1.n; i++) {
				p.vertex(vl1.v[i].x,vl1.v[i].y);
				p.vertex(vl2.v[i].x,vl2.v[i].y);			
			}
			p.endShape();
		}
		else { // is 3D
			p.beginShape(QUAD_STRIP);
			for(int i=0; i<vl1.n; i++) {
				p.vertex(vl1.v[i].x,vl1.v[i].y,vl1.v[i].z);
				p.vertex(vl2.v[i].x,vl2.v[i].y,vl2.v[i].z);			
			}
			p.endShape();
		}
	}

	/////////////////////////////////////////////////////////
	// TRANSFORMATIONS

	public UGeometry rotateX(float a) {
		vert.rotateX(a);
		for(int i=0; i<vln; i++) vl[i].rotateX(a);
		if(bb!=null) calcBounds();
		return this;
	}

	public UGeometry rotateY(float a) {
		vert.rotateY(a);
		for(int i=0; i<vln; i++) vl[i].rotateY(a);
		if(bb!=null) calcBounds();
		return this;
	}

	public UGeometry rotateZ(float a) {
		vert.rotateZ(a);
		for(int i=0; i<vln; i++) vl[i].rotateZ(a);
		if(bb!=null) calcBounds();
		return this;
	}

	public UGeometry translate(float x,float y,float z) {
		vert.translate(x,y,z);
		for(int i=0; i<vln; i++) vl[i].translate(x,y,z);
		if(bb!=null) bb.translate(x, y, z);
		return this;
	}
	
	public UGeometry translate(UVec3 vv) {
		translate(vv.x,vv.y,vv.z);
		return this;
	}

	/**
	 * Calculates bounding box and translates the mesh to origin by calling <code>translate(-bb.min.x,-bb.min.y,-bb.min.z);</code>
	 */
	public UGeometry toOrigin() {
		if(bb==null) calcBounds();
		translate(-bb.min.x,-bb.min.y,-bb.min.z);
		return this;
	}


	public UGeometry scale(float m) {
		scale(m,m,m);
		if(bb!=null) bb.scale(m,m,m);
		return this;
	}

	public UGeometry scale(float mx,float my,float mz) {
		vert.scale(mx,my,mz);
		for(int i=0; i<vln; i++) vl[i].scale(mx,my,mz);
		if(bb!=null) bb.scale(mx,my,mz);
		return this;
	}

	/**
	 * Convenience method to produce a copy of this UGeometry instance. 
	 * @return Copy of UGeometry
	 */
	public UGeometry getCopy() {
		UGeometry g;
		
		g=new UGeometry(this);
		return g;
	}


	public static UGeometry extrudeNonFlat(
			UVertexList vl1,UVertexList vl2,
			float z,boolean reverse){
		UGeometry o1=null,o2;
		UVertexList vl[]=new UVertexList[4];
		UVertexList n1=new UVertexList();
		UVertexList n2=new UVertexList();
		UVec3 n=new UVec3();
		
		z*=0.5f;
		
		for(int i=0; i<vl1.n; i++) {
			if(i==0) {
				n1.add(UVec3.calcFaceNormal(vl1.v[1], vl1.v[0],vl2.v[0]).mult(1));
				n2.add(UVec3.calcFaceNormal(vl2.v[1], vl2.v[0], vl1.v[1]).mult(1));
			}
//			else if(i==vl1.n-1) {
//				n1.add(UVec3.calcFaceNormal(vl1.v[i], vl1.v[i-1],vl2.v[i]));
//				n2.add(UVec3.calcFaceNormal(vl2.v[i], vl2.v[i-1], vl1.v[1]));				
//			}
			else {
				n=UVec3.calcFaceNormal(vl1.v[i], vl1.v[i-1],vl2.v[i]);
				n1.add(n);
				n=UVec3.calcFaceNormal(vl2.v[i], vl2.v[i-1], vl1.v[i]);
				n2.add(n);
			}
		}
		
		for(int i=0; i<4; i++) vl[i]=new UVertexList();
		for(int i=0; i<vl1.n; i++) {
			n.set(n1.v[i]).mult(-z).add(vl1.v[i]);
			vl[0].add(n);
			n.set(n2.v[i]).mult(-z).add(vl1.v[i]);
			vl[1].add(n);
			
			n.set(n1.v[i]).mult(z).add(vl2.v[i]);
			vl[2].add(n);
			n.set(n2.v[i]).mult(z).add(vl2.v[i]);
			vl[3].add(n);
		}
		
		o1=new UGeometry();
		o1.quadStrip(vl[1],vl[0]);
		o1.quadStrip(vl[2],vl[3]);
		o1.quadStrip(vl[2],vl[1]);
		o1.quadStrip(vl[0],vl[3]);
		
		o1.beginShape(QUAD);
		o1.vertex(vl[2].v[0]);
		o1.vertex(vl[3].v[0]);
		o1.vertex(vl[0].v[0]);
		o1.vertex(vl[1].v[0]);
		o1.endShape();

		o1.beginShape(QUAD);
		o1.vertex(vl[1].v[vl[0].n-1]);
		o1.vertex(vl[0].v[vl[0].n-1]);
		o1.vertex(vl[3].v[vl[0].n-1]);
		o1.vertex(vl[2].v[vl[0].n-1]);
		o1.endShape();

//		o1.beginShape(QUAD_STRIP);
//			.quadStrip(vl[2], vl[3], true);
//			.quadStrip(vl[1],vl[2])
//			.quadStrip(vl[3],vl[0],true);
		return o1;
	}

	/**
	 * Produces a UGeometry mesh by extruding a QUAD_STRIP along its face 
	 * normal. The input is a list of vertices making up the QUAD_STRIP. The 
	 * face normal of the first quad is used to calculate the extrusion,  
	 * multiplied by the parameter <code>z</code>. All vertices are presumed to be co-planar.  
	 * @param vl Vertex list defining a QUAD_STRIP mesh
	 * @param z Offset to extrude by along the face normal, can be positive or negative
	 * @param reverse Construct in reverse order? Useful in case face normals of 
	 * resulting mesh is incorrect. 
	 * @return
	 */
	public static UGeometry extrude(UVertexList vl,float z,boolean reverse){
		UVertexList vl2;
		vl=new UVertexList(vl);
		vl2=new UVertexList(vl);
		if(reverse) {
			vl.reverseOrder();
			vl2.reverseOrder();
		}
		
		int n=vl.n/2,id=0;
		UGeometry g=new UGeometry();
		UVec3 vv,offs;
		
		
		offs=UVec3.crossProduct(vl.v[1].x-vl.v[0].x,
				vl.v[1].y-vl.v[0].y,
				vl.v[1].z-vl.v[0].z,
				vl.v[2].x-vl.v[0].x,
				vl.v[2].y-vl.v[0].y,
				vl.v[2].z-vl.v[0].z).norm(z);

//		UUtil.log("extrude - normal "+offs.toString());
//		UUtil.log(vl.toDataString());

		offs.mult(0.5f);
		vl.translate(-offs.x,-offs.y,-offs.z);
		g.quadStrip(vl,reverse);

		vl2.translate(offs);
		g.quadStrip(vl2, !reverse);
		
		vl.QStoOutline();
//		vl.add(vl.v[0]);
		vl2.QStoOutline();
//		vl2.add(vl2.v[0]);
		g.quadStrip(vl2,vl,!reverse);
		
		
		return g;
	}

	public void calcBounds() {
		if(bb==null) bb=new UBBox();
		bb.calc(this);
 	}

	public UGeometry calcFaceNormals() {
		for(int i=0; i<faceNum; i++) face[i].calcNormal();
		return this;
	}
	
	/**
	 * Calculates bounding box and centers all faces by calling <code>translate(-bb.c.x,-bb.c.y,-bb.c.z)</code>
	 */
	public UGeometry center() {
		calcBounds();
		return translate(-bb.c.x,-bb.c.y,-bb.c.z);		
	}

	public UGeometry setDimensions(float m) {
		calcBounds();
		scale(m/bb.maxDimension);
		return this;
	}

	public UGeometry setDimensionsXZ(float m) {
		calcBounds();
		float dim=bb.sz.x;
		if(bb.sz.z>dim) dim=bb.sz.z;
		
		scale(m/dim);
		return this;
	}

	public UGeometry setDimensionsXY(float m) {
		calcBounds();
		float dim=bb.sz.x;
		if(bb.sz.y>dim) dim=bb.sz.y;
		
		scale(m/dim);
		return this;
	}

	public UGeometry setDimensionsYZ(float m) {
		calcBounds();
		float dim=bb.sz.z;
		if(bb.sz.y>dim) dim=bb.sz.y;
		
		scale(m/dim);
		return this;
	}

	/**
	 * Output binary STL file of mesh geometry.
	 * @param p Reference to PApplet instance
	 * @param filename Name of file to save to
	 */
	public void writeSTL(PApplet p,String filename) {
  	byte [] header;
  	ByteBuffer buf;
  	UFace f;
  	
    try {
//    	FileOutputStream out=(FileOutputStream)IO.getOutputStream(filename);
    	FileOutputStream out=(FileOutputStream)UIO.getOutputStream(p.sketchPath(filename));

  		buf = ByteBuffer.allocate(200);
  		header=new byte[80];
  		buf.get(header,0,80);
    	out.write(header);
  		buf.rewind();

  		buf.order(ByteOrder.LITTLE_ENDIAN);
  		buf.putInt(faceNum);
  		buf.rewind();
  		buf.get(header,0,4);
    	out.write(header,0,4);
  		buf.rewind();
  		
  		UUtil.logDivider("Writing STL '"+filename+"' "+faceNum);

    	buf.clear();
    	header=new byte[50];
    	if(bb!=null) UUtil.log(bb.toString());
    	
			for(int i=0; i<faceNum; i++) {
				f=face[i];
				if(f.n==null) f.calcNormal();
				
				buf.rewind();
				buf.putFloat(f.n.x);
				buf.putFloat(f.n.y);
				buf.putFloat(f.n.z);
				
				for(int j=0; j<3; j++) {
					buf.putFloat(f.v[j].x);
					buf.putFloat(f.v[j].y);
					buf.putFloat(f.v[j].z);
				}
				
				buf.rewind();
				buf.get(header);
				out.write(header);
			}

			out.flush();
			out.close();
			UUtil.log("Closing '"+filename+"'. "+faceNum+" triangles written.\n");
		} catch (Exception e) {
			e.printStackTrace();
		}          
  }
	
/*  public static UGeometry readData(UDataText data) {
  	UGeometry geo=new UGeometry();
  	data.parseSkipLine();
  	int n=UUtil.parseInt(data.parseGetLine());
  	UUtil.log("Reading UGeometry from DataText - "+n+" faces.");
  	for(int i=0; i<n; i++) geo.addFace(UFace.fromDataString(data.parseGetLine()));
  	UUtil.log("UGeometry "+geo.faceNum+" faces.");
  	data.parseSkipLine();
  	return geo;
  }
*/
	
	public void writeData(UDataText data) {
		data.addDivider("UGeometry - "+faceNum+" faces.");
		data.add(faceNum).endLn();
		for(int i=0; i<faceNum; i++) data.add(face[i].toDataString()).endLn();
		data.endBlock();
	}
	
	public void writePOVRayMesh(PApplet p,String filename) {
		StringBuffer strbuf;
		PrintWriter outWriter;
		Writer outStream;
		String pre="triangle {<",div=">,<",end=">}";
		UFace ff;
  	int num,perc,lastperc=-1,step=5,stepMult=20;

		strbuf=new StringBuffer();
		try {
			outStream=new OutputStreamWriter(
					UIO.getOutputStream(filename,false));		
			outWriter=new PrintWriter(outStream);
			
			outWriter.println("#declare UGeometryMesh = mesh {");
    	if(faceNum>1000000) {step=1; stepMult=100;}
    	
			for (int i=0; i<faceNum; i++) {
				ff=face[i];
				
				strbuf.setLength(0);
				strbuf.append(pre).
				append(UUtil.nf(ff.v[0].x)).append(',').
				append(UUtil.nf(ff.v[0].y)).append(',').
				append(UUtil.nf(ff.v[0].z)).append(div);
				strbuf.
				append(UUtil.nf(ff.v[1].x)).append(',').
				append(UUtil.nf(ff.v[1].y)).append(',').
				append(UUtil.nf(ff.v[1].z)).append(div);
				strbuf.
				append(UUtil.nf(ff.v[2].x)).append(',').
				append(UUtil.nf(ff.v[2].y)).append(',').
				append(UUtil.nf(ff.v[2].z)).append(end);
				
				outWriter.println(strbuf.toString());
	  		perc=(int)(stepMult*(float)i/(float)(faceNum-1));
	  		if(perc!=lastperc) {
	  			lastperc=perc;
	  			System.out.println(UUtil.nf(lastperc*step,2)+"% | "+(i+1)+" triangles written.");//f[i]);
	  		}
			}
			
			outWriter.println("}");
			outWriter.flush();
			outStream.close();		
			

//		  UUtil.log("Saved '"+filename+"' "+numStr);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("writePOVRayMesh failed: "+e.getMessage());
		}
	  catch (Exception e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
	  }
		
	}
	
  /////////////////////////////////////////////
  // FUNCTIONS FOR STL INPUT
  
  public static UGeometry readSTL(PApplet p,String path) {
  	byte [] header,byte4;
  	ByteBuffer buf;
  	int num,perc,lastperc=-1,step,stepMult;
  	float vv[]=new float[12];
    File file=null;
    String filename;
    UGeometry geo=null;

		header=new byte[80];
		byte4=new byte[4];

    try { 
			if (path != null) {
			  filename=path;
			  file = new File(path);
			  if (!file.isAbsolute()) file=new File(p.savePath(path));
			  if (!file.isAbsolute()) 
			    throw new RuntimeException("RawSTLBinary requires an absolute path " +
			    "for the location of the input file.");
			}
			
			FileInputStream in=new FileInputStream(file);
    	System.out.println("\n\nReading "+file.getName());
			
			in.read(header);
			in.read(byte4);
  		buf = ByteBuffer.wrap(byte4);
  		buf.order(ByteOrder.nativeOrder());
  		num=buf.getInt();
			
			System.out.println("Polygons to read: "+num);

    	header=new byte[50];    	

    	
    	geo=new UGeometry();
    	if(num>1000000) {step=1; stepMult=100;}
    	else {step=20; stepMult=5;}
    	step=20; stepMult=5;
  		int id=0;
			for(int i=0; i<num; i++) try {
				in.read(header);
				buf = ByteBuffer.wrap(header);
	  		buf.order(ByteOrder.nativeOrder());
	  		buf.rewind();
	  		
	  		for(int j=0; j<12; j++) vv[j]=buf.getFloat();
		  		id=3;
		  		geo.addFace(
	  				new UVec3[] {
		  				new UVec3(vv[id++],vv[id++],vv[id++]),
		  				new UVec3(vv[id++],vv[id++],vv[id++]),
		  				new UVec3(vv[id++],vv[id++],vv[id++])
	  				});
	  		
				if(i%1000==0) System.out.println(i+" triangles read.");//f[i]);
	  		perc=(int)(stepMult*(float)i/(float)(num-1));
	  		if(perc!=lastperc) {
	  			lastperc=perc;
	  			System.out.println(UUtil.nf(lastperc*step,2)+"% | "+(i+1)+" triangles read.");//f[i]);
	  		}
			} catch(Exception e) {
				p.println("UGeometry.readSTL() "+e.getMessage());
				e.printStackTrace();
			}
			

			System.out.println("Faces: "+geo.faceNum+" ("+num+" reported in file)");

    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    
    return geo;
  }

	public static float triangleArea(UVec3 v1,UVec3 v2,UVec3 v3) {
		float p,a,b,c,val=0;
		
		// Heron's formula 
		// http://www.mathopenref.com/heronsformula.html
		a=UVec3.dist(v1, v2);
		b=UVec3.dist(v1,v3);
		c=UVec3.dist(v2,v3);
		p=(a+b+c)*0.5f;
		val=(float)Math.sqrt(p*(p-a)*(p-b)*(p-c));
		
		return val;
	}

	public float surfaceArea() {
		int id=0;
		float val=0;
		
		for(int i=0; i<faceNum; i++) {
			val+=triangleArea(face[i].v[0],face[i].v[1],face[i].v[2]);
		}
		
		return val;
	}

	/**
	 * Convenience method to call {@link unlekker.modelbuilder.UVertexList.drawVertices() UVertexList.drawVertices()}
	 * on all vertex lists. NOTE: begin/endShape() are not called.
	 * @param p Reference to PApplet instance
	 */
	public void drawVertexLists(PApplet p) {
		for(int i=0; i<vln; i++) vl[i].draw(p);			
	}

	public void add(UStrip s) {
		if(strip==null) strip=new UStrip[100];
		if(strip.length==stripNum) strip=(UStrip [])UUtil.expandArray(strip);
		strip[stripNum++]=s;
	}

	/**
	 * Takes array of vertex indices and returns the matching UVec3
	 * instances from the <code>vert</code> vertex list. If the parameter <code>v</code>
	 * is not null it is filled with pointers to the matching instances, if
	 * not a new UVec3 array of <code>id.length</code> length.
	 * @param id Array of vertex IDs to match
	 * @param v Array to populate with instances, if <code>null</code>
	 * a new UVec3 array is created.
	 * @return Array of UVec3 instances matching vertex IDs
	 */
	public UVec3[] matchIDtoVertex(int[] id, UVec3[] v) {
		if(v==null) v=new UVec3[id.length];
		for(int i=0; i<id.length; i++) v[i]=vert.v[id[i]];
		
		return v;
	}


	public UFace [] getNonQuads() {
		UFace [] f=new UFace[100];
		int fn=0;
		boolean found;
		
		for(int i=0; i<faceNum; i++) {
			found=false;
			for(int j=0; j<quadNum && !found; j++) {
				if(quad[j].fid1==i || quad[j].fid2==i) found=true;
				if(found) UUtil.log(i+" "+quad[j].fid1+" "+quad[j].fid2);
			}
			if(!found) {
				if(f.length==fn) f=(UFace [])UUtil.expandArray(f);
				f[fn++]=face[i];
			}
		}
		
		UUtil.log("getNonQuads() "+fn+" found out of "+faceNum+" faces ("+quadNum+" quads)");
		if(fn==0) return null;
		return (UFace [])UUtil.resizeArray(f, fn);
	}

}

