package unlekker.modelbuilder;

import processing.core.PApplet;

public class UFace {
	public UVec3 v[],n;
//	public int c[];
	
	public UFace(UVec3 fv1, UVec3 fv2, UVec3 fv3) {
		v=new UVec3[3];
		v[0]=new UVec3(fv1);
		v[1]=new UVec3(fv2);
		v[2]=new UVec3(fv3);
		calcNormal();
	}

	public UFace(UFace face) {
		v=new UVec3[3];
		v[0]=new UVec3(face.v[0]);
		v[1]=new UVec3(face.v[1]);
		v[2]=new UVec3(face.v[2]);
		n=new UVec3(face.n);;
	}

	public void draw(PApplet p) {
		p.beginShape(p.TRIANGLES);
		p.vertex(v[0].x, v[0].y, v[0].z);
		p.vertex(v[1].x, v[1].y, v[1].z);
		p.vertex(v[2].x, v[2].y, v[2].z);
		p.endShape();
		
	}
	
	public void rotateX(float a) {
		for(int i=0; i<3; i++) v[i].rotateX(a);
	}

	public void rotateY(float a) {
		for(int i=0; i<3; i++) v[i].rotateY(a);
	}

	public void rotateZ(float a) {
		for(int i=0; i<3; i++) v[i].rotateZ(a);
	}

	public void translate(float x,float y,float z) {
		for(int i=0; i<3; i++) v[i].add(x,y,z);
	}

	public void translate(UVec3 vv) {
		for(int i=0; i<3; i++) v[i].add(vv.x,vv.y,vv.z);
	}

	public void scale(float m) {
		for(int i=0; i<3; i++) v[i].mult(m);
	}

	public void scale(float mx,float my,float mz) {
		for(int i=0; i<3; i++) v[i].mult(mx,my,mz);
	}

	public void calcNormal() {
		n=UVec3.crossProduct(
				v[1].x-v[0].x,v[1].y-v[0].y,v[1].z-v[0].z,
  			v[2].x-v[0].x,v[2].y-v[0].y,v[2].z-v[0].z);
	}

	
}
