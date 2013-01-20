package unlekker.modelbuilder.filter;

import processing.core.PApplet;
import unlekker.modelbuilder.*;
import unlekker.util.*;

public class UFilter implements UConstants {
	public UGeometry result,geom[];
	public int geomN;
	static public int VERTEX=0,RADIUS=1,WIDTH=2,HEIGHT=3,
			FORCE=4,BEND=5,TWIST=6,TAPER=7,STACK=8;
	static public int SUBDIVIDE_CENTROID=0,SUBDIVIDE_MIDPOINTS=1,SUBDIVIDE_MULTI=2;

	protected boolean changed;
	protected int paramType[];
	protected int paramN;
	protected int paramValN;
	protected float param[];
	
	public UFilter() {
	}
	
	public void build(UVertexList input) {		
	}

	public UGeometry build(UGeometry input) {
		for(int i=0; i<input.quadNum; i++) build(input.quad[i]);
		UFace f[]=input.getNonQuads();
		if(f!=null)
			for(int i=0; i<f.length; i++) build(f[i]);
		
		return getResult();
	}
	
	public UGeometry build(UQuad input) {
		build(input.f1);
		build(input.f2);		
		UUtil.log("Default build(UQuad) - no transformation performed");
		
		return null;
	}

	/**
	 * Generates geometry based on a <code>UFace</code> instance. This method should be overloaded by every <code>UFilterGenerator</code>
	 * descendant.
	 * 
	 * @param input
	 * @return A reference to the <code>UGeometry</code> object that is generated, which is also added to the <code>geom</code> array.
	 */
	public UGeometry build(UFace input) {
		UUtil.log("Default build() - no transformation performed");
		return null;
	}

	public void transform(UGeometry g) {
		transform(g.vert);
		
	}

	public void transform(UVertexList vl) {
		transform(vl.v,vl.n);
	}

	protected void transform(UVec3 v) {
		
	}

	public void transform(UVec3 v[],int n) {
		for(int i=0; i<n; i++) transform(v[i]);
	}

	public void transform(UVec3 v[]) {
		transform(v,v.length);
	}

	public UFilter addParam(int type,float val) {
		if(paramType==null) {
			paramType=new int[100];
			param=new float[100];
		}
		else if(paramType.length==paramN) 
			paramType=(int [])UUtil.expandArray(paramType);
		
		if(param.length==paramValN)
			param=(float [])UUtil.expandArray(param);

		
		paramType[paramN++]=type;
		param[paramValN++]=val;

		return this;
	}

	public UFilter addParam(int type,float val[]) {
		if(paramType==null) {
			paramType=new int[100];
			param=new float[100];
		}
		else if(paramType.length==paramN) 
			paramType=(int [])UUtil.expandArray(paramType);
		
		if(param.length<paramValN+val.length)
			param=(float [])UUtil.expandArray(param);

		
		paramType[paramN++]=type;
		for(int i=0; i<val.length; i++) param[paramValN++]=val[i];
		
		return this;
	}

	public UFilter addParam(int type,UVec3 v) {
		return addParam(type,new float[] {v.x,v.y,v.z});	
	}

	public void addResult(UGeometry g) {
		if(geom==null) geom=new UGeometry[100];
		if(geomN==geom.length) 
			geom=(UGeometry [])UUtil.expandArray(geom);
		geom[geomN++]=g;
		
		changed=true;
	}

	public UGeometry getResult() {
		if(result!=null && !changed) return result;
		
		result=new UGeometry();
		for(int i=0; i<geomN; i++) result.add(geom[i]);
		changed=false;
		
		return result;
	}

	
}
