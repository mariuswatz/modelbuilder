package unlekker.modelbuilder.filter;

import unlekker.modelbuilder.*;

public class UPolyOutliner extends UFilter {
	public float rad;
	private UBBox bb;
	UVertexList check[];
	
	public UPolyOutliner() {
		super();		
		check=UVertexList.getVertexLists(2);
	}
	
	public UPolyOutliner radius(float radius) {
		addParam(RADIUS, radius);
		rad=radius;
		return this;
	}

	public UGeometry build(UFace input) {
		UGeometry g=new UGeometry();
		
		add3Dline(g, input.v[0], input.v[1]);
		add3Dline(g, input.v[1], input.v[2]);
		add3Dline(g, input.v[2], input.v[0]);
		add3Dline(g, input.v[2], UVec3.interpolate(input.v[0], input.v[1], 0.5f));
		add3Dline(g, input.v[0], UVec3.interpolate(input.v[2], input.v[1], 0.5f));
		
		addResult(g);
		return g;
	}

	private void add3Dline(UGeometry tmp, UVec3 pt1, UVec3 pt2) {
		if(checkDuplicate(pt1, pt2)) return;
		
		tmp.add(UPrimitive.box2Points(pt1, pt2, rad));
		check[0].add(pt1);
		check[1].add(pt2);		
	}

	private boolean checkDuplicate(UVec3 v1,UVec3 v2) {
		for(int i=0; i<check[0].n; i++) {
			if((check[0].v[i].distanceTo(v1)<0.1f && 
					check[1].v[i].distanceTo(v2)<0.1f) ||
					(check[1].v[i].distanceTo(v1)<0.1f && 
					check[0].v[i].distanceTo(v2)<0.1f)) return true;
		}
		
		return false;
	}
}
