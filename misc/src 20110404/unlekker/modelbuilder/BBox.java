package unlekker.modelbuilder;

public class BBox {
	Vec3 min,max,dim,centroid;
	float maxDimension;
	
	public BBox() {
		min=new Vec3();
		max=new Vec3();
		dim=new Vec3();
		centroid=new Vec3();				
	}
	
	public void calc(Geometry g) {
		reset();
		for(int i=0; i<g.faceNum; i++) {
			addPoint(g.face[i].v[0]);
			addPoint(g.face[i].v[1]);
			addPoint(g.face[i].v[2]);
		}
		finishCalc();
	}

	public void calc(VertexList vl) {
		reset();
		for(int i=0; i<vl.n; i++) addPoint(vl.v[i]);
		finishCalc();
	}

	private void finishCalc() {
		centroid.set(min);
		centroid.add(max);
		centroid.mult(0.5f);
		
		dim.set(max);
		dim.sub(min);
		maxDimension=dim.x;
  	if(dim.y>maxDimension) maxDimension=dim.y;
  	if(dim.z>maxDimension) maxDimension=dim.z;
	}
	
	private void addPoint(Vec3 v) {
		if(v.x<min.x) min.x=v.x;
		if(v.y<min.y) min.y=v.y;
		if(v.z<min.z) min.z=v.z;
		if(v.x>max.x) max.x=v.x;
		if(v.y>max.y) max.y=v.y;
		if(v.z>max.z) max.z=v.z;
	}
	
	private void reset() {
		min.set(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
		max.set(Float.MIN_VALUE,Float.MIN_VALUE,Float.MIN_VALUE);
	}
}
