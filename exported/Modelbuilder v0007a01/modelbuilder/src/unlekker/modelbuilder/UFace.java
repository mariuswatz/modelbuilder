package unlekker.modelbuilder;

import processing.core.PApplet;
import unlekker.util.*;

public class UFace implements UConstants, Comparable<UFace> {
	public UVec3 v[],n,centroid;	
	public int c[],vid[];
	public UGeometry parent;
	
	/**
	 * Empty constructor to be used when parsing or otherwise
	 * constructing a UFace in a non-standard way
	 */
	public UFace() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor for cases when a face belongs to a {@link UGeometry} 
	 * instance. Stores vertex IDs then populates the <code>v</code>
	 * array with UVec3 pointers to the matching vertex instances.
	 * @param _parent
	 * @param id Array of vertex IDs
	 */
	public UFace(UGeometry _parent,int id[]) {
		parent=_parent;
		vid=new int[]{id[0],id[1],id[2]};
		getVertices();
	}

	/**
	 * Constructor for creating UFace instances without a {@link UGeometry} 
	 * instance. Does not store vertex indices>, only the {@link UVec3} vertex data.  
	 * @param fv1
	 * @param fv2
	 * @param fv3
	 */
	public UFace(UVec3 fv1, UVec3 fv2, UVec3 fv3) {
		v=UVec3.getVec3(3);
		v[0].set(fv1);
		v[1].set(fv2);
		v[2].set(fv3);
		calcNormal();
	}

/*	public void setVertexColor(int c1,int c2,int c3) {
		if(c==null) c=new int[3];
		c[0]=c1;
		c[1]=c2;
		c[2]=c3;
	}
*/
	
//	public void draw(PApplet p) {
//		arrID=0;
//		p.beginShape(p.TRIANGLES);
//		if(vArr==null) calcVertexArray();
//		p.vertex(vArr[arrID++], vArr[arrID++],vArr[arrID++]);
//		p.vertex(vArr[arrID++], vArr[arrID++],vArr[arrID++]);
//		p.vertex(vArr[arrID++], vArr[arrID++],vArr[arrID++]);
////		p.vertex(v[0].x, v[0].y, v[0].z);
////		p.vertex(v[1].x, v[1].y, v[1].z);
////		p.vertex(v[2].x, v[2].y, v[2].z);
//		p.endShape();		
//	}
//
//	public void drawColor(PApplet p) {
//		arrID=0;
//		if(vArr==null) calcVertexArray();
//		
//		p.beginShape(p.TRIANGLES);
//		p.fill(c[0]);
////		p.vertex(v[0].x, v[0].y, v[0].z);
//		p.vertex(vArr[arrID++], vArr[arrID++],vArr[arrID++]);
//		p.fill(c[1]);
//		p.vertex(vArr[arrID++], vArr[arrID++],vArr[arrID++]);
////		p.vertex(v[1].x, v[1].y, v[1].z);
//		p.fill(c[2]);
//		p.vertex(vArr[arrID++], vArr[arrID++],vArr[arrID++]);
////		p.vertex(v[2].x, v[2].y, v[2].z);
//		p.endShape();		
//	}

	public UFace rotateX(float a) {
		if(parent!=null) {
			for(int i=0; i<v.length; i++) parent.vert.v[vid[i]].rotateX(a);
		}
		else {
			for(int i=0; i<3; i++) v[i].rotateX(a);
		}
		return this;
	}

	public UFace rotateY(float a) {
		if(parent!=null) {
			for(int i=0; i<v.length; i++) parent.vert.v[vid[i]].rotateY(a);
		}
		else {
			for(int i=0; i<3; i++) v[i].rotateY(a);
		}
		return this;
	}

	public UFace rotateZ(float a) {
		if(parent!=null) {
			for(int i=0; i<v.length; i++) parent.vert.v[vid[i]].rotateZ(a);
		}
		else {
			for(int i=0; i<3; i++) v[i].rotateZ(a);
		}
		return this;
	}
	
	public UFace center() {
		calcCentroid();
		translate(-centroid.x,-centroid.y,-centroid.z);
		return this;
	}

	public UFace toOrigin() {
		UVec3 min=new UVec3(100000,10000,10000);
		for(int i=0; i<3; i++) {
			min.x=PApplet.min(min.x,v[i].x);
			min.y=PApplet.min(min.y,v[i].y);
			min.z=PApplet.min(min.z,v[i].z);
		}
		
		translate(-min.x,-min.y,-min.z);
		return this;
	}

