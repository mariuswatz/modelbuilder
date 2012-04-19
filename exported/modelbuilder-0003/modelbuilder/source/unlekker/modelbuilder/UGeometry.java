package unlekker.modelbuilder;

/**
 * Utility class to deal with mesh geometries. It uses the PApplet's <code>beginShape() / vertex() / endShape()</code> to build meshes.
 * A secondary function of the class is to act as a collection of 
 * {@link unlekker.modelbuilder.UVertexList UVertexList} objects, for 
 * instance to perform transformations. 
 *  
 * @author <a href="http://workshop.evolutionzone.com/">Marius Watz</a>
 */

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import processing.core.PApplet;
import processing.core.PConstants;
import unlekker.util.*;

public class UGeometry implements PConstants {
	public static String NONAME="No name";
	
	protected boolean checkForDuplicates=false;
	public String name;
	
	private int bvCnt,shapeType=-1;
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
	 * Array of vertex lists stored in this object.
	 */
	public UVertexList vl[];
	/**
	 * Number of vertex lists stored in this object.
	 */
	public int vln;
	/**
	 * Boundng box. 
	 */
	public BBox bb;
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
		name=_g.name;
		faceNum=_g.faceNum;
		
		if(faceNum>0) {
			face=new UFace[faceNum];
			for(int i=0; i<faceNum; i++) face[i]=new UFace(_g.face[i]);
			bb=_g.bb;		
		}
//		else Util.log("Added Geometry object with 0 faces.");
		
