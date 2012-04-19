package unlekker.modelbuilder;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import processing.core.PApplet;
import processing.core.PConstants;
//import unlekker.geom.Model;
import unlekker.util.*;

public class Geometry implements PConstants {
	public static String NONAME="No name";
	
	public boolean checkForDuplicates=false;
	public String name;
	
	private int bvCnt,shapeType=-1;
	private Vec3 bv[];

	public UFace face[];
	public int faceNum;
	
	public BBox bb;
	
	public Geometry() {
		name=NONAME;
	}

	public Geometry(String _name) {
		name=_name;
	}
	
	public Geometry(Geometry _g) {
		name=_g.name;
		faceNum=_g.faceNum;
		if(faceNum>0) {
			face=new UFace[faceNum];
			for(int i=0; i<faceNum; i++) face[i]=new UFace(_g.face[i]);
			bb=_g.bb;		
		}
		else Util.log("Added Geometry object with 0 faces.");
	}
	
	public void reset() {
		faceNum=0;
		bb=null;
	}

	public void quadStrip(VertexList v1,VertexList v2) {
		int numv=v1.n;

		beginShape(QUAD_STRIP);
		for(int j=0; j<numv; j++) {
			vertex(v1.v[j]);
			vertex(v2.v[j]);
		}
		
		vertex(v1.v[0]);
		vertex(v2.v[0]);
		endShape();
	}
	
	public void quadStrip(VertexList vl[],int vln) {
		if(vln<2) return;
		int numv=vl[0].n;

		for(int i=0; i<vln-1; i++) {
			quadStrip(vl[i], vl[i+1]);
		}		
	}
	