	public UFace flatten() {
		UVec3 vv=new UVec3(v[1]).sub(v[0]).norm();
		UVec3 head=UVec3.getHeadingAngles(vv);
		
//		rotateY(-head.y);
//		rotateZ(-head.x);

		head.z=(float)Math.atan2(vv.y,vv.x);
		UUtil.log("aa "+UUtil.nf(head.x*RAD_TO_DEG)+" "+
				UUtil.nf(head.y*RAD_TO_DEG)+" "+UUtil.toString(v));

		rotateZ(-head.z);
		vv=new UVec3(v[1]).sub(v[0]).norm();
		head.y=(float)Math.atan2(vv.z,vv.x);
		rotateY(-head.y);

		vv=new UVec3(v[2]).sub(v[1]).norm();
		head.x=(float)Math.atan2(vv.z,vv.y);
		rotateX(-head.x);

		toOrigin();
		calcNormal();
		calcCentroid();
//		rotateY((float)Math.atan2(vv.z,vv.x));
	
			   // get rotation amount

		vv=new UVec3(v[1]).sub(v[0]);
		return this;
	}
	
	public UFace translate(float x,float y,float z) {
		if(parent!=null) {
			for(int i=0; i<v.length; i++) parent.vert.v[vid[i]].add(x,y,z);
		}
		else {
			for(int i=0; i<3; i++) v[i].add(x,y,z);
		}
		
		if(centroid!=null) centroid.add(x,y,z);
		return this;
	}

	public UFace translate(UVec3 vv) {
		if(centroid!=null) centroid.add(vv);
		return translate(vv.x,vv.y,vv.z);
	}

	public UFace scale(float m) {
		if(centroid!=null) centroid.mult(m);
		return scale(m,m,m);
	}

	public UFace scale(float mx,float my,float mz) {
		if(parent!=null) {
			for(int i=0; i<v.length; i++) parent.vert.v[vid[i]].mult(mx,my,mz);
		}
		else {
			for(int i=0; i<3; i++) v[i].mult(mx,my,mz);
		}
		
		if(centroid!=null) centroid.mult(mx,my,mz);

		return this;
	}

	public UVec3 calcCentroid() {
		centroid=new UVec3();
		if(v==null) getVertices();
		
		for(int i=0; i<v.length; i++) centroid.add(v[i]);
		centroid.div(v.length);
		
		return centroid;
	}
	
	public UVec3 [] getVertices() {
		if(parent!=null) {
			v=parent.matchIDtoVertex(vid, v);
		}
		return v;
	}
	
	public UVec3 getDimensions() {
		UVec3 dim=new UVec3();
		dim.x=PApplet.max(PApplet.max(v[0].x,v[1].x),v[2].x)-
				PApplet.min(PApplet.min(v[0].x,v[1].x),v[2].x);
		dim.y=PApplet.max(PApplet.max(v[0].y,v[1].y),v[2].y)-
				PApplet.min(PApplet.min(v[0].y,v[1].y),v[2].y);
		dim.z=PApplet.max(PApplet.max(v[0].z,v[1].z),v[2].z)-
				PApplet.min(PApplet.min(v[0].z,v[1].z),v[2].z);
		
		return dim;
	}

	public void calcNormal() {
		n=UVec3.crossProduct(
				v[1].x-v[0].x,v[1].y-v[0].y,v[1].z-v[0].z,
  			v[2].x-v[0].x,v[2].y-v[0].y,v[2].z-v[0].z).norm();
	}

	public String toString() {
		String s="face=";
		if(vid!=null) s+=" "+UUtil.toString(vid);
		s+=" "+UUtil.toString(v);
		
		return s;
	}

	public static UFace fromDataString(String s) {
		UFace f=new UFace();
		
		s=UUtil.chopBraces(s);
		String [] tok=PApplet.split(s, UDataText.SPACER);
		UUtil.log("UFace fromDataString - "+tok.length+" "+UUtil.toString(tok));
		f.v[0]=UVec3.parse(tok[0]);
		f.v[1]=UVec3.parse(tok[1]);
		f.v[2]=UVec3.parse(tok[2]);
		return f;
	}

	public String toDataString() {
		String s=UUtil.toString(v);
		
		return s;
	}

	public void draw(PApplet p) {
		p.beginShape(TRIANGLES);
		p.vertex(v[0].x,v[0].y,v[0].z);
		p.vertex(v[1].x,v[1].y,v[1].z);
		p.vertex(v[2].x,v[2].y,v[2].z);
		p.endShape();
	}

	/** Compares this instance to another UFace instance. Uses 
	 * vertex indices if they exist, if not <code>UVec3.equals()</code>
	 * is used to compare vertices.
	 */
	public int compareTo(UFace o) {
		if(o.vid!=null && vid!=null) {
			if(o.vid[0]==vid[0] && o.vid[1]==vid[1] && o.vid[2]==vid[2]) return 0;
			return -1;
		}
		
		if(o.v[0].equals(v[0]) && o.v[1].equals(v[1]) && o.v[2].equals(v[2])) 
			return 0;
		
		return -1; // no match
	}

}