		if(_g.vln>0) {			
			for(int i=0; i<_g.vln; i++) add(new UVertexList(_g.vl[i]));
		}

	}
	
	/**
	 * Resets geometry to empty.
	 */	
	public void reset() {
		faceNum=0;
		vln=0;
		bb=null;
	}

	/**
	 * Adds QUAD_STRIP of faces from a list of vertex pairs.
	 * @param vl Array of vertex pairs
	 * @param reverseOrder Build in reverse order?
	 */
	public void quadStrip(UVertexList vl, boolean reverseOrder) {
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
	}

	/**
	 * Adds QUAD_STRIP of faces built from two vertex lists. 
	 * @param v1 First edge of QUAD_STRIP
	 * @param v2 Second edge of QUAD_STRIP
	 */
	public void quadStrip(UVertexList v1,UVertexList v2) {
		quadStrip(v1,v2,false);
	}
	
	/**
	 * Adds QUAD_STRIP of faces built from two vertex lists. 
	 * @param v1 First edge of QUAD_STRIP
	 * @param v2 Second edge of QUAD_STRIP
	 * @param reverse Build in reverse order?
	 */
	public void quadStrip(UVertexList v1,UVertexList v2,boolean reverse) {
		int numv=v1.n;
		int id=0;

		beginShape(QUAD_STRIP);
		if(reverse) {
			id=numv-1;
			for(int j=0; j<numv; j++) {
				vertex(v1.v[id]);
				vertex(v2.v[id]);
				id--;
			}
			
			vertex(v1.v[numv-1]);
			vertex(v2.v[numv-1]);
		}
		else {
			for(int j=0; j<numv; j++) {
				vertex(v1.v[id]);
				vertex(v2.v[id++]);
			}
			
			vertex(v1.v[0]);
			vertex(v2.v[0]);
		}
		endShape();
	}
	
	/**
	 * Adds a mesh of QUAD_STRIPs built from an array of vertex lists.
	 * Each vertex list is treated as a single edge and will be connected to the next edge in the array.
	 * All vertex lists must have the same number of vertices. 
	 * @param vl Array of vertex lists to be used as edges.
	 * @param vln Number of lists to use from array
	 */
	public void quadStrip(UVertexList vl[],int vln) {
		if(vln<2) return;
		int numv=vl[0].n;

		for(int i=0; i<vln-1; i++) {
			quadStrip(vl[i], vl[i+1]);
		}		
	}
	
	/**
	 * Adds a TRIANGLE_FAN constructed from input vertex list.
	 * @param vl Array of vertex pairs
	 * @param reverseOrder Build in reverse order?
	 */
	public void triangleFan(UVertexList vl,boolean reverseOrder) {
		UVec3 c=new UVec3();
		
		if(vl.n<3) return;
		
		for(int i=0; i<vl.n; i++) c.add(vl.v[i]);
		c.div(vl.n);
		
		beginShape(TRIANGLE_FAN);
		vertex(c);
		if(reverseOrder) {
			for(int i=vl.n-1; i>-1; i--) vertex(vl.v[i]);			
			vertex(vl.v[vl.n-1]);
		}
		else {
			for(int i=0; i<vl.n; i++) vertex(vl.v[i]);
			vertex(vl.v[0]);
		}
		endShape();
		
	}
	
	///////////////////////////////////////////////////
	// BEGINSHAPE / ENDSHAPE METHODS
	
	
	/**
	 * Starts building a new series of faces, using the same logic 
	 * as <a href="http://processing.org/reference/beginShape_.html">PApplet.beginShape()</a>.
	 * 
	 * Currently the following shape types are supported: TRIANGLE_FAN, TRIANGLE_STRIP, TRIANGLES, QUADS, QUAD_STRIP
	 * @param _type Shape type (TRIANGLE_FAN, TRIANGLE_STRIP, TRIANGLES, QUADS, QUAD_STRIP)
	 */
	public void beginShape(int _type) {
		bvCnt=0;
		bv=new UVec3[100];
		shapeType=_type;
		if(face==null) face=new UFace[100];
	}

	public void endShape() {
    switch (shapeType) {
      case TRIANGLE_FAN: {
        int stop = bvCnt - 1;
        for (int i =0; i<stop; i++) {
          addFace(bv[0], bv[i+1],bv[i]);
        }
      }
      break;

      case TRIANGLES: {
        int stop = bvCnt - 2;
        for (int i = 0; i < stop; i += 3) 
        	addFace(bv[i], bv[i+2], bv[i+1]);
      }
      break;

      case TRIANGLE_STRIP: {
        int stop = bvCnt - 2;
        for (int i = 0; i < stop; i++) {
        	// HANDED-NESS ISSUE
        	if(i%2==1) addFace(bv[i], bv[i+2], bv[i+1]);
        	else addFace(bv[i], bv[i+1], bv[i+2]);
        }
      }
      break;

      case QUADS: {
        int stop = bvCnt-3;
//        Util.log("QUADS "+bvCnt+" "+stop);
        for (int i = 0; i < stop; i += 4) {
        	addFace(bv[i], bv[i+2], bv[i+1]);
        	addFace(bv[i], bv[i+3], bv[i+2]);
        }
      }
      break;

      case QUAD_STRIP: {
        int stop = bvCnt-3;
        for (int i =0; i<stop; i += 2) {
        	// HANDED-NESS ISSUE
        	addFace(bv[i+3], bv[i+1], bv[i+0]);
        	addFace(bv[i], bv[i+2], bv[i+3]);
        }
      }
      break;

      case POLYGON:{
      	Util.log("beginShape(POLYGON) currently unsupported.");
//      	Util.log("beginShape(POLYGON) "+bvCnt+" "+bv.length);
//      	if(bvCnt!=bv.length) bv=(Vec3 [])Util.resizeArray(bv, bvCnt);
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
    
//		Util.log("Faces: "+faceNum);
	}
	
	/**
	 * Add vertex to shape being built by <code>beginShape() / endShape()</code>
	 * @param x
	 * @param y
	 * @param z
	 */
	public void vertex(float x,float y,float z) {
		vertex(new UVec3(x,y,z));
	}

	/**
	 * Add UVec3 vertex to shape being built by <code>beginShape() / endShape()</code>
	 * The vertex information is copied, leaving the original UVec3 instance unchanged.
	 * @param v
	 */
	public void vertex(UVec3 v) {
		if(bv.length==bvCnt) bv=(UVec3 [])Util.expandArray(bv);
		bv[bvCnt]=new UVec3(v);
		bvCnt++;
	}

	/**
	 * Add vertex list to shape being built by <code>beginShape() / endShape()</code>. 
	 * All vertices are copied, leaving the original instances unchanged.
	 * @param vl 
	 * @param reverseOrder Add in reverse order?
	 */
	public void vertex(UVertexList vl,boolean reverseOrder) {
		vertex(vl.v,vl.n, reverseOrder);
	}

	/**
	 * Adds vertex list to shape being built by <code>beginShape() / endShape()</code>. 
	 * All vertices are copied, leaving the original instances unchanged.
	 */
	public void vertex(UVertexList vl) {
		vertex(vl.v,vl.n, false);
	}

	/**
	 * Adds array of UVec3 vertices to shape being built by <code>beginShape() / endShape()</code>. 
	 * All objects are copied, leaving the original instances unchanged.
	 * @param _v Array of vertices
	 * @param nv Number of vertices to add
	 */
	public void vertex(UVec3 _v[],int nv) {
		vertex(_v,nv,false);
	}

	/**
	 * Adds array of UVec3 vertices to shape being built by <code>beginShape() / endShape()</code>. 
	 * All objects are copied, leaving the original instances unchanged.
	 * @param _v Array of vertices
	 * @param nv Number of vertices to add
	 * @param reverseOrder Add in reverse order?
	 */
	public void vertex(UVec3 _v[],int nv,boolean reverseOrder) {
		if(bv.length<bvCnt+nv) 
			bv=(UVec3 [])Util.expandArray(bv,bvCnt+nv+100);		
		
		if(reverseOrder) {
			for(int i=nv-1; i>-1; i--) bv[bvCnt++]=new UVec3(_v[i]);
		}
		else {
			for(int i=0; i<nv; i++) bv[bvCnt++]=new UVec3(_v[i]);
		}
	}

	
	
	/**
	 * Adds single face from three UVec3 objects.
	 * @param v1
	 * @param v2
	 * @param v3
	 */
	public void addFace(UVec3 v1, UVec3 v2, UVec3 v3) {
		if(face.length==faceNum) face=(UFace[])Util.expandArray(face);
		face[faceNum++]=new UFace(v1, v2, v3);
//		Util.log("Added face: "+faceNum);
	}
	
	/**
	 * Adds a UVertexList object to the <code>vl</code> array. The object 
	 * is stored by reference, so any changes made to it through this class will
	 * also affect the original instance.  
	 * Not to be confused with {@link #vertex(UVertexList vl)}  
	 * @param vv 
	 * @return
	 */
	public UGeometry add(UVertexList vv) {
		if(vl==null) vl=new UVertexList[10];
		else if(vln==vl.length) vl=(UVertexList[])Util.expandArray(vl);
		vl[vln++]=vv;
		
		return this;
	}
	
	/**
	 * Adds all the faces of a UGeometry instance. The faces is copied and the original instance is left unchanged. 
	 * @param g UGeometry to add
	 * @return
	 */
	public UGeometry add(UGeometry g) {
		if(face==null) {
			face=new UFace[100];
			faceNum=0;
		}
		
		for(int i=0; i<g.faceNum; i++) {
			addFace(g.face[i]);
		}
		
		return this;
	}
	
	
	/**
	 * Add single face
	 * @param f
	 */
	public void addFace(UFace f) {
		addFace(f.v[0],f.v[1],f.v[2]);		
	}
	
	/**
	 * Draws all faces contained in this UGeometry object.
	 * @param p Reference to PApplet instance to draw into
	 */

	public void draw(PApplet p) {
		UFace f;
		int fid=0,id=0;

		p.beginShape(TRIANGLES);		
		for(int i=0; i<faceNum; i++) {			
			f=face[i];
			p.vertex(f.v[0].x,f.v[0].y,f.v[0].z);
			p.vertex(f.v[1].x,f.v[1].y,f.v[1].z);
			p.vertex(f.v[2].x,f.v[2].y,f.v[2].z);
		}
		p.endShape();		
	}
	
	/////////////////////////////////////////////////////////
	// TRANSFORMATIONS

	public UGeometry rotateX(float a) {
		for(int i=0; i<faceNum; i++) face[i].rotateX(a);
		for(int i=0; i<vln; i++) vl[i].rotateX(a);
		if(bb!=null) calcBounds();
		return this;
	}

	public UGeometry rotateY(float a) {
		for(int i=0; i<faceNum; i++) face[i].rotateY(a);
		for(int i=0; i<vln; i++) vl[i].rotateY(a);
		if(bb!=null) calcBounds();
		return this;
	}

	public UGeometry rotateZ(float a) {
		for(int i=0; i<faceNum; i++) face[i].rotateZ(a);
		for(int i=0; i<vln; i++) vl[i].rotateZ(a);
		if(bb!=null) calcBounds();
		return this;
	}

	public UGeometry translate(float x,float y,float z) {
		for(int i=0; i<faceNum; i++) face[i].translate(x,y,z);
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
		for(int i=0; i<faceNum; i++) face[i].scale(mx,my,mz);
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
	
	/**
	 * Produces a UGeometry mesh by extruding a QUAD_STRIP along its face 
	 * normal. The input is a list of vertices making up the QUAD_STRIP. The 
	 * face normal of the first quad is used to calculate the extrusion,  
	 * multiplied by the parameter <code>z</code>.
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
				vl.v[2].z-vl.v[0].z);
		offs.norm(z);
//		Util.log("extrude - normal "+offs.toString());
//		Util.log(vl.toDataString());

		offs.mult(0.5f);
		vl.translate(-offs.x,-offs.y,-offs.z);
		g.quadStrip(vl,reverse);

		vl2.translate(offs);
		g.quadStrip(vl2, !reverse);
		
		vl.QStoOutline();
		vl2.QStoOutline();
		g.quadStrip(vl2,vl,!reverse);
		
		
		return g;
	}

	public void calcBounds() {
		if(bb==null) bb=new BBox();
		bb.calc(this);
 	}

	public void calcFaceNormals() {
		for(int i=0; i<faceNum; i++) face[i].calcNormal();
	}
	
	/**
	 * Calculates bounding box and centers all faces by calling <code>translate(-bb.c.x,-bb.c.y,-bb.c.z)</code>
	 */
	public void center() {
		calcBounds();
		translate(-bb.c.x,-bb.c.y,-bb.c.z);
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
    	FileOutputStream out=(FileOutputStream)IO.getOutputStream(p.sketchPath(filename));

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
  		
  		Util.logDivider("Writing STL '"+filename+"' "+faceNum);

    	buf.clear();
    	header=new byte[50];
    	if(bb!=null) Util.log(bb.toString());
    	
			for(int i=0; i<faceNum; i++) {
				f=face[i];
				
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
			Util.log("Closing '"+filename+"'. "+faceNum+" triangles written.\n");
		} catch (Exception e) {
			e.printStackTrace();
		}          
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


}

