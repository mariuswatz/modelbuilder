package unlekker.modelbuilder;

/**
 * Utility class to deal with mesh geometries. It uses the PApplet's <code>beginShape() / vertex() / endShape()</code> to build meshes.
 * A secondary function of the class is to act as a collection of 
 * {@link unlekker.modelbuilder.UVertexList UVertexList} objects, for 
 * instance to perform transformations. 
 *  
 * @author <a href="http://workshop.evolutionzone.com/">Marius Watz</a>
 */

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import processing.core.PApplet;
import processing.core.PConstants;
import unlekker.util.*;

public class UGeometryTransformer implements PConstants {
	public UGeometry old;
	public UGeometry result;
	
	/**
	 * Creates an extruded version of all quads stored in a UGeometry instance, given inset and extrusion factors. 
	 * Optionally, the <code>isWindow</code> parameter can be set to true to leave the front face of the extruded geometry
	 * open, producing a window effect.
	 * @param obj <code>UGeometry</code> instance where the generated geometry should be stored
	 * @param inset Inset of the extruded face
	 * @param extrusion Extrusion factor for the the extruded face. No checking is undertaken to prevent intersections with other geometry.
	 * @param isWindow isWindow Flag indicating whether to leave front faces empty to create a window effect.
	 * @return
	 */
	public UGeometry extrude(UGeometry obj,float inset,float extrusion,boolean isWindow) {
		
		old=obj;
		newResult();
		for(int i=0; i<old.quadNum; i++) 
			old.quad[i].extrude(result, inset, extrusion,isWindow);
		
		result.add(old.getNonQuads());
		
		return result;
	}

	private UGeometry handleAttractors(UGeometry obj,UVertexList fpos,float force[],boolean isRepulsor) {
		UVec3 vv,vforce=new UVec3();
		
		old=obj;
		result=new UGeometry(old);
		
		for(int i=0; i<result.vert.n; i++) {
			for(int j=0; j<fpos.n; j++) {
				vv=result.vert.v[i];
				float d=fpos.v[j].distanceTo(vv);
				if(d<force[j]) {
					d=1-d/force[j];
					vforce.set(fpos.v[j]).sub(vv).mult(d*d);
					if(isRepulsor) vv.sub(vforce);
					else vv.add(vforce);
				}
			}
		}
		return result;
	}
	
	public UGeometry attractor(UGeometry obj,UVec3 fpos,float force) {
		return handleAttractors(obj,new UVertexList().add(fpos),new float[]{force}, false);
	}

	public UGeometry repulsor(UGeometry obj,UVec3 fpos,float force) {
		return handleAttractors(obj,new UVertexList().add(fpos),new float[]{force}, true);
	}

	public UGeometry attractor(UGeometry obj,UVertexList fpos,float force[]) {
		return handleAttractors(obj, fpos, force, false);
//		UVec3 vv,vforce=new UVec3();
//		
//		old=obj;
//		newResult();
//		for(int i=0; i<result.vert.n; i++) {
//			for(int j=0; j<pos.n; j++) {
//				vv=result.vert.v[i];
//				float d=pos.v[j].distanceTo(vv);
//				if(d<force[j]) {
//					d=1-d/force[j];
//					vforce.set(pos.v[j]).sub(vv).mult(d*d);
//					vv.add(vforce);
//				}
//			}
//		}
//		return result;
	}

	public UGeometry repulsor(UGeometry obj,UVertexList fpos,float force[]) {
		return handleAttractors(obj, fpos, force, true);
//		UVec3 vv,vforce=new UVec3();
//		
//		old=obj;
//		newResult();
//		for(int i=0; i<result.vert.n; i++) {
//			for(int j=0; j<fpos.n; j++) {
//				vv=result.vert.v[i];
//				float d=fpos.v[j].distanceTo(vv);
//				if(d<force[j]) {
//					d=1-d/force[j];
//					vforce.set(fpos.v[j]).sub(vv).mult(d*d);
//					vv.sub(vforce);
//				}
//			}
//		}
//		return result;
	}

//	public UGeometry repulsor(UGeometry obj,UVec3 pos,float force) {
//		UVec3 vv,vforce=new UVec3();
//		
//		old=obj;
//		newResult();
//		for(int i=0; i<result.vert.n; i++) {
//			vv=result.vert.v[i];
//			float d=pos.distanceTo(vv);
//			if(d<force) {
//				d=1-d/force;
//				vforce.set(pos).sub(vv).mult(d*d);
//				vv.sub(vforce);
//			}
//		}
//		return result;
//	}

	private void newResult() {
//		if(result==null) 
			result=new UGeometry();
//		else result.reset();		
	}

}

