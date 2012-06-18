package unlekker.modelbuilder.filter;

import processing.core.PApplet;
import unlekker.modelbuilder.*;
import unlekker.util.*;

/**
 * TODO 
 * - Add untransformed
 * - Deal with quads (or just triangles)
 * - Extruder
 * - Attractor
 * - Lattice
 * 
 * @author marius
 *
 */
public class UTransformAttractor extends UFilter {
	
	public UTransformAttractor addAttractor(UVec3 pos,float radius,float force) {
		if(force<0) force=-force;
		addParam(FORCE, force).addParam(RADIUS, radius).addParam(VERTEX, pos);
		return this;
	}

	public UTransformAttractor addRepulsor(UVec3 pos,float radius,float force) {
		if(force>0) force=-force;
		addParam(FORCE, force).addParam(RADIUS, radius).addParam(VERTEX, pos);
		return this;
	}

	public void transform(UVec3 v[], int n) {		
			
		int paramTypeIndex=0,paramIndex=0;
		float f,rad;
		UVec3 vv;
		
		while(paramTypeIndex<paramN) {			
			if(paramType[paramTypeIndex]==FORCE) {
				f=param[paramIndex++];
				rad=param[paramIndex++];
				vv=new UVec3(
						param[paramIndex++],param[paramIndex++],param[paramIndex++]);
				paramTypeIndex+=3; // Pass by FORCE, RADIUS and VERTEX params
				
				handleAttractor(vv, f, rad, v, n);
			}
		}
	}


	private void handleAttractor(UVec3 pos,float force,float rad,UVec3 v[], int n) {
		UVec3 vv,vforce=new UVec3();
		
//		if(force>0) force=1+force;
//		else force=-1+force;
		
		for(int j=0; j<n; j++) {
			vv=v[j];
			float d=pos.distanceTo(vv);
			if(d<rad) {
				d=1-d/rad;
				vforce.set(pos).sub(vv).mult(d*d*force);
				vv.add(vforce);
			}
		}
	}

}
