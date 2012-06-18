package unlekker.modelbuilder.filter;

import java.util.ArrayList;

import unlekker.modelbuilder.*;
import unlekker.util.UProgressInfo;
import unlekker.util.UUtil;

public class USubdivider extends UFilter {
	public int type;
	public float maxSurfaceArea=-1;
	public float maxEdgeLength=-1;
	
	public USubdivider() {
		super();		
		type=SUBDIVIDE_MIDPOINTS;
	}

	public USubdivider setType(int _type) {
		type=_type;
		return this;
	}
	
	public USubdivider setMaxArea(float area) {
		maxSurfaceArea=area;
		return this;
	}

	public USubdivider setMaxEdgeLength(float length) {
		maxEdgeLength=length;
		return this;
	}

	public UGeometry subdivideConditional(UGeometry input) {
		UGeometry g=new UGeometry();
		if(input.doNoDuplicates) g.noDuplicates();
		
		ArrayList<UFace> fl=new ArrayList<UFace>();
		
		for(int i=0; i<input.faceNum; i++) {
			UFace f=input.face[i];
			boolean ok=true;
			if(maxSurfaceArea>0 && f.surfaceArea()>maxSurfaceArea) 
				ok=false;
			if(ok && maxEdgeLength>0) {
				if(f.v[0].distanceTo(f.v[1])>maxEdgeLength) ok=false;
				else if(f.v[1].distanceTo(f.v[2])>maxEdgeLength) ok=false;
				else if(f.v[2].distanceTo(f.v[0])>maxEdgeLength) ok=false;
			}
			
			if(!ok) {
				UFace ff[]=subdivideFace(input.face[i]);
				for(int j=0; j<ff.length; j++) fl.add(ff[j]);
			}
			else fl.add(input.face[i]);
		}
		
		for(UFace f:fl) g.add(f);
		UUtil.log("USubdividor - "+input.faceNum+" faces => "+g.faceNum);
		return g;
	}
	
	public UGeometry build(UGeometry input) {
		UProgressInfo prog=new UProgressInfo();
		
		UGeometry g=new UGeometry();
		if(input.doNoDuplicates) g.noDuplicates();

		ArrayList<UFace> fl=new ArrayList<UFace>();
	
		
		for(int i=0; i<input.faceNum; i++) {
			prog.update(null, 100f*(float)i/(float)input.faceNum);
			UFace ff[]=subdivideFace(input.face[i]);
			for(int j=0; j<ff.length; j++) fl.add(ff[j]);
			UUtil.log(""+prog.elapsed);
		}
		
		prog.start();
		float cnt=0,maxCnt=fl.size();
		for(UFace f:fl) {
			prog.update(null, 100f*cnt/maxCnt);
			if(prog.elapsed>2000 && (int)cnt%10==0) UUtil.log(prog.lastUpdate+" "+g.faceNum+" faces built.");
			g.add(f);
			cnt++;
		}
		
		UUtil.log("USubdividor - "+input.faceNum+" faces => "+g.faceNum);
		return g;
	}

	public UGeometry build(UFace input) {
		UGeometry g=new UGeometry();

		g.add(subdivideFace(input));
		addResult(g);
		return g;
	}

	public UFace [] subdivideFace(UFace f) {
		UFace [] nf=null;
		
		if(type==SUBDIVIDE_CENTROID) {
			nf=new UFace[3];
			UVec3 c=f.calcCentroid();
			nf[0]=new UFace(f.v[0], c, f.v[1]);
			nf[1]=new UFace(f.v[1], f.v[2],c);
			nf[2]=new UFace(f.v[2], f.v[0],c);
		}
		else {
			UVec3 mid[]=f.calcMidEdges();
			nf=new UFace[4];
			nf[0]=new UFace(f.v[0], mid[0], mid[2]);
			nf[1]=new UFace(mid[0], f.v[1],mid[1]);
			nf[2]=new UFace(mid[0], mid[1],mid[2]);
			nf[3]=new UFace(mid[2], mid[1],f.v[2]);
		}
		
		return nf;
	}

	public UFace [] subdivideFaceConditional(UFace f,float minSurfaceArea) {
		UFace [] nf=null;
		
		if(f.surfaceArea()<minSurfaceArea) return nf;
		
		if(type==SUBDIVIDE_CENTROID) {
			nf=new UFace[3];
			UVec3 c=f.calcCentroid();
			nf[0]=new UFace(f.v[0], c, f.v[1]);
			nf[1]=new UFace(f.v[1], f.v[2],c);
			nf[2]=new UFace(f.v[2], f.v[0],c);
		}
		else {
			UVec3 mid[]=f.calcMidEdges();
			nf=new UFace[4];
			nf[0]=new UFace(f.v[0], mid[0], mid[2]);
			nf[1]=new UFace(mid[0], f.v[1],mid[1]);
			nf[2]=new UFace(mid[0], mid[1],mid[2]);
			nf[3]=new UFace(mid[2], mid[1],f.v[2]);
		}
		
		ArrayList<UFace> fl=new ArrayList<UFace>();

		for(int i=0; i<nf.length; i++) {
			if(nf[i].surfaceArea()>minSurfaceArea)  {
				UFace ff[]=subdivideFaceConditional(nf[i], minSurfaceArea);
				for(int j=0; j<ff.length; j++) fl.add(ff[i]);
			}
			else fl.add(nf[i]);
		}
		
		nf=new UFace[fl.size()];
		for(int i=0; i<nf.length; i++) nf[i]=fl.get(i);
		
		return nf;
	}
}