	public void triangleFan(VertexList vl,boolean reverseOrder) {
		Vec3 c=new Vec3();
		
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
	
	public void beginShape(int _type) {
		bvCnt=0;
		bv=new Vec3[100];
		shapeType=_type;
		if(face==null) face=new UFace[100];
	}

	public void vertex(float x,float y,float z) {
		vertex(new Vec3(x,y,z));
	}

	public void vertex(Vec3 v) {
		if(bv.length==bvCnt) bv=(Vec3 [])Util.expandArray(bv);
		bv[bvCnt]=new Vec3(v);
		bvCnt++;
	}

	public void vertex(VertexList vl,boolean reverseOrder) {
		vertex(vl.v,vl.n, reverseOrder);
	}

	public void vertex(VertexList vl) {
		vertex(vl.v,vl.n, false);
	}

	public void vertex(Vec3 _v[],int nv) {
		vertex(_v,nv,false);
	}

	public void vertex(Vec3 _v[],int nv,boolean reverseOrder) {
		if(bv.length<bvCnt+nv) 
			bv=(Vec3 [])Util.expandArray(bv,bvCnt+nv+100);		
		
		if(reverseOrder) {
			for(int i=nv-1; i>-1; i--) bv[bvCnt++]=new Vec3(_v[i]);
		}
		else {
			for(int i=0; i<nv; i++) bv[bvCnt++]=new Vec3(_v[i]);
		}
	}

	public void addFace(Vec3 v1, Vec3 v2, Vec3 v3) {
		if(face.length==faceNum) face=(UFace[])Util.expandArray(face);
		face[faceNum++]=new UFace(v1, v2, v3);
//		Util.log("Added face: "+faceNum);
	}
	
	public Geometry add(Geometry g) {
		if(face==null) {
			face=new UFace[100];
			faceNum=0;
		}
		
		for(int i=0; i<g.faceNum; i++) {
			addFace(g.face[i]);
		}
		
		return this;
	}

	public void addFace(UFace f) {
		addFace(f.v[0],f.v[1],f.v[2]);		
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

	public Geometry rotateX(float a) {
		for(int i=0; i<faceNum; i++) face[i].rotateX(a);
		return this;
	}

	public Geometry rotateY(float a) {
		for(int i=0; i<faceNum; i++) face[i].rotateY(a);
		return this;
	}

	public Geometry rotateZ(float a) {
		for(int i=0; i<faceNum; i++) face[i].rotateZ(a);
		return this;
	}

	public Geometry translate(float x,float y,float z) {
		for(int i=0; i<faceNum; i++) face[i].translate(x,y,z);
		return this;
	}

	public Geometry scale(float m) {
		for(int i=0; i<faceNum; i++) face[i].scale(m);
		return this;
	}

	public Geometry scale(float mx,float my,float mz) {
		for(int i=0; i<faceNum; i++) face[i].scale(mx,my,mz);
		return this;
	}


	public Geometry getCopy() {
		Geometry g;
		
		g=new Geometry(this);
		return g;
	}
	
	public static Geometry extrude(Vec3 vl[],int n,float z){
		int id;
		Geometry g=new Geometry();
		Vec3 vv,offs;
		
		offs=Vec3.crossProduct(vl[1].x-vl[0].x,
				vl[1].y-vl[0].y,
				vl[1].z-vl[0].z,
				vl[2].x-vl[0].x,
				vl[2].y-vl[0].y,
				vl[2].z-vl[0].z);
		offs.norm(z);
		Util.log("Normal: "+offs.toString());

		g.beginShape(QUAD_STRIP);
		id=0;
		for(int i=0; i<n; i++) {
			g.vertex(vl[id++]);
			g.vertex(vl[id++]);
		}
		g.endShape();

		g.beginShape(QUAD_STRIP);
		id=0;
		for(int i=0; i<n; i++) {
			g.vertex(vl[id].x+offs.x,vl[id].y+offs.y,vl[id].z+offs.z);
			g.vertex(vl[id+1].x+offs.x,vl[id+1].y+offs.y,vl[id+1].z+offs.z);
			id+=2;
		}
		g.endShape();

		g.beginShape(QUAD_STRIP);
		
		id=0;
		g.vertex(vl[id].x,vl[id].y,vl[id].z);
		g.vertex(vl[id].x+offs.x,vl[id].y+offs.y,vl[id].z+offs.z);
		
		id=1;
		for(int i=0; i<n; i++) {
			g.vertex(vl[id].x,vl[id].y,vl[id].z);
			g.vertex(vl[id].x+offs.x,vl[id].y+offs.y,vl[id].z+offs.z);
			id+=2;
		}
		
		id=(n-1)*2;
		for(int i=0; i<n; i++) {
			g.vertex(vl[id].x,vl[id].y,vl[id].z);
			g.vertex(vl[id].x+offs.x,vl[id].y+offs.y,vl[id].z+offs.z);
			id-=2;
		}		
		
		g.endShape();
		
		Util.log("g.faceNum "+g.faceNum);
		
		
		return g;
	}

	public void calcBounds() {
		if(bb==null) bb=new BBox();
		bb.calc(this);
 	}

	public void calcFaceNormals() {
		for(int i=0; i<faceNum; i++) face[i].calcNormal();
	}
	
	public void center() {
		calcBounds();
		translate(-bb.centroid.x,-bb.centroid.y,-bb.centroid.z);
	}

	public Geometry setDimensions(float m) {
		calcBounds();
		scale(m/bb.maxDimension);
		return this;
	}

	public Geometry setDimensionsXZ(float m) {
		calcBounds();
		float dim=bb.dim.x;
		if(bb.dim.z>dim) dim=bb.dim.z;
		
		scale(m/dim);
		return this;
	}

	public Geometry setDimensionsXY(float m) {
		calcBounds();
		float dim=bb.dim.x;
		if(bb.dim.y>dim) dim=bb.dim.y;
		
		scale(m/dim);
		return this;
	}

	public Geometry setDimensionsYZ(float m) {
		calcBounds();
		float dim=bb.dim.z;
		if(bb.dim.y>dim) dim=bb.dim.y;
		
		scale(m/dim);
		return this;
	}

	
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

	public float surfaceArea() {
		int id=0;
		float val=0;
		
		for(int i=0; i<faceNum; i++) {
			val+=triangleArea(face[i].v[0],face[i].v[1],face[i].v[2]);
		}
		
		return val;
	}

}

