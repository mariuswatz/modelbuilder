package unlekker.modelbuilder.filter;

import unlekker.modelbuilder.*;
import unlekker.util.UUtil;

/**
 * Various geometry vertex deformations, such as bend, twist, taper and stack. All transformations are applied according to 
 * relative Y values so that vertices with the lowest Y value are considered to be at <code>t=0</code>, with 
 * <code>t=1</code> occurring at the maximum Y value. Consequently vertices with low Y values will be transformed the
 * least, with the highest points being transformed the most.
 * 
 * Tyipcal use:
 * 
 * <code>UGeometry geo=UPrimitive.
 * new UTransformDeform().bend(radians(60)).transform(geo);
 * </code>
 * 
 * @author Marius Watz
 *
 */
public class UTransformDeform extends UFilter {
	private UBBox bb;
	
	public UTransformDeform() {
		super();		
	}
	
	public UTransformDeform bend(float a) {
		addParam(BEND, a);
		return this;
	}

	public UTransformDeform twist(float a) {
		addParam(TWIST, a);
		return this;
	}

	public UTransformDeform taper(float a) {
		addParam(TAPER, a);
		return this;
	}

	public UTransformDeform stack(float x,float y,float z) {
		addParam(STACK, new UVec3(x,y,z));
		return this;
	}

	public UTransformDeform stack(UVec3 dir) {
		addParam(STACK, dir);
		return this;
	}
	
	public void transform(UVec3 v[], int n) {
		bb=new UBBox().add(v,n).finishCalc();
		
		int paramIndex=0;
		for(int i=0; i<paramN; i++) {
			if(paramType[i]==BEND) bend(v,n,param[paramIndex++]);
			if(paramType[i]==TWIST) twist(v,n,param[paramIndex++]);
			if(paramType[i]==TAPER) taper(v,n,param[paramIndex++]);
			if(paramType[i]==STACK) stack(v,n,param[paramIndex++],param[paramIndex++],param[paramIndex++]);
		}
	}

	private void stack(UVec3[] v, int n, float x,float y,float z) {
		float D,yd=bb.max.y-bb.min.y;
		
		for(int i=0; i<n; i++) {
			D=(v[i].y-bb.min.y)/yd;
//			UUtil.log(i+" "+UUtil.nf(D)+" "+v[i].toString());
			v[i].add(D*x,D*y,D*z);
//			UUtil.log(i+" "+v[i].toString());
		}
	}

	private void bend(UVec3[] v, int n, float a) {
		float D,yd=bb.max.y-bb.min.y;
		
		for(int i=0; i<n; i++) {
			D=(v[i].y-bb.min.y)/yd;
//			UUtil.log(i+" "+UUtil.nf(D)+" "+v[i].toString());
			v[i].rotateZ(a*D);
		}
	}

	private void twist(UVec3[] v, int n, float a) {
		float D,yd=bb.max.y-bb.min.y;
		
		for(int i=0; i<n; i++) {
			D=(v[i].y-bb.min.y)/yd;
			v[i].rotateY(a*D);
		}
	}

	private void taper(UVec3[] v, int n, float a) {
		float D,yd=bb.max.y-bb.min.y;
		
		for(int i=0; i<n; i++) {
			D=(v[i].y-bb.min.y)/yd;
			v[i].mult(1+a*D,1,1+a*D);
		}
	}

}
