package unlekker.modelbuilder;

import unlekker.util.*;

public class UBezierPatch {
	public UVertexList vl,result;
	public int numSeg,nu,nv,resu,resv;
	public UVec3 cp[][];
	public UGeometry geo;

	
	public UBezierPatch() {
	}
	
	public UBezierPatch(UVec3 _cp[][]) {
		set(_cp);
	}
	
	public void set(UVec3 _cp[][]) {
		cp=_cp;
		nu=cp.length-1;
		nv=cp[0].length-1; 
	}
	
	public void eval(int _resu,int _resv) {
		UVertexList vl;
	  double mui, muj, bi, bj;

		resv=_resv;
		resu=_resu;
		
	  if(geo==null) geo=new UGeometry();
	  else geo.reset();
	  
	  for(int i=0; i<resu; i++) {
	    mui = i / (double)(resu-1);
			vl=new UVertexList();
		  for(int j=0; j<resv; j++) {
	      muj = j / (double)(resv-1);	      
		  	UVec3 vv=new UVec3();
		  	
	      for (int ki=0;ki<=nu;ki++) {
	        bi = BezierBlend(ki, mui, nu);
	        for (int kj=0;kj<=nv;kj++) {
	          bj = BezierBlend(kj, muj, nv);
	          vv.add(
	          		(float)(cp[ki][kj].x * bi * bj),
	          		(float)(cp[ki][kj].y * bi * bj),
	          		(float)(cp[ki][kj].z * bi * bj));
	        }
	      }
	      vl.add(vv);
		  }
	  	geo.addVertexList(vl);
	  	if(i>0) geo.quadStrip(geo.vl[i-1],vl);
	  }

	}
	
	private double BezierBlend(int k, double mu, int n) {
	  int nn, kn, nkn;
	  double blend=1;

	  nn = n;
	  kn = k;
	  nkn = n - k;

	  while (nn >= 1) {
	    blend *= nn;
	    nn--;
	    if (kn > 1) {
	      blend /= (double)kn;
	      kn--;
	    }
	    if (nkn > 1) {
	      blend /= (double)nkn;
	      nkn--;
	    }
	  }
	  if (k > 0)
	    blend *= Math.pow(mu, (double)k);
	  if (n-k > 0)
	    blend *= Math.pow(1-mu, (double)(n-k));

	  return(blend);
	}

}
