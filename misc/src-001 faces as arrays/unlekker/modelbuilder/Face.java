package unlekker.modelbuilder;

import processing.core.PApplet;

public class Face {
	public Vec3 v[],n;
	public int c[];
	
	public Face(Vec3 fv1, Vec3 fv2, Vec3 fv3) {
		v=new Vec3[3];
		v[0]=fv1;
		v[1]=fv2;
		v[2]=fv3;
		n=Vec3.crossProduct(fv2.x-fv1.x,fv2.y-fv1.y,fv2.z-fv1.z,
  			fv3.x-fv1.x,fv3.y-fv1.y,fv3.z-fv1.z);
	}

	public void draw(PApplet p) {
		p.beginShape(p.TRIANGLES);
		p.vertex(v[0].x, v[0].y, v[0].z);
		p.vertex(v[1].x, v[1].y, v[1].z);
		p.vertex(v[2].x, v[2].y, v[2].z);
		p.endShape();
		
	}
	
	
}
