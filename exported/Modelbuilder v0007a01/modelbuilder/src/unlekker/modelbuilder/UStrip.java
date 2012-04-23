package unlekker.modelbuilder;

import processing.core.PApplet;
import unlekker.util.UConstants;
import unlekker.util.UUtil;

public class UStrip implements UConstants  {
	public UGeometry parent;
	public int type,quadNum,triNum;
	public int quadID[],triID[];
	private UVertexList vert;
	
	public UStrip(int _type) {
		type=_type;
	}
	
	public UStrip(int _type,UGeometry _p, UVec3 vertIn[]) {
		type=_type;
		parent=_p;
		for(int i=0; i<vertIn.length; i++) add(vertIn[i]);
	}

	public UStrip add(UVec3 v) {
		if(vert==null) vert=new UVertexList();
		vert.add(v);
		if(type==QUAD_STRIP && vert.n>2 && vert.n%2==0) {
			int id=vert.n-4;
			addQuad(vert.v,id);
		}
		if(type==TRIANGLE_FAN && vert.n>2) {
			int id=vert.n-2;
			addTri(vert.v[0],vert.v[id+1],vert.v[id]);
		}
		
		return this;
	}
	
	public void draw(PApplet p) {
		UQuad q;
		
		if(type==QUAD_STRIP) {
			p.beginShape(TRIANGLES);
			for(int i=0; i<quadNum; i++) {
				q=parent.quad[quadID[i]];
				p.vertex(q.v[0].x,q.v[0].y,q.v[0].z);
				p.vertex(q.v[2].x,q.v[2].y,q.v[2].z);
				p.vertex(q.v[1].x,q.v[1].y,q.v[1].z);

				p.vertex(q.v[1].x,q.v[1].y,q.v[1].z);
				p.vertex(q.v[2].x,q.v[2].y,q.v[2].z);
				p.vertex(q.v[3].x,q.v[3].y,q.v[3].z);
			}
			p.endShape();
		}
	}
	
	public UStrip addQuad(UVec3 v[],int start) {
		if(quadID==null) quadID=new int[100];
		if(quadID.length==quadNum) quadID=UUtil.expandArray(quadID);

		quadID[quadNum++]=parent.addFace(
				new UVec3[] {
						v[start],v[start+1],
						v[start+2],v[start+3]
				});
		
		return this;
	}
	
	public UStrip addTri(UVec3 v1,UVec3 v2,UVec3 v3) {
		if(triID==null) triID=new int[100];
		if(triID.length==triNum) triID=UUtil.expandArray(triID);
		
		triID[triNum++]=parent.addFace(new UVec3[]{v1,v2,v3});
		
		return this;
	}

}
