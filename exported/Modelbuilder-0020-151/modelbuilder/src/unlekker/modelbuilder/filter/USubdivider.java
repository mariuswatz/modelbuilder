package unlekker.modelbuilder.filter;

import java.util.ArrayList;

import processing.core.PApplet;

import unlekker.modelbuilder.*;
import unlekker.util.UProgressInfo;
import unlekker.util.UUtil;

public class USubdivider extends UFilter {
	public int type,multiN;
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
	
	public USubdivider setMultiRes(int res) {
		multiN=res;
		
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
		else if(type==SUBDIVIDE_MIDPOINTS){
			UVec3 mid[]=f.calcMidEdges();
			nf=new UFace[4];
			nf[0]=new UFace(f.v[0], mid[0], mid[2]);
			nf[1]=new UFace(mid[0], f.v[1],mid[1]);
			nf[2]=new UFace(mid[0], mid[1],mid[2]);
			nf[3]=new UFace(mid[2], mid[1],f.v[2]);
		}
		else if(type==SUBDIVIDE_MULTI){
			if(multiN<2) multiN=2; 
			int realN=multiN+1;
			
			UVertexList vl[]=new UVertexList[realN];
			UVertexList edge1=UVec3.interpolateMultiple(f.v[0], f.v[1], realN);
			UVertexList edge2=UVec3.interpolateMultiple(f.v[0], f.v[2], realN);
			
			vl[0]=new UVertexList().add(edge1.first());
			
			for(int i=1; i<realN; i++) vl[i]=UVec3.interpolateMultiple(edge1.v[i], edge2.v[i], i+1);

			UGeometry gg=new UGeometry();
			gg.noDuplicates();
			
			UVec3 vv[]=new UVec3[3];
			
			gg.add(new UFace(vl[1].v[0],vl[1].v[1],vl[0].v[0]));
			for(int i=2; i<realN; i++) {
				UVertexList v1=vl[i],v2=vl[i-1];
				for(int j=0; j<v1.n-1; j++) {
					vv[0]=v1.v[j];
					vv[1]=v1.v[j+1];
					vv[2]=v2.v[j];
					gg.addFace(vv);
						
					if(j>0) {
						vv[0]=v1.v[j];
						vv[1]=v2.v[j];
						vv[2]=v2.v[j-1];
						gg.addFace(vv);
					}
				}
			}
			
//			UUtil.log(gg.vert.toDataString());

//			gg.removeDuplicateVertices();
			
			nf=(UFace[])UUtil.expandArray(gg.face,gg.faceNum);
			UUtil.log(gg.faceNum+" "+gg.face.length);
			UUtil.log(UUtil.toString(gg.face));
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
