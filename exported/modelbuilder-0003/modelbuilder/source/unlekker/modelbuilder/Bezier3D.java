package unlekker.modelbuilder;

import unlekker.util.*;

public class Bezier3D {
	UVertexList vl,result;
	
	public Bezier3D(UVertexList _vl) {
		vl=_vl;
	}
	
	public void eval(int steps) {
		float ct,ctsq,tsq,t,t0,t1,t2,t3;
		UVec3 res=new UVec3();
		
		result=new UVertexList();

		for(int i=0; i<steps; i++) {
			t=(float)i/(float)(steps-1);
			ct=1f-t;
			ctsq=ct*ct;
			tsq=t*t;

			t0=ctsq*ct;
			t1=ctsq*t;
			t2=ct*tsq;
			t3=t*tsq;

			res.set(
					 vl.v[0].x*t0+3*vl.v[1].x*t1+3*vl.v[2].x*t2+vl.v[3].x*t3,
					 vl.v[0].y*t0+3*vl.v[1].y*t1+3*vl.v[2].y*t2+vl.v[3].y*t3,
					 vl.v[0].z*t0+3*vl.v[1].z*t1+3*vl.v[2].z*t2+vl.v[3].z*t3);
			result.add(res);
		}
	}

}
