package unlekker.modelbuilder.filter;
import processing.core.PApplet;
import unlekker.modelbuilder.*;

public class UTVertexListSweep extends UFilter {
	private UBBox bb;
	public int plane;
	public UVertexList profile;

	public UTVertexListSweep setPlane(int plane) {
		this.plane=plane;
		return this;
	}
	
	public UTVertexListSweep setProfile(UVertexList profile) {
		this.profile=new UVertexList(profile);
		return this;
	}
	
	public UTVertexListSweep sweep(UVertexList input) {
		UGeometry gg=new UGeometry();
		float a= 0,aOld=0,b=0,bOld=0,m=1;
		UVertexList tvl1= null,tvl2= null,tvl3= null;
		UVec3 tv=new UVec3(),delta=new UVec3();

		if (plane==XZPLANE) {
			a=0;
			for (int j=0; j<input.n; j++) {
				aOld=a;
				bOld=b;

				if (j<input.n-1) {
					tv=UVec3.delta(input.v[j+1], input.v[j]).mult(1, 0, 1).norm(100);

					tv=new UVec3(input.v[j]).norm();
					float xd=tv.x, zd=tv.z;
					b=PApplet.atan2(zd, xd);
					if (b<0) b=TWO_PI+b;

					b=PApplet.map(j, 0, input.n, 0, TWO_PI);

					delta=UVec3.delta(input.v[j+1], input.v[j]).rotateY(-b).norm();
					a=PApplet.atan2(delta.y, delta.x);
				}
				// log(j+" "+nf(p.degrees(b))+" "+tv.toString());

				if (j==0) aOld=a;
				tv=new UVec3(input.v[j]);

				tvl3=new UVertexList(profile).rotateZ((a+aOld)*0.5f-HALF_PI).rotateY(b)
						.translate(tv);// b*0.3f+bOld*0.7f

				gg.addVertexList(tvl3);
			}

			gg.quadStrip(gg.vl);
			gg.triangleFan(gg.vl[0], true, true);
			gg.triangleFan(gg.vl[gg.vln-1], true, false);
		}
		
		gg.vln=0;
		addResult(gg);
		return this;
	}
}
