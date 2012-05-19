package unlekker.modelbuilder;

import processing.core.PApplet;
import unlekker.util.UConstants;
import unlekker.util.UUtil;

/**
 * Stores information about quad geometry. Primarily intended
 * to be a support class for {@link UGeometry} and {@link UQuadStrip}
 *  
 * Stores vertices in the same order as a quad strip does: 
 * 0 == Bottom left, 1 == top left, 2 == bottom right, 3 == top right.
 * @author marius
 *
 */
public class UQuad extends UFace implements UConstants {
	public int fid1=-1,fid2=-1;
	public UFace f1,f2;
	
	public UQuad(UGeometry _parent,int _fid1,int _fid2) {
		parent=_parent;
		f1=parent.face[fid1];
		f2=parent.face[fid2];
		
		getVertices();
	}

	public UVec3 [] getVertices() {
		v=new UVec3[] {f1.v[0],f1.v[2],f1.v[1],f2.v[2]};
		return v;
	}
	
	public UQuad(UGeometry _parent, UVec3[] vv) {
		parent=_parent;
//		int id[]=parent.addVerticesToMasterList(vv);

		fid1=parent.addFace(new UVec3[]{vv[0],vv[2],vv[1]});
		fid2=parent.addFace(new UVec3[]{vv[1],vv[2],vv[3]});
		f1=parent.face[fid1];
		f2=parent.face[fid2];
		
		getVertices();
	}

/*	*//**
	 * Sets vertices of quad and generates new UFace instances.
	 * Only intended to be used in cases where UQuad is used without a
	 * UGeometry reference.
	 * @param fv1
	 * @param fv2
	 * @param fv3
	 * @param fv4
 * @return 
	 *//*
	public void set(UVec3 fv1, UVec3 fv2, UVec3 fv3,UVec3 fv4) {
		v=new UVec3[]{fv1,fv2,fv3,fv4};
		f1=new UFace(fv1, fv3, fv2);
		f2=new UFace(fv3, fv4, fv2);
		v[0]=f1.v[0];
		v[1]=f1.v[2];
		v[2]=f1.v[1];
		v[3]=f2.v[1];
		calcNormal();
	}
*/
	public UVec3 calcNormal() {
		f1.calcNormal();
		n=f1.n;
		return n;
	}

/*	public void buildWindow(UGeometry geo,float mod) {
		UVec3 c=calcCentroid();
		if(parent!=null) getVertices();
		
		UVec3 vD[]=UVec3.getVec3(4);
		for(int j=0; j<4; j++) 
			vD[j].set(c).sub(v[j]).mult(mod).add(v[j]);
		
		geo.beginShape(QUAD_STRIP);
		int order[]=new int[]{0,2,3,1,0};
		for(int i=0; i<order.length; i++) {
			geo.vertex(v[order[i]]);
			geo.vertex(vD[order[i]]);
		}
		geo.endShape();
	}

	public void buildWindowAbs(UGeometry geo,float mod) {
		UVec3 v1=null,v2=null;
		UVec3 vtan1=new UVec3(),vtan2=new UVec3();
		UVec3 vD[]=UVec3.getVec3(4);
		UVec3 c=calcCentroid();
		if(parent!=null) getVertices();

		float l=10000;		
		l=PApplet.min(l, vtan1.set(v[1]).sub(v[0]).length());
		l=PApplet.min(l, vtan1.set(v[2]).sub(v[0]).length());
		l=PApplet.min(l, vtan1.set(v[1]).sub(v[3]).length());
		l=PApplet.min(l, vtan1.set(v[2]).sub(v[3]).length());
		if(mod>l/2) mod=l/2-1;
		
		for(int j=0; j<4; j++) {
			if(j==0) {v1=v[1]; v2=v[2];}
			if(j==1) {v1=v[0]; v2=v[3];}
			if(j==2) {v1=v[0]; v2=v[3];}
			if(j==3) {v1=v[2]; v2=v[1];}
			vtan1.set(v1).sub(v[j]).norm(mod);
			vtan2.set(v2).sub(v[j]).norm(mod);
			vD[j].set(v[j]).add(vtan1).add(vtan2);
		}
		
		geo.beginShape(QUAD_STRIP);
		int order[]=new int[]{0,2,3,1,0};
		for(int i=0; i<order.length; i++) {
			geo.vertex(v[order[i]]);
			geo.vertex(vD[order[i]]);
		}
		geo.endShape();
	}

*/
	
	/**
	 * Creates a extruded pyramid-like geometry based on a quad, with the option to leave the front quad open to 
	 * create a window effect. 
	 * @param geo <code>UGeometry</code> instance where the generated geometry should be stored
	 * @param inset Inset of the extruded face, will be overriden if the length <code>l</code> of any of the quad edges are less than
	 * <code>inset</code> (in which case <code>inset=l/2-1</code> is used.) This is done to avoid self-intersecting geometry.
	 * @param extrusion Extrusion factor for the the extruded face. No checking is undertaken to prevent intersections with other geometry.
	 * @param isWindow Flag indicating whether to leave front face empty to create a window effect.
	 */
	public void extrude(UGeometry geo,float inset, float extrusion,boolean isWindow) {
		UVec3 v1=null,v2=null;
		UVec3 vtan1=new UVec3(),vtan2=new UVec3();
		UVec3 vD[]=UVec3.getVec3(4);
		UVec3 c=calcCentroid();
		float l=10000;
		
		l=PApplet.min(l, vtan1.set(v[1]).sub(v[0]).length());
		l=PApplet.min(l, vtan1.set(v[2]).sub(v[0]).length());
		l=PApplet.min(l, vtan1.set(v[1]).sub(v[3]).length());
		l=PApplet.min(l, vtan1.set(v[2]).sub(v[3]).length());
		if(inset>l/2) inset=l/2-1;

		calcNormal();
		UVec3 normal=new UVec3(n).norm(extrusion);
		
		for(int j=0; j<4; j++) {
			if(j==0) {v1=v[1]; v2=v[2];}
			if(j==1) {v1=v[0]; v2=v[3];}
			if(j==2) {v1=v[0]; v2=v[3];}
			if(j==3) {v1=v[2]; v2=v[1];}
			vtan1.set(v1).sub(v[j]).norm(inset);
			vtan2.set(v2).sub(v[j]).norm(inset);
			vD[j].set(v[j]).add(vtan1).add(vtan2).add(normal);
		}

//		if(!isWindow) geo.addFace(v);

		int order[]=new int[]{0,2,3,1,0};
		geo.beginShape(QUAD_STRIP);
		for(int i=0; i<order.length; i++) {
			geo.vertex(v[order[i]]);
			geo.vertex(vD[order[i]]);
		}
		geo.endShape();
		
		if(!isWindow)
			geo.addFace(new UVec3[]{vD[0],vD[1],vD[2],vD[3]});
	}

	public int compareTo(UQuad o) {
		if(o.f1.compareTo(f1)==0 && o.f2.compareTo(f2)==0) 
			return 0;
		
		return -1; // no match
	}

	public void draw(PApplet p) {
		p.beginShape(TRIANGLES);
		p.vertex(v[0].x,v[0].y,v[0].z);
		p.vertex(v[2].x,v[2].y,v[2].z);
		p.vertex(v[1].x,v[1].y,v[1].z);

		p.vertex(v[1].x,v[1].y,v[1].z);
		p.vertex(v[2].x,v[2].y,v[2].z);
		p.vertex(v[3].x,v[3].y,v[3].z);
		p.endShape();
	}
}
