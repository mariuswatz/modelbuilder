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

	public Vec3 vert[],faceN[];
	public int faces[];
	public int faceNum,vertNum;
	
	public BBox bb;
	
	public Geometry() {
		name=NONAME;
	}

	public Geometry(String _name) {
		name=_name;
	}
	
	public void beginShape(int _type) {
		bvCnt=0;
		bv=new Vec3[100];
		shapeType=_type;
	}

	public void vertex(float x,float y,float z) {
		vertex(new Vec3(x,y,z));
	}

	public void vertex(Vec3 v) {
		if(bv.length==bvCnt) bv=(Vec3 [])Util.expandArray(bv);
		bv[bvCnt]=new Vec3(v);
		bvCnt++;
	}

	public void vertex(Vec3 _v[]) {
		int nv=_v.length;
		
		if(bv.length<bvCnt+nv) 
			bv=(Vec3 [])Util.expandArray(bv,bvCnt+nv+100);		
		for(int i=0; i<nv; i++) {
			bv[bvCnt++]=new Vec3(_v[i]);
		}	
	}
	
	public int addVertex(Vec3 _v) {
		if(vert==null)	vert=new Vec3[100];
			
		if(vert.length==vertNum) vert=(Vec3 [])Util.expandArray(vert);
		vert[vertNum++]=new Vec3(_v);
		return vertNum-1;
	}
	
	public void addFace(Vec3 v1, Vec3 v2, Vec3 v3) {
		addFace(addVertex(v1),addVertex(v2),addVertex(v3));
		
	}
		
	public void addFace(int i, int j, int k) {
		int id=0;
//		Util.log("addFace "+faceNum);

		if(faces==null) faces=new int[100*3];
 
		if(faces.length==faceNum*3) 
			faces=Util.expandArray(faces,faceNum*3*2);
		id=faceNum*3;
		
		faces[id++]=i;
		faces[id++]=j;
		faces[id++]=k;
		
		faceNum++;
	}
	
	public void addGeometry(Geometry g) {
		int id1,id2,id3;
		
		int vid=0;
		for(int i=0; i<g.faceNum; i++) {
			id1=addVertex(g.vert[g.faces[vid++]]);
			id2=addVertex(g.vert[g.faces[vid++]]);
			id3=addVertex(g.vert[g.faces[vid++]]);
			addFace(id1, id2, id3);
		}
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
        for (int i = 0; i < stop; i++) 
        	addFace(bv[i], bv[i+2], bv[i+1]);
      }
      break;

      case QUADS: {
        int stop = bvCnt-3;
        for (int i = 0; i < stop; i += 4) {
        	addFace(bv[i], bv[i+1], bv[i+2]);
        	addFace(bv[i], bv[i+2], bv[i+3]);
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
//        addPolygonTriangles();
      }
      break;
    }
    
		Util.log("Faces: "+faceNum);
	}

	public void draw(PApplet p) {
		int fid=0,id=0;

		p.beginShape(TRIANGLES);		
		for(int i=0; i<faceNum; i++) {
			id=faces[fid++];
			p.vertex(vert[id].x,vert[id].y,vert[id].z);
			id=faces[fid++];
			p.vertex(vert[id].x,vert[id].y,vert[id].z);
			id=faces[fid++];
			p.vertex(vert[id].x,vert[id].y,vert[id].z);
		}
		p.endShape();		
	}

	public void rotateX(float a) {
		for(int i=0; i<vertNum; i++) vert[i].rotateX(a);
	}

	public void rotateY(float a) {
		for(int i=0; i<vertNum; i++) vert[i].rotateY(a);
	}

	public void rotateZ(float a) {
		for(int i=0; i<vertNum; i++) vert[i].rotateZ(a);
	}

	public void translate(float x,float y,float z) {
		for(int i=0; i<vertNum; i++) vert[i].add(x,y,z);
	}
	
	public Geometry getCopy() {
		Geometry g;
		
		g=new Geometry(name);
		g.vertNum=vertNum;
		g.faceNum=faceNum;
		g.vert=new Vec3[vertNum];
		for(int i=0; i<vertNum; i++) g.vert[i]=new Vec3(vert[i]);
		
		int fn=faceNum*3;
		g.faces=new int[fn];
		for(int i=0; i<fn; i++) g.faces[i]=faces[i];
		
		
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
		
		Util.log("g.vert "+g.vertNum+" g.face "+g.faceNum);
		
		
		return g;
	}
	
	public void calcFaceNormals() {
		int fid=0;
		Vec3 nn;
		
		if(faceN==null || faceN.length<faceNum) 
			faceN=new Vec3[faceNum];
		
		for(int i=0; i<faceNum; i++) {					
			 nn=Vec3.crossProduct(vert[faces[fid++]],
					 vert[faces[fid++]],
					 vert[faces[fid++]]);
			 nn.norm();
//			 nn.mult(-1);
			 faceN[i]=nn;
		}
	}
	
	public void writeSTL(String filename) {
  	byte [] header;
  	ByteBuffer buf;
  	
    try {
    	FileOutputStream out=(FileOutputStream)IO.getOutputStream(filename);

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
  		
  		Util.logDivider("Writing STL '"+filename+"' "+faceNum+" "+vertNum);

    	buf.clear();
    	header=new byte[50];
    	if(bb!=null) Util.log(bb.toString());
    	
    	int fid=0;
    	calcFaceNormals();
    	
			for(int i=0; i<faceNum; i++) {
				buf.rewind();
				buf.putFloat(faceN[i].x);
				buf.putFloat(faceN[i].y);
				buf.putFloat(faceN[i].z);
				
				for(int j=0; j<3; j++) {
					buf.putFloat(vert[faces[fid]].x);
					buf.putFloat(vert[faces[fid]].y);
					buf.putFloat(vert[faces[fid++]].z);
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
			val+=triangleArea(
					vert[faces[id++]],
					vert[faces[id++]],
					vert[faces[id++]]);			
		}
		
		return val;
	}

}

